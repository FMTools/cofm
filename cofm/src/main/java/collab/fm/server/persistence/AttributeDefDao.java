package collab.fm.server.persistence;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface AttributeDefDao extends GenericDao<AttributeType, Long> {
	public AttributeType getByAttrName(Long entityTypeId, String attrName) throws ItemPersistenceException, StaleDataException;
}
