package com.sirius.madness.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.ParticipantBean;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.BluemixSessionParticipants;
import com.sirius.madness.ui.adapters.RegisterParticipantAdapter;
import com.google.gson.Gson;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class RegisterParticipantActivity extends BaseActivity {

    private static final String CLASS_NAME = "RegisterParticipant";
    private static final int REGISTER_PARTICIPANT_REQUEST = 1;
    private List<SessionBean> session_data;
    private ListView sessionListView;

    private final String[] projectionList = {
            IgniteContract.Sessions.SESSION_ID,
            IgniteContract.Sessions.SESSION_IMAGE,
            IgniteContract.Sessions.SESSION_SHORT_TITLE,
            IgniteContract.Sessions.SESSION_LONG_TITLE,
            IgniteContract.Sessions.SESSION_LONG_DESCRIPTION,
            IgniteContract.Sessions.SESSION_HALL,
            IgniteContract.Sessions.SESSION_FROM_TIME,
            IgniteContract.Sessions.SESSION_TO_TIME,
            IgniteContract.Sessions.SESSION_IS_HIDDEN,
            IgniteContract.Categories.CATEGORY_NAME,
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.Schedules.CALENDAR_EVENT_ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_participant);
        isFirstLevelActivity = true;

        sessionListView = (ListView) findViewById(R.id.sessionList);

        Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
        session_data = new ArrayList<SessionBean>();
        initDataset(cursor);

        //If we have session data, show it to the user
        if(session_data != null && session_data.size() > 0) {
            Log.d(CLASS_NAME, "session_data is > 0");
            /*for(int i = 0; i < session_data.size(); i++){
                if(session_data.get(i).getIsHidden()=="true"){
                    session_data.remove(i);
                }
            }*/
            RegisterParticipantAdapter adapter = new RegisterParticipantAdapter(RegisterParticipantActivity.this, R.layout.layout_register_participant, session_data);
            sessionListView.setAdapter(adapter);

            sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(RegisterParticipantActivity.this, QRScannerActivity.class);
                    intent.putExtra("RegisterParticipant", true);
                    intent.putExtra("sessionId",String.valueOf(session_data.get(position).getSessionId()));
                    intent.putExtra("sessionName",String.valueOf(session_data.get(position).getSessionName()));
                    startActivityForResult(intent, REGISTER_PARTICIPANT_REQUEST);
                }
            });
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            sessionListView.setVisibility(ListView.GONE);
            TextView sessionError = (TextView) findViewById(R.id.sessionErrorMessage);
            sessionError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REGISTER_PARTICIPANT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra("ScanResult");
                String sessionId = data.getStringExtra("sessionId");
                String sessionName = data.getStringExtra("sessionName");
                try {
                    Gson object = new Gson();
                    //JsonObject obj = new JsonParser().parse().getAsJsonObject();
                    final ParticipantBean bean = object.fromJson(scanResult, ParticipantBean.class);
                    //TODO Check-In to Session (store sessionId and empId to Blumix)
                    if(isNetworkAvailable()) {
                        storeSessionParticipants(sessionId, sessionName, bean);
                    }else{
                        Toast.makeText(RegisterParticipantActivity.this, "No Internet Connectivity", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e("JsonParsingException : ",e.getMessage());
                }
            }else if (resultCode == RESULT_CANCELED) {
                if(data != null) {
                    Toast.makeText(RegisterParticipantActivity.this, "QR-Code is invalid", Toast.LENGTH_LONG).show();
                    String sessionId = data.getStringExtra("sessionId");
                    //write your code here to be executed after 1 second
                    Intent intent = new Intent(RegisterParticipantActivity.this, QRScannerActivity.class);
                    intent.putExtra("RegisterParticipant", true);
                    intent.putExtra("sessionId", sessionId);
                    startActivityForResult(intent, REGISTER_PARTICIPANT_REQUEST);
                }
            }
        }
    }

    public void storeSessionParticipants(final String sessionId, final String sessionName, final ParticipantBean participant){
        IBMQuery<BluemixSessionParticipants> query = IBMQuery.queryForClass("SessionParticipants");
        // Query all the BluemixParticipant objects from the server

        query.whereKeyEqualsTo("sessionId", sessionId);
        query.whereKeyEqualsTo("participantId", participant.getEmpId());
        query.find().onSuccess(new Continuation<List<BluemixSessionParticipants>, Void>() {
            @Override
            public Void then(Task<List<BluemixSessionParticipants>> task) throws Exception {
                final List<BluemixSessionParticipants> objects = task.getResult();

                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSessionParticipants : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSessionParticipants : " + task.getError().getMessage());
                } else {
                    if (objects.size() == 0) {
                        Log.d("Registering: ", participant.getEmpId());
                        BluemixSessionParticipants sParticipant = new BluemixSessionParticipants();

                        sParticipant.setSessionId(sessionId);
                        sParticipant.setParticipantId(participant.getEmpId());
                        sParticipant.setSessionName(sessionName);
                        sParticipant.setParticipantName(participant.getFirstName()+" "+participant.getLastName());
                        sParticipant.setParticipantNumber(participant.getPhone());
                        sParticipant.setParticipantEmail(participant.getMailId());

                        // Use the IBMDataObject to create and persist the Item object.
                        sParticipant.save().continueWith(new Continuation<IBMDataObject, Void>() {

                            @Override
                            public Void then(Task<IBMDataObject> task) throws Exception {
                                // Log if the save was cancelled.
                                if (task.isCancelled()) {
                                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                                }
                                // Log error message, if the save task fails.
                                else if (task.isFaulted()) {
                                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                }

                                // If the result succeeds, show toast.
                                else {
                                    Toast.makeText(RegisterParticipantActivity.this, "Participant Added", Toast.LENGTH_SHORT).show();
                                }
                                return null;
                            }

                        });
                        Log.d("Check-In:", "Participant Details Added Successfully");
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                        Intent intent = new Intent(RegisterParticipantActivity.this, QRScannerActivity.class);
                        intent.putExtra("RegisterParticipant", true);
                        intent.putExtra("sessionId", sessionId);
                        intent.putExtra("toast", "Participant Details Added Successfully");
                        startActivityForResult(intent, REGISTER_PARTICIPANT_REQUEST);
                    } else {
                        Log.d("Check-In:", "Participant Already Added");
                        Intent intent = new Intent(RegisterParticipantActivity.this, QRScannerActivity.class);
                        intent.putExtra("RegisterParticipant", true);
                        intent.putExtra("sessionId", sessionId);
                        intent.putExtra("toast", "Participant Already Added");
                        startActivityForResult(intent, REGISTER_PARTICIPANT_REQUEST);
                    }
                }
                return null;
            }
        });
    }

    public void initDataset(Cursor cursor) {

        Log.d(CLASS_NAME, "Entering initDataset");

        cursor.moveToFirst();
        Date date;
        int i = 0;
        if (cursor.getCount() > 0) {
            date = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
            do {
                Log.d(CLASS_NAME, cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)) + "");
                Log.d(CLASS_NAME, cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));

                if(!"true".equals(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)))) {
                    SessionBean session = new SessionBean();
                    session.setSessionId(cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)));
                    session.setSessionName(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
                    session.setSessionCategory(cursor.getString(cursor.getColumnIndex(IgniteContract.Categories.CATEGORY_NAME)));
                    session.setSessionDescription(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION)));
                    session.setFromTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                    session.setToTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_TO_TIME)));
                    session.setSessionBg(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                    session.setCalendarEventId(cursor.getString(cursor.getColumnIndex(IgniteContract.Schedules.CALENDAR_EVENT_ID)));
                    session.setIsHidden(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));
                    if (i == 0) {
                        session.setHeaderIndex(0);
                    } else {
                        Date fromDate = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                        if (i > 0 && (date.getDay() != fromDate.getDay())) {
                            session.setHeaderIndex(1);
                        } else {
                            session.setHeaderIndex(0);
                        }
                    }
                    session_data.add(session);
                }
                i++;
            } while (cursor.moveToNext());
        }else {
            Log.d(CLASS_NAME, "Cursor returned no results!");
        }
        Log.d(CLASS_NAME, "Exiting initDataset");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected int getSelfMenuBarItem() {
        return MENU_BAR_ITEM_REGISTER_PARTICIPANT;
    }
}
