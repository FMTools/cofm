package collab.fm.server.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.*;

import collab.fm.server.util.exception.BeanPersistenceException;

/**
 * Generic DAO Hibernate implementation.
 * @author Yi Li
 *
 * @param <EntityType>
 * @param <IdType>
 */
public abstract class GenericDaoImpl<EntityType, IdType> implements GenericDao<EntityType, IdType> {

	static Logger logger = Logger.getLogger(GenericDaoImpl.class);
	
	public abstract Class<EntityType> getEntityClass();
	
	public List<EntityType> getAll() throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<EntityType> getByExample(EntityType example, boolean like)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityType getById(IdType id) throws BeanPersistenceException {
		try {
			return (EntityType) HibernateUtil.getCurrentSession().get(getEntityClass(), (Serializable) id);
		} catch (RuntimeException e) {
			logger.info("Get by ID failed. (ID=" + id + ")", e);
			throw new BeanPersistenceException("Get by ID failed. (ID=" + id + ")", e);
		}
	}

	public IdType save(EntityType entity) throws BeanPersistenceException {
		try {
			Session session = HibernateUtil.getCurrentSession();
						
			IdType id = (IdType)session.save(entity);
			
			session.flush();
			return id;
		} catch (RuntimeException e) {
			logger.error("Couldn't save entity", e);
			throw new BeanPersistenceException("Couldn't save entity", e);
		}
	}

	public List<IdType> saveAll(List<EntityType> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(EntityType entity) throws BeanPersistenceException {
		// TODO Auto-generated method stub
		
	}

	public void updateAll(Collection<EntityType> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		
	}

}
