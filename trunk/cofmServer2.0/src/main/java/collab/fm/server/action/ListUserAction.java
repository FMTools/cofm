package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.ListUserResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.transfer.User2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ListUserAction extends Action {

	static Logger logger = Logger.getLogger(ListUserAction.class);
	
	public ListUserAction() {
		super(new String[] { Resources.REQ_LISTUSER });
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		try {
			List<User> all = DaoUtil.getUserDao().getAll();
			List<User2> result = new ArrayList<User2>();
			if (all != null) {
				for (User u: all) {
					result.add(u.transfer());
				}
			}
			ListUserResponse lur = new ListUserResponse();
			lur.setName(Resources.RSP_SUCCESS);
			lur.setUsers(result);
			
			rg.setBack(lur);
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		} 
	}

}
