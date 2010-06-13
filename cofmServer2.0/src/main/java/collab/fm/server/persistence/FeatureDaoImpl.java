package collab.fm.server.persistence;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class FeatureDaoImpl extends GenericDaoImpl<Feature, Long> implements FeatureDao  {

	static Logger logger = Logger.getLogger(FeatureDaoImpl.class);

	public Feature getByName(Long modelId, String name) throws EntityPersistenceException, StaleDataException {
		try {
			return (Feature) HibernateUtil.getCurrentSession()
				.createQuery("select feature " +
						"from Feature as feature " +
						"join feature.model as m " +
						"join feature.namesInternal as featureName " +
						"where m.id = :mId and featureName.name = :fname")
				.setLong("mId", modelId)
				.setString("fname", name)
				.uniqueResult();	
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new EntityPersistenceException("Query failed.", e);
		}
	}
	
	public List getBySimilarName(Long modelId, String name) throws EntityPersistenceException, StaleDataException {
		try {
			List result = HibernateUtil.getCurrentSession()
				.createQuery("select feature " +
						"from Feature as feature " +
						"join feature.model as m " +
						"join feature.namesInternal as featureName " +
						"where m.id = :mId and featureName.name like :fname")
				.setLong("mId", modelId)
				.setString("fname", "%" + name + "%")
				.list();	
			return result.size() > 0 ? result: null;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new EntityPersistenceException("Query failed.", e);
		}
	}
	
	public List getAll(Long modelId) throws EntityPersistenceException,
			StaleDataException {
		return super.getAll(modelId, "model");
	}
}
