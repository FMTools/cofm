package collab.fm.server.util;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

import collab.fm.server.bean.operation.BinaryRelationshipOperation;
import collab.fm.server.bean.operation.FeatureOperation;
import collab.fm.server.bean.operation.Operation;
import collab.fm.server.bean.protocol.*;

public class ProtocolUtilTest {

	static Logger logger = Logger.getLogger(ProtocolUtilTest.class);
	
	@Test 
	public void testLoginRequest() {
		LoginRequest lr = new LoginRequest();
		lr.setId(1L);
		lr.setName(Resources.REQ_LOGIN);
		lr.setUser("Lao Yi");
		lr.setPwd("WWWWWWWW");
		
		try {
			String json = BeanUtil.beanToJson(lr);
			logger.debug(json);
			
			Request req = ProtocolUtil.jsonToRequest(json);
			assertTrue(req instanceof LoginRequest);
			assertNull(req.getRequesterId());
		} catch (Exception e) {
			logger.info(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGenericResponse() {
		Response r = new Response();
		r.setMessage("haha");
		r.setName(Resources.RSP_SUCCESS);
		r.setRequesterId(1L);
		r.setRequestId(10L);
		r.setRequestName(Resources.REQ_CONNECT);
		
		try {
			logger.info(ProtocolUtil.ResponseToJson(r));
		} catch (Exception e) {
			logger.info(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testCommitRequestWithMinimalValidInput() {
		// 1. Construct a minimal valid JSON string from an object
		CommitRequest cr = new CommitRequest();
		cr.setId(100L);
		cr.setName(Resources.REQ_COMMIT);
		cr.setRequesterId(5L);
		BinaryRelationshipOperation brop = new BinaryRelationshipOperation();
		// The relationshipId and userId are not always required.
		brop.setLeftFeatureId(1L);
		brop.setRightFeatureId(3L);
		brop.setName(Resources.OP_CREATE_RELATIONSHIP);
		brop.setType(Resources.BIN_REL_REFINES);
		brop.setVote(true);
		
		cr.setOperation(brop);
		
		try {
			String json = BeanUtil.beanToJson(cr);
			logger.debug(json);
			
			//2. Convert
			Request req = ProtocolUtil.jsonToRequest(json);
			assertTrue(req instanceof CommitRequest);
			CommitRequest creq = (CommitRequest)req;
			assertTrue(creq.getOperation() instanceof BinaryRelationshipOperation);
			logger.debug(creq);
			logger.debug(req);
		} catch (Exception e) {
			logger.info(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testCommitResponse() {
		// 1. Create some operations
		BinaryRelationshipOperation op1 = new BinaryRelationshipOperation();
		op1.setLeftFeatureId(1L);
		op1.setName(Resources.OP_CREATE_RELATIONSHIP);
		op1.setRelationshipId(20L);
		op1.setRightFeatureId(5L);
		op1.setType(Resources.BIN_REL_EXCLUDES);
		op1.setUserid(200L);
		op1.setVote(true);
		
		FeatureOperation op2 = new FeatureOperation();
		op2.setFeatureId(200L);
		op2.setName(Resources.OP_ADD_DES);
		op2.setUserid(10000L);
		op2.setValue("hhhhhhhhhhhhh");
		op2.setVote(true);
		
		CommitResponse rsp = new CommitResponse();
		rsp.setName(Resources.RSP_FORWARD);
		rsp.setOperations(Arrays.asList(new Operation[] {op1, op2}));
		rsp.setRequesterId(333L);
		rsp.setRequestId(9L);
		rsp.setRequestName(Resources.REQ_COMMIT);
		
		try {
			logger.info(ProtocolUtil.ResponseToJson(rsp));
		} catch (Exception e) {
			logger.info(e);
			assertTrue(false);
		}
	}
}
