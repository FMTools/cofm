package collab.fm.server.bean.protocol.op;

import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.PersonalView;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
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

public class VoteAddValueRequest extends Request {

	private Long modelId;
	private Long activePvId;
	private Long entityId;
	private Long attrId;
	private String val;
	private Boolean yes;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddValueProcessor();
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

	public Long getAttrId() {
		return attrId;
	}

	public void setAttrId(Long attrId) {
		this.attrId = attrId;
	}

	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public Boolean getYes() {
		return yes;
	}
	public void setYes(Boolean yes) {
		this.yes = yes;
	}
	
	public void setActivePvId(Long activePvId) {
		this.activePvId = activePvId;
	}

	public Long getActivePvId() {
		return activePvId;
	}

	private static class VoteAddValueProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddValueRequest)) return false;
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			return r.getModelId() != null && r.getEntityId() != null &&
				r.getAttrId() != null && r.getVal() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_value operation");
			}
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			DefaultResponse rsp = new DefaultResponse(r);
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			PersonalView pv = DaoUtil.getPersonalViewDao().getById(r.getActivePvId(), false);
			if (pv == null) {
				throw new InvalidOperationException("Invalid personal view ID: " + r.getActivePvId());
			}
			
			Entity en = DaoUtil.getEntityDao().getById(r.getEntityId(), false);
			if (en == null) {
				throw new InvalidOperationException("Invalid entity ID: " + r.getEntityId());
			}
			
			EntityType entp = DaoUtil.getEntityTypeDao().getById(en.getType().getId(), false);
			AttributeType attrDef = entp.findAttributeTypeDef(r.getAttrId(), false);
			if (attrDef == null) {
				throw new InvalidOperationException("Unknown attribute definition of the entity.");
			}
			
			// If the attribute is NOT enabled to have global duplicates, we should check
			// for the same value in current model.
			if (!attrDef.isEnableGlobalDupValues()) {
				List<Entity> allWithSameValue = DaoUtil.getEntityDao()
					.getByAttrValue(r.getModelId(),
							r.getAttrId(), r.getVal(), false);
				if (allWithSameValue != null) {
					en = allWithSameValue.get(0);
				}
				
			}

			if (en.voteOrAddValue(r.getAttrId(), 
					r.getVal(), r.getYes(), r.getRequesterId(), pv) == DataItem.INVALID_OPERATION) {
				req.setLastError("Invalid value: " + r.getVal());
				return false;
			}
			
			en.setLastModifier(r.getRequesterId());
			en = DaoUtil.getEntityDao().save(en);
			
			DaoUtil.getPersonalViewDao().save(pv);
			
			rsp.setExecTime(DataItemUtil.formatDate(en.getLastModifyTime()));
			rsp.setEntityId(en.getId());
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		private Long modelId;
		private Long entityId;
		private Long attrId;
		private String val;
		private Boolean yes;
		
		public DefaultResponse(VoteAddValueRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setEntityId(r.getEntityId());
			this.setAttrId(r.getAttrId());
			this.setVal(r.getVal());
			this.setYes(r.getYes());
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

		public Long getAttrId() {
			return attrId;
		}

		public void setAttrId(Long attrId) {
			this.attrId = attrId;
		}

		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public Boolean getYes() {
			return yes;
		}
		public void setYes(Boolean yes) {
			this.yes = yes;
		}
	}
}
