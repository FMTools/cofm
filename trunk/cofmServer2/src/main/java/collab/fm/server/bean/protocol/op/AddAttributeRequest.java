package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class AddAttributeRequest extends Request {
	protected Long modelId;
	protected String entityType;
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

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
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
			if (!(req instanceof AddAttributeRequest)) return false;
			AddAttributeRequest r = (AddAttributeRequest) req;
			if (r.getMultiYes() == null) {
				r.setMultiYes(true);
			}
			if (r.getAllowDup() == null) {
				r.setAllowDup(true);
			}
			return r.getName() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_attribute operatin");
			}
			
			AddAttributeRequest r = (AddAttributeRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			EntityType entp = null;
			for (EntityType et: m.getEntityTypes()) {
				if (et.getTypeName().equals(r.getEntityType())) {
					entp = et;
					break;
				}
			}
			if (entp == null) {
				throw new InvalidOperationException("Invalid entity type: " + r.getEntityType());
			}
			
			if (entp.getAttrDefs().get(r.getAttr()) != null) {
				throw new InvalidOperationException("Attribute has already existed: " + r.getAttr());
			}
			
			AttributeType a = createAttribute(r);
			entp.getAttrDefs().put(r.getAttr(), a);
			
			DaoUtil.getModelDao().save(m);
			
			DefaultResponse rsp = createResponse(r);
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = createResponse(r);
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
		protected DefaultResponse createResponse(AddAttributeRequest r) {
			return new DefaultResponse(r);
		}

		protected AttributeType createAttribute(AddAttributeRequest r) {
			AttributeType a = new AttributeType();
			a.setCreator(r.getRequesterId());
			a.setTypeName(r.getType());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			return a;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		protected Long modelId;
		protected String entityType;
		protected String attr;
		protected String type;
		protected Boolean multiYes;
		protected Boolean allowDup;
		
		public DefaultResponse(AddAttributeRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setEntityType(r.getEntityType());
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
		
		public String getEntityType() {
			return entityType;
		}

		public void setEntityType(String entityType) {
			this.entityType = entityType;
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
