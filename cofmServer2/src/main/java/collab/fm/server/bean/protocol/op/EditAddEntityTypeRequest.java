package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.Model;
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

public class EditAddEntityTypeRequest extends Request {
	
	private Long typeId;
	
	private Long modelId;
	private String typeName;
	private Long superTypeId;
	
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

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getSuperTypeId() {
		return superTypeId;
	}

	public void setSuperTypeId(Long superTypeId) {
		this.superTypeId = superTypeId;
	}

	private static class AddEntityTypeProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof EditAddEntityTypeRequest)) return false;
			EditAddEntityTypeRequest r = (EditAddEntityTypeRequest) req;
			return r.getRequesterId() != null && r.getModelId() != null && r.getTypeName() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_entity_type operation.");
			}
			
			EditAddEntityTypeRequest r = (EditAddEntityTypeRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			EntityType entp = null;
			if (r.getTypeId() != null && 
					(entp = DaoUtil.getEntityTypeDao().getById(r.getTypeId(), false)) != null) {
				entp.setTypeName(r.getTypeName());
				entp.setLastModifier(r.getRequesterId());
				entp = DaoUtil.getEntityTypeDao().save(entp);
			} else {
				EntityType sup = null;
				for (EntityType et: m.getEntityTypes()) {
					if (et.getTypeName().equals(r.getTypeName())) {
						throw new InvalidOperationException("Entity type has already existed: " + r.getTypeName());
					}
					if (et.getId().equals(r.getSuperTypeId())) {
						sup = et;
					}
				}
				
				entp = new EntityType();
				DataItemUtil.setNewDataItemByUserId(entp, r.getRequesterId());
				
				entp.setTypeName(r.getTypeName());
				entp.setSuperType(sup);
				entp.setModel(m);
				m.addEntityType(entp);
				
				DaoUtil.getModelDao().save(m);
				entp = DaoUtil.getEntityTypeDao().save(entp);
				r.setTypeId(entp.getId());
			}
			
			DefaultResponse rsp = new DefaultResponse(r);
			rsp.setExecTime(DataItemUtil.formatDate(entp.getLastModifyTime()));
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = new DefaultResponse(r);
			rsp.setExecTime(DataItemUtil.formatDate(entp.getLastModifyTime()));
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		
		private Long typeId;
		
		private Long modelId;
		private String typeName;
		private Long superTypeId;
		
		public DefaultResponse(EditAddEntityTypeRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setTypeName(r.getTypeName());
			this.setTypeId(r.getTypeId());
			this.setSuperTypeId(r.getSuperTypeId());
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

		public Long getTypeId() {
			return typeId;
		}

		public void setTypeId(Long typeId) {
			this.typeId = typeId;
		}

		public Long getSuperTypeId() {
			return superTypeId;
		}

		public void setSuperTypeId(Long superTypeId) {
			this.superTypeId = superTypeId;
		}
		
	}
}
