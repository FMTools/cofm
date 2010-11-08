package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.User;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface UserDao extends GenericDao<User, Long> {
	public User getByName(String name) throws ItemPersistenceException, StaleDataException;
	public User checkPasswordThenGet(User user) throws ItemPersistenceException, StaleDataException;
}
