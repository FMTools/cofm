package collab.fm.server.bean.protocol.op;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.persist.Feature;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Attribute;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.controller.JsonConverter;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.JsonConvertException;
import collab.fm.server.util.exception.StaleDataException;

public class VoteAddFeatureRequestTest {
	
	private static Logger logger = Logger.getLogger(VoteAddFeatureRequestTest.class);
	
	private static Long modelId;
	@BeforeClass
	public static void beginSession() throws EntityPersistenceException, StaleDataException {
		HibernateUtil.getCurrentSession().beginTransaction();
		Model m = new Model(1L);
		m = DaoUtil.getModelDao().save(m);
		modelId = m.getId();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	@Test
	public void testGetFeatureByName() throws EntityPersistenceException, StaleDataException {
		Model m = DaoUtil.getModelDao().getById(modelId, false);
		
		Feature f = new Feature(2L);
		Attribute fname = new Attribute(2L, 
				Resources.ATTR_FEATURE_NAME, Attribute.TYPE_STR);
		fname.setEnableGlobalDupValues(false);
		fname.setMultipleSupport(true);
		f.addAttribute(fname);
		
		f.voteOrAddValue(Resources.ATTR_FEATURE_NAME,
				"Feature X", true, 2L);
		f.voteOrAddValue(Resources.ATTR_FEATURE_NAME,
				"特征X", true, 2L);
		f.vote(true, 2L);
		
		m.addFeature(f);
		
		DaoUtil.getFeatureDao().save(f);
		DaoUtil.getModelDao().save(m);
		
		assertNotNull(DaoUtil.getFeatureDao().getByName(modelId, "Feature X"));
		assertNotNull(DaoUtil.getFeatureDao().getByName(modelId, "特征X"));
	}
	

	@Test
	public void testCreateNewFeature() throws EntityPersistenceException, StaleDataException, InvalidOperationException, JsonConvertException {
		VoteAddFeatureRequest vafr = new VoteAddFeatureRequest();
		vafr.setName(Resources.REQ_VA_FEATURE);
		vafr.setId(100L);
		vafr.setRequesterId(2L);
		vafr.setFeatureName("New feature X");
		vafr.setModelId(modelId);
		
		ResponseGroup rg = new ResponseGroup();
		vafr.process(rg);
		logger.info(JsonConverter.responseToJson(rg.getBack()));
		logger.info(JsonConverter.responseToJson(rg.getBroadcast()));
	}
	
	@Test
	public void testVoteYesOnFeature() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testDeleteFeature() {
		//fail("Not yet implemented");
	}

}
