package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class EntityDaoImpl extends GenericDaoImpl<Entity, Long> implements EntityDao  {

	static Logger logger = Logger.getLogger(EntityDaoImpl.class);
	
	public Entity getByAttrValue(Long modelId, String attrName, String val)
	throws ItemPersistenceException, StaleDataException {
		List list = super.getByAttrValue(modelId, attrName, val, false);
		if (list != null) {
			return (Entity) list.get(0);
		}
		return null;
	}
	
	public Entity getByName(Long modelId, String name) throws ItemPersistenceException, StaleDataException {
		return getByAttrValue(modelId, Resources.ATTR_ENTITY_NAME, name);
	}
	
	public List getBySimilarName(Long modelId, String name) throws ItemPersistenceException, StaleDataException {
		return super.getByAttrValue(modelId, Resources.ATTR_ENTITY_NAME, name, true);
	}
	
	public List getAllOfModel(Long modelId) throws ItemPersistenceException,
			StaleDataException {
		return super.getAllOfModelByFieldName(modelId, "model");
	}

}
