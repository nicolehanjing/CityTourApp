package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.help.Tip;
import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.adapter.ListAdapter;
import com.example.hanjing.citytourapp.app.MyApplication;
import com.example.hanjing.citytourapp.model.RequestVo;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.JsonUtils;
import com.example.hanjing.citytourapp.util.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.example.hanjing.citytourapp.R.id.add_end_bt;
import static com.example.hanjing.citytourapp.R.id.btn_add;

public class ActivityPublish extends Activity implements View.OnClickListener, NumberPicker.OnValueChangeListener {


    private TextView from_place, date, time_value, number, endlocTv, priority, money_view, titleTv;
    private Button submitButton, cancleButton, addButton, addPriority, subPriority;
    private ImageView backButton, telephone;
    private ListView request_list;
    private ListAdapter listAdapter;

    private Calendar calendar;
    private DatePickerDialog dialog;
    private TimePickerDialog dialog2;

    private List<RequestVo> list = new ArrayList<RequestVo>();

    private Tip startTip;

    private String time;
    private int money;
    private int num = 1;
    private int priority_value = 1; //优先级
    private int flag = 0;
    private LinearLayout addLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        flag = getIntent().getIntExtra("flag",0);

        addLayout = (LinearLayout)findViewById(R.id.add_layout);
        titleTv = (TextView)findViewById(R.id.jy_title_tv);
        endlocTv = (TextView)findViewById(R.id.endloc_tv);

        from_place = (TextView)findViewById(R.id.from_place);
        date = (TextView)findViewById(R.id.date);
        time_value = (TextView)findViewById(R.id.time);
        number = (TextView)findViewById(R.id.number);
        money_view = (TextView)findViewById(R.id.request_money_et);
        priority = (TextView)findViewById(R.id.priority);
        submitButton = (Button) findViewById(R.id.submit);
        cancleButton = (Button) findViewById(R.id.clear_end_bt);
        addButton = (Button) findViewById(add_end_bt);
        endlocTv = (TextView)findViewById(R.id.endloc_tv);
        addPriority = (Button) findViewById(btn_add);
        subPriority = (Button) findViewById(R.id.btn_sub);
        backButton = (ImageView)findViewById(R.id.back);
        telephone = (ImageView)findViewById(R.id.telephone);

        //listview适配
        request_list = (ListView)findViewById(R.id.request_list);
        listAdapter = new ListAdapter(this, list);
        request_list.setAdapter(listAdapter);

        if(flag == 1){
            addLayout.setVisibility(View.GONE);
            if(MyApplication.getInstance().getUser().type == 0){//乘客
                titleTv.setText("搭顺风车");
            }else {
                titleTv.setText("查看派单");
            }

        }else {
            addLayout.setVisibility(View.VISIBLE);
            if(MyApplication.getInstance().getUser().type == 0){//乘客
                titleTv.setText("发布需求");
            }else {
                titleTv.setText("发布顺风车");
            }
        }


        //监听
        from_place.setOnClickListener(this);
        date.setOnClickListener(this);
        time_value.setOnClickListener(this);
        number.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        cancleButton.setOnClickListener(this);
        addPriority.setOnClickListener(this);
        subPriority.setOnClickListener(this);
        backButton.setOnClickListener(this);
        telephone.setOnClickListener(this);

        request_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ActivityPublish.this,ActivityDetail.class);
                intent.putExtra("req", list.get(position));
                startActivity(intent);
            }
        });
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResult();
            }
        });
    }

    //点击按钮
    @Override
    public void onClick(View v) {

        Intent intent ;
        if (v == from_place) {
            intent = new Intent(ActivityPublish.this, InputtipsActivity.class);
            startActivityForResult(intent, 1);
        }

        else if (v == addButton) {
            intent = new Intent(ActivityPublish.this, InputtipsActivity.class);
            startActivityForResult(intent, 2);
        }


        else if (v == date) {
            calendar = Calendar.getInstance();
            dialog = new DatePickerDialog(ActivityPublish.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    System.out.println("年-->" + year + "月-->" + month + "日-->" + day);
                    date.setText(year + "/" + month + "/" + day);

                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        }

         else if (v == time_value) {
            calendar = Calendar.getInstance();
            dialog2 = new TimePickerDialog(ActivityPublish.this, new TimePickerDialog.OnTimeSetListener() {


                @Override
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    System.out.println("时-->" + hour + ":" + "分-->" + minute);
                    time_value.setText(hour + ":" + minute);

                }
            }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);

            dialog2.show();
        }

        else if (v == number) {
            showD();
        }


        else if (v == cancleButton) {
            endTips.clear();
            endlocTv.setText("");
            endlocTv.setVisibility(View.GONE);
        }

            //用户提交
            else if (v == submitButton) {
            if (null == startTip) {
                Toast.makeText(ActivityPublish.this, "请输入起始位置", Toast.LENGTH_SHORT).show();
                return;
            }
            if (endTips.size() == 0) {
                Toast.makeText(ActivityPublish.this, "请输入目的地", Toast.LENGTH_SHORT).show();
                return;
            }
            time = time_value.getText().toString();
            if (StringUtils.isBlank(time)) {
                Toast.makeText(ActivityPublish.this, "请输入出发时间", Toast.LENGTH_SHORT).show();
                return;
            }
            new AddDataTask().execute();
        }


        else if (v == addPriority) {//加
            if (priority_value < 4) {
                priority_value++;
                priority.setText(priority_value + "");
            }
        }

         else if (v == subPriority) {//减
            if (priority_value > 1) {
                priority_value--;
                priority.setText(priority_value + "");
            }
        }

        else if (v == backButton) {
            intent = new Intent(ActivityPublish.this, MainActivity.class);
            startActivity(intent);
        }

        else if (v == telephone){
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:17710266279"));
            startActivity(intent);

        }
    }


    //show the number of people
    private void showD() {

        final Dialog d = new Dialog(ActivityPublish.this);
        d.setTitle("Number of People");
        d.setContentView(R.layout.activity_number_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        np.setMaxValue(4); // max value 4
        np.setMinValue(1);   // min value 1
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                number.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();

    }

    //数字选择器
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is","" + newVal);
    }


    private List<Tip>  endTips = new ArrayList<Tip>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            switch (requestCode) {
                case 1:
                    startTip =	data.getParcelableExtra("result");
                    from_place.setText(startTip.getDistrict() + startTip.getName());

                    searchResult();
                    break;
                case 2:
                    Tip	endTip = data.getParcelableExtra("result");
                    endTips.add(endTip);
                    String end = "";
                    for (Tip tip: endTips){
                        end = end + tip.getDistrict() +  tip.getName() +"\n";
                    }

                    endlocTv.setText(end);
                    endlocTv.setVisibility(View.VISIBLE);
                    searchResult();
                    break;
                default:
                    break;
            }


        }

    }


    List<String> locations = new ArrayList<String>();
    private void  searchResult() {

        if(null == startTip || endTips.size() <= 0){
            return;
        }
        locations.clear();
        locations.add(startTip.getPoint().toString());


        for (int i = 0; i < endTips.size(); i++) {
            locations.add(endTips.get(i).getPoint().toString());
        }

        for (int i = 1; i < locations.size(); i++) {
            int startNum = i;
            int num = i;
            float dis = 100000000;
            for (int j = i; j < locations.size(); j++) {
                String startloc = locations.get(i - 1);
                String curloc = locations.get(j);


                float distance = AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(startloc.split(",")[0]),
                        Double.parseDouble(startloc.split(",")[1])), new LatLng(Double.parseDouble(curloc.split(",")[0]),
                        Double.parseDouble(curloc.split(",")[1])));

                if (distance < dis) {
                    num = j;
                    dis = distance;

                }

            }

            if (startNum != num) {
                String temp = locations.get(startNum);
                locations.set(startNum, locations.get(num));
                locations.set(num, temp);
            }

        }
        float alldis = 0;
        for (int i = 0; i < locations.size() - 1; i++) {
            String startloc = locations.get(i);
            String curloc = locations.get(i + 1);


            float distance = AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(startloc.split(",")[0]),
                    Double.parseDouble(startloc.split(",")[1])), new LatLng(Double.parseDouble(curloc.split(",")[0]),
                    Double.parseDouble(curloc.split(",")[1])));
            alldis = alldis + (int) (distance);
        }
        if(StringUtils.isNotBlank(number.getText().toString())){
            num = Integer.parseInt(number.getText().toString());
        }

        money = (int) ((alldis / 1000.0) * 2.3) * num;
        money_view.setText("Cost："+ money +"RMB");
    }



    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new GetDataTask().execute();
    }

    //异步处理
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            // 这个方法中的所有代码都会在子线程中运行，在这里去处理所有的耗时任务

            String result = query();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            //当后台任务执行完毕并通过 return 语句进行返回时，这个方法就很快会被调用
            if (result != null) {
                Type listType = new TypeToken<List<RequestVo>>() {}.getType();
                list.clear();
                list = (List<RequestVo>) JsonUtils.StringFromJson(result,listType);
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
            }
        }
    }


    private String query() {
        // +"&zh_id=" + MyApplication.getInstance().getZhID()
        String queryString =  "op=3" +"&user_id="+ MyApplication.getInstance().getUser().id
                +"&flag=" + flag + "&type=" + MyApplication.getInstance().getUser().type;
        // url
        String url = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?" + queryString;
        return HttpUtil.queryStringForPost(url);
    }



    private class AddDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Simulates a background job.
            String result = add();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null && result.equals("1")) {
                from_place.setText("");
                time_value.setText("");
                number.setText("");
                endTips.clear();
                endlocTv.setText("");
                endlocTv.setVisibility(View.GONE);
                money_view.setText("Cost：unknown");
                Toast.makeText(ActivityPublish.this, "提交成功", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();

            }else {
                Toast.makeText(ActivityPublish.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String add() {
        RequestVo requestVo = new RequestVo();
        requestVo.user_id = MyApplication.getInstance().getUser().id;
        requestVo.start_loc = startTip.getPoint().toString();
        requestVo.start_add = startTip.getDistrict() + startTip.getName();
        String endLoc = "";
        String endADDress = "";
        for (Tip tip :endTips ){
            endLoc = endLoc + ":" +tip.getPoint().toString();
            endADDress = endADDress + ":"+tip.getDistrict() + tip.getName();
        }
        requestVo.end_loc = endLoc;
        requestVo.end_add = endADDress;
        requestVo.type = MyApplication.getInstance().getUser().type;
        requestVo.status = 0;
        requestVo.time = time;
        num = Integer.parseInt(number.getText().toString());
        requestVo.number = num;
        requestVo.money = money;
        requestVo.uuid = UUID.randomUUID().toString();
        int p = 0;
        String pString = priority.getText().toString();
        if(StringUtils.isNotBlank(pString)){
            p = Integer.parseInt(pString);
        }

        requestVo.priority = p;
        requestVo.receiving_id = 0;



        String queryString =  "op=" + 2 +"&req=" + JsonUtils.createJsonString(requestVo) ;
        // url
        String strUrl = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?" + queryString;


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
