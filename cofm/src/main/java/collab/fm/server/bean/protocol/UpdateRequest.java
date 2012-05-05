package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.EntityType2;
import collab.fm.server.bean.transfer.PersonalView2;
import collab.fm.server.bean.transfer.Relation2;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class UpdateRequest extends Request {
	
	private Long modelId;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new UpdateProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	private static class UpdateProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof UpdateRequest)) return false;
			return ((UpdateRequest) req).getModelId() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid update operation.");
			}
			
			UpdateRequest r = (UpdateRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), true);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			List<Entity2> list1 = new ArrayList<Entity2>();
			for (Entity f: m.getEntities()) {
				Entity2 f2 = new Entity2();
				f.transfer(f2);
				list1.add(f2);
			}
			
			// Return all relationships
			List<Relation2> list2 = new ArrayList<Relation2>();
			for (Relation rel: m.getRelations()) {
				Relation2 r2 = new Relation2();
				rel.transfer(r2);
				list2.add(r2);
			}
			
			List<EntityType2> list3 = new ArrayList<EntityType2>();
			for (EntityType et: m.getEntityTypes()) {
				EntityType2 et2 = new EntityType2();
				et.transfer(et2);
				list3.add(et2);
			}
			
			
			UpdateResponse response = new UpdateResponse(r);
			response.setEntities(list1);
			response.setBinaries(list2);
			response.setEntypes(list3);
			response.setModelName(m.getName());
			response.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(response);
			
			return true;
		}
		
	}
	
	public static class UpdateResponse extends Response {

		private List<Entity2> entities;
		private List<Relation2> binaries;
		private List<EntityType2> entypes;
		private List<PersonalView2> pvs;  // Basic info of personal views (PVs) 
		private String modelName;
		
		public UpdateResponse(Request r) {
			super(r);
		}

		public List<Entity2> getEntities() {
			return entities;
		}

		public void setEntities(List<Entity2> entities) {
			this.entities = entities;
		}


		public List<EntityType2> getEntypes() {
			return entypes;
		}

		public void setEntypes(List<EntityType2> entypes) {
			this.entypes = entypes;
		}


		public void setModelName(String modelName) {
			this.modelName = modelName;
		}

		public String getModelName() {
			return modelName;
		}

		public void setPvs(List<PersonalView2> pvs) {
			this.pvs = pvs;
		}

		public List<PersonalView2> getPvs() {
			return pvs;
		}

		public List<Relation2> getBinaries() {
			return binaries;
		}

		public void setBinaries(List<Relation2> binaries) {
			this.binaries = binaries;
		}
		
	}

}
