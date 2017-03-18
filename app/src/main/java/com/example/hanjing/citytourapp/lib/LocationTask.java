/**  
 * Project Name:Android_Car_Example  
 * File Name:LocationTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月3日上午9:27:45  
 *  
 */

package com.example.hanjing.citytourapp.lib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;


/**
 * ClassName:LocationTask <br/>
 * Function: 简单封装了定位请求，可以进行单次定位和多次定位，注意的是在app结束或定位结束时注意销毁定位 <br/>
 * Date: 2017年2月5日 上午11:09:45 <br/>
 * 
 * @author hanjing
 *
 */
public class LocationTask implements LocationSource, AMapLocationListener, OnLocationGetListener {

	private AMapLocationClient mLocationClient;
	private AMapLocationClientOption mLocationOption;

	private static LocationTask mLocationTask;

	private LocationSource.OnLocationChangedListener mListener;

	private Context mContext;

	private OnLocationGetListener mOnLocationGetlisGetListener;

	private RegeocodeTask mRegecodeTask;

	private LocationTask(Context context) {
		mLocationClient = new AMapLocationClient(context);
		mLocationClient.setLocationListener(this);
		mRegecodeTask = new RegeocodeTask(context);
		mRegecodeTask.setOnLocationGetListener(this);
		mContext = context;
	}

	public void setOnLocationGetListener(
			OnLocationGetListener onGetLocationListener) {
		mOnLocationGetlisGetListener = onGetLocationListener;
	}

	public static LocationTask getInstance(Context context) {
		if (mLocationTask == null) {
			mLocationTask = new LocationTask(context);
		}
		return mLocationTask;
	}

	/**  
	 * 开启单次定位
	 */
	public void startSingleLocate() {
		mLocationOption=new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mLocationOption.setOnceLocation(true);
		mLocationClient.setLocationOption(mLocationOption);

		mLocationClient.startLocation();

	}

	/**  
	 * 开启多次定位
	 */
	public void startLocate() {

		mLocationOption=new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mLocationOption.setOnceLocation(false);
		mLocationOption.setInterval(8*1000);
		mLocationClient.setLocationOption(mLocationOption);

		mLocationClient.startLocation();

	}

	/**  
	 * 结束定位，可以跟多次定位配合使用
	 */
	public void stopLocate() {
		mLocationClient.stopLocation();

	}

	/**  
	 * 销毁定位资源
	 */
	public void onDestroy() {
		mLocationClient.stopLocation();
		if(null != mLocationClient){
			mLocationClient.onDestroy();
		}
	}


	@Override
	public void onLocationChanged(AMapLocation amapLocation) {

		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				PositionEntity entity = new PositionEntity();
				entity.latitue = amapLocation.getLatitude();
				entity.longitude = amapLocation.getLongitude();

				if (!TextUtils.isEmpty(amapLocation.getAddress())) {
					entity.address = amapLocation.getAddress();
				}
				mOnLocationGetlisGetListener.onLocationGet(entity);
			}
			else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
			}
		}

	}

	@Override
	public void onLocationGet(PositionEntity entity) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

		// TODO Auto-generated method stub

	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mLocationClient == null) {
			//初始化定位
			mLocationClient = new AMapLocationClient(mContext);
			//初始化定位参数
			mLocationOption = new AMapLocationClientOption();
			//设置定位回调监听
			mLocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mLocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mLocationClient.startLocation();//启动定位
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mLocationClient != null) {
			mLocationClient.stopLocation();
			mLocationClient.onDestroy();
		}
		mLocationClient = null;
	}

}
