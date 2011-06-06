package collab.fm.server.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.persist.entity.NumericAttributeType;
import collab.fm.server.bean.transfer.AttributeType2;
import collab.fm.server.bean.transfer.EnumAttributeType2;
import collab.fm.server.bean.transfer.NumericAttributeType2;

public class EntityUtil {
	
	private static Logger logger = Logger.getLogger(EntityUtil.class);
	
	public static <T> Set<T> cloneSet(Set<T> source) {
		Set<T> result = new HashSet<T>();
		result.addAll(source);
		return result;
	}
	
	public static AttributeType cloneAttribute(AttributeType a) {
		AttributeType a2 = null;
		if (AttributeType.TYPE_ENUM.equals(a.getTypeName())) {
			a2 = new EnumAttributeType();
			List<String> validValues = new ArrayList<String>();
			for (String s: ((EnumAttributeType)a).getValidValues()) {
				validValues.add(s);
			}
			((EnumAttributeType)a2).setValidValues(validValues);
		} else if (AttributeType.TYPE_NUMBER.equals(a.getTypeName())) {
			a2 = new NumericAttributeType();
			((NumericAttributeType)a2).setMax(((NumericAttributeType)a).getMax());
			((NumericAttributeType)a2).setUnit(((NumericAttributeType)a).getUnit());
			((NumericAttributeType)a2).setMin(((NumericAttributeType)a).getMin());
		} else {
			a2 = new AttributeType();
		}
		a2.setEnableGlobalDupValues(a.isEnableGlobalDupValues());
		a2.setMultipleSupport(a.isMultipleSupport());
		a2.setTypeName(a.getTypeName());
		return a2;
	}

	public static AttributeType2 transferAttributeType(AttributeType t) {
		AttributeType2 rslt = null;
		if (t.getTypeName().equals(AttributeType.TYPE_ENUM)) {
			rslt = new EnumAttributeType2();
			((EnumAttributeType)t).transfer(rslt);
		} else if (t.getTypeName().equals(AttributeType.TYPE_NUMBER)) {
			rslt = new NumericAttributeType2();
			((NumericAttributeType)t).transfer(rslt);
		} else {
			rslt = new AttributeType2();
			t.transfer(rslt);
		}
		return rslt;
	}
	
}
