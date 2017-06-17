package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class RegisterActivity extends Activity {
    private static final String[] AVATARS = new String[400];
    private EditText userName;//用户名
    private EditText userPass1;//密码1
    private EditText userPass2;//密码2
    private EditText phoneNumber;//手机号码
    private EditText validCode;//验证码
    private String UN = "";//用户名
    private String UP1 = "";//密码1
    private String UP2 = "";//密码2
    private String PN = "";//手机号码
    private String PN_Sure = "";//验证成功后的手机号码，最后写进数据库的手机号码
    private String VC = "";//验证码
    private Button btn;//发送验证码
    private Boolean Click = false;//是否发送验证码了
    private int tosatNum = 0;//提示次数
    private int clickNum = 0;//点击次数
    String APPKEY = "1db15e4285f80";//KEY
    String APPSECRET = "a520e87757554fc9a93c49b4c1c62f79";//SECRET
    private ProgressDialog progressDialog;
    private JSONObject result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        userName = (EditText) findViewById(R.id.userName_Register_ET);
        userPass1 = (EditText) findViewById(R.id.userPass_Register_ET1);
        userPass2 = (EditText) findViewById(R.id.userPass_Register_ET2);
        phoneNumber = (EditText) findViewById(R.id.userPhoneNum);
        validCode = (EditText) findViewById(R.id.userMessNum);
        btn = (Button) findViewById(R.id.send_mess);
        //初始化短信SDK
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            progressDialog.dismiss();
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Toast.makeText(getApplicationContext(), "获取验证码成功", Toast.LENGTH_SHORT).show();
                    Click = true;
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    new Thread(requestRegister).start();
                    showProcessDialog(RegisterActivity.this, "注册中...");
//                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();//弹出对话框，是否登录，是的话直接登录，否的话返回登录界面
//                    PN_Sure = PN;
//                    finish();
                } else if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
                    //提交验证用户信息成功，不做任何事情
                } else {
                    //不做任何事情
                }
            } else {
                //回调失败
                if (clickNum > tosatNum) {
                    Toast.makeText(getApplicationContext(), "验证码错误,请重试",
                            Toast.LENGTH_SHORT).show();
                    tosatNum++;
                }
                ((Throwable) data).printStackTrace();
            }
        }
    };

    public void Sure_Register(View v) {
        UN = userName.getText().toString();
        UP1 = userPass1.getText().toString();
        UP2 = userPass2.getText().toString();
        VC = validCode.getText().toString();
        if (Valid()) {
            if (!Click) {
                Toast.makeText(getApplicationContext(), "请先获取验证码!",
                        Toast.LENGTH_SHORT).show();
            } else {
                //发送注册信息
                VaildateputInfo();
                clickNum++;
                //解析返回信息，得到结果，成功或失败
            }
        }//注册按钮监听器
    }

    /**
     * 1.验证验证码
     * 2.提交用户信息
     */

    private void VaildateputInfo() {
        vaildatePassword();
    }

    //验证 验证码
    private void vaildatePassword() {
        SMSSDK.submitVerificationCode("86", PN, VC);
        //putUserInfo("86", PN);
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

    //判断所有字符串是否符合规则
    private boolean Valid() {
        if (UN.equals("")) {
            Toast.makeText(getApplicationContext(), "用户名不能为空!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (UN.contains(" ")) {
            Toast.makeText(getApplicationContext(), "用户名不能含有空格!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (UP1.equals("") || UP2.equals("")) {
            Toast.makeText(getApplicationContext(), "密码不能为空!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (UP1.contains(" ") || UP2.contains(" ")) {
            Toast.makeText(getApplicationContext(), "密码不能含有空格!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (UP1.length() < 6 || UP1.length() > 16 || UP2.length() < 6 || UP2.length() > 16) {
            Toast.makeText(getApplicationContext(), "密码长度为6-16位!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!UP1.equals(UP2)) {
            Toast.makeText(getApplicationContext(), "两次密码不相同，请确认!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //发送验证码，得到验证码
        if (VC.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入验证码!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断验证码是否正确,不正确返回false
        return true;
    }

    //计时器
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btn.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            btn.setEnabled(true);
            btn.setText("获取验证码");
        }
    };

    public void Send_Mess(View v) {
        PN = phoneNumber.getText().toString();
        if (!(PN.length() == 11)) {
            Toast.makeText(getApplicationContext(), "请输入正确的手机号码!",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            new Thread(requestValidPhone).start();
            showProcessDialog(RegisterActivity.this, "正在验证手机号...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();//关闭短信时间回调监听
    }

    public void Btn_Back_Res(View v) {
        finish();
    }//返回按钮监听器

    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }

    private Runnable requestRegister = new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what=MessageCode.MSG_REGISTER;
            // 发送网络请求
            result = Webservice.register(userName.getText().toString(),
                    userPass1.getText().toString(), phoneNumber.getText().toString());
            registerHandler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Handler registerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try{
                if(msg.what== MessageCode.MSG_REGISTER) {
                    if(result == null) {
                        Toast.makeText(getApplicationContext(),"注册失败，请检查网络",Toast.LENGTH_SHORT).show();//请求失败
                        //dismiss
                    }
                    else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        Toast.makeText(getApplicationContext(), result.getString("message"),Toast.LENGTH_SHORT).show();
                        PN_Sure = PN;
                        finish();
                        //dismiss
                    } else {
                        Toast.makeText(getApplicationContext(), result.getString("message"),Toast.LENGTH_SHORT).show(); // login failed
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
                    else if(result.getInt("code") != MessageCode.SUCCESSFUL) {
                        showProcessDialog(RegisterActivity.this,"获取验证码...");
                        btn.requestFocus();
                        btn.setEnabled(false);
                        SMSSDK.getVerificationCode("86", PN);//请求获取验证码
                        timer.start();
                        //发送验证码，发送成功就跳转
                    } else {
                        Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();;// login failed
                        //dismiss
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
