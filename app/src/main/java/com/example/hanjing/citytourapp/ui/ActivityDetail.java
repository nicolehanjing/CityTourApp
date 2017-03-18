package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.app.MyApplication;
import com.example.hanjing.citytourapp.model.RequestVo;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.JsonUtils;
import com.example.hanjing.citytourapp.util.StringUtils;

import java.net.URI;
import java.net.URL;

/**
 * Created by hanjing on 2017-2-20 下午11:08:48
 * Describe :
 */
public class ActivityDetail extends Activity {
    
	private Button bxbt;
	private TextView reStatus,reTime,reStart,reEnd,reMoney;
	private RequestVo requestVo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		requestVo = (RequestVo) getIntent().getSerializableExtra("req");
		
		reStatus = (TextView)findViewById(R.id.re_status_tv);
		reTime = (TextView)findViewById(R.id.re_time_tv);
		reStart = (TextView)findViewById(R.id.re_start_tv);
		reEnd = (TextView)findViewById(R.id.re_end_tv);
		reMoney = (TextView)findViewById(R.id.re_money_tv);
		bxbt = (Button)findViewById(R.id.bx_bt);

		
		reTime.setText("时间："+(StringUtils.isBlank(requestVo.time)?"":requestVo.time));
		reStart.setText("起始位置："+(StringUtils.isBlank(requestVo.start_add)?"":requestVo.start_add));
		reEnd.setText("结束位置："+(StringUtils.isBlank(requestVo.end_add)?"":requestVo.end_add));
		reMoney.setText("价格:"+requestVo.money + "元");

		setData();
		bxbt = (Button)findViewById(R.id.bx_bt);
	    bxbt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(requestVo.user_id == MyApplication.getInstance().getUser().id){
					requestVo.status = 3;
				}

				else {
					requestVo.status = requestVo.status +1;
					requestVo.receiving_id = MyApplication.getInstance().getUser().id;
				}

				new AddDataTask().execute();
			}
		});
	}


	private void setData(){
		String statusS = "待接单中";
		if(MyApplication.getInstance().getUser().type == 1){//司机
			if(requestVo.status == 0){
				statusS = "待接单中";
				if(requestVo.user_id == MyApplication.getInstance().getUser().id){
					bxbt.setText("取消订单");
					bxbt.setEnabled(true);
				}else {
					bxbt.setText("接单");
					bxbt.setEnabled(true);
				}
				
			}else if(requestVo.status == 1){
				statusS = "进行中";
				bxbt.setText("结束行程");
				bxbt.setEnabled(true);
			}else if(requestVo.status == 2){
				statusS = "已完成";
				bxbt.setText("行程结束");
				bxbt.setEnabled(false);
			}else if(requestVo.status == 3){
				statusS = "订单已取消";
				bxbt.setText("已取消");
				bxbt.setEnabled(false);
			}
		}else {
			if(requestVo.status == 0){
				statusS = "待接单中";
				if(requestVo.user_id == MyApplication.getInstance().getUser().id){
					bxbt.setText("取消订单");
					bxbt.setEnabled(true);
				}else {
					bxbt.setText("搭载顺风车");
					bxbt.setEnabled(true);
				}
				
				
			}else if(requestVo.status == 1){
				statusS = "进行中";
				bxbt.setText("行程中");
				bxbt.setEnabled(false);
			}else if(requestVo.status == 2){
				statusS = "已完成";
				bxbt.setText("行程结束");
				bxbt.setEnabled(false);
			}else if(requestVo.status == 3){
				statusS = "订单已取消";
				bxbt.setText("已取消");
				bxbt.setEnabled(false);
			}
		}
		
		reStatus.setText("状态:"+statusS);    
	}
	private class AddDataTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.

			String result = add();
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (result != null && result.equals("1")) {
				Toast.makeText(ActivityDetail.this, "提交成功", Toast.LENGTH_SHORT).show();
				setData();
			}else {
				Toast.makeText(ActivityDetail.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
			}

		}
	}
	
	private String add() {
		
		String queryString =  "op=4"+"&req=" + JsonUtils.createJsonString(requestVo);
		String strUrl = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?"
				+ queryString;
		
		
		URI uri;
		try {
			URL url = new URL(strUrl);
			uri = new URI(url.getProtocol(), url.getHost()+":8080", url.getPath(), url.getQuery(), null);
			return HttpUtil.queryStringForGet(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
