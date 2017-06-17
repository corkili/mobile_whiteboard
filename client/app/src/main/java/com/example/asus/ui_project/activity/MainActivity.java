package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;
import com.example.asus.ui_project.core.Meeting;
import com.example.asus.ui_project.core.MeetingManager;
import com.example.asus.ui_project.dialog.JoinMeetingDialog;
import com.example.asus.ui_project.dialog.OrganizeMeetingDialog;
import com.example.asus.ui_project.dialog.TipDialog;
import com.example.asus.ui_project.view.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends Activity {

    private SlidingMenu mLeftMenu;
    private long exitTime = 0;//时间
    private View head;//头像
    private RelativeLayout friends;//侧滑栏我的好友按钮
    private RelativeLayout modify;//侧滑栏修改密码按钮
    private RelativeLayout logout;//侧滑栏注销按钮
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView startMeeting;//召集会议
    private TextView joinMeeting;//参与会议
    private TextView meetingNote;//会议笔记
    private TextView myInformation;//个人信息
    private ProgressDialog progressDialog;
    private JSONObject result;
    private String meetingName;
    private String maxPartnerNumber;
    private String roomPassword;
    private String roomId;
    private boolean isExit;
    private boolean isInitMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initData();//初始化数据、控件、监听器
        ((TextView)findViewById(R.id.user_name_text)).setText(sharedPreferences.getString("username", "Whiteboard"));
    }

    //初始化数据
    private void initData() {
        sharedPreferences = getSharedPreferences("data",
                Activity.MODE_PRIVATE);//SP对象
        editor = sharedPreferences.edit();
        mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);//侧滑栏
        //头像及头像点击事件
        head = findViewById(R.id.header_icon);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftMenu.ON) {//打开的，点击关闭
                    mLeftMenu.smoothScrollTo(mLeftMenu.mMenuWidth, 0);
                    mLeftMenu.ON = false;
                } else//关闭的，点击打开
                {
                    mLeftMenu.smoothScrollTo(0, 0);
                    mLeftMenu.ON = true;
                }
            }
        });
        //侧滑栏好友及其点击事件
        friends= (RelativeLayout) findViewById(R.id.Rela_friends);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"敬请期待~",Toast.LENGTH_SHORT).show();
            }
        });
        //侧滑栏修改密码及其点击事件
        modify= (RelativeLayout) findViewById(R.id.Rela_modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
        //侧滑栏注销及其点击事件
        logout = (RelativeLayout) findViewById(R.id.Rela_lagout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();//弹出是否退出登录对话框
            }
        });
        startMeeting = (TextView) findViewById(R.id.start_meeting);
        startMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showOrganizeDialog();
            }
        });
        joinMeeting = (TextView) findViewById(R.id.join_meeting);//参与会议
        joinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinDialog();
            }
        });
        meetingNote = (TextView) findViewById(R.id.meeting_note);//会议笔记
        meetingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "还未开发~", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SimpleMeetingInfoActivity.class);
                startActivity(intent);
            }
        });
        myInformation = (TextView) findViewById(R.id.my_information);//个人信息
        myInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "还未开发~", Toast.LENGTH_SHORT).show();
                if (mLeftMenu.ON) {//打开的，点击关闭
                    mLeftMenu.smoothScrollTo(mLeftMenu.mMenuWidth, 0);
                    mLeftMenu.ON = false;
                } else//关闭的，点击打开
                {
                    mLeftMenu.smoothScrollTo(0, 0);
                    mLeftMenu.ON = true;
                }
            }
        });
    }

    //按下返回键监听器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLeftMenu.ON) {
                mLeftMenu.smoothScrollTo(mLeftMenu.mMenuWidth, 0);
                mLeftMenu.ON = false;
            } else {
                exit();//连按两次退出
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }//返回按键监听器

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            editor.putString("password", "");
            editor.putBoolean("login", false);
            editor.commit();//更新数据，回到登录界面
            //发送注销数据
            isExit = true;
            new Thread(requestLogout).start();
        }
    }//退出时间间隔判断


    //退出登录的对话框
    public void showLogoutDialog() {

        TipDialog.Builder builder = new TipDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭Dialog
                editor.putString("password", "");
                editor.putBoolean("login", false);
                editor.commit();//更新数据，回到登录界面
                //发送注销数据
                isExit = false;
                new Thread(requestLogout).start();
                //设置你的操作事项
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//关闭Dialog
                        ;//不做
                    }
                });
        builder.create().show();
    }

    //召集会议的对话框
    public void showOrganizeDialog() {

        final OrganizeMeetingDialog.Builder builder = new OrganizeMeetingDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //获取输入记录，向服务器传送数据并请求
                meetingName = builder.getName();
                if(meetingName == null || meetingName.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "会议室名称不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                roomPassword = builder.getPasswrod();
                maxPartnerNumber = "10";
                isInitMeeting = true;
                new Thread(requestInitMeeting).start();
                dialog.dismiss();//关闭Dialog
                showProcessDialog(MainActivity.this, "正在初始化会议");
                //Toast.makeText(getApplicationContext(),"您输入的会议名称为:"+builder.getName()+"\n您输入的会议密码为:"+builder.getPasswrod(),Toast.LENGTH_SHORT).show();
                //设置你的操作事项
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//关闭Dialog
                        ;//不做
                    }
                });
        builder.create().show();
    }

    //参与会议的对话框
    public void showJoinDialog() {

        final JoinMeetingDialog.Builder builder = new JoinMeetingDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //获取输入记录，向服务器传送数据并请求
                roomId = builder.getId();
                roomPassword = builder.getPasswrod();
                if (roomId == null || "".equals(roomId.trim())) {
                    Toast.makeText(getApplicationContext(), "会议室ID不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                isInitMeeting = false;
                //Toast.makeText(getApplicationContext(),"您输入的会议号为:"+builder.getId()+"\n您输入的会议密码为:"+builder.getPasswrod(),Toast.LENGTH_SHORT).show();
                new Thread(requestJoinMeeting).start();
                dialog.dismiss();//关闭Dialog
                showProcessDialog(MainActivity.this, "正在请求加入会议");
                //设置你的操作事项
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//关闭Dialog
                        ;//不做
                    }
                });
        builder.create().show();
    }

    private Runnable requestInitMeeting = new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what= MessageCode.MSG_INIT_MEETING;
            // 发送网络请求
            result = Webservice.initMeeting(meetingName, maxPartnerNumber, roomPassword);
            meetingHandler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Runnable requestJoinMeeting = new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what= MessageCode.MSG_JOIN_MEETING;
            // 发送网络请求
            result = Webservice.joinMeeting(roomId, roomPassword);
            meetingHandler.sendMessage(msg);//发送异步处理请求
        }
    };


    private Handler meetingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try{
                if(msg.what== MessageCode.MSG_INIT_MEETING || msg.what == MessageCode.MSG_JOIN_MEETING) {
                    if(result == null) {
                        Toast.makeText(getApplicationContext(),"初始化会议失败，请检查网络",Toast.LENGTH_SHORT).show();//请求失败
                        //dismiss
                    }
                    else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        MeetingManager meetingManager = MeetingManager.getMeetingManager();
                        JSONObject meetingInfo = result.getJSONObject("meeting_info");
                        long meetingId = meetingInfo.getLong("meeting_id");
                        String meetingName = meetingInfo.getString("meeting_name");
                        int partnerNumber = meetingInfo.getInt("partner_number");
                        String organizer = meetingInfo.getString("organizer");
                        int roomId = meetingInfo.getInt("room_id");
                        boolean status = meetingInfo.getBoolean("status");
                        Meeting meeting = new Meeting(meetingId, meetingName, partnerNumber,
                                organizer, null, null, roomId, status);
                        JSONArray partners = meetingInfo.getJSONArray("partners");
                        int i = 0;
                        while (!partners.isNull(i)) {
                            meeting.addPartner(((JSONObject)partners.get(i)).getString("username"));
                            i++;
                        }
                        meetingManager.setMeeting(meeting);
                        // 成功初始化会议，跳转会议
                        Intent intent = new Intent();
                        intent.putExtra("isOrganizer", isInitMeeting);
                        intent.setClass(MainActivity.this, MeetingActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_SHORT).show(); // login failed
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

    private Runnable requestLogout=new Runnable() {
        @Override
        public void run() {
            Message msg=new Message();
            msg.what=MessageCode.MSG_LOGOUT;
            // 发送网络请求
            result = Webservice.logout();
            logoutHandler.sendMessage(msg);//发送异步处理请求
        }
    };

    private Handler logoutHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== MessageCode.MSG_LOGOUT) {
                if(isExit) {
                    System.exit(0);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                ;//dismiss,可以不用，假装
            }
        }
    };//响应回调

    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }
}
