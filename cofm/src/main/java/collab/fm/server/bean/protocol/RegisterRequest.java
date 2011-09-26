package collab.fm.server.bean.protocol;

import java.text.MessageFormat;
import java.util.Date;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.User;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.MailUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class RegisterRequest extends Request {
	static Logger logger = Logger.getLogger(RegisterRequest.class);
	
	private String user;
	private String pwd;
	private String mail;
	
	private static final String SERVER_MAIL = "yili.org";
	private static final String SERVER_URL = "http://159.226.47.103:8080/cofm/p?a=vf&n=$name&v=$validation";
	@Override 
	protected Processor makeDefaultProcessor() {
		return new RegisterProcessor();
	}
	
	public boolean valid() {
		if (super.valid()) {
			return Resources.REQ_REGISTER.equals(name) && user != null && pwd != null && mail != null;
		}
		return false;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	private static class RegisterProcessor implements Processor {

		public boolean checkRequest(Request req) {
			return req.valid();
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!req.valid()) {
				throw new InvalidOperationException("Invalid register operation.");
			}
			
			RegisterRequest r = (RegisterRequest) req;
			Response rsp = new Response(req);

			User u = DaoUtil.getUserDao().getByName(r.getUser());

			if (u == null) {
				String nameMd5 = DataItemUtil.generateMD5(r.getUser());
				String validationMd5 = DataItemUtil.generateMD5(new Date().toString());
				String validationUrl = SERVER_URL.replace("$name", nameMd5)
					.replace("$validation", validationMd5);
				// Send validation email
				boolean mailSent = false;
				try {
					MailUtil.sendFromGmail(SERVER_MAIL, 
							"FX5Vu6bp8Yk5", 
							new String[] {r.getMail()},
							Resources.MSG_REGISTER_MAIL_TITLE, 
							MessageFormat.format(Resources.MSG_REGISTER_MAIL_TEXT, 
									r.getUser(), validationUrl));
					mailSent = true;
				} catch (MessagingException e) {
					logger.warn("Sending register mail failed. (User=" + r.getUser() +
							", Mail=" + r.getMail());
					rsp.setMessage(Resources.MSG_ERROR_USER_EXISTED);
					rsp.setName(Resources.RSP_ERROR);
				}
				if (mailSent) {
					u = new User();
					u.setName(r.getUser());
					u.setNameInMD5(nameMd5);
					u.setEmail(r.getMail());
					u.setPasswordInMD5(r.getPwd());
					u.setValidated(false);
					u.setValidationStr(validationMd5);
					u = DaoUtil.getUserDao().save(u);
	
					rsp.setMessage(Resources.MSG_REGISTER);
					rsp.setName(Resources.RSP_SUCCESS);
					rsp.setRequesterId(u.getId());
					
					// logger.info(LogUtil.logOp(u.getId(), LogUtil.OP_REGISTER, u.getName()));
					
					// Broadcast new user info to each client.
					Response r2 = (Response) rsp.clone();
					r2.setMessage(r.getUser());  // use the "message" field to transfer the name of the new user.
					r2.setName(Resources.RSP_FORWARD);
					rg.setBroadcast(r2);
				}
			} else {
				rsp.setMessage(Resources.MSG_ERROR_USER_EXISTED);
				rsp.setName(Resources.RSP_ERROR);
			}

			rg.setBack(rsp);
			
			return true;
		}
		
	}
}
