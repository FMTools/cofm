package collab.test.filter;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.*;

import collab.filter.*;
import collab.data.*;
import collab.data.bean.*;

public class ProtocolInterpreterTest {
	
	protected static ProtocolInterpreter jc; 
	@BeforeClass
	public static void setUp() {
		jc = new ProtocolInterpreter("json-converter");
	}

	@Test
	public void testFilterRequest() {
		String rawBody = "{'id':100,'name':'commit','data':{'op':'god', 'vote':'yes'}}";
		Request req = new Request();
		req.setAddress("123.456");
		req.setData(rawBody);
		Request filteredReq = jc.filterRequest(req); // OK if no "req = "
		if (filteredReq == null) {
			System.out.println(req.filterError());
			System.out.println(req.filterMessage());
		} else {
			assertEquals("commit", filteredReq.getName());
			assertEquals(100, filteredReq.getId());
			assertEquals("god", ((DynaBean)filteredReq.getData()).get("op"));
			assertEquals("json-converter", filteredReq.latestFilter());		
		}
	}
	
	@Test
	public void testFilterRequest2() {
		String json = "{'id':100,'name':'commit','data':{'yesno':true}}";
		Request req = new Request();
		req.setData(json);
		jc.filterRequest(req);
		assertTrue((Boolean)((DynaBean)req.getData()).get("yesno"));
		/*if (Boolean.class.isInstance(((DynaBean)req.getData()).get("yesno"))) {
			System.out.println("yes");
		}*/
	}
	
	@Test
	public void testFilterResponse() {
		Response rsp = new Response();
		rsp.setType(Response.TYPE_PEER);
		rsp.setBody(null);
		Response filteredRsp = jc.filterResponse(rsp);
		if (filteredRsp == null) {
			System.out.println(rsp.filterError());
			System.out.println(rsp.filterMessage());
		} else {
			System.out.println(rsp.getBody());
		}
	}
	
}
