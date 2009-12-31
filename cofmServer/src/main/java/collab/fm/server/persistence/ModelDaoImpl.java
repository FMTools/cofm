package collab.fm.server.persistence;

import java.util.List;

import org.hibernate.StaleObjectStateException;

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
	
	public List getBySimilarName(String name) throws BeanPersistenceException,
			StaleDataException {
		try {
			return HibernateUtil.getCurrentSession()
				.createQuery("select model " +
						"from Model as model " +
						"join model.namesInternal as mName " +
						"where mName.name like :theName")
				.setString("theName", "%" + name + "%")
				.list();	
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new BeanPersistenceException("Query failed.", e);
		}
		
	}
}
