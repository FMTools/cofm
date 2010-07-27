package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class AddAttributeRequest extends Request {
	protected Long modelId;
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
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_attribute operatin");
			}
			AddAttributeRequest r = (AddAttributeRequest) req;
			DefaultResponse rsp = createResponse(r);
			
			Attribute a = createAttribute(r);
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}

			m.addAttributeToFeatures(a);
			DaoUtil.getModelDao().save(m);
			
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

		protected Attribute createAttribute(AddAttributeRequest r) {
			Attribute a = new Attribute(r.getRequesterId(), r.getAttr(), r.getType());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			return a;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		protected Long modelId;
		protected String attr;
		protected String type;
		protected Boolean multiYes;
		protected Boolean allowDup;
		
		public DefaultResponse(AddAttributeRequest r) {
			super(r);
			this.setModelId(r.getModelId());
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
