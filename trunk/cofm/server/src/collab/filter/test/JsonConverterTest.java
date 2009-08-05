package collab.filter.test;

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

public class JsonConverterTest {
	
	protected static JsonConverter jc; 
	@BeforeClass
	public static void setUp() {
		jc = new JsonConverter("json-converter");
	}

	@Test
	public void testFilterRequest() {
		String rawBody = "{'type': 'commit', 'username': 'admin', 'nil': null}";
		Request req = new Request(new InetSocketAddress(1234), rawBody);
		Request filteredReq = jc.filterRequest(req); // OK if no "req = "
		if (filteredReq == null) {
			System.out.println(req.filterError());
			System.out.println(req.filterMessage());
		} else {
			DynaBean body = (DynaBean)filteredReq.body();
			assertEquals("commit", body.get("type"));
			assertEquals("admin", body.get("username"));
			assertNull(body.get("nil"));
			assertEquals("json-converter", filteredReq.latestFilter());
		}
	}
	
	@Test
	public void testFilterResponse() {
		ResponseBody rawBody = new ResponseBody();
		rawBody.setName("myname");
		ResponseBody.Source src = rawBody.new Source();
		src.setName("srcname");
		src.setAddress("123:321");
		src.setId("444");
		src.setUser("admin");
		rawBody.setSource(src);
		
		Response rsp = new Response(Response.TYPE_PEER, rawBody);
		Response filteredRsp = jc.filterResponse(rsp);
		if (filteredRsp == null) {
			System.out.println(rsp.filterError());
			System.out.println(rsp.filterMessage());
		} else {
			System.out.println(rsp.body());
		}
		
	}
}
