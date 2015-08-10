package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
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
public class MapWidget extends BaseWidget {
    private final static String TAG = "MapWidget";

    private List<Shop> shops = new ArrayList<>();

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.activity_map_location_mark);
    // 定位相关
    LocationClient mLocClient;

    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位

    private int near_time = 500;
    private int shop_type = 0;
    private LatLng myLocation = new LatLng(0, 0);
    int ooColor05 = Color.argb(80, 1, 1, 1);
    int ooColor10 = Color.argb(50, 1, 1, 1);
    int ooColor30 = Color.argb(20, 1, 1, 1);
    CircleOptions ooGround = new CircleOptions();

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
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        mBaiduMap.setMapStatus(msu);
        initButton();
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
                        mBaiduMap.hideInfoWindow();
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

    private void initButton() {
        doInitButton(R.id.map_near_time, "相距时间", near_time_list);
        doInitButton(R.id.map_shop_type, "场馆类型", shop_type_list);
    }

    private void doInitButton(final int buttonId, final String initialTest, final SparseArray<String> list) {
        final Button button = (Button) findViewById(buttonId);
        button.setText(initialTest);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        ECApplication.getInstance().getNowActivity());
                builder.setTitle(initialTest);
                final String[] items = new String[list.size()];
                for (int i = 0; i != list.size(); i++) {
                    items[i] = list.valueAt(i);
                }

                int value = 0;
                if (R.id.map_near_time == buttonId) {
                    value = near_time;
                }
                if (R.id.map_shop_type == buttonId) {
                    value = shop_type;
                }
                builder.setSingleChoiceItems(items,
                        list.indexOfKey(value), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                if (R.id.map_near_time == buttonId) {
                                    near_time = list.keyAt(which);
                                }
                                if (R.id.map_shop_type == buttonId) {
                                    shop_type = list.keyAt(which);
                                }
                            }
                        });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int tmp = 0;
                        if (R.id.map_near_time == buttonId) {
                            tmp = near_time;
                            switch (near_time_list.indexOfKey(tmp)) {
                                case 0:
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16.0f));
                                    ooGround.fillColor(ooColor05);
                                    ooGround.radius(near_time);
                                    break;
                                case 1:
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
                                    ooGround.fillColor(ooColor10);
                                    ooGround.radius(near_time);
                                    break;
                                case 2:
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14.0f));
                                    ooGround.fillColor(ooColor30);
                                    ooGround.radius(near_time);
                                    break;
                            }
                        }
                        if (R.id.map_shop_type == buttonId) {
                            tmp = shop_type;
                        }
                        button.setText(list.get(tmp));
                        refreshShop();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void refreshShop() {
        ViewUtil.showLoadingDialog(IntentUtil.getActivity(), "", "加载中...", false);
        final HashMap<String, String> params = new HashMap<>();
        params.put("apiversion", "1.0.000001");
        params.put("method", "content/place/nearby_shops");
        params.put("cacheTime", "0");
        params.put("sort_father_ids", "480");
        params.put("sort_ids", shop_type == 0 ? "" : String.valueOf(shop_type));
        params.put("lon", String.valueOf(myLocation.longitude));
        params.put("lat", String.valueOf(myLocation.latitude));
        params.put("map_type", "baidu");
        params.put("distance", String.valueOf(near_time));
        params.put("page_size", "1000");

        HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
            @Override
            public void onFailure(String failResopnse) {
            }

            @Override
            public void onSuccess(String response) {
                JsonObject jsonObject = (JsonObject) (new JsonParser().parse(response));
                JsonArray jsonArray = jsonObject.getAsJsonArray("shops");
                shops.clear();
                for (int i = 0; i != jsonArray.size(); i++) {
                    JsonObject shopObject = (JsonObject) jsonArray.get(i);
                    int id = shopObject.get("shop_id").getAsInt();
                    String title = shopObject.get("title").getAsString();
                    String address = shopObject.get("address").getAsString();
                    double baidu_latitude = shopObject.get("baidu_latitude").getAsDouble();
                    double baidu_longitude = shopObject.get("baidu_longitude").getAsDouble();
                    shops.add(new Shop(id, title, address, baidu_latitude, baidu_longitude));
                }
                initOverlay();
                ViewUtil.closeLoadingDianlog();
            }

            @Override
            public void onProgress(Float progress) {
            }

            @Override
            public void onResponse(String resopnseString) {
            }
        });
    }

    private void initOverlay() {
        // add marker overlay
        mBaiduMap.clear();
        for (Shop shop : shops) {
            LatLng ll = new LatLng(shop.baidu_latitude, shop.baidu_longitude);

            OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
                    .zIndex(2);
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
            mMarker.setTitle(String.valueOf(shop.id));
        }
        // add ground overlay
        ooGround.center(myLocation);
        ooGround.radius(near_time);
        ooGround.zIndex(1);
        ooGround.fillColor(ooColor05);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(myLocation);
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
                refreshShop();
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

    private SparseArray<String> near_time_list = new SparseArray<String>() {
        {
            put(500, "5分钟");
            put(1000, "10分钟");
            put(2000, "30分钟");
        }
    };

    private SparseArray<String> shop_type_list = new SparseArray<String>() {
        {
            put(0, "全部");
            put(985, "在线预订");
            put(481, "运动加油站");
            put(604, "健身苑点");
            put(594, "公共运动场");
            put(593, "社区文化中心");
            put(602, "健康驿站");
            put(488, "健身房");
            put(603, "综合房");
            put(598, "运动操场");
            put(580, "游泳池");
            put(571, "篮球");
            put(487, "足球");
            put(486, "乒乓球");
            put(485, "羽毛球");
            put(577, "桌球");
            put(575, "网球");
            put(574, "舞蹈");
            put(489, "瑜伽");
            put(578, "武术");
            put(581, "其他");
        }
    };
}
