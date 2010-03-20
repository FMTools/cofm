package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.protocol.UpdateResponse;
import collab.fm.server.bean.transfer.BinaryRelation2;
import collab.fm.server.bean.transfer.Feature2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;


public class UpdateAction extends Action {
	
	static Logger logger = Logger.getLogger(UpdateAction.class);
	
	public UpdateAction() {
		super(new String[]{Resources.REQ_UPDATE});
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	protected boolean doExecute(Request req, ResponseGroup rg) throws ActionException, StaleDataException {
		try {
			// Return all features
			List<Feature> allFeatures = DaoUtil.getFeatureDao().getAll(req.getModelId());
			
			List<Feature2> list1 = new ArrayList<Feature2>();
			if (allFeatures != null) {
				for (Feature f: allFeatures) {
					list1.add(f.transfer());
				}
			}
			
			// Return all binary relationships
			List<Relationship> allRelation = DaoUtil.getRelationshipDao().getAll(req.getModelId());
			
			List<BinaryRelation2> list2 = new ArrayList<BinaryRelation2>();
			if (allRelation != null) {
				for (Relationship r: allRelation) {
					if (isBinary(r.getType())) {
						list2.add(((BinaryRelationship)r).transfer());
					}	
				}
			}
			
			UpdateResponse response = new UpdateResponse();
			response.setFeatures(list1);
			response.setBinaries(list2);
			response.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(response);
			
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		} 	
	}
	
	private boolean isBinary(String type) {
		return Resources.BIN_REL_EXCLUDES.equals(type) || 
			Resources.BIN_REL_REFINES.equals(type) ||
			Resources.BIN_REL_REQUIRES.equals(type);
	}

}
