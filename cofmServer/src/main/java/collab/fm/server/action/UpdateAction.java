package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.FeatureDescription;
import collab.fm.server.bean.entity.FeatureName;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.bean.entity.Votable;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.protocol.UpdateResponse;
import collab.fm.server.bean.protocol.UpdateResponse.BinaryRelation2;
import collab.fm.server.bean.protocol.UpdateResponse.Des2;
import collab.fm.server.bean.protocol.UpdateResponse.Feature2;
import collab.fm.server.bean.protocol.UpdateResponse.Name2;
import collab.fm.server.persistence.*;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.controller.*;


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
			for (Feature f: allFeatures) {
				Feature2 f2 = new Feature2();
				f2.setId(f.getId());
				f2.setuYes(f.getExistence().getSupporters());
				f2.setuNo(f.getExistence().getOpponents());
				f2.setuOptYes(f.getOptionality().getSupporters());
				f2.setuOptNo(f.getOptionality().getOpponents());
				
				List<Name2> name2 = new ArrayList<Name2>();
				for (Votable name: f.getNames()) {
					FeatureName n = (FeatureName)name;
					Name2 n2 = new Name2();
					n2.setVal(n.getName());
					n2.setuYes(n.getVote().getSupporters());
					n2.setuNo(n.getVote().getOpponents());
					name2.add(n2);
				}
				f2.setName(name2);
				
				List<Des2> des2 = new ArrayList<Des2>();
				for (Votable des: f.getDescriptions()) {
					FeatureDescription d = (FeatureDescription)des;
					Des2 d2 = new Des2();
					d2.setVal(d.getValue());
					d2.setuYes(d.getVote().getSupporters());
					d2.setuNo(d.getVote().getOpponents());
					des2.add(d2);
				}
				f2.setDes(des2);
				
				List<Long> rList = new ArrayList<Long>();
				for (Relationship r: f.getRelationships()) {
					rList.add(r.getId());
				}
				f2.setRels(rList);
				
				list1.add(f2);
			}
			
			// Return all binary relationships
			List<Relationship> allRelation = DaoUtil.getRelationshipDao().getAll(req.getModelId());
			
			List<BinaryRelation2> list2 = new ArrayList<BinaryRelation2>();
			for (Relationship r: allRelation) {
				if (isBinary(r.getType())) {
					BinaryRelationship br = (BinaryRelationship) r;
					BinaryRelation2 br2 = new BinaryRelation2();
					br2.setId(br.getId());
					br2.setLeft(br.getLeftFeatureId());
					br2.setRight(br.getRightFeatureId());
					br2.setType(br.getType());
					br2.setuNo(br.getExistence().getOpponents());
					br2.setuYes(br.getExistence().getSupporters());
					list2.add(br2);
				}
			}
			
			UpdateResponse response = new UpdateResponse();
			response.setFeatures(list1);
			response.setBinaries(list2);
			response.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(response);
			rg.setBroadcast(null);
			rg.setPeer(null);
			
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		} catch (StaleDataException e) {
			logger.info("Stale Data");
			throw e;
		}		
	}
	
	private boolean isBinary(String type) {
		return Resources.BIN_REL_EXCLUDES.equals(type) || 
			Resources.BIN_REL_REFINES.equals(type) ||
			Resources.BIN_REL_REQUIRES.equals(type);
	}

}
