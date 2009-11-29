package collab.fm.server.action;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import collab.fm.server.bean.protocol.BinaryRelationshipOperation;
import collab.fm.server.bean.protocol.Operation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.controller.BasicController;
import collab.fm.server.controller.Controller;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.Resources;

public class CommitActionTest {
	
	static Logger logger = Logger.getLogger(CommitActionTest.class);
	
	private void testGetBinaryRelationshipOperation(Long relationshipId) {
		//1. Construct a request
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("relationshipId", relationshipId);
		map.put("name", Resources.OP_CREATE_BINARY_RELATIONSHIP);
		map.put("type", Resources.BIN_REL_EXCLUDES);
		map.put("vote", true);
		map.put("userid", 100L);
		map.put("leftFeatureId", 1L);
		map.put("rightFeatureId", 2L);
		try {
			Operation op = BeanUtil.mapToBean(
					BinaryRelationshipOperation.class, map);

			String opJson = BeanUtil.beanToJson(op);

			logger.debug("<id=" + relationshipId + "> " + opJson);

			Request req = new Request();
			req.setAddress("123.123.123.123");
			req.setId(11111L);
			req.setName(Resources.REQ_COMMIT);
			req.setUser("Mark");
			req.setData(opJson);

			// 2. Test getOperation
			Controller c = new BasicController();
			CommitAction action = new CommitAction(c);
			Method testee = action.getClass().getDeclaredMethod("getOperation",
					Request.class);
			testee.setAccessible(true);
			Operation op2 = (Operation) testee.invoke(action, req);

			assertEquals(op.toString(), op2.toString());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test 
	public void testGetBinaryRelationshipOperationWithNullId() {
		testGetBinaryRelationshipOperation(null);
	}
	
	@Test 
	public void testGetBinaryRelationshipOperation() {
		testGetBinaryRelationshipOperation(new Long(1000L));
	}
}
