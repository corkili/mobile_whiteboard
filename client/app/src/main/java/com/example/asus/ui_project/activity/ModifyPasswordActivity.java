package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;

import org.json.JSONException;
import org.json.JSONObject;


public class ModifyPasswordActivity extends Activity {
    private EditText now_pass;//现在密码
    private EditText new_pass1;//新密码
    private EditText new_pass2;//新密码
    private CheckBox look;//显示密码
    private JSONObject result;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;//进度框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifypasswordlayout);
        sharedPreferences = getSharedPreferences("data",
                Activity.MODE_PRIVATE);//SP对象
        editor = sharedPreferences.edit();
        now_pass= (EditText) findViewById(R.id.Modi_now_pass);
        new_pass1=(EditText)findViewById(R.id.Modi_new_pass1);
        new_pass2=(EditText)findViewById(R.id.Modi_new_pass2);
        look= (CheckBox) findViewById(R.id.view_password);
        look.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    now_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    new_pass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    new_pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    now_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    new_pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    new_pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });//显示密码选择框
    }

    public void Sure_modify(View v){
        String pass1 = new_pass1.getText().toString();
        String pass2 = new_pass2.getText().toString();
        if(!now_pass.getText().toString().equals(sharedPreferences.getString("password", ""))) {
            Toast.makeText(getApplicationContext(), "旧密码错误", Toast.LENGTH_LONG).show();
        } else if (pass1.length() < 6 || pass1.length() > 20 || pass2.length() < 6 || pass2.length() > 20 || !pass1.equals(pass2)) {
            Toast.makeText(getApplicationContext(), "两次输入的密码不相同或不满足长度要求(6-20位)", Toast.LENGTH_SHORT).show();
        } else {
            showProcessDialog(ModifyPasswordActivity.this, "正在修改密码");
            new Thread(requestModifyPassword).start();
        }
    }

    public void Btn_Back_Mod(View v)
    {
        finish();
    }//返回按钮

    private Runnable requestModifyPassword = new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what= MessageCode.MSG_MODIFY_PASSWORD;
            // 发送网络请求
            result = Webservice.modifyPassword(sharedPreferences.getString("name", ""), new_pass1.getText().toString());
            modifyPasswordHandler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Handler modifyPasswordHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try{
                if(msg.what== MessageCode.MSG_MODIFY_PASSWORD) {
                    if(result == null) {
                        Toast.makeText(getApplicationContext(),"请求失败，请检查网络",Toast.LENGTH_SHORT).show();//请求失败
                        //dismiss
                    }
                    else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        Toast.makeText(getApplicationContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                        editor.remove("password");
                        editor.putString("password", new_pass1.getText().toString());
                        editor.commit();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
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

    //ProgcessDialog
    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }
}
