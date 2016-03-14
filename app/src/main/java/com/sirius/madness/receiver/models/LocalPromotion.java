package com.sirius.madness.receiver.models;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class LocalPromotion implements Parcelable {

    private Date showFrom;
    private Date showTo;
    private String imageId;
    private String rsvpLink;
    private Integer promotionId;
    private String calendarId;
    private String venue;
    private String eventTitle;
    private Date fromTime;
    private Date toTime;
    private Location location;

    public LocalPromotion(){
        showFrom = null;
        showTo = null;
        rsvpLink = null;
        imageId = null;
        promotionId = 0;
        calendarId = null;
        venue = null;
        eventTitle = null;
        fromTime = null;
        toTime = null;
        location = null;
    }

    public LocalPromotion(Parcel in){
        showFrom = new Date(in.readLong());
        showTo = new Date(in.readLong());
        imageId = in.readString();
        rsvpLink = in.readString();
        promotionId = in.readInt();
        calendarId = in.readString();
        venue = in.readString();
        eventTitle = in.readString();
        fromTime = new Date(in.readLong());
        toTime = new Date(in.readLong());
        location = (Location) in.readParcelable(Location.class.getClassLoader());
    }

    public Date getShowFrom() {
        return showFrom;
    }

    public void setShowFrom(Date showFrom) {
        this.showFrom = showFrom;
    }

    public Date getShowTo() {
        return showTo;
    }

    public void setShowTo(Date showTo) {
        this.showTo = showTo;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getRsvpLink() {
        return rsvpLink;
    }

    public void setRsvpLink(String rsvpLink) {
        this.rsvpLink = rsvpLink;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(showFrom.getTime());
        out.writeLong(showTo.getTime());
        out.writeString(rsvpLink);
        out.writeString(imageId);
        out.writeInt(promotionId);
        out.writeString(calendarId);
        out.writeString(venue);
        out.writeString(eventTitle);
        out.writeLong(fromTime.getTime());
        out.writeLong(toTime.getTime());
        out.writeParcelable(location, 0);
    }

    public static final Creator<LocalPromotion> CREATOR
            = new Creator<LocalPromotion>() {
        public LocalPromotion createFromParcel(Parcel in) {
            return new LocalPromotion(in);
        }

        public LocalPromotion[] newArray(int size) {
            return new LocalPromotion[size];
        }
    };

    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalPromotion())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalPromotion that = (LocalPromotion) o;

        if (!promotionId.equals(that.promotionId)) return false;
        if (showFrom != null ? !showFrom.equals(that.showFrom) : that.showFrom != null) return false;
        if (showTo != null ? !showTo.equals(that.showTo) : that.showTo != null) return false;
        if (rsvpLink != null ? !rsvpLink.equals(that.rsvpLink) : that.rsvpLink != null) return false;
        if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) return false;
        if (calendarId != null ? !calendarId.equals(that.calendarId) : that.calendarId != null) return false;
        if (venue != null ? !venue.equals(that.venue) : that.venue != null) return false;
        if (eventTitle != null ? !eventTitle.equals(that.eventTitle) : that.eventTitle != null) return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null) return false;
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = promotionId.hashCode();
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (showFrom != null ? showFrom.hashCode() : 0);
        result = 31 * result + (showTo != null ? showTo.hashCode() : 0);
        result = 31 * result + (rsvpLink != null ? rsvpLink.hashCode() : 0);
        result = 31 * result + (calendarId != null ? calendarId.hashCode() : 0);
        result = 31 * result + (venue != null ? venue.hashCode() : 0);
        result = 31 * result + (eventTitle != null ? eventTitle.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
