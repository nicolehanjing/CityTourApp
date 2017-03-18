package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.app.MyApplication;
import com.example.hanjing.citytourapp.model.UserVo;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputtipsActivity extends Activity implements TextWatcher, InputtipsListener {

	private String city = "北京";
	private AutoCompleteTextView mKeywordText;
	private ListView minputlist;
	private List<Tip> tips = new ArrayList<Tip>();
	private int click_position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputtip);

		minputlist = (ListView)findViewById(R.id.inputlist);
		mKeywordText = (AutoCompleteTextView)findViewById(R.id.input_edittext);
        mKeywordText.addTextChangedListener(this);
        minputlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				//click_position = position;
				//new GetDataTask().execute();
				
				
				//数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", tips.get(position));
                //设置返回数据
                InputtipsActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                InputtipsActivity.this.finish();
				
				
			}
		});
        
        
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		if(StringUtils.isNotBlank(MyApplication.getInstance().getCity())){
			city = MyApplication.getInstance().getCity();
		}
		
        InputtipsQuery inputquery = new InputtipsQuery(newText, city);
        inputquery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputtipsActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == 1000) {
        	tips.clear();
        	tips.addAll(tipList);
        	List<HashMap<String, String>> listString = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
            	HashMap<String, String> map = new HashMap<String, String>();
            	map.put("name", tipList.get(i).getName());
            	map.put("address", tipList.get(i).getDistrict());
            	//map.put("address", ""+tipList.get(i).getPoint());
                listString.add(map);
            }

            SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), listString, R.layout.item_layout, 
            		new String[] {"name","address"}, new int[] {R.id.poi_field_id, R.id.poi_value_id});

            minputlist.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();

        }
		
	}

	
	private class GetDataTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			
			

			String result = query();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(null != result && "1".equals(result)){//保存成功
				flag = 0;
				showDialog("家庭地址设置成功");
				UserVo userVo = MyApplication.getInstance().getUser();
				userVo.home_location = "" + tips.get(click_position).getPoint();
				MyApplication.getInstance().setUser(userVo);
				
			}else {
				flag = 1;
				showDialog("保存失败，请稍后重试");
				
			}
			

		}
	}

	private int  flag = 0;
	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(flag == 0){
							InputtipsActivity.this.finish();
						}
						
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	private String query() {
		String queryString = "op=3"+ "&home_location=" + tips.get(click_position).getPoint()
				+ "&id=" + MyApplication.getInstance().getUser().id;
		// url
		String url = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?" + queryString;

		return HttpUtil.queryStringForPost(url);
	}

}
