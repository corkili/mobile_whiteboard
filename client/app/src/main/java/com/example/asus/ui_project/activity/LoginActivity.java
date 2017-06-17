package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {
    private EditText userName;//用户名
    private EditText userPassword;//用户密码
    private long exitTime = 0;//时间
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private JSONObject result;//json结果对象
    private ProgressDialog progressDialog;//进度框

    //异步网络处理

    private Runnable request=new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what=MessageCode.MSG_LOGIN;
            // 发送网络请求
            result = Webservice.login(userName.getText().toString(),userPassword.getText().toString());
            handler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try{
                if(msg.what== MessageCode.MSG_LOGIN) {
                    if(result == null) {
                        Toast.makeText(getApplicationContext(),"登陆失败，请检查网络",Toast.LENGTH_SHORT).show();//请求失败
                        //dismiss
                    }
                    else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        // result.getString("message");//提示信息，不用
                        // login success
                        //将登录信息存至SP对象,跳转页面
                        JSONObject userInfo = result.getJSONObject("user_info");
                        editor.putBoolean("login", true);//已经登录
                        editor.putString("name", userName.getText().toString());//已登录手机号
                        editor.putString("password", userPassword.getText().toString());//记录密码
                        editor.putString("username", userInfo.getString("username"));
                        editor.putInt("user_id", userInfo.getInt("user_id"));
                        editor.commit();//提交
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();//销毁当前页面
                        //dismiss
                    } else {
                        Toast.makeText(getApplicationContext(),"登陆失败，请检查用户名或密码",Toast.LENGTH_SHORT).show(); // login failed
                        //dismiss
                    }
                }
                else
                {
                    ;//dismiss,可以不用，假装
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    };//响应回调

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initData();
    }

    private void initData() {
        sharedPreferences = getSharedPreferences("data",
                Activity.MODE_PRIVATE);//SP对象
        editor = sharedPreferences.edit();
        userName = (EditText) findViewById(R.id.userName_Login_ET);
        userPassword = (EditText) findViewById(R.id.userPass_Login_ET);
        Boolean is_login = sharedPreferences.getBoolean("login", false);//用户是否已经登录
        if (is_login)//如果已经登录
        {
            String name = sharedPreferences.getString("name", "");
            String password = sharedPreferences.getString("password", "");
            Login(name, password);//执行登录操作
        } else
            userName.setText(sharedPreferences.getString("name", ""));
    }

    private void Login(String name, String password) {
        if (Login_is_correct(name, password)) {
            showProcessDialog(this,"登陆中...");
            new Thread(request).start();
            //show ProcessDialog
        }
    }

    public void Sure_Login(View v) {
        String name = userName.getText().toString();
        String password = userPassword.getText().toString();
        Login(name, password);//执行登录操作
    }//确认登录按钮监听器

    private Boolean Login_is_correct(String name, String password) {
        if (name.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "手机号或密码不能为空哦~", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }//判断用户名和密码输了没有

    public void NewUserRegister(View v) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }//新用户注册监听器

    public void ForgetPassword(View v) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }//忘记密码监听器

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();//返回监听函数
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }//返回按键监听器

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }//退出时间间隔判断

    //ProgcessDialog
    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }


}
