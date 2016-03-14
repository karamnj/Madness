package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;


public class LocalAvnetService implements Parcelable{

    String addressTitle;
    String addressLine1;
    String addressLine2;
    String shortTitle;
    String longTitle;
    String shortDesc;
    String longDesc;
    String logo;
    String email;
    String phone;
    String linkedInLink;
    String websiteLink;
    String twitterHashTag;

    public LocalAvnetService(){
        addressTitle = null;
        addressLine1 = null;
        addressLine2 = null;
        shortTitle = null;
        longTitle = null;
        shortDesc = null;
        longDesc = null;
        logo = null;
        email = null;
        phone = null;
        linkedInLink = null;
        websiteLink = null;
        twitterHashTag = null;
    }

    public LocalAvnetService(Parcel in){
        addressTitle = in.readString();
        addressLine1 = in.readString();
        addressLine2 = in.readString();
        shortTitle = in.readString();
        longTitle = in.readString();
        shortDesc = in.readString();
        longDesc = in.readString();
        logo = in.readString();
        email = in.readString();
        phone = in.readString();
        linkedInLink = in.readString();
        websiteLink = in.readString();
        twitterHashTag = in.readString();
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getTwitterHashTag() {
        return twitterHashTag;
    }

    public void setTwitterHashTag(String twitterHashTag) {
        this.twitterHashTag = twitterHashTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(addressTitle);
        out.writeString(addressLine1);
        out.writeString(addressLine2);
        out.writeString(shortTitle);
        out.writeString(longTitle);
        out.writeString(shortDesc);
        out.writeString(longDesc);
        out.writeString(email);
        out.writeString(phone);
        out.writeString(logo);
        out.writeString(linkedInLink);
        out.writeString(websiteLink);
        out.writeString(twitterHashTag);

    }

    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalPromotion())) {
            result = false;
        }

        return result;
    }

    public static final Creator<LocalAvnetService> CREATOR
            = new Creator<LocalAvnetService>() {
        public LocalAvnetService createFromParcel(Parcel in) {
            return new LocalAvnetService(in);
        }

        public LocalAvnetService[] newArray(int size) {
            return new LocalAvnetService[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalAvnetService that = (LocalAvnetService) o;

        if (addressTitle != null ? !addressTitle.equals(that.addressTitle) : that.addressTitle != null) return false;
        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) return false;
        if (shortTitle != null ? !shortTitle.equals(that.shortTitle) : that.shortTitle != null) return false;
        if (longTitle != null ? !longTitle.equals(that.longTitle) : that.longTitle != null) return false;
        if (shortDesc != null ? !shortDesc.equals(that.shortDesc) : that.shortDesc != null) return false;
        if (longDesc != null ? !longDesc.equals(that.longDesc) : that.longDesc != null) return false;
        if (logo != null ? !logo.equals(that.logo) : that.logo != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (linkedInLink != null ? !linkedInLink.equals(that.linkedInLink) : that.linkedInLink != null) return false;
        if (websiteLink != null ? !websiteLink.equals(that.websiteLink) : that.websiteLink != null) return false;
        if (twitterHashTag != null ? !twitterHashTag.equals(that.twitterHashTag) : that.twitterHashTag != null) return false;


        return true;
    }
    @Override
    public int hashCode() {
        int result = shortTitle.hashCode();
        result = 31 * result + (addressTitle != null ? addressTitle.hashCode() : 0);
        result = 31 * result + (addressLine1 != null ? addressLine1.hashCode() : 0);
        result = 31 * result + (addressLine2 != null ? addressLine2.hashCode() : 0);
        result = 31 * result + (longTitle != null ? longTitle.hashCode() : 0);
        result = 31 * result + (shortDesc != null ? shortDesc.hashCode() : 0);
        result = 31 * result + (longDesc != null ? longDesc.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (linkedInLink != null ? linkedInLink.hashCode() : 0);
        result = 31 * result + (websiteLink != null ? websiteLink.hashCode() : 0);
        result = 31 * result + (twitterHashTag != null ? twitterHashTag.hashCode() : 0);
        return result;
    }
}
