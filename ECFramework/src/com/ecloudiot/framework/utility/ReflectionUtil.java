package com.ecloudiot.framework.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.greenrobot.event.EventBus;

public class ReflectionUtil {
	private final static String TAG = "ReflectionUtil";

	public static Object invokeMethod(Object owner, String methodName, Object arg) {
		Object[] args = null;
		if (null != arg) {
			args = new Object[]{arg};
		}
		return invokeMethod(owner, methodName, args);
	}
	@SuppressWarnings("rawtypes")
	public static Object invokeMethod(Object owner, String methodName, Object[] args) {
		Class[] argsClass = null;
		if (null != args) {
			argsClass = new Class[args.length];
			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
		}
		return invokeMethod(owner, methodName, argsClass, args);
	}

	@SuppressWarnings("rawtypes")
	public static Object invokeMethod(Object owner, String methodName, Class[] argsClass, Object[] args) {
		try {
			Class<? extends Object> ownerClass = owner.getClass();
			// LogUtil.d(TAG, "invokeMethod :" + ownerClass.toString()+owner + " : " + methodName);
			if (null == argsClass || null == args) {
				Method method = ownerClass.getMethod(methodName);
				return method.invoke(owner);
			}
			Method method = ownerClass.getMethod(methodName, argsClass);
			return method.invoke(owner, args);
		} catch (Exception e) {
			LogUtil.e(TAG, "invokeMethod error: owner:" + owner.toString() + ";methodName:" + methodName.toString() + ";argsClass:" + argsClass.toString()
			        + ";args:" + args.toString() + "  - " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 根据类名获得类
	 * 
	 * @param className
	 * @return
	 */
	public static Class<?> getClass(String className) {
		Class<?> class1 = null;
		// TODO 没有包名暂时不行
		if (!className.contains(".")) {
			Package[] packages = Package.getPackages();
			for (Package pkg : packages) {
				String fullyClassName = pkg.getName() + "." + className;
				try {
					return Class.forName(fullyClassName);
				} catch (ClassNotFoundException e) {
					LogUtil.e(TAG, "getClass : try next package ...");
				}
			}
		}
		try {
			class1 = Class.forName(className);
		} catch (ClassNotFoundException e) {
			LogUtil.e(TAG, "getClass error: " + e.toString());
		}
		return class1;
	}

	/**
	 * String向其它格式的转换
	 * 
	 * @param widgetName
	 * @return
	 */
	public static Object StringToAny(String typeName, String str) {
		// LogUtil.d(TAG, "StringToAny:" + typeName + " - " + str);
		Method m;
		Object o = null;
		try {
			m = StringUtil.class.getDeclaredMethod("stringTo" + typeName, String.class);
			m.setAccessible(true); // if security settings allow this
			o = m.invoke(null, str);
		} catch (NoSuchMethodException e) {
			LogUtil.e(TAG, "NoSuchMethodException:" + e.toString());
		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "IllegalArgumentException:" + e.toString());
		} catch (IllegalAccessException e) {
			LogUtil.e(TAG, "IllegalAccessException:" + e.toString());
		} catch (InvocationTargetException e) {
			LogUtil.e(TAG, "InvocationTargetException:" + e.toString());
		} // use null if the method is static
		return o;
	}

	@SuppressWarnings("rawtypes")
	public static Class getAnyClass(String typeName) throws ClassNotFoundException {
		// LogUtil.d(TAG, "getAnyClass:" + typeName);
		Method m;
		Object o = null;
		try {
			m = StringUtil.class.getDeclaredMethod("class" + typeName);
			m.setAccessible(true); // if security settings allow this
			o = m.invoke(null);
		} catch (NoSuchMethodException e) {
			LogUtil.e(TAG, "NoSuchMethodException:" + e.toString());
		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "IllegalArgumentException:" + e.toString());
		} catch (IllegalAccessException e) {
			LogUtil.e(TAG, "IllegalAccessException:" + e.toString());
		} catch (InvocationTargetException e) {
			LogUtil.e(TAG, "InvocationTargetException:" + e.toString());
		}
		return (Class) o;
	}
	/**
	 * 反射 获得 数据模型的 数据
	 * 
	 * @param dataModel
	 * @param getString
	 * @return
	 */
	public static Object getModelData(Object dataModel, String getString) {
		if (null == dataModel)
			return null;
		if (StringUtil.isEmpty(getString)) {
			return dataModel;
		}
		// LogUtil.d(TAG, "getModelData : dataModel = " + dataModel.toString());
		Object value = null;
		int position = -1;
		if (getString.contains("[") && getString.contains("]")) {
			Pattern p1 = Pattern.compile("\\((\\d*)\\)");
			Matcher m1 = p1.matcher(getString);
			if (m1.find()) {
				position = Integer.parseInt(StringUtil.slim(m1.group(0)));
			}
			getString = getString.substring(0, getString.indexOf("["));
		}
		getString = "get" + StringUtil.getFeatureString(getString);
		try {
			// LogUtil.d(TAG, "getModelData getString =" + getString);
			value = invokeMethod(dataModel, getString, null);
			if (value instanceof List || value instanceof ArrayList) {
				// LogUtil.d(TAG, "getModelData : now it is list");
				// value = ((List)value).get(position);
				Method m1 = value.getClass().getMethod("get", int.class);
				value = m1.invoke(value, position);
				// LogUtil.d(TAG, "getModelData : value = " + value);
			}

		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "getEventParamValue error: IllegalArgumentException");
		} catch (NoSuchMethodException e) {
			LogUtil.e(TAG, "getEventParamValue error: NoSuchMethodException");
		} catch (IllegalAccessException e) {
			LogUtil.e(TAG, "getEventParamValue error: IllegalAccessException");
		} catch (InvocationTargetException e) {
			LogUtil.e(TAG, "getEventParamValue error: InvocationTargetException");
		} catch (Exception e) {
			LogUtil.e(TAG, "getEventParamValue error: unknown");
		}
		return value;
	}
	/**
	 * 根据名称和类找到该类的接口
	 * 
	 * @param c
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getInterface(Class<?> c, String interfaceName) {
		Class<?>[] interfList = null;
		try {
			interfList = c.getInterfaces();
		} catch (Exception e) {
			LogUtil.e(TAG, "getInterface error: " + e.toString());
		}
		if (interfList != null && interfList.length > 0) {
			for (Class<?> class1 : interfList) {
				if (class1.getSimpleName().equalsIgnoreCase(interfaceName)) {
					return class1;
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * @param uriString
	 */
	public static void handleProyocalURI(String uriString) {
		EventBus.getDefault().post(uriString);
		// URI uri = null;
		// try {
		// uri = new URI(uriString);
		// } catch (URISyntaxException e) {
		// LogUtil.e(TAG, "handleProyocalURI error: "+e.toString());
		// }
		// if (null != uri) {
		// String proto = uri.getScheme();
		// String control = uri.getAuthority();
		// String command = uri.getPath();
		// String query = uri.getQuery();
		// LogUtil.d(TAG, "handleProyocalURI : query = "+query+", proto = "+proto+", control = "+control+", command = "+command);
		//
		// }
	}

}
