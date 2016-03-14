package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object which represents a single Speaker from local data storage
 */
public class LocalPresenter implements Parcelable {
    public static final String CLASS_NAME = "LocalPresenter";

    //Class variables
    private String firstName;
    private String lastName;
    private String shortDesc;
    private String longDesc;
    private Integer presenterId;
    private String image;
    private String phone;
    private String email;
    private String linkedInLink;
    private String twitterLink;
    private String designation;


    /**
     * Default constructor
     */
    public LocalPresenter() {
        firstName = null;
        lastName = null;
        shortDesc = null;
        longDesc = null;
        presenterId = -1;
        image = null;
        phone = null;
        email = null;
        linkedInLink = null;
        twitterLink = null;
        designation = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalPresenter(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        presenterId = in.readInt();
        image = in.readString();
        phone = in.readString();
        email = in.readString();
        linkedInLink = in.readString();
        twitterLink = in.readString();
        designation = in.readString();
    }

    /**
     * Gets first name of the Speaker
     * @return Speaker's first name if successful, null if unsuccessful
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets last name of the Speaker
     * @return Speaker's last name if successful, null if unsuccessful
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets a short description of the Speaker
     * @return Short description of the Speaker if successful, null if unsuccessful
     */
    public String getShortDesc() {
        return shortDesc;
    }

    /**
     * Gets a long description of the Speaker
     * @return Long description of the Speaker if successful, null if unsuccessful
     */
    public String getLongDesc() {
        return longDesc;
    }

    /**
     * Gets the Speaker's Presenter ID number
     * @return Speaker's Presenter ID number if successful, null if unsuccessful
     */
    public Integer getPresenterId() {
        return presenterId;
    }

    /**
     * Gets an image of the Speaker
     * @return An image of the Speaker if successful, null if unsuccessful
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the phone number of the Speaker
     * @return Speaker's phone number if successful, null if unsuccessful
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Gets the Speaker's e-mail address
     * @return Speaker's e-mail address if successful, null if unsuccessful
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets a link to the Speaker's LinkedIn page
     * @return Link to Speaker's LinkedIn page if successful, null if unsuccessful
     */
    public String getLinkedInLink() {
        return linkedInLink;
    }

    /**
     * Gets a link to the Speaker's Twitter page
     * @return Link to the Speaker's Twitter page if successful, null if unsuccessful
     */
    public String getTwitterLink() {
        return twitterLink;
    }

    /**
     * Gets the Speaker's job title/designation
     * @return Speaker's job title/designation if successful, null if unsuccessful
     */
    public String getDesignation() {
        return designation;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public void setPresenterId(Integer presenterId) {
        this.presenterId = presenterId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLinkedInLink(String linkedInLink) {
        this.linkedInLink = linkedInLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeInt(presenterId);
        out.writeString(image);
        out.writeString(phone);
        out.writeString(email);
        out.writeString(linkedInLink);
        out.writeString(twitterLink);
        out.writeString(designation);
    }

    public static final Creator<LocalPresenter> CREATOR
            = new Creator<LocalPresenter>() {
        public LocalPresenter createFromParcel(Parcel in) {
            return new LocalPresenter(in);
        }

        public LocalPresenter[] newArray(int size) {
            return new LocalPresenter[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalPresenter())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalPresenter presenter = (LocalPresenter) o;

        if (designation != null ? !designation.equals(presenter.designation) : presenter.designation != null)
            return false;
        if (email != null ? !email.equals(presenter.email) : presenter.email != null) return false;
        if (firstName != null ? !firstName.equals(presenter.firstName) : presenter.firstName != null)
            return false;
        if (image != null ? !image.equals(presenter.image) : presenter.image != null) return false;
        if (lastName != null ? !lastName.equals(presenter.lastName) : presenter.lastName != null)
            return false;
        if (linkedInLink != null ? !linkedInLink.equals(presenter.linkedInLink) : presenter.linkedInLink != null)
            return false;
        if (longDesc != null ? !longDesc.equals(presenter.longDesc) : presenter.longDesc != null)
            return false;
        if (phone != null ? !phone.equals(presenter.phone) : presenter.phone != null) return false;
        if (!presenterId.equals(presenter.presenterId)) return false;
        if (shortDesc != null ? !shortDesc.equals(presenter.shortDesc) : presenter.shortDesc != null)
            return false;
        if (twitterLink != null ? !twitterLink.equals(presenter.twitterLink) : presenter.twitterLink != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + presenterId.hashCode();
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (linkedInLink != null ? linkedInLink.hashCode() : 0);
        result = 31 * result + (twitterLink != null ? twitterLink.hashCode() : 0);
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
