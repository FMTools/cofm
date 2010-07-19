package collab.fm.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.transfer.Attribute2;
import collab.fm.server.bean.transfer.EnumAttribute2;
import collab.fm.server.bean.transfer.NumericAttribute2;

public class EntityUtil {
	
	private static Logger logger = Logger.getLogger(EntityUtil.class);
	
	public static <T> Set<T> cloneSet(Set<T> source) {
		Set<T> result = new HashSet<T>();
		result.addAll(source);
		return result;
	}
	
	public static String formatDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}
	
	public static Attribute2 transferFromAttr(Attribute origin) {
		String atype = origin.getType();
		Attribute2 a2 = null;
		if (Attribute.TYPE_ENUM.equals(atype)) {
			a2 = new EnumAttribute2();
		} else if (Attribute.TYPE_NUMBER.equals(atype)) {
			a2 = new NumericAttribute2();
		} else {
			a2 = new Attribute2();
		}
		origin.transfer(a2);
		return a2;
	}
	
}
