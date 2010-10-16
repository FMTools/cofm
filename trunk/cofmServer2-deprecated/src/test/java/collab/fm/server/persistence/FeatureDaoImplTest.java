//package collab.fm.server.persistence;
//
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.hibernate.FlushMode;
//import org.junit.*;
//
//import collab.fm.server.bean.entity.FeatureOld;
//import collab.fm.server.bean.entity.Model;
//import collab.fm.server.util.DaoUtil;
//import collab.fm.server.util.exception.EntityPersistenceException;
//import collab.fm.server.util.exception.StaleDataException;
//import static org.junit.Assert.*;
//public class FeatureDaoImplTest {
//
//	static Logger logger = Logger.getLogger(FeatureDaoImplTest.class);
//	
//	private static FeatureDao dao = DaoUtil.getFeatureDao();
//	private static Model m;
//	@BeforeClass
//	public static void beginSession() {
//		HibernateUtil.getCurrentSession().beginTransaction();
//		m = new Model();
//		m.voteName("hahahaha domain", true, 9L);
//		try {
//			m = DaoUtil.getModelDao().save(m);
//		} catch (EntityPersistenceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@AfterClass
//	public static void closeSession() {
//		HibernateUtil.getCurrentSession().getTransaction().commit();
//	}
//	
//	@Test
//	public void testSaveChineseCharacters() {
//		FeatureOld feature = new FeatureOld();
//		feature.vote(true, 1L);
//		feature.voteOptionality(false, 1L);
//		feature.voteName("中文1", true, 3L, m.getId());
//		feature.voteDescription("汉字，，，，，，，", true, 4L, m.getId());
//		feature.voteName("中文1", false, 4L, m.getId());
//		m.addFeature(feature);
//		try {
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			logger.debug("Feature = " + feature.toString());
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertEquals("Shouldn't reach here", "");
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testSave() {
//		FeatureOld feature = new FeatureOld();
//		feature.vote(true, 1L);
//		feature.voteOptionality(false, 1L);
//		feature.voteName("Dragon", true, 3L, m.getId());
//		feature.voteDescription("An award from XXX", true, 4L, m.getId());
//		feature.voteName("Dragon", false, 4L, m.getId());
//		m.addFeature(feature);
//		try {
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			logger.debug("Feature = " + feature.toString());
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertEquals("Shouldn't reach here", "");
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test @Ignore
//	public void testSaveValueTwice() {
//		FeatureOld feature = new FeatureOld();
//		feature.vote(false, 2L);
//		feature.vote(true, 1L);
//		feature.voteOptionality(false, 11L);
//		feature.voteName("Firefox", true, 333L, m.getId());
//		feature.voteDescription("A software", true, 14L, m.getId());
//		feature.voteName("Mozilla", true, 4L, m.getId());
//		feature.voteName("Firefox", false, 11L, m.getId());
//		m.addFeature(feature);
//		try {
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			logger.debug("Feature = " + feature.toString());
//			
//			FeatureOld feature2 = new FeatureOld();
//			feature2.setId(feature.getId());
//			feature2 = dao.save(feature2);
//			assertTrue(false);
//		} catch (EntityPersistenceException e) {
//			//logger.info("Couldn't save samething twice.");
//			assertTrue(true);
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testGetByIdAfterSave() {
//		try {
//			FeatureOld feature = new FeatureOld();
//			feature.vote(true, 10L);
//			feature.vote(true, 20L);
//			feature.vote(false, 11L);
//			m.addFeature(feature);
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			logger.debug("Feature = " + feature.toString());
//			FeatureOld feature2 = dao.getById(feature.getId(), false);
//			logger.debug("Feature fetched");
//			assertTrue(feature == feature2);
//		} catch (EntityPersistenceException e) {
//			logger.error("Get after save failed.", e);
//			assertTrue(false);
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testGetNullById() {
//		try {
//			assertNull(dao.getById(1000L, false));
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertTrue(false);
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test 
//	public void testGetByName() {
//		try {
//			
//			FeatureOld feature = new FeatureOld();
//			feature.voteName("QueryMe", true, 1L, m.getId());
//			
//			
//			FeatureOld another = new FeatureOld();
//			another.voteName("Another", true, 3L, m.getId());
//			
//			m.addFeature(feature);
//			m.addFeature(another);
//			
//			dao.save(another);
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			FeatureOld me = dao.getByName(m.getId(), "QueryMe");
//			assertEquals(feature.getId(), me.getId());
//		} catch (Exception e) {
//			logger.error(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testGetBySimilarName() {
//		try {
//			
//			FeatureOld feature = new FeatureOld();
//			feature.voteName("MarkWilliams", true, 1L, m.getId());
//			
//			
//			FeatureOld another = new FeatureOld();
//			another.voteName("MarkAllen", true, 3L, m.getId());
//			
//			m.addFeature(feature);
//			m.addFeature(another);
//			
//			dao.save(another);
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			List<FeatureOld> me = dao.getBySimilarName(m.getId(), "Mark");
//			assertTrue(me.size()==2);
//		} catch (Exception e) {
//			logger.error(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testGetNullByName() {
//		try {
//			assertNull(dao.getByName(1L, "IamNotHere"));
//		} catch (Exception e) {
//			logger.error(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testDeleteFeature(){ 
//		FeatureOld feature = new FeatureOld();
//		feature.vote(true, 10L);
//		feature.voteOptionality(false, 10L);
//		feature.voteName("No-This-Name", true, 3L, m.getId());
//		feature.voteDescription("Very bad thing happens if you see this", true, 4L, m.getId());
//		m.addFeature(feature);
//		try {
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			dao.deleteById(feature.getId());
//			
//			assertNull(dao.getById(feature.getId(), false));
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertEquals("Shouldn't reach here", "");
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testDeleteFeatureNameByVoting(){ 
//		FeatureOld feature = new FeatureOld();
//		feature.vote(true, 10L);
//		feature.voteOptionality(false, 10L);
//		feature.voteName("No-This-Name-Please", true, 3L, m.getId());
//		feature.voteName("You-should-see-me", true, 3L, m.getId());
//		feature.voteDescription("You should see me as well!!", true, 4L, m.getId());
//		m.addFeature(feature);
//		try {
//			feature = dao.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			feature.voteName("No-This-Name-Please", false, 3L, m.getId());
//			dao.save(feature);
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertEquals("Shouldn't reach here", "");
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
