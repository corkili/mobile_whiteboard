package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPasswordActivity extends Activity {
    private static final String[] AVATARS = new String[400];
    private LinearLayout page1;
    private LinearLayout page2;
    private LinearLayout page3;
    String APPKEY = "1db15e4285f80";//KEY
    String APPSECRET = "a520e87757554fc9a93c49b4c1c62f79";//SECRET
    private EditText phoneNumber;//电话号码
    private EditText validCode;//验证码
    private EditText newPassword1;//新密码1
    private EditText newPassword2;//新密码2
    private String phone = "";
    private String code = "";
    private Boolean page_1 = true;
    private Boolean page_2 = false;
    private ProgressDialog progressDialog;//进度框
    private JSONObject result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpasslayout);
        page1 = (LinearLayout) findViewById(R.id.Lin_for_page1);
        page2 = (LinearLayout) findViewById(R.id.Lin_for_page2);
        page3 = (LinearLayout) findViewById(R.id.Lin_for_page3);
        phoneNumber = (EditText) findViewById(R.id.Edit_for_phone);
        validCode = (EditText) findViewById(R.id.Edit_for_code);
        newPassword1 = (EditText) findViewById(R.id.Edit_for_newpassword);
        newPassword2 = (EditText) findViewById(R.id.Edit_for_repassword);
        SMSSDK.initSDK(this, APPKEY, APPSECRET);//初始化短信SDK
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = Message.obtain();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        });
    }

    private Runnable requestModifyPassword = new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what=MessageCode.MSG_MODIFY_PASSWORD;
            // 发送网络请求
            result = Webservice.modifyPassword(phone, newPassword1.getText().toString());
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

    private Runnable requestValidPhone = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what= MessageCode.MSG_VALID_PHONE;
            // 发送网络请求
            result = Webservice.validatePhone(phoneNumber.getText().toString());
            validPhoneHandler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Handler validPhoneHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try {
                if (msg.what == MessageCode.MSG_VALID_PHONE) {
                    if(result == null) {
                        Toast.makeText(getApplicationContext(),"验证失败，请检查网络",Toast.LENGTH_SHORT).show();//请求失败
                        //dismiss
                    }
                    else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        showProcessDialog(ForgetPasswordActivity.this,"获取验证码...");
                        SMSSDK.getVerificationCode("86", phone);//请求获取验证码
                        //发送验证码，发送成功就跳转
                    } else {
                        Toast.makeText(getApplicationContext(), "用户不存在", Toast.LENGTH_SHORT).show();;// login failed
                        //dismiss
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    progressDialog.dismiss();//关闭验证框
                    page1.setVisibility(View.INVISIBLE);
                    page2.setVisibility(View.VISIBLE);//获取成功就跳转至输入验证码界面
                    page_1 = false;
                    page_2 = true;
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //验证成功
                    progressDialog.dismiss();//关闭验证框
                    page2.setVisibility(View.INVISIBLE);
                    page3.setVisibility(View.VISIBLE);
                    page_2 = false;
                } else if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
                    //不做任何事
                } else {
                    Toast.makeText(getApplicationContext(), "验证码错误!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                //回调失败
                if (page_1) {
                    progressDialog.dismiss();//关闭验证框
                    Toast.makeText(getApplicationContext(), "获取验证码失败,请检查网络",
                            Toast.LENGTH_SHORT).show();
                } else if (page_2) {
                    progressDialog.dismiss();//关闭验证框
                    Toast.makeText(getApplicationContext(), "验证码错误，请重试",
                            Toast.LENGTH_SHORT).show();
                }
                ((Throwable) data).printStackTrace();
            }
        }
    };

    private void VaildateputInfo() {
        vaildatePassword();
    }

    //验证 验证码
    private void vaildatePassword() {
        SMSSDK.submitVerificationCode("86", phone, code);
        putUserInfo("86", phone);
    }

    //提交用户信息
    private void putUserInfo(String country, String phone) {
        Random rnd = new Random();
        int id = Math.abs(rnd.nextInt());
        String uid = String.valueOf(id);
        String nickName = "SmsSDK_User_" + uid;
        String avatar = AVATARS[id % 12];
        SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
    }

    public void Btn_onclick_next1(View v) {
        phone = phoneNumber.getText().toString();
        if (phone.length() != 11) {
            Toast.makeText(getApplicationContext(), "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(requestValidPhone).start();
            showProcessDialog(ForgetPasswordActivity.this, "验证手机号...");
        }
    }//填写了正确的电话号码

    public void Btn_onclick_next2(View v) {
        code = validCode.getText().toString();
        if (code.length() != 4) {
            Toast.makeText(getApplicationContext(), "验证码错误，请重试", Toast.LENGTH_SHORT).show();
        } else
        {
            showProcessDialog(ForgetPasswordActivity.this,"验证中...");
            VaildateputInfo();
        }
    }//输入了正确的验证码

    public void Btn_onclick_sure(View v) {
        String pass1 = newPassword1.getText().toString();
        String pass2 = newPassword2.getText().toString();
        if (pass1.length() < 6 || pass1.length() > 16 || pass2.length() < 6 || pass2.length() > 16 || !pass1.equals(pass2)) {
            Toast.makeText(getApplicationContext(), "两次输入的密码不相同或不满足长度要求(6-16位)", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(requestModifyPassword).start();
            showProcessDialog(ForgetPasswordActivity.this, "正在请求修改密码");
        }

    }//找回密码&&修改密码成功

    public void Btn_Back_For(View v) {
        finish();
    }//返回按钮监听器

    //ProgcessDialog
    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();//关闭短信时间回调监听
    }
}
