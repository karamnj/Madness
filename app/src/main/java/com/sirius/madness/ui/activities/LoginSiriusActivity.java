package com.sirius.madness.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;
import com.sirius.madness.R;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.BluemixParticipant;
import com.sirius.madness.receiver.models.BluemixSchedule;
import com.sirius.madness.receiver.models.BluemixUserSchedules;
import com.sirius.madness.receiver.models.LocalParticipant;
import com.sirius.madness.receiver.models.LocalSchedule;
import com.sirius.madness.receiver.models.LocalUserSchedules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import bolts.Continuation;
import bolts.Task;

public class LoginSiriusActivity extends BaseActivity {
    private static final String CLASS_NAME = "LoginSirius";

    private SharedPreferences prefs;
    Boolean incorrectLogin = false;
    private SessionBean session_data[];
    private LocalUserSchedules localUserSchedules;
    Bundle localSavedInstanceState;
    Dialog dialog;

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
        localSavedInstanceState =savedInstanceState;
        setContentView(R.layout.activity_login_sirius);
        prefs = PreferenceManager.getDefaultSharedPreferences(LoginSiriusActivity.this);
        //Check if already Logged In
        String loggedin = prefs.getString("participantUName",null);
        if(loggedin!=null){
            Log.d("participantUName",loggedin);
            if(!loggedin.equalsIgnoreCase("partner") && !loggedin.equalsIgnoreCase(" partner")) {
                Intent nextIntent = new Intent(LoginSiriusActivity.this, DiscoverActivity.class);
                startActivity(nextIntent);
                finish();
            }else{
                Intent nextIntent = new Intent(LoginSiriusActivity.this, SponsorsActivity.class);
                startActivity(nextIntent);
                finish();
            }
        }
        getSupportActionBar().hide();

        final EditText uName = (EditText) findViewById(R.id.user_name);
        final EditText pwd = (EditText) findViewById(R.id.password);

        /*dialog = new Dialog(LoginSiriusActivity.this);
        dialog.setContentView(R.layout.dialog_layout_login);
        TextView okBtn = (TextView) dialog.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        if(incorrectLogin){
            //dialog.show();
            Toast.makeText(LoginSiriusActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
        }

        TextView login = (TextView) findViewById(R.id.login_now);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("login", "*");
                dialogLoading.show();
                if(uName.getText().toString()!="" && pwd.getText().toString()!=""){
                    checkCredentials(uName.getText().toString(), pwd.getText().toString());
                }else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginSiriusActivity.this);
                            alertDialogBuilder.setMessage("Please enter both Username and Passeord.").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                            Button bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            bq.setTextColor(Color.BLUE);
                        }
                    });
                }
            }
        });
        /*uName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(uName);
                }
            }
        });
        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(pwd);
                }
            }
        });*/

    }
    private void alert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginSiriusActivity.this);
        alertDialogBuilder.setMessage("Incorrect Username or Password").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        Button bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.BLUE);
    }

    private void hideKeyboard(EditText text) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }
    public void checkCredentials(final String userId, String pwd){
        IBMQuery<BluemixParticipant> query = IBMQuery.queryForClass("Participant");
        // Query the BluemixParticipant objects from the server

        query.whereKeyEqualsTo("userName", userId);
        query.whereKeyEqualsTo("password", pwd);
        query.find().onSuccess(new Continuation<List<BluemixParticipant>, Void>() {
            @Override
            public Void then(Task<List<BluemixParticipant>> task) throws Exception {
                final List<BluemixParticipant> objects = task.getResult();

                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception in BluemixParticipant : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception in BluemixParticipant : " + task.getError().getMessage());
                } else {
                    if (objects.size() == 0) {
                        Log.d(CLASS_NAME, "Incorrect Credentials Login");
                        incorrectLogin = true;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                alert();
                            }
                        });
                    } else {
                        Log.d("Sirius:", "Logging In");
                        for (IBMDataObject BluemixParticipant : objects) {
                            try {
                                com.sirius.madness.receiver.models.BluemixParticipant item = (BluemixParticipant) BluemixParticipant;
                                item.initialize();
                                LocalParticipant localParticipant = item.convertToLocal();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("participantId", localParticipant.getEmpId());
                                Log.d("participantUName", localParticipant.getUserName());
                                editor.putString("participantUName", localParticipant.getUserName());
                                editor.putString("participantName", localParticipant.getFirstName() + " " + localParticipant.getLastName());
                                editor.putString("participantNumber", localParticipant.getPhone());
                                editor.putString("participantEmail", localParticipant.getMailId());
                                editor.commit();
                                if(!localParticipant.getUserName().equalsIgnoreCase("partner") && !localParticipant.getUserName().equalsIgnoreCase(" partner")) {
                                    loadUserBasedSchedule(userId);
                                }else{
                                    Intent nextIntent = new Intent(LoginSiriusActivity.this, SponsorsActivity.class);
                                    startActivity(nextIntent);
                                    finish();
                                }
                            } catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a metadata from an image : " + error.getMessage());
                            }
                        }
                    }
                }
                return null;
            }
        });
    }

    public void loadUserBasedSchedule(String userId){
        IBMQuery<BluemixUserSchedules> query = IBMQuery.queryForClass("UserSchedules");
        // Query the BluemixUserSchedules objects from the server

        Log.d("UserBased","schedules for "+userId);
        query.whereKeyEqualsTo("userName", userId);
        query.find().onSuccess(new Continuation<List<BluemixUserSchedules>, Void>() {
            @Override
            public Void then(Task<List<BluemixUserSchedules>> task) throws Exception {
                final List<BluemixUserSchedules> objects = task.getResult();

                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSurveyAnswers : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSurveyAnswers : " + task.getError().getMessage());
                } else {
                    if (objects.size() == 0) {
                        Log.d(CLASS_NAME, "No userSchedules");
                        Intent nextIntent = new Intent(LoginSiriusActivity.this, DiscoverActivity.class);
                        startActivity(nextIntent);
                        finish();
                    } else {
                        Log.d("Sirius:", "Logging In");
                        for (IBMDataObject BluemixUserSchedule : objects) {
                            try {
                                BluemixUserSchedules item = (BluemixUserSchedules) BluemixUserSchedule;
                                item.initialize();
                                localUserSchedules = item.convertToLocal();
                                getSchedules();
                            } catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a metadata from an image : " + error.getMessage());
                            }
                        }
                    }
                }
                return null;
            }
        });
    }
    private void addToSchedule(final int[] userSessionList){
        runOnUiThread(new Runnable() {
            public void run() {
                Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
                initDataset(cursor);
                Log.d("Sirius: SessionLength", String.valueOf(session_data.length));
                if (session_data.length != 0) {
                    for (SessionBean session : session_data) {
                        for (int i = 0; i < userSessionList.length; i++) {
                            if (userSessionList[i] == session.getSessionId()) {
                                Log.d("Sirius:addtocalender", ""+session.getSessionId());
                                addToCalender(session, session_data);
                            }
                        }
                    }
                } else {
                    Log.d(CLASS_NAME, "session_data is null or empty");
                }
                cursor.close();
                Intent nextIntent = new Intent(LoginSiriusActivity.this, DiscoverActivity.class);
                startActivity(nextIntent);
                finish();
            }
        });
    }
    public void initDataset(Cursor cursor) {
        Log.d(CLASS_NAME, "Entering initDataset");

        String scheduleProjection[] = {
                IgniteContract.Schedules.CALENDAR_EVENT_ID
        };
        session_data = new SessionBean[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        if (cursor.getCount() > 0) {
            do {
                Log.d(CLASS_NAME, cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)) + "");
                Log.d(CLASS_NAME, cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));

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
                session_data[i] = session;
                i++;
            } while (cursor.moveToNext());
        }

        Log.d(CLASS_NAME, "Exiting initDataset");
    }

    private void addToCalender(SessionBean session, SessionBean[] sessions){
        Uri baseUri = Uri.parse("content://com.android.calendar/events");
        int index = 0;
        if (session.getCalendarEventId() == null) {
            final ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID, 1);
            event.put(CalendarContract.Events.TITLE, session.getSessionName());
            event.put(CalendarContract.Events.DESCRIPTION, session.getSessionDescription());
            event.put(CalendarContract.Events.DTSTART, session.getFromTime());
            event.put(CalendarContract.Events.DTEND, session.getToTime());
            event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
            event.put(CalendarContract.Events.HAS_ALARM, 0); // 0 for false, 1 for true

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
            Uri uri = getContentResolver().insert(baseUri, event);
            sessions[index].setCalendarEventId(uri.getLastPathSegment());
            Uri scheduleUri = IgniteContract.Schedules.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, uri.getLastPathSegment());
            values.put(IgniteContract.Schedules.EVENT_ID_FK, 1);
            values.put(IgniteContract.Schedules.SESSION_ID_FK, session.getSessionId());
            values.put(IgniteContract.Schedules.EVENT_TITLE, session.getSessionName());
            values.put(IgniteContract.Schedules.FROM_TIME, session.getFromTime());
            values.put(IgniteContract.Schedules.TO_TIME, session.getToTime());
            values.put(IgniteContract.Schedules.MANDATORY, "false");
            getContentResolver().insert(scheduleUri, values);

            //Store Session to Preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            JSONObject eventJSON = new JSONObject();
            try {
                eventJSON.put("SessionId", session.getSessionId());
                eventJSON.put("SessionName", session.getSessionName());
                eventJSON.put("SessionDescription", session.getSessionDescription());
                eventJSON.put("FromTime", session.getFromTime());
                eventJSON.put("ToTime", session.getToTime());
                eventJSON.put("CalendarEventId", uri.getLastPathSegment());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONArray sessionJSON = null;
            String prefJson = prefs.getString("sessionJSON",null);
            if(prefJson != null){
                try {
                    sessionJSON = new JSONArray(prefJson);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else{
                sessionJSON = new JSONArray();
            }
            sessionJSON.put(eventJSON);
            prefs.edit().putString("sessionJSON", sessionJSON.toString()).commit();

            // Schedule Local notification
            String desc = "Session starts in 15 minutes.";
            Date eventDate = new Date();
            eventDate.setTime(session.getFromTime());
            Date eventEndDate = new Date();
            eventEndDate.setTime(session.getToTime());
            scheduleLocalNotification(session.getSessionName(), desc, eventDate, eventEndDate, session.getSessionId());
        } else {
            Uri scheduleUri = IgniteContract.Schedules.buildSessionUri(session.getSessionId());
            getContentResolver().delete(scheduleUri, null, null);
            long eventId = Long.valueOf(session.getCalendarEventId());
            getContentResolver().delete(ContentUris.withAppendedId(baseUri, eventId), null, null);
            sessions[index].setCalendarEventId(null);

            //Remove Session Preference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            JSONArray sessionJSON = null;
            String prefJson = prefs.getString("sessionJSON", null);
            try {
                if(prefJson != null){

                    sessionJSON = new JSONArray(prefJson);
                } else{
                    sessionJSON = new JSONArray();
                }
                for (int i = 0; i < sessionJSON.length(); i++) {
                    JSONObject explrObject = sessionJSON.getJSONObject(i);
                    if(explrObject.getInt("SessionId")==session.getSessionId()){
                        sessionJSON.remove(i);
                        break;
                    }
                }
                prefs.edit().putString("sessionJSON", sessionJSON.toString()).commit();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Cancel Local Notification
            cancelLocalNotification(session.getSessionName(), session.getSessionName() + " will start in 15 minutes.", session.getSessionId());
        }
    }
    public void getSchedules(){
        try {

            IBMQuery<BluemixSchedule> query = IBMQuery.queryForClass(BluemixSchedule.class);
            // Query all the BluemixPresenter objects from the server
            query.find().continueWith(new Continuation<List<BluemixSchedule>, Void>() {
                @Override
                public Void then(Task<List<BluemixSchedule>> task) throws Exception {
                    final List<BluemixSchedule> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSchedule : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSchedule : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.Schedules.CONTENT_URI;
                        for (IBMDataObject BluemixScheduleItem : objects) {
                            try {
                                BluemixSchedule item = (BluemixSchedule) BluemixScheduleItem;
                                item.initialize();
                                LocalSchedule schedule = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Schedules.EVENT_ID_FK, schedule.getEventId());
                                values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, schedule.getCalendarEventId());
                                values.put(IgniteContract.Schedules.EVENT_TITLE, schedule.getEventTitle());
                                values.put(IgniteContract.Schedules.FROM_TIME, schedule.getFromTime().getTime());
                                values.put(IgniteContract.Schedules.TO_TIME, schedule.getToTime().getTime());
                                values.put(IgniteContract.Schedules.MANDATORY, "true");

                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a schedule : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        //Add To Schedule
                        addToSchedule(localUserSchedules.getSessions());
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    // record the fact that the app has completed first time initial set up
                    prefs.edit().putBoolean("initial_setup_complete", true).commit();
                    finish();
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixSchedule : " + error.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Login", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Login","onResume");
        if(incorrectLogin){
            Toast.makeText(LoginSiriusActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogLoading.hide();
    }
}
