package collab.fm.server.bean.protocol;

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
			
			User example = new User();
			example.setName(lr.getUser());
			example.setPassword(lr.getPwd());
		
			User user = DaoUtil.getUserDao().checkPasswordThenGet(example);
			
			if (user == null) {
				rsp.setMessage(Resources.MSG_ERROR_USER_LOGIN_FAILED);
				rsp.setName(Resources.RSP_ERROR);
			} else {
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
