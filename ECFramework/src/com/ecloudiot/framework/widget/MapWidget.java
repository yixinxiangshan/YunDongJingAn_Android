package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.view.View;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenuItem;
import android.widget.Button;
import android.widget.Toast;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

@SuppressLint("ViewConstructor")
public class MapWidget extends BaseWidget {
	private final static String TAG = "MapWidget";

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerA;
	private Marker mMarkerB;
	private Marker mMarkerC;
	private Marker mMarkerD;
	private InfoWindow mInfoWindow;

	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);
	BitmapDescriptor bdB = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);
	BitmapDescriptor bdC = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);
	BitmapDescriptor bdD = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);
	BitmapDescriptor bd = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);
	BitmapDescriptor bdGround = BitmapDescriptorFactory
			.fromResource(R.drawable.activity_map_location_mark);

	public MapWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
        ActionBar actionBar = ECApplication.getInstance().getNowActivity().getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        LogUtil.d(TAG, "init MapWidget");
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
			initBaseView("activity_map");
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
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        initOverlay();
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button( ECApplication.getInstance().getNowActivity().getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                OnInfoWindowClickListener listener = null;
                if (marker == mMarkerA || marker == mMarkerD) {
                    button.setText("更改位置");
                    listener = new OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                            LatLng ll = marker.getPosition();
                            LatLng llNew = new LatLng(ll.latitude + 0.005,
                                    ll.longitude + 0.005);
                            marker.setPosition(llNew);
                            mBaiduMap.hideInfoWindow();
                        }
                    };
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarkerB) {
                    button.setText("更改图标");
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            marker.setIcon(bd);
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarkerC) {
                    button.setText("删除");
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            marker.remove();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });
    }
    public void initOverlay() {
        // add marker overlay
        LatLng llA = new LatLng(39.963175, 116.400244);
        LatLng llB = new LatLng(39.942821, 116.369199);
        LatLng llC = new LatLng(39.939723, 116.425541);
        LatLng llD = new LatLng(39.906965, 116.401394);

        OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        OverlayOptions ooB = new MarkerOptions().position(llB).icon(bdB)
                .zIndex(5);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
        OverlayOptions ooC = new MarkerOptions().position(llC).icon(bdC)
                .perspective(false).anchor(0.5f, 0.5f).rotate(30).zIndex(7);
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        OverlayOptions ooD = new MarkerOptions().position(llD).icons(giflist)
                .zIndex(0).period(10);
        mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));

        // add ground overlay
        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);
	}
}
