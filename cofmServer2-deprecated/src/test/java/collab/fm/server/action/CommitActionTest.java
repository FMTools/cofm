//package collab.fm.server.action;
//
//import static org.junit.Assert.*;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import collab.fm.server.bean.entity.*;
//import collab.fm.server.bean.operation.*;
//import collab.fm.server.bean.protocol.*;
//import collab.fm.server.persistence.HibernateUtil;
//import collab.fm.server.util.DaoUtil;
//import collab.fm.server.util.ProtocolUtil;
//import collab.fm.server.util.Resources;
//import collab.fm.server.util.exception.JsonConvertException;
//
//public class CommitActionTest {
//	
//	static Logger logger = Logger.getLogger(CommitActionTest.class);
//
//	private static Action action = new CommitAction();
//	
//	@BeforeClass
//	public static void beginSession() {
//		HibernateUtil.getCurrentSession().beginTransaction();
//	}
//	
//	@AfterClass
//	public static void closeSession() {
//		HibernateUtil.getCurrentSession().getTransaction().commit();
//	}
//		
//	private static void showResponse(Response rsp) throws JsonConvertException {
//		logger.info(ProtocolUtil.ResponseToJson(rsp));
//	}
//	
//	@Test
//	public void testCreateNewFeature() {
//		try {
//		// Create a Model
//		Model m = new Model();
//		m.voteName("THE PROBLEM DOMAIN", true, 1L);
//		m = DaoUtil.getModelDao().save(m);
//		
//		FeatureOperation op = new FeatureOperation();
//		op.setName(Resources.OP_CREATE_FEATURE);
//		op.setUserid(100L);
//		op.setVote(true);
//		op.setValue("I_am_a_new_feature");
//		op.setModelId(m.getId());
//
//		CommitRequest req = new CommitRequest();
//		req.setId(1L);
//		req.setName(Resources.REQ_COMMIT);
//		req.setRequesterId(op.getUserid());
//		req.setModelId(op.getModelId());
//		req.setOperation(op);
//		
//		ResponseGroup rg = new ResponseGroup();
//	
//		
//			action.execute(req, rg);
//			assertNotNull(rg.getBack());
//			assertTrue(rg.getBack() instanceof CommitResponse);
//			CommitResponse cr = (CommitResponse)rg.getBack();
//			assertNotNull(cr.getOperations());
//			
//			logger.info("------------- CREATE NEW FEATURE -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testCreateTwoFeatures() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("THE XXXXX DOMAIN", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			
//			FeatureOperation op = new FeatureOperation();
//			op.setName(Resources.OP_CREATE_FEATURE);
//			op.setUserid(100L);
//			op.setVote(true);
//			op.setValue("I_am_a_new_feature");
//			op.setModelId(m.getId());
//
//			CommitRequest req = new CommitRequest();
//			req.setId(1L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(op.getModelId());
//			req.setOperation(op);
//			
//			FeatureOperation op2 = new FeatureOperation();
//			op2.setName(Resources.OP_CREATE_FEATURE);
//			op2.setUserid(100L);
//			op2.setVote(true);
//			op2.setValue("I_am_another_new_feature");
//			op2.setModelId(m.getId());
//
//			CommitRequest req2 = new CommitRequest();
//			req2.setId(1L);
//			req2.setName(Resources.REQ_COMMIT);
//			req2.setRequesterId(op2.getUserid());
//			req2.setModelId(op2.getModelId());
//			req2.setOperation(op2);
//			
//			List<CommitRequest> reqs = new ArrayList<CommitRequest>();
//			reqs.add(req);
//			reqs.add(req2);
//			
//			for (CommitRequest r: reqs) {
//				ResponseGroup rg = new ResponseGroup();
//		
//				action.execute(r, rg);
//				assertNotNull(rg.getBack());
//				assertTrue(rg.getBack() instanceof CommitResponse);
//				CommitResponse cr = (CommitResponse)rg.getBack();
//				assertNotNull(cr.getOperations());
//				
//				logger.info("------------- CREATE NEW FEATURE -------------");
//				showResponse(rg.getBack());
//				showResponse(rg.getBroadcast());
//			}
//			} catch (Exception e) {
//				logger.error("Couldn't execute.", e);
//				assertTrue(false);
//			}
//	}
//	
//	@Test
//	public void testVoteNoToFeatureWithoutRelationship() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("THE ULTIMATE DOMAIN", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			
//			// Create a feature
//			FeatureOld f = new FeatureOld();
//			f.vote(true, 222L);
//			f.voteName("I_am_a_GOOD_man", true, 222L, m.getId());
//			f.setModel(m);	
//			f = DaoUtil.getFeatureDao().save(f);
//			
//			
//			// Make and handle a request
//			FeatureOperation op = new FeatureOperation();
//			op.setName(Resources.OP_CREATE_FEATURE);
//			op.setUserid(999L);
//			op.setVote(false);
//			op.setFeatureId(f.getId());
//			op.setModelId(m.getId());
//
//			CommitRequest req = new CommitRequest();
//			req.setId(4L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(op.getModelId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- VOTE NO TO FEATURE -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//			
//			// Query and show
//			FeatureOld me = DaoUtil.getFeatureDao().getByName(op.getModelId(), "I_am_a_GOOD_man");
//			logger.info(me.toString());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testVoteNoToFeatureWithInvolvedRelationship() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("My First Domain", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			// Create 2 features and 1 binary relationship
//			FeatureOld f1 = new FeatureOld();
//			FeatureOld f2 = new FeatureOld();
//			f1.vote(true, 11111L);
//			f2.vote(true, 2222L);
//			f1.voteName("OHOHOHiamleft", true, 11111L, m.getId());
//			f2.voteName("HOHOHOiamright", true, 2222L, m.getId());
//			f1.setModel(m);
//			f2.setModel(m);
//			f1 = DaoUtil.getFeatureDao().save(f1);
//			f2 = DaoUtil.getFeatureDao().save(f2);
//			
//			BinaryRelationship r = new BinaryRelationship();
//			r.setType(Resources.BIN_REL_EXCLUDES);
//			r.vote(true, 43L);
//			r.setFeatures(f1, f2);
//			r.setModel(m);
//			
//			
//			r = (BinaryRelationship)DaoUtil.getRelationshipDao().save(r);
//			
//			
//			// Make and handle a request
//			FeatureOperation op = new FeatureOperation();
//			op.setName(Resources.OP_CREATE_FEATURE);
//			op.setUserid(999L);
//			op.setVote(false);
//			op.setFeatureId(f1.getId());
//			op.setModelId(m.getId());
//
//			CommitRequest req = new CommitRequest();
//			req.setId(4L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(op.getModelId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- VOTE NO TO FEATURE (WITH RELATIONSHIP INVOLVED)-------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//			
//			// Query and show
//			logger.info(DaoUtil.getRelationshipDao().getById(r.getId(), false));
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testAddNewName() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("Good Domain", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			// Create a feature
//			FeatureOld f = new FeatureOld();
//			f.vote(true, 222L);
//			f.voteName("My1stName", true, 222L, m.getId());
//			f.setModel(m);
//			
//			f = DaoUtil.getFeatureDao().save(f);
//			
//			
//			// Make and handle a request
//			FeatureOperation op = new FeatureOperation();
//			op.setName(Resources.OP_ADD_NAME);
//			op.setUserid(100L);
//			op.setVote(true);
//			op.setValue("My2ndName");
//			op.setFeatureId(f.getId());
//			op.setModelId(m.getId());
//
//			CommitRequest req = new CommitRequest();
//			req.setId(3L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(100L);
//			req.setModelId(m.getId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- ADD NAME -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//			
//			// Query and compare
//			FeatureOld f1 = DaoUtil.getFeatureDao().getByName(m.getId(), "My1stName");
//			FeatureOld f2 = DaoUtil.getFeatureDao().getByName(m.getId(), "My2ndName");
//			assertTrue(f1==f2);
//			logger.info(f1.toString());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testVoteNoToName() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("Bad Domain", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			// Create a feature
//			FeatureOld f = new FeatureOld();
//			f.vote(true, 222L);
//			f.voteName("testVoteNoToName", true, 222L, m.getId());
//			f.setModel(m);
//			
//			f = DaoUtil.getFeatureDao().save(f);
//			
//			
//			// Make and handle a request
//			FeatureOperation op = new FeatureOperation();
//			op.setName(Resources.OP_ADD_NAME);
//			op.setUserid(100L);
//			op.setVote(false);
//			op.setValue("testVoteNoToName");
//			op.setFeatureId(f.getId());
//			op.setModelId(m.getId());
//
//			CommitRequest req = new CommitRequest();
//			req.setId(3L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(op.getModelId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- VOTE NO TO NAME -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//			
//			// Query and compare
//			FeatureOld f1 = DaoUtil.getFeatureDao().getByName(m.getId(), "testVoteNoToName");
//			logger.info(f1.toString());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testCreateBinaryRelationship() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("XXXXXXX Domain", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			// Insert 2 features
//			FeatureOld f1 = new FeatureOld();
//			FeatureOld f2 = new FeatureOld();
//			f1.vote(true, 11111L);
//			f2.vote(true, 2222L);
//			f1.voteName("ThisIsFirstOnehaha", true, 11111L, m.getId());
//			f2.voteName("opopopopopop", true, 2222L, m.getId());
//			f1.setModel(m);
//			f2.setModel(m);
//						
//			f1 = DaoUtil.getFeatureDao().save(f1);
//			f2 = DaoUtil.getFeatureDao().save(f2);
//			
//			
//			// Make and handle request
//			BinaryRelationshipOperation op = new BinaryRelationshipOperation();
//			op.setLeftFeatureId(f1.getId());
//			op.setRightFeatureId(f2.getId());
//			op.setName(Resources.OP_CREATE_RELATIONSHIP);
//			op.setType(Resources.BIN_REL_REFINES);
//			op.setVote(true);
//			op.setUserid(1212L);
//			op.setModelId(m.getId());
//			
//			CommitRequest req = new CommitRequest();
//			req.setId(444L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(m.getId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- CREATE NEW BINARY RELATIONSHIP -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//			
//			// Query and show
//			BinaryRelationship rel = new BinaryRelationship();
//			rel.setType(op.getType());
//			rel.setFeatures(f1, f2);
//			assertTrue(DaoUtil.getRelationshipDao().getByExample(m.getId(), rel).size() == 1);
//			logger.info(((Relationship)DaoUtil.getRelationshipDao().getByExample(m.getId(), rel).get(0)).getFeatures());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testVoteNoToBinaryRelationship() {
//		try {
//			// Create a Model
//			Model m = new Model();
//			m.voteName("lllllllll Domain", true, 1L);
//			m = DaoUtil.getModelDao().save(m);
//			// Create 2 features and 1 binary relationship
//			FeatureOld f1 = new FeatureOld();
//			FeatureOld f2 = new FeatureOld();
//			f1.vote(true, 11111L);
//			f2.vote(true, 2222L);
//			f1.voteName("iamleft", true, 11111L, m.getId());
//			f2.voteName("iamright", true, 2222L, m.getId());
//			f1.setModel(m);
//			f2.setModel(m);
//			f1 = DaoUtil.getFeatureDao().save(f1);
//			f2 = DaoUtil.getFeatureDao().save(f2);
//			
//			BinaryRelationship r = new BinaryRelationship();
//			r.setType(Resources.BIN_REL_EXCLUDES);
//			r.vote(true, 999L);
//			r.setFeatures(f1, f2);
//			r.setModel(m);
//			
//			
//			r = (BinaryRelationship)DaoUtil.getRelationshipDao().save(r);
//			
//			
//			// Make and handle request
//			BinaryRelationshipOperation op = new BinaryRelationshipOperation();
//			op.setRightFeatureId(f2.getId());
//			op.setName(Resources.OP_CREATE_RELATIONSHIP);
//			//op.setType(Resources.BIN_REL_REFINES);
//			op.setVote(false);
//			op.setUserid(1212L);
//			op.setRelationshipId(r.getId());
//			op.setModelId(m.getId());
//			
//			CommitRequest req = new CommitRequest();
//			req.setId(444L);
//			req.setName(Resources.REQ_COMMIT);
//			req.setRequesterId(op.getUserid());
//			req.setModelId(op.getModelId());
//			req.setOperation(op);
//			
//			ResponseGroup rg = new ResponseGroup();
//			action.execute(req, rg);
//			
//			logger.info("------------- VOTE NO TO BINARY RELATIONSHIP -------------");
//			showResponse(rg.getBack());
//			showResponse(rg.getBroadcast());
//		} catch (Exception e) {
//			logger.error("Couldn't execute.", e);
//			assertTrue(false);
//		}
//	}
//}
