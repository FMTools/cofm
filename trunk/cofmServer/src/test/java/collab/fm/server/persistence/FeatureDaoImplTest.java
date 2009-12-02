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
		HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
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
		try {
			Long id = dao.save(feature);
			logger.debug("Feature inserted, ID = " + id);
		} catch (BeanPersistenceException e) {
			assertEquals("Shouldn't reach here", "");
			logger.error(e);
		}
	}
	
	@Test
	public void testSaveValueTwice() {
		Feature feature = new Feature();
		feature.vote(false, 2L);
		try {
			Long id = dao.save(feature);
			logger.debug("Feature inserted, ID = " + id);
			
			Feature feature2 = new Feature();
			feature2.setId(id);
			feature2.vote(false, 2L);
			Long id2 = dao.save(feature2);
			logger.debug("Feature inserted again, ID = " + id2);
			assertEquals(id2, new Long(id+1));
		} catch (BeanPersistenceException e) {
			assertTrue(false);
			logger.info("Couldn't save samething twice.", e);
		}
	}
	
	@Test
	public void testGetAfterSave() {
		try {
			Feature feature = new Feature();
			feature.vote(true, 10L);
			feature.vote(true, 20L);
			feature.vote(false, 11L);
			
			Long id = dao.save(feature);
			logger.debug("Feature inserted, ID = " + id);
			Feature feature2 = dao.getById(id);
			logger.debug("Feature fetched");
			assertTrue(feature == feature2);
			logger.debug("Feature = " + feature2.toString());
		} catch (BeanPersistenceException e) {
			assertTrue(false);
			logger.error("Get after save failed.", e);
		}
	}
	
	@Test
	public void testGetInexistedThing() {
		
	}
}
