package collab.fm.server.action;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Comment;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.protocol.AddCommentRequest;
import collab.fm.server.bean.protocol.AddCommentResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class AddCommentAction extends Action {
	
	private static Logger logger = Logger.getLogger(AddCommentAction.class);
	
	public AddCommentAction() {
		super(new String[] { Resources.REQ_COMMENT });
	}

	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		AddCommentRequest acr = (AddCommentRequest) req;
		try {
			Feature f = DaoUtil.getFeatureDao().getById(acr.getFeatureId(), false);
			if (f == null) {
				return false;
			}
			Comment c = new Comment(acr.getRequesterId());
			c.setContent(acr.getContent());
			f.addComment(c);
			DaoUtil.getFeatureDao().save(f);
			
			AddCommentResponse rsp = new AddCommentResponse();
			rsp.setContent(acr.getContent());
			rsp.setFeatureId(acr.getFeatureId());
			rsp.setDateTime(c.strCreated());
			rsp.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(rsp);
			
			AddCommentResponse rsp2 = new AddCommentResponse();
			rsp2.setContent(acr.getContent());
			rsp2.setFeatureId(acr.getFeatureId());
			rsp2.setDateTime(c.strCreated());
			rsp2.setName(Resources.RSP_FORWARD);
			
			rg.setBroadcast(rsp2);

		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		}
		
		return true;
	}

}
