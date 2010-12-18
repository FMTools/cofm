package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.StaleObjectStateException;

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

	public List<Entity> getAllByTypeId(Long modelId, Long typeId)
			throws ItemPersistenceException, StaleDataException {
		try {
			List result = HibernateUtil.getCurrentSession().createQuery(
					"select entity from Entity as entity" +
					"join entity.model as m" +
					"join entity.type as t" +
					"where m.id = :mId and t.id = :tId ")
					.setLong("mId", modelId)
					.setLong("tId", typeId)
					.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new ItemPersistenceException("Query failed.", e);
		}
	}
	
}
