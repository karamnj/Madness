package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Object which represents a single Developer from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalDeveloper</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalDeveloper</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Developer")
public class BluemixDeveloper extends IBMDataObject {
    private static final String CLASS_NAME = "BluemixDeveloper";

    //Column names in Bluemix data store
    private static final String DEVELOPER_ID = "developerId";
    private static final String PARTNER_ID = "partnerId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String IMAGE = "image";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String LINKED_IN_ID = "linkedInId";
    private static final String LINKED_IN_LINK = "linkedInLink";
    private static final String TWITTER_ID = "twitterId";
    private static final String TWITTER_LINK = "twitterLink";
    private static final String DESIGNATION = "designation";

    //Class variable
    LocalDeveloper developer;

    /**
     * Default constructor
     */
    public BluemixDeveloper() {
        developer = null;
    }

    /**
     * Initialize Developer fields for use outside a Bluemix scope.
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
        developer = new LocalDeveloper();

        Object object = getObject(DEVELOPER_ID);

        if (object instanceof Integer) {
            developer.setDeveloperId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer ID failed, as the database returned a non-Integer data type");
        }

        object = getObject(PARTNER_ID);

        if (object instanceof Integer) {
            developer.setPartnerId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner ID failed, as the database returned a non-Integer data type");
        }

        object = getObject(FIRST_NAME);

        if (object instanceof String) {
            developer.setFirstName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's first name failed, as the database returned a non-string data type");
        }

        object = getObject(LAST_NAME);

        if (object instanceof String) {
            developer.setLastName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's last name failed, as the database returned a non-string data type");
        }

        object = getObject(SHORT_DESC);

        if (object instanceof String) {
            developer.setShortDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's short description failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_DESC);

        if (object instanceof String) {
            developer.setLongDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's long description failed, as the database returned a non-string data type");
        }

        object = getObject(IMAGE);

        if (object instanceof String) {
            developer.setImage((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's image failed, as the database returned a non-string data type");
        }

        object = getObject(EMAIL);

        if (object instanceof String) {
            developer.setEmail((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's e-mail address failed, as the database returned a non-string data type");
        }

        object = getObject(PHONE);

        if (object instanceof String) {
            developer.setPhone((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's phone number failed, as the database returned a non-string data type");
        }

        object = getObject(LINKED_IN_ID);

        if (object instanceof String) {
            developer.setLinkedInId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve deeloper's LinkedIn link failed, as the database returned a non-string data type");
        }

        object = getObject(LINKED_IN_LINK);

        if (object instanceof String) {
            developer.setLinkedInLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve deeloper's LinkedIn link failed, as the database returned a non-string data type");
        }

        object = getObject(TWITTER_ID);

        if (object instanceof String) {
            developer.setTwitterId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's Twitter id failed, as the database returned a non-string data type");
        }

        object = getObject(TWITTER_LINK);

        if (object instanceof String) {
            developer.setTwitterLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's Twitter link failed, as the database returned a non-string data type");
        }

        object = getObject(DESIGNATION);

        if (object instanceof String) {
            developer.setDesignation((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve developer's designation failed, as the database returned a non-string data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalDeveloper if successful, null if unsuccessful
     */
    public LocalDeveloper convertToLocal() {

        return developer;
    }
}
