package collab.fm.server.bean.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * Deduce the implicit votes on features or relationships, according to the following 5 rules: <br/>
 * <li>1. Vote NO on feature F -> Vote NO on relationships involving F.</li>
 * <li>2. Vote YES on relationship R -> Vote YES on features involved in R.</li>
 * <li>3. Vote NO on feature F -> Vote NO on F's attributes.</li>
 * <li>4. Vote YES on feature F's attributes -> Vote YES on F</li>
 * <li>5. Create X -> Vote YES on X.</li>
 * Note the third and the fifth rule do NOT have targets (see targetIds property of this class).
 * However, the third rule is implemented here which means the database is changed. But the 
 * fifth rule is implemented in FeatureOperation and RelationshipOperations, respectively. <br/>
 *   
 * @author Yi Li
 *
 */
public class ImplicitVoteOperation extends Operation {

	/**
	 * Implicit vote targets, which can be features or relationships, according to the operation name.
	 * The operation name should be either CreateRelationship or CreateFeature.
	 */
	protected List<Long> targetIds;
	
	protected Operation sourceOp;
	protected Object sourceObject;

	public Operation clone() {
		ImplicitVoteOperation op = new ImplicitVoteOperation(sourceOp);
		this.copyTo(op);
		return op;
	}
	
	public ImplicitVoteOperation(Operation sourceOp) {
		this.setModelId(sourceOp.getModelId());
		this.sourceOp = sourceOp;
	}
	
	protected void copyTo(ImplicitVoteOperation op) {
		super.copyTo(op);
		op.setTargetIds(this.getTargetIds());
	}
	
	protected void setBasicInfo(String opName, boolean yes, Long userid) {
		this.setName(opName);
		this.setVote(yes);
		this.setUserid(userid);
	}
	
	public static ImplicitVoteOperation makeOperation(Operation op, Object source) {
		if (Resources.OP_ADD_DES.equals(op.getName()) ||
				Resources.OP_ADD_NAME.equals(op.getName()) ||
				Resources.OP_SET_OPT.equals(op.getName())) {
			return new DeduceFromFeatureAttributeVote(op, source);
		}
		if (Resources.OP_CREATE_FEATURE.equals(op.getName())) {
			return new DeduceFromFeatureVote(op, source);
		}
		return new DeduceFromRelationshipVote(op, source);	
	}
	
	public List<Long> getTargetIds() {
		return targetIds;
	}

	public void setTargetIds(List<Long> targetIds) {
		this.targetIds = targetIds;
	}
	
	private static class DeduceFromFeatureVote extends ImplicitVoteOperation {		
		public DeduceFromFeatureVote(Operation op, Object source) {
			super(op);
			sourceObject = source;
		}
		
		/**
		 * Rule 1 & 3: Vote NO on feature F -> Vote NO on involved relationships and attributes 
		 */
		public List<Operation> apply() throws BeanPersistenceException, InvalidOperationException, StaleDataException {
			try {
				if (sourceOp.getVote().equals(false)) {
					setBasicInfo(Resources.OP_CREATE_RELATIONSHIP, false, sourceOp.getUserid());
					
					Feature feature = (Feature)sourceObject;
					// Handle rule 1
					for (Relationship rel : feature.getRelationships()) {
						rel.vote(false, sourceOp.getUserid());
						if (rel.getSupporterNum() <= 0) {
							DaoUtil.getRelationshipDao().delete(rel);
						} else {
							DaoUtil.getRelationshipDao().save(rel);
						}
					}
					
					// Handle rule 3
					feature.voteAllDescription(false, sourceOp.getUserid());
					feature.voteAllName(false, sourceOp.getUserid());
					
					// Construct forwarded implicit operations, note that rule 3 don't have to be forwarded.
					if (feature.getRelationships().size() <= 0) {
						return null;  // nothing to forward
					}
					targetIds = new ArrayList<Long>();
					for (Relationship rel: feature.getRelationships()) {
						targetIds.add(rel.getId());
					}
					List<Operation> result = new ArrayList<Operation>();
					result.add(this.clone());
					return result;
				}
				return null;
			} catch (ClassCastException cce) {
				throw new InvalidOperationException("Couldn't get the source feature.", cce);
			} catch (BeanPersistenceException bpe) {
				throw bpe;
			}
		}
	}
	
	private static class DeduceFromFeatureAttributeVote extends ImplicitVoteOperation {
		public DeduceFromFeatureAttributeVote(Operation op, Object source) {
			super(op);
			sourceObject = source;
		}
		
		/**
		 * Rule 4: Vote YES on feature F's attributes -> Vote YES on F
		 */
		public List<Operation> apply() throws InvalidOperationException, StaleDataException {
			try {
				// Set_Optionality always imply a YES vote to feature.
				if (sourceOp.getVote().equals(true) || Resources.OP_SET_OPT.equals(sourceOp.getName())) {
					setBasicInfo(Resources.OP_CREATE_FEATURE, true, sourceOp.getUserid());
					
					// Modify the database
					Feature feature = (Feature)sourceObject;
					feature.vote(true, sourceOp.getUserid());
					
					// Construct the implicit operation
					targetIds = new ArrayList<Long>();
					targetIds.add(feature.getId());
					
					List<Operation> result = new ArrayList<Operation>();
					result.add(this.clone());
					return result;
				}
				return null; // No implicit operations were generated
				
			} catch (ClassCastException cce) {
				throw new InvalidOperationException("Couldn't get the source feature.", cce);
			} 			
		}
	}
	
	private static class DeduceFromRelationshipVote extends ImplicitVoteOperation {
		public DeduceFromRelationshipVote(Operation op, Object source) {
			super(op);
			sourceObject = source;
		}
		
		/**
		 * Rule 2: Vote YES on relationship R -> Vote YES on features involved in R
		 */
		public List<Operation> apply() throws BeanPersistenceException, InvalidOperationException, StaleDataException {
			try {
				if (sourceOp.getVote().equals(true)) {
					setBasicInfo(Resources.OP_CREATE_FEATURE, true, sourceOp.getUserid());
					// Handle rule 2
					Relationship rel = (Relationship)sourceObject;
					targetIds = new ArrayList<Long>();
					for (Feature feature: rel.getFeatures()) {
						feature.vote(true, userid);
						DaoUtil.getFeatureDao().save(feature);
						// Forward the implicit votes
						targetIds.add(feature.getId());
					}
					List<Operation> result = new ArrayList<Operation>();
					result.add(this.clone());
					return result;
				}
				return null;
			} catch (BeanPersistenceException bpe) {
				throw bpe;
			} catch (Exception e) {
				throw new InvalidOperationException("Couldn't get the source operation.", e); 
			}
		}
	}

	@Override
	public List<Operation> apply() throws BeanPersistenceException,
			InvalidOperationException, StaleDataException {
		// TODO Auto-generated method stub
		return null;
	}
}
