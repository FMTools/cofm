package collab.fm.server.persistence;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public class FeatureDaoImpl extends GenericDaoImpl<Feature, Long> implements FeatureDao  {

	static Logger logger = Logger.getLogger(FeatureDaoImpl.class);

	public Feature getByName(String name) throws BeanPersistenceException {
		try {
			return (Feature) HibernateUtil.getCurrentSession()
				.createQuery("select feature " +
						"from Feature as feature " +
						"join feature.namesInternal as featureName " +
						"where featureName.name = :fname")
				.setString("fname", name)
				.uniqueResult();	
		} catch (Exception e) {
			logger.error("Query failed.", e);
			throw new BeanPersistenceException("Query failed.", e);
		}
	}

}
