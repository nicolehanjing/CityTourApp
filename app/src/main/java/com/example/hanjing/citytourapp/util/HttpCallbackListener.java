package com.example.hanjing.citytourapp.util;

/**
 * Created by hanjing on 2017/3/18.
 */

public interface HttpCallbackListener {

    //回调天气服务返回的结果
    void onFinish(String response);
    void onError(Exception e);
}
