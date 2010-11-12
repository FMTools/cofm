package collab.fm.server.bean.protocol.op;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.controller.JsonConverter;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.JsonConvertException;
import collab.fm.server.util.exception.StaleDataException;

@Ignore
public class VoteAddFeatureRequestTest {
	
	private static Logger logger = Logger.getLogger(VoteAddFeatureRequestTest.class);
	
	private static Long modelId;
//	@BeforeClass
//	public static void beginSession() throws ItemPersistenceException, StaleDataException {
//		HibernateUtil.getCurrentSession().beginTransaction();
//		Model m = new Model(1L);
//		m = DaoUtil.getModelDao().save(m);
//		modelId = m.getId();
//	}
//	
//	@AfterClass
//	public static void closeSession() {
//		HibernateUtil.getCurrentSession().getTransaction().commit();
//	}
//	
//	@Test
//	public void testGetFeatureByName() throws ItemPersistenceException, StaleDataException {
//		Model m = DaoUtil.getModelDao().getById(modelId, false);
//		
//		Feature f = new Feature(2L);
//		AttributeType fname = new AttributeType(2L, 
//				Resources.ATTR_ENTITY_NAME, AttributeType.TYPE_STR);
//		fname.setEnableGlobalDupValues(false);
//		fname.setMultipleSupport(true);
//		f.addAttribute(fname);
//		
//		f.voteOrAddValue(Resources.ATTR_ENTITY_NAME,
//				"Feature X", true, 2L);
//		f.voteOrAddValue(Resources.ATTR_ENTITY_NAME,
//				"特征X", true, 2L);
//		f.vote(true, 2L);
//		
//		m.addFeature(f);
//		
//		DaoUtil.getEntityDao().save(f);
//		DaoUtil.getModelDao().save(m);
//		
//		assertNotNull(DaoUtil.getEntityDao().getByName(modelId, "Feature X"));
//		assertNotNull(DaoUtil.getEntityDao().getByName(modelId, "特征X"));
//	}
//	

	@Test
	public void testCreateNewFeature() throws ItemPersistenceException, StaleDataException, InvalidOperationException, JsonConvertException {
		VoteAddEntityRequest vafr = new VoteAddEntityRequest();
		vafr.setName(Resources.REQ_VA_ENTITY);
		vafr.setId(100L);
		vafr.setRequesterId(2L);
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
