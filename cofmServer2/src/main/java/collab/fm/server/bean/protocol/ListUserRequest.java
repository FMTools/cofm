package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.User;
import collab.fm.server.bean.transfer.User2;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class ListUserRequest extends Request {
	@Override
	protected Processor makeDefaultProcessor() {
		return new ListUserProcessor();
	}
	
	private static class ListUserProcessor implements Processor {

		public boolean checkRequest(Request req) {
			return true;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid list_user operation.");
			}
			List<User> all = DaoUtil.getUserDao().getAll();
			List<User2> result = new ArrayList<User2>();
			if (all != null) {
				for (User u: all) {
					User2 u2 = new User2();
					u.transfer(u2);
					result.add(u2);
				}
			}
			ListUserResponse lur = new ListUserResponse(req);
			lur.setName(Resources.RSP_SUCCESS);
			lur.setUsers(result);
			rg.setBack(lur);
			return true;
		}
		
	}
	
	public static class ListUserResponse extends Response {
		
		private List<User2> users;
		
		public ListUserResponse(Request r) {
			super(r);
		}
		
		public List<User2> getUsers() {
			return users;
		}

		public void setUsers(List<User2> users) {
			this.users = users;
		}
	}
}
