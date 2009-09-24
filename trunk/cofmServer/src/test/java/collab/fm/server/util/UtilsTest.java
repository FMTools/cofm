package collab.fm.server.util;

import java.util.*;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

import collab.fm.server.util.*;
import collab.fm.server.bean.*;

public class UtilsTest {

	static Logger logger = Logger.getLogger(UtilsTest.class);
	
	@Test
	public void testResponseJsonCast() {
		// Response to JSON
		Response rsp = new Response();
		Response.Body body = new Response.Body();
		Response.Body.Source src = new Response.Body.Source();
		
		src.setAddress("123.123.123.1");
		src.setId(1);
		src.setName(Resources.REQ_COMMIT);
		src.setUser("mark");
		
		body.setStatus(Resources.RSP_DENIED);
		body.setSource(src);
		body.setData("String Data");
		
		rsp.setBody(body);
		rsp.setType(Response.TYPE_BROADCAST);
		
		String json = Utils.beanToJson(rsp);
		logger.info(json);
		
		// JSON to Response
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("body", Response.Body.class);
		map.put("source", Response.Body.Source.class);
		Response rsp2 = Utils.jsonToBean(json, Response.class, map);
		
		Response.Body b = (Response.Body)rsp.getBody();
		Response.Body.Source s = b.getSource();
		Response.Body b2 = (Response.Body)rsp2.getBody();
		Response.Body.Source s2 = b2.getSource();
		
		assertEquals(rsp.getType(), rsp2.getType());
		assertEquals(b.getData(), b2.getData());
		assertEquals(b.getStatus(), b2.getStatus());
		assertEquals(s.getAddress(), s2.getAddress());
		assertEquals(s.getId(), s2.getId());
		assertEquals(s.getName(), s2.getName());
		assertEquals(s.getUser(), s2.getUser());
	}
		
	@Test
	public void testTwoStepJsonToBean() {
		Bean1 b1 = new Bean1();
		b1.setName("bean one");
		Bean2 b2 = new Bean2();
		b2.setId("two");
		b2.setData(Arrays.asList(b1));
		Bean3 b3 = new Bean3();
		b3.setKey("key 3");
		b3.setValue(b2);
		
		String json = Utils.beanToJson(b3);
		logger.info(json);
		
		// Step 1: json to bean3, typeof value == DynaBean
		Bean3 bean = Utils.jsonToBean(json, Bean3.class, null);
		
		// Step 2: bean3.value(DynaBean) to Bean2
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("data", Bean1.class);
		Bean2 bean2 = Utils.jsonToBean(bean.getValue(), Bean2.class, map);
		
		assertEquals(b3.getKey(), bean.getKey());
		assertEquals(b2.getId(), bean2.getId());
		assertArrayEquals(b2.getData().toArray(), bean2.getData().toArray());
	}

	public static class Bean1 {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object obj) {
			return name.equals(((Bean1)obj).getName());
		}
		
		
	}
	
	public static class Bean2 {
		private String id;
		private List<Bean1> data = new ArrayList<Bean1>();
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<Bean1> getData() {
			return data;
		}
		public void setData(List<Bean1> data) {
			this.data = data;
		}
	}
	
	public static class Bean3 {
		private String key;
		private Object value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
}
