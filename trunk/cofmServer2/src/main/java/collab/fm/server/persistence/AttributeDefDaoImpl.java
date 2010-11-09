package collab.fm.server.persistence;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class AttributeDefDaoImpl extends GenericDaoImpl<AttributeType, Long>
		implements AttributeDefDao {

	public AttributeType getByAttrName(Long entityTypeId, String attrName)
			throws ItemPersistenceException, StaleDataException {
		try {
			return (AttributeType) HibernateUtil.getCurrentSession()
				.createQuery("select ad from AttributeType as ad " +
						"join ad.hostType as ht " +
						"where ht.id = :htId " +
						"and ad.attrName = :aName")
				.setLong("htId", entityTypeId)
				.setString("aName", attrName)
				.uniqueResult();
		
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new ItemPersistenceException("Query failed.", e);
		}
	}
}
