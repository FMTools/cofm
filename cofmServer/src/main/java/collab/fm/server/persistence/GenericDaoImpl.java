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
 * @param <BeanType>
 * @param <IdType>
 */
public class GenericDaoImpl<BeanType, IdType> implements GenericDao<BeanType, IdType> {

	static Logger logger = Logger.getLogger(GenericDaoImpl.class);
	
	public List<BeanType> getAll() throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BeanType> getByExample(BeanType example, boolean like)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public BeanType getById(IdType id) throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public IdType save(BeanType entity) throws BeanPersistenceException {
		try {
		Session session = HibernateUtil.getCurrentSession();
		session.beginTransaction();
		
		IdType id = (IdType)session.save(entity);
		
		session.getTransaction().commit();
		return id;
		} catch (RuntimeException ex) {
			try {
				HibernateUtil.getCurrentSession().getTransaction().rollback();
			} catch (RuntimeException rbEx) {
				logger.error("Couldn't roll back transaction.", rbEx);
			}
			logger.error("Couldn't save entity.", ex);
			throw new BeanPersistenceException("Couldn't save entity.", ex);
		}
	}

	public List<IdType> saveAll(List<BeanType> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(BeanType entity) throws BeanPersistenceException {
		// TODO Auto-generated method stub
		
	}

	public void updateAll(Collection<BeanType> entities)
			throws BeanPersistenceException {
		// TODO Auto-generated method stub
		
	}

}
