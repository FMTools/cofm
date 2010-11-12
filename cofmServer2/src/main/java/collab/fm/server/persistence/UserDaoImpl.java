package collab.fm.server.persistence;

import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.User;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	public User getByName(String name) throws ItemPersistenceException, StaleDataException {
		try {
			return (User) HibernateUtil.getCurrentSession()
				.createQuery("from User as user " +
						"where user.name = :uName")
				.setString("uName", name)
				.uniqueResult();
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch(RuntimeException e) {
			logger.warn("Couldn't get by name.", e);
			throw new ItemPersistenceException(e);
		}
	}

	public User checkPasswordThenGet(User user) throws ItemPersistenceException, StaleDataException {
		try {
			return (User) HibernateUtil.getCurrentSession()
				.createQuery("from User as user " +
						"where user.name = :uName " +
						"and user.password = :uPwd")
				.setString("uName", user.getName())
				.setString("uPwd", user.getPassword())
				.uniqueResult();
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch(RuntimeException e) {
			logger.warn("Couldn't get by name.", e);
			throw new ItemPersistenceException(e);
		}
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
