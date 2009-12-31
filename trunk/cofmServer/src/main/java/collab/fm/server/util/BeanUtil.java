package collab.fm.server.util;

import java.lang.reflect.Field;
import java.util.*;

import net.sf.ezmorph.*;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.*;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertyFilter;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Votable;
import collab.fm.server.bean.protocol.UpdateResponse.Name2;
import collab.fm.server.util.exception.BeanConvertException;
import collab.fm.server.util.exception.JsonConvertException;

public final class BeanUtil {
	
	static Logger logger = Logger.getLogger(BeanUtil.class);
	
	public static <T> T mapToBean(Class<T> beanClass, Map<String, Object> map)
		throws BeanConvertException {
		if (map == null) {
			return null;
		}
		List<Field> fields = new ArrayList<Field>();
		for (Class<? super T> clazz = beanClass; clazz != null; clazz = clazz.getSuperclass()) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		T bean;
		try {
			bean = beanClass.newInstance();
		} catch (Exception e) {
			throw new BeanConvertException("Cannot create the bean.", e);
		}
		for (Field field: fields) {
			Object value = map.get(field.getName());
			
				try {
					field.setAccessible(true);
					if (value != null) {
						field.set(bean, field.getType().cast(value));
					} else {
						field.set(bean,	null);
					}
				} catch (IllegalArgumentException e) {
					throw new BeanConvertException("Cannot set field of the bean.", e);
				} catch (IllegalAccessException e) {
					throw new BeanConvertException("Cannot set field of the bean.", e);
				}
		}
		return bean;
	}
	
	/**
	 * Convert Java Bean to JSON string.
	 * @param bean
	 * 		  The Java Bean
	 * @return The JSON string, null if cast fails.
	 */
	public static String beanToJson(Object bean) throws JsonConvertException {
		return beanToJson(bean, null);
	}
	
	/**
	 * Convert Java Bean to JSON string, but skip some fields in the bean.
	 * @param bean
	 * 		  The Java Bean
	 * @param skipFields
	 * 		  Name of the skipped fields.
	 * @return
	 * 		  The JSON String, null if cast fails.
	 */
	public static String beanToJson(Object bean, final String[] skipFields) 
		throws JsonConvertException {
		try {
			JsonConfig cfg = new JsonConfig();
			
				cfg.setJsonPropertyFilter(new PropertyFilter() {
					public boolean apply(Object source, String name,
							Object value) {
						if (value == null) { // skip the null fields
							return true;
						} else if (skipFields != null) {
							for (String skip : skipFields) {
								if (skip.equals(name)) {
									return true;
								}
							}
						}
						return false;
					}
				});
			JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(bean, cfg);
			return jsonObj.toString();
		} catch (JSONException e) {
			throw new JsonConvertException("Convert bean to json failed.", e);
		}
	} 
	
	public static <T> T jsonToBean(Object srcJson, Class<T> beanClass, Map<String, Class> clsMap) 
		throws BeanConvertException {
		return jsonToBean(srcJson, beanClass, clsMap, null);
	}
	
	/**
	 * Convert a JSON string to a Java bean.
	 * @param srcJson
	 * 		The JSON string
	 * @param beanClass
	 * 		The class of the Java Bean
	 * @param clsMap
	 * 		If a field, whether nested or not, of the bean is of a class type (not of a primitive type), 
	 * 		then its class must be listed in the map.
	 * @param fields
	 * 		Keep the fields only. 
	 * @return The bean
	 */
	public static <T> T jsonToBean(Object srcJson, Class<T> beanClass, Map<String, Class> clsMap, final String[] fields) 
		throws BeanConvertException {
		logger.debug("json is: " + srcJson);
		try {
			JSON json = JSONSerializer.toJSON(srcJson);
			JsonConfig cfg = new JsonConfig();
			cfg.setRootClass(beanClass);
			if (clsMap != null) {
				cfg.setClassMap(clsMap);
			}
			if (fields != null) {
				cfg.setJavaPropertyFilter(new PropertyFilter() {
					public boolean apply(Object source, String name, Object value) {
						for (String field: fields) {
							if (field.equals(name)) {
								return false;
							}
						}
						return true;
					}
				});
			}

			return beanClass.cast(JSONSerializer.toJava(json, cfg));
		} catch (Exception e) {
			throw new BeanConvertException("Convert json to bean failed.", e);
		}
	}
	
	/** 
	 * Cast a list of bean of some type to another type.
	 * @param src The origin list of bean
	 * @param beanClass The result type of the beans
	 * @return The casted list of bean
	 */
	public static List castBeanList(List src, Class beanClass ) throws BeanConvertException {
		try {
			MorpherRegistry reg = JSONUtils.getMorpherRegistry();
			reg.registerMorpher(new BeanMorpher(beanClass, reg));
			List list = new ArrayList();
			for (Object o: src) {
				list.add(beanClass.cast(reg.morph(beanClass, o)));
			}
			return list;
		} catch (Exception e) {
			throw new BeanConvertException("Convert bean list failed.", e);
		}
	}
}
