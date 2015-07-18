package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.adapter.LayoutButtonImageAdapter;
import com.ecloudiot.framework.widget.model.TabModel;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.util.HashMap;


@SuppressLint("ViewConstructor")
public class LayoutButtonWidget extends BaseWidget implements OnItemClickListener {
    private String TAG = "LayoutButtonWidget";
    private TabModel widgetDataModel;
    private GridView gridview;
    private String itemlayoutName;
    private String layoutButtonName;
    private int imageCol;
    private LayoutButtonImageAdapter adapter;

    public LayoutButtonWidget(Object pageContext, String dataString, String layoutName) {
        super(pageContext, dataString, layoutName);
        this.setId(R.id.layoutbutton_widget);
        parsingData();
    }

    public void initViewLayout(String layoutName) {
        super.initViewLayout(layoutName);
        if (StringUtil.isNotEmpty(layoutName) && layoutName.contains(".")) {
            try {
                String[] ss = layoutName.split("\\.");
                itemlayoutName = ss[0];
                layoutButtonName = ss[1];
            } catch (Exception e) {
                LogUtil.e(TAG, "initViewLayout error: layoutName is invalid...");
                layoutButtonName = "widget_layout_button_default";
                itemlayoutName = "widget_layout_button_item_default";
            }
        } else if (StringUtil.isNotEmpty(layoutName)) {
            layoutButtonName = "widget_layout_button_default";
            itemlayoutName = layoutName;
        } else {
            layoutButtonName = "widget_layout_button_default";
            itemlayoutName = "widget_layout_button_item_default";
        }
        initBaseView(layoutButtonName);
    }

    protected void parsingWidgetData(JsonObject widgetDataJObject) {
        super.parsingWidgetData(widgetDataJObject);
        try {
            widgetDataModel = GsonUtil.fromJson(widgetDataJObject, TabModel.class);
        } catch (Exception e) {
            LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
        }
    }

    protected void setData() {
        // LinearLayout layout = (LinearLayout)
        // getBaseView().findViewById(R.id.widget_gridview_bg);
        // layout.setBackgroundResource(R.drawable.proj_activity_splash_screen);
//        ImageView activity_logo = (ImageView) getBaseView().findViewById(R.id.proj_activity_logo);
//        activity_logo.setImageResource(R.drawable.proj_activity_layoutbutton_logo);
        // 实现GridView
//        LogUtil.d(TAG, "start init griview...");
        gridview = (GridView) getBaseView().findViewById(R.id.gridview);
        gridview.setNumColumns(getImageCol());
        gridview.setOnItemClickListener(this);
//        LogUtil.d(TAG, "start set  griview adapter...");

        adapter = new LayoutButtonImageAdapter(this.ctx, widgetDataModel, itemlayoutName);
        gridview.setAdapter(adapter);
//        LogUtil.d(TAG, "end set  griview adapter...");
        super.setData();
    }

    public int getImageCol() {
        return imageCol > 0 ? imageCol : 2;
    }

    public void setImageCol(String numColumns) {
        if (StringUtil.toInt(numColumns) >= 0)
            this.imageCol = Integer.parseInt(numColumns);
    }

    public TabModel getDataModel() {
        return widgetDataModel;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        gridview.setOnItemClickListener(onItemClickListener);
    }

    public LayoutButtonImageAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("position", "" + position);
//        eventParams.put("controlId", getControlId());
//        eventParams.put("allTypes", "" + getAdapter().getViewTypeCount());
//        eventParams.put("currentType", "" + getAdapter().getItemViewType(position - 1));
        // 执行js里面的事件
        if (pageContext instanceof ItemActivity) {
            JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
        } else if (pageContext instanceof ItemFragment) {
            // LogUtil.d(TAG, "onItemClick:" + ((ItemFragment) pageContext).getWidgetJsEvents().toString());
            JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
        }

    }
}
