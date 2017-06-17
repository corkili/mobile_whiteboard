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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.asus.ui_project.R;
import com.example.asus.ui_project.communication.Webservice;
import com.example.asus.ui_project.constant.MessageCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SimpleMeetingInfoActivity extends Activity {
    private List<String> ID = new ArrayList<>();//会议id
    private List<String> Name = new ArrayList<>();//会议名称
    private List<String> Leader = new ArrayList<>();//组织者
    private List<String> Num = new ArrayList<>();//参与人数
    private List<String> Actor = new ArrayList<>();//参与者
    private List<String> StartTime = new ArrayList<>();//开始时间
    private List<String> FinishTime = new ArrayList<>();//结束时间
    private JSONObject result;
    private ProgressDialog progressDialog;//进度框
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_info);
        sharedPreferences = getSharedPreferences("data",
                Activity.MODE_PRIVATE);//SP对象
        editor = sharedPreferences.edit();

        new Thread(requestMeetingInfo).start();
        showProcessDialog(this,"加载中...");//进度圈圈
        //开始回调线程，回调完成，创建List，关闭进度框
        //回调中，每一个键值对，将其对应的值add进上面的ArrayList中,后面传参数需要
    }

    private Runnable requestMeetingInfo= new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = MessageCode.MSG_GET_MEETINGS;
            result = Webservice.getMeetings();
            meetingInfoHandler.sendMessage(msg);
        }
    };

    private Handler meetingInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            try {
                if (result != null) {
                    int number = result.getInt("meeting_number");
                    JSONArray meetings = result.getJSONArray("meeting_list");
                    for (int i = 0; i < number; i++) {
                        JSONObject meeting = meetings.getJSONObject(i);
                        ID.add(meeting.getString("meeting_id"));
                        Name.add(meeting.getString("meeting_name"));
                        Num.add(meeting.getString("partner_number"));
                        Leader.add(meeting.getString("organizer"));
                        StartTime.add(meeting.getString("start_time"));
                        FinishTime.add(meeting.getString("end_time"));
                        JSONArray partners = meeting.getJSONArray("partners");
                        StringBuffer sb = new StringBuffer();
                        int count = meeting.getInt("partner_number");
                        for (int j = 0; j < count; j++){
                            String username = partners.getJSONObject(j).getString("username");
                            if (!username.equals(Leader.get(i))) {
                                sb.append(username).append("\n");
                            }
                        }
                        Actor.add(sb.toString());
                    }
                    List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();//键值对数组
                    //遍历ArrayList，创建Map对象添加进List里面,每一个对象是一个列表项
                    for(int i=0;i<Name.size();i++)
                    {
                        Map<String,Object> listItem=new HashMap<String, Object>();//键值对
                        listItem.put("name",Name.get(i));
                        listItem.put("start",StartTime.get(i));
                        listItem.put("finish",FinishTime.get(i));
                        listItems.add(listItem);//加入到键值对数组里
                    }//列表中需要的信息
                    SimpleAdapter simpleAdapter = new SimpleAdapter(SimpleMeetingInfoActivity.this, listItems, R.layout.info_item,
                            new String[]{"name","start","finish"}, new int[]{R.id.info_name,R.id.info_start,R.id.info_finish});
                    ListView list= (ListView) findViewById(R.id.list_simple);//布局中的ListView控件
                    list.setAdapter(simpleAdapter);//按照构造器来显示
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MeetingInfoActivity.ID = ID.get(position);
                            MeetingInfoActivity.Name = Name.get(position);
                            MeetingInfoActivity.Leader = Leader.get(position);
                            MeetingInfoActivity.Num = Num.get(position);
                            MeetingInfoActivity.Actor = Actor.get(position);
                            MeetingInfoActivity.StartTime = StartTime.get(position);
                            MeetingInfoActivity.FinishTime = FinishTime.get(position);
                            Intent intent = new Intent();
                            intent.setClass(SimpleMeetingInfoActivity.this,MeetingInfoActivity.class);
                            startActivity(intent);
                        }
                    });//Item监听器，改变静态变量ID的值，跳转到下一个Activity并跳转
                } else {
                    Toast.makeText(getApplicationContext(), "网络错误，请检查网络配置或重新登录", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void Btn_Back_Info(View v){
        finish();
    }

    //ProgcessDialog
    public void showProcessDialog(Context mContext, String text)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.show();//弹出
        progressDialog.setMessage(text);//信息
    }
}
