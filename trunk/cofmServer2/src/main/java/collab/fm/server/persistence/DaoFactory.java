package collab.fm.server.persistence;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.relation.BinRelationType;
import collab.fm.server.bean.persist.relation.RelationType;


public abstract class DaoFactory {
	
	// TODO: use a configuration file to decide which factory to create, if necessary.
	private static DaoFactory theFactory = new HibernateDaoFactory();
	
	public static DaoFactory getFactory() {
		return theFactory;
	}
	
	abstract public EntityDao getEntityDao();
	abstract public RelationDao getRelationDao();
	abstract public UserDao getUserDao();
	abstract public ModelDao getModelDao();
	abstract public EntityTypeDao getEntityTypeDao();
	abstract public RelationTypeDao getRelationTypeDao();
	abstract public AttributeDefDao getAttributeDefDao();
	abstract public ElementTypeDao getElementTypeDao();
	abstract public ElementDao getElementDao();
	
	// TODO: move the inner class to a separated file.
	public static class HibernateDaoFactory extends DaoFactory {
		
		private static final EntityDao f = new EntityDaoImpl();
		private static final RelationDao r = new RelationDaoImpl();
		private static final UserDao u = new UserDaoImpl();
		private static final ModelDao m = new ModelDaoImpl();
		
		private static final RelationTypeDao brt = new RelationTypeDaoImpl();
		private static final EntityTypeDao et = new EntityTypeDaoImpl();
		private static final AttributeDefDao ad = new AttributeDefDaoImpl();
		private static final ElementTypeDao elemt = new ElementTypeDaoImpl();
		private static final ElementDao elem = new ElementDaoImpl();
		
		public static class RelationTypeDaoImpl extends GenericDaoImpl<RelationType, Long>
			implements RelationTypeDao {
			
		}
		
		public static class EntityTypeDaoImpl extends GenericDaoImpl<EntityType, Long>
			implements EntityTypeDao {
			
		}
		
		public static class ElementTypeDaoImpl extends GenericDaoImpl<ElementType, Long>
			implements ElementTypeDao {
		
		}
		
		public static class ElementDaoImpl extends GenericDaoImpl<Element, Long>
			implements ElementDao {
			
		}
		
		@Override
		public EntityDao getEntityDao() {
			return f;
		}

		@Override
		public RelationDao getRelationDao() {
			return r;
		}

		@Override
		public UserDao getUserDao() {
			return u;
		}

		@Override
		public ModelDao getModelDao() {
			return m;
		}

		@Override
		public RelationTypeDao getRelationTypeDao() {
			return brt;
		}

		@Override
		public EntityTypeDao getEntityTypeDao() {
			return et;
		}

		@Override
		public AttributeDefDao getAttributeDefDao() {
			return ad;
		}

		@Override
		public ElementTypeDao getElementTypeDao() {
			return elemt;
		}

		@Override
		public ElementDao getElementDao() {
			return elem;
		}	
	}
}
