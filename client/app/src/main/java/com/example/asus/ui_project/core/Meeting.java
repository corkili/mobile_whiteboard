package com.example.asus.ui_project.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by 李浩然 on 2017/5/30.
 */

public class Meeting {
    private long meetingId;
    private String meetingName;
    private int partnerNumber;
    private String organizer;
    private Date startTime;
    private Date endTime;
    private int meetingRoomId;

    private List<String> partners;
    private boolean isStarted;

    public Meeting() {
        this.partners = new ArrayList<>();
    }

    public Meeting(long meetingId, String meetingName, int partnerNumber, String organizer,
                   Date startTime, Date endTime, int meetingRoomId, boolean isStarted) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.partnerNumber = partnerNumber;
        this.organizer = organizer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.meetingRoomId = meetingRoomId;
        this.isStarted = isStarted;
        if(partners == null) {
            partners = new ArrayList<>();
        }
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public int getPartnerNumber() {
        return partnerNumber;
    }

    public void setPartnerNumber(int partnerNumber) {
        this.partnerNumber = partnerNumber;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(int meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public List<String> getPartners() {
        return partners;
    }

    public void setPartners(List<String> partners) {
        this.partners = partners;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void addPartner(String username) {
        partners.add(username);
    }

    public void removePartner(String username) {
        partners.remove(username);
    }

    public void addPartner(Collection<String> usernames) {
        partners.addAll(usernames);
    }

    public void removePartner(Collection<String> usernames) {
        partners.removeAll(usernames);
    }
}
