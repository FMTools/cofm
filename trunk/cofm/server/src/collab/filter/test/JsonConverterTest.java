package collab.filter.test;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.apache.commons.beanutils.DynaBean;
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
		req = jc.filterRequest(req); // OK if no "req = "
		DynaBean body = (DynaBean)req.body();
		assertEquals("commit", body.get("type"));
		assertEquals("admin", body.get("username"));
		assertEquals("json-converter", req.latestFilter());
	}
	
	@Test
	public void testFilterResponse() {
		Body rawBody = new Body("commit", "admin");
		Response rsp = new Response(Response.TYPE_PEER, rawBody, true);
		Response filteredRsp = jc.filterResponse(rsp);
		if (filteredRsp == null) {
			System.out.println(rsp.filterMessage());
		} else {
			System.out.println(rsp.body());
		}
		
	}
	
	public class Body {
		private String type;
		private String username;
		
		public Body(String t, String u) {
			type = t;
			username = u;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String t) {
			type = t;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String u) {
			username = u;
		}
	}
}
