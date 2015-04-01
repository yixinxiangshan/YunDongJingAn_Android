package com.ecloudiot.framework.javascript;

import com.ecloudiot.framework.utility.Constants;
/**
 * Js解析引擎控制器，当在调试状态时使用JV8，生产模式使用J2V8<br>
 * J2V8做到了资源释放，及页面销毁后不再执行回调<br>
 * JV8只做了资源释放，未做页面销毁后不再执行回调<br>
 * @author Lee
 *
 */
public class JsEngine {
	private  int CURRENT_PROVIDER;
	private JsEngineProvieder provieder;

	public JsEngine(String scriptString, Object parentObject) {
		if (Constants.DEBUG) {
			 CURRENT_PROVIDER = Constants.JS_ENGINE_JV8;
        }else {
			CURRENT_PROVIDER = Constants.JS_ENGINE_JV8;
		}
		
		switch (CURRENT_PROVIDER) {
			//JV8
			case Constants.JS_ENGINE_JV8 :
				JsAPI.instance().addJsMaker( this.hashCode() + "", this);
				provieder = new JsMakerV8(parentObject, this.hashCode());
				provieder.jsEngineStart(scriptString);
				break;
			//J2V8	
			case Constants.JS_ENGINE_J2V8 :
				JsAPI.instance().addJsMaker( this.hashCode() + "", this);
				provieder = new JsMakerJ2V8(parentObject, this.hashCode());
				provieder.jsEngineStart(scriptString);
				break;
			//如果未来版本不出问题，将会永久去除Rhino
			case Constants.JS_ENGINE_RIHNO :
				break;
			default :
				break;
		}
	}

	public String callback(String callback_id, Object args) {
		return provieder.callback(callback_id, args);
	}
	
	public void release(){
		provieder.release();
		provieder.setViewHasGone(true);
	}

	public Object getParent() {
		return provieder.getParent();
	}
}
