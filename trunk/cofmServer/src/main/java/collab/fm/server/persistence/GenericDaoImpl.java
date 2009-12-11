package collab.fm.server.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Example;

import collab.fm.server.util.exception.BeanPersistenceException;

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
	
	public List getAll() throws BeanPersistenceException {
		Criteria crit = HibernateUtil.getCurrentSession().createCriteria(getEntityClass());
		List result = crit.list();
		return result.isEmpty() ? null : result;
	}
	
	public List getByExample(EntityType example, String... excludeProperties) throws BeanPersistenceException {
		Criteria crit = HibernateUtil.getCurrentSession().createCriteria(getEntityClass());
		Example ex = Example.create(example);
		for (String property: excludeProperties) {
			ex.excludeProperty(property);
		}
		crit.add(ex);
		List result = crit.list();
		return result.isEmpty() ? null : result;
	}
	
	public EntityType getById(IdType id, boolean lock) throws BeanPersistenceException {
		try {
			if (lock) {
				return (EntityType) HibernateUtil.getCurrentSession().get(getEntityClass(), id, LockMode.UPGRADE);
			} else {
				return (EntityType) HibernateUtil.getCurrentSession().get(getEntityClass(), id);
			}
		} catch (RuntimeException e) {
			logger.info("Get by ID failed. (ID=" + id + ")", e);
			throw new BeanPersistenceException("Get by ID failed. (ID=" + id + ")", e);
		}
	}

	public EntityType save(EntityType entity) throws BeanPersistenceException {
		try {
			Session session = HibernateUtil.getCurrentSession();
						
			session.saveOrUpdate(entity);
			
			return entity;
		} catch (RuntimeException e) {
			logger.error("Couldn't save entity", e);
			throw new BeanPersistenceException("Couldn't save entity", e);
		}
	}

	public List<EntityType> saveAll(List<EntityType> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

}
