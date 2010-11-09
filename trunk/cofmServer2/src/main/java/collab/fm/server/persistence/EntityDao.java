package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface EntityDao extends GenericDao<Entity, Long> {

	public List<Entity> getByAttrValue(Long modelId, Long attrId, String val, boolean similar) throws ItemPersistenceException, StaleDataException;
	
}
