package collab.fm.server.util;

import collab.fm.server.persistence.DaoFactory;
import collab.fm.server.persistence.FeatureDao;
import collab.fm.server.persistence.ModelDao;
import collab.fm.server.persistence.RelationshipDao;
import collab.fm.server.persistence.UserDao;

public class DaoUtil {
	
	public static FeatureDao getFeatureDao() {
		return DaoFactory.getFactory().getFeatureDao();
	}
	
	public static RelationshipDao getRelationshipDao() {
		return DaoFactory.getFactory().getRelationshipDao();
	}
	
	public static UserDao getUserDao() {
		return DaoFactory.getFactory().getUserDao();
	}
	
	public static ModelDao getModelDao() {
		return DaoFactory.getFactory().getModelDao();
	}
}
