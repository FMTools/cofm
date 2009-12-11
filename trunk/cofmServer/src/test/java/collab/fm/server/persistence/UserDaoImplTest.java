package collab.fm.server.persistence;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.DaoUtil;

public class UserDaoImplTest {
	static Logger logger = Logger.getLogger(UserDaoImplTest.class);
	
	private static UserDao dao = DaoUtil.getUserDao();
	
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
		prepareUsers();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	private static void prepareUsers() {
		saveUser("lao yi", "12324");
		saveUser("hoho", "ddd");
		saveUser("hehe", "00000");
	}
	
	private static void saveUser(String name, String pwd) {
		try {
			User u = new User();
			u.setName(name);
			u.setPassword(pwd);
			dao.save(u);
		} catch (Exception e) {
			logger.error("Couldn't save user.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetAll() {
		try {
			assertTrue(dao.getAll().size()==3);
		} catch (Exception e) {
			logger.error("Couldn't get all.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetByName() {
		try {
			assertNotNull(dao.getByName("lao yi"));
		} catch (Exception e) {
			logger.error("Couldn't get by name", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetNullByName() {
		try {
			assertNull(dao.getByName("mei you zhe ge ming zi"));
		} catch (Exception e) {
			logger.error("Couldn't get by name.", e);
			assertTrue(false);
		}
	}
}
