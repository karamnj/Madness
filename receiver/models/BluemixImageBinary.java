package com.sirius.madness.receiver.models;

import android.util.Base64;
import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Object which represents a single Speaker from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalImageBinary</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalImageBinary</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("ImageDataPoc")
public class BluemixImageBinary extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixImageBinary";

    //Column names in Bluemix data store
    private static final String IMAGE_ID = "imageId";
    private static final String IMAGE_DATA = "imageData";

    //Class variable
    private LocalImageBinary imageBinary;

    /**
     * Default constructor
     */
    public BluemixImageBinary() {
        imageBinary = null;
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
        imageBinary = new LocalImageBinary();

        Object object = getObject(IMAGE_ID);

        if(object instanceof Integer) {
            imageBinary.setImageId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image ID failed, as the database returned a non-integer data type");
        }

        object = getObject(IMAGE_DATA);

        if(object instanceof String) {
            byte[] data = null;
            try{
                data = Base64.decode(((String)object).getBytes(), Base64.DEFAULT);
            }catch (Exception e){
                Log.d("Base64", Log.getStackTraceString(e));
            }

            imageBinary.setImageData(data);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image data failed, as the database returned a non-byte array data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalImageBinary if successful, null if unsuccessful
     */
    public LocalImageBinary convertToLocal() {

        return imageBinary;
    }

}
