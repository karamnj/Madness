package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Object which represents a single Speaker from local data storage
 */
public class LocalImageBinary implements Parcelable {
    public static final String CLASS_NAME = "LocalImageBinary";

    //Class variables
    private Integer imageId;
    private byte[] imageData;


    /**
     * Default constructor
     */
    public LocalImageBinary() {
        imageId = -1;
        imageData = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalImageBinary(Parcel in) {
        imageId = in.readInt();
        imageData = in.createByteArray();
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
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
        out.writeInt(imageId);
        out.writeByteArray(imageData);
    }

    public static final Creator<LocalImageBinary> CREATOR
            = new Creator<LocalImageBinary>() {
        public LocalImageBinary createFromParcel(Parcel in) {
            return new LocalImageBinary(in);
        }

        public LocalImageBinary[] newArray(int size) {
            return new LocalImageBinary[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalImageBinary())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalImageBinary that = (LocalImageBinary) o;

        if (!Arrays.equals(imageData, that.imageData)) return false;
        if (!imageId.equals(that.imageId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = imageId.hashCode();
        result = 31 * result + (imageData != null ? Arrays.hashCode(imageData) : 0);
        return result;
    }

    @Override
    public String toString() {
        return imageId + "";
    }
}
