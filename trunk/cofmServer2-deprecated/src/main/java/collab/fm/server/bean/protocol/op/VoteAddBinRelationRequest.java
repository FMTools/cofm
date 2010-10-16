package collab.fm.server.bean.protocol.op;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class VoteAddBinRelationRequest extends Request {
	
	private Long modelId;
	
	private Long relationshipId;
	
	private Boolean yes;
	
	// relationship type
	private String type;
	
	private Long leftFeatureId;
	private Long rightFeatureId;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddBinRelationProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	public Long getRelationshipId() {
		return relationshipId;
	}
	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	public Boolean getYes() {
		return yes;
	}

	public void setYes(Boolean yes) {
		this.yes = yes;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	
	private static class VoteAddBinRelationProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddBinRelationRequest)) return false;
			VoteAddBinRelationRequest r = (VoteAddBinRelationRequest) req;
			if (r.getModelId() == null || r.getRequesterId() == null) return false;
			if (r.getRelationshipId() == null) {
				// A creating operation
				return r.getType() != null && r.getLeftFeatureId() != null && r.getRightFeatureId() != null;
			} else {
				// A voting operation
				return r.getYes() != null;
			}
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_bin_relation operation.");
			}
			VoteAddBinRelationRequest r = (VoteAddBinRelationRequest) req;
			DefaultResponse rsp = new DefaultResponse(r);
			
			// If it is a creating operation
			if (r.getRelationshipId() == null) {
				// Get the model
				Model model = DaoUtil.getModelDao().getById(r.getModelId(), false);
				if (model == null) {
					throw new InvalidOperationException("Invalid feature model ID: " + r.getModelId());
				}
				
				// See if the relationship has already existed.
				rsp.setExist(false);
				BinaryRelationship relation = new BinaryRelationship(r.getRequesterId());
				relation.setType(r.getType());
				relation.setLeftFeatureId(r.getLeftFeatureId());
				relation.setRightFeatureId(r.getRightFeatureId());
				List sameRelations = DaoUtil.getRelationshipDao().getByExample(r.getModelId(), relation); 
				if (sameRelations != null) {
					rsp.setExist(true);
					relation = (BinaryRelationship)sameRelations.get(0);
				} else {
					// CREATE THE (ACTUAL) RELATIONSHIP HERE (USING Feature OBJECTS.)
					relation.setFeatures(DaoUtil.getFeatureDao().getById(r.getLeftFeatureId(), false),
							DaoUtil.getFeatureDao().getById(r.getRightFeatureId(), false));
					// After creation, add it to the model.
					model.addRelationship(relation);
				}
				relation.vote(true, r.getRequesterId());
				
				// Save and set relationshipId in the response
				relation = (BinaryRelationship)DaoUtil.getRelationshipDao().save(relation);
				rsp.setRelationshipId(relation.getId());

				// Save the model
				if (rsp.getExist().booleanValue() == false) {
					DaoUtil.getModelDao().save(model);
				}
				
				// Set the inferred votes (because the "vote" always is "YES" here.)
				rsp.setInferVotes(generateInferVotes(relation));
				
			} else { // A voting operation
				
				Relationship relation = DaoUtil.getRelationshipDao().getById(
						r.getRelationshipId(), false);
				if (relation == null) {
					throw new InvalidOperationException("Invalid relationship ID: "
							+ r.getRelationshipId());
				}
				// Set the inferred votes
				if (r.getYes().booleanValue() == true) {
					rsp.setInferVotes(generateInferVotes(relation));
				}
				// Handle the vote and possible removals.
				if (relation.vote(r.getYes(), r.getRequesterId())) {
					DaoUtil.getRelationshipDao().save(relation);
				} else { //removal
					DaoUtil.getRelationshipDao().delete(relation);
				}
				// Set the fields to proper values in the response
				rsp.setExist(true);
				rsp.setLeftFeatureId(null);
				rsp.setRightFeatureId(null); // left & right feature ID is unnecessary for voting operations
			}
			
			// Add "back" and "broadcast" responses to the response group
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}

		private List<Long> generateInferVotes(Relationship relation) {
			List<Long> rslt = new ArrayList<Long>();
			for (Feature f: relation.getFeatures()) {
				rslt.add(f.getId());
			}
			if (rslt.size() > 0) {
				return rslt;
			}
			return null;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		private Boolean exist;
		
		private Long modelId;
		private Long relationshipId;
		private String type;
		private Boolean yes;
		private Long leftFeatureId;
		private Long rightFeatureId;
		
		private List<Long> inferVotes;
		
		public DefaultResponse(VoteAddBinRelationRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setRelationshipId(r.getRelationshipId());
			this.setType(r.getType());
			this.setYes(r.getYes());
			this.setLeftFeatureId(r.getLeftFeatureId());
			this.setRightFeatureId(r.getRightFeatureId());
		}
		
		public Boolean getExist() {
			return exist;
		}

		public void setExist(Boolean exist) {
			this.exist = exist;
		}

		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
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

		public Boolean getYes() {
			return yes;
		}

		public void setYes(Boolean yes) {
			this.yes = yes;
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

		public List<Long> getInferVotes() {
			return inferVotes;
		}

		public void setInferVotes(List<Long> inferVotes) {
			this.inferVotes = inferVotes;
		}
		
	}
	
}
