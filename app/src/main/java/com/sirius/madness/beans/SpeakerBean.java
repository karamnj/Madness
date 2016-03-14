package com.sirius.madness.beans;

/**
 * Created by 915649 on 21/01/15.
 */
public class SpeakerBean {
    private int speakerId;
    private String firstName;
    private String lastName;
    private byte[] image;
    private int partnerIdFk;

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getPartnerIdFk() {
        return partnerIdFk;
    }

    public void setPartnerIdFk(int partnerIdFk) {
        this.partnerIdFk = partnerIdFk;
    }
}
