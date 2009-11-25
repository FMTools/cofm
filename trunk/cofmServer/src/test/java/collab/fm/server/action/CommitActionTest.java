package collab.fm.server.action;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import collab.fm.server.bean.BinaryRelationshipOperation;
import collab.fm.server.bean.Operation;
import collab.fm.server.bean.Request;
import collab.fm.server.controller.BasicController;
import collab.fm.server.controller.Controller;
import collab.fm.server.util.BeanUtils;
import collab.fm.server.util.Resources;

public class CommitActionTest {
	
	@Test 
	public void testGetOperation() {
		//1. Construct a request
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", Resources.OP_ADD_CHILD);
		map.put("vote", true);
		map.put("userid", 100L);
		map.put("leftFeatureId", 1L);
		map.put("rightFeatureId", 2L);
		try {
		Operation op = BeanUtils.mapToBean(BinaryRelationshipOperation.class, map);
		String opJson = BeanUtils.beanToJson(op);
		
		Request req = new Request();
		req.setAddress("123.123.123.123");
		req.setId(11111L);
		req.setName(Resources.REQ_COMMIT);
		req.setUser("Mark");
		req.setData(opJson);
		
		Controller c = new BasicController();
		CommitAction action = new CommitAction(c);
		Method testee = action.getClass().getDeclaredMethod("getOperation", Request.class);
		testee.setAccessible(true);
		Operation op2 = (Operation)testee.invoke(action, req);
		
		assertEquals(op.toString(), op2.toString());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
