package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.PhoneUtils;

import static com.example.hanjing.citytourapp.R.id.nameInput;
import static com.example.hanjing.citytourapp.R.id.radioGroup;
import static com.example.hanjing.citytourapp.R.id.roleDescView;

public class ActivityRegister extends Activity implements OnClickListener {

    private EditText register_name;
    private EditText register_password;
    private EditText register_conf_pwd;
    private EditText register_phone;
    private RadioButton riderRadio;
    private TextView role_view;
    private RadioGroup radioGroup;

    private boolean roleSelected = false;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_name = (EditText)findViewById(nameInput);
        register_password = (EditText)findViewById(R.id.password);
        register_conf_pwd = (EditText)findViewById(R.id.confirm);
        register_phone = (EditText)findViewById(R.id.phone);
        riderRadio = (RadioButton)findViewById(R.id.driverRButton);
        role_view = (TextView) findViewById(R.id.roleView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        type = riderRadio.isChecked()? 0 :1;//0 rider 1 driver

        Typeface OpenSans = Typeface.createFromAsset(getAssets(), "fonts/OpenSans.ttf");
        role_view.setTypeface(OpenSans);

        //监听按钮
        ((Button)findViewById(R.id.registerButton)).setOnClickListener(this);
        ((Button)findViewById(R.id.backButton)).setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.driverRButton) {
                    role_view.setText("You are going to be a driver.");
                    roleSelected = true;
                    type=1;
                } else if (checkedId == R.id.riderRButton) {
                    role_view.setText("You are going to be a rider.");
                    roleSelected = true;
                    type=0;
                }
            }
        });
    }



    private class GetDataTask extends AsyncTask<Void, Void, String> {

        //此方法中定义要执行的后台任务，在这个方法中可以调用publishProgress来更新任务进度
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job
            String username = register_name.getText().toString();
            String pwd = register_password.getText().toString();
            String phone = register_phone.getText().toString();
            int type = riderRadio.isChecked()? 0 :1;//0 rider 1 driver

            String result = query(username,pwd,phone,type);
            return result;
        }

        //此方法会在后台任务执行后被调用
        @Override
        protected void onPostExecute(String result) {

            if (result != null && result.equals("1")) {
                showDialog("注册成功");
                register_name.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ActivityRegister.this.finish();
                    }
                }, 2000);


            } else if (result != null && result.equals("2")){
                showDialog("此手机号已经注册过了");
            }else {
                showDialog("注册失败，请稍后重试");
            }

        }
    }

    // 验证方法
    private boolean validate() {
        String username = register_name.getText().toString();
        if (username.equals("")) {

            showDialog("昵称不能为空");
            return false;
        }
        String pwd = register_password.getText().toString();
        if (pwd.equals("")) {
            showDialog("密码不能为空");
            return false;
        }

        String conpwd = register_conf_pwd.getText().toString();
        if (conpwd.equals("")) {
            showDialog("确认密码不能为空");
            return false;
        }
        if(!pwd.equals(conpwd)){
            showDialog("二次输入密码不一致");
            return false;
        }

        //判断手机格式是否正确
        String phone = register_phone.getText().toString();
        if(!PhoneUtils.isMobilePhone(phone)){
            showDialog("手机号格式不正确");
            return false;
        }

        //判断是否选择role
        if(!roleSelected){
            showDialog("Please select a role");
            return false;
        }
        return true;
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 根据用户名称密码查询
    private String query(String username, String pwd, String phone, int role) {
        String queryString = "username=" + username + "&pwd=" + pwd + "&phone=" + phone + "&sex=" + role ;
        // url
        String url = HttpUtil.BASE_URL + "servlet/AppRegisterServlet?" + queryString;
        return HttpUtil.queryStringForPost(url);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.backButton:
                ActivityRegister.this.finish();
                break;

            case R.id.registerButton:
                if(validate()){
                    new GetDataTask().execute();
                }
                break;

        }

    }
}
