package collab.fm.server.bean.protocol;

import org.apache.log4j.Logger;

import collab.fm.server.util.Resources;

public class LoginRequest extends Request {

	static Logger logger = Logger.getLogger(LoginRequest.class);
	
	private String user;
	private String pwd;
	
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
	
}
