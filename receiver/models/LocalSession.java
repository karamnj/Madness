package com.sirius.madness.receiver.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;

/**
 * Object which represents a single Session from local data storage
 */
public class LocalSession implements Parcelable {
    public static final String CLASS_NAME = "LocalSession";

    //Class variables
    private Integer sessionId;
    private String shortTitle;
    private String longTitle;
    private String shortDesc;
    private String longDesc;
    private String hall;
    private Long image;
    private Date fromTime;
    private Date toTime;
    private Location location;
    private int[] presenters;
    private String isHidden;

    /**
     * Default constructor
     */
    public LocalSession() {
        sessionId = -1;
        shortTitle = null;
        longTitle = null;
        shortDesc = null;
        longDesc = null;
        hall = null;
        image = null;
        fromTime = null;
        toTime = null;
        location = null;
        presenters = null;
        isHidden = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSession(Parcel in) {
        sessionId = in.readInt();
        shortTitle = in.readString();
        longTitle = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        hall = in.readString();
        image = in.readLong();
        fromTime = new Date(in.readLong());
        toTime = new Date(in.readLong());
        location = (Location) in.readParcelable(Location.class.getClassLoader());
        presenters = in.createIntArray();
        isHidden = in.readString();
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getHall() {
        return hall;
    }

    public Long getImage() {
        return image;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public Location getLocation() {
        return location;
    }

    public int[] getPresenters() {
        return presenters;
    }
    
    public String getIsHidden() { return isHidden; }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void setImage(Long image) {
        this.image = image;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPresenters(int[] presenters) {
        this.presenters = presenters;
    }
    
    public void setIsHidden(String isHidden) { this.isHidden = isHidden; }

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
        out.writeInt(sessionId);
        out.writeString(shortTitle);
        out.writeString(longTitle);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeString(hall);
        out.writeLong(image);
        out.writeLong(fromTime.getTime());
        out.writeLong(toTime.getTime());
        out.writeParcelable(location, 0);
        out.writeIntArray(presenters);
        out.writeString(isHidden);
    }

    public static final Creator<LocalSession> CREATOR
            = new Creator<LocalSession>() {
        public LocalSession createFromParcel(Parcel in) {
            return new LocalSession(in);
        }

        public LocalSession[] newArray(int size) {
            return new LocalSession[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalSession())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSession that = (LocalSession) o;

        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (hall != null ? !hall.equals(that.hall) : that.hall != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (longDesc != null ? !longDesc.equals(that.longDesc) : that.longDesc != null)
            return false;
        if (longTitle != null ? !longTitle.equals(that.longTitle) : that.longTitle != null)
            return false;
        if (!Arrays.equals(presenters, that.presenters)) return false;
        if (!sessionId.equals(that.sessionId)) return false;
        if (shortDesc != null ? !shortDesc.equals(that.shortDesc) : that.shortDesc != null)
            return false;
        if (shortTitle != null ? !shortTitle.equals(that.shortTitle) : that.shortTitle != null)
            return false;
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) return false;
        if (isHidden != null ? !isHidden.equals(that.isHidden) : that.isHidden != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionId.hashCode();
        result = 31 * result + (shortTitle != null ? shortTitle.hashCode() : 0);
        result = 31 * result + (longTitle != null ? longTitle.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + (hall != null ? hall.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (presenters != null ? Arrays.hashCode(presenters) : 0);
        result = 31 * result + (isHidden != null ? isHidden.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return sessionId + " - " + longTitle;
    }
}
