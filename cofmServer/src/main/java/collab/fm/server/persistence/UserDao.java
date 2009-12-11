package collab.fm.server.persistence;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.exception.BeanPersistenceException;

public interface UserDao extends GenericDao<User, Long> {
	public User getByName(String name) throws BeanPersistenceException;
}
