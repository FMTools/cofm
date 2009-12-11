package collab.fm.server.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.util.DaoUtil;

public class RelationshipDaoImplTest {
	
	static Logger logger = Logger.getLogger(RelationshipDaoImplTest.class);
	
	private static RelationshipDao dao = new RelationshipDaoImpl();
	
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
			featureIds.add(DaoUtil.getFeatureDao().save(f).getId());
		} catch (Exception e) {
			logger.error("Couldn't create feature: name='" + name + "'");
		}
	}
	
	@Test
	public void testSaveBinaryRelationship() {
		
	}
	
	@Test
	public void testFeatureRelationshipAssociationIsOK() {
		
	}
}
