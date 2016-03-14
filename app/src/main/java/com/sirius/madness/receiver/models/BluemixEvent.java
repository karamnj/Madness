package com.sirius.madness.receiver.models;

import android.location.Location;
import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

import java.util.Date;

/**
 * Object which represents a single Event from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalEvent</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalEvent</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Event")
public class BluemixEvent extends IBMDataObject {
    private static final String CLASS_NAME = "BluemixEvent";

    //Column names in Bluemix data store
    private static final String EVENT_ID = "eventId";
    private static final String SHORT_TITLE = "shortTitle";
    private static final String LONG_TITLE = "longTitle";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String HALL = "hall";
    private static final String IMAGE = "image";
    private static final String FROM_TIME = "fromTime";
    private static final String TO_TIME = "toTime";
    private static final String LOCATION = "location";
    private static final String PARTNERS = "partners";
    private static final String SESSIONS = "sessionIds";
    private static final String MAP = "map";

    //Class variable
    LocalEvent event;

    /**
     * Default constructor
     */
    public BluemixEvent() {
        event = null;
    }

    /**
     * Initialize Event fields for use outside a Bluemix scope.
     *
     * Data for this class will normally be retrieved directly from Bluemix Mobile Data via the
     * <code>getObject()</code> method. However, it is not possible to guarantee that Bluemix will
     * always be available. Therefore, this method stores any data retrieved from Bluemix
     * into class variables. Storing data in this manner allows the class to be portable
     * once instantiated, even if no Bluemix connection is present.
     *
     * NOTE: Class variables are only changed if Bluemix provides data that passes data type
     * testing. If Bluemix returns an unexpected data type (or does not return data at all),
     * any existing value for a specific class variable will be left in place. This allows any
     * previously retrieved data to stay in place during the event of a disconnect. If there is no
     * Bluemix connection and this class is newly created, all data values will be null by
     * default.
     */
    //TODO: Read from SQLite local storage before attempting to call Bluemix service
    public void initialize() {
        event = new LocalEvent();

        Object object = getObject(EVENT_ID);

        if (object instanceof Integer) {
            event.setEventId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event ID failed, as the database returned a non-Integer data type");
        }

        object = getObject(SHORT_TITLE);

        if (object instanceof String) {
            event.setShortTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's short title failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_TITLE);

        if (object instanceof String) {
            event.setLongTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's long title failed, as the database returned a non-string data type");
        }

        object = getObject(SHORT_DESC);

        if (object instanceof String) {
            event.setShortDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's short description failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_DESC);

        if (object instanceof String) {
            event.setLongDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's long description failed, as the database returned a non-string data type");
        }

        object = getObject(HALL);

        if (object instanceof String) {
            event.setHall((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's hall failed, as the database returned a non-string data type");
        }

        object = getObject(IMAGE);

        if (object instanceof String) {
            event.setImage((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's image failed, as the database returned a non-string data type");
        }

        object = getObject(MAP);

        if (object instanceof String) {
            event.setMap((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's map failed, as the database returned a non-string data type");
        }

        object = getObject(FROM_TIME);

        if (object instanceof Date) {
            event.setFromTime((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's start time failed, as the database returned a non-Date data type");
        }

        object = getObject(TO_TIME);

        if (object instanceof Date) {
            event.setToTime((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's end time failed, as the database returned a non-Date data type");
        }

        object = getObject(LOCATION);
        if(object instanceof Location)
        {
            event.setLocation((Location) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve location failed, as the database returned a non-Location data type");
        }

        object = getObject(PARTNERS);

        if(object instanceof JSONArray) {
            try {
                JSONArray jsonPresenters = (JSONArray) object;
                int[] presenters = new int[jsonPresenters.length()];
                for (int i = 0; i < jsonPresenters.length(); i++) {
                    presenters[i] = jsonPresenters.getInt(i);
                }
                event.setPartners(presenters);
            }catch (Exception e){
                event.setPartners(new int[0]);
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's list of partner IDs failed, as the database returned a non-Integer[] data type");
        }

        object = getObject(SESSIONS);

        if(object instanceof JSONArray) {
            try {
                JSONArray jsonPresenters = (JSONArray) object;
                int[] presenters = new int[jsonPresenters.length()];
                for (int i = 0; i < jsonPresenters.length(); i++) {
                    presenters[i] = jsonPresenters.getInt(i);
                }
                event.setPartners(presenters);
            }catch (Exception e){
                event.setPartners(new int[0]);
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve event's list of session IDs failed, as the database returned a non-Integer[] data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalEvent if successful, null if unsuccessful
     */
    public LocalEvent convertToLocal() {

        return event;
    }
}
