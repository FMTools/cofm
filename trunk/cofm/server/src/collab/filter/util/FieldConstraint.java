package collab.filter.util;

import java.lang.reflect.*;

import org.apache.commons.beanutils.DynaBean;

/** 
 * Check missing field, wrong field type and/or null field value in a bean(or a dynabean)
 * Usage: new FieldConstraint("foo.bar", String.class).conformTo(myBean)
 */
public class FieldConstraint implements Constraint {
	
	private String[] parts;
	private Class<?> type;
	
	public FieldConstraint(String name, Class<?> type) {
		if (name.indexOf(".") >= 0) {
			parts = name.split(".");
		} else {
			parts = new String[]{name};
		}
		this.type = type;
	}
	
	public boolean conformTo(Object obj) {
		try {
			Object o = obj;
			for (String part: parts) {
				if (obj instanceof DynaBean) {
					o = ((DynaBean)obj).get(part);
				} else {
					Field field = obj.getClass().getDeclaredField(part); 
					field.setAccessible(true);
					o = field.get(obj);
				}
				obj = o;
			}
			if (type.isInstance(o)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			sb.append("." + parts[i]);
		}
		return "Field '" + sb.toString() + "' should have type of '" + type.toString() + "'";
	}
}
