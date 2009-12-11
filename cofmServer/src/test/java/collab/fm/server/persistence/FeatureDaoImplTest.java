package collab.fm.server.persistence;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.junit.*;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.util.exception.BeanPersistenceException;
import static org.junit.Assert.*;

public class FeatureDaoImplTest {

	static Logger logger = Logger.getLogger(FeatureDaoImplTest.class);
	
	private static FeatureDao dao = new FeatureDaoImpl();
	
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	
	@Test
	public void testSave() {
		Feature feature = new Feature();
		feature.vote(true, 1L);
		feature.voteOptionality(false, 1L);
		feature.voteName("Dragon", true, 3L);
		feature.voteDescription("An award from XXX", true, 4L);
		feature.voteName("Dragon", false, 4L);
		try {
			feature = dao.save(feature);
			logger.debug("Feature = " + feature.toString());
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertEquals("Shouldn't reach here", "");
			
		}
	}
	
	@Test
	public void testSaveValueTwice() {
		Feature feature = new Feature();
		feature.vote(false, 2L);
		feature.vote(true, 1L);
		feature.voteOptionality(false, 11L);
		feature.voteName("Firefox", true, 333L);
		feature.voteDescription("A software", true, 14L);
		feature.voteName("Mozilla", true, 4L);
		feature.voteName("Firefox", false, 11L);
		try {
			feature = dao.save(feature);
			logger.debug("Feature = " + feature.toString());
			
			Feature feature2 = new Feature();
			feature2.setId(feature.getId());
			feature2 = dao.save(feature2);
			assertTrue(false);
		} catch (BeanPersistenceException e) {
			//logger.info("Couldn't save samething twice.");
			assertTrue(true);
			
		}
	}
	
	@Test
	public void testGetByIdAfterSave() {
		try {
			Feature feature = new Feature();
			feature.vote(true, 10L);
			feature.vote(true, 20L);
			feature.vote(false, 11L);
			
			feature = dao.save(feature);
			logger.debug("Feature = " + feature.toString());
			Feature feature2 = dao.getById(feature.getId(), false);
			logger.debug("Feature fetched");
			assertTrue(feature == feature2);
		} catch (BeanPersistenceException e) {
			logger.error("Get after save failed.", e);
			assertTrue(false);
			
		}
	}
	
	@Test
	public void testGetNullById() {
		try {
			assertNull(dao.getById(1000L, false));
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test 
	public void testGetByName() {
		try {
			Feature feature = new Feature();
			feature.voteName("QueryMe", true, 1L);
			feature = dao.save(feature);
			
			Feature another = new Feature();
			another.voteName("Another", true, 3L);
			dao.save(another);
			
			Feature me = dao.getByName("QueryMe");
			assertEquals(feature.getId(), me.getId());
		} catch (Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetNullByName() {
		try {
			assertNull(dao.getByName("IamNotHere"));
		} catch (Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
}
