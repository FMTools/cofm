package collab.fm.server.bean.protocol;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.User;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class RegisterRequest extends Request {
	static Logger logger = Logger.getLogger(RegisterRequest.class);
	
	private String user;
	private String pwd;
	
	@Override 
	protected Processor makeDefaultProcessor() {
		return new RegisterProcessor();
	}
	
	public boolean valid() {
		if (super.valid()) {
			return Resources.REQ_REGISTER.equals(name) && user != null && pwd != null;
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
	
	private static class RegisterProcessor implements Processor {

		public boolean checkRequest(Request req) {
			return req.valid();
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!req.valid()) {
				throw new InvalidOperationException("Invalid register operation.");
			}
			
			RegisterRequest r = (RegisterRequest) req;
			Response rsp = new Response(req);

			User u = DaoUtil.getUserDao().getByName(r.getUser());

			if (u == null) {
				u = new User();
				u.setName(r.getUser());
				u.setPassword(r.getPwd());
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
				
			} else {
				rsp.setMessage(Resources.MSG_ERROR_USER_EXISTED);
				rsp.setName(Resources.RSP_ERROR);
			}

			rg.setBack(rsp);
			
			return true;
		}
		
	}
}
