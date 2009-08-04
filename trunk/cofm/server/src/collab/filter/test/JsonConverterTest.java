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

public class JsonConverterTest {
	
	protected static JsonConverter jc; 
	@BeforeClass
	public static void setUp() {
		jc = new JsonConverter("json-converter", null, null);
	}

	@Test
	public void testFilterRequest() {
		String rawBody = "{'type': 'commit', 'username': 'admin'}";
		Request req = new Request(new InetSocketAddress(1234), rawBody);
		Request filteredReq = jc.filterRequest(req); // OK if no "req = "
		if (filteredReq == null) {
			System.out.println(req.filterError());
			System.out.println(req.filterMessage());
		} else {
			DynaBean body = (DynaBean)filteredReq.body();
			assertEquals("commit", body.get("type"));
			assertEquals("admin", body.get("username"));
			assertEquals("json-converter", filteredReq.latestFilter());
		}
	}
	
	@Test
	public void testFilterResponse() {
		//class Body { String type; String username; }
		DynaClass dc = new BasicDynaClass("Body", BasicDynaBean.class, 
				new DynaProperty[] {
			new DynaProperty("type", String.class),
			new DynaProperty("username", String.class)
		});
		DynaBean rawBody = new BasicDynaBean(dc); 
		rawBody.set("type", "commit");
		rawBody.set("username", "admin");
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
