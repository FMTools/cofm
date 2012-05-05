package collab.fm.server.bean.protocol.op;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
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
	
	private String signature;
	private Boolean refine;
	private String relName;
	
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

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Boolean getRefine() {
		return refine;
	}

	public void setRefine(Boolean refine) {
		this.refine = refine;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public String getRelName() {
		return relName;
	}

	private static class VoteAddBinRelationProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddBinRelationRequest)) return false;
			VoteAddBinRelationRequest r = (VoteAddBinRelationRequest) req;
			if (r.getModelId() == null || r.getRequesterId() == null) return false;
			if (r.getRelationId() == null) {
				// A creating operation
				return r.getSignature() != null;
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
			Relation br = null;
			if (r.getRelationId() != null && 
					(br = (Relation) DaoUtil.getRelationDao().getById(r.getRelationId(), false)) != null) {
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
					br = DaoUtil.getRelationDao().save(br);
					rsp.setExecTime(DataItemUtil.formatDate(br.getLastModifyTime()));
				}
				
			} else {
				// creating
				rsp.setExist(false);
				
				br = new Relation();
				br.setSignature(r.getSignature());
				
				// See if the same relation has already existed. (Use signature)
				List sameRelations = DaoUtil.getRelationDao().getByExample(
						r.getModelId(), br);
				if (sameRelations != null) {
					br = (Relation) sameRelations.get(0);
					rsp.setExist(true);
				} else {
					br = sigToRelation(r.getSignature(), 0, r.getSignature().length());
					DataItemUtil.setNewDataItemByUserId(br, r.getRequesterId());
					br.setName(r.getRelName());
					br.setRefine(r.getRefine());
					
					br.computeSignature();
					
					m.addRelation(br);
					DaoUtil.getModelDao().save(m);
				}
				
				// Creation always leads to a YES vote.
				br.vote(true, r.getRequesterId());
				br.setLastModifier(r.getRequesterId());
				
				br = DaoUtil.getRelationDao().save(br);
				rsp.setRelationId(br.getId());
				
				// Set the inferred votes (because the "vote" always is "YES" here.)
				rsp.setInferVotes(computeInferVotes(br));
				rsp.setExecTime(DataItemUtil.formatDate(br.getLastModifyTime()));
			}
			
			// Add "back" and "broadcast" responses to the response group
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}

		private List<Long> computeInferVotes(Relation r) 
			throws ItemPersistenceException, StaleDataException {
			List<Long> onEntity = new ArrayList<Long>();
			
			for (Entity e: r.getEntities()) {
				onEntity.add(e.getId());
			}
			return onEntity.size() <= 0 ? null : onEntity;
		}
		
		private Relation sigToRelation(String sig, int pos, int last) throws ItemPersistenceException, StaleDataException {
			Relation cur = new Relation();
			
			int begin = pos, end;
			
			// Relation type
			for (end = pos; end < last && isDigit(sig.charAt(end)); end++) {
				// empty loop
			}
			int curNum = Integer.valueOf(sig.substring(begin, end));
			cur.setPredicate(curNum);
			
			long id = 0;
			begin = end + 1;  // Skip '('
			end = begin;
			while (end < last) {
				// Find a number
				while (end < last && isDigit(sig.charAt(end))) {
					end++;
				}
				if (begin < end) {
					id = Long.valueOf(sig.substring(begin, end));
				}
				
				if (end < last) {
					if (sig.charAt(end) == '(') {
						int nest = 1;
						while (nest > 0 && ++end < last) {
							if (sig.charAt(end) == '(') {
								nest++;
							} else if (sig.charAt(end) == ')') {
								nest--;
							}
						}
						if (end < last) {
							++end;
						}
						cur.addElement(sigToRelation(sig, begin, end));
					} else if (id > 0) {
						Entity en = DaoUtil.getEntityDao().getById(id, false);
						cur.addElement(en);
					}
					end++;
				}
				// Next number position
				begin = end;
			}
			
			return cur;
		}
		
		private boolean isDigit(char c) {
			return c >= '0' && c <= '9';
		}
	}
	
	public static class DefaultResponse extends Response {
		private Boolean exist;
		
		private Long modelId;
		
		private Long relationId;
		
		private Boolean yes;
		
		private String signature;
		private Boolean refine;
		private String relName;
		
		private List<Long> inferVotes;
		
		public DefaultResponse(VoteAddBinRelationRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setYes(r.getYes());
			this.setRelationId(r.getRelationId());
			this.setSignature(r.getSignature());
			this.setRefine(r.getRefine());
			this.setRelName(r.getRelName());
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

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public Boolean getRefine() {
			return refine;
		}

		public void setRefine(Boolean refine) {
			this.refine = refine;
		}

		public void setRelName(String relName) {
			this.relName = relName;
		}

		public String getRelName() {
			return relName;
		}

	}
	
}
