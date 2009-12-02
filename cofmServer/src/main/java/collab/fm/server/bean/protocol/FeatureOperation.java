package collab.fm.server.bean.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.persistence.FeatureDao;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;

/**
 * TODO: Origin vote and implicit votes should be transactional. <br/>
 * 
 * Class FeatureOperation defines the structure and meaning of operations which affect 
 * a feature or its attributes, including create_feature, add_name, add_description, and
 * set_optionality. <br/>
 * The logic of FeatureOperation.apply(): <br/>
 *  1) create_feature: If the feature ID is not null, it means this is a vote to an existed feature.
 *  Otherwise, try to create a new feature. First, check the name to ensure there's no other features with the same name.
 *  Then create (insert) the new feature via the persistence layer, which returns the generated
 *  database identifier. Finally, the database ID sets the featureId field in the operation. <br/>
 *  2) add_name/description, set_optionality: Check the feature's ID, and then invoke voteName/Description/Optionality. <br/> 
 *  3) If vote NO to a feature, then all relationships that contains the feature are voted NO. In addition, all
 *  attributes of the feature are voted NO as well. <br/>
 *  4) A voteExistence(YES) is implied when create a new feature. <br/>
 *  5) If vote YES to name or description of a feature, then a voteExistence(YES) is implied. <br/>
 *  6) Vote to optionality always implies YES to feature. 
 *  
 * @author Yi Li
 *
 */
public class FeatureOperation extends Operation {

	static Logger logger = Logger.getLogger(FeatureOperation.class);
	
	private Long featureId;
	private String value;
	
	public FeatureOperation() {
		
	}
	
	public Operation apply() throws BeanPersistenceException, InvalidOperationException {
		if (!valid()) {
			throw new InvalidOperationException("Invalid op fields.");
		}
		if (Resources.OP_ADD_DES.equals(name)) {
			return applyAddDes();
		} else if (Resources.OP_ADD_NAME.equals(name)) {
			return applyAddName();
		} else if (Resources.OP_CREATE_FEATURE.equals(name)) {
			return applyCreateFeature();
		} else if (Resources.OP_SET_OPT.equals(name)) {
			return applySetOpt();
		} 
		throw new InvalidOperationException("Invalid op name: " + name);
	}
	
	public boolean valid() {
		if (super.valid() && userid != null) {
			if (Resources.OP_CREATE_FEATURE.equals(name)) {
				if (vote.equals(false)) {
					return featureId != null;
				}
				return featureId != null || value != null;
			} else {
				return featureId != null;
			}
		}
		return false;
	}
	
	private Operation applyAddDes() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteDescription(value, vote, userid);
		checkImplyYesToFeature(feature);
		DaoUtil.getFeatureDao().update(feature);
		return this;
	}
	
	private Operation applyAddName() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteName(value, vote, userid);
		checkImplyYesToFeature(feature);
		DaoUtil.getFeatureDao().update(feature);
		return this;
	}
	
	private Operation applyCreateFeature() throws BeanPersistenceException, InvalidOperationException {
		if (featureId == null) {
			if (vote.equals(false)) {
				throw new InvalidOperationException("Invalid vote: NO to inexisted feature.");
			}
			// Check if a feature with same name has already existed.
			Feature featureWithSameName = new Feature();
			featureWithSameName.voteName(value, vote, userid);
			if (DaoUtil.getFeatureDao().getByExample(featureWithSameName, false) != null) {
				throw new InvalidOperationException("Feature '" + value + "' already existed.");
			}
			// Now save the new feature, which will return the generated feature ID
			featureId = DaoUtil.getFeatureDao().save(featureWithSameName);
			checkImplyYesToFeature(featureWithSameName);
			return this;
		}
		Feature feature = DaoUtil.getFeatureDao().getById(featureId);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.vote(vote, userid);
		DaoUtil.getFeatureDao().update(feature);
		if (vote.equals(false)) {
			List<Relationship> rels = DaoUtil.getFeatureDao().getInvolvedRelationships(featureId);
			if (rels != null) {
				for (Relationship rel: rels) {
					rel.vote(false, userid);
				}
				DaoUtil.getRelationshipDao().updateAll(rels);
			}
			checkImplyNoToFeatureAttributes(feature);
		}
		return this;
	}
	
	private Operation applySetOpt() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteOptionality(vote, userid);
		feature.vote(true, userid); // Always implies a YES to feature
		DaoUtil.getFeatureDao().update(feature);
		return this;
	}
	
	private void checkImplyYesToFeature(Feature feature) {
		if (vote.equals(true)) {
			feature.vote(true, userid);
		}
	}
	
	private void checkImplyNoToFeatureAttributes(Feature feature) {
		if (vote.equals(false)) {
			feature.voteAllDescription(false, userid);
			feature.voteAllName(false, userid);
		}
	}
	
	public String toString() {
		return super.toString() + " " + featureId + " " + value;
	}
	
	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
