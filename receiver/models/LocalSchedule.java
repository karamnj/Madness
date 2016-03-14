package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

/**
 * Created by 915649 on 22/01/15.
 */
public class LocalSchedule implements Parcelable {
    public static final String CLASS_NAME = "LocalSchedule";

    private Integer eventId;
    private String calendarEventId;
    private String eventTitle;
    private Date fromTime;
    private Date toTime;
    private Integer sessionId;
    private String mandatory;

    /**
     * Default constructor
     */
    public LocalSchedule() {
        eventId = -1;
        calendarEventId = null;
        eventTitle = null;
        fromTime = null;
        toTime = null;
        sessionId = -1;
        mandatory = "false";
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSchedule(Parcel in) {
        eventId = in.readInt();
        calendarEventId = in.readString();
        eventTitle = in.readString();
        fromTime = new Date(in.readLong());
        toTime = new Date(in.readLong());
        sessionId = in.readInt();
        mandatory = in.readString();
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public int describeContents() {
        return 0;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(eventId);
        out.writeString(calendarEventId);
        out.writeString(eventTitle);
        out.writeLong(fromTime.getTime());
        out.writeLong(toTime.getTime());
        out.writeInt(sessionId);
        out.writeString(mandatory);
    }

    public static final Creator<LocalSchedule> CREATOR
            = new Creator<LocalSchedule>() {
        public LocalSchedule createFromParcel(Parcel in) {
            return new LocalSchedule(in);
        }

        public LocalSchedule[] newArray(int size) {
            return new LocalSchedule[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalSchedule())) {
            result = false;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSchedule that = (LocalSchedule) o;

        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (eventTitle != null ? !eventTitle.equals(that.eventTitle) : that.eventTitle != null) return false;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        if (calendarEventId != null ? !calendarEventId.equals(that.calendarEventId) : that.calendarEventId != null)
            return false;
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        if (mandatory != null ? !mandatory.equals(that.mandatory) : that.mandatory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventId.hashCode();
        result = 31 * result + (calendarEventId != null ? calendarEventId.hashCode() : 0);
        result = 31 * result + (eventTitle != null ? eventTitle.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (sessionId.hashCode());
        result = 31 * result + (mandatory != null ? mandatory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return eventId + " - " + eventTitle;
    }

}
