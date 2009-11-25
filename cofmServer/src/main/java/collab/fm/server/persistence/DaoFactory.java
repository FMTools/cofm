package collab.fm.server.persistence;

public abstract class DaoFactory {
	
	public static DaoFactory getFactory() {
		return null;
	}
	
	public FeatureDao getFeatureDao() {
		return null;
		
	}
	
	public RelationshipDao getRelationshipDao() {
		return null;
		
	}
	
	public UserDao getUserDao() {
		return null;
		
	}
}
