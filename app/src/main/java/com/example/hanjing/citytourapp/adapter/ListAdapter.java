package com.example.hanjing.citytourapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.model.RequestVo;
import com.example.hanjing.citytourapp.util.StringUtils;

import java.util.List;

public class ListAdapter extends BaseAdapter {
	private ViewHolder holder; // 视图容器
	private LayoutInflater layoutInflater;
	private Context context;
	private List<RequestVo> list; // 请求列表



	public ListAdapter(Context context, List<RequestVo> list) {

		this.context = context;
		this.list = list;
		this.layoutInflater = LayoutInflater.from(context);
		
	}

	public void setList(List<RequestVo> list) {
		this.list = list;
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 组装数据
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_detail,null);
					
			holder = new ViewHolder();
			
			holder.re_status_tv      = (TextView) convertView.findViewById(R.id.re_status_tv);
			holder.re_time_tv      = (TextView) convertView.findViewById(R.id.re_time_tv);
			holder.re_start_tv      = (TextView) convertView.findViewById(R.id.re_start_tv);
			holder.re_end_tv      = (TextView) convertView.findViewById(R.id.re_end_tv);
			
			// 使用tag存储数据
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RequestVo item = list.get(position); // 获取当前数据
		if (item != null) {
			
			String statusS = "待接单中";
			if(item.status == 0){
				statusS = "待接单中";
			}else if(item.status == 1){
				statusS = "进行中";
			}else if(item.status == 2){
				statusS = "已完成";
			}else if(item.status == 3){
				statusS = "已取消";
			}
			holder.re_status_tv .setText("状态:"+statusS);    ;
			holder.re_time_tv .setText("Time："+(StringUtils.isBlank(item.time)?"":item.time));    ;
			holder.re_start_tv .setText("起始位置："+(StringUtils.isBlank(item.start_add)?"":item.start_add));
			holder.re_end_tv    .setText("结束位置："+(StringUtils.isBlank(item.end_add)?"":item.end_add));    ;
			
		}

		return convertView;
	}

	public static class ViewHolder {
		
		TextView re_end_tv;
		TextView re_status_tv;
		TextView re_time_tv;
		TextView re_start_tv;

	}

}
