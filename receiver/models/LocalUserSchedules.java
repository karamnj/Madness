package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Object which represents a single Speaker from local data storage
 */
public class LocalUserSchedules implements Parcelable {
    public static final String CLASS_NAME = "LocalUserSchedules";

    //Class variables
    private int[] sessions;
    private String userName;


    /**
     * Default constructor
     */
    public LocalUserSchedules() {
        sessions = null;
        userName = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalUserSchedules(Parcel in) {
        sessions = in.createIntArray();
        userName = in.readString();
    }

    /**
     * Gets first name of the Speaker
     * @return sessions list if successful, null if unsuccessful
     */
    public int[] getSessions() {
        return sessions;
    }

    /**
     * Gets last name of the Speaker
     * @return user name if successful, null if unsuccessful
     */
    public String getUserName() {
        return userName;
    }


    public void setSessions(int[] sessions) {
        this.sessions = sessions;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
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
        out.writeIntArray(sessions);
        out.writeString(userName);
    }

    public static final Creator<LocalUserSchedules> CREATOR
            = new Creator<LocalUserSchedules>() {
        public LocalUserSchedules createFromParcel(Parcel in) {
            return new LocalUserSchedules(in);
        }

        public LocalUserSchedules[] newArray(int size) {
            return new LocalUserSchedules[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalUserSchedules())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalUserSchedules presenter = (LocalUserSchedules) o;

        if (!Arrays.equals(sessions, presenter.sessions)) return false;
        if (userName != null ? !userName.equals(presenter.userName) : presenter.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (sessions != null ? Arrays.hashCode(sessions) : 0);
        return result;
    }

    @Override
    public String toString() {
        return userName + " " + sessions;
    }
}
