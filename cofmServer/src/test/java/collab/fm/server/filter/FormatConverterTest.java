package collab.fm.server.filter;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.util.BeanUtils;
import collab.fm.server.util.Resources;
import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.BinaryRelationshipOperation;
import collab.fm.server.bean.protocol.Operation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;

public class FormatConverterTest {
	
	protected static FormatConverter jc; 
	@BeforeClass
	public static void setUp() {
		jc = new FormatConverter("json-converter");
	}
	
	static Logger logger = Logger.getLogger(FormatConverterTest.class);

	@Test
	public void testFilterRequest() {
		//1. Construct a request
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Mark");
		map.put("vote", true);
		map.put("userid", 100L);
		map.put("leftFeatureId", 1L);
		map.put("rightFeatureId", 2L);
		try {
		Operation op = BeanUtils.mapToBean(BinaryRelationshipOperation.class, map);
		
		Request req = new Request();
		req.setAddress("123.123.123.123");
		req.setId(11111L);
		req.setName(Resources.REQ_COMMIT);
		req.setUser("Mark");
		req.setData(op);
		
		String originData = BeanUtils.beanToJson(req, new String[] {"address"});
		
		Request originReq = new Request();
		originReq.setAddress(req.getAddress());
		originReq.setData(originData);
		
		Request filteredReq = jc.filterRequest(originReq);
		if (filteredReq == null) {
			logger.debug(originReq.filterError());
			logger.debug(originReq.filterMessage());
			assertEquals(1, 2);  // Deliberately report a failure here.
		} else {
			assertEquals(filteredReq.getAddress(), req.getAddress());
		}
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testFilterResponse() {
		Response rsp = new Response();
		rsp.setType(Response.TYPE_PEER_FORWARD);
		rsp.setBody(new User());  
		Response filteredRsp = jc.filterResponse(rsp);
		if (filteredRsp == null) {
			logger.debug(rsp.filterError());
			logger.debug(rsp.filterMessage());
		} else {
			logger.debug(filteredRsp.getBody());
		}
	}
	
}
