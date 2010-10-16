package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.Feature;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class FeatureDaoImpl extends GenericDaoImpl<Feature, Long> implements FeatureDao  {

	static Logger logger = Logger.getLogger(FeatureDaoImpl.class);
	
	public Feature getByAttrValue(Long modelId, String attrName, String val)
	throws EntityPersistenceException, StaleDataException {
		List list = super.getByAttrValue(modelId, attrName, val, "Feature", false);
		if (list != null) {
			return (Feature) list.get(0);
		}
		return null;
	}
	
	public Feature getByName(Long modelId, String name) throws EntityPersistenceException, StaleDataException {
		return getByAttrValue(modelId, Resources.ATTR_FEATURE_NAME, name);
	}
	
	public List getBySimilarName(Long modelId, String name) throws EntityPersistenceException, StaleDataException {
		return super.getByAttrValue(modelId, Resources.ATTR_FEATURE_NAME, name, "Feature", true);
	}
	
	public List getAll(Long modelId) throws EntityPersistenceException,
			StaleDataException {
		return super.getAll(modelId, "model");
	}

}
