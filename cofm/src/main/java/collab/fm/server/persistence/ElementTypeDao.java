package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface ElementTypeDao extends GenericDao<EntityType, Long> {
}
