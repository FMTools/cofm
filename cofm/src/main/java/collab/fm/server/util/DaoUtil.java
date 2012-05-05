package collab.fm.server.util;

import collab.fm.server.persistence.AttributeDefDao;
import collab.fm.server.persistence.DaoFactory;
import collab.fm.server.persistence.ElementDao;
import collab.fm.server.persistence.ElementTypeDao;
import collab.fm.server.persistence.EntityDao;
import collab.fm.server.persistence.EntityTypeDao;
import collab.fm.server.persistence.ModelDao;
import collab.fm.server.persistence.RelationDao;
import collab.fm.server.persistence.UserDao;

public class DaoUtil {
	
	public static EntityDao getEntityDao() {
		return DaoFactory.getFactory().getEntityDao();
	}
	
	public static RelationDao getRelationDao() {
		return DaoFactory.getFactory().getRelationDao();
	}
	
	public static UserDao getUserDao() {
		return DaoFactory.getFactory().getUserDao();
	}
	
	public static ModelDao getModelDao() {
		return DaoFactory.getFactory().getModelDao();
	}
	
	public static EntityTypeDao getEntityTypeDao() {
		return DaoFactory.getFactory().getEntityTypeDao();
	}
	
	public static AttributeDefDao getAttributeDefDao() {
		return DaoFactory.getFactory().getAttributeDefDao();
	}
	
	public static ElementTypeDao getElementTypeDao() {
		return DaoFactory.getFactory().getElementTypeDao();
	}
	
	public static ElementDao getElementDao() {
		return DaoFactory.getFactory().getElementDao();
	}
}
