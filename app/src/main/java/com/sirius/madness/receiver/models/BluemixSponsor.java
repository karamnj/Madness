package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Object which represents a single Partner from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalPartner</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalPartner</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Sponsor")
public class BluemixSponsor extends IBMDataObject {
    private static final String CLASS_NAME = "BluemixSponsor";

    //Column names in Bluemix data store
    private static final String SPONSOR_ID = "sponsorId";
    private static final String NAME = "name";
    private static final String LOGO = "logo";
    private static final String WEBSITE_LINK = "websiteLink";
    private static final String CATEGORY = "category";
    //Class variable
    LocalSponsor sponsor;

    /**
     * Default constructor
     */
    public BluemixSponsor() {
        sponsor = null;
    }

    /**
     * Initialize Partner fields for use outside a Bluemix scope.
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
        sponsor = new LocalSponsor();

        Object object = getObject(SPONSOR_ID);

        if (object instanceof Integer) {
            sponsor.setSponsorId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve sponsor ID failed, as the database returned a non-Integer data type");
        }

        object = getObject(NAME);

        if (object instanceof String) {
            sponsor.setName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve Name failed, as the database returned a non-string data type");
        }

        object = getObject(LOGO);

        if (object instanceof Integer) {
            sponsor.setLogo((String) String.valueOf(object));
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner logo failed, as the database returned a non-string data type");
        }

        object = getObject(WEBSITE_LINK);

        if (object instanceof String) {
            sponsor.setWebsiteLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner's website link failed, as the database returned a non-string data type");
        }

        object = getObject(CATEGORY);

        if (object instanceof Integer) {
            sponsor.setCategory((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve category failed, as the database returned a non-Integer data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalPartner if successful, null if unsuccessful
     */
    public LocalSponsor convertToLocal() {

        return sponsor;
    }
}
