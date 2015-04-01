package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.widget.adapter.HorizontalListViewAdapter;
import com.ecloudiot.framework.widget.adapter.ZoomGalleryAdapter;
import com.ecloudiot.framework.widget.model.SlideShowItemModel;
import com.ecloudiot.framework.widget.model.SlideShowModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetme.android.horizontallistview.HorizontalListView;

@SuppressLint("ViewConstructor")
public class GalleryWidget extends BaseWidget {

	private final static String TAG = "GalleryWidget";
	private String layoutName;
	private String itemLayoutName;
	private SlideShowModel dataModel;
	private HorizontalListView horizontalListView;
	private HorizontalListViewAdapter adapter;
	private static Dialog dialog;
	private Button camPhotoButton;
	private PopupWindow popup;
	private View root;
	private Button cameraButton;
	private Button photoButton;
	private Button cancleButton;
	// private HttpPool mHttpPool = null;
	// private EditText text;
	// private HashMap<String, String> params = new HashMap<String, String>();
	// private HashMap<String, File> files = new HashMap<String, File>();
	// private UploadResultModel mResult = null;
	/** 返回码：成功. */
	public static final int RESULRCODE_OK = 0;

	public GalleryWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.gallery_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName) && layoutName.contains(".")) {
			try {
				String[] ss = layoutName.split("\\.");
				itemLayoutName = ss[0];
				this.layoutName = ss[1];
			} catch (Exception e) {
				LogUtil.e(TAG, "initViewLayout error: layoutName is invalid...");
				this.layoutName = "widget_gallery_default";
				itemLayoutName = "widget_gallery_item";
			}
		} else if (StringUtil.isNotEmpty(layoutName)) {
			this.layoutName = "widget_gallery_default";
			itemLayoutName = layoutName;
		} else {
			this.layoutName = "widget_gallery_default";
			itemLayoutName = "widget_gallery_item";
		}
		initBaseView(this.layoutName);
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			this.dataModel = GsonUtil.fromJson(widgetDataJObject, SlideShowModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: data String is invalid...");
			e.printStackTrace();
		}
	}

	/**
	 * 解析数据为listView 的Model
	 * 
	 * @param dataString
	 * @return
	 */
	@SuppressWarnings("unused")
	private SlideShowModel parsingWidgetModel(String dataString) {
		if (StringUtil.isEmpty(dataString))
			return null;
		JsonParser jParser = new JsonParser();
		JsonObject jObject = null;
		try {
			jObject = (JsonObject) jParser.parse(dataString);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingWidgetModel error: data string may be invalid or  " + e.toString());
		}
		return parsingWidgetModel(jObject);
	}

	/**
	 * 解析数据为listView 的Model
	 * 
	 * @param widgetDataJObject
	 * @return
	 */
	private SlideShowModel parsingWidgetModel(JsonObject widgetDataJObject) {
		SlideShowModel dataModel = null;
		try {
			dataModel = GsonUtil.fromJson(widgetDataJObject, SlideShowModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: widgetDataJObject  is invalid...");
		}
		return dataModel;
	}

	protected void setData() {
		horizontalListView = (HorizontalListView) this.getBaseView().findViewById(R.id.horizontalListView);
		adapter = new HorizontalListViewAdapter(this.ctx, dataModel, this.itemLayoutName);
		/*
		 * if (null != dataModel &&
		 * "openPopupWindow".equals(dataModel.getActionType())) {
		 * SlideShowItemModel slideShowItemModel = new SlideShowItemModel();
		 * slideShowItemModel.setImage("addimg"+String
		 * .valueOf(R.drawable.widget_gallery_item_addbtn));
		 * adapter.addItem(slideShowItemModel); }
		 */
		horizontalListView.setAdapter(adapter);
		horizontalListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				int count = GalleryWidget.this.adapter.getCount() - 1;
				if (position == count && "openPopupWindow".equals(dataModel.getActionType())) {
					camPhoteClick();
					return;
				}
				if (null != dataModel && "openPopupWindow".equals(dataModel.getActionType())) {
					photeItemClick(position);
				} else {
					createDialog(position);
				}
			}
		});

		// MessageUtil.setHandler(new MyHandler());
		// TODO: 换成观察者模式
		camPhotoButton = (Button) this.getBaseView().findViewById(R.id.cam_photo);
		camPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				camPhoteClick();
			}
		});
		// text = (EditText) this.getBaseView().findViewById(R.id.text);
		super.setData();
	}

	public SlideShowModel getDataModel() {
		return dataModel;
	}

	/**
	 * 刷新控件数据
	 * 
	 * @param moreData
	 */
	public void refreshData(String moreData) {
		super.refreshData(moreData);
		parsingWidgetData(moreData);
		setData();
	}

	/**
	 * 拍照更新horizontallistview
	 * 
	 * @author qijian
	 * 
	 */
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			LogUtil.d("MyHandler", "handleMessage......");
			super.handleMessage(msg);
			// 此处可以更新UI
			Bundle b = msg.getData();
			LogUtil.d(TAG, "bitmapUri:" + b.getString("bitmapDir"));

			SlideShowItemModel slideShowItemModel = new SlideShowItemModel();
			slideShowItemModel.setImage(b.getString("bitmapDir"));
			horizontalListView.itemWidth = 45;
			horizontalListView.setAutoScroll(true);

			if (adapter.getCount() == 0) {
				SlideShowItemModel addimgModel = new SlideShowItemModel();
				addimgModel.setImage("addimg" + String.valueOf(R.drawable.widget_gallery_item_addbtn));
				adapter.addItem(addimgModel);
				adapter.notifyDataSetChanged();
			}

			adapter.addItem(adapter.getCount() - 1, slideShowItemModel);
			adapter.notifyDataSetChanged();
		}
	}

	// 上传
	@SuppressLint("NewApi")
	public void upload() {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// mHttpPool = HttpPool.getInstance();
		// params.clear();
		// files.clear();
		// if (adapter.getItemList().size() < 2) {
		// Toast.makeText(ctx, "请先选择要上传的图片", Toast.LENGTH_LONG).show();
		// return;
		// }
		// try {
		// params.put("data1", URLEncoder.encode("中文可处理", HTTP.UTF_8));
		// params.put("data2", text.getText().toString());
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		// // 第一个是照相机的图标，上传要去掉
		// for (int i = 1; i < adapter.getItemList().size(); i++) {
		// String path = adapter.getItemList().get(i).getImage();
		// File file = new File(path);
		// files.put(file.getName(), file);
		// }

		// showDialog(AbConstant.DIALOGPROGRESS);
		// final HttpItem item = new HttpItem();
		// item.callback = new HttpCallback() {
		// @Override
		// public void update() {
		// // removeDialog(AbConstant.DIALOGPROGRESS);
		// if (mResult != null) {
		// if (mResult.getResultCode() == RESULRCODE_OK) {
		// Toast.makeText(ctx, "上传成功", Toast.LENGTH_LONG).show();
		// }
		// }
		// }
		//
		// @Override
		// public void get() {
		// try {
		// //TODO: 采用post方法上传
		// // String responseStr = FileUtil.postFile(
		// // Constants.ADDOVERLAYURL, params, files);
		// // 服务端的接受代码，采用org.apache.commons.fileupload
		// // FileUploadUtil类在util包下可以copy到web应用中
		// // String newPath =
		// //
		// request.getSession().getServletContext().getRealPath(Constant.SEPARATOR
		// // + Constant.GFIMAGES + Constant.SEPARATOR);
		// // FileUploadUtil upload = new FileUploadUtil(new
		// // File(newPath));
		// // 开始上传文件,文件名和路径
		// // HashMap<String,String> filePaths =
		// // upload.download(request,"GBK");
		// // mResult = GsonUtil.fromJson(responseStr,
		// // UploadResultModel.class);
		// } catch (Exception e) {
		// // showToastInThread(e.getMessage());
		// }
		// };
		// };
		// mHttpPool.download(item);
	}

	/**
	 * 初始化指定layout的popupwindow
	 * 
	 * @param layoutName
	 */
	private void initPopupWindow(String layoutName) {
		popup = ViewUtil.initPopupWindow(layoutName);
		root = popup.getContentView();
		cameraButton = (Button) root.findViewById(R.id.button1);
		photoButton = (Button) root.findViewById(R.id.button2);
		cancleButton = (Button) root.findViewById(R.id.button3);
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popup.isShowing())
					popup.dismiss();

			}
		});
	}

	/**
	 * 打开拍照的popupwindow
	 */
	private void camPhoteClick() {
		initPopupWindow("widget_gallery_popup_window_layout");
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtil.openCamera();
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		photoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtil.openPhotoAlbum();
				if (popup.isShowing())
					popup.dismiss();
			}
		});

	}

	/**
	 * 点击单张图片，打开popupwindow
	 * 
	 * @param position
	 */
	private void photeItemClick(final int position) {
		initPopupWindow("widget_gallery_item_popup_window_layout");
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (GalleryWidget.this.adapter.getCount() == 2) {
					GalleryWidget.this.adapter.clear();
				} else {
					GalleryWidget.this.adapter.remove(position);
				}
				GalleryWidget.this.adapter.notifyDataSetChanged();
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		/*
		 * photoButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (popup.isShowing())
		 * popup.dismiss(); GalleryWidget.this.position = position;
		 * camPhoteClick();
		 * 
		 * } });
		 */
	}

	@SuppressLint("InflateParams")
    public void createDialog(int position) {
		dialog = new Dialog(ECApplication.getInstance().getNowActivity(), R.style.DialogFullscreen);
		LayoutInflater inflater = LayoutInflater.from(ECApplication.getInstance().getNowActivity());
		View mainView = inflater.inflate(R.layout.dialog_content_view, null);
		final ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.dialog_pagers);
		ZoomGalleryAdapter zoomAdapter = new ZoomGalleryAdapter(this.ctx, this.adapter.getItemList());
		viewPager.setAdapter(zoomAdapter);
		viewPager.setCurrentItem(position);
		dialog.setContentView(mainView);
		dialog.getWindow().setWindowAnimations(R.style.DialogWindowAnim);
		dialog.show();
	}

	public static Dialog getDialog() {
		if (null != dialog)
			return dialog;
		else {
			LogUtil.d(TAG, "galleryWidget dialog is null");
			return null;
		}
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		if (null != itemClickListener) {
			LogUtil.d(TAG, "GalleryWidget:setOnItemClickListener");
			horizontalListView.setOnItemClickListener(itemClickListener);
		}
	}
}
