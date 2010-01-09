package collab.fm.server.action;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.CreateModelRequest;
import collab.fm.server.bean.protocol.CreateModelResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class CreateModelAction extends Action {

	static Logger logger = Logger.getLogger(CreateModelAction.class);
	public CreateModelAction() {
		super(new String[] { Resources.REQ_CREATE_MODEL });
	}

	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		CreateModelRequest cmr = (CreateModelRequest) req;
		CreateModelResponse rsp = new CreateModelResponse();
		try {
			if (DaoUtil.getModelDao().getByName(cmr.getModelName()) == null) {
				Model m = new Model();
				User me = DaoUtil.getUserDao().getById(cmr.getRequesterId(), false);
				if (me == null) {
					throw new ActionException("Invalid user id");
				}
				m.voteName(cmr.getModelName(), true, cmr.getRequesterId());
				m.voteDescription(cmr.getDescription(), true, cmr.getRequesterId());
				m = DaoUtil.getModelDao().save(m);
				me.addModel(m);
				DaoUtil.getUserDao().save(me);
				
				rsp.setModelId(m.getId());
				rsp.setName(Resources.RSP_SUCCESS);
			} else {
				rsp.setName(Resources.RSP_ERROR);
				rsp.setMessage("Model has already existed.");
			}
			rg.setBack(rsp);
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		}
	}

}
