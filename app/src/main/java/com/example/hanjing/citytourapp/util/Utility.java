package com.example.hanjing.citytourapp.util;

import android.text.TextUtils;
import com.example.hanjing.citytourapp.model.*;


import com.example.hanjing.citytourapp.db.WeatherDB;

/**
 * Created by hanjing on 2017/3/18.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据 */
    public synchronized static boolean handleProvincesResponse(WeatherDB WeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]); // 将解析出来的数据存储到Province表

                    WeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据 */
    public static boolean handleCitiesResponse(WeatherDB WeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();

                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    // 将解析出来的数据存储到City表
                    WeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据 */
    public static boolean handleCountiesResponse(WeatherDB WeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    Country county = new Country();
                    county.setCountryCode(array[0]);
                    county.setCountryName(array[1]);
                    county.setCityId(cityId);

                    // 将解析出来的数据存储到County表
                    WeatherDB.saveCountry(county);
                }
                return true;
            }
        }
        return false;
    }
}
