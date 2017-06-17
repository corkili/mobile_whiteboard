package com.example.asus.ui_project.core;

/**
 * Created by 李浩然 on 2017/5/30.
 */

public class MeetingManager {
    private static MeetingManager meetingManager;
    private Meeting meeting;

    private MeetingManager() {

    }

    public static MeetingManager getMeetingManager() {
        if(meetingManager == null) {
            meetingManager = new MeetingManager();
        }
        return meetingManager;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }
}
