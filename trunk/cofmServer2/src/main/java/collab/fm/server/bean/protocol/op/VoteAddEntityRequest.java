package collab.fm.server.bean.protocol.op;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class VoteAddEntityRequest extends Request {

	private Long modelId;
	private Long entityId;
	private Long typeId;
	private Boolean yes;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddFeatureProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Boolean getYes() {
		return yes;
	}

	public void setYes(Boolean yes) {
		this.yes = yes;
	}

	private static class VoteAddFeatureProcessor implements Processor {

		public boolean process(Request req, ResponseGroup rg) 
		throws InvalidOperationException, ItemPersistenceException, StaleDataException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_feature operation.");
			}
			VoteAddEntityRequest r = (VoteAddEntityRequest) req;
			DefaultResponse rsp = new DefaultResponse((VoteAddEntityRequest)req);
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			Entity en = null;
			if (r.getEntityId() != null &&
					(en = DaoUtil.getEntityDao().getById(r.getEntityId(), false)) != null) {
				// voting 
				rsp.setExist(new Boolean(true));
				
				if (r.getYes().booleanValue() == false) {
					// Set the inferred vote in response
					List<Long> targets = new ArrayList<Long>();
					for (Relation rel: en.getRels()) {
						targets.add(rel.getId());
					}
					if (targets.size() > 0) {
						rsp.setInferVotes(targets);
					}
				}
				
				if (en.vote(r.getYes().booleanValue(), r.getRequesterId()) == DataItem.REMOVAL_EXECUTED) {
					DaoUtil.getEntityDao().delete(en);
				} else {
					DaoUtil.getEntityDao().save(en);
				}
				
			} else {
				// creating
				rsp.setExist(false);
				EntityType entp = DaoUtil.getEntityTypeDao().getById(r.getTypeId(), false);
				if (entp == null) {
					throw new InvalidOperationException("Invalid entity type ID: " + r.getTypeId());
				}
				
				en = new Entity();
				DataItemUtil.setNewDataItemByUserId(en, r.getRequesterId());
				en.setModel(m);
				en.setType(entp);
				
				// Creation always leads to a YES vote.
				en.vote(true, r.getRequesterId());
				
				m.addEntity(en);
				
				en = DaoUtil.getEntityDao().save(en);
				DaoUtil.getModelDao().save(m);
			}
			
			rsp.setEntityId(en.getId());
			
			// Write the responses (back and broadcast)
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddEntityRequest)) return false;
			VoteAddEntityRequest r = (VoteAddEntityRequest) req;
			if (r.getModelId() == null || r.getRequesterId() == null) return false;
			if (r.getEntityId() == null) {
				return r.getTypeId() != null;
			}
			return r.getYes() != null;
		}
	}
	
	public static class DefaultResponse extends Response {
		
		// exist == true if this is a voting.
		private Boolean exist;
		
		private Long modelId;
		private Long entityId;
		private Long typeId;
		private Boolean yes;
		
		// inference votes on relationships (if any)
		private List<Long> inferVotes;
		
		public DefaultResponse(VoteAddEntityRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setEntityId(r.getEntityId());
			this.setTypeId(r.getTypeId());
			this.setYes(r.getYes());
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

		public Long getEntityId() {
			return entityId;
		}

		public void setEntityId(Long entityId) {
			this.entityId = entityId;
		}

		public Long getTypeId() {
			return typeId;
		}

		public void setTypeId(Long typeId) {
			this.typeId = typeId;
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
		
	}
}
