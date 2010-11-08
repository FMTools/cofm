package collab.fm.server.persistence;


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
	
	// TODO: move the inner class to a separated file.
	public static class HibernateDaoFactory extends DaoFactory {
		
		private static final EntityDao f = new EntityDaoImpl();
		private static final RelationDao r = new RelationDaoImpl();
		private static final UserDao u = new UserDaoImpl();
		private static final ModelDao m = new ModelDaoImpl();
		
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
	}
}
