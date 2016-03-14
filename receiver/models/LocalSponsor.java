package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object which represents a single Partner from local data storage
 */
public class LocalSponsor implements Parcelable{
    public static final String CLASS_NAME = "LocalSponsor";

    //Class variables
    private Integer sponsorId;
    private String name;
    private String logo;
    private String websiteLink;

    /**
     * Default constructor
     */
    public LocalSponsor() {
        sponsorId = -1;
        name = null;
        logo = null;
        websiteLink = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSponsor(Parcel in) {
        sponsorId = in.readInt();
        name = in.readString();
        logo = in.readString();
        websiteLink = in.readString();
    }

    public Integer getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Integer partnerId) {
        this.sponsorId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String shortTitle) {
        this.name = shortTitle;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
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
        out.writeInt(sponsorId);
        out.writeString(name);
        out.writeString(logo);
        out.writeString(websiteLink);
    }

    public static final Creator<LocalSponsor> CREATOR
            = new Creator<LocalSponsor>() {
        public LocalSponsor createFromParcel(Parcel in) {
            return new LocalSponsor(in);
        }

        public LocalSponsor[] newArray(int size) {
            return new LocalSponsor[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalSponsor())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSponsor that = (LocalSponsor) o;

        if (logo != null ? !logo.equals(that.logo) : that.logo != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (!sponsorId.equals(that.sponsorId)) return false;
        if (websiteLink != null ? !websiteLink.equals(that.websiteLink) : that.websiteLink != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sponsorId.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (websiteLink != null ? websiteLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return sponsorId + " - " + name;
    }
}
