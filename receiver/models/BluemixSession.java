package com.sirius.madness.receiver.models;

import android.location.Location;
import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

import java.util.Date;

/**
 * Object which represents a single Session from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalSession</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalSession</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Session")
public class BluemixSession extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixSession";

    //Column names in Bluemix data store
    private static final String SESSION_ID = "sessionId";
    private static final String SHORT_TITLE = "shortTitle";
    private static final String LONG_TITLE = "longTitle";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String HALL = "hall";
    private static final String IMAGE = "image";
    private static final String FROM_TIME = "fromTime";
    private static final String TO_TIME = "toTime";
    private static final String LOCATION = "location";
    private static final String PRESENTERS = "presenters";
    private static final String IS_HIDDEN = "isHidden";

    //Class variable
    LocalSession session;

    /**
     * Default constructor
     */
    public BluemixSession() {
        session = null;
    }

    /**
     * Initialize Session fields for use outside a Bluemix scope.
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
        session = new LocalSession();

        Object object = getObject(SESSION_ID);

        if(object instanceof Integer) {
            session.setSessionId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve session ID failed, as the database returned a non-integer data type");
        }

        object = getObject(SHORT_TITLE);

        if(object instanceof String) {
            session.setShortTitle((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve short title failed, as the database returned a non-String data type");
        }

        object = getObject(LONG_TITLE);

        if(object instanceof String) {
            session.setLongTitle((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve long title failed, as the database returned a non-String data type");
        }

        object = getObject(SHORT_DESC);

        if(object instanceof String) {
            session.setShortDesc((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve short description failed, as the database returned a non-String data type");
        }

        object = getObject(LONG_DESC);

        if(object instanceof String) {
            session.setLongDesc((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve long description failed, as the database returned a non-String data type");
        }

        object = getObject(HALL);

        if(object instanceof String) {
            session.setHall((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve hall failed, as the database returned a non-String data type");
        }

        object = getObject(IMAGE);

        if(object instanceof String) {
            try {
            session.setImage(Long.valueOf((String) object));
            } catch (NumberFormatException nfe) {
                //Image is N/A
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image failed, as the database returned a non-String data type");
        }

        object = getObject(FROM_TIME);

        if(object instanceof Date) {
            session.setFromTime((Date) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve start date failed, as the database returned a non-Date data type");
        }

        object = getObject(TO_TIME);

        if(object instanceof Date) {
            session.setToTime((Date) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve end date failed, as the database returned a non-Date data type");
        }

        object = getObject(LOCATION);
        if(object instanceof Location)
        {
            session.setLocation((Location) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve location failed, as the database returned a non-Location data type");
        }

        object = getObject(PRESENTERS);
        if(object instanceof JSONArray) {
            try {
                JSONArray jsonPresenters = (JSONArray) object;
                int[] presenters = new int[jsonPresenters.length()];
                for (int i = 0; i < jsonPresenters.length(); i++) {
                    presenters[i] = jsonPresenters.getInt(i);
                }
                session.setPresenters(presenters);
            }catch (Exception e){
                session.setPresenters(new int[0]);
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve list of presenter IDs failed, as the database returned a non-Integer[] data type");
        }

        object = getObject(IS_HIDDEN);

        if(object instanceof Boolean)
        {
            Boolean newObject = (Boolean) object;

            if(newObject == true) {
                session.setIsHidden("true");
            }else {
                session.setIsHidden("false");
            }
        }else if(object instanceof Integer) {
            Integer number = (Integer) object;

            if(number == 0) {
                session.setIsHidden("false");
            }else if(number == 1) {
                session.setIsHidden("true");
            }else {
                Log.e(CLASS_NAME, "Attempting to retrieve isHidden failed, as the database returned an Integer that could not be parsed to a Boolean");
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve isHidden failed, as the database returned a data type that was neither Boolean nor Integer");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalSession if successful, null if unsuccessful
     */
    public LocalSession convertToLocal() {

        return session;
    }
}
