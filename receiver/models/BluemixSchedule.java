package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.util.Date;

/**
 * Created by 915649 on 22/01/15.
 */

@IBMDataObjectSpecialization("Schedule")
public class BluemixSchedule extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixSchedule";

    //Column names in Bluemix data store
    private static final String EVENT_ID = "eventId";
    private static final String CALENDAR_EVENT_ID = "calendarEventId";
    private static final String EVENT_TITLE = "eventTitle";
    private static final String FROM_TIME = "fromTime";
    private static final String TO_TIME = "toTime";
    private static final String IS_SESSION = "isSession";
    private static final String SESSION_ID = "sessionId";

    //Class variable
    private LocalSchedule schedule;

    /**
     * Default constructor
     */
    public BluemixSchedule() {
        schedule = null;
    }

    public void initialize(){
        schedule = new LocalSchedule();

        Object object = getObject(EVENT_ID);

        if(object instanceof Integer) {
            schedule.setEventId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve event id failed, as the database returned a non-integer data type");
        }

        object = getObject(CALENDAR_EVENT_ID);
        Log.d("calendar event type",object.getClass().toString());
        if(object instanceof String) {
            schedule.setCalendarEventId((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve calendar event id failed, as the database returned a non-string data type");
        }

        object = getObject(EVENT_TITLE);

        if(object instanceof String) {
            schedule.setEventTitle((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve calendar event title failed, as the database returned a non-string data type");
        }

        object = getObject(FROM_TIME);

        if(object instanceof Date) {
            schedule.setFromTime((Date) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve from time failed, as the database returned a non-integer data type");
        }

        object = getObject(TO_TIME);

        if(object instanceof Date) {
            schedule.setToTime((Date) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve to time failed, as the database returned a non-integer data type");
        }

        if(object instanceof Integer) {
            schedule.setSessionId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve session id failed, as the database returned a non-integer data type");
        }

    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalSchedule if successful, null if unsuccessful
     */
    public LocalSchedule convertToLocal() {
        return schedule;
    }
}
