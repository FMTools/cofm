package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class EntityDaoImpl extends GenericDaoImpl<Entity, Long> implements EntityDao  {

	static Logger logger = Logger.getLogger(EntityDaoImpl.class);
	
	public List<Entity> getByAttrValue(Long modelId, Long attrId, String val, boolean similar)
	throws ItemPersistenceException, StaleDataException {
		List list = super.getByAttrValue(modelId, attrId, val, similar);
		if (list != null) {
			return (List<Entity>) list;
		}
		return null;
	}
	
}
