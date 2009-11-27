package collab.fm.server.persistence;

public class DaoUtils {
	
	private static DaoFactory factory = DaoFactory.getFactory();
	
	public static FeatureDao getFeatureDao() {
		return factory.getFeatureDao();
	}
	
	public static RelationshipDao getRelationshipDao() {
		return factory.getRelationshipDao();
	}
}
