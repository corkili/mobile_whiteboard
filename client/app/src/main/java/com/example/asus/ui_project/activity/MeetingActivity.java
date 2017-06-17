package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;
import com.example.asus.ui_project.core.MeetingManager;
import com.example.asus.ui_project.core.WhiteboardCore;
import com.example.asus.ui_project.view.LinePathView;

import org.json.JSONException;
import org.json.JSONObject;

public class MeetingActivity extends Activity{
    private static final int DELAY_TIME = 3000; // 3s
    private LinePathView board;//白板对象
    private ImageView imageView; // 观察者白板对象
    private boolean click_width = false;//是否点击粗细
    private boolean click_color = false;//是否点击颜色
    private boolean click_eraser = false;//是否点击橡皮擦
    private int paintColor;//画笔颜色
    private int paintWidth;//画笔宽度
    private LinearLayout width;//width菜单
    private LinearLayout color;//color菜单
    private Button btn_color;//颜色按钮
    private TextView tvTitle;// 房间号
    private MeetingManager meetingManager;
    private boolean isOrganizer;
    private JSONObject result;
    private Bitmap content;
    private long exitTime = 0;
    private boolean running = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOrganizer = getIntent().getBooleanExtra("isOrganizer", true);
        if (isOrganizer) {
            setContentView(R.layout.whiteboard);
            board = (LinePathView) findViewById(R.id.whiteBoard);
        } else {
            setContentView(R.layout.whiteboard_ob);
            imageView = (ImageView) findViewById(R.id.whiteBoard);
        }
        meetingManager = MeetingManager.getMeetingManager();
        width= (LinearLayout) findViewById(R.id.width_lin);
        color= (LinearLayout) findViewById(R.id.color_lin);
        btn_color= (Button) findViewById(R.id.btn_color);
        tvTitle = (TextView) findViewById(R.id.tv_room_id);
        String title = generateTitle();
        tvTitle.setText(title);
        if(isOrganizer) {
            board.setEnabled(false);
        }
        new Thread(requestWhiteboard).start();
    }

    public void setPaintWidth(View v) {
        if (!isOrganizer) {
            return;
        }
        if(click_eraser)
        {
            Toast.makeText(getApplicationContext(),"现在处于橡皮擦模式，不能更换笔的粗细哟~n(*≧▽≦*)n",Toast.LENGTH_SHORT).show();
            return;
        }
        click_color=false;
        color.setVisibility(View.INVISIBLE);//不可见
        if(!click_width){
            width.setVisibility(View.VISIBLE);//可见
            click_width=true;
        }
        else
        {
            width.setVisibility(View.INVISIBLE);//不可见
            click_width=false;
        }
    }//粗细

    public void setPaintColor(View v) {
        if (!isOrganizer) {
            return;
        }
        if(click_eraser)
        {
            Toast.makeText(getApplicationContext(),"现在处于橡皮擦模式，不能更换笔的颜色哟~n(*≧▽≦*)n",Toast.LENGTH_SHORT).show();
            return;
        }
        click_width=false;
        width.setVisibility(View.INVISIBLE);//不可见
        if(!click_color){
            color.setVisibility(View.VISIBLE);//可见
            click_color=true;
        }
        else
        {
            color.setVisibility(View.INVISIBLE);//不可见
            click_color=false;
        }
    }//颜色

    public void makeEraser(View v) {
        if (!isOrganizer) {
            return;
        }
        click_color=false;
        color.setVisibility(View.INVISIBLE);//不可见
        click_width=false;
        width.setVisibility(View.INVISIBLE);//不可见
        if (!click_eraser) {
            paintColor=board.getPaintColor();//get画笔颜色
            paintWidth=board.getPaintWidth();//get画笔宽度
            board.setPaintColor(Color.parseColor("#ffffff"));
            board.setPaintWidth(40);
            v.setBackgroundResource(R.drawable.eraser_click);
            click_eraser=true;
        }
        else{
            board.setPaintColor(paintColor);//设置回原来的画笔颜色
            board.setPaintWidth(paintWidth);//宽度
            v.setBackgroundResource(R.drawable.eraser_normal);
            click_eraser=false;
        }
    }//橡皮擦

    public void Clear(View v) {
        if (!isOrganizer) {
            return;
        }
        click_color = false;
        color.setVisibility(View.INVISIBLE);//不可见
        click_width = false;
        width.setVisibility(View.INVISIBLE);//不可见
        //board.clear();
        new Thread(requestSave).start();
    }//清除

    public void setPaintWidth_1(View v) {
        board.setPaintWidth(5);
        width.setVisibility(View.INVISIBLE);//不可见
        click_width=false;
    }//细
    public void setPaintWidth_2(View v) {
        board.setPaintWidth(10);
        width.setVisibility(View.INVISIBLE);//不可见
        click_width=false;
    }//中
    public void setPaintWidth_3(View v) {
        board.setPaintWidth(20);
        width.setVisibility(View.INVISIBLE);//不可见
        click_width=false;
    }//粗

    public void setPaintColor_1(View v) {
        board.setPaintColor(Color.parseColor("#000000"));
        btn_color.setBackgroundResource(R.drawable.color_2c2c2c);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//黑
    public void setPaintColor_2(View v) {
        board.setPaintColor(Color.parseColor("#d81e06"));
        btn_color.setBackgroundResource(R.drawable.color_d81e06);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//红
    public void setPaintColor_3(View v) {
        board.setPaintColor(Color.parseColor("#1afa29"));
        btn_color.setBackgroundResource(R.drawable.color_1afa29);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//绿
    public void setPaintColor_4(View v) {
        board.setPaintColor(Color.parseColor("#1296db"));
        btn_color.setBackgroundResource(R.drawable.color_1296db);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//蓝
    public void setPaintColor_5(View v) {
        board.setPaintColor(Color.parseColor("#f4ea2a"));
        btn_color.setBackgroundResource(R.drawable.color_f4ea2a);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//黄
    public void setPaintColor_6(View v) {
        board.setPaintColor(Color.parseColor("#d4237a"));
        btn_color.setBackgroundResource(R.drawable.color_d4237a);
        color.setVisibility(View.INVISIBLE);//不可见
        click_color=false;
    }//粉

    private String generateTitle() {
        return String.valueOf(meetingManager.getMeeting().getMeetingRoomId())
                + "(在线人数:" + String.valueOf(meetingManager.getMeeting().getPartnerNumber()) + ")";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.err.println("exit...");
            exit();//连按两次退出
        }
        return false;
    }//返回按键监听器

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出会议", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            // 发送退出会议数据
            new Thread(requestExitMeeting).start();
            //finish();
        }
    }

    private final Runnable requestExitMeeting = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = MessageCode.MSG_EXIT_MEETING;
            if (isOrganizer) {
                Webservice.stopMeeting();
            } else {
                Webservice.quitMeeting();
            }
            exitMeetingHandler.sendMessage(msg);
        }
    };

    private Handler exitMeetingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MessageCode.MSG_EXIT_MEETING) {
                running = false;
                MeetingActivity.this.finish();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            content = board.getBitMap();

            if (msg.what == MessageCode.MSG_UPDATE) {
                new Thread(requestUpdate).start();
            }
        }
    };



    private final Runnable requestWhiteboard = new Runnable() {
        @Override
        public void run() {
            while (running) {
                Message msg = new Message();
                msg.what = isOrganizer ? MessageCode.MSG_UPDATE : MessageCode.MSG_SYNC;
                if(isOrganizer) {
                    //Bitmap content = board.getBitMap();
                    handler.sendMessage(msg);
                } else {
                    result = Webservice.syncWhiteboard();
                    whiteboardHandler.sendMessage(msg);
                }
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private Runnable requestUpdate = new Runnable() {
        @Override
        public void run() {
            if (content != null) {
                String content64 = WhiteboardCore.bitmapToBase64(content);
                System.out.println("update: " + content64.substring(0, 5) + " - " + content64.length());
                result = Webservice.updateWhiteboard(
                        String.valueOf(meetingManager.getMeeting().getMeetingRoomId()),
                        content64);
            } else {
                result = Webservice.updateWhiteboard(
                        String.valueOf(meetingManager.getMeeting().getMeetingRoomId()), "error");
            }
            Message msg = new Message();
            msg.what = MessageCode.MSG_UPDATE;
            whiteboardHandler.sendMessage(msg);
        }
    };

    private Handler whiteboardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MessageCode.MSG_UPDATE) {
                if (result == null) {
//                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络或重新登录", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        meetingManager.getMeeting().setPartnerNumber(result.getInt("online_partners"));
                        tvTitle.setText(generateTitle());
//                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == MessageCode.MSG_SYNC) {
                try {
                    if (result == null) {
//                        Toast.makeText(getApplicationContext(), "网络异常，请检查网络或重新登录", Toast.LENGTH_SHORT).show();
                    } else if(result.getInt("code") == MessageCode.SUCCESSFUL) {
                        meetingManager.getMeeting().setPartnerNumber(result.getInt("online_partners"));
                        tvTitle.setText(generateTitle());
                        String content64 = result.getString("content");
                        System.out.println("sync: " + content64.substring(0, 5) + " - " + content64.length());
                        Bitmap bitmap = WhiteboardCore.base64ToBitmap(result.getString("content"))
                                .copy(Bitmap.Config.ARGB_8888, true);
                        BitmapDrawable drawable = new BitmapDrawable(bitmap);
                        imageView.setBackgroundDrawable(drawable);
//                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
            synchronized (requestWhiteboard) {
                System.err.println(isOrganizer + ": " + "notify");
                requestWhiteboard.notify();
            }
        }
    };

    private final Runnable requestSave = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = MessageCode.MSG_SAVE_WHITEBOARD;
            if(isOrganizer) {
                result = Webservice.saveWhiteboard(
                            String.valueOf(meetingManager.getMeeting().getMeetingRoomId()));
            }
            saveHandler.sendMessage(msg);
        }
    };

    private Handler saveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MessageCode.MSG_SAVE_WHITEBOARD) {
                board.clear();
            }
        }
    };
}
