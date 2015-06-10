package com.ecloudiot.framework.widget.listview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.JsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.view.CountDownButtonHelper;
import com.ecloudiot.framework.view.CountDownButtonHelper.OnFinishListener;
import com.google.gson.JsonObject;

@SuppressLint("HandlerLeak")
public class ListViewCellInputText extends ListViewCellBase {

	private static String TAG = ListViewCellInputText.class.getSimpleName();
	public ListViewCellInputText(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);

	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		final DataModel data;
		final ViewHolder holder;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
//		if (convertView == null) {
		convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_input_text, null);
		holder = new ViewHolder();
		holder.editText = (EditText) convertView.findViewById(R.id.widget_listviewcell_input_text_edittext);
		holder.btn = (Button) convertView.findViewById(R.id.widget_listviewcell_input_text_btn);
		convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}

		setHint(holder.editText, data.hint);
		setlines(holder.editText, data.lines);
		holder.editText.setTextSize(data.textSize);

		// if (!StringUtil.isEmpty(data.inputText)) {
		holder.editText.setText(getListViewBaseAdapter().getInputMap().get(data.name));
        if (position == 0) {
            setEnable(holder.editText, data.enable);
        }
        // holder.editText.setSelection(data.inputText.length());
		// holder.editText.setMovementMethod(ScrollingMovementMethod.getInstance());
		getListViewBaseAdapter().addInputMap(data.name, holder.editText.getText().toString());
		// }

		if (data.inputType.equals("number")) {
			holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (data.inputType.equals("phone")) {
			TelephonyManager telephonyManager = (TelephonyManager) ECApplication.getInstance().getNowActivity().getSystemService(Context.TELEPHONY_SERVICE);;
			String nativePhoneNumber = telephonyManager.getLine1Number();
			if (!StringUtil.isEmpty(nativePhoneNumber)) {
				if (nativePhoneNumber.startsWith("+")) {
					nativePhoneNumber = nativePhoneNumber.substring(3);
				}
				holder.editText.setText(nativePhoneNumber);
				getListViewBaseAdapter().addInputMap(data.name, holder.editText.getText().toString());
				// LogUtil.d(TAG, getListViewBaseAdapter().getInputMap().toString());
			}
			holder.editText.setInputType(InputType.TYPE_CLASS_PHONE);
		} else if (data.inputType.equals("email")) {
			holder.editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		} else if (data.inputType.equals("password")) {
			holder.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else if (data.inputType.equals("text")) {
			holder.editText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		setlines(holder.editText, data.lines);

		holder.editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// if (!StringUtil.isEmpty(data.btnName)) {
				// if (holder.editText.getText().toString().trim().length() != 11) {
				// holder.btn.setEnabled(false);
				// holder.btn.setClickable(false);
				// } else {
				// holder.btn.setClickable(true);
				// holder.btn.setEnabled(true);
				// }
				// }
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				getListViewBaseAdapter().addInputMap(data.name, holder.editText.getText().toString());
//				LogUtil.d(TAG, getListViewBaseAdapter().getInputMap().toString());
			}
		});
		if ((!StringUtil.isEmpty(data.type)) && data.type.equals("captcha")) {
			// 验证码
			String SMS_URI_INBOX = "content://sms/inbox";
			Handler smsHandler = new Handler() {
				// 这里可以进行回调的操作
			};
			SmsObserver smsObserver = new SmsObserver(getContext(), smsHandler, holder.editText);
			getContext().getContentResolver().registerContentObserver(Uri.parse(SMS_URI_INBOX), true, smsObserver);
		}

		if (!StringUtil.isEmpty(data.btnName)) {
			holder.btn.setVisibility(View.VISIBLE);
			holder.btn.setText(data.btnName);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("target", "button");
			map.put("position", position);
			holder.btn.setTag(map);

			holder.btn.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {

					CountDownButtonHelper helper = new CountDownButtonHelper(holder.btn, "", 60, 1);
					helper.setOnFinishListener(new OnFinishListener() {
						@Override
						public void finish() {
							holder.btn.setText("验证");
							// Toast.makeText(MainActivity.this, "倒计时结束", Toast.LENGTH_SHORT).show();
						}
					});
					helper.start();

					LogUtil.d(TAG, getSmsFromPhone().toString());
					HashMap<String, Object> eventParams = new HashMap<String, Object>();
					HashMap<String, Object> map = new HashMap<String, Object>();
					map = (HashMap<String, Object>) v.getTag();
					eventParams.put("target", map.get("target").toString());
					eventParams.put("position", "" + map.get("position"));
					eventParams.put("_form", new JSONObject(getListViewBaseAdapter().getInputMap()));
					JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				}
			});
		} else {
			holder.btn.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void setlines(EditText editText, int num) {
		if (num != 0) {
			editText.setLines(num);
		}
	}
	public void setHint(EditText editText, String hint) {
		if (!StringUtil.isEmpty(hint)) {
			editText.setHint(hint);
		}
	}
	public void setEnable(EditText editText, boolean enable) {
		if (enable) {
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
			editText.requestFocus();
		} else {
			editText.setFocusable(false);
			editText.setFocusableInTouchMode(false);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public String getSmsFromPhone() {
		// final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		// final String SMS_URI_SEND = "content://sms/sent";
		// final String SMS_URI_DRAFT = "content://sms/draft";

		StringBuilder smsBuilder = new StringBuilder();
		StringBuilder smbBody = new StringBuilder();
		try {
			ContentResolver cr = getContext().getContentResolver();
			String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
			Uri uri = Uri.parse(SMS_URI_INBOX);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");

			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;

				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				do {
					name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					smsbody = cur.getString(smsbodyColumn);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);

					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "接收";
					} else if (typeId == 2) {
						type = "发送";
					} else {
						type = "";
					}

					smsBuilder.append("[");
					smsBuilder.append(name + ",");
					smsBuilder.append(phoneNumber + ",");
					smsBuilder.append(smsbody + ",");
					smbBody.append(smsbody);
					smsBuilder.append(date + ",");
					smsBuilder.append(type);
					smsBuilder.append("] ");

					if (smsbody == null)
						smsbody = "";
				} while (false);
				// } while (cur.moveToNext());
			} else {
				smsBuilder.append("no result!");
			}
			smsBuilder.append("getSmsInPhone has executed!");
		} catch (SQLiteException ex) {
			LogUtil.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		return smbBody.toString();
	}

	class SmsObserver extends ContentObserver {
		EditText editText;
		public SmsObserver(Context context, Handler handler, EditText editText) {
			super(handler);
			this.editText = editText;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 每当有新短信到来时，使用我们获取短消息的方法
			// getSmsInPhone();
			// getSmsFromPhone().
			String a = getSmsFromPhone();
			Pattern p = Pattern.compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + 6 + "})(?![a-zA-Z0-9])");
			Matcher m = p.matcher(a);
			if (m.find()) {
				editText.setText(m.group(0));
				// LogUtil.d("LocationActivity", getSmsInPhone().toString());
			}

		}
	}

	/**
	 * holder
	 */
	static class ViewHolder {
		EditText editText;
		Button btn;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String inputType;
		private String inputText;
		private String type;
		private int textSize = 22;
		private String name;
		private String hint;
		private String btnName;
		private int lines = 1;
		private boolean enable = true;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getLines() {
			return lines;
		}

		public void setLines(int lines) {
			this.lines = lines;
		}

		public String getHint() {
			return hint;
		}

		public void setHint(String hint) {
			this.hint = hint;
		}

		public String getInputType() {
			return inputType;
		}

		public void setInputType(String inputType) {
			this.inputType = inputType;
		}

		public String getBtnName() {
			return btnName;
		}

		public void setBtnName(String btnName) {
			this.btnName = btnName;
		}

		public String getInputText() {
			return inputText;
		}

		public void setInputText(String inputText) {
			this.inputText = inputText;
		}

	}
}
