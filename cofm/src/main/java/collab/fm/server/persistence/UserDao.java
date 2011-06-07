package collab.fm.server.persistence;

import collab.fm.server.bean.persist.User;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface UserDao extends GenericDao<User, Long> {
	public User getByName(String name) throws ItemPersistenceException, StaleDataException;
	public User getByEncryptName(String encryptName) throws ItemPersistenceException, StaleDataException;
	public User getByNameAndPwd(String name, String pwd) throws ItemPersistenceException, StaleDataException;
}
