package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.PersonalView;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.BinRelationType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.persist.relation.RelationType;
import collab.fm.server.bean.transfer.BinRelation2;
import collab.fm.server.bean.transfer.BinRelationType2;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.EntityType2;
import collab.fm.server.bean.transfer.PersonalView2;
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
			
			// Return all binary relationships
			List<BinRelation2> list2 = new ArrayList<BinRelation2>();
			for (Relation rel: m.getRelations()) {
				if (rel instanceof BinRelation) {
					BinRelation2 r2 = new BinRelation2();
					((BinRelation)rel).transfer(r2);
					list2.add(r2);
				}	
			}
			
			List<EntityType2> list3 = new ArrayList<EntityType2>();
			for (EntityType et: m.getEntityTypes()) {
				EntityType2 et2 = new EntityType2();
				et.transfer(et2);
				list3.add(et2);
			}
			
			List<BinRelationType2> list4 = new ArrayList<BinRelationType2>();
			for (RelationType rt: m.getRelationTypes()) {
				if (rt instanceof BinRelationType) {
					BinRelationType2 rt2 = new BinRelationType2();
					((BinRelationType)rt).transfer(rt2);
					list4.add(rt2);
				}
			}
			
			List<PersonalView2> list5 = new ArrayList<PersonalView2>();
			for (PersonalView pv: m.getViews()) {
				PersonalView2 basicInfo = new PersonalView2();
				pv.transfer(basicInfo);
				list5.add(basicInfo);
			}
			
			UpdateResponse response = new UpdateResponse(r);
			response.setEntities(list1);
			response.setBinaries(list2);
			response.setEntypes(list3);
			response.setBintypes(list4);
			response.setPvs(list5);
			response.setModelName(m.getName());
			response.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(response);
			
			return true;
		}
		
	}
	
	public static class UpdateResponse extends Response {

		private List<Entity2> entities;
		private List<BinRelation2> binaries;
		private List<EntityType2> entypes;
		private List<BinRelationType2> bintypes;
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

		public List<BinRelation2> getBinaries() {
			return binaries;
		}

		public void setBinaries(List<BinRelation2> binaries) {
			this.binaries = binaries;
		}

		public List<EntityType2> getEntypes() {
			return entypes;
		}

		public void setEntypes(List<EntityType2> entypes) {
			this.entypes = entypes;
		}

		public List<BinRelationType2> getBintypes() {
			return bintypes;
		}

		public void setBintypes(List<BinRelationType2> bintypes) {
			this.bintypes = bintypes;
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
		
	}

}
