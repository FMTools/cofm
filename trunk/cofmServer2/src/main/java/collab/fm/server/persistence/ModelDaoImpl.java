package collab.fm.server.persistence;

import java.util.List;

import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.Model;
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
		try {
			List result = HibernateUtil.getCurrentSession().createQuery(
					"select m from Model as m " +
					"where m.name like :val")
					.setString("val", "%" + name + "%")
					.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new EntityPersistenceException("Query failed.", e);
		}
	}
	
	public Model getByName(String name) throws EntityPersistenceException, StaleDataException {
		try {
			return (Model) HibernateUtil.getCurrentSession().createQuery(
					"select m from Model as m " +
					"where m.name = :val")
					.setString("val", name)
					.uniqueResult();
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new EntityPersistenceException("Query failed.", e);
		}
	}


}
