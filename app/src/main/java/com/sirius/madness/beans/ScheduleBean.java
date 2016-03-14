package com.sirius.madness.beans;

import java.util.Date;

/**
 * Created by 915649 on 23/01/15.
 */
public class ScheduleBean {
    String eventId;
    String calendarEventId;
    int sessionId;
    Date fromTime;
    Date toTime;
    boolean isHeader;
    String headerTitle;
    int headerIndex;
    String mandatory;

    public int getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }
}
