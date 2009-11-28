package collab.fm.server.bean;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import collab.fm.server.bean.protocol.Operation;
import collab.fm.server.util.BeanUtils;

public class OperationTest {

	@Test
	public void testFromMapAndJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Mark");
		map.put("vote", true);
		map.put("userid", 100L);
		try {
		Operation op = BeanUtils.mapToBean(Operation.class, map);
		assertEquals("Mark", op.getName());
	    } catch (Exception e) {
			assertTrue(false);
		}
	}
}
