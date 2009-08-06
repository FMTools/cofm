package collab.filter.test;

import static org.junit.Assert.*;
import org.apache.commons.beanutils.*;
import org.junit.Test;
import collab.filter.util.*;
import collab.data.*;

public class FieldConstraintTest {

	@Test
	public void testMyself() {
		FieldConstraint myself = new FieldConstraint("parts", String[].class);
		assertTrue(myself.conformTo(myself));
	}
	
	@Test
	public void testObject() {
		Request req = new Request();
		req.setName("n");
		req.setId(100);
		FieldConstraint[] cons = new FieldConstraint[]{
			new FieldConstraint("name", String.class),
			new FieldConstraint("id", Integer.class),
			new FieldConstraint("user", String.class)
		};
		assertTrue(cons[0].conformTo(req));
		assertTrue(cons[1].conformTo(req));
		assertFalse(cons[2].conformTo(req));
	}
	
	@Test
	public void testDynaBean() {
		DynaClass dc = new BasicDynaClass("MyClass", BasicDynaBean.class, 
			new DynaProperty[] {
			    new DynaProperty("name", String.class),
			    new DynaProperty("id", Integer.class),
			    new DynaProperty("dyna", BasicDynaBean.class)
		});
		DynaBean bean = new BasicDynaBean(dc);
		bean.set("name", "admin");
		bean.set("id", 100);
		bean.set("dyna", bean);
		FieldConstraint[] cons = new FieldConstraint[]{
				new FieldConstraint("name", String.class),
				new FieldConstraint("id", Integer.class),
				new FieldConstraint("dyna", BasicDynaBean.class)
		};
		assertTrue(cons[0].conformTo(bean));
		assertTrue(cons[1].conformTo(bean));
		assertTrue(cons[2].conformTo(bean));
	}

}
