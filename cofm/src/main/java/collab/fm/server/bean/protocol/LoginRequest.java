package collab.fm.server.bean.protocol;

import java.util.Date;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.User;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class LoginRequest extends Request {

	static Logger logger = Logger.getLogger(LoginRequest.class);
	
	private String user;
	private String pwd;
	private Boolean forceLogin;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new LoginProcessor();
	}
	
	public boolean valid() {
		if (super.valid()) {
			return Resources.REQ_LOGIN.equals(name) && user != null && pwd != null;
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
	
	public void setForceLogin(Boolean forceLogin) {
		this.forceLogin = forceLogin;
	}

	public Boolean getForceLogin() {
		return forceLogin;
	}

	private static class LoginProcessor implements Processor {

		public boolean checkRequest(Request req) {
			return req.valid();
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!req.valid()) {
				throw new InvalidOperationException("Invalid login operation.");
			}
			LoginRequest lr = (LoginRequest)req;
			Response rsp = new Response(req);
		
			User user = null;
			if (lr.getForceLogin()) {
				user = DaoUtil.getUserDao().getByName(lr.getUser());
			} else {
				user = DaoUtil.getUserDao().getByNameAndPwd(lr.getUser(), lr.getPwd());
			}
			
			if (user == null) {
				rsp.setMessage(Resources.MSG_ERROR_USER_LOGIN_FAILED);
				rsp.setName(Resources.RSP_ERROR);
			} else if (!user.isValidated()) {
				rsp.setMessage(Resources.MSG_ERROR_USER_NO_VERIFICATION);
				rsp.setName(Resources.RSP_ERROR);
			} else {
				user.setLastLoginTime(new Date());
				DaoUtil.getUserDao().save(user);
				
				req.setRequesterId(user.getId());
				rsp.setRequesterId(user.getId());
				rsp.setMessage(Resources.MSG_LOGIN);
				rsp.setName(Resources.RSP_SUCCESS);
				
//				logger.info(LogUtil.logOp(user.getId(), LogUtil.OP_LOGIN,
//						user.getName() + " " + req.getAddress()));				
			}
			
			rg.setBack(rsp);
			
			return true;
		}
		
	}
	
}
