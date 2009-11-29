package collab.fm.server.persistence;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.util.exception.BeanPersistenceException;
import static org.junit.Assert.*;

public class FeatureDaoImplTest {

	static Logger logger = Logger.getLogger(FeatureDaoImplTest.class);
	
	private static FeatureDao dao = new FeatureDaoImpl();
	
	@BeforeClass
	public static void openSession() {
		HibernateUtil.openSession();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.closeCurrentSession();
	}
	
	
	@Test
	public void testSave() {
		Feature feature = new Feature();
		feature.voteExistence(true, 1L);
		try {
			Long id = dao.save(feature);
			logger.info("Feature inserted, ID = " + id);
		} catch (BeanPersistenceException e) {
			assertTrue(false);
			logger.error(e);
		}
	}
}
