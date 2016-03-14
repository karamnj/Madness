package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

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
@IBMDataObjectSpecialization("Partner")
public class BluemixPartner extends IBMDataObject {
    private static final String CLASS_NAME = "BluemixPartner";

    //Column names in Bluemix data store
    private static final String PARTNER_ID = "partnerId";
    private static final String SHORT_TITLE = "shortTitle";
    private static final String LONG_TITLE = "longTitle";
    private static final String LOGO = "logo";
    private static final String PROMOTION_IMAGE = "promotionImage";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String IS_PARTNER = "isPartner";
    private static final String LINKED_IN_LINK = "linkedInLink";
    private static final String TWITTER_HASH_TAG = "twitterHashTag";
    private static final String WEBSITE_LINK = "websiteLink";
    private static final String SESSIONS = "sessions";
    private static final String PRESENTERS = "presenters";

    //Class variable
    LocalPartner partner;

    /**
     * Default constructor
     */
    public BluemixPartner() {
        partner = null;
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
        partner = new LocalPartner();

        Object object = getObject(PARTNER_ID);

        if (object instanceof Integer) {
            partner.setPartnerId((Integer) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner ID failed, as the database returned a non-Integer data type");
        }

        object = getObject(SHORT_TITLE);

        if (object instanceof String) {
            partner.setShortTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve short title failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_TITLE);

        if (object instanceof String) {
            partner.setLongTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve long title failed, as the database returned a non-string data type");
        }

        object = getObject(LOGO);

        if (object instanceof String) {
            partner.setLogo((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner logo failed, as the database returned a non-string data type");
        }

        object = getObject(PROMOTION_IMAGE);

        if (object instanceof String) {
            partner.setPromotionImage((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner logo failed, as the database returned a non-string data type");
        }

        object = getObject(SHORT_DESC);

        if (object instanceof String) {
            partner.setShortDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve short description failed, as the database returned a non-string data type");
        }

        object = getObject(LONG_DESC);

        if (object instanceof String) {
            partner.setLongDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve long description failed, as the database returned a non-string data type");
        }

        object = getObject(IS_PARTNER);
        if (object.getClass() == Boolean.class) {
            partner.setPartner((boolean) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner status failed, as the database returned a non-boolean data type");
        }

        object = getObject(LINKED_IN_LINK);

        if (object instanceof String) {
            partner.setLinkedInLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve LinkedIn link failed, as the database returned a non-string data type");
        }

        object = getObject(TWITTER_HASH_TAG);

        if (object instanceof String) {
            partner.setTwitterHashTag((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner's Twitter hashtag failed, as the database returned a non-string data type");
        }

        object = getObject(WEBSITE_LINK);

        if (object instanceof String) {
            partner.setWebsiteLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner's website link failed, as the database returned a non-string data type");
        }

        object = getObject(SESSIONS);

        if (object instanceof JSONArray) {
            try {
                JSONArray jsonSessions = (JSONArray) object;
                int[] sessions = new int[jsonSessions.length()];
                for (int i = 0; i < jsonSessions.length(); i++) {
                    sessions[i] = jsonSessions.getInt(i);
                }
                partner.setSessions(sessions);
            }catch (Exception e){
                partner.setSessions(new int[0]);
            }

        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner's list of session IDs failed, as the database returned a non-string data type");
        }

        object = getObject(PRESENTERS);

        if (object instanceof JSONArray) {
            try {
                JSONArray jsonPresenters = (JSONArray) object;
                int[] presenters = new int[jsonPresenters.length()];
                for (int i = 0; i < jsonPresenters.length(); i++) {
                    presenters[i] = jsonPresenters.getInt(i);
                }
                partner.setPresenters(presenters);
            }catch (Exception e){
                partner.setPresenters(new int[0]);
            }

        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve partner's list of presenter IDs failed, as the database returned a non-string data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalPartner if successful, null if unsuccessful
     */
    public LocalPartner convertToLocal() {

        return partner;
    }
}
