package collab.fm.server.action;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import collab.fm.server.bean.entity.*;
import collab.fm.server.bean.operation.*;
import collab.fm.server.bean.protocol.*;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.ProtocolUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.ProtocolInterpretException;

public class CommitActionTest {
	
	static Logger logger = Logger.getLogger(CommitActionTest.class);

	private static Action action = new CommitAction();
	
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
		
	private static void showResponse(Response rsp) throws ProtocolInterpretException {
		logger.info(ProtocolUtil.ResponseToJson(rsp));
	}
	
	@Test
	public void testCreateNewFeature() {
		FeatureOperation op = new FeatureOperation();
		op.setName(Resources.OP_CREATE_FEATURE);
		op.setUserid(100L);
		op.setVote(true);
		op.setValue("I_am_a_new_feature");

		CommitRequest req = new CommitRequest();
		req.setId(1L);
		req.setName(Resources.REQ_COMMIT);
		req.setRequesterId(100L);
		req.setOperation(op);
		
		ResponseGroup rg = new ResponseGroup();
	
		try {
			action.execute(req, rg);
			assertNotNull(rg.getBack());
			assertTrue(rg.getBack() instanceof CommitResponse);
			CommitResponse cr = (CommitResponse)rg.getBack();
			assertNotNull(cr.getOperations());
			
			logger.info("------------- CREATE NEW FEATURE -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testVoteNoToFeatureWithoutRelationship() {
		try {
			// Create a feature
			Feature f = new Feature();
			f.vote(true, 222L);
			f.voteName("I_am_a_GOOD_man", true, 222L);
			f = DaoUtil.getFeatureDao().save(f);
			
			// Make and handle a request
			FeatureOperation op = new FeatureOperation();
			op.setName(Resources.OP_CREATE_FEATURE);
			op.setUserid(999L);
			op.setVote(false);
			op.setFeatureId(f.getId());

			CommitRequest req = new CommitRequest();
			req.setId(4L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(op.getUserid());
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- VOTE NO TO FEATURE -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
			
			// Query and show
			Feature me = DaoUtil.getFeatureDao().getByName("I_am_a_GOOD_man");
			logger.info(me.toString());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testVoteNoToFeatureWithInvolvedRelationship() {
		try {
			// Create 2 features and 1 binary relationship
			Feature f1 = new Feature();
			Feature f2 = new Feature();
			f1.vote(true, 11111L);
			f2.vote(true, 2222L);
			f1.voteName("OHOHOHiamleft", true, 11111L);
			f2.voteName("HOHOHOiamright", true, 2222L);
			
			f1 = DaoUtil.getFeatureDao().save(f1);
			f2 = DaoUtil.getFeatureDao().save(f2);
			
			BinaryRelationship r = new BinaryRelationship();
			r.setFeatures(f1, f2);
			r.setType(Resources.BIN_REL_EXCLUDES);
			r.vote(true, 43L);
			
			r = (BinaryRelationship)DaoUtil.getRelationshipDao().save(r);
			
			// Make and handle a request
			FeatureOperation op = new FeatureOperation();
			op.setName(Resources.OP_CREATE_FEATURE);
			op.setUserid(999L);
			op.setVote(false);
			op.setFeatureId(f1.getId());

			CommitRequest req = new CommitRequest();
			req.setId(4L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(op.getUserid());
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- VOTE NO TO FEATURE (WITH RELATIONSHIP INVOLVED)-------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
			
			// Query and show
			logger.info(DaoUtil.getRelationshipDao().getById(r.getId(), false));
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testAddNewName() {
		try {
			// Create a feature
			Feature f = new Feature();
			f.vote(true, 222L);
			f.voteName("My1stName", true, 222L);
			f = DaoUtil.getFeatureDao().save(f);
			
			// Make and handle a request
			FeatureOperation op = new FeatureOperation();
			op.setName(Resources.OP_ADD_NAME);
			op.setUserid(100L);
			op.setVote(true);
			op.setValue("My2ndName");
			op.setFeatureId(f.getId());

			CommitRequest req = new CommitRequest();
			req.setId(3L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(100L);
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- ADD NAME -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
			
			// Query and compare
			Feature f1 = DaoUtil.getFeatureDao().getByName("My1stName");
			Feature f2 = DaoUtil.getFeatureDao().getByName("My2ndName");
			assertTrue(f1==f2);
			logger.info(f1.toString());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testVoteNoToName() {
		try {
			// Create a feature
			Feature f = new Feature();
			f.vote(true, 222L);
			f.voteName("testVoteNoToName", true, 222L);
			f = DaoUtil.getFeatureDao().save(f);
			
			// Make and handle a request
			FeatureOperation op = new FeatureOperation();
			op.setName(Resources.OP_ADD_NAME);
			op.setUserid(100L);
			op.setVote(false);
			op.setValue("testVoteNoToName");
			op.setFeatureId(f.getId());

			CommitRequest req = new CommitRequest();
			req.setId(3L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(op.getUserid());
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- VOTE NO TO NAME -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
			
			// Query and compare
			Feature f1 = DaoUtil.getFeatureDao().getByName("testVoteNoToName");
			logger.info(f1.toString());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testCreateBinaryRelationship() {
		try {
			// Insert 2 features
			Feature f1 = new Feature();
			Feature f2 = new Feature();
			f1.vote(true, 11111L);
			f2.vote(true, 2222L);
			f1.voteName("ThisIsFirstOnehaha", true, 11111L);
			f2.voteName("opopopopopop", true, 2222L);
			
			f1 = DaoUtil.getFeatureDao().save(f1);
			f2 = DaoUtil.getFeatureDao().save(f2);
			
			// Make and handle request
			BinaryRelationshipOperation op = new BinaryRelationshipOperation();
			op.setLeftFeatureId(f1.getId());
			op.setRightFeatureId(f2.getId());
			op.setName(Resources.OP_CREATE_RELATIONSHIP);
			op.setType(Resources.BIN_REL_REFINES);
			op.setVote(true);
			op.setUserid(1212L);
			
			CommitRequest req = new CommitRequest();
			req.setId(444L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(op.getUserid());
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- CREATE NEW BINARY RELATIONSHIP -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
			
			// Query and show
			BinaryRelationship rel = new BinaryRelationship();
			rel.setFeatures(f1, f2);
			rel.setType(op.getType());
			assertTrue(DaoUtil.getRelationshipDao().getByExample(rel).size() == 1);
			logger.info(((Relationship)DaoUtil.getRelationshipDao().getByExample(rel).get(0)).getFeatures());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testVoteNoToBinaryRelationship() {
		try {
			// Create 2 features and 1 binary relationship
			Feature f1 = new Feature();
			Feature f2 = new Feature();
			f1.vote(true, 11111L);
			f2.vote(true, 2222L);
			f1.voteName("iamleft", true, 11111L);
			f2.voteName("iamright", true, 2222L);
			
			f1 = DaoUtil.getFeatureDao().save(f1);
			f2 = DaoUtil.getFeatureDao().save(f2);
			
			BinaryRelationship r = new BinaryRelationship();
			r.setFeatures(f1, f2);
			r.setType(Resources.BIN_REL_EXCLUDES);
			r.vote(true, 999L);
			
			r = (BinaryRelationship)DaoUtil.getRelationshipDao().save(r);
			
			// Make and handle request
			BinaryRelationshipOperation op = new BinaryRelationshipOperation();
			op.setRightFeatureId(f2.getId());
			op.setName(Resources.OP_CREATE_RELATIONSHIP);
			//op.setType(Resources.BIN_REL_REFINES);
			op.setVote(false);
			op.setUserid(1212L);
			op.setRelationshipId(r.getId());
			
			CommitRequest req = new CommitRequest();
			req.setId(444L);
			req.setName(Resources.REQ_COMMIT);
			req.setRequesterId(op.getUserid());
			req.setOperation(op);
			
			ResponseGroup rg = new ResponseGroup();
			action.execute(req, rg);
			
			logger.info("------------- VOTE NO TO BINARY RELATIONSHIP -------------");
			showResponse(rg.getBack());
			showResponse(rg.getBroadcast());
		} catch (Exception e) {
			logger.error("Couldn't execute.", e);
			assertTrue(false);
		}
	}
}
