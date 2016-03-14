package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Object which represents a single Participant from the Bluemix Mobile Data store
 *
 * This class is intended to be initialized via the <code>initialize()</code> method, in the same
 * scope as a call to the Bluemix Mobile Data service, before use. This class is also intended
 * for Bluemix retrieval <strong>only</strong>. For normal usage, see <code>LocalParticipant</code>.
 *
 * The flow is as follows:
 * -Initialize Bluemix query, via <code>IBMQuery.queryForClass()</code> method
 * -Begin Bluemix query, via <code>IBMQuery.find()</code> method
 * -For each query result of this class' type, call <code>initialize()</code> to populate
 * -Class can now be converted into a <code>LocalParticipant</code> object via <code>convertToLocal()</code>
 */
@IBMDataObjectSpecialization("Participant")
public class BluemixParticipant extends IBMDataObject {
    private static final String CLASS_NAME = "BluemixParticipant";

    //Column names in Bluemix data store
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE = "phone";
    private static final String MAIL_ID = "mailId";
    private static final String COMPANY = "company";
    private static final String EMP_ID = "empId";
    private static final String JOB_TITLE = "jobTitle";
    private static final String TWITTER_LINK = "twitterLink";
    private static final String LINKED_IN_LINK = "linkedInLink";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";

    //Class variable
    LocalParticipant participant;

    /**
     * Default constructor
     */
    public BluemixParticipant() {
        participant = null;
    }

    /**
     * Initialize Participant fields for use outside a Bluemix scope.
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
        participant = new LocalParticipant();

        Object object = getObject(FIRST_NAME);

        if (object instanceof String) {
            participant.setFirstName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's first name failed, as the database returned a non-string data type");
        }

        object = getObject(LAST_NAME);

        if (object instanceof String) {
            participant.setLastName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's last name failed, as the database returned a non-string data type");
        }

        object = getObject(PHONE);

        if (object instanceof String) {
            participant.setPhone((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's phone failed, as the database returned a non-string data type");
        }

        object = getObject(MAIL_ID);

        if (object instanceof String) {
            participant.setMailId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's mailId failed, as the database returned a non-string data type");
        }

        object = getObject(COMPANY);

        if (object instanceof String) {
            participant.setCompany((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's company failed, as the database returned a non-string data type");
        }

        object = getObject(EMP_ID);

        if (object instanceof String) {
            participant.setEmpId((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's empId address failed, as the database returned a non-string data type");
        }

        object = getObject(JOB_TITLE);

        if (object instanceof String) {
            participant.setJobTitle((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's jobTitle number failed, as the database returned a non-string data type");
        }

        object = getObject(TWITTER_LINK);

        if (object instanceof String) {
            participant.setTwitterLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve participant's Twitter link failed, as the database returned a non-string data type");
        }

        object = getObject(LINKED_IN_LINK);

        if (object instanceof String) {
            participant.setLinkedInLink((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve deeloper's LinkedIn link failed, as the database returned a non-string data type");
        }

        object = getObject(USER_NAME);

        if (object instanceof String) {
            participant.setUserName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve user name failed, as the database returned a non-string data type");
        }

        object = getObject(PASSWORD);

        if (object instanceof String) {
            participant.setPassword((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve password failed, as the database returned a non-string data type");
        }
    }

    public void setFirstName(String item) {
        setObject(FIRST_NAME, (item != null) ? item : "");
    }
    public void setLastName(String item) {
        setObject(LAST_NAME, (item != null) ? item : "");
    }
    public void setPhone(String item) {
        setObject(PHONE, (item != null) ? item : "");
    }
    public void setMailId(String item) {
        setObject(MAIL_ID, (item != null) ? item : "");
    }
    public void setCompany(String item) {
        setObject(COMPANY, (item != null) ? item : "");
    }
    public void setEmpId(String item) {
        setObject(EMP_ID, (item != null) ? item : "");
    }
    public void setJobTitle(String item) {
        setObject(JOB_TITLE, (item != null) ? item : "");
    }
    public void setTwitterLink(String item) {
        setObject(TWITTER_LINK, (item != null) ? item : "");
    }
    public void setLinkedInLink(String item) {
        setObject(LINKED_IN_LINK, (item != null) ? item : "");
    }
    public void setUserName(String item) {
        setObject(USER_NAME, (item != null) ? item : "");
    }
    public void setPassword(String item) {
        setObject(PASSWORD, (item != null) ? item : "");
    }

    /**
     * Converts data received from Bluemix into data that can be stored locally
     * @return Populated object of type LocalParticipant if successful, null if unsuccessful
     */
    public LocalParticipant convertToLocal() {

        return participant;
    }
}
