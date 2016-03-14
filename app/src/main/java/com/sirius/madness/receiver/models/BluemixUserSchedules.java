package com.sirius.madness.receiver.models;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

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
@IBMDataObjectSpecialization("UserSchedules")
public class BluemixUserSchedules extends IBMDataObject {
    public static final String CLASS_NAME = "BluemixUserSchedules";

    //Column names in Bluemix data store
    private static final String SESSIONS = "sessions";
    private static final String USERNAME = "userName";


    LocalUserSchedules userSchedules;
    /**
     * Default constructor
     */
    public BluemixUserSchedules() {
        userSchedules = null;
    }

    public void initialize() {
        userSchedules = new LocalUserSchedules();

        Object object = getObject(USERNAME);

        if (object instanceof String) {
            userSchedules.setUserName((String) object);
        } else {
            Log.e(CLASS_NAME, "Attempting to retrieve user name failed, as the database returned a non-string data type");
        }

        object = getObject(SESSIONS);
        if(object instanceof JSONArray) {
            try {
                JSONArray jsonSessions = (JSONArray) object;
                int[] sessions = new int[jsonSessions.length()];
                for (int i = 0; i < jsonSessions.length(); i++) {
                    sessions[i] = jsonSessions.getInt(i);
                }
                userSchedules.setSessions(sessions);
            }catch (Exception e){
                userSchedules.setSessions(new int[0]);
            }
        }else {
            Log.e(CLASS_NAME, "Attempting to retrieve sessions list failed, as the database returned a non-Integer[] data type");
        }
    }

    public String getSessions(){
        return getObject(SESSIONS).toString();
    }
    public String getUsername(){
        return  getObject(USERNAME).toString();
    }


    public void setSessions(String item) {
        setObject(SESSIONS, (item != null) ? item : "");
    }
    public void setUsername(String item) {
        setObject(USERNAME, (item != null) ? item : "");
    }

    public LocalUserSchedules convertToLocal() {

        return userSchedules;
    }

}
