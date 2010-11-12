package collab.fm.server.bean.protocol.op;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.BinRelationType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class VoteAddBinRelationRequest extends Request {
	
	private Long modelId;
	
	private Long relationId;
	
	private Boolean yes;
	
	// relationship type
	private Long typeId;
	
	private Long sourceId;
	private Long targetId;
	
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
	public Boolean getYes() {
		return yes;
	}

	public void setYes(Boolean yes) {
		this.yes = yes;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	
	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	private static class VoteAddBinRelationProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddBinRelationRequest)) return false;
			VoteAddBinRelationRequest r = (VoteAddBinRelationRequest) req;
			if (r.getModelId() == null || r.getRequesterId() == null) return false;
			if (r.getRelationId() == null) {
				// A creating operation
				return r.getTypeId() != null && r.getSourceId() != null && r.getTargetId() != null;
			} else {
				// A voting operation
				return r.getYes() != null;
			}
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_bin_relation operation.");
			}
			VoteAddBinRelationRequest r = (VoteAddBinRelationRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			DefaultResponse rsp = new DefaultResponse(r);
			BinRelation br = null;
			if (r.getRelationId() != null && 
					(br = (BinRelation) DaoUtil.getRelationDao().getById(r.getRelationId(), false)) != null) {
				// voting 
				rsp.setExist(true);
				br.setLastModifier(r.getRequesterId());
				
				// Set the inferred votes
				if (r.getYes().booleanValue() == true) {
					rsp.setInferVotes(computeInferVotes(br));
				}
				
				// Handle the vote and possible removals.
				if (br.vote(r.getYes(), r.getRequesterId()) == DataItem.REMOVAL_EXECUTED) {
					DaoUtil.getRelationDao().delete(br);
				} else {
					DaoUtil.getRelationDao().save(br);
				}
				
			} else {
				// creating
				BinRelationType myType = 
					(BinRelationType) DaoUtil.getRelationTypeDao().getById(r.getTypeId(), false);
				if (myType == null) {
					throw new InvalidOperationException("Invalid relation type.");
				}
				
				rsp.setExist(false);
				
				br = new BinRelation();
				DataItemUtil.setNewDataItemByUserId(br, r.getRequesterId());
				br.setType(myType);
				br.setSourceId(r.getSourceId());
				br.setTargetId(r.getTargetId());
				
				// See if the same relation has already existed.
				List sameRelations = DaoUtil.getRelationDao().getByExample(
						r.getModelId(), br);
				if (sameRelations != null) {
					br = (BinRelation) sameRelations.get(0);
					rsp.setExist(true);
				} else {
					Element src = DaoUtil.getElementDao().getById(r.getSourceId(), false);
					if (src == null) {
						throw new InvalidOperationException("Invalid source ID: " + r.getSourceId());
					}
					
					Element target = DaoUtil.getElementDao().getById(r.getTargetId(), false);
					if (target == null) {
						throw new InvalidOperationException("Invalid target ID: " + r.getTargetId());
					}
					
					br.resetElements(src, target);
					m.addRelation(br);
					DaoUtil.getModelDao().save(m);
				}
				
				// Creation always leads to a YES vote.
				br.vote(true, r.getRequesterId());
				
				br = (BinRelation) DaoUtil.getRelationDao().save(br);
				rsp.setRelationId(br.getId());
				
				// Set the inferred votes (because the "vote" always is "YES" here.)
				rsp.setInferVotes(computeInferVotes(br));
				
			}
			
			// Add "back" and "broadcast" responses to the response group
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}

		private List<Long> computeInferVotes(Relation r) {
			List<Long> rslt = new ArrayList<Long>();
			DataItemUtil.generateInferVotes(r, rslt);
			return rslt.size() <= 0 ? null : rslt;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		private Boolean exist;
		
		private Long modelId;
		
		private Long relationId;
		
		private Boolean yes;
		
		// relationship type
		private Long typeId;
		
		private Long sourceId;
		private Long targetId;
		
		private List<Long> inferVotes;
		
		public DefaultResponse(VoteAddBinRelationRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setYes(r.getYes());
			this.setRelationId(r.getRelationId());
			this.setTargetId(r.getTargetId());
			this.setTypeId(r.getTypeId());
			this.setSourceId(r.getSourceId());
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

		public Boolean getYes() {
			return yes;
		}

		public void setYes(Boolean yes) {
			this.yes = yes;
		}

		public List<Long> getInferVotes() {
			return inferVotes;
		}

		public void setInferVotes(List<Long> inferVotes) {
			this.inferVotes = inferVotes;
		}

		public Long getRelationId() {
			return relationId;
		}

		public void setRelationId(Long relationId) {
			this.relationId = relationId;
		}

		public Long getTypeId() {
			return typeId;
		}

		public void setTypeId(Long typeId) {
			this.typeId = typeId;
		}

		public Long getSourceId() {
			return sourceId;
		}

		public void setSourceId(Long sourceId) {
			this.sourceId = sourceId;
		}

		public Long getTargetId() {
			return targetId;
		}

		public void setTargetId(Long targetId) {
			this.targetId = targetId;
		}
		
	}
	
}
