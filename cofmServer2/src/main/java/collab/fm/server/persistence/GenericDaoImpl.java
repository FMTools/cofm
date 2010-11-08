package collab.fm.server.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Restrictions;

import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * Generic DAO Hibernate implementation.
 * @author Yi Li
 *
 * @param <ItemType>
 * @param <IdType>
 */
public abstract class GenericDaoImpl<ItemType, IdType extends Serializable> implements GenericDao<ItemType, IdType> {

	static Logger logger = Logger.getLogger(GenericDaoImpl.class);
	
	private Class<ItemType> itemClass;
	
	public GenericDaoImpl() {
		itemClass = (Class<ItemType>) 
			((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public Class<ItemType> getItemClass() {
		return itemClass;
	}
	
	// For Entities only
	@SuppressWarnings("unchecked")
	protected List getByAttrValue(Long modelId, String attrName, String val, boolean similar) 
	throws ItemPersistenceException, StaleDataException {
		String queryString = "select entity from Entity as entity " +
			"join entity.model as m " +     // m.class == Model
			"join entity.attrs as a " +     // a.class == ValueList
			"join a.values as v " +         // v.class == Value
			"where index(a) = '" + attrName + "' " +   
			"and m.id = :mId " +
			"and v.val " +
			(similar ? "like :val" : "= :val");
		Query qry = HibernateUtil.getCurrentSession().createQuery(queryString);
		qry = qry.setLong("mId", modelId)
				 .setString("val", (similar ? "%" + val + "%": val));
		
		try {
			List result = qry.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (Exception e) {
			logger.warn("Query failed.", e);
			throw new ItemPersistenceException("Query failed.", e);
		}
	}
	
	public List getAll() throws ItemPersistenceException ,StaleDataException {
		try {
			Criteria crit = HibernateUtil.getCurrentSession()
				.createCriteria(getItemClass());
			List result = crit.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't get all.", e);
			throw new ItemPersistenceException(e);
		}
	}
	
	/**
	 * Helper method for sub-DAOs' getAllOfModel method.
	 * @param modelId
	 * @param modelFieldName The name of field pointed at the Model in the Element 
	 * @return
	 * @throws ItemPersistenceException
	 * @throws StaleDataException
	 */
	protected List getAllOfModelByFieldName(IdType modelId, String modelFieldName) throws ItemPersistenceException, StaleDataException {
		try {
			Criteria crit = HibernateUtil.getCurrentSession()
				.createCriteria(getItemClass())
					.createCriteria(modelFieldName)
					.add(Restrictions.eq("id", modelId));
			List result = crit.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't get all.", e);
			throw new ItemPersistenceException(e);
		}
	}
	
	public ItemType getById(IdType id, boolean lock) throws ItemPersistenceException, StaleDataException {
		try {
			if (lock) {
				return (ItemType) HibernateUtil.getCurrentSession().get(getItemClass(), id, LockMode.UPGRADE);
			} else {
				return (ItemType) HibernateUtil.getCurrentSession().get(getItemClass(), id);
			}
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Get by ID failed. (ID=" + id + ")", e);
			throw new ItemPersistenceException(e);
		}
	}

	public ItemType save(ItemType item) throws ItemPersistenceException, StaleDataException {
		try {
			Session session = HibernateUtil.getCurrentSession();
						
			session.saveOrUpdate(item);
			
			return item;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Couldn't save entity", e);
			throw new ItemPersistenceException(e);
		}
	}
	
	public void deleteById(IdType id)
		throws ItemPersistenceException, StaleDataException {
		HibernateUtil.getCurrentSession().delete(this.getById(id, false));
	}
	
	public void delete(ItemType item) 
		throws ItemPersistenceException, StaleDataException{
		HibernateUtil.getCurrentSession().delete(item);
	}

}
