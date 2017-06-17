package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;
import com.example.asus.ui_project.core.WhiteboardCore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeetingInfoActivity extends Activity {
    public static String ID="";//id
    public static String Name="";//名称
    public static String Leader="";//组织者
    public static String Num="";//参与人数
    public static String Actor="";//参与者
    public static String StartTime="";//开始时间
    public static String FinishTime="";//结束时间
    private LinearLayout linearLayout;
    private JSONObject result;
    private ProgressDialog progressDialog;//进度框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_info);
        initData();
        new Thread(requestWhiteboard).start();
        showProcessDialog(this, "正在加载笔记...");
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.welcome);
//        add_image(bitmap);//把Bitmap展示在界面上
//        add_image(bitmap);
    }

    private void initData(){
        TextView name= (TextView) findViewById(R.id.INFO_NAME);
        name.setText(Name);
        TextView id= (TextView) findViewById(R.id.INFO_ID);
        id.setText(ID);
        TextView leader= (TextView) findViewById(R.id.INFO_LEADER);
        leader.setText(Leader);
        TextView num= (TextView) findViewById(R.id.INFO_NUM);
        num.setText(Num);
        TextView actor= (TextView) findViewById(R.id.INFO_ACTOR);
        actor.setText(Actor);
        TextView start= (TextView) findViewById(R.id.INFO_START);
        start.setText(StartTime);
        TextView finish= (TextView) findViewById(R.id.INFO_FINISH);
        finish.setText(FinishTime);
        linearLayout= (LinearLayout) findViewById(R.id.info_linear);
    }

    private void add_image(Bitmap bitmap){
        ImageView imageView=new ImageView(this);//新建一个ImageView对象
        imageView.setImageBitmap(bitmap);//imageView引入bitmap
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//scaleType
        linearLayout.addView(imageView);//imageView添加至界面
    }//添加图片，一次一张，传入一个bitmap对象

    public void Btn_Back_Info2(View v){
        finish();
    }

    private Runnable requestWhiteboard= new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = MessageCode.MSG_GET_WHITEBOARDS;
            result = Webservice.getWhiteboard(ID);
            whiteboardsHandler.sendMessage(msg);
        }
    };

    private Handler whiteboardsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (result != null) {
                    int count = result.getInt("whiteboard_number");
                    JSONArray whiteboards = result.getJSONArray("whiteboards");
                    for (int i = 0; i < count; i++) {
                        String content =  whiteboards.getString(i);
                        Bitmap bitmap = WhiteboardCore.base64ToBitmap(content);
                        add_image(bitmap);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络错误，请检查网络配置或重新登录", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    };

    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }
}
