package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ViewConstructor")
public class SimpleMapWidget extends BaseWidget {
    private final static String TAG = "MapWidget";
    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.activity_map_location_mark);

    public SimpleMapWidget(Object pageContext, String dataString, String layoutName) {
        super(pageContext, dataString, layoutName);
        LogUtil.d(TAG, "init SimpleMapWidget");
        this.setId(R.id.simple_map_widget);
        parsingData();

//		loading(LOADING_0N_OFF.TURN_OFF);
    }

    @Override
    protected void initViewLayout(String layoutName) {
        super.initViewLayout(layoutName);
        if (StringUtil.isNotEmpty(layoutName)) {
            initBaseView(layoutName);
        } else {
            initBaseView("activity_simple_map");
        }
    }

    protected void parsingWidgetData(JsonObject widgetDataJObject) {
        super.parsingWidgetData(widgetDataJObject);
        this.widgetDataJObject = widgetDataJObject;
    }

    protected void setData() {
        setContent();
        super.setData();
    }

    private void setContent() {
        mMapView = (MapView) findViewById(R.id.bsimpleMapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.5f);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    public void refreshData(String data) {
        putWidgetData(data);
        refreshData();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void refreshData() {
        // add marker overlay
        LatLng llA = new LatLng(getWidgetDataJObject().get("latitude").getAsFloat(),
                getWidgetDataJObject().get("longitude").getAsFloat());

        OverlayOptions ooA = new MarkerOptions().position(llA).icon(bd)
                .zIndex(0).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(llA);
        mBaiduMap.setMapStatus(u);
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(IntentUtil.getActivity().getApplicationContext());
                button.setText(getWidgetDataJObject().get("title").getAsString());
                button.setBackgroundResource(R.drawable.popup);
                button.setTextColor(Color.BLACK);
                LatLng ll = marker.getPosition();
                OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                    }
                };
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }
}

