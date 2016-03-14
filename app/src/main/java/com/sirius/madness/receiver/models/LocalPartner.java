package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Object which represents a single Partner from local data storage
 */
public class LocalPartner implements Parcelable{
    public static final String CLASS_NAME = "LocalPartner";

    //Class variables
    private Integer partnerId;
    private String shortTitle;
    private String longTitle;
    private String logo;
    private String shortDesc;
    private String longDesc;
    private String promotionImage;
    private boolean isPartner;
    private String linkedInLink;
    private String twitterHashTag;
    private String websiteLink;
    private int[] sessions;
    private int[] presenters;

    /**
     * Default constructor
     */
    public LocalPartner() {
        partnerId = -1;
        shortTitle = null;
        longTitle = null;
        logo = null;
        promotionImage = null;
        shortDesc = null;
        longDesc = null;
        isPartner = false;
        linkedInLink = null;
        twitterHashTag = null;
        websiteLink = null;
        sessions = null;
        presenters = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalPartner(Parcel in) {
        partnerId = in.readInt();
        shortTitle = in.readString();
        longTitle = in.readString();
        logo = in.readString();
        promotionImage = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        isPartner = Boolean.parseBoolean(in.readString());
        linkedInLink = in.readString();
        twitterHashTag = in.readString();
        websiteLink = in.readString();
        sessions = in.createIntArray();
        presenters = in.createIntArray();
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean isPartner) {
        this.isPartner = isPartner;
    }

    public String getLinkedInLink() {
        return linkedInLink;
    }

    public void setLinkedInLink(String linkedInLink) {
        this.linkedInLink = linkedInLink;
    }

    public String getTwitterHashTag() {
        return twitterHashTag;
    }

    public void setTwitterHashTag(String twitterHashTag) {
        this.twitterHashTag = twitterHashTag;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public int[] getSessions() {
        return sessions;
    }

    public void setSessions(int[] sessions) {
        this.sessions = sessions;
    }

    public int[] getPresenters() {
        return presenters;
    }

    public void setPresenters(int[] presenters) {
        this.presenters = presenters;
    }

    public String getPromotionImage() {
        return promotionImage;
    }

    public void setPromotionImage(String promotionImage) {
        this.promotionImage = promotionImage;
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
        out.writeInt(partnerId);
        out.writeString(shortTitle);
        out.writeString(longTitle);
        out.writeString(logo);
        out.writeString(promotionImage);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeString(String.valueOf(isPartner));
        out.writeString(linkedInLink);
        out.writeString(twitterHashTag);
        out.writeString(websiteLink);
        out.writeIntArray(sessions);
        out.writeIntArray(presenters);
    }

    public static final Creator<LocalPartner> CREATOR
            = new Creator<LocalPartner>() {
        public LocalPartner createFromParcel(Parcel in) {
            return new LocalPartner(in);
        }

        public LocalPartner[] newArray(int size) {
            return new LocalPartner[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalPartner())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalPartner that = (LocalPartner) o;

        if (isPartner != that.isPartner) return false;
        if (linkedInLink != null ? !linkedInLink.equals(that.linkedInLink) : that.linkedInLink != null)
            return false;
        if (logo != null ? !logo.equals(that.logo) : that.logo != null) return false;
        if (promotionImage != null ? !promotionImage.equals(that.promotionImage) : that.promotionImage != null) return false;
        if (longDesc != null ? !longDesc.equals(that.longDesc) : that.longDesc != null)
            return false;
        if (longTitle != null ? !longTitle.equals(that.longTitle) : that.longTitle != null)
            return false;
        if (!partnerId.equals(that.partnerId)) return false;
        if (!Arrays.equals(presenters, that.presenters)) return false;
        if (!Arrays.equals(sessions, that.sessions)) return false;
        if (shortDesc != null ? !shortDesc.equals(that.shortDesc) : that.shortDesc != null)
            return false;
        if (shortTitle != null ? !shortTitle.equals(that.shortTitle) : that.shortTitle != null)
            return false;
        if (twitterHashTag != null ? !twitterHashTag.equals(that.twitterHashTag) : that.twitterHashTag != null)
            return false;
        if (websiteLink != null ? !websiteLink.equals(that.websiteLink) : that.websiteLink != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = partnerId.hashCode();
        result = 31 * result + (shortTitle != null ? shortTitle.hashCode() : 0);
        result = 31 * result + (longTitle != null ? longTitle.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (promotionImage != null ? promotionImage.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + (isPartner ? 1 : 0);
        result = 31 * result + (linkedInLink != null ? linkedInLink.hashCode() : 0);
        result = 31 * result + (twitterHashTag != null ? twitterHashTag.hashCode() : 0);
        result = 31 * result + (websiteLink != null ? websiteLink.hashCode() : 0);
        result = 31 * result + (sessions != null ? Arrays.hashCode(sessions) : 0);
        result = 31 * result + (presenters != null ? Arrays.hashCode(presenters) : 0);
        return result;
    }

    @Override
    public String toString() {
        return partnerId + " - " + longTitle;
    }
}
