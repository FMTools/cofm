package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface EntityDao extends GenericDao<Entity, Long> {

	public Entity getByName(Long modelId, String name) throws ItemPersistenceException, StaleDataException;
	
	public Entity getByAttrValue(Long modelId, String attrName, String val) throws ItemPersistenceException, StaleDataException;
	
	public List getBySimilarName(Long modelId, String name) throws ItemPersistenceException, StaleDataException;
	
}
