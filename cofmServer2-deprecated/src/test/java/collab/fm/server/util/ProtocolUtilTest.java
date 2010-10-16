//package collab.fm.server.util;
//
//import java.util.*;
//
//import org.apache.log4j.Logger;
//import org.junit.*;
//
//import static org.junit.Assert.*;
//
//import collab.fm.server.bean.operation.BinaryRelationshipOperation;
//import collab.fm.server.bean.operation.FeatureOperation;
//import collab.fm.server.bean.operation.Operation;
//import collab.fm.server.bean.protocol.*;
//import collab.fm.server.bean.transfer.BinaryRelation2;
//import collab.fm.server.bean.transfer.Feature2;
//import collab.fm.server.bean.transfer.Model2;
//import collab.fm.server.bean.transfer.VotableString;
//public class ProtocolUtilTest {
//
//	static Logger logger = Logger.getLogger(ProtocolUtilTest.class);
//	
//	@Test 
//	public void testLoginRequest() {
//		LoginRequest lr = new LoginRequest();
//		lr.setId(1L);
//		lr.setName(Resources.REQ_LOGIN);
//		lr.setUser("Lao Yi");
//		lr.setPwd("WWWWWWWW");
//		
//		try {
//			String json = JsonUtil.beanToJson(lr);
//			logger.debug(json);
//			
//			Request req = ProtocolUtil.jsonToRequest(json);
//			assertTrue(req instanceof LoginRequest);
//			assertNull(req.getRequesterId());
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test 
//	public void testRegisterRequest() {
//		RegisterRequest lr = new RegisterRequest();
//		lr.setId(1L);
//		lr.setName(Resources.REQ_REGISTER);
//		lr.setUser("Lao Yi");
//		lr.setPwd("WWWWWWWW");
//		
//		try {
//			String json = JsonUtil.beanToJson(lr);
//			logger.debug(json);
//			
//			Request req = ProtocolUtil.jsonToRequest(json);
//			assertTrue(req instanceof RegisterRequest);
//			assertNull(req.getRequesterId());
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testListModelRequest() {
//		ListModelRequest req = new ListModelRequest();
//		req.setId(3L);
//		req.setName(Resources.REQ_LIST_MODEL);
//		
//		try {
//			String json = JsonUtil.beanToJson(req);
//			logger.debug(json);
//			
//			Request r = ProtocolUtil.jsonToRequest(json);
//			assertTrue(r instanceof ListModelRequest);
//			assertNull(r.getRequesterId());
//			assertNull(((ListModelRequest)r).getSearchWord());
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testListSpecificModelsRequest() {
//		ListModelRequest req = new ListModelRequest();
//		req.setId(3L);
//		req.setName(Resources.REQ_LIST_MODEL);
//		req.setSearchWord("Mark");
//		try {
//			String json = JsonUtil.beanToJson(req);
//			logger.debug(json);
//			
//			Request r = ProtocolUtil.jsonToRequest(json);
//			assertTrue(r instanceof ListModelRequest);
//			assertNull(r.getRequesterId());
//			assertEquals("Mark", ((ListModelRequest)r).getSearchWord());
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testListUserRequest() {
//		Request r = new Request();
//		r.setName(Resources.REQ_LIST_USER);
//		r.setId(1L);
//		try {
//			String json = JsonUtil.beanToJson(r);
//			logger.debug(json);
//			
//			Request req = ProtocolUtil.jsonToRequest(json);
//			logger.info(req.toString());
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testGenericResponse() {
//		Response r = new Response();
//		r.setMessage("haha");
//		r.setName(Resources.RSP_SUCCESS);
//		r.setRequesterId(1L);
//		r.setRequestId(10L);
//		r.setRequestName(Resources.REQ_CONNECT);
//		
//		try {
//			logger.info(ProtocolUtil.ResponseToJson(r));
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testCommitRequestWithMinimalValidInput() {
//		// 1. Construct a minimal valid JSON string from an object
//		CommitRequest cr = new CommitRequest();
//		cr.setId(100L);
//		cr.setName(Resources.REQ_COMMIT);
//		cr.setRequesterId(5L);
//		cr.setModelId(111L);
//		BinaryRelationshipOperation brop = new BinaryRelationshipOperation();
//		// The relationshipId and userId are not always required.
//		brop.setLeftFeatureId(1L);
//		brop.setRightFeatureId(3L);
//		brop.setName(Resources.OP_CREATE_RELATIONSHIP);
//		brop.setType(Resources.BIN_REL_REFINES);
//		brop.setVote(true);
//		
//		cr.setOperation(brop);
//		
//		try {
//			String json = JsonUtil.beanToJson(cr);
//			logger.debug(json);
//			
//			//2. Convert
//			Request req = ProtocolUtil.jsonToRequest(json);
//			assertTrue(req instanceof CommitRequest);
//			CommitRequest creq = (CommitRequest)req;
//			assertTrue(creq.getOperation() instanceof BinaryRelationshipOperation);
//			logger.debug(creq);
//			logger.debug(req);
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testCommitResponse() {
//		// 1. Create some operations
//		BinaryRelationshipOperation op1 = new BinaryRelationshipOperation();
//		op1.setLeftFeatureId(1L);
//		op1.setName(Resources.OP_CREATE_RELATIONSHIP);
//		op1.setRelationshipId(20L);
//		op1.setRightFeatureId(5L);
//		op1.setType(Resources.BIN_REL_EXCLUDES);
//		op1.setUserid(200L);
//		op1.setVote(true);
//		
//		FeatureOperation op2 = new FeatureOperation();
//		op2.setFeatureId(200L);
//		op2.setName(Resources.OP_ADD_DES);
//		op2.setUserid(10000L);
//		op2.setValue("hhhhhhhhhhhhh");
//		op2.setVote(true);
//		
//		CommitResponse rsp = new CommitResponse();
//		rsp.setName(Resources.RSP_FORWARD);
//		rsp.setOperations(Arrays.asList(new Operation[] {op1, op2}));
//		rsp.setRequesterId(333L);
//		rsp.setRequestId(9L);
//		rsp.setRequestName(Resources.REQ_COMMIT);
//		
//		try {
//			logger.info(ProtocolUtil.ResponseToJson(rsp));
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testListModelResponse() {
//		Set<Long> yes = new HashSet<Long>();
//		yes.addAll(Arrays.asList(new Long[] { 1L, 3L, 5L, 7L, 9L }));
//		
//		Set<Long> no = new HashSet<Long>();
//		no.addAll(Arrays.asList(new Long[] { 2L, 4L, 6L, 8L, 10L }));
//		
//		VotableString n1 = new VotableString();
//		n1.setVal("eclipse");
//		n1.setV1(yes);
//		n1.setV0(no);
//		
//		VotableString n2 = new VotableString();
//		n2.setVal("jbuilder");
//		n2.setV0(yes);
//		n2.setV1(no);
//		
//		List<VotableString> names = Arrays.asList(new VotableString[] { n1, n2 });
//		
//		VotableString d = new VotableString();
//		d.setVal("----------------------------------------------");
//		d.setV1(yes);
//		d.setV0(no);
//		List<VotableString> des = Arrays.asList(new VotableString[] { d });
//		
//		Set<Long> users = new HashSet<Long>();
//		users.addAll(yes);
//		users.addAll(no);
//		
//		Model2 m2 = new Model2();
//		m2.setId(1L);
//		m2.setNames(names);
//		m2.setDscs(des);
//		m2.setUsers(users);
//		
//		ListModelResponse lmr = new ListModelResponse();
//		lmr.setModels(new ArrayList<Model2>());
//		lmr.getModels().add(m2);
//		lmr.setName(Resources.RSP_SUCCESS);
//		lmr.setRequesterId(null);
//		lmr.setRequestId(3L);
//		lmr.setRequestName(Resources.REQ_LIST_MODEL);
//		try {
//			logger.info(ProtocolUtil.ResponseToJson(lmr));
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//	}
//	
//	@Test
//	public void testUpdateResponse() {
//		Set<Long> yes = new HashSet<Long>();
//		yes.addAll(Arrays.asList(new Long[] { 1L, 3L, 5L, 7L, 9L }));
//		
//		Set<Long> no = new HashSet<Long>();
//		no.addAll(Arrays.asList(new Long[] { 2L, 4L, 6L, 8L, 10L }));
//		
//		VotableString n1 = new VotableString();
//		n1.setVal("eclipse");
//		n1.setV1(yes);
//		n1.setV0(no);
//		
//		VotableString n2 = new VotableString();
//		n2.setVal("jbuilder");
//		n2.setV0(yes);
//		n2.setV1(no);
//		
//		List<VotableString> names = Arrays.asList(new VotableString[] { n1, n2 });
//		
//		VotableString d = new VotableString();
//		d.setVal("----------------------------------------------");
//		d.setV1(yes);
//		d.setV0(no);
//		List<VotableString> des = Arrays.asList(new VotableString[] { d });
//		
//		Feature2 f = new Feature2();
//		f.setDscs(des);
//		f.setId(3L);
//		f.setNames(names);
//		f.setRels(yes);
//		f.setV0(no);
//		f.setOpt0(yes);
//		f.setOpt1(no);
//		f.setV1(yes);
//		
//		BinaryRelation2 b = new BinaryRelation2();
//		b.setId(1L);
//		b.setLeft(999L);
//		b.setRight(333L);
//		b.setType(Resources.BIN_REL_EXCLUDES);
//		b.setV0(no);
//		b.setV1(yes);
//		
//		UpdateResponse response = new UpdateResponse();
//		response.setBinaries(Arrays.asList(new BinaryRelation2[] { b }));
//		response.setFeatures(Arrays.asList(new Feature2[] { f, f }));
//		response.setName(Resources.RSP_SUCCESS);
//		response.setRequesterId(11L);
//		response.setRequestId(3L);
//		response.setRequestName(Resources.REQ_UPDATE);
//		try {
//			logger.info(ProtocolUtil.ResponseToJson(response));
//		} catch (Exception e) {
//			logger.info(e);
//			assertTrue(false);
//		}
//		
//	}
//}
