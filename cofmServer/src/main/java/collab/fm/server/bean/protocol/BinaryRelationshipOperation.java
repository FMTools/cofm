package collab.fm.server.bean.protocol;

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
public class BinaryRelationshipOperation extends Operation {
	
	static Logger logger = Logger.getLogger(BinaryRelationshipOperation.class);

	/**
	 * A null relationshipId means a new relationship is created.
	 */
	private Long relationshipId;
	private String type; // relationship type
	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationshipOperation() {
		
	}
	
	private boolean isTypeValid() {
		return Resources.BIN_REL_EXCLUDES.equals(type) ||
			Resources.BIN_REL_REFINES.equals(type) ||
			Resources.BIN_REL_REQUIRES.equals(type);
	}
	
	public boolean valid() {
		logger.debug("check BinaryRelationshipOperation is valid.");
		if (super.valid() && userid != null) {
			return isTypeValid() && leftFeatureId != null && rightFeatureId != null;
		}
		return false;
	}
	
	public Operation apply() throws BeanPersistenceException, InvalidOperationException {
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
			if (DaoUtil.getRelationshipDao().getByExample(relation, false) != null) {
				throw new InvalidOperationException("Relationship '" + leftFeatureId + " " + type + " " + rightFeatureId + "' already existed.");
			}
			relation.vote(true, userid);
			relationshipId = DaoUtil.getRelationshipDao().save(relation);
			Feature left = DaoUtil.getFeatureDao().getById(leftFeatureId);
			Feature right = DaoUtil.getFeatureDao().getById(rightFeatureId);
			checkImplyYesToInvolvedFeatures(Arrays.asList(new Feature[] {left, right}));
		} else {
			Relationship relation = DaoUtil.getRelationshipDao().getById(relationshipId);
			if (relation == null) {
				throw new InvalidOperationException("No relationship has ID: " + relationshipId);
			}
			relation.vote(vote, userid);
			DaoUtil.getRelationshipDao().update(relation);
			checkImplyYesToInvolvedFeatures(null);
		}
		return this;
	}
	
	private void checkImplyYesToInvolvedFeatures(List<Feature> features) throws BeanPersistenceException {
		if (vote.equals(true)) {
			if (features == null) {
				features = DaoUtil.getRelationshipDao().getInvolvedFeatures(relationshipId);
			}
			if (features != null) {
				for (Feature feature: features) {
					feature.vote(true, userid);
				}
				DaoUtil.getFeatureDao().updateAll(features);
			}
		}
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
	
	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
