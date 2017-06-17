package com.example.asus.ui_project.communication;


public class WebserviceHelp {
    /**
     * IP地址或域名
     */
    protected static String IP = "http://118.114.44.50";
    /**
     * Webservice提供地址
     */
    protected static String URL = IP + ":8080";


    /*************下面是方法名*************/

    /**
     * 用户登录
     **/
    protected static final String Login = "/user/login";

    protected static final String ValidPhone = "/user/valid_phone";

    protected static final String ModifyPassword = "/user/modify_password";

    protected static final String Logout = "/user/logout";

    protected static final String InitMeeting = "/meeting/init_meeting";

    protected static final String Register = "/user/register";

    protected static final String JoinMeeting = "/meeting/join_meeting";

    protected static final String UpdateWhiteboard = "/whiteboard/update";

    protected static final String SyncWhiteboard = "/whiteboard/sync";

    protected static final String SaveWhiteboard = "/whiteboard/save";

    protected static final String QuitMeeting = "/meeting/quit_meeting";

    protected static final String StopMeeting = "/meeting/stop_meeting";

    protected static final String GetMeetings = "/meeting/get_my_meetings";

    protected static final String GetWhiteboards = "/whiteboard/get_whiteboards";

}