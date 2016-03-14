package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Object which represents a single Session Category from local data storage
 */
public class LocalSessionCategory {
    private static final String CLASS_NAME = "LocalSessionCategory";

    //Class variables
    private Integer categoryId;
    private String categoryName;
    private int[] sessions;

    /**
     * Default constructor
     */
    public LocalSessionCategory() {
        categoryId = -1;
        categoryName = null;
        sessions = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSessionCategory(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
        sessions = in.createIntArray();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int[] getSessions() {
        return sessions;
    }

    public void setSessions(int[] sessions) {
        this.sessions = sessions;
    }

    /**
     * Implementation of method from "Parcelable" interface. Describes the data to be written
     * when this class is packaged into a Parcel, and in what order to package it
     * @param out
     * @param flags
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(categoryId);
        out.writeString(categoryName);
        out.writeIntArray(sessions);
    }

    public static final Parcelable.Creator<LocalSessionCategory> CREATOR
            = new Parcelable.Creator<LocalSessionCategory>() {
        public LocalSessionCategory createFromParcel(Parcel in) {
            return new LocalSessionCategory(in);
        }

        public LocalSessionCategory[] newArray(int size) {
            return new LocalSessionCategory[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalSessionCategory())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSessionCategory that = (LocalSessionCategory) o;

        if (!categoryId.equals(that.categoryId)) return false;
        if (categoryName != null ? !categoryName.equals(that.categoryName) : that.categoryName != null)
            return false;
        if (!Arrays.equals(sessions, that.sessions)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = categoryId.hashCode();
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        result = 31 * result + (sessions != null ? Arrays.hashCode(sessions) : 0);
        return result;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
