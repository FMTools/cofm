package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ModelDaoImpl extends GenericDaoImpl<Model, Long> implements
		ModelDao {

	public List getAll() throws EntityPersistenceException,
			StaleDataException {
		return super.getAll();
	}

	public List getAll(Long modelId) throws EntityPersistenceException,
			StaleDataException {
		// TODO Auto-generated method stub
		return getAll();
	}
	
	public List getBySimilarName(String name) throws EntityPersistenceException,
			StaleDataException {
		return super.getByAttrValue(null, Resources.ATTR_MODEL_NAME, name, "Model", true);
	}
	
	public Model getByName(String name) throws EntityPersistenceException, StaleDataException {
		return getByAttrValue(Resources.ATTR_MODEL_NAME, name);
	}

	public Model getByAttrValue(String attrName, String val)
			throws EntityPersistenceException, StaleDataException {
		// The "modelId" is ignored.
		List list = super.getByAttrValue(null, attrName, val, "Model", false);
		if (list != null) {
			return (Model) list.get(0);
		}
		return null;
	}

}
