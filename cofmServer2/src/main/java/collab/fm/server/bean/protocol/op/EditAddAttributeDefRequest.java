package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.entity.AttributeType;
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

/** Add or edit attribute-definitions for an entity type in a model.
 *  Now only the attribute name ("attr" field) can be edited.
 */
public class EditAddAttributeDefRequest extends Request {
	
	// If attrId is valid (not null and exists), this is an editing;
	// otherwise it is an adding.
	protected Long attrId;
	
	protected Long modelId;
	protected Long entityTypeId;
	
	protected String attr;
	protected String type;
	protected Boolean multiYes;
	protected Boolean allowDup;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new AddAttributeProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}
	
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	
	public Long getAttrId() {
		return attrId;
	}

	public void setAttrId(Long attrId) {
		this.attrId = attrId;
	}

	public Long getEntityTypeId() {
		return entityTypeId;
	}

	public void setEntityTypeId(Long entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getMultiYes() {
		return multiYes;
	}
	public void setMultiYes(Boolean multiYes) {
		this.multiYes = multiYes;
	}
	public Boolean getAllowDup() {
		return allowDup;
	}
	public void setAllowDup(Boolean allowDup) {
		this.allowDup = allowDup;
	}
	
	protected static class AddAttributeProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof EditAddAttributeDefRequest)) return false;
			EditAddAttributeDefRequest r = (EditAddAttributeDefRequest) req;
			if (r.getMultiYes() == null) {
				r.setMultiYes(true);
			}
			if (r.getAllowDup() == null) {
				r.setAllowDup(true);
			}
			return r.getModelId() != null && r.getName() != null && r.getEntityTypeId() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_attribute operatin");
			}
			
			EditAddAttributeDefRequest r = (EditAddAttributeDefRequest) req;
			
			if (DaoUtil.getModelDao().getById(r.getModelId(), true) == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			EntityType entp = DaoUtil.getEntityTypeDao().getById(r.getEntityTypeId(), true);
			if (entp == null) {
				throw new InvalidOperationException("Invalid entity type id: " + r.getEntityTypeId());
			}
			
			AttributeType a = null;
			if (r.getAttrId() != null && 
					(a = DaoUtil.getAttributeDefDao().getById(r.getAttrId(), false)) != null) {
				// An editing operation
				a.setAttrName(r.getAttr());
				a.setLastModifier(r.getRequesterId());
				DaoUtil.getAttributeDefDao().save(a);
			} else {
				// An adding operation
				if (entp.findAttributeTypeDef(r.getAttr()) != null) {
					throw new InvalidOperationException("Attribute has already existed: " + r.getAttr());
				}
				
				a = createAttribute(r);
				a.setHostType(entp);
				
				a = DaoUtil.getAttributeDefDao().save(a);
				
				entp.getAttrDefs().add(a);
				DaoUtil.getEntityTypeDao().save(entp);
				
				r.setAttrId(a.getId());
			}
			
			DefaultResponse rsp = createResponse(r);
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = createResponse(r);
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
		protected DefaultResponse createResponse(EditAddAttributeDefRequest r) {
			return new DefaultResponse(r);
		}

		protected AttributeType createAttribute(EditAddAttributeDefRequest r) {
			AttributeType a = new AttributeType();
			DataItemUtil.setNewDataItemByUserId(a, r.getRequesterId());
			
			a.setAttrName(r.getAttr());
			a.setTypeName(r.getType());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			return a;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		protected Long modelId;
		protected Long attrId;
		protected Long entityTypeId;
		protected String attr;
		protected String type;
		protected Boolean multiYes;
		protected Boolean allowDup;
		
		public DefaultResponse(EditAddAttributeDefRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setAttrId(r.getAttrId());
			this.setEntityTypeId(r.getEntityTypeId());
			this.setAttr(r.getAttr());
			this.setType(r.getType());
			this.setMultiYes(r.getMultiYes());
			this.setAllowDup(r.getAllowDup());
		}
		
		public Long getModelId() {
			return modelId;
		}
		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}
		
		public Long getAttrId() {
			return attrId;
		}

		public void setAttrId(Long attrId) {
			this.attrId = attrId;
		}

		public Long getEntityTypeId() {
			return entityTypeId;
		}

		public void setEntityTypeId(Long entityTypeId) {
			this.entityTypeId = entityTypeId;
		}

		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Boolean getMultiYes() {
			return multiYes;
		}
		public void setMultiYes(Boolean multiYes) {
			this.multiYes = multiYes;
		}
		public Boolean getAllowDup() {
			return allowDup;
		}
		public void setAllowDup(Boolean allowDup) {
			this.allowDup = allowDup;
		}
	}
	
}
