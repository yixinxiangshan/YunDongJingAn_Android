package com.ecloudiot.framework.utility;

import java.util.Observable;
import java.util.Observer;
import java.util.WeakHashMap;

public class MessageUtil extends Observable {
	private final static String TAG = "MessageUtil";
	private static MessageUtil instance;
	private WeakHashMap<String, Observer> observersMap;

	public static MessageUtil instance() {
		if (instance == null) {
			instance = new MessageUtil();
		}
		return instance;
	}

	public void addObserver(String key, Observer observer) {
		if (observersMap == null) {
			observersMap = new WeakHashMap<String, Observer>();
		}
		observersMap.put(key, observer);
		addObserver(observer);
	}
	
	public void notifyObserver(final Observer observer, final Object data) {
		if (observer == null) {
			LogUtil.w(TAG, "notifyObserver is null ...");
			return;
		}
		IntentUtil.getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setChanged();
				observer.update(MessageUtil.this, data);
			}
		});
	}
	
	public void sendMessage(String observerName, Object data) {
		LogUtil.d(TAG, "sendMessage : observerName = "+observerName+" , data = "+data);
		MessageData msgData = new MessageData();
		msgData.setData(data);
		if (StringUtil.isEmpty(observerName)) {
			notifyObservers(msgData);
			return;
		}
		if (observersMap == null) {
			return;
		}
		msgData.setName(observerName);
		Observer observer = observersMap.get(observerName);
		LogUtil.d(TAG, "sendMessage : observer = "+observer+" , data = "+data);
		notifyObserver(observer, msgData);
	}

	public void sendMessage(Object data) {
		setChanged();
		sendMessage("",data);
	}
	
	public synchronized void deleteObserver(String observerName) {
		if (StringUtil.isEmpty(observerName)) {
			return;
		}
		Observer observer = observersMap.get(observerName);
		if (observer!=null) {
			deleteObserver(observer);
			observersMap.remove(observerName);
		}
	}

	public class MessageData{
		private String name;
		private Object data;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
	}
	
	

}
