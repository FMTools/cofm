package collab.fm.server.persistence;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.User;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
@Ignore
public class UserDaoImplTest {
	static Logger logger = Logger.getLogger(UserDaoImplTest.class);
	
	private static UserDao dao = DaoUtil.getUserDao();
	private static Long mId;
	
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
		Model m = new Model();
		//m.voteName("my last model", true, 4L);
		
		try {
			m = DaoUtil.getModelDao().save(m);
			mId = m.getId();
			saveUser(m, "lao yi", "12324");
			saveUser(m, "hoho", "ddd");
			saveUser(m, "hehe", "00000");
			DaoUtil.getModelDao().save(m);
		} catch (EntityPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void saveUser(Model m, String name, String pwd) {
		try {
			User u = new User();
			u.setName(name);
			u.setPassword(pwd);
			u.addModel(m);
			dao.save(u);
		} catch (Exception e) {
			logger.error("Couldn't save user.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetAll() {
		try {
			assertTrue(dao.getAll().size()>=3);
			assertTrue(dao.getAll(mId).size()==3);
			List<User> u = dao.getAll(mId);
			for (User user: u) {
				logger.info(user.getId());
			}
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
