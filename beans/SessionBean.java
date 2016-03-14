package com.sirius.madness.beans;

public class SessionBean {
    private byte[] sessionBg;
    private int sessionId;
    private String sessionName;
    private String sessionDescription;
    private String sessionCategory;
    private String sessionDate;
    private String sessionHall;
    private long fromTime;
    private long toTime;
    private String calendarEventId;
    private String isHidden;
    private int headerIndex;


    public SessionBean(){

    }

    public int getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionDescription() {
        return sessionDescription;
    }

    public void setSessionDescription(String sessionDescription) {
        this.sessionDescription = sessionDescription;
    }

    public byte[] getSessionBg() {
        return sessionBg;
    }

    public void setSessionBg(byte[] sessionBg) {
        this.sessionBg = sessionBg;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionCategory() {
        return sessionCategory;
    }

    public void setSessionCategory(String sessionCategory) {
        this.sessionCategory = sessionCategory;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionHall() {
        return sessionHall;
    }

    public void setSessionHall(String sessionHall) {
        this.sessionHall = sessionHall;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }

    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }
}
