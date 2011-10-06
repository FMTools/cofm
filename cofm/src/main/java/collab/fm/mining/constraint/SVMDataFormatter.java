package collab.fm.mining.constraint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class SVMDataFormatter {

	static Logger logger = Logger.getLogger(SVMDataFormatter.class);
	
	private static final String[] dataAttributes = {
		"totalSim", "objectSim", "firstAsObject", "secondAsObject"
	};
	

	private static int[] attrIndex;
	
	public static String attrInfo() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dataAttributes.length; i++) {
			sb.append(i + "=" + dataAttributes[i] + " ");
		}
		return sb.toString();
	}

	public static void updateAttrList(String[] attrs) {
		attrIndex = new int[attrs.length];
		for (int i = 0; i < attrIndex.length; i++) {
			attrIndex[i] = Integer.valueOf(attrs[i]).intValue();
		}
	}

	public static String format(int label, Object p) {
		StringBuffer sb = new StringBuffer();
		sb.append(label);
		
		for (int i = 0; i < attrIndex.length; i++) {
			String val = getAttrValue(dataAttributes[attrIndex[i]], p);
			if (val != null) {
				sb.append(" " + (i+1) + ":" + val);  
			}
		}
		return sb.toString();
	}
	
	private static String getMethodName(String attr) {
		return "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
	}
	
	private static String getAttrValue(String attr, Object p) {
		Method m;
		try {
			m = p.getClass().getMethod(getMethodName(attr));
			return m.invoke(p).toString();
		} catch (Exception e) {
			logger.warn("Cannot get value for " + attr, e);
			return null;
		}
	}
}
