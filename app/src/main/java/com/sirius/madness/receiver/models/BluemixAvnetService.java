package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("AvnetServices")
public class BluemixAvnetService extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixAvnetService";

    //Column names in Bluemix data store
    private static final String ADDRESS_TITLE = "addressTitle";
    private static final String ADDRESS_LINE_1 = "addressLine1";
    private static final String ADDRESS_LINE_2 = "addressLine2";
    private static final String SHORT_TITLE = "shortTitle";
    private static final String LONG_TITLE = "longTitle";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String LOGO = "logo";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String LINKED_IN_LINK = "linkedInLink";
    private static final String WEBSITE_LINK = "websiteLink";
    private static final String TWITTER_HASH_TAG = "twitterHashTag";


    LocalAvnetService avnetService;

    public BluemixAvnetService() {
        avnetService = null;
    }

    public void initialize() {
        avnetService = new LocalAvnetService();

        Object object = getObject(ADDRESS_TITLE);

        if (object instanceof String) {
            avnetService.setAddressTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve addressTitle failed, as the database returned a non-integer data type");
        }
        object = getObject(ADDRESS_LINE_1);

        if (object instanceof String) {
            avnetService.setAddressLine1((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve addressLine1 failed, as the database returned a non-integer data type");
        }
        object = getObject(ADDRESS_LINE_2);

        if (object instanceof String) {
            avnetService.setAddressLine2((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve addressLine2 failed, as the database returned a non-integer data type");
        }
        object = getObject(SHORT_TITLE);

        if (object instanceof String) {
            avnetService.setShortTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve shortTitle failed, as the database returned a non-integer data type");
        }

        object = getObject(LONG_TITLE);

        if (object instanceof String) {
            avnetService.setLongTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve longTitle failed, as the database returned a non-integer data type");
        }

        object = getObject(SHORT_DESC);

        if (object instanceof String) {
            avnetService.setShortDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve shortDesc failed, as the database returned a non-integer data type");
        }

        object = getObject(LONG_DESC);

        if (object instanceof String) {
            avnetService.setLongDesc((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve longDesc failed, as the database returned a non-integer data type");
        }

        object = getObject(LOGO);

        if (object instanceof String) {
            avnetService.setLogo((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve logo failed, as the database returned a non-integer data type");
        }

        object = getObject(EMAIL);

        if (object instanceof String) {
            avnetService.setEmail((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve email failed, as the database returned a non-integer data type");
        }

        object = getObject(PHONE);

        if (object instanceof String) {
            avnetService.setPhone((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve phone failed, as the database returned a non-integer data type");
        }

        object = getObject(LINKED_IN_LINK);

        if (object instanceof String) {
            avnetService.setLinkedInLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve linkedIn link failed, as the database returned a non-integer data type");
        }

        object = getObject(WEBSITE_LINK);

        if (object instanceof String) {
            avnetService.setWebsiteLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve website link failed, as the database returned a non-integer data type");
        }

        object = getObject(TWITTER_HASH_TAG);

        if (object instanceof String) {
            avnetService.setTwitterHashTag((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve twitter hash tag failed, as the database returned a non-integer data type");
        }
    }

    public LocalAvnetService convertToLocal() {
        return avnetService;
    }
}
