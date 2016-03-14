package com.sirius.madness.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.ParticipantBean;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.beans.SpeakerBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.BluemixSurveyAnswers;
import com.sirius.madness.ui.adapters.SpeakerListAdapter;
import com.google.gson.Gson;
import com.ibm.mobile.services.data.IBMQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class SessionDetailActivity extends BaseActivity {


    private SpeakerListAdapter mSpeakersAdapter;
    private RecyclerView mSpeakerListRecyclerView;
    private LinearLayoutManager mSpeakerLayoutManager;
    private Typeface iconFont;
    private String filledStar;
    private String emptyStar;
    private SessionBean session;
    private int sessionId;
    Button provideFeedback;
    boolean feedbackVisible = false;
    private SharedPreferences prefs;
    private static final int LOGIN_QR_REQUEST = 3;
    private static final int ATTEND_SURVEY = 4;

    protected List<SpeakerBean> speakers;
    protected String speakerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);
        speakers = new ArrayList<SpeakerBean>();
        iconFont = Typeface.createFromAsset(this.getAssets(), "fonts/customIconFont.ttf");
        filledStar = getResources().getString(R.string.custom_font_icon_star_filled);
        emptyStar = getResources().getString(R.string.custom_font_icon_star_empty);

        session = new SessionBean();
        sessionId = getIntent().getIntExtra("Session Id",1);
        session.setSessionId(sessionId);

        TextView sessionTitle = (TextView) findViewById(R.id.sessionDetailTitle);
        TextView sessionDescription = (TextView) findViewById(R.id.sessionDescription);

        //get session speakers
        String [] presenterProjectionList= {
                IgniteContract.Presenters.PRESENTER_ID,
                IgniteContract.Presenters.PRESENTER_FIRST_NAME,
                IgniteContract.Presenters.PRESENTER_LAST_NAME,
                IgniteContract.Presenters.PRESENTER_IMAGE,
                IgniteContract.ImageBinary.IMAGE_DATA,
                IgniteContract.ImageBinary.IMAGE_ID
        };
        Cursor presentersCursor = getContentResolver().query(IgniteContract.SessionsPresenters.buildSessionPresentersUri(sessionId),presenterProjectionList,null,null, null);
        initDataSet(presentersCursor);

        //get Session
        String [] sessionProjectionList= {
                IgniteContract.Sessions.SESSION_ID,
                IgniteContract.Sessions.SESSION_LONG_TITLE,
                IgniteContract.Sessions.SESSION_LONG_DESCRIPTION,
                IgniteContract.Sessions.SESSION_FROM_TIME,
                IgniteContract.Sessions.SESSION_TO_TIME,
                IgniteContract.Sessions.SESSION_HALL,
                IgniteContract.Schedules.CALENDAR_EVENT_ID,
        };
        Cursor sessionCursor = getContentResolver().query(IgniteContract.Sessions.buildSessionUri(sessionId),sessionProjectionList,null,null,null);
        sessionCursor.moveToFirst();
        session.setSessionName(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
        session.setSessionDescription(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION)));
        session.setFromTime(sessionCursor.getLong(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
        session.setToTime(sessionCursor.getLong(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_TO_TIME)));
        session.setCalendarEventId(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Schedules.CALENDAR_EVENT_ID)));
        session.setSessionHall(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_HALL)));

        sessionTitle.setText(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
        sessionDescription.setText(sessionCursor.getString(sessionCursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION)));
        sessionDescription.setMovementMethod(new ScrollingMovementMethod());
        /*mSpeakerListRecyclerView = (RecyclerView) findViewById(R.id.rvSpeakerList);
        mSpeakersAdapter = new SpeakerListAdapter(this,speakers);
        mSpeakerLayoutManager = new LinearLayoutManager(this);
        mSpeakerLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSpeakerListRecyclerView.setAdapter(mSpeakersAdapter);
        mSpeakerListRecyclerView.setLayoutManager(mSpeakerLayoutManager);*/

        TextView speakersTextView = (TextView) findViewById(R.id.speakers_text);
        speakersTextView.setText(speakerText);

        //Button addToCalendar = (Button) findViewById(R.id.addToCalendarButton);
        Button shareButton = (Button) findViewById(R.id.shareButton);
        provideFeedback = (Button) findViewById(R.id.provideFeedback);

        TextView sessionDate = (TextView) findViewById(R.id.sessionDate);
        TextView sessionTime = (TextView) findViewById(R.id.sessionTime);
        TextView sessionLocation = (TextView) findViewById(R.id.sessionLocation);
        LinearLayout locationWrap = (LinearLayout) findViewById(R.id.locationWrapper);

        Date fromdate = new Date(session.getFromTime());
        Date todate = new Date(session.getToTime());
        SimpleDateFormat dateFormat = generateFormat(fromdate);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

        sessionDate.setText(dateFormat.format(fromdate));
        sessionTime.setText(timeFormat.format(fromdate)+" - "+timeFormat.format(todate));
        if(!session.getSessionHall().equals("")){
            sessionLocation.setText(session.getSessionHall());
        }else{
            locationWrap.setEnabled(false);
            locationWrap.setVisibility(View.GONE);
        }

        isSessionEnded(session.getToTime());

        /*addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if(textView.getText().toString().equalsIgnoreCase(emptyStar)){
                    textView.setText(filledStar);
                }else{
                    textView.setText(emptyStar);
                }
                Uri baseUri = Uri.parse("content://com.android.calendar/events");
                Button addButton = (Button) v;
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
                    //sessions[index].setCalendarEventId(uri.getLastPathSegment());
                    Uri scheduleUri = IgniteContract.Schedules.CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, uri.getLastPathSegment());
                    values.put(IgniteContract.Schedules.EVENT_ID_FK, 1);
                    values.put(IgniteContract.Schedules.SESSION_ID_FK, sessionId);
                    values.put(IgniteContract.Schedules.EVENT_TITLE, session.getSessionName());
                    values.put(IgniteContract.Schedules.FROM_TIME, session.getFromTime());
                    values.put(IgniteContract.Schedules.TO_TIME, session.getToTime());
                    values.put(IgniteContract.Schedules.MANDATORY, "false");
                    getContentResolver().insert(scheduleUri, values);

                    //Store Session to Preferences
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SessionDetailActivity.this);
                    JSONObject eventJSON = new JSONObject();
                    try {
                        eventJSON.put("SessionId", sessionId);
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

                    // Schedule Local Notification
                    String desc = session.getSessionName() + " will start in 15 minutes.";
                    Date eventDate = new Date();
                    eventDate.setTime(session.getFromTime());
                    Date eventEndDate = new Date();
                    eventEndDate.setTime(session.getToTime());
                    scheduleLocalNotification(session.getSessionName(), desc, eventDate, eventEndDate, sessionId);

                } else {
                    Uri scheduleUri = IgniteContract.Schedules.buildSessionUri(sessionId);
                    getContentResolver().delete(scheduleUri, null, null);
                    long eventId = Long.valueOf(session.getCalendarEventId());
                    getContentResolver().delete(ContentUris.withAppendedId(baseUri, eventId), null, null);
                    // sessions[index].setCalendarEventId(null);

                    //Remove Session Preference
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SessionDetailActivity.this);
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
                            if(explrObject.getInt("SessionId")==sessionId){
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
                    cancelLocalNotification(session.getSessionName(), session.getSessionName() + " will start in 15 minutes.", sessionId);

                }
            }
        });*/

        // Set OnClick event for share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Share Intent on click
                String sessionTitle = session.getSessionName();
                String sessionDescription = session.getSessionDescription();
                createShareIntent(v, sessionDescription, sessionTitle);
            }
        });

        // Set OnClick event for feedback button
        provideFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String participantId = prefs.getString("participantId", null);
                if(participantId==null){
                    Intent intent = new Intent(SessionDetailActivity.this, QRScannerActivity.class);
                    intent.putExtra("RegisterParticipant", false);
                    intent.putExtra("toast", "Please Scan Your ID to Login");
                    startActivityForResult(intent, LOGIN_QR_REQUEST);
                }else{
                    // Create Survey Intent on click
                    Intent intent = new Intent(SessionDetailActivity.this,SurveyActivity.class);
                    intent.putExtra("sessionId", String.valueOf(session.getSessionId()));
                    intent.putExtra("sessionName",session.getSessionName());
                    startActivityForResult(intent, ATTEND_SURVEY);
                }
            }
        });

        /*if(session.getCalendarEventId() == null){
            addToCalendar.setText(emptyStar);
        }else{
            addToCalendar.setText(filledStar);
        }*/

        // Set Icon Fonts
        shareButton.setTypeface(iconFont);
        //addToCalendar.setTypeface(iconFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_QR_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra("ScanResult");
                try {
                    Gson object = new Gson();
                    ParticipantBean bean = object.fromJson(scanResult, ParticipantBean.class);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("participantId", bean.getEmpId());
                    editor.putString("participantName", bean.getFirstName()+" "+bean.getLastName());
                    editor.putString("participantNumber", bean.getPhone());
                    editor.putString("participantEmail", bean.getMailId());
                    editor.commit();

                    checkAndStartSurvey();

                    Toast.makeText(SessionDetailActivity.this, "Welcome " + bean.getFirstName(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("JsonParsingException : ",e.getMessage());
                }
            }else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(SessionDetailActivity.this, "QR-Code is invalid", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == ATTEND_SURVEY){
            if (resultCode == RESULT_OK) {
                Log.d("SurveyBTN", "Gone activityresult");
                provideFeedback.setVisibility(View.GONE);
            }
        }
    }

    private void checkAndStartSurvey(){
        prefs = PreferenceManager.getDefaultSharedPreferences(SessionDetailActivity.this);
        String participantId = prefs.getString("participantId", null);
        if(participantId!=null) {

            IBMQuery<BluemixSurveyAnswers> query = IBMQuery.queryForClass("SurveyAnswers");
            // Query all the BluemixParticipant objects from the server

            query.whereKeyEqualsTo("sessionId", sessionId);
            query.whereKeyEqualsTo("participantId", participantId);
            query.find().onSuccess(new Continuation<List<BluemixSurveyAnswers>, Void>() {
                @Override
                public Void then(Task<List<BluemixSurveyAnswers>> task) throws Exception {
                    final List<BluemixSurveyAnswers> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e("SurveyCheck", "Exception in BluemixSurveyAnswers : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e("SurveyCheck", "Exception in BluemixSurveyAnswers : " + task.getError().getMessage());
                    } else {
                        if (objects.size() != 0) {
                            Toast.makeText(SessionDetailActivity.this, "Survey Already Attended", Toast.LENGTH_SHORT).show();
                            Log.d("SurveyBTN", "Gone check and start");
                            provideFeedback.setVisibility(View.GONE);
                        } else {
                            Intent intent = new Intent(SessionDetailActivity.this, SurveyActivity.class);
                            intent.putExtra("sessionId", String.valueOf(session.getSessionId()));
                            intent.putExtra("sessionName", session.getSessionName());
                            startActivity(intent);
                        }
                    }
                    return null;
                }
            });
        }
    }

    private void isSessionEnded(long endDate){
        Date currentDate = new Date();
        if(endDate < currentDate.getTime()) {
            isSurveyCompleted();
        }
    }

    public void isSurveyCompleted(){
        Log.d("Survey","isSurveyCompleted()");
                prefs = PreferenceManager.getDefaultSharedPreferences(SessionDetailActivity.this);
                final String participantId = prefs.getString("participantId", null);
                if (participantId != null) {

                    IBMQuery<BluemixSurveyAnswers> query = IBMQuery.queryForClass("SurveyAnswers");
                    // Query all the BluemixParticipant objects from the server

                    query.whereKeyEqualsTo("sessionId", String.valueOf(sessionId));
                    query.whereKeyEqualsTo("participantId", participantId);
                    query.find().onSuccess(new Continuation<List<BluemixSurveyAnswers>, Void>() {
                        @Override
                        public Void then(Task<List<BluemixSurveyAnswers>> task) throws Exception {
                            final List<BluemixSurveyAnswers> objects = task.getResult();

                            if (task.isCancelled()) {
                                Log.e("SurveyCheck", "Exception in BluemixSurveyAnswers : Task " + task.toString() + " was cancelled.");
                            } else if (task.isFaulted()) {
                                Log.e("SurveyCheck", "Exception in BluemixSurveyAnswers : " + task.getError().getMessage());
                            } else {
                                if (objects.size() != 0) {
                                    Toast.makeText(SessionDetailActivity.this, "Survey Already Attended", Toast.LENGTH_SHORT).show();
                                    Log.d("SurveyBTN", "Gone");
                                    provideFeedback.setVisibility(View.GONE);
                                } else {
                                    Log.d("SurveyBTN", "Visible");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("surveyCompleted", true);
                                    editor.commit();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            provideFeedback.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                            return null;
                        }
                    });
                } else {
                    Log.d("SurveyBTN", "Visible out");
                    provideFeedback.setVisibility(View.VISIBLE);
                }

    }

    @Override
    protected void onResume() {
        Log.d("SessionDetailActivity", "onResume");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private SimpleDateFormat generateFormat(Date datetime){
        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(datetime);

        if(date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("MMM d'st'");
        else if(date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("MMM d'nd'");
        else if(date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("MMM d'rd'");
        else
            format = new SimpleDateFormat("MMM d'th'");
        return format;
    }

    private void initDataSet(Cursor cursor) {
        cursor.moveToFirst();
        speakerText = "";
        if(cursor.getCount() > 0){
            do{
                SpeakerBean speaker = new SpeakerBean();
                speaker.setSpeakerId(cursor.getInt(0));
                speaker.setFirstName(cursor.getString(1));
                speaker.setLastName(cursor.getString(2));
                speaker.setImage(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                int id = cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_ID));
                speakers.add(speaker);
                if(cursor.isLast()){
                    speakerText += cursor.getString(1)+" "+cursor.getString(2);
                }else {
                    speakerText += cursor.getString(1) + " " + cursor.getString(2) + ",";
                }
            }while (cursor.moveToNext());
        }
    }

    /**
     * Creates a share intent to share the session summary
     * @param v - current view
     * @param shareBody - session summary
     * @param shareSubject - session title
     */
    private void createShareIntent(View v, String shareBody, String shareSubject){
        Resources resources = getResources();
        int twitterCharCount = 140;

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    String twitBody = (shareBody.length() > twitterCharCount) ? shareBody.substring(0,twitterCharCount) : shareBody;
                    intent.putExtra(Intent.EXTRA_TEXT, twitBody);
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialogLoading.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogLoading.hide();
        dialogLoading.dismiss();
    }
}
