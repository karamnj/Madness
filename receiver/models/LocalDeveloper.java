package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object which represents a single Developer from local data storage
 */
public class LocalDeveloper implements Parcelable{
    private static final String CLASS_NAME = "LocalDeveloper";

    //Class variables
    private Integer developerId;
    private Integer partnerId;
    private String firstName;
    private String lastName;
    private String shortDesc;
    private String longDesc;
    private String image;
    private String email;
    private String phone;
    private String linkedInId;
    private String linkedInLink;
    private String twitterId;
    private String twitterLink;
    private String designation;



    /**
     * Default constructor
     */
    public LocalDeveloper() {
        developerId = -1;
        partnerId = -1;
        firstName = null;
        lastName = null;
        shortDesc = null;
        longDesc = null;
        image = null;
        email = null;
        phone = null;
        linkedInLink = null;
        twitterLink = null;
        designation = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalDeveloper(Parcel in) {
        developerId = in.readInt();
        partnerId = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        image = in.readString();
        email = in.readString();
        phone = in.readString();
        linkedInId = in.readString();
        linkedInLink = in.readString();
        twitterId = in.readString();
        twitterLink = in.readString();
        designation = in.readString();
    }

    public Integer getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Integer developerId) {
        this.developerId = developerId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLinkedInLink() {
        return linkedInLink;
    }

    public void setLinkedInLink(String linkedInLink) {
        this.linkedInLink = linkedInLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getDesignation() { return designation; }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLinkedInId() { return linkedInId; }

    public void setLinkedInId(String linkedInId) { this.linkedInId = linkedInId; }

    public String getTwitterId() { return twitterId; }

    public void setTwitterId(String twitterId) { this.twitterId = twitterId;
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
        out.writeInt(developerId);
        out.writeInt(partnerId);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeString(image);
        out.writeString(email);
        out.writeString(phone);
        out.writeString(linkedInId);
        out.writeString(linkedInLink);
        out.writeString(twitterId);
        out.writeString(twitterLink);
        out.writeString(designation);
    }

    public static final Creator<LocalDeveloper> CREATOR
            = new Creator<LocalDeveloper>() {
        public LocalDeveloper createFromParcel(Parcel in) {
            return new LocalDeveloper(in);
        }

        public LocalDeveloper[] newArray(int size) {
            return new LocalDeveloper[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalDeveloper())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalDeveloper that = (LocalDeveloper) o;

        if (!developerId.equals(that.developerId)) return false;
        if (!partnerId.equals(that.partnerId)) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (linkedInId != null ? !linkedInId.equals(that.linkedInId) : that.linkedInId != null)
            return false;
        if (linkedInLink != null ? !linkedInLink.equals(that.linkedInLink) : that.linkedInLink != null)
            return false;
        if (longDesc != null ? !longDesc.equals(that.longDesc) : that.longDesc != null)
            return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (shortDesc != null ? !shortDesc.equals(that.shortDesc) : that.shortDesc != null)
            return false;
        if (twitterId != null ? !twitterId.equals(that.twitterId) : that.twitterId != null)
            return false;
        if (twitterLink != null ? !twitterLink.equals(that.twitterLink) : that.twitterLink != null)
            return false;
        if (designation != null ? !designation.equals(that.designation) : that.designation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = developerId.hashCode();
        result = 31 * result + (partnerId != null ? partnerId.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (linkedInId != null ? linkedInId.hashCode() : 0);
        result = 31 * result + (linkedInLink != null ? linkedInLink.hashCode() : 0);
        result = 31 * result + (twitterId != null ? twitterId.hashCode() : 0);
        result = 31 * result + (twitterLink != null ? twitterLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return developerId + " - " + firstName + " " + lastName;
    }
}
