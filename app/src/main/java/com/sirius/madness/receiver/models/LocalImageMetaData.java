package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Object which represents a single Speaker from local data storage
 */
public class LocalImageMetaData implements Parcelable {
    public static final String CLASS_NAME = "LocalImageMetaData";

    //Class variables
    private Integer imageId;
    private String imageName;
    private String imageExtension;
    private Integer imageHeight;
    private Integer imageWidth;
    private Date uploadedDate;
    private Date modifiedDate;


    /**
     * Default constructor
     */
    public LocalImageMetaData() {
        imageId = -1;
        imageName = null;
        imageExtension = null;
        imageHeight = -1;
        imageWidth = -1;
        uploadedDate = null;
        modifiedDate = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalImageMetaData(Parcel in) {
        imageId = in.readInt();
        imageName = in.readString();
        imageExtension = in.readString();
        imageHeight = in.readInt();
        imageWidth = in.readInt();
        uploadedDate = new Date(in.readLong());
        modifiedDate = new Date(in.readLong());
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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
        out.writeString(imageName);
        out.writeString(imageExtension);
        out.writeInt(imageHeight);
        out.writeInt(imageWidth);
        out.writeLong(uploadedDate.getTime());
        out.writeLong(modifiedDate.getTime());
    }

    public static final Creator<LocalImageMetaData> CREATOR
            = new Creator<LocalImageMetaData>() {
        public LocalImageMetaData createFromParcel(Parcel in) {
            return new LocalImageMetaData(in);
        }

        public LocalImageMetaData[] newArray(int size) {
            return new LocalImageMetaData[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalImageMetaData())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalImageMetaData that = (LocalImageMetaData) o;

        if (imageExtension != null ? !imageExtension.equals(that.imageExtension) : that.imageExtension != null)
            return false;
        if (imageHeight != null ? !imageHeight.equals(that.imageHeight) : that.imageHeight != null)
            return false;
        if (!imageId.equals(that.imageId)) return false;
        if (imageName != null ? !imageName.equals(that.imageName) : that.imageName != null)
            return false;
        if (imageWidth != null ? !imageWidth.equals(that.imageWidth) : that.imageWidth != null)
            return false;
        if (modifiedDate != null ? !modifiedDate.equals(that.modifiedDate) : that.modifiedDate != null)
            return false;
        if (uploadedDate != null ? !uploadedDate.equals(that.uploadedDate) : that.uploadedDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = imageId.hashCode();
        result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
        result = 31 * result + (imageExtension != null ? imageExtension.hashCode() : 0);
        result = 31 * result + (imageHeight != null ? imageHeight.hashCode() : 0);
        result = 31 * result + (imageWidth != null ? imageWidth.hashCode() : 0);
        result = 31 * result + (uploadedDate != null ? uploadedDate.hashCode() : 0);
        result = 31 * result + (modifiedDate != null ? modifiedDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return imageName + "." + imageExtension;
    }
}
