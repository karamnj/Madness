package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

/**
 * Object which represents a single Session Category from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalSessionCategory</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalSessionCategory</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("SessionCategory")
public class BluemixSessionCategory extends IBMDataObject{
    private static final String CLASS_NAME = "BluemixSessionCategory";

    //Column names in Bluemix data store
    private static final String CATEGORY_ID = "categoryId";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String SESSIONS = "sessions";

    //Class variables
    private LocalSessionCategory category;

    /**
     * Default constructor
     */
    public BluemixSessionCategory() {
        category = null;
    }

    /**
     * Initialize Session Category fields for use outside a Bluemix scope.
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
        category = new LocalSessionCategory();

        Object object = getObject(CATEGORY_ID);

        if(object instanceof Integer) {
            category.setCategoryId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve category ID failed, as the database returned a non-string data type");
        }

        object = getObject(CATEGORY_NAME);

        if(object instanceof String) {
            category.setCategoryName((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve category name failed, as the database returned a non-string data type");
        }

        object = getObject(SESSIONS);

        if(object instanceof JSONArray) {
            try {
                JSONArray jsonPresenters = (JSONArray) object;
                int[] sessions = new int[jsonPresenters.length()];
                for (int i = 0; i < jsonPresenters.length(); i++) {
                    sessions[i] = jsonPresenters.getInt(i);
                }
                category.setSessions(sessions);
            }catch (Exception e){
                category.setSessions(new int[0]);
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve list of session IDs failed, as the database returned a non-string data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalSessionCategory if successful, null if unsuccessful
     */
    public LocalSessionCategory convertToLocal() {

        return category;
    }
}
