package collab.fm.server.persistence;

import org.hibernate.Query;
import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.User;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.util.Pair;
import edu.stanford.nlp.util.StringUtils;

public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	protected User getByAttrValuePairs(Pair<String, String>[] items) throws ItemPersistenceException, StaleDataException {
		try {
			String[] conds = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				conds[i] =  "user." + items[i].first + " = :" + items[i].first + "Val"; 
			}
			Query query = HibernateUtil.getCurrentSession()
				.createQuery("from User as user where " + StringUtils.join(conds, " and "));
			for (int i = 0; i < items.length; i++) {
				query.setString(items[i].first + "Val", items[i].second);
			}
			return (User) query.uniqueResult();
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch(RuntimeException e) {
			logger.warn("Couldn't get user.", e);
			throw new ItemPersistenceException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User getByName(String name) throws ItemPersistenceException, StaleDataException {
		return getByAttrValuePairs(new Pair[] {
			Pair.make("name", name)
		});
	}

	@SuppressWarnings("unchecked")
	public User getByEncryptName(String encryptName)
		throws ItemPersistenceException, StaleDataException {
		return getByAttrValuePairs(new Pair[] {
			Pair.make("nameInMD5", encryptName)
		});
	}
	
	@SuppressWarnings("unchecked")
	public User getByNameAndPwd(String name, String pwd)
		throws ItemPersistenceException, StaleDataException {
		return getByAttrValuePairs(new Pair[] {
			Pair.make("name", name),
			Pair.make("passwordInMD5", pwd)
		});
	}

//	public List getAllOfModel(Long modelId) throws ItemPersistenceException,
//			StaleDataException {
//		try {
//			List result = HibernateUtil.getCurrentSession()
//				.createQuery("select user from User as user " +
//						"left join user.models as model " +
//						"where model.id = :mId")
//				.setLong("mId", modelId)
//				.list();
//			return result.size() > 0 ? result: null;
//		} catch (StaleObjectStateException sose) {
//			logger.warn("Stale data detected. Force client to retry.", sose);
//			throw new StaleDataException(sose);
//		} catch (Exception e) {
//			logger.warn("Query failed.", e);
//			throw new ItemPersistenceException("Query failed.", e);
//		}
//	}
}
