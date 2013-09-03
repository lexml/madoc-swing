package br.gov.lexml.madoc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection utilities
 * @author lauro
 *
 */
public class ReflectionUtil {

	private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
	
	public static final class MethodPair {
		private final Method _get;
		private final Method _set;

		public MethodPair(Method get, Method set) {
			super();
			this._get = get;
			this._set = set;
		}

		public String get(Object o) {
			try {
				return (String) _get.invoke(o);
			} catch (IllegalArgumentException e) {
				log.warn("error calling get method " + _get.getName() + " on " + o, e);
			} catch (IllegalAccessException e) {
				log.warn("error calling get method " + _get.getName() + " on " + o, e);
			} catch (InvocationTargetException e) {
				log.warn("error calling get method " + _get.getName() + " on " + o, e);
			}
			return null;
		}

		public void set(Object o, String value) {
			try {
				_set.invoke(o, value);
			} catch (IllegalArgumentException e) {
				log.warn("error calling set method " + _set.getName() + " on " + o, e);
			} catch (IllegalAccessException e) {
				log.warn("error calling set method " + _set.getName() + " on " + o, e);
			} catch (InvocationTargetException e) {
				log.warn("error calling set method " + _set.getName() + " on " + o, e);
			} 
		}

	}
	
	/**
	 * Returns a map of field name and pair of get and set methods of the classType
	 * @param o
	 * @return
	 */
	public static Map<String, MethodPair> getGetSetMethods(Object o, Class<?> classType) {
		Class<?> clazz = o.getClass();
		Map<String, MethodPair> result = new HashMap<String, MethodPair>();
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (m.getParameterTypes().length == 0
					&& m.getName().startsWith("get")
					&& m.getReturnType().equals(classType)) {
				String name = m.getName().substring(3);
				try {
					Method set = clazz.getMethod("set" + name, classType);
					if (set != null) {
						String n = name.substring(0, 1).toLowerCase()
								+ name.substring(1);
						result.put(n, new MethodPair(m, set));
					}
				} catch (SecurityException e) {
					log.warn("Error listing methods in " + o + ": " + e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					log.warn("Error listing methods in " + o + ": " + e.getMessage(), e);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns a map of field name and pair of get and set methods of the classType that:
	 *  a) the current value matches to nameRegex and;
	 *  b) the field name is not in noFields list
	 */
	public static Map<String, MethodPair> getGetSetMethods(Object o, Class<?> classType, String valueRegex, List<String> noFields){
		Map<String, MethodPair> semiResult = getGetSetMethods(o, classType);
		
		Map<String, MethodPair> result = new HashMap<String, MethodPair>();
		for (Map.Entry<String, ReflectionUtil.MethodPair> entry : semiResult.entrySet()) {
			MethodPair mp = entry.getValue();
			String name = entry.getKey();
			String originalValue = mp.get(o);
			
			if (originalValue!= null && originalValue.matches(valueRegex) && (!noFields.contains(name))){
				result.put(name, mp);
			}
		}
		
		return result;
	}
}
