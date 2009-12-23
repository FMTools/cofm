package collab.fm.server.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * Generic DAO Hibernate implementation.
 * @author Yi Li
 *
 * @param <EntityType>
 * @param <IdType>
 */
public abstract class GenericDaoImpl<EntityType, IdType extends Serializable> implements GenericDao<EntityType, IdType> {

	static Logger logger = Logger.getLogger(GenericDaoImpl.class);
	
	private Class<EntityType> entityClass;
	
	public GenericDaoImpl() {
		entityClass = (Class<EntityType>) 
			((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public Class<EntityType> getEntityClass() {
		return entityClass;
	}
	
	protected List getAll() throws BeanPersistenceException ,StaleDataException {
		try {
			Criteria crit = HibernateUtil.getCurrentSession()
				.createCriteria(getEntityClass());
			List result = crit.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't get all.", e);
			throw new BeanPersistenceException(e);
		}
	}
	
	protected List getAll(IdType modelId, String modelPropertyName) throws BeanPersistenceException, StaleDataException {
		try {
			Criteria crit = HibernateUtil.getCurrentSession()
				.createCriteria(getEntityClass())
					.createCriteria(modelPropertyName)
					.add(Restrictions.eq("id", modelId));
			List result = crit.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't get all.", e);
			throw new BeanPersistenceException(e);
		}
	}
	
	public EntityType getById(IdType id, boolean lock) throws BeanPersistenceException, StaleDataException {
		try {
			if (lock) {
				return (EntityType) HibernateUtil.getCurrentSession().get(getEntityClass(), id, LockMode.UPGRADE);
			} else {
				return (EntityType) HibernateUtil.getCurrentSession().get(getEntityClass(), id);
			}
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Get by ID failed. (ID=" + id + ")", e);
			throw new BeanPersistenceException(e);
		}
	}

	public EntityType save(EntityType entity) throws BeanPersistenceException, StaleDataException {
		try {
			Session session = HibernateUtil.getCurrentSession();
						
			session.saveOrUpdate(entity);
			
			return entity;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't save entity", e);
			throw new BeanPersistenceException(e);
		}
	}

	public List<EntityType> saveAll(List<EntityType> entities)
			throws BeanPersistenceException, StaleDataException {
		// TODO Auto-generated method stub
		return null;
	}

}
