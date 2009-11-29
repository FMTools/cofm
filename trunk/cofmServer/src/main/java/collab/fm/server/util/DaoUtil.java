package collab.fm.server.util;

import collab.fm.server.persistence.DaoFactory;
import collab.fm.server.persistence.FeatureDao;
import collab.fm.server.persistence.RelationshipDao;

public class DaoUtil {
	
	private static DaoFactory factory = DaoFactory.getFactory();
	
	public static FeatureDao getFeatureDao() {
		return factory.getFeatureDao();
	}
	
	public static RelationshipDao getRelationshipDao() {
		return factory.getRelationshipDao();
	}
	
}
