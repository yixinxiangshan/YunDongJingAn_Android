package com.ecloudiot.framework.utility;

import java.lang.reflect.Constructor;

import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.BaseWidgetConfigSetEventModel;

public class EventUtil {
	private final static String TAG = "EventUtil";
	public static final int EVENTPAGE = 1;
	public static final int EVENTWIDGET = 2;

	/**
	 * 设置事件监听器
	 * 
	 * @param pageContext
	 * @param widget
	 * @param eventModel
	 * @param EVENTLEVEL
	 */
	public static void setEvent(Object pageContext, Object widget, BaseWidgetConfigSetEventModel eventModel,
			int EVENTLEVEL) {
		String eventName = eventModel.getName();
		if (StringUtil.isEmpty(eventName)) {
			LogUtil.e(TAG, "setEventWidget error: eventName is null...");
			return;
		}
		String eventConfigString = GsonUtil.toJson(eventModel);
		String listenerClassString = StringUtil.getFeatureString(eventName) + "Listener";
		String listenerInterfaceString = "On" + listenerClassString;
		String listenerMethodString = "set" + listenerInterfaceString;
		LogUtil.d(TAG, "setEventWidget , listenerClass = " + listenerClassString + " , listenerInterface = "
				+ listenerInterfaceString);
		Class<?> listenerClass = ReflectionUtil.getClass("com.ecloudiot.framework.event.listener."
				+ listenerClassString);
		Class<?> listenerInterface = ReflectionUtil.getInterface(listenerClass, listenerInterfaceString);
		Object listener = null;
		try {
			Constructor<?> c = listenerClass.getConstructor(new Class[] { Object.class, Object.class, String.class });
			listener = c.newInstance(new Object[] { pageContext, widget, eventConfigString });
		} catch (Exception e) {
			LogUtil.e(TAG, "setEventWidget error: " + e.toString());
		}
		switch (EVENTLEVEL) {
		case EVENTPAGE:
			ReflectionUtil.invokeMethod(pageContext, listenerMethodString, new Class[] { listenerInterface },
					new Object[] { listener });
			break;
		case EVENTWIDGET:
			if (StringUtil.isNotEmpty(eventModel.getId())) {
				ReflectionUtil.invokeMethod(widget, listenerMethodString,
						new Class[] { listenerInterface, String.class }, new Object[] { listener, eventModel.getId() });
			} else {
				ReflectionUtil.invokeMethod(widget, listenerMethodString, new Class[] { listenerInterface },
						new Object[] { listener });
			}
			break;
		default:
			break;
		}
	}

}
