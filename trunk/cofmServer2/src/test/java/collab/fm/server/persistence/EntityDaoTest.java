package collab.fm.server.persistence;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.junit.*;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.persist.entity.NumericAttributeType;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.*;
import static org.junit.Assert.*;

public class EntityDaoTest {

	static Logger logger = Logger.getLogger(EntityDaoTest.class);
	
	private static ModelDao md = DaoUtil.getModelDao();
	private static EntityDao ed = DaoUtil.getEntityDao();
	private static EntityTypeDao td = DaoUtil.getEntityTypeDao();
	private static AttributeDefDao ad = DaoUtil.getAttributeDefDao();
	
	private static Model m;
	private static EntityType type1;
	private static EntityType type2;
	
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
		
		m = new Model();
		m.setCreator(1L);
		m.setName("EntityDaoTest Model");
		
		try {
			
			m = md.save(m);
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	@Test
	public void testSaveAndGetByValue() {
		try {
			
		type1 = new EntityType();
		type1.setTypeName("TYPE_ONE");
		type1.setCreator(1L);
			
		AttributeType a1 = new AttributeType();
		a1.setCreator(10L);
		a1.setEnableGlobalDupValues(false);
		a1.setMultipleSupport(true);
		a1.setTypeName(AttributeType.TYPE_STR);
		a1 = ad.save(a1);
		
		NumericAttributeType a2 = new NumericAttributeType();
		a2.setCreator(2L);
		a2.setEnableGlobalDupValues(false);
		a2.setMultipleSupport(true);
		a2.setMax(99.99f);
		a2.setMin(1.1f);
		a2.setUnit("RMB");
		a2 = (NumericAttributeType) ad.save(a2);
		
		EnumAttributeType a3 = new EnumAttributeType();
		a3.setCreator(3L);
		a3.setEnableGlobalDupValues(true);
		a3.setMultipleSupport(true);
		a3.setValidValues(Arrays.asList(new String[] { "High", "Medium", "Low" }));
		a3 = (EnumAttributeType) ad.save(a3);
		
		type1.getAttrDefs().add(a1);
		type1.getAttrDefs().add(a2);
		type1.getAttrDefs().add(a3);
		
		m.addEntityType(type1);
		
		m = md.save(m);
		type1 = td.save(type1);
		
		Entity e = new Entity();
		e.setCreator(1L);
		e.vote(true, 1L);
		e.setType(type1);
		
		Value v1 = new Value();
		v1.setCreator(1L);
		v1.setVal("中文名字");
		e.voteOrAddValue(a1.getId(), v1, true, 1L);
		
		Value v2 = new Value();
		v2.setCreator(2L);
		v2.setVal(Float.valueOf(33.33f).toString());
		e.voteOrAddValue(a2.getId(), v2, true, 2L);
		
		Value v3 = new Value();
		v3.setCreator(3L);
		v3.setVal("High");
		e.voteOrAddValue(a3.getId(), v3, true, 3L);
		
		m.addEntity(e);
		
			// ---------- Save ---------
			e = ed.save(e);
			md.save(m);
			assertNotNull(e.getId());
			
			//---------- Get by value ------------
			Entity e2 = ed.getByAttrValue(m.getId(), a1.getId(), "中文名字", false).get(0);
			assertNotNull(e2);
			assertEquals(e2.getId(), e.getId());
		} catch (ItemPersistenceException ex) {
			logger.error(ex);
			assertEquals("Shouldn't reach here", "");
			
		} catch (StaleDataException ex) {
			logger.error(ex);
			assertEquals("Shouldn't reach here", "");
		}
		
		
		
	}
	
//	@Test
//	public void testGetByIdAfterSave() {
//		try {
//			FeatureOld feature = new FeatureOld();
//			feature.vote(true, 10L);
//			feature.vote(true, 20L);
//			feature.vote(false, 11L);
//			m.addFeature(feature);
//			feature = ed.save(feature);
//			DaoUtil.getModelDao().save(m);
//			logger.debug("Feature = " + feature.toString());
//			FeatureOld feature2 = ed.getById(feature.getId(), false);
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
//			assertNull(ed.getById(1000L, false));
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
//			ed.save(another);
//			feature = ed.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			FeatureOld me = ed.getByName(m.getId(), "QueryMe");
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
//			ed.save(another);
//			feature = ed.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			List<FeatureOld> me = ed.getBySimilarName(m.getId(), "Mark");
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
//			assertNull(ed.getByName(1L, "IamNotHere"));
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
//			feature = ed.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			ed.deleteById(feature.getId());
//			
//			assertNull(ed.getById(feature.getId(), false));
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
//			feature = ed.save(feature);
//			DaoUtil.getModelDao().save(m);
//			
//			feature.voteName("No-This-Name-Please", false, 3L, m.getId());
//			ed.save(feature);
//		} catch (EntityPersistenceException e) {
//			logger.error(e);
//			assertEquals("Shouldn't reach here", "");
//			
//		} catch (StaleDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
