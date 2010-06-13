package collab.fm.server.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.entity.*;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
public class RelationshipDaoImplTest {
	
	static Logger logger = Logger.getLogger(RelationshipDaoImplTest.class);
	
	private static RelationshipDao rDao = DaoUtil.getRelationshipDao();
	private static FeatureDao fDao = DaoUtil.getFeatureDao();
	
	private static Long modelId;
	private static List<Long> featureIds = new ArrayList<Long>();
	
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
		prepareFeatures();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	private static void prepareFeatures() {
		Model m = new Model();
		m.voteName("IIIIIIIIIIIIIIIII", true, 1L);
		try {
			m = DaoUtil.getModelDao().save(m);
			modelId = m.getId();
		} catch (EntityPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createFeature(m, "Eclipse", 1L);
		createFeature(m, "JBuilder", 3L);
		createFeature(m, "JDK", 2L);
		createFeature(m, "IDE", 9L);
		
		
	}
	
	private static void createFeature(Model m, String name, Long userid) {
		try {
			Feature f = new Feature();
			f.vote(true, userid);
			f.voteName(name, true, userid, -1L);
			f.setModel(m);
			featureIds.add(fDao.save(f).getId());
		} catch (Exception e) {
			logger.error("Couldn't create feature: name='" + name + "'", e);
		}
	}
	
	@Test
	public void testSaveBinaryRelationship() {
		try {
			Model m = DaoUtil.getModelDao().getById(modelId, false);
			
			BinaryRelationship br = new BinaryRelationship();
			br.setType(Resources.BIN_REL_EXCLUDES);
			br.setFeatures(fDao.getByName(modelId, "Eclipse"), 
					fDao.getByName(modelId, "JBuilder"));
			br.vote(true, 10L);
			br.setModel(m);
			DaoUtil.getModelDao().save(m);
			rDao.save(br);
			
			for (Feature f: br.getFeatures()) {
				assertTrue(f.getRelationships().size()==1);
				for (Relationship r: f.getRelationships()) {
					assertEquals(br.getType(), r.getType());
				}
			}
		} catch(Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetBinaryRelationshipByExample() {
		try {
			Model m = DaoUtil.getModelDao().getById(modelId, false);
			BinaryRelationship br = new BinaryRelationship();
			br.setType(Resources.BIN_REL_REFINES);
			Long featureId1 = featureIds.get(0);
			Long featureId2 = featureIds.get(2);
			br.setFeatures(fDao.getById(featureId1, false), fDao.getById(featureId2, false));
			br.vote(true, 2L);
			br.setModel(m);
			DaoUtil.getModelDao().save(m);
			rDao.save(br);
			
			BinaryRelationship example = new BinaryRelationship();
			example.setType(br.getType());
			example.setLeftFeatureId(br.getLeftFeatureId());
			example.setRightFeatureId(br.getRightFeatureId());
			
			assertTrue(rDao.getByExample(modelId, example).size()==1);
			
			BinaryRelationship bad = new BinaryRelationship();
			bad.setType("Invalid_Type");
			bad.setLeftFeatureId(br.getLeftFeatureId());
			bad.setRightFeatureId(br.getRightFeatureId());
			
			assertNull(rDao.getByExample(modelId, bad));
		} catch(Exception e) {
			logger.error("Couldn't get by example.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteRelationship() {
		try {
			Model m = DaoUtil.getModelDao().getById(modelId, false);
			BinaryRelationship br = new BinaryRelationship();
			br.setType(Resources.BIN_REL_REQUIRES);
			Long featureId1 = featureIds.get(0);
			Long featureId2 = featureIds.get(2);
			br.setFeatures(fDao.getById(featureId1, false), fDao.getById(featureId2, false));
			br.vote(true, 2L);
			br.setModel(m);
			DaoUtil.getModelDao().save(m);
			
			Relationship r = rDao.save(br);
			
			// Select r
			assertTrue(rDao.getById(r.getId(), false).getId().equals(r.getId()));
			// Delete r
			rDao.deleteById(r.getId());
			
			assertTrue(rDao.getById(r.getId(), false)==null);
			// Select again
			//assertNull(rDao.getById(r.getId(), false));
			
		} catch(Exception e) {
			logger.error("Couldn't delete.", e);
			assertTrue(false);
		}
	}
}
