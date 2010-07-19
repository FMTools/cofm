package collab.fm.server.bean;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import collab.fm.server.util.JsonUtil;
@Ignore
public class OperationTest {

	@Test
	public void testFromMapAndJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Mark");
		map.put("vote", true);
		map.put("userid", 100L);
		try {
	    } catch (Exception e) {
			assertTrue(false);
		}
	}
}
