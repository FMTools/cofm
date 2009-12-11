package collab.fm.server.persistence;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.exception.BeanPersistenceException;

public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	public User getByName(String name) throws BeanPersistenceException {
		try {
			return (User) HibernateUtil.getCurrentSession()
				.createQuery("from User as user " +
						"where user.name = :uName")
				.setString("uName", name)
				.uniqueResult();
		} catch(Exception e) {
			logger.error("Couldn't get by name.", e);
			return null;
		}
	}
}
