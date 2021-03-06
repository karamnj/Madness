package com.sirius.madness.receiver.models;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import com.ibm.mobile.services.data.IBMDataRelation;

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
@IBMDataObjectSpecialization("SessionParticipants")
public class BluemixSessionParticipants extends IBMDataObject {
    public IBMDataRelation participantlist;
    public static final String CLASS_NAME = "BluemixSessionParticipants";

    //Column names in Bluemix data store
    private static final String SESSION_ID = "sessionId";
    private static final String PARTICIPANT_ID = "participantId";
    private static final String SESSION_NAME = "sessionName";
    private static final String PARTICIPANT_NAME = "participantName";
    private static final String PARTICIPANT_NUMBER = "participantNumber";
    private static final String PARTICIPANT_EMAIL = "participantEmail";

    /**
     * Default constructor
     */
    public BluemixSessionParticipants() {

    }

    public String getSessionId(){
        return getObject(SESSION_ID).toString();
    }
    public String getParticipantId(){
        return  getObject(PARTICIPANT_ID).toString();
    }
    public String getSessionName(){
        return  getObject(SESSION_NAME).toString();
    }
    public String getParticipantName(){
        return  getObject(PARTICIPANT_NAME).toString();
    }
    public String getParticipantNumber(){
        return  getObject(PARTICIPANT_NUMBER).toString();
    }
    public String getParticipantEmail(){
        return  getObject(PARTICIPANT_EMAIL).toString();
    }

    public void setSessionId(String item) {
        setObject(SESSION_ID, (item != null) ? item : "");
    }
    public void setParticipantId(String item) {
        setObject(PARTICIPANT_ID, (item != null) ? item : "");
    }
    public void setSessionName(String item) {
        setObject(SESSION_NAME, (item != null) ? item : "");
    }
    public void setParticipantName(String item) {
        setObject(PARTICIPANT_NAME, (item != null) ? item : "");
    }
    public void setParticipantNumber(String item) {
        setObject(PARTICIPANT_NUMBER, (item != null) ? item : "");
    }
    public void setParticipantEmail(String item) {
        setObject(PARTICIPANT_EMAIL, (item != null) ? item : "");
    }

}
