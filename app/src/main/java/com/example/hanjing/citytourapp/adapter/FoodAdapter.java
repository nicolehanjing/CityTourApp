package com.example.hanjing.citytourapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.model.Food;

import java.util.List;

/**
 * Created by hanjing on 2017/3/16.
 */

public class FoodAdapter extends ArrayAdapter<Food> {
    private int resourceId;

    public FoodAdapter(Context context, int textViewResourceId, List<Food> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Food food = getItem(position); // 获取当前项的Food实例
        //View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        View view;
        ViewHolder viewHolder;//用于对控件的实例进行缓存

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.foodImage = (ImageView) view.findViewById(R.id.food_image);
            viewHolder.foodName = (TextView) view.findViewById(R.id.food_name);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
             }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }

        viewHolder.foodImage.setImageResource(food.getImageId());
        viewHolder.foodName.setText(food.getName());

        return view;
    }

    class ViewHolder {

        ImageView foodImage;
        TextView foodName;
    }
}
