package com.example.asus.ui_project.communication;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Webservice extends WebserviceHelp
{

    private static final String  Webservice_Tag="网络服务";

    /***
     * 用户登陆
     * @param phoneNumber 手机号
     * @param password 密码
     * @return
     */
    public static JSONObject login(String phoneNumber,String password) {
        JSONObject object;
        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("phone_number",phoneNumber));//phone_number:phoneNumber
            params.add(new BasicNameValuePair("password",password));//password:password

            System.out.println(URL + Login);

            object = HttpUtils.post(URL, Login, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            Log.e(Webservice_Tag+Login, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject logout() {
        JSONObject object;
        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            System.out.println(URL + Logout);

            object = HttpUtils.post(URL, Logout, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            Log.e(Webservice_Tag+Login, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject getWhiteboard(String meetingId) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("meeting_id", meetingId));//phone_number:phoneNumber

            System.out.println(URL + GetWhiteboards);

            object = HttpUtils.post(URL, GetWhiteboards, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
           // Log.e(Webservice_Tag + GetWhiteboards, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject validatePhone(String phoneNumber) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("phone_number",phoneNumber));//phone_number:phoneNumber

            System.out.println(URL + ValidPhone);

            System.out.println(phoneNumber);

            object = HttpUtils.post(URL, ValidPhone, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            Log.e(Webservice_Tag + ValidPhone, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject initMeeting(String meetingName, String maxPartnerNumber, String roomPassword) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("meeting_name", meetingName));
            params.add(new BasicNameValuePair("max_partner_number", maxPartnerNumber));
            params.add(new BasicNameValuePair("room_password", roomPassword));

            System.out.println(URL + InitMeeting);

            object = HttpUtils.post(URL, InitMeeting, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject joinMeeting(String roomId, String roomPassword) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("room_id", roomId));
            params.add(new BasicNameValuePair("room_password", roomPassword));

            System.out.println(URL + JoinMeeting);

            object = HttpUtils.post(URL, JoinMeeting, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject modifyPassword(String phoneNumber, String password) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("phone_number",phoneNumber));//phone_number:phoneNumber
            params.add(new BasicNameValuePair("password",password));//password:password

            System.out.println(URL + ModifyPassword);

            System.out.println(phoneNumber);

            object = HttpUtils.post(URL, ModifyPassword, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            // Log.e(Webservice_Tag + ModifyPassword, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject register(String username, String password, String phoneNumber) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("phone_number", phoneNumber));

            System.out.println(URL + Register);

            object = HttpUtils.post(URL, Register, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject updateWhiteboard(String roomId, String content) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("room_id", roomId));
            params.add(new BasicNameValuePair("content", content));
            System.out.println(URL + UpdateWhiteboard);

            object = HttpUtils.post(URL, UpdateWhiteboard, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject syncWhiteboard() {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            System.out.println(URL + SyncWhiteboard);

            object = HttpUtils.post(URL, SyncWhiteboard, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject quitMeeting() {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            System.out.println(URL + QuitMeeting);

            object = HttpUtils.post(URL, QuitMeeting, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject stopMeeting() {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            System.out.println(URL + StopMeeting);

            object = HttpUtils.post(URL, StopMeeting, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject getMeetings() {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            System.out.println(URL + GetMeetings);

            object = HttpUtils.post(URL, GetMeetings, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }

    public static JSONObject saveWhiteboard(String roomId) {
        JSONObject object;

        try
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();//参数HashMap--键值对

            params.add(new BasicNameValuePair("room_id", roomId));

            System.out.println(URL + SaveWhiteboard);

            object = HttpUtils.post(URL, SaveWhiteboard, params);//post

            return object;//返回json对象

        }
        catch(Exception e)
        {
            //Log.e(Webservice_Tag + InitMeeting, e.toString());
            return null;
        }//没有网络,没有得到object，返回null
    }
}
