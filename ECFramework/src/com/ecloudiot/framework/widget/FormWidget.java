package com.ecloudiot.framework.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.event.linterface.OnButtonClickListener;
import com.ecloudiot.framework.event.linterface.OnGroupItemClickListener;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.FilePath;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.MessageUtil;
import com.ecloudiot.framework.utility.MessageUtil.MessageData;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.view.ImageUploadView;
import com.ecloudiot.framework.view.SecondaryMenuView;
import com.ecloudiot.framework.view.VoteView;
import com.ecloudiot.framework.view.ImageUploadView.OnRemoveListener;
import com.ecloudiot.framework.view.VoteView.OnVoteListener;
import com.ecloudiot.framework.widget.model.FormHelperModel;
import com.ecloudiot.framework.widget.model.FormInputModel;
import com.ecloudiot.framework.widget.model.FormModel;
import com.ecloudiot.framework.widget.model.FormSelectOptionModel;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel.MenuItemModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint({"ViewConstructor", "NewApi", "InflateParams"})
public class FormWidget extends BaseWidget implements Observer {
	private final static String TAG = "FormWidget";
	private FormModel widgetDataModel;
	private Calendar cal = Calendar.getInstance();
	private View uploadView;
	private String bitmapDir;
	private LinkedHashMap<String, String> formMap;
	private ArrayList<FilePath> filePaths;
	private boolean refresh = false;
	private final int UPLOADS_KEY_BASE = 10000;
	// 控件属性
	private Integer padding;
	private String textInputBackgroud;

	public FormWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.form_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_form");
		}
	}

	// 设置数据
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, FormModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
		}
	}

	public void putWidgetData(String widgetDataString) {
		LogUtil.i(TAG, "FormWidget : padding = " + padding);
		super.putWidgetData(widgetDataString);
	}

	protected void setData() {
		View view;
		if (refresh) {
			this.getBaseView().removeAllViews();
		}
		view = LayoutInflater.from(this.ctx).inflate(R.layout.widget_form, null);
		this.getBaseView().addView(view);
		// title
		TextView title = (TextView) getBaseView().findViewById(R.id.widget_form_title);
		if (!this.widgetDataModel.getTitle().equals("")) {
			title.setText(this.widgetDataModel.getTitle());
			title.setVisibility(View.VISIBLE);
		}
		// 容器
		LinearLayout nextbtn = (LinearLayout) getBaseView().findViewById(R.id.widget_form_submitsection_nextbtn);
		LinearLayout submit = (LinearLayout) getBaseView().findViewById(R.id.widget_form_submitsection_submitbtn);
		LinearLayout itemsection = (LinearLayout) getBaseView().findViewById(R.id.widget_form_itemsection);
		if (getPadding() != null) {
			nextbtn.setPadding(getPadding(), getPadding(), getPadding(), getPadding());
			submit.setPadding(getPadding(), getPadding(), getPadding(), getPadding());
			itemsection.setPadding(getPadding(), getPadding(), getPadding(), getPadding());
		}
		if (!this.widgetDataModel.getButton_next().equals("")) {
			view = LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_button_next, nextbtn, false);
			nextbtn.addView(view);
			Button button = (Button) getBaseView().findViewById(R.id.widget_form_button_next);
			button.setText(this.widgetDataModel.getButton_next());
			button.setOnClickListener(new ClickListener());
		}
		if (!this.widgetDataModel.getButton_submit().equals("")) {
			view = LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_button_submit, submit, false);
			submit.addView(view);
			Button button = (Button) getBaseView().findViewById(R.id.widget_form_button_submit);
			button.setText(this.widgetDataModel.getButton_submit());
			button.setOnClickListener(new ClickListener());
		}
		if (!this.widgetDataModel.getButton_cancel().equals("")) {
			view = LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_button_cancel, submit, false);
			submit.addView(view);
			Button button = (Button) getBaseView().findViewById(R.id.widget_form_button_cancel);
			button.setText(this.widgetDataModel.getButton_cancel());
			button.setOnClickListener(new ClickListener());
		}
		// helper
		LinearLayout helper = (LinearLayout) getBaseView().findViewById(R.id.widget_form_submitsection_helper);
		List<FormHelperModel> helplist = (List<FormHelperModel>) this.widgetDataModel.getHelper_list();
		if (null != helplist) {
			for (int i = 0; i < helplist.size(); i++) {
				TextView helperlable = (TextView) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_clicklable, null);
				helper.addView(helperlable);
				helperlable.setText(helplist.get(i).getText());
			}
			super.setData();
		}

		// 显示inputs

		List<FormInputModel> inputlist = (List<FormInputModel>) this.widgetDataModel.getInput_list();

		for (int i = 0; i < inputlist.size(); i++) {
			FormInputModel inputModel = inputlist.get(i);
			// 保存默认值
			String defalutValue = inputModel.getDefault_value();
			String inputType = inputModel.getDefault_layout();
			if (StringUtil.isNotEmpty(defalutValue) && !inputType.equalsIgnoreCase("upload") && !inputType.equalsIgnoreCase("uploads")) {
				LogUtil.d(TAG, "setData : defalutValue = " + defalutValue);
				saveFormData(inputModel.getName(), inputModel.getDefault_value());
			}
			final ArrayList<String> textType = new ArrayList<String>() {
				private static final long serialVersionUID = 1386900352116551911L;
				{
					add("text");
					add("number");
					add("password");
					add("textarea");
					add("date");
				}
			};
			if (textType.contains(inputType)) {
				this.showTextInput(itemsection, inputModel);
			} else if (inputType.equals("label")) {
				this.showLabelInput(itemsection, inputModel);
			} else if (inputType.equals("checkbox")) {
				this.showCheckboxInput(itemsection, inputModel);
			} else if (inputType.equals("radio")) {
				this.showRadioInput(itemsection, inputModel);
			} else if (inputType.equals("select")) {
				this.showSelectInput(itemsection, inputModel);
			} else if (inputType.equals("select2")) {
				this.showSelectSecondaryInput(itemsection, inputModel);
			} else if (inputType.equals("vote")) {
				this.showVoteInput(itemsection, inputModel);
			} else if (inputType.equals("upload")) {
				// 显示上传文件文本框
				this.showUploadInput(itemsection, inputModel);
			} else if (inputType.equals("uploads")) {
				// 显示上传图片文本框
				this.showUploadsInput(itemsection, inputModel);
			} else if (inputType.equals("tag_textarea")) {
				// 显示 标签+文本域组合
				this.showTagTextareaInput(itemsection, inputModel);
			}
		}
	}
	// 标签+文本域
	private void showTagTextareaInput(LinearLayout itemsection, FormInputModel inputModel) {
		// 添加文本输入域
		final EditText editText = (EditText) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_text, itemsection, false);
		itemsection.addView(editText);
		// 添加单选菜单
		RadioGroup radio = (RadioGroup) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_radio, itemsection, false);
		itemsection.addView(radio);
		List<FormSelectOptionModel> options = inputModel.getSelectlist().getOptions();
		for (int i = 0; i < options.size(); i++) {
			RadioButton btnview = new RadioButton(this.ctx);
			btnview.setTextColor(Color.BLACK);
			radio.addView(btnview);
			btnview.setText(options.get(i).getText());
			btnview.setTag(options.get(i).getValue());
		}

		editText.setSelection(0);
		editText.setCursorVisible(true);
		editText.setHint(inputModel.getBackground_wrods());
		editText.setTag(inputModel.getName());
		// 显示描述内容
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
		// 设置输入框背景
		if (StringUtil.isNotEmpty(getTextInputBackgroud())) {
			editText.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, getTextInputBackgroud()));
		}
		// 设置默认值
		String defaultValue = inputModel.getDefault_value();
		if (StringUtil.isNotEmpty(defaultValue)) {
			editText.setText(defaultValue);
		}
		// 输入完毕模拟提交事件
		if (inputModel.isSimulateConfirm()) {
			simulateSubmitBt(editText);
		}
		// 设置输入类型
		editText.setMinLines(6);
		editText.setSingleLine(false); // 多行输入 换行
		setItemSelected(editText, "TagTextarea");

		((RadioGroup) radio).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				RadioButton btnButton = (RadioButton) findViewById(checkedId);
				String text = editText.getText().toString();
				// 恢复情况的切换 切换不覆盖用户输入的内容
				if (text.contains("\n恢复情况")) {
					text = text.substring(0, text.lastIndexOf("\n恢复情况"));
				}
				editText.setText(text + "\n恢复情况：" + btnButton.getText() + "。");

				editText.setSelection(editText.getText().toString().length());
			}

		});

	}

	private void showUploadsInput(LinearLayout itemsection, FormInputModel inputModel) {
		HorizontalScrollView view = (HorizontalScrollView) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_uploads, itemsection, false);
		itemsection.addView(view);
		ImageView imageView = (ImageView) view.findViewById(R.id.widget_form_uploads_image);
		LinearLayout imagesContainer = (LinearLayout) view.findViewById(R.id.widget_form_uploads_container);
		imagesContainer.setTag(inputModel.getName());
		String defaultValue = inputModel.getDefault_value();
		if (StringUtil.isNotEmpty(defaultValue)) {
			List<String> netFilePaths = new ArrayList<String>();
			if (defaultValue.contains(",")) {
				String[] ss = defaultValue.split(",");
				netFilePaths = Arrays.asList(ss);
			} else {
				netFilePaths.add(defaultValue);
			}
			if (filePaths == null) {
				filePaths = new ArrayList<FilePath>();
			}
			for (String netFilePath : netFilePaths) {
				if (StringUtil.isImageName(netFilePath)) {
					LogUtil.d(TAG, "showUploadsInput :  = " + netFilePath);
					FilePath filePath = new FilePath();
					filePath.setNetFilePaths(netFilePath);
					filePaths.add(filePath);
				}
			}
			uploads();
		}
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtil.openCameras(filePaths);
				MessageUtil.instance().addObserver(Constants.MESSAGE_TAG_CAMERA_ACTIVITY, FormWidget.this);
			}
		});
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	// 上传文件文本框
	private void showUploadInput(LinearLayout itemsection, FormInputModel inputModel) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_upload, itemsection, false);
		itemsection.addView(view);
		TextView textView = (TextView) view.findViewById(R.id.widget_form_upload_text);
		if (StringUtil.isNotEmpty(inputModel.getText())) {
			textView.setText(inputModel.getText());
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.widget_form_upload_image);
		// view.setHint(inputModel.getBackground_wrods());
		String defaultValue = inputModel.getDefault_value();
		if (StringUtil.isNotEmpty(defaultValue)) {
			ImageLoader.getInstance().displayImage(URLUtil.getSImageWholeUrl(defaultValue), imageView);
		}
		imageView.setTag(inputModel.getName());
		setItemSelected(imageView, "Upload");
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	// 下拉单选
	private void showSelectInput(LinearLayout itemsection, FormInputModel inputModel) {
		Spinner view = (Spinner) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_select, itemsection, false);
		itemsection.addView(view);
		List<FormSelectOptionModel> options = inputModel.getSelectlist().getOptions();
		// 添加请选择字样
		String defaultwordString = inputModel.getBackground_wrods();
		if (defaultwordString.equals("")) {
			FormSelectOptionModel opt = new FormSelectOptionModel();
			opt.setText(defaultwordString);
			opt.setValue("-1");
			options.add(opt);
		}
		ArrayAdapter<FormSelectOptionModel> adapter = new ArrayAdapter<FormSelectOptionModel>(this.ctx, R.layout.widget_form_input_selectitem, options);
		view.setAdapter(adapter);
		view.setSelection(options.size() - 1);
		view.setTag(inputModel.getName());
		setItemSelected(view, "Spinner");
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	private void showSelectSecondaryInput(LinearLayout itemsection, final FormInputModel inputModel) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input, itemsection, false);
		itemsection.addView(view);
		final TextView textView = (TextView) view.findViewById(R.id.widget_form_input_tv);
		final SecondaryMenuModel secondaryMenuModel = new SecondaryMenuModel();
		secondaryMenuModel.setMenulList(optionsToSecondarysModel(inputModel.getSelectlist().getOptions()));
		if (StringUtil.isEmpty(inputModel.getDefault_value())) {
			textView.setText(secondaryMenuModel.getSubMenu(0).get(0).getTitle());
		} else {
			if (!inputModel.getDefault_value().contains(":")) {
				int i = 0;
				for (FormSelectOptionModel item : inputModel.getSelectlist().getOptions()) {
					if (item.getOptions() != null) {
						int j = 0;
						for (FormSelectOptionModel item2 : item.getOptions()) {
							LogUtil.d(TAG, "value = " + item2.getValue() + " , Default_value = " + inputModel.getDefault_value());
							if (item2.getValue().equalsIgnoreCase(inputModel.getDefault_value())) {
								inputModel.setDefault_value(i + ":" + j);
								textView.setText(item2.getText());
								break;
							}
							j++;
						}
					}
					i++;
				}
			}
			if (!inputModel.getDefault_value().contains(":"))
				inputModel.setDefault_value("0:0");
		}
		final SecondaryMenuView secondaryMenuView = (SecondaryMenuView) LayoutInflater.from(ctx).inflate(R.layout.widget_form_input_select_secondary, this,
		        false);
		secondaryMenuView.setData(GsonUtil.toJson(secondaryMenuModel));
		secondaryMenuView.setOnGroupItemClickListener(new OnGroupItemClickListener() {
			@Override
			public void onGroupItemClick(int groupId, int position) {
				String text = secondaryMenuModel.getSubMenu(groupId).get(position).getTitle();
				textView.setText(text);
				saveFormData(inputModel.getName(), secondaryMenuModel.getSubMenu(groupId).get(position).getId());
				inputModel.setDefault_value(groupId + ":" + position);
				ViewUtil.closeDialog();
			}
		});
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isEmpty(inputModel.getDefault_value()) || !inputModel.getDefault_value().contains(":")) {
					inputModel.setDefault_value("0:0");
				}
				String defaultString = inputModel.getDefault_value();
				int index = defaultString.indexOf(":");
				secondaryMenuView.setPrimeDefaultSelection(Integer.parseInt(defaultString.substring(0, index)));
				secondaryMenuView.setMinorDefaultSelection(Integer.parseInt(defaultString.substring(index + 1)));
				ViewUtil.openCustomDialog(secondaryMenuView, false, false);
			}
		});
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	private void showVoteInput(LinearLayout itemsection, FormInputModel inputModel) {
		VoteView voteView = (VoteView) LayoutInflater.from(ctx).inflate(R.layout.widget_form_input_vote, itemsection, false);
		itemsection.addView(voteView);
		if (StringUtil.isNotEmpty(inputModel.getDefault_value())) {
			try {
				int score = Integer.parseInt(inputModel.getDefault_value());
				voteView.setScore(score);
			} catch (NumberFormatException e) {
				LogUtil.e(TAG, "showVoteInput error: " + e.toString());
			}
		}
		voteView.setTag(inputModel.getName());
		setItemSelected(voteView, "Vote");
	}

	// 不能输入
	private void showLabelInput(LinearLayout itemsection, FormInputModel inputModel) {
		TextView view = (TextView) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_lable, itemsection, false);
		itemsection.addView(view);
		view.setText(inputModel.getText());
		view.setTag(inputModel.getName());
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	// checkbox ------ 还没用到，没做
	private void showCheckboxInput(LinearLayout itemsection, FormInputModel inputModel) {
		CheckBox view = (CheckBox) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_checkbox, itemsection, false);
		itemsection.addView(view);
		view.setText(inputModel.getText());
		view.setTag(inputModel.getName());
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	// 单选
	private void showRadioInput(LinearLayout itemsection, FormInputModel inputModel) {
		RadioGroup view = (RadioGroup) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_radio, itemsection, false);
		itemsection.addView(view);
		List<FormSelectOptionModel> options = inputModel.getSelectlist().getOptions();
		if (inputModel.getDefault_value().equalsIgnoreCase("true")) {
			inputModel.setDefault_value("1");
		} else if (inputModel.getDefault_value().equalsIgnoreCase("false")) {
			inputModel.setDefault_value("0");
		}
		for (int i = 0; i < options.size(); i++) {
			RadioButton btnview = new RadioButton(this.ctx);
			btnview.setTextColor(Color.BLACK);
			view.addView(btnview);
			btnview.setText(options.get(i).getText());
			btnview.setTag(options.get(i).getValue());
			if (inputModel.getDefault_value().equalsIgnoreCase(options.get(i).getValue())) {
				btnview.setChecked(true);
			}
		}
		view.setTag(inputModel.getName());
		setItemSelected(view, "Radio");
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
	}

	// text edit type
	private void showTextInput(LinearLayout itemsection, FormInputModel inputModel) {
		String inputType = inputModel.getDefault_layout();
		EditText editText = (EditText) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_input_text, itemsection, false);
		itemsection.addView(editText);
		editText.setSelection(0);
		editText.setCursorVisible(true);
		editText.setHint(inputModel.getBackground_wrods());
		editText.setTag(inputModel.getName());
		// 显示描述内容
		this.setInputDescription(itemsection, inputModel.getDes_wrods());
		// 设置输入框背景
		if (StringUtil.isNotEmpty(getTextInputBackgroud())) {
			editText.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, getTextInputBackgroud()));
		}
		// 设置默认值
		String defaultValue = inputModel.getDefault_value();
		if (StringUtil.isNotEmpty(defaultValue)) {
			editText.setText(defaultValue);
		}
		// 输入完毕模拟提交事件
		if (inputModel.isSimulateConfirm()) {
			simulateSubmitBt(editText);
		}
		// 设置输入类型
		if (inputType.equalsIgnoreCase("number")) {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (inputType.equalsIgnoreCase("password")) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else if (inputType.equalsIgnoreCase("date")) {
			editText.setInputType(InputType.TYPE_CLASS_DATETIME);
			setItemSelected(editText, "EditDate");
			if (StringUtil.isEmpty(defaultValue)) {
				updateDate(editText, Calendar.getInstance());
			} else {
				updateDate(editText, defaultValue);
			}
			return;
		} else if (inputType.equalsIgnoreCase("textarea")) {
			editText.setMinLines(6);
			updateDate(editText, defaultValue);// 解决 意见反馈的 空指针异常。（不是太好的方案）
			editText.setSingleLine(false); // 多行输入 换行
		}
		setItemSelected(editText, "EditText");
	}

	// 设置说明文字
	private void setInputDescription(LinearLayout itemsection, String description) {
		if (description.equals("")) {
			this.setInputSep(itemsection);
			return;
		}
		TextView view = (TextView) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_description, itemsection, false);
		itemsection.addView(view);
		view.setText(description);
	}

	// 设置行间距
	private void setInputSep(LinearLayout itemsection) {
		TextView view = (TextView) LayoutInflater.from(this.ctx).inflate(R.layout.widget_form_sep, itemsection, false);
		itemsection.addView(view);
	}

	// 触发选择事件
	private void setItemSelected(final View view, String type) {
		LogUtil.d(TAG, "view = " + view.toString() + "type = " + type);
		if (type == "Spinner") {
			((Spinner) view).setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					FormSelectOptionModel item = (FormSelectOptionModel) parent.getSelectedItem();
					putParam(parent.getTag().toString(), item.getValue());
					// mMap.put(parent.getTag().toString(), item.getValue());
					saveFormData(parent.getTag().toString(), item.getValue());
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		}
		if (type == "Select2") {
			final SecondaryMenuView secondaryMenuView = (SecondaryMenuView) LayoutInflater.from(ctx).inflate(R.layout.widget_form_input_select_secondary, this,
			        false);
			secondaryMenuView.setPrimeDefaultSelection(0);
			secondaryMenuView.setMinorDefaultSelection(0);
			secondaryMenuView.setData("{\"menulist\":[{\"title\":a}]}");
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtil.d(TAG, "onClick : secondaryMenuView ...");
					ViewUtil.openCustomDialog(secondaryMenuView, false, false);
				}
			});
		}
		if (type == "Radio") {
			((RadioGroup) view).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					RadioButton btnButton = (RadioButton) findViewById(checkedId);
					saveFormData((String) group.getTag(), (String) btnButton.getTag());
				}
			});
		}
		if (type == "EditText") {
			((EditText) view).addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					saveFormData((String) view.getTag(), ((EditText) view).getText().toString());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}
		if (type == "EditDate") {
			((EditText) view).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtil.d(TAG, "input changed ...");
					showDatePicker((EditText) v);
				}
			});

		}
		if (type == "Vote") {
			((VoteView) view).setOnVoteListener(new OnVoteListener() {

				@Override
				public void onVote(VoteView voteView, int score) {
					saveFormData((String) voteView.getTag(), String.valueOf(score));
				}

			});
		}
		if (type == "Upload") {
			((ImageView) view).setOnClickListener(new OnClickListener() {
				@SuppressLint("HandlerLeak")
				@Override
				public void onClick(View v) {
					ViewUtil.hideKeyboard();
					uploadView = v;
					MessageUtil.instance().addObserver(Constants.MESSAGE_TAG_CAMERA, FormWidget.this);
					MessageUtil.instance().addObserver(Constants.MESSAGE_TAG_ALBUM, FormWidget.this);
					JsViewUtility.getSysImageRes();

				}
			});
		}
		if (type == "TagTextarea") {
			((EditText) view).addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					saveFormData((String) view.getTag(), ((EditText) view).getText().toString());
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

		}
	}

	public void upload() {
		showUploadedPicture(uploadView);
		saveFormData((String) uploadView.getTag(), "file://" + bitmapDir);
		LogUtil.i(TAG, "upload : bitmapDir = " + bitmapDir);
	}

	// 设置用于修改日期的监听器
	class DateSetListener implements OnDateSetListener {
		private EditText editText;

		public DateSetListener(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDate(editText, cal);
		}
	}

	public void uploads() {
		if (filePaths == null) {
			return;
		}
		final LinearLayout view = (LinearLayout) findViewById(R.id.widget_form_uploads_container);
		saveUploadsData((String) view.getTag());
		int i = 0;
		for (FilePath filePath : filePaths) {
			ImageUploadView imageUploadView = new ImageUploadView(getContext());
			int margin = DensityUtil.dipTopx(getContext(), 5);
			LayoutParams layoutParams = new LayoutParams(DensityUtil.dipTopx(getContext(), 72), DensityUtil.dipTopx(getContext(), 72));
			layoutParams.setMargins(margin, 0, 0, 0);
			imageUploadView.setImageFile(filePath);
			view.addView(imageUploadView, i, layoutParams);
			imageUploadView.setOnRemoveListener(new OnRemoveListener() {

				@Override
				public void onRemove(ImageUploadView imageUploadView) {
					String key = (String) view.getTag();
					for (int i = 0; i < filePaths.size(); i++) {
						saveFormData(key.replace("uploads", String.valueOf(i + UPLOADS_KEY_BASE)), "");
					}
					filePaths.remove(imageUploadView.getFilePath());
					saveUploadsData(key);
				}
			});
			i++;
		}
	}

	public Bitmap zoom(Bitmap bitmap, float scaleWidth, float scaleHeight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	// EditText显示上传的图片
	public void showUploadedPicture(View v) {
		Bitmap bitmap = ImageUtil.smallBitmap(bitmapDir);
		((ImageView) v).setImageBitmap(bitmap);
	}

	// 保存表单数据至页面级参数
	private void saveFormData(String tag, String value) {
		LogUtil.d(TAG, "saveFormData : key = " + tag + " , value = " + value);
		// 保存至本地formMap
		if (formMap == null) {
			formMap = new LinkedHashMap<String, String>();
		}
		if (StringUtil.isNotEmpty(tag)) {
			formMap.put(tag, value);
		}
		JSONObject jsonObject = new JSONObject(formMap);
		String formDataString = jsonObject.toString();
		putParam("formData", formDataString);
		ReflectionUtil.invokeMethod(pageContext, "putParam", new Object[]{"formData", formDataString});
	}

	private void saveUploadsData(String key) {
		int i = 0;
		for (FilePath filePath : filePaths) {
			saveFormData(key.replace("uploads", String.valueOf(i + UPLOADS_KEY_BASE)), filePath.getNetFilePaths());
			i++;
		}
	}

	private void clearAllUploadsImages() {
		if (filePaths == null) {
			return;
		}
		LinearLayout view = (LinearLayout) findViewById(R.id.widget_form_uploads_container);
		for (int i = 0; i < filePaths.size(); i++) {
			saveFormData(((String) view.getTag()).replace("uploads", String.valueOf(i)), "");
		}
		int count = view.getChildCount() - 1;
		for (int i = 0; i < count; i++) {
			view.removeViewAt(0);
		}

	}

	@SuppressLint("SimpleDateFormat")
	public void showDatePicker(EditText editText) {
		Date date = new Date();
		if (StringUtil.isEmpty(editText.getText().toString())) {
			try {
				date = new SimpleDateFormat("yyyy-MM-dd").parse(editText.getText().toString());
			} catch (ParseException e) {
				LogUtil.e(TAG, "showDatePicker error: " + e.toString());
			}
		}
		new DatePickerDialog(this.ctx, new DateSetListener(editText), date.getYear(), date.getMonth(), date.getDate()).show();
	}

	@SuppressLint("SimpleDateFormat")
	private void updateDate(EditText editText, Calendar cal) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		updateDate(editText, simpleDateFormat.format(cal.getTime()));
	}

	private void updateDate(EditText editText, String time) {
		editText.setText(time);
		saveFormData((String) editText.getTag(), time);
	}

	private ArrayList<MenuItemModel> optionsToSecondarysModel(List<FormSelectOptionModel> options) {
		ArrayList<MenuItemModel> secondaryMenus = new ArrayList<SecondaryMenuModel.MenuItemModel>();
		SecondaryMenuModel secondaryMenuModel = new SecondaryMenuModel();
		for (FormSelectOptionModel optionModel : options) {
			MenuItemModel itemModel = secondaryMenuModel.new MenuItemModel();
			itemModel.setTitle(optionModel.getText());
			itemModel.setId(optionModel.getValue());
			if (optionModel.getOptions() != null) {
				itemModel.setMenuList(optionsToSecondarysModel(optionModel.getOptions()));
			}
			secondaryMenus.add(itemModel);
		}
		return secondaryMenus;
	}

	public FormModel getDataModel() {
		return widgetDataModel;
	}

	public void refreshData(String widgetDataString) {
		refresh = true;
		parsingWidgetData(widgetDataString);
		setData();
	}

	class ClickListener implements OnClickListener {
		OnClickListener clickListener;

		public ClickListener() {
		}

		public ClickListener(OnClickListener clickListener) {
			this.clickListener = clickListener;
		}

		public void onClick(View v) {
			ViewUtil.hideKeyboard();
			// String eventString = "onClick";
			// if (v instanceof Button) {
			// eventString = "onButtonClick";
			// }
			HashMap<String, String> eventParams = new HashMap<String, String>();
			eventParams.put("viewId", ctx.getResources().getResourceEntryName(v.getId()));
			eventParams.put("controlId", getControlId());

			// 执行v8js里面的事件，如果有返回，则返回
			String result = "";
			if (pageContext instanceof ItemActivity) {
				result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onSubmit", new JSONObject(
				        (HashMap<String, String>) GsonUtil.toHashMap(getParam("formData"))));
			} else if (pageContext instanceof ItemFragment) {
				result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onSubmit", new JSONObject(
				        (HashMap<String, String>) GsonUtil.toHashMap(getParam("formData"))));
			}

			if (result != null && !StringUtil.isEmpty(result)) {
				if (result.equals("_false"))
					return;
				// 更改表单内容
				putParam("formData", result);
			}
			// 继续执行
			if (ctx.getResources().getResourceEntryName(v.getId()).equalsIgnoreCase("widget_form_button_submit")) {
				if (!submitForm(eventParams))
					return;
			}
			// if (JsManager.getInstance().callJsMethodSync(getControlId(), eventString, eventParams).equalsIgnoreCase("true")) {
			// return;
			// }
			if (clickListener != null) {
				clickListener.onClick(v);
			}
		}
	}

	/**
	 * 返回是否继续运行 老版本的事件
	 * 
	 * @param eventParams
	 * @return Ohmer-Nov 28, 2013 2:26:03 PM
	 */
	private boolean submitForm(HashMap<String, String> eventParams) {
		// String canSubmit = JsManager.getInstance().callJsMethodSync(getControlId(), "onSubmit", eventParams);
		// if (StringUtil.isEmpty(canSubmit) || canSubmit.equalsIgnoreCase("true")) {
		ctx.setProgressIndeterminateVisible(true);
		ViewUtil.showLoadingDialog(ctx, "请稍等", "努力提交中...", false);

		HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(getParam("formData"));

		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put("method", widgetDataModel.getMethod());

		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
			@Override
			public void onFailure(String failResopnse) {
				ViewUtil.closeLoadingDianlog();
				ctx.setProgressIndeterminateVisible(false);
				LogUtil.e(TAG, "onFailure error: " + failResopnse);

				// if (JsManager.getInstance().callJsMethodSyncS(getControlId(), "onSubmitFailed", failResopnse).equalsIgnoreCase("true")) {
				// return;
				// }
				// 处理js事件
				try {
					JSONObject responseJObject = new JSONObject(failResopnse);
					JSONObject res = new JSONObject();
					res.put("errors", responseJObject.get("errors"));
					String result = "";
					if (pageContext instanceof ItemActivity) {
						result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onSubmitSuccess", res);
					} else if (pageContext instanceof ItemFragment) {
						result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onSubmitSuccess", res);
					}
					if (result != null && result.equals("_false"))
						return;
				} catch (Exception e) {
					LogUtil.e(TAG, e.toString());
					e.printStackTrace();
				}

				Toast.makeText(IntentUtil.getActivity(), "网络出错，请稍后重试!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(String response) {
				ViewUtil.closeLoadingDianlog();
				String widgetDataString = response;
				LogUtil.d(TAG, "onSuccess : widgetDataString = " + widgetDataString);
				// if (JsManager.getInstance().callJsMethodSyncS(getControlId(), "onSubmitSuccess", widgetDataString).equalsIgnoreCase("true")) {
				// return;
				// }
				// 处理js事件
				Object responseJObject = null;
				try {
					responseJObject = (JsonObject) (new JsonParser()).parse(response);
				} catch (Exception e) {
					responseJObject = response;
					LogUtil.e(TAG, e.toString());
					e.printStackTrace();
				}
				String result = "";
				if (pageContext instanceof ItemActivity) {
					result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onSubmitSuccess", responseJObject);
				} else if (pageContext instanceof ItemFragment) {
					result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onSubmitSuccess", responseJObject);
				}
				if (result != null && result.equals("_false"))
					return;

				Toast.makeText(ctx, "提交成功", Toast.LENGTH_SHORT).show();
				IntentUtil.closeActivity();
			}

			@Override
			public void onProgress(Float progress) {

			}

			@Override
			public void onResponse(String resopnseString) {
				// TODO Auto-generated method stub

			}
		}, 0);
		return false;
		// }
	}

	/**
	 * 视图输入完成 ，模拟提交事件
	 * 
	 * @param view
	 *            Ohmer-Dec 4, 2013 2:20:50 PM
	 */
	private void simulateSubmitBt(EditText view) {
		view.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					HashMap<String, String> eventParams = new HashMap<String, String>();
					eventParams.put("controlId", getControlId());
					submitForm(eventParams);
				}
				return false;
			}
		});
	}

	public void setOnButtonClickListener(OnButtonClickListener buttonClickListener) {
		Button btNext = (Button) findViewById(R.id.widget_form_button_next);
		Button btSumbit = (Button) findViewById(R.id.widget_form_button_submit);
		Button btCancel = (Button) findViewById(R.id.widget_form_button_cancel);
		btNext.setOnClickListener(new ClickListener(buttonClickListener));
		btSumbit.setOnClickListener(new ClickListener(buttonClickListener));
		btCancel.setOnClickListener(new ClickListener(buttonClickListener));
	}

	public void setOnButtonClickListener(final OnButtonClickListener buttonClickListener, String viewID) {
		Button bt = (Button) findViewById(ResourceUtil.getViewIdFromContext(ctx, viewID));
		if (null == bt) {
			return;
		}
		bt.setOnClickListener(new ClickListener(buttonClickListener));
	}

	public void setOnClickListener(OnClickListener clickListener, String viewID) {
		View view = (View) findViewById(ResourceUtil.getViewIdFromContext(ctx, viewID));
		view.setOnClickListener(clickListener);
	}

	public String getTextInputBackgroud() {
		return textInputBackgroud;
	}

	public void setTextInputBackgroud(String textInputBackgroud) {
		this.textInputBackgroud = textInputBackgroud;
	}

	public Integer getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = Integer.parseInt(padding);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object data) {
		MessageData msgData = (MessageData) data;
		if (msgData.getName().equalsIgnoreCase(Constants.MESSAGE_TAG_ALBUM) || msgData.getName().equalsIgnoreCase(Constants.MESSAGE_TAG_CAMERA)) {
			bitmapDir = (String) msgData.getData();
			upload();
			MessageUtil.instance().deleteObserver(Constants.MESSAGE_TAG_CAMERA);
			MessageUtil.instance().deleteObserver(Constants.MESSAGE_TAG_ALBUM);
		}
		if (msgData.getName().equalsIgnoreCase(Constants.MESSAGE_TAG_CAMERA_ACTIVITY)) {
			clearAllUploadsImages();
			filePaths = (ArrayList<FilePath>) msgData.getData();
			uploads();
			MessageUtil.instance().deleteObserver(Constants.MESSAGE_TAG_CAMERA_ACTIVITY);
		}
	}

}