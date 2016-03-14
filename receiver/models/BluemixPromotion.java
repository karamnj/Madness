package com.sirius.madness.receiver.models;

import android.location.Location;
import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import java.util.Date;

@IBMDataObjectSpecialization("Promotion")
public class BluemixPromotion extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixPromotion";

    //Column names in Bluemix data store
    private static final String SHOW_FROM = "showFrom";
    private static final String SHOW_TO = "showTo";
    private static final String RSVP_LINK = "rsvpLink";
    private static final String IMAGE = "image";
    private static final String PROMOTION_ID = "promotionId";
    private static final String CALENDAREVENTID = "calendarEventId";
    private static final String VENUE = "venue";
    private static final String EVENTTITLE = "eventTitle";
    private static final String FROMTIME = "fromTime";
    private static final String TOTIME = "toTime";
    private static final String LOCATION = "location";


    LocalPromotion promotion;

    public BluemixPromotion() {
        promotion = null;
    }

    public void initialize() {
        promotion = new LocalPromotion();

        Object object = getObject(SHOW_FROM);

        if (object instanceof Date) {
            promotion.setShowFrom((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion show from failed, as the database returned a non-date data type");
        }

        object = getObject(SHOW_TO);

        if (object instanceof Date) {
            promotion.setShowTo((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion show to failed, as the database returned a non-date data type");
        }

        object = getObject(IMAGE);

        if (object instanceof String) {
            promotion.setImageId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion image failed, as the database returned a non-string data type");
        }

        object = getObject(RSVP_LINK);

        if (object instanceof String) {
            promotion.setRsvpLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion rsvp link failed, as the database returned a non-string data type");
        }

        object = getObject(PROMOTION_ID);

        if (object instanceof Integer) {
            promotion.setPromotionId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion id failed, as the database returned a non-string data type");
        }

        object = getObject(CALENDAREVENTID);

        if (object instanceof String) {
            promotion.setCalendarId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve calendar id failed, as the database returned a non-string data type");
        }

        object = getObject(VENUE);

        if (object instanceof String) {
            promotion.setVenue((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion venue failed, as the database returned a non-string data type");
        }

        object = getObject(EVENTTITLE);

        if (object instanceof String) {
            promotion.setEventTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion event title failed, as the database returned a non-string data type");
        }

        object = getObject(FROMTIME);

        if (object instanceof Date) {
            promotion.setFromTime((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion from date failed, as the database returned a non-string data type");
        }

        object = getObject(TOTIME);

        if (object instanceof Date) {
            promotion.setToTime((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion to date failed, as the database returned a non-string data type");
        }

        object = getObject(LOCATION);

        if (object instanceof Location) {
            promotion.setLocation((Location) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve promotion from date failed, as the database returned a non-string data type");
        }
    }

    public LocalPromotion convertToLocal() {
        return promotion;
    }
}
