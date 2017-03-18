package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanjing.citytourapp.R;
import com.example.hanjing.citytourapp.app.MyApplication;
import com.example.hanjing.citytourapp.model.UserVo;
import com.example.hanjing.citytourapp.util.HttpUtil;
import com.example.hanjing.citytourapp.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends Activity implements OnClickListener {

    private EditText login_phone;
    private EditText login_password;
    private Button startButton;
    private ImageView eye;
    private CheckBox rememberPass;

    private Boolean showPassword = false;

    //存储密码
    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);


        login_phone = (EditText)findViewById(R.id.mobileInput);
        login_password = (EditText)findViewById(R.id.passwordInput);
        startButton = (Button)findViewById(R.id.startButton);
        eye = (ImageView)findViewById(R.id.eye);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);


        //记住密码
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        editor = pref.edit();

        if(isRemember){
            // 将账号和密码都设置到文本框中
            String account = pref.getString("phone", "");
            String password = pref.getString("password", "");
            login_phone.setText(account);
            login_password.setText(password);
            rememberPass.setChecked(true);
        }


        startButton.setOnClickListener(this);
        eye.setOnClickListener(this);
        ((TextView)findViewById(R.id.registbtn)).setOnClickListener(this);
    }


    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job
            String username = login_phone.getText().toString();
            String pwd = login_password.getText().toString();

            String result = query(username, pwd);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null ) {
                try {
                    JSONObject object = new JSONObject(result);
                    int error = object.getInt("error");
                    if(-1 == error){
                        Toast.makeText(ActivityLogin.this, "用户名或密码不对，请重新输入", Toast.LENGTH_LONG).show();
                        //new AlertDialog.Builder(ActivityLogin.this).setTitle("Wrong!")
                                //.setMessage("用户名或密码不对，请重新输入").create().show();

                    } else if(1 == error) {
                        String  userData = object.getString("user");
                        UserVo userVo = (UserVo) JsonUtils.StringFromJson2(userData, UserVo.class);
                        MyApplication.getInstance().setUser(userVo);

                        Intent i = new Intent(ActivityLogin.this, MainActivity.class);
                        startActivity(i);
                        ActivityLogin.this.finish();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                new AlertDialog.Builder(ActivityLogin.this).setTitle("WRONG!")
                        .setMessage("登陆失败，请稍后重试").create().show();
            }

        }
    }

    // 验证方法
    private boolean validate() {
        String userphone = login_phone.getText().toString();
        if (userphone.equals("")) {

            showDialog("号码不能为空");
            return false;
        }
        String pwd = login_password.getText().toString();
        if (pwd.equals("")) {
            showDialog("密码不能为空");
            return false;
        }
        return true;
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    // 根据用户名称密码查询
    private String query(String account, String password) {
        String queryString = "account=" + account + "&password=" + password;
        // url
        String url = HttpUtil.BASE_URL + "servlet/AppLoginServlet?" + queryString;
        return HttpUtil.queryStringForPost(url);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.registbtn:
                Intent  intent = new Intent(ActivityLogin.this,ActivityRegister.class);
                startActivity(intent);
                break;

            //开始按钮
            case R.id.startButton:
                if(validate()){

                    String phone = login_phone.getText().toString();
                    String password = login_password.getText().toString();
                    if(rememberPass.isChecked()){// 检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                    } else {editor.clear();}

                    editor.commit();

                    new GetDataTask().execute();
                }
                break;

            //记住密码
            case R.id.eye:
                if(!showPassword){//显示密码
                    showPassword = !showPassword;
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{//隐藏密码
                    showPassword = !showPassword;
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

        }
    }

}