package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ModelDaoImpl extends GenericDaoImpl<Model, Long> implements
		ModelDao {

	public List getAll() throws BeanPersistenceException,
			StaleDataException {
		return super.getAll();
	}

	public List getAll(Long modelId) throws BeanPersistenceException,
			StaleDataException {
		// TODO Auto-generated method stub
		return getAll();
	}
}
