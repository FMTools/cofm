package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public interface FeatureDao extends GenericDao<Feature, Long> {

	public Feature getByName(String name) throws BeanPersistenceException;
	
}
