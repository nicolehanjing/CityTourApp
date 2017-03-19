package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.app.MyApplication;
import com.example.hanjing.citytourapp.model.RequestVo;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.JsonUtils;
import com.example.hanjing.citytourapp.util.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements OnClickListener, AMapLocationListener,
        AMap.OnMapLoadedListener, RouteSearch.OnRouteSearchListener, PopupMenu.OnMenuItemClickListener {

    private TextView request_ride, see_avaliable, look_around, price_view;
    private ImageView back_to_login, setting, travel;
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    PopupMenu popupMenu;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption;
    private RouteSearch mRouteSearch;

    private double mLat ;
    private double mLong;
    private Marker curMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_main);


        mapView =	(MapView)findViewById(R.id.map);
        request_ride = (TextView)findViewById(R.id.request_ride);
        see_avaliable = (TextView)findViewById(R.id.see_avaliable);
        look_around = (TextView)findViewById(R.id.look_around);
        price_view = (TextView)findViewById(R.id.price_view);
        price_view.setVisibility(View.GONE);
        back_to_login = (ImageView)findViewById(R.id.back_main);
        setting = (ImageView)findViewById(R.id.setting);
        travel = (ImageView)findViewById(R.id.travel);

        // View当前PopupMenu显示的相对View的位置
        popupMenu = new PopupMenu(MainActivity.this, setting);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_user, popupMenu.getMenu());//创建menu菜单
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(this);
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "closing PopupMenu...", Toast.LENGTH_SHORT).show();
            }
        });


        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();//设置map属性
        }

        //监听
        request_ride.setOnClickListener(this);
        see_avaliable.setOnClickListener(this);
        look_around.setOnClickListener(this);
        back_to_login.setOnClickListener(this);
        setting.setOnClickListener(this);
        travel.setOnClickListener(this);

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //开启定位
        mLocationClient.startLocation();

        //初始化"出行路线规划"对象
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);//设置数据回调监听器
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.onResume();

        if (MyApplication.getInstance() != null) { // to avoid NullPointerException
            if (MyApplication.getInstance() .getUser().type == 1) {//driver
                look_around.setText("Start now");
                see_avaliable.setText("View available");
            } else if (MyApplication.getInstance() .getUser().type == 0) {//rider
                look_around.setText("Look around");
                see_avaliable.setText("Take a ride!");
            }

        }
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (null != mapView) {
            mapView.onSaveInstanceState(outState);
        }

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (null != mapView) {
            mapView.onDestroy();
        }

        //unregisterReceiver(mGeoFenceReceiver);
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
        }

        //mLocationTask.onDestroy();//销毁定位资源
    }


    //设置map的ui
    private void setUpMap() {
        //UiSettings类用于操控浮在地图图面上的一系列用于操作地图的组件，例如缩放按钮、指南针、定位按钮、比例尺等
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
        aMap.setOnMapLoadedListener(this);

        //UiSettings类用于操控浮在地图图面上的一系列用于操作地图的组件，例如缩放按钮、指南针、定位按钮、比例尺等
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        //aMap.getUiSettings().setZoomControlsEnabled(true);//是否允许显示缩放按钮
        //aMap.getUiSettings().setScaleControlsEnabled(true);////控制比例尺控件是否显示
        //aMap.getUiSettings().setAllGesturesEnabled(true);//生效所有手势

        // 设置中心点和缩放比例
        //aMap.moveCamera(CameraUpdateFactory.zoomTo(14));//不带地图视觉移动动画
        //aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        //aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //aMap.showIndoorMap(true);//true：显示室内地图；false：不显示；
        //aMap.setTrafficEnabled(true);//显示实时路况图层
    }

    //点击按钮时
    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        Intent intent;
        if(v == request_ride){
            intent = new Intent(MainActivity.this,ActivityPublish.class);
            intent.putExtra("flag", 0);//传递数据
            startActivity(intent);

        }else if(v == look_around){
            //PoiAroundSearchActivity
            if(MyApplication.getInstance().getUser().type == 0){//乘客
                intent = new Intent(MainActivity.this,PoiAroundSearchActivity.class);
                intent.putExtra("lat", mLat);
                intent.putExtra("lon", mLong);
                startActivity(intent);
            }else {
                new GetDataTask().execute();
            }


        }else if(v == see_avaliable){
            intent = new Intent(MainActivity.this,ActivityPublish.class);
            intent.putExtra("flag", 1);
            startActivity(intent);
        }

        else if (v == back_to_login){
            intent = new Intent(MainActivity.this,ActivityLogin.class);
            startActivity(intent);
        }

        else if (v == setting){
            popupMenu.show();
        }

        else if (v == travel){
            intent = new Intent(MainActivity.this,ActivityTravel.class);
            //intent.putExtra("from_main_activity", true);
            startActivity(intent);

            //通知
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //实例化通知栏构造器
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

            mBuilder.setContentTitle("Have fun!").setContentText("welcome to Beijing, explore it!")
                    .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                    .setTicker("Have fun") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(R.drawable.taxiloco);//设置通知小ICON


            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            manager.notify(1, mBuilder.build());
        }

    }

    public PendingIntent getDefalutIntent(int flags){
        Intent intent = new Intent(MainActivity.this,PoiAroundSearchActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, intent, flags);
        return pendingIntent;
    }


    @Override
        public boolean onMenuItemClick(MenuItem item){

                //设置单项点击
                switch (item.getItemId()) {
                    case R.id.profile:
                        Toast.makeText(getApplicationContext(), "You are a driver", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.food:
                        Toast.makeText(getApplicationContext(), "You could have many kinds of dishes", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.search:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //接收一个 Uri 对象，主要用于指定当前 Intent 正在操作的数据，到 Uri.parse()方法中解析
                        intent.setData(Uri.parse("http://www.baidu.com"));
                        MainActivity.this.startActivity(intent);
                    default:
                }
                return false;
    }


    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        // TODO Auto-generated method stub
    }

    //规划开车路径，绘制路径
    private DriveRouteResult driveRouteResult;
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        // TODO Auto-generated method stub
        if (errorCode == 1000) {

            price_view.setVisibility(View.VISIBLE);
            driveRouteResult = result;
            DrivePath drivePath = driveRouteResult.getPaths().get(0);
            //aMap.clear();// 清理地图上的所有覆盖物

            //绘制路径
            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                    this, aMap, drivePath, driveRouteResult.getStartPos(),
                    driveRouteResult.getTargetPos());
            drivingRouteOverlay.removeFromMap();
            drivingRouteOverlay.setNodeIconVisibility(false);
            drivingRouteOverlay.setThroughPointIconVisibility(false);
            drivingRouteOverlay.addToMap();
            drivingRouteOverlay.zoomToSpan();

        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
         // TODO Auto-generated method stub
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        // TODO Auto-generated method stub
    }

    //位置改变监听
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        // TODO Auto-generated method stub

        if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
            if (aMapLocation != null) {
            }
            return;
        }

        mLat = aMapLocation.getLatitude();
        mLong = aMapLocation.getLongitude();//取出经纬度
        MyApplication.getInstance().setCity(aMapLocation.getCity());
        //new GetDataTask().execute();

        addCurMark();//标记位置

    }

    //标记位置
    private void addCurMark(){

        if(curMarker!=null){
            curMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);//设置marker平贴地图效果
        markerOptions.position(new LatLng(mLat,mLong));
        markerOptions.snippet("Welcome to Beijing!");
        //设置图标
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.location))).draggable(true).period(50);
        //标记位置
        curMarker = aMap.addMarker(markerOptions);

        curMarker.showInfoWindow();//主动显示indowindow
        curMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);//居中

        aMap.moveCamera(CameraUpdateFactory.changeLatLng((new LatLng(mLat,mLong))));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        //mLocationTask.startSingleLocate();//开始定位

    }


        List<String> locations = new ArrayList<String>();
        private void  searchResult(){

            locations.clear();

            //排序算法优化路径
            if(list.size() > 0){
                locations.add(list.get(0).start_loc);
                String end_l = list.get(0).end_loc;
                for (String loc:end_l.split("-")){
                    if(StringUtils.isNotBlank(loc)){
                        locations.add(loc);
                    }
                }
            }


            for(int i=1; i< locations.size() ; i++){
                int startNum = i;
                int num = i;
                float dis = 100000000;
                for(int j = i ;j< locations.size();j++){
                    String startloc = locations.get(i-1);
                    String curloc = locations.get(j);


                    float distance = AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(startloc.split(",")[0]),
                            Double.parseDouble(startloc.split(",")[1])),new LatLng(Double.parseDouble(curloc.split(",")[0]),
                            Double.parseDouble(curloc.split(",")[1])));

                    if(distance < dis){
                        num = j;
                        dis = distance;

                    }

                }

                if(startNum != num){
                    String temp = 	locations.get(startNum);
                    locations.set(startNum, locations.get(num));
                    locations.set(num,temp);
                }

            }
		/*float alldis = 0;
		for(int i=0;i< locations.size()-1 ; i++){
			String startloc = locations.get(i);
			String curloc = locations.get(i+1);


			float distance = AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(startloc.split(",")[0]),
					Double.parseDouble(startloc.split(",")[1])),new LatLng(Double.parseDouble(curloc.split(",")[0]),
							Double.parseDouble(curloc.split(",")[1])));
			alldis = alldis + (int)(distance);
		}

		int  money =  (int) ((alldis/1000.0) * 1.8);*/

		price_view.setText("Cost：" + list.get(0).money +"RMB");

            for (int i=0 ;i< locations.size() -1 ;i++) {
                String[] startlocs = locations.get(i).split(",");
                String[] endlocs = locations.get(i+1).split(",");

                LatLonPoint startP = new LatLonPoint(Double.valueOf(startlocs[0]),Double.valueOf(startlocs[1]));
                LatLonPoint endP =  new LatLonPoint(Double.valueOf(endlocs[0]),Double.valueOf(endlocs[1]));
                mRouteSearch.calculateDriveRouteAsyn(new DriveRouteQuery(
                        new RouteSearch.FromAndTo(startP,endP ), RouteSearch.DrivingDefault, null, null, ""));
            }

        }


    private List<RequestVo> list;
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.

            String result = query();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                Type listType = new TypeToken<List<RequestVo>>() {
                }.getType();
                list = (List<RequestVo>) JsonUtils.StringFromJson(result, listType);
                if (list.size() > 0) {
                    searchResult();
                } else {
                    Toast.makeText(MainActivity.this, "不存在进行中的行程", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


    private String query() {
        // +"&zh_id=" + MyApplication.getInstance().getZhID()
        String queryString =  "op=5" +"&user_id=" + MyApplication.getInstance().getUser().id;
        // url
        String url = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?" + queryString;
        return HttpUtil.queryStringForPost(url);
    }


}
