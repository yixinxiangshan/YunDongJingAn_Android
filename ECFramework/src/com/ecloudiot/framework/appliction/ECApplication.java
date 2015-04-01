package com.ecloudiot.framework.appliction;
import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.utility.Constants;
import com.pgyersdk.crash.PgyCrashManager;

public class ECApplication extends BaseApplication {
	public void onCreate() {
		super.onCreate();
		//蒲公英内测
		if(Constants.INNER_TEST){
			PgyCrashManager.register(this,Constants.PGY_APP_KEY);
		}
	}
}
