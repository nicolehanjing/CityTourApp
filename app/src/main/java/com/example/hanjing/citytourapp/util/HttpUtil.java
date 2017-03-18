package com.example.hanjing.citytourapp.util;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class HttpUtil {

	//连接服务器
	public static final String BASE_URL="http://192.168.1.19:8080/Order_Server/";

	public static HttpGet getHttpGet(String url){
		HttpGet request = new HttpGet(url);
		 return request;
	}
	public static HttpGet getHttpGet(URI url){
		HttpGet request = new HttpGet(url);
		 return request;
	}
	// 获得Post请求对象request
	public static HttpPost getHttpPost(String url){
		 HttpPost request = new HttpPost(url);
		 return request;
	}
	// 根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	// 根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	
	// 发送Post请求，获得响应查询结果
	public static String queryStringForPost(String url){
		// 根据url获得HttpPost对象
		HttpPost request = HttpUtil.getHttpPost(url);
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(response.getStatusLine().getStatusCode()==200){
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		}
        return null;
    }
	// 获得响应查询结果
	public static String queryStringForPost(HttpPost request){
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(response.getStatusLine().getStatusCode()==200){
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		}
        return null;
    }

	// 发送Get请求，获得响应查询结果
	public static  String queryStringForGet(String url){
		// 获得HttpGet对象
		HttpGet request = HttpUtil.getHttpGet(url);
		request.addHeader("Content-Type", "text/html"); //这行很重
		request.addHeader("charset", "utf-8");         //这行很重
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(response.getStatusLine().getStatusCode()==200){
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		}
        return null;
    }
	
	public static  String queryStringForGet(URI url){
		// 获得HttpGet对象
		HttpGet request = HttpUtil.getHttpGet(url);
		request.addHeader("Content-Type", "text/html"); //这行很重要
		request.addHeader("charset", "utf-8");         //这行很重要
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(response.getStatusLine().getStatusCode()==200){
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		}
        return null;
    }

}
