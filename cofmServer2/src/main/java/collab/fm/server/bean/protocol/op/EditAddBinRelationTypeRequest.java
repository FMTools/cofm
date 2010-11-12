package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.relation.BinRelationType;
import collab.fm.server.bean.persist.relation.RelationType;
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

// NOTE: Only the name of the type can be edited. 
public class EditAddBinRelationTypeRequest extends Request {
	
	// If relId == null, it's an adding operation; otherwise it's an editing operation
	protected Long relId;
	
	protected Long modelId;

	protected Long sourceId;
	protected Long targetId;
	protected String typeName;
	protected boolean hierarchical;
	protected boolean directed;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new EditAddBinRelationTypeProcessor();
	}
	
	public Long getRelId() {
		return relId;
	}

	public void setRelId(Long relId) {
		this.relId = relId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
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

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isHierarchical() {
		return hierarchical;
	}

	public void setHierarchical(boolean hierarchical) {
		this.hierarchical = hierarchical;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}


	private static class EditAddBinRelationTypeProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof EditAddBinRelationTypeRequest)) return false;
			return true;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid edit_add_bin_relation_type operation.");
			}
			
			EditAddBinRelationTypeRequest r = (EditAddBinRelationTypeRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			BinRelationType brt = null;
			if (r.getRelId() != null &&
					(brt = (BinRelationType) DaoUtil.getRelationTypeDao().getById(r.getId(), false)) != null) {
				// An editing operation
				brt.setTypeName(r.getTypeName());
				brt.setLastModifier(r.getRequesterId());
				DaoUtil.getRelationTypeDao().save(brt);
			} else {
				// An adding operation
				for (RelationType rt: m.getRelationTypes()) {
					if (rt.getTypeName().equals(r.getTypeName())) {
						throw new InvalidOperationException("Relation type has already existed: " + rt.getTypeName());
					}
				}
				
				ElementType st = DaoUtil.getElementTypeDao().getById(r.getSourceId(), false);
				if (st == null) {
					throw new InvalidOperationException("Invalid source type ID: " + r.getSourceId());
				}
				ElementType tt = DaoUtil.getElementTypeDao().getById(r.getTargetId(), false);
				if (tt == null) {
					throw new InvalidOperationException("Invalid target type ID: " + r.getTargetId());
				}
		
				brt = new BinRelationType();
				DataItemUtil.setNewDataItemByUserId(brt, r.getRequesterId());
				
				brt.setDirected(r.isDirected());
				brt.setHierarchical(r.isHierarchical());
				brt.setModel(m);
				brt.setSourceType(st);
				brt.setTargetType(tt);
				brt.setTypeName(r.getTypeName());
				
				m.addRelationType(brt);
				
				brt = (BinRelationType) DaoUtil.getRelationTypeDao().save(brt);
				DaoUtil.getModelDao().save(m);
				
				r.setRelId(brt.getId());
			}
			
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
		protected Long relId;
		
		protected Long modelId;
		
		protected Long sourceId;
		protected Long targetId;
		protected String typeName;
		protected boolean hierarchical;
		protected boolean directed;
		
		public DefaultResponse(EditAddBinRelationTypeRequest r) {
			super(r);
			this.setRelId(r.getRelId());
			this.setModelId(r.getModelId());
			this.setSourceId(r.getSourceId());
			this.setTargetId(r.getTargetId());
			this.setTypeName(r.getTypeName());
			this.setHierarchical(r.isHierarchical());
			this.setDirected(r.isDirected());
		}
		
		public Long getRelId() {
			return relId;
		}
		public void setRelId(Long relId) {
			this.relId = relId;
		}
		public Long getModelId() {
			return modelId;
		}
		public void setModelId(Long modelId) {
			this.modelId = modelId;
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
		public String getTypeName() {
			return typeName;
		}
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		public boolean isHierarchical() {
			return hierarchical;
		}
		public void setHierarchical(boolean hierarchical) {
			this.hierarchical = hierarchical;
		}
		public boolean isDirected() {
			return directed;
		}
		public void setDirected(boolean directed) {
			this.directed = directed;
		}
		
	}
}
