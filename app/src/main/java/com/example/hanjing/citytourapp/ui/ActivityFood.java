package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.adapter.FoodAdapter;
import com.example.hanjing.citytourapp.model.Food;

import java.util.ArrayList;
import java.util.List;

public class ActivityFood extends Activity implements SensorEventListener {


    private List<Food> foodList = new ArrayList<Food>();
    private TextView lightLevel, show_change;
    private SensorManager sensorManager;
    private Sensor mag_sensor;
    //加速度传感器数据
    float accValues[]=new float[3];
    //地磁传感器数据
    float magValues[]=new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[]=new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float values[]=new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        lightLevel = (TextView)findViewById(R.id.light_level);
        show_change = (TextView)findViewById(R.id.show_change);

        //传感器
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);//光照传感器
        Sensor sensor_gravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//磁场传感器

        //给传感器注册监听
        sensorManager.registerListener(this, sensor_light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensor_gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mag_sensor,SensorManager.SENSOR_DELAY_GAME);

        initFood(); // 初始化食物数据

        FoodAdapter foodadapater = new FoodAdapter(ActivityFood.this, R.layout.food_item, foodList);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(foodadapater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = foodList.get(position);
                Toast.makeText(ActivityFood.this, food.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFood() {
        Food duck = new Food("Beijing Roast Duck", R.drawable.roast_duck);
        foodList.add(duck);
        Food lv = new Food("Soybean-flour Cake", R.drawable.lvdagun);
        foodList.add(lv);
        Food hulu = new Food("Candied Haw", R.drawable.hulu);
        foodList.add(hulu);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);//释放使用的资源
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // values数组中第一个下标的值就是当前的光照强度
        float value = event.values[0];
        lightLevel.setText("Current light level is " + value + " lx");

        // 加速度可能会是负值，所以要取它们的绝对值
        float xValue = Math.abs(event.values[0]);
        float yValue = Math.abs(event.values[1]);
        float zValue = Math.abs(event.values[2]);
        if (xValue > 15 || yValue > 15 || zValue > 15) {
            // 认为用户摇动了手机，触发摇一摇逻辑
            Toast.makeText(ActivityFood.this, "摇一摇", Toast.LENGTH_SHORT).show();
        }

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

        //将弧度转化为角度后输出
        StringBuffer buff=new StringBuffer();
        for(float i:values){
            i=(float) Math.toDegrees(i);
            buff.append(i + "  ");
        }
        show_change.setText(buff.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
