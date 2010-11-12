package collab.fm.server.persistence;

import java.util.List;

import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ModelDaoImpl extends GenericDaoImpl<Model, Long> implements
		ModelDao {

	public List getAllOfModel(Long modelId) throws ItemPersistenceException,
			StaleDataException {
		throw new UnsupportedOperationException("Use getAll() instead.");
	}
	
	public List getBySimilarName(String name) throws ItemPersistenceException,
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
			throw new ItemPersistenceException("Query failed.", e);
		}
	}
	
	public Model getByName(String name) throws ItemPersistenceException, StaleDataException {
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
			throw new ItemPersistenceException("Query failed.", e);
		}
	}


}
