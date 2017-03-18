package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.example.hanjing.citytourapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PoiAroundSearchActivity extends Activity implements OnClickListener,
        OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener,
        OnPoiSearchListener, SensorEventListener {
	private MapView mapview;
	private AMap mAMap;

	private SensorManager sensorManager;
	//加速度传感器数据
	float accValues[]=new float[3];
	//地磁传感器数据
	float magValues[]=new float[3];
	//旋转矩阵，用来保存磁场和加速度的数据
	float r[]=new float[9];
	//模拟方向传感器的数据（原始数据为弧度）
	float values[]=new float[3];
	private float lastRotateDegree;


	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint lp = new LatLonPoint(39.993743, 116.472995);// 116.472995,39.993743
	private Marker locationMarker; // 选择的点
	private Marker detailMarker;
	private Marker mlastMarker;
	private ImageView compassImg;
	private PoiSearch poiSearch;
	private myPoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	
	private RelativeLayout mPoiDetail;
	private TextView mPoiName, mPoiAddress;
	private String keyWord = "";
	private ImageView camera_btn, take_picture;
	private EditText mSearchText;

	private Uri imageUri;
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poiaroundsearch_activity);
		mapview = (MapView)findViewById(R.id.mapView);
		compassImg = (ImageView) findViewById(R.id.compass_img);//指南针
		camera_btn = (ImageView)findViewById(R.id.camera);
		take_picture = (ImageView)findViewById(R.id.take_picture);
		mapview.onCreate(savedInstanceState);
		double  lat = getIntent().getDoubleExtra("lat", 0);
		double  lon = getIntent().getDoubleExtra("lon", 0);
		lp.setLatitude(lat);
		lp.setLongitude(lon);

		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor_gravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
		Sensor mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//磁场传感器

		//给传感器注册监听
		sensorManager.registerListener(this, sensor_gravity, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, mag_sensor,SensorManager.SENSOR_DELAY_GAME);
		
		init();
	}
	
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mapview.getMap();
			mAMap.setOnMapClickListener(this);
			camera_btn.setOnClickListener(this);
			mAMap.setOnMarkerClickListener(this);
			mAMap.setOnInfoWindowClickListener(this);
			mAMap.setInfoWindowAdapter(this);
			TextView searchButton = (TextView) findViewById(R.id.btn_search);
			searchButton.setOnClickListener(this);
			locationMarker = mAMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
					.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
			locationMarker.showInfoWindow();

		}
		setup();
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
	}
	private void setup() {
		mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
		mPoiDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);
				
			}
		});
		mPoiName = (TextView) findViewById(R.id.poi_name);
		mPoiAddress = (TextView) findViewById(R.id.poi_address);
		mSearchText = (EditText)findViewById(R.id.input_edittext);
	}
	/**
	 * 开始进行poi搜索
	 */
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		keyWord = mSearchText.getText().toString().trim();
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));//
			// 设置搜索区域为以lp点为圆心，其周围5000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
		whetherToShowDetailInfo(false);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);//释放使用的资源
		}
	}
	
	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		if (rcode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						//清除POI信息显示
						whetherToShowDetailInfo(false);
						//并还原点击marker样式
						if (mlastMarker != null) {
							resetlastmarker();
						}				
						//清理之前搜索结果的marker
						if (poiOverlay !=null) {
							poiOverlay.removeFromMap();
						}
						mAMap.clear();
						poiOverlay = new myPoiOverlay(mAMap, poiItems);
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
						
						mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
								.icon(BitmapDescriptorFactory
								.fromBitmap(BitmapFactory.decodeResource(
										getResources(), R.drawable.point4)))
						.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
						
						mAMap.addCircle(new CircleOptions()
						.center(new LatLng(lp.getLatitude(),
								lp.getLongitude())).radius(5000)
						.strokeColor(Color.BLUE)
						.fillColor(Color.argb(50, 1, 1, 1))
						.strokeWidth(2));

					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						Toast.makeText(PoiAroundSearchActivity.this,
								R.string.no_result, Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(PoiAroundSearchActivity.this,
						R.string.no_result, Toast.LENGTH_SHORT).show();
			}
		}
	}


	@Override
	public boolean onMarkerClick(Marker marker) {
		
		if (marker.getObject() != null) {
			whetherToShowDetailInfo(true);
			try {
				PoiItem mCurrentPoi = (PoiItem) marker.getObject();
				if (mlastMarker == null) {
					mlastMarker = marker;
				} else {
					// 将之前被点击的marker置为原来的状态
					resetlastmarker();
					mlastMarker = marker;
				}
				detailMarker = marker;
				detailMarker.setIcon(BitmapDescriptorFactory
									.fromBitmap(BitmapFactory.decodeResource(
											getResources(),
											R.drawable.poi_marker_pressed)));

				setPoiItemDisplayContent(mCurrentPoi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			whetherToShowDetailInfo(false);
			resetlastmarker();
		}


		return true;
	}

	// 将之前被点击的marker置为原来的状态
	private void resetlastmarker() {
		int index = poiOverlay.getPoiIndex(mlastMarker);
		if (index < 10) {
			mlastMarker.setIcon(BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(
							getResources(),
							markers[index])));
		}else {
			mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
			BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
		}
		mlastMarker = null;
		
	}


	private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
		mPoiName.setText(mCurrentPoi.getTitle());
		mPoiAddress.setText(mCurrentPoi.getSnippet()+mCurrentPoi.getDistance());
	}


	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search:
				doSearchQuery();
				break;

			//拍照
			case R.id.camera:
				// 创建File对象，用于存储拍照后的图片
				File outputImage = new File(Environment.
						getExternalStorageDirectory(), "tempImage.jpg");
				try {
					if (outputImage.exists()) {
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imageUri = Uri.fromFile(outputImage);
				Intent intent = new Intent("android.media.action. IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序

				break;

			default:
				break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case TAKE_PHOTO:
				if (resultCode == RESULT_OK) {
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(imageUri, "image/*");
					intent.putExtra("scale", true);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
				}
				break;

			case CROP_PHOTO:
				if (resultCode == RESULT_OK) {
					try {
						Bitmap bitmap = BitmapFactory.decodeStream
								(getContentResolver().openInputStream(imageUri));
						take_picture.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} }
				break;
			default:
				break;
		}
	}
	
	private int[] markers = {
			R.drawable.poi_marker_1,
			R.drawable.poi_marker_2,
			R.drawable.poi_marker_3,
			R.drawable.poi_marker_4,
			R.drawable.poi_marker_5,
			R.drawable.poi_marker_6,
			R.drawable.poi_marker_7,
			R.drawable.poi_marker_8,
			R.drawable.poi_marker_9,
			R.drawable.poi_marker_10
			};
	
	private void whetherToShowDetailInfo(boolean isToShow) {
		if (isToShow) {
			mPoiDetail.setVisibility(View.VISIBLE);

		} else {
			mPoiDetail.setVisibility(View.GONE);

		}
	}


	@Override
	public void onMapClick(LatLng arg0) {
		whetherToShowDetailInfo(false);
		if (mlastMarker != null) {
			resetlastmarker();
		}
	}
	
	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		Toast.makeText(PoiAroundSearchActivity.this, infomation, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据

		}


		/**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
		 * 填充旋转数组r
		 * r：要填充的旋转数组
		 * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
		 * gravity:加速度传感器数据
		 * geomagnetic：地磁传感器数据
		 */
		SensorManager.getRotationMatrix(r, null, accValues, magValues);
		/**
		 * public static float[] getOrientation (float[] R, float[] values)
		 * R：旋转数组
		 * values ：模拟方向传感器的数据
		 */

		SensorManager.getOrientation(r, values);
		//Log.d("ActivityFood", "value[0] is " + Math.toDegrees(values[0]));

		// 将计算出的旋转角度取反，用于旋转指南针背景图，用到了旋转动画技术
		float rotateDegree = -(float) Math.toDegrees(values[0]);
		if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
			RotateAnimation animation = new RotateAnimation (lastRotateDegree, rotateDegree,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation. RELATIVE_TO_SELF, 0.5f);
			animation.setFillAfter(true);
			compassImg.startAnimation(animation);
			lastRotateDegree = rotateDegree;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}


	/**
	 * 自定义PoiOverlay
	 *
	 */
	
	private class myPoiOverlay {
		private AMap mamap;
		private List<PoiItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myPoiOverlay(AMap amap , List<PoiItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	        for (int i = 0; i < mPois.size(); i++) {
	            Marker marker = mamap.addMarker(getMarkerOptions(i));
	            PoiItem item = mPois.get(i);
				marker.setObject(item);
	            mPoiMarks.add(marker);
	        }
	    }

	    /**
	     * 去掉PoiOverlay上所有的Marker。
	     *
	     * @since V2.1.0
	     */
	    public void removeFromMap() {
	        for (Marker mark : mPoiMarks) {
	            mark.remove();
	        }
	    }

	    /**
	     * 移动镜头到当前的视角。
	     * @since V2.1.0
	     */
	    public void zoomToSpan() {
	        if (mPois != null && mPois.size() > 0) {
	            if (mamap == null)
	                return;
	            LatLngBounds bounds = getLatLngBounds();
	            mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	        }
	    }

	    private LatLngBounds getLatLngBounds() {
	        LatLngBounds.Builder b = LatLngBounds.builder();
	        for (int i = 0; i < mPois.size(); i++) {
	            b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
	                    mPois.get(i).getLatLonPoint().getLongitude()));
	        }
	        return b.build();
	    }

	    private MarkerOptions getMarkerOptions(int index) {
	        return new MarkerOptions()
	                .position(
	                        new LatLng(mPois.get(index).getLatLonPoint()
	                                .getLatitude(), mPois.get(index)
	                                .getLatLonPoint().getLongitude()))
	                .title(getTitle(index)).snippet(getSnippet(index))
	                .icon(getBitmapDescriptor(index));
	    }

	    protected String getTitle(int index) {
	        return mPois.get(index).getTitle();
	    }

	    protected String getSnippet(int index) {
	        return mPois.get(index).getSnippet();
	    }

	    /**
	     * 从marker中得到poi在list的位置。
	     *
	     * @param marker 一个标记的对象。
	     * @return 返回该marker对应的poi在list的位置。
	     * @since V2.1.0
	     */
	    public int getPoiIndex(Marker marker) {
	        for (int i = 0; i < mPoiMarks.size(); i++) {
	            if (mPoiMarks.get(i).equals(marker)) {
	                return i;
	            }
	        }
	        return -1;
	    }

	    /**
	     * 返回第index的poi的信息。
	     * @param index 第几个poi。
	     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	     * @since V2.1.0
	     */
	    public PoiItem getPoiItem(int index) {
	        if (index < 0 || index >= mPois.size()) {
	            return null;
	        }
	        return mPois.get(index);
	    }

		protected BitmapDescriptor getBitmapDescriptor(int arg0) {
			if (arg0 < 10) {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), markers[arg0]));
				return icon;
			}else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
				return icon;
			}	
		}
	}

}
