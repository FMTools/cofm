package collab.fm.server.action;

import java.util.*;
import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.ModelDescription;
import collab.fm.server.bean.entity.ModelName;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.entity.Votable;
import collab.fm.server.bean.protocol.ListModelRequest;
import collab.fm.server.bean.protocol.ListModelResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.protocol.ListModelResponse.Model2;
import collab.fm.server.bean.protocol.UpdateResponse.Des2;
import collab.fm.server.bean.protocol.UpdateResponse.Name2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ListModelAction extends Action {
	
	private static Logger logger = Logger.getLogger(ListModelAction.class);

	public ListModelAction() {
		super(new String[] { Resources.REQ_LIST_MODEL} );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		try {
			ListModelRequest request = (ListModelRequest)req;
			
			List<Model> all = null;
			if (request.getSearchWords() == null) {
				all = DaoUtil.getModelDao().getAll();
			} else {
				all = DaoUtil.getModelDao().getBySimilarName(request.getSearchWords());
			}
			
			ListModelResponse lmr = new ListModelResponse();
			lmr.setModels(new ArrayList<Model2>());
			
			if (all != null) {
				for (Model m: all) {
					Model2 m2 = new Model2();
					m2.setId(m.getId());
					
					Set<Long> m2Users = new HashSet<Long>();
					for (User u: m.getUsers()) {
						m2Users.add(u.getId());
					}
					m2.setUser(m2Users);
					
					List<Name2> m2Name = new ArrayList<Name2>();
					for (Votable v: m.getNames()) {
						ModelName n = (ModelName)v;
						Name2 n2 = new Name2();
						n2.setVal(n.getName());
						n2.setuNo(n.getVote().getOpponents());
						n2.setuYes(n.getVote().getSupporters());
						m2Name.add(n2);
					}
					m2.setName(m2Name);
					
					List<Des2> m2Des = new ArrayList<Des2>();
					for (Votable v: m.getDescriptions()) {
						ModelDescription d = (ModelDescription)v;
						Des2 d2 = new Des2();
						d2.setVal(d.getValue());
						d2.setuNo(d.getVote().getOpponents());
						d2.setuYes(d.getVote().getSupporters());
						m2Des.add(d2);
					}
					m2.setDes(m2Des);
					
					lmr.getModels().add(m2);
				}
			}
			lmr.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(lmr);
			
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		} catch (StaleDataException e) {
			logger.info("Stale data found.");
			throw e;
		}
	}

}
