package collab.fm.server.persistence;

import java.util.Collection;
import java.util.List;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public class FeatureDaoImpl extends GenericDaoImpl<Feature, Long> implements FeatureDao  {

	public List<Relationship> getInvolvedRelationships(Long id)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Feature> getAll() throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Feature> getByExample(Feature example, boolean like)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Feature getById(Long id) throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Long> saveAll(List<Feature> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(Feature entity) throws BeanPersistenceException {
		// TODO Auto-generated method stub

	}

	public void updateAll(Collection<Feature> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub

	}

}