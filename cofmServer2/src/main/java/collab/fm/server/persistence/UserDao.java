package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface UserDao extends GenericDao<User, Long> {
	public User getByName(String name) throws EntityPersistenceException, StaleDataException;
	public User checkPasswordThenGet(User user) throws EntityPersistenceException, StaleDataException;
	public List getAll() throws EntityPersistenceException, StaleDataException;
}
