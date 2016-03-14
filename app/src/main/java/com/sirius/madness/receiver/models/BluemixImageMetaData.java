package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.util.Date;

/**
 * Object which represents a single Speaker from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalImageMetaData</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalImageMetaData</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("ImageMetaData")
public class BluemixImageMetaData extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixImageMetaData";

    //Column names in Bluemix data store
    private static final String IMAGE_ID = "imageId";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_EXTENSION = "imageExtension";
    private static final String IMAGE_HEIGHT = "imageHeight";
    private static final String IMAGE_WIDTH = "imageWidth";
    private static final String UPLOADED_DATE = "uploadedDate";
    private static final String MODIFIED_DATE = "modifiedDate";

    //Class variable
    private LocalImageMetaData imageMetaData;

    /**
     * Default constructor
     */
    public BluemixImageMetaData() {
        imageMetaData = null;
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
        imageMetaData = new LocalImageMetaData();

        Object object = getObject(IMAGE_ID);

        if(object instanceof Integer) {
            imageMetaData.setImageId((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image ID failed, as the database returned a non-integer data type");
        }

        object = getObject(IMAGE_NAME);

        if(object instanceof String) {
            imageMetaData.setImageName((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image name failed, as the database returned a non-string data type");
        }

        object = getObject(IMAGE_EXTENSION);

        if(object instanceof String) {
            imageMetaData.setImageExtension((String) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image extension failed, as the database returned a non-string data type");
        }

        object = getObject(IMAGE_HEIGHT);

        if(object instanceof Integer) {
            imageMetaData.setImageHeight((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image height failed, as the database returned a non-integer data type");
        }

        object = getObject(IMAGE_WIDTH);

        if(object instanceof Integer) {
            imageMetaData.setImageWidth((Integer) object);
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve image width failed, as the database returned a non-integer data type");
        }

        object = getObject(UPLOADED_DATE);

        if (object instanceof Date) {
            imageMetaData.setUploadedDate((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve image upload date, as the database returned a non-Date data type");
        }

        object = getObject(MODIFIED_DATE);

        if (object instanceof Date) {
            imageMetaData.setModifiedDate((Date) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve image modified date, as the database returned a non-Date data type");
        }
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalImageMetaData if successful, null if unsuccessful
     */
    public LocalImageMetaData convertToLocal() {

        return imageMetaData;
    }

}
