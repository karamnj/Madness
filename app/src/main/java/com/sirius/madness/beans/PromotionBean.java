package com.sirius.madness.beans;

/**
 * Created by 915649 on 21/01/15.
 */
public class PromotionBean {
    byte[] image;
    String rsvpLink;
    Long show_from;
    Long show_to;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getRsvpLink() {
        return rsvpLink;
    }

    public void setRsvpLink(String rsvpLink) {
        this.rsvpLink = rsvpLink;
    }

    public Long getShow_from() {
        return show_from;
    }

    public void setShow_from(Long show_from) {
        this.show_from = show_from;
    }

    public Long getShow_to() {
        return show_to;
    }

    public void setShow_to(Long show_to) {
        this.show_to = show_to;
    }
}
