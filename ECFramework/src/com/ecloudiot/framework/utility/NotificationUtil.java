package com.ecloudiot.framework.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ecloudiot.framework.activity.NotiActivity;
import com.ecloudiot.framework.widget.model.NotificationMedol;

public class NotificationUtil {
	private static String TAG = "NotificationUtil";
	public static String NOTI_ACTIVITY_TAG = "notiActivityTag";

	/**
	 * 创建并显示notification
	 * 
	 * @param notificationId
	 * @param icon
	 *            消息图片的id
	 * @param tickertext
	 *            提示信息文字
	 * @param title
	 *            notification打开的后title
	 * @param content
	 *            notification打开后的内容
	 */

	public static void showNotification(Context context, NotificationMedol dataItem) {
		// NotificationMedol dataItem = GsonParsing.fromJson(dataString, NotificationMedol.class);
		LogUtil.d(TAG, "showNotification start ..");
		if (dataItem == null) {
			LogUtil.e(TAG, "dataItem 错误");
			return;
		}
		int iconId = -1;
		if (StringUtil.isNotEmpty(dataItem.getIconName())) {
			iconId = ResourceUtil.getDrawableIdFromContext(context, dataItem.getIconName());
		}

		if (iconId > 0) {
			LogUtil.d(TAG, "showNotification");
			Bundle bundle = new Bundle();
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// Context mContext = null;
			// if (ECApplication.getInstance().GetNowActivity()!=null) {
			// mContext = ECApplication.getInstance().GetNowActivity();
			// }else {
			// mContext = context;
			// }
			// Notification的Intent，即点击后转向的Activity
			Intent notificationIntent = new Intent(context, NotiActivity.class);
			bundle.putString(NOTI_ACTIVITY_TAG, dataItem.getNotiActivityTag());
			LogUtil.d(TAG, dataItem.getNotiActivityTag());
			notificationIntent.putExtras(bundle);
			// PendingIntent的Flags为0代表该PendingIntent不带数据.PendingIntent.FLAG_UPDATE_CURRENT Extra会被更新为最后一个传入的Intent的Extra
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			// 创建Notifcation
			Notification notification = new Notification(iconId, dataItem.getTitle(), System.currentTimeMillis());
			// 设定Notification出现时的声音，一般不建议自定义
			notification.defaults |= Notification.DEFAULT_SOUND;
			// 设定如何振动
			// notification.defaults |= Notification.DEFAULT_VIBRATE;
			// 指定Flag，Notification.FLAG_AUTO_CANCEL意指点击这个Notification后，立刻取消自身
			// 这符合一般的Notification的运作规范
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, dataItem.getTitle(), dataItem.getContent(), contentIntent);

			// notification.setLatestEventInfo(this, "title", "content", contentIntent);
			// 显示这个notification
			mNotificationManager.notify(dataItem.getNotificationId(), notification);
		} else {
			LogUtil.e(TAG, "notification params error!");
		}
	}
}
