package com.sirius.madness.receiver.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;

/**
 * Object which represents a single Event from local data storage
 */
public class LocalEvent implements Parcelable {
    private static final String CLASS_NAME = "LocalEvent";

    //Class variables
    private Integer eventId;
    private String shortTitle;
    private String longTitle;
    private String shortDesc;
    private String longDesc;
    private String hall;
    private String image;
    private String map;
    private Date fromTime;
    private Date toTime;
    private Location location;
    private int[] partners;
    private int[] sessions;

    /**
     * Default constructor
     */
    public LocalEvent() {
        eventId = -1;
        shortTitle = null;
        longTitle = null;
        shortDesc = null;
        longDesc = null;
        hall = null;
        image = null;
        fromTime = null;
        toTime = null;
        location = null;
        partners = null;
        sessions = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalEvent(Parcel in) {
        eventId = in.readInt();
        shortTitle = in.readString();
        longTitle = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        hall = in.readString();
        image = in.readString();
        fromTime = new Date(in.readLong());
        toTime = new Date(in.readLong());
        location = (Location) in.readParcelable(Location.class.getClassLoader());
        partners = in.createIntArray();
        sessions = in.createIntArray();
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getImage() {
        return image;
    }
    public String getMap() {
        return map;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMap(String map) {
        this.map = map;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int[] getPartners() {
        return partners;
    }

    public void setPartners(int[] partners) {
        this.partners = partners;
    }

    public int[] getSessions() {
        return partners;
    }

    public void setSessions(int[] sessions) {
        this.sessions = sessions;
    }

    public int describeContents() {
        return 0;
    }

    /**
     * Implementation of method from "Parcelable" interface. Describes the data to be written
     * when this class is packaged into a Parcel, and in what order to package it
     * @param out
     * @param flags
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(eventId);
        out.writeString(shortTitle);
        out.writeString(longTitle);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeString(hall);
        out.writeString(image);
        out.writeString(map);
        out.writeLong(fromTime.getTime());
        out.writeLong(toTime.getTime());
        out.writeParcelable(location, 0);
        out.writeIntArray(partners);
        out.writeIntArray(sessions);
    }

    public static final Creator<LocalEvent> CREATOR
            = new Creator<LocalEvent>() {
        public LocalEvent createFromParcel(Parcel in) {
            return new LocalEvent(in);
        }

        public LocalEvent[] newArray(int size) {
            return new LocalEvent[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalEvent())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalEvent that = (LocalEvent) o;

        if (!eventId.equals(that.eventId)) return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (hall != null ? !hall.equals(that.hall) : that.hall != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (map != null ? !map.equals(that.map) : that.map != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (longDesc != null ? !longDesc.equals(that.longDesc) : that.longDesc != null)
            return false;
        if (longTitle != null ? !longTitle.equals(that.longTitle) : that.longTitle != null)
            return false;
        if (!Arrays.equals(partners, that.partners)) return false;
        if (!Arrays.equals(sessions, that.sessions)) return false;

        if (shortDesc != null ? !shortDesc.equals(that.shortDesc) : that.shortDesc != null)
            return false;
        if (shortTitle != null ? !shortTitle.equals(that.shortTitle) : that.shortTitle != null)
            return false;
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventId.hashCode();
        result = 31 * result + (shortTitle != null ? shortTitle.hashCode() : 0);
        result = 31 * result + (longTitle != null ? longTitle.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + (hall != null ? hall.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (map != null ? map.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (partners != null ? Arrays.hashCode(partners) : 0);
        result = 31 * result + (sessions != null ? Arrays.hashCode(sessions) : 0);
        return result;
    }

    @Override
    public String toString() {
        return eventId + " - " + longTitle;
    }
}
