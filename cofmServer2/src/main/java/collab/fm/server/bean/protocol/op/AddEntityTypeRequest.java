package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class AddEntityTypeRequest extends Request {
	private Long modelId;
	private String typeName;
	private String superType;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new AddEntityTypeProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getSuperType() {
		return superType;
	}

	public void setSuperType(String superType) {
		this.superType = superType;
	}


	private static class AddEntityTypeProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof AddEntityTypeRequest)) return false;
			AddEntityTypeRequest r = (AddEntityTypeRequest) req;
			return r.getRequesterId() != null && r.getModelId() != null && r.getTypeName() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_entity_type operation.");
			}
			
			AddEntityTypeRequest r = (AddEntityTypeRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			EntityType sup = null;
			for (EntityType et: m.getEntityTypes()) {
				if (et.getTypeName().equals(r.getTypeName())) {
					throw new InvalidOperationException("Entity type has already existed: " + r.getTypeName());
				}
				if (et.getTypeName().equals(r.getSuperType())) {
					sup = et;
				}
			}
			
			EntityType entp = new EntityType();
			entp.setCreator(r.getRequesterId());
			entp.setLastModifier(r.getRequesterId());
			entp.setTypeName(r.getTypeName());
			entp.setSuperType(sup);
			
			m.addEntityType(entp);
			
			DaoUtil.getModelDao().save(m);
			
			DefaultResponse rsp = new DefaultResponse(r);
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = new DefaultResponse(r);
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		
		private Long modelId;
		private String typeName;
		private String superType;
		
		public DefaultResponse(AddEntityTypeRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setTypeName(r.getTypeName());
			this.setSuperType(r.getSuperType());
		}

		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getSuperType() {
			return superType;
		}

		public void setSuperType(String superType) {
			this.superType = superType;
		}
		
	}
}
