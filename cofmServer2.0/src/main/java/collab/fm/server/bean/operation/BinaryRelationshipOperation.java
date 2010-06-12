package collab.fm.server.bean.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.LogUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * TODO: Origin vote and implicit votes should be transactional. <br/>
 *      This class defines the structure and meaning of operations which affect binary relationships
 * in feature models. There're 3 kinds of binary relationships: refinement(parent-child), requiring, 
 * and excluding. For each relationship, an "add" operation is the only valid operation. The applied
 * operation is returned after necessary persistence has finished. <br/>
 *      In addition, vote YES to these "addXXX" operations imply vote YES to features which have
 * involved in the corresponding relationship.  
 * @author Yi Li
 *
 */
public class BinaryRelationshipOperation extends RelationshipOperation {
	
	static Logger logger = Logger.getLogger(BinaryRelationshipOperation.class);

	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationshipOperation() {
		
	}
	
	public Operation clone() {
		BinaryRelationshipOperation op = new BinaryRelationshipOperation();
		this.copyTo(op);
		return op;
	}
	
	protected void copyTo(BinaryRelationshipOperation op) {
		super.copyTo(op);
		op.setRelationshipId(this.getRelationshipId());
		op.setLeftFeatureId(this.getLeftFeatureId());
		op.setRightFeatureId(this.getRightFeatureId());
		op.setType(this.getType());
	}
	
	public List<Operation> apply() throws BeanPersistenceException, InvalidOperationException, StaleDataException {
		
		if (!valid()) {
			throw new InvalidOperationException("Invalid op fields.");
		}
		List<Operation> result = null;
		if (relationshipId == null) {
			if (vote.equals(false)) {
				throw new InvalidOperationException("Invalid vote: NO to inexisted relationship.");
			}
			// Get the model
			Model model = DaoUtil.getModelDao().getById(modelId, false);
			if (model == null) {
				throw new InvalidOperationException("Invalid model ID: " + modelId);
			}
			
			// See if the relationship has already existed.
			boolean alreadyExisted = false;
			BinaryRelationship relation = new BinaryRelationship(userid);
			relation.setType(type);
			relation.setLeftFeatureId(leftFeatureId);
			relation.setRightFeatureId(rightFeatureId);
			List sameRelations = DaoUtil.getRelationshipDao().getByExample(modelId, relation); 
			if (sameRelations != null) {
				alreadyExisted = true;
				relation = (BinaryRelationship)sameRelations.get(0);
			} else {
				// CREATE A NEW BINARY RELATIONSHIP HERE
				relation.setFeatures(DaoUtil.getFeatureDao().getById(leftFeatureId, false),
						DaoUtil.getFeatureDao().getById(rightFeatureId, false));
			}
			if (alreadyExisted) {
				relation.vote(true, userid, modelId);
			} else {
				relation.vote(true, userid);
				model.addRelationship(relation);
			}
			
			relation = (BinaryRelationship)DaoUtil.getRelationshipDao().save(relation);
			DaoUtil.getModelDao().save(model);
			
			relationshipId = relation.getId();
			
			if (!alreadyExisted) {
				logger.info(LogUtil.logOp(userid, LogUtil.OP_CREATE,
						LogUtil.relationToStr(LogUtil.OBJ_RELATION,
								modelId, relationshipId, relation)));
			}
			
			result = ImplicitVoteOperation.makeOperation(this, relation).apply();
		} else {
		
			Relationship relation = DaoUtil.getRelationshipDao().getById(
					relationshipId, false);
			if (relation == null) {
				throw new InvalidOperationException("No relationship has ID: "
						+ relationshipId);
			}
			relation.vote(vote, userid, modelId);
			
			result = ImplicitVoteOperation.makeOperation(this, relation).apply();
			
			if (relation.getSupporterNum() <= 0) {
				DaoUtil.getRelationshipDao().delete(relation);
				logger.info(LogUtil.logOp(userid, LogUtil.OP_REMOVE,
						LogUtil.relationToStr(LogUtil.OBJ_RELATION,
								modelId, relationshipId, relation)));

			} else {
				DaoUtil.getRelationshipDao().save(relation);
			}
		}
		if (result == null) {
			result = new ArrayList<Operation>();
		}
		result.add(this.clone());
		return result;
	}
	
	public String toString() {
		return super.toString() + " " + relationshipId + " " + type + " " + leftFeatureId + " " + rightFeatureId;
	}
	
	public Long getLeftFeatureId() {
		return leftFeatureId;
	}

	public void setLeftFeatureId(Long leftFeatureId) {
		this.leftFeatureId = leftFeatureId;
	}

	public Long getRightFeatureId() {
		return rightFeatureId;
	}

	public void setRightFeatureId(Long rightFeatureId) {
		this.rightFeatureId = rightFeatureId;
	}
	
}
