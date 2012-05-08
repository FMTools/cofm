package quiz.server.dao;

import quiz.server.bean.User;

public interface UserDao extends GenericDao<User> {

	User getByNameAndVCode(String name, String vcode);

	User getByName(String name);
}
