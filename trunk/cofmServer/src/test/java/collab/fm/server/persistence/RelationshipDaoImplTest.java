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

public class RelationshipDaoImplTest {
	
	static Logger logger = Logger.getLogger(RelationshipDaoImplTest.class);
	
	private static RelationshipDao rDao = DaoUtil.getRelationshipDao();
	private static FeatureDao fDao = DaoUtil.getFeatureDao();
	
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
		createFeature("Eclipse", 1L);
		createFeature("JBuilder", 3L);
		createFeature("JDK", 2L);
		createFeature("IDE", 9L);
	}
	
	private static void createFeature(String name, Long userid) {
		try {
			Feature f = new Feature();
			f.vote(true, userid);
			f.voteName(name, true, userid);
			featureIds.add(fDao.save(f).getId());
		} catch (Exception e) {
			logger.error("Couldn't create feature: name='" + name + "'", e);
		}
	}
	
	@Test
	public void testSaveBinaryRelationship() {
		try {
			BinaryRelationship br = new BinaryRelationship();
			br.setType(Resources.BIN_REL_EXCLUDES);
			br.setFeatures(fDao.getByName("Eclipse"), 
					fDao.getByName("JBuilder"));
			br.vote(true, 10L);
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
			BinaryRelationship br = new BinaryRelationship();
			br.setType(Resources.BIN_REL_REFINES);
			Long featureId1 = featureIds.get(0);
			Long featureId2 = featureIds.get(2);
			br.setFeatures(fDao.getById(featureId1, false), fDao.getById(featureId2, false));
			br.vote(true, 2L);
			rDao.save(br);
			
			BinaryRelationship example = new BinaryRelationship();
			example.setType(br.getType());
			example.setLeftFeatureId(br.getLeftFeatureId());
			example.setRightFeatureId(br.getRightFeatureId());
			
			assertTrue(rDao.getByExample(example).size()==1);
			
			BinaryRelationship another = new BinaryRelationship();
			another.setType(br.getType());
			Iterator<Feature> it = br.getFeatures().iterator();
			Feature left = it.next();
			Feature right = it.next();
			another.setFeatures(left, right);
			
			assertTrue(rDao.getByExample(another).size()==1);
			
			BinaryRelationship bad = new BinaryRelationship();
			bad.setType("Invalid_Type");
			bad.setFeatures(left, right);
			
			assertNull(rDao.getByExample(bad));
		} catch(Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
}
