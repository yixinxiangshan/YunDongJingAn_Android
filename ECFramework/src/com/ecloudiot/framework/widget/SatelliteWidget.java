package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenuItem;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ViewConstructor")
public class SatelliteWidget extends BaseWidget {
    private final static String TAG = "SatelliteWidget";

    // private Button button = null;

    public SatelliteWidget(Object pageContext, String dataString, String layoutName) {
        super(pageContext, dataString, layoutName);
        ActionBar actionBar = ECApplication.getInstance().getNowActivity().getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        LogUtil.d(TAG, "init SatelliteWidget");
        this.setId(R.id.satelite_menu_widget);
        parsingData();

//		loading(LOADING_0N_OFF.TURN_OFF);
    }

    @Override
    protected void initViewLayout(String layoutName) {
        super.initViewLayout(layoutName);
        if (StringUtil.isNotEmpty(layoutName)) {
            initBaseView(layoutName);
        } else {
            initBaseView("satellite");
        }
    }

    protected void parsingWidgetData(JsonObject widgetDataJObject) {
        super.parsingWidgetData(widgetDataJObject);
    }

    protected void setData() {
        setContent();
        super.setData();
    }

    private void setContent() {
        SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);
        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(1, R.drawable.ic_1));
        items.add(new SatelliteMenuItem(2, R.drawable.ic_2));
        items.add(new SatelliteMenuItem(3, R.drawable.ic_3));
        items.add(new SatelliteMenuItem(4, R.drawable.ic_4));
        items.add(new SatelliteMenuItem(5, R.drawable.ic_5));
        menu.addItems(items);

        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {

            public void eventOccured(int id) {
                HashMap<String, String> eventParams = new HashMap<String, String>();
                eventParams.put("id", "" + id);
                JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
            }
        });
    }
}
