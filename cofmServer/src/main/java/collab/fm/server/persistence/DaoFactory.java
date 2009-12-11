package collab.fm.server.persistence;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.exception.BeanPersistenceException;

public abstract class DaoFactory {
	
	// TODO: use a configuration file to decide which factory to create, if necessary.
	private static DaoFactory theFactory = new HibernateDaoFactory();
	
	public static DaoFactory getFactory() {
		return theFactory;
	}
	
	abstract public FeatureDao getFeatureDao();
	abstract public RelationshipDao getRelationshipDao();
	abstract public UserDao getUserDao();
	
	// TODO: move the inner class to a separated file.
	public static class HibernateDaoFactory extends DaoFactory {
		
		private static final FeatureDao f = new FeatureDaoImpl();
		private static final RelationshipDao r = new RelationshipDaoImpl();
		private static final UserDao u = new UserDaoImpl();
		
		@Override
		public FeatureDao getFeatureDao() {
			return f;
		}

		@Override
		public RelationshipDao getRelationshipDao() {
			return r;
		}

		@Override
		public UserDao getUserDao() {
			return u;
		}
		
	}
}
