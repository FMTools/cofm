package collab.fm.server.bean.operation;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;

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
	
	protected boolean typeValid() {
		return Resources.BIN_REL_EXCLUDES.equals(type) ||
			Resources.BIN_REL_REFINES.equals(type) ||
			Resources.BIN_REL_REQUIRES.equals(type);
	}
	
	public List<Operation> apply() throws BeanPersistenceException, InvalidOperationException {
		if (!valid()) {
			throw new InvalidOperationException("Invalid op fields.");
		}
		if (relationshipId == null) {
			if (vote.equals(false)) {
				throw new InvalidOperationException("Invalid vote: NO to inexisted relationship.");
			}
			// See if the relationship has already existed.
			BinaryRelationship relation = new BinaryRelationship();
			relation.setType(type);
			relation.setLeftFeatureId(leftFeatureId);
			relation.setRightFeatureId(rightFeatureId);
			List sameRelations = DaoUtil.getRelationshipDao().getByExample(relation); 
			if (sameRelations != null) {
				relation = (BinaryRelationship)sameRelations.get(0);
			} else {
				// CREATE A NEW BINARY RELATIONSHIP HERE
				relation.setFeatures(DaoUtil.getFeatureDao().getById(leftFeatureId, false),
						DaoUtil.getFeatureDao().getById(rightFeatureId, false));
			}
			relation.vote(true, userid);
			relation = (BinaryRelationship)DaoUtil.getRelationshipDao().save(relation);
			relationshipId = relation.getId();
			
			return ImplicitVoteOperation.makeOperation(this, relation).apply();
		} 
		
		Relationship relation = DaoUtil.getRelationshipDao().getById(
				relationshipId, false);
		if (relation == null) {
			throw new InvalidOperationException("No relationship has ID: "
					+ relationshipId);
		}
		relation.vote(vote, userid);
		DaoUtil.getRelationshipDao().save(relation);

		return ImplicitVoteOperation.makeOperation(this, null).apply();
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
