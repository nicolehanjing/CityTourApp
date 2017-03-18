package com.example.hanjing.citytourapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.hanjing.citytourapp.util.HttpCallbackListener;
import com.example.hanjing.citytourapp.util.Utility;
import com.example.hanjing.citytourapp.util.weatherHttpUtil;

/**
 * Created by hanjing on 2017/3/18.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE); int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 更新天气信息。 */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";


        weatherHttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
