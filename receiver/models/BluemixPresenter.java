package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Object which represents a single Speaker from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalPresenter</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalPresenter</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Presenter")
public class BluemixPresenter extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixPresenter";

    //Column names in Bluemix data store
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String PRESENTER_ID = "presenterId";
    private static final String IMAGE = "image";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String LINKED_IN_LINK = "linkedInLink";
    private static final String TWITTER_LINK = "twitterLink";
    private static final String DESIGNATION = "designation";

    //Class variable
    private LocalPresenter presenter;

    /**
     * Default constructor
     */
    public BluemixPresenter() {
        presenter = null;
    }

    /**
     * Initialize Speaker fields for use outside a Bluemix scope.
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
        presenter = new LocalPresenter();

        Object object = getObject(FIRST_NAME);

        if(object instanceof String) {
            presenter.setFirstName((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve first name failed, as the database returned a non-string data type");
        }

        object = getObject(LAST_NAME);

        if(object instanceof String) {
            presenter.setLastName((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve last name failed, as the database returned a non-string data type");
        }

        object = getObject(SHORT_DESC);

        if(object instanceof String) {
            presenter.setShortDesc((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve short description failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_DESC);

        if(object instanceof String) {
            presenter.setLongDesc((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve long description failed, as the database returned a non-string data type");
        }

        object = getObject(PRESENTER_ID);

        if(object instanceof Integer) {
            presenter.setPresenterId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve presenter ID failed, as the database returned a non-integer data type");
        }

        object = getObject(IMAGE);

        if(object instanceof String) {
            presenter.setImage((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image failed, as the database returned a non-string data type");
        }

        object = getObject(PHONE);

        if(object instanceof String) {
            presenter.setPhone((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve phone number failed, as the database returned a non-string data type");
        }

        object = getObject(EMAIL);

        if(object instanceof String) {
            presenter.setEmail((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve e-mail address failed, as the database returned a non-string data type");
        }

        object = getObject(LINKED_IN_LINK);

        if(object instanceof String) {
            presenter.setLinkedInLink((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve LinkedIn link failed, as the database returned a non-string data type");
        }

        object = getObject(TWITTER_LINK);

        if(object instanceof String) {
            presenter.setTwitterLink((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve Twitter link failed, as the database returned a non-string data type");
        }

        object = getObject(DESIGNATION);

        if(object instanceof String) {
            presenter.setDesignation((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve job designation failed, as the database returned a non-string data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalPresenter if successful, null if unsuccessful
     */
    public LocalPresenter convertToLocal() {

        return presenter;
    }

}
