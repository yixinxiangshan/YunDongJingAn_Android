package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Button;
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
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class MapWidget extends BaseWidget {
    private final static String TAG = "MapWidget";

    private List<Shop> shops = new ArrayList<>();

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<Marker> mMarkerList = new ArrayList<>();
    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    List<BitmapDescriptor> bdList = new ArrayList<>();
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.activity_map_location_mark);
    BitmapDescriptor ol = BitmapDescriptorFactory
            .fromResource(R.drawable.activity_map_location_overlap);
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位

    private LatLng myLocation = new LatLng(0, 0);
    private double size = 0.01;
    private double size_l = size * 1.15;

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
        shops.add(new Shop(8945, "酷炫武术", "徐汇区龙吴路118号酷贝拉学堂", 31.17522416579, 121.45174180023));
        shops.add(new Shop(8924, "锦鹰台球会所", "徐汇区黄石路538号", 31.16341362692, 121.46130907222));
        shops.add(new Shop(8954, "突破拓展运动中心", "徐汇区黄石路538号", 31.170948895226, 121.44457232753));
        shops.add(new Shop(8859, "民航中专足球俱乐部", "徐汇区黄石路538号", 31.174757393363, 121.46020753883));
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        initOverlay();
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(ECApplication.getInstance().getNowActivity().getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                OnInfoWindowClickListener listener = null;

                final Shop shop = findShopById(Integer.valueOf(marker.getTitle()));
                if (shop == null) {
                    return false;
                }
                button.setText(shop.title);
                button.setTextColor(Color.BLACK);
                listener = new OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        IntentUtil.openActivity("", "page_cheerup_info", "{\"info\":\"" + shop.id + "\"}");
                    }
                };
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });

        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(ECApplication.getInstance().getNowActivity().getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    public void initOverlay() {
        // add marker overlay
        for (Shop shop : shops) {
            LatLng ll = new LatLng(shop.baidu_latitude, shop.baidu_longitude);

            OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
                    .zIndex(2);
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
            mMarker.setTitle(String.valueOf(shop.id));
        }
        // add ground overlay
        LatLng southwest = new LatLng(myLocation.latitude - size, myLocation.longitude - size_l);
        LatLng northeast = new LatLng(myLocation.latitude + size, myLocation.longitude + size_l);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(ol).transparency(0.8f);
//        CircleOptions ooGround = new CircleOptions();
//        ooGround.center(new LatLng(31.168489, 121.453797));
//        ooGround.fillColor(Color.YELLOW);
//        ooGround.radius(100);
//        ooGround.fillColor(Color.YELLOW);
//        ooGround.zIndex(1);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                myLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(myLocation);
                mBaiduMap.animateMapStatus(u);
                initOverlay();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private class Shop {
        public Shop(int id, String title, String address, double baidu_latitude, double baidu_longitude) {
            this.id = id;
            this.title = title;
            this.address = address;
            this.baidu_latitude = baidu_latitude;
            this.baidu_longitude = baidu_longitude;
        }

        public int id;
        public String title;
        public String address;
        public double baidu_latitude;
        public double baidu_longitude;
    }

    private Shop findShopById(int id) {
        for (Shop shop : shops) {
            if (shop.id == id) {
                return shop;
            }
        }
        return null;
    }
}
