package quiz.server.dao;

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


public abstract class GenericDaoImpl<ItemType> implements GenericDao<ItemType> {

	
	private Class<ItemType> itemClass;
	
	public GenericDaoImpl() {
		itemClass = (Class<ItemType>) 
			((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public Class<ItemType> getItemClass() {
		return itemClass;
	}
	
		
	public List getAll() {
		try {
			Criteria crit = HibernateUtil.getCurrentSession()
				.createCriteria(getItemClass());
			List result = crit.list();
			return result.isEmpty() ? null : result;
		} catch (StaleObjectStateException sose) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}
	public ItemType getById(int id, boolean lock) {
		try {
			if (lock) {
				return (ItemType) HibernateUtil.getCurrentSession().get(getItemClass(), id, LockMode.UPGRADE);
			} else {
				return (ItemType) HibernateUtil.getCurrentSession().get(getItemClass(), id);
			}
		} catch (StaleObjectStateException sose) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public ItemType save(ItemType item) {
		try {
			Session session = HibernateUtil.getCurrentSession();
						
			session.saveOrUpdate(item);
			
			return item;
		} catch (StaleObjectStateException sose) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}
	
	public void remove(ItemType item) {
		HibernateUtil.getCurrentSession().delete(item);
	}

}
