package com.sirius.madness.ui.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.push.IBMPush;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;
import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.LocalPresenter;
import com.sirius.madness.ui.activities.about.AboutActivity;
import com.sirius.madness.util.AlarmReceiver;
import com.sirius.madness.util.BlueListApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public abstract class BaseActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String CLASS_NAME = BaseActivity.class.getSimpleName();
    protected Dialog dialogLoading;
    private View mMenuBar;
    private View line1,line2,line3,wrap;
    private static final String PROPS_FILE = "menu.properties";
    //Alpha Animation
    final AlphaAnimation alpha1 = new AlphaAnimation(1, 0);
    final AlphaAnimation alpha2 = new AlphaAnimation(0, 1);

    //Translate Animation
    /*float distance = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15,
            getResources().getDisplayMetrics()
    );*/
    TranslateAnimation trans1;
    TranslateAnimation trans2;

    //Rotate Animation
    final RotateAnimation rotate1 = new RotateAnimation(0, 52, Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, .0f);
    final RotateAnimation rotate2 = new RotateAnimation(52, 0, Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, .0f);
    final RotateAnimation rotate3 = new RotateAnimation(0, -52, Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, 1.0f);
    final RotateAnimation rotate4 = new RotateAnimation(-52, 0, Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, 1.0f);

    private BaseActivity currentObject;

    private ProgressDialog progress;
    private IBMPush push = null;
    private IBMPushNotificationListener notificationListener = null;
    private ArrayList<LocalPresenter> speakerList;
    protected BlueListApplication blApplication;
    private SharedPreferences prefs;
    private MenuBarAdapter menuBarAdapter;
    private int backCount;
    private ListView menuListView;
    protected String loggedInAs;

    protected boolean isFirstLevelActivity;

    public static int ch=0,cht=1;
    private int mLastFirstVisibleItem;
    String passcode;

    private static String aboutUs, shareContact, maps, sponsors, contactUs, developmentTeam, softwareUsed;
    private int menuItemCount = 4;
    int[] menuPosition;

    // symbols for menubar items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int MENU_BAR_ITEM_WELCOME = 13;
    protected static final int MENU_BAR_ITEM_DISCOVER = 0;
    protected static final int MENU_BAR_ITEM_BREAKOUT = 1;
    protected static final int MENU_BAR_ITEM_EVENTS = 2;
    protected static final int MENU_BAR_ITEM_MY_SCHEDULE = 3;
    protected static final int MENU_BAR_ITEM_SPEAKERS = 12;
    protected static final int MENU_BAR_ITEM_PARTNERS = 11;
    protected static final int MENU_BAR_ITEM_SPONSORS = 4;
    protected static final int MENU_BAR_ITEM_MAPS = 5;
    protected static final int MENU_BAR_ITEM_AGENDA = 14;
    protected static final int MENU_BAR_ITEM_KEYNOTESPEAKERS = 15;
    protected static final int MENU_BAR_ITEM_SHARE_CONTACT = 6;
    protected static final int MENU_BAR_ITEM_ABOUT = 7;
    protected static final int MENU_BAR_ITEM_REGISTER_PARTICIPANT = 8;
    protected static final int MENU_BAR_ITEM_GALLERY = 9;
    protected static final int MENU_BAR_ITEM_SOLUTIONS_GALLERY = 10;
    protected static final int MENU_BAR_ITEM_INVALID = -1;

    // titles for menubar items (indices must correspond to the above) now moved to setupMenuBar()
    /*private static final int[] MENU_BAR_TITLE_RES_ID = new int[]{
            R.string.menu_bar_discover,
            R.string.menu_bar_my_schedule,
            R.string.menu_bar_speakers,
            R.string.menu_bar_sponsors,
            R.string.menu_bar_maps,
            R.string.menu_bar_share_contact,
            R.string.menu_bar_about,
            R.string.menu_bar_regisrer_participant
    };*/

    private void readResourceProperties(){
        // Read from properties file.
        Properties props = new java.util.Properties();
        Context context = getApplicationContext();
        try {
            AssetManager assetManager = context.getAssets();
            props.load(assetManager.open(PROPS_FILE));
            Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
        } catch (FileNotFoundException e2) {
            Log.e(CLASS_NAME, "The menu.properties file was not found.", e2);
        } catch (IOException e) {
            Log.e(CLASS_NAME, "The menu.properties file could not be read properly.", e);
        }

        // Get settings from properties file
        aboutUs = (props.getProperty("aboutUs") != null) ? props.getProperty("aboutUs") : "";
        shareContact = (props.getProperty("shareContact") != null) ? props.getProperty("shareContact") : "";
        maps = (props.getProperty("maps") != null) ? props.getProperty("maps") : "";
        sponsors = (props.getProperty("sponsors") != null) ? props.getProperty("sponsors") : "";
        contactUs = (props.getProperty("contactUs") != null) ? props.getProperty("contactUs") : "";
        developmentTeam = (props.getProperty("developmentTeam") != null) ? props.getProperty("developmentTeam") : "";
        softwareUsed = (props.getProperty("softwareUsed") != null) ? props.getProperty("softwareUsed") : "";
        menuItemCount++;
        if (sponsors.equals("true")) {
            menuItemCount++;
        }
        if (maps.equals("true")) {
            menuItemCount++;
        }
        if (shareContact.equals("true")) {
            menuItemCount++;
        }
        if (aboutUs.equals("true")||contactUs.equals("true")||developmentTeam.equals("true")||softwareUsed.equals("true")) {
            menuItemCount++;
        }
    }

    PopupWindow mPopupMenuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "Entering onCreate");

        super.onCreate(savedInstanceState);

        backCount = 0;
        dialogLoading = new Dialog(this, R.style.Theme_Dialog);
        dialogLoading.setContentView(R.layout.dialog_loading);
        /*progress = new ProgressDialog(this);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setTitle("Loading...");
        progress.setMessage("Fetching data from BlueMix...");*/

        //Alpha Animation
        alpha1.setDuration(100);
        alpha1.setFillEnabled(true);
        alpha1.setFillAfter(true);
        alpha2.setDuration(1000);
        alpha2.setFillEnabled(true);
        alpha2.setFillAfter(true);
        //Translate Animation
        float distance = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, Float.valueOf("7.5"),
                getResources().getDisplayMetrics()
        );
        trans1 = new TranslateAnimation(.0f, distance, .0f, .0f);
        trans2 = new TranslateAnimation(distance, .0f, .0f, .0f);
        trans1.setDuration(500);
        trans1.setFillEnabled(true);
        trans1.setFillAfter(true);
        trans2.setDuration(500);
        trans2.setFillEnabled(true);
        trans2.setFillAfter(true);
        //Rotate Animation
        rotate1.setDuration(500);
        rotate1.setFillEnabled(true);
        rotate1.setFillAfter(true);
        rotate2.setDuration(500);
        rotate2.setFillEnabled(true);
        rotate2.setFillAfter(true);
        rotate3.setDuration(500);
        rotate3.setFillEnabled(true);
        rotate3.setFillAfter(true);
        rotate4.setDuration(500);
        rotate4.setFillEnabled(true);
        rotate4.setFillAfter(true);

        currentObject = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        passcode = prefs.getString("passcode", null);
        //Check if Logged In As Partner
        loggedInAs = prefs.getString("participantUName",null);
        if(loggedInAs!=null){
            if(loggedInAs.equalsIgnoreCase("partner")||loggedInAs.equalsIgnoreCase(" partner")) {
                menuItemCount = 0;
            }
        }

        /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();

        readResourceProperties();

        ActionBar actionBar = getSupportActionBar();
        String title = actionBar.getTitle().toString();
        Log.d("Title",title);
        SpannableString barTitle = new SpannableString(title);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.otf");
        barTitle.setSpan(tf, 0, title.length(), Spanned.SPAN_COMPOSING);
        actionBar.setTitle(barTitle);
        actionBar.openOptionsMenu();
        Log.d(CLASS_NAME, "Exiting onCreate");
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "Entering onPostCreate");

        super.onPostCreate(savedInstanceState);
        setupMenuBar();
        setupActionBar();

        Log.d(CLASS_NAME, "Exiting onPostCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(CLASS_NAME, "Entering onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_base, menu);

        Log.d(CLASS_NAME, "Exiting onCreateOptionsMenu");

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(CLASS_NAME, "Entering onPrepareOptionsMenu pass"+passcode);

        /*if(null == passcode) {
            menu.findItem(R.id.action_settings).setVisible(true);
        }else{
            menu.findItem(R.id.action_settings).setVisible(false);
        }*/
        Log.d(CLASS_NAME, "Exiting onPrepareOptionsMenu");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(CLASS_NAME, "Entering onOptionsItemSelected");

        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/

        Log.d(CLASS_NAME, "Exiting onOptionsItemSelected");

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(CLASS_NAME, "Entering onResume");
        super.onResume();
        if(menuBarAdapter != null) {
            menuBarAdapter.notifyDataSetChanged();
        }
        backCount=0;
        Log.d(CLASS_NAME, "Exiting onResume");
    }

    @Override
    protected void onPause() {
        Log.d(CLASS_NAME, "Entering onPause");
        super.onPause();
        Log.d(CLASS_NAME, "Exiting onPause");
    }

    @Override
    protected void onStop() {
        Log.d(CLASS_NAME, "Entering onStop");
        super.onStop();
        //Hide the popup menu if its shown
        hideMenuBar();

        Log.d(CLASS_NAME, "Exiting onStop");
    }

    @Override
    public void onClick(final View v) {
        Log.d(CLASS_NAME, "Entering onClick");

        //Onclick for Menu Bar
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        //ImageButton img = (ImageButton)mMenuBar;
        //img.setBackgroundResource(R.drawable.menu_close1);
        lineToCross();

        if (v.getId() == R.id.MenuAnimate) {
            v.post(new Runnable() {
                @Override
                public void run() {
                    showMenuBar(v);
                }
            });
        }
        //v.setElevation(Float.valueOf("2"));
        v.bringToFront();
        v.invalidate();
        //ViewCompat.setTranslationZ(v, Float.valueOf("10.0"));

        Log.d(CLASS_NAME, "Exiting onClick");
    }

    public void lineToCross(){
        line1.startAnimation(rotate1);
        line2.startAnimation(alpha1);
        line3.startAnimation(rotate3);
        wrap.startAnimation(trans1);
    }
    public void crossToLine(){
        line1.startAnimation(rotate2);
        line2.startAnimation(alpha2);
        line3.startAnimation(rotate4);
        wrap.startAnimation(trans2);
    }

    public void openAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("\t\t\t\tThere is an Update\n\nPlease note, App will be restarted");

        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(BaseActivity.this, "Update clicked", Toast.LENGTH_SHORT).show();
                removeScheduled();
                Intent intent = new Intent(BaseActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Contains the logic for navigating to correct view after receiving a push
     * notification.
     * @param message
     */
    public void handleReceivedPushNotification(IBMSimplePushNotification message){
        Log.d(CLASS_NAME, "Entering handleReceivedPushNotification");

        // Parse received payload
        String alertMsg = message.getAlert();
        String payload = message.getPayload();
        JSONObject payloadJson = null;
        String destinationView = null;

        Log.d(CLASS_NAME, "Notification message received: " + message.toString());
        // Present the message when sent from Push notification console.
        if(message.getAlert().contains("The Ignite Backend has been updated.")){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            settings.edit().putBoolean("update_complete", false).apply();
            openAlert();
        }else {
            // Get payload JSON from payload String
            try {
                payloadJson = new JSONObject(payload);
                destinationView = (payloadJson != null) ? payloadJson.getString("view") : "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       if(destinationView != null) {
           // Distinguish where app should route to after received notification
           Intent intent;
           // TODO - parse incoming message to distinguish which view should be shown
           // Currently defaults to Discover Activity when opened from push_notifications_icon notification
           if (destinationView.equalsIgnoreCase("Discover")) {
               intent = new Intent(this, DiscoverActivity.class);
               startActivity(intent);
               finish();
               return;
           } else if (destinationView.equalsIgnoreCase("Schedule")) {
               intent = new Intent(this, MyScheduleActivity.class);
               startActivity(intent);
               finish();
               return;
           } else if (destinationView.equalsIgnoreCase("Solution")) {
               intent = new Intent(this, SolutionsGalleryActivity.class);
               startActivity(intent);
               finish();
               return;
           } else if (destinationView.equalsIgnoreCase("Partner")) {
               intent = new Intent(this, GalleryActivity.class);
               startActivity(intent);
               finish();
               return;
           } else if (destinationView.equalsIgnoreCase("Sponsor")) {
               intent = new Intent(this,SponsorsActivity.class);
               startActivity(intent);
               finish();
               return;
           } else if (destinationView.equalsIgnoreCase("Speaker")) {
               intent = new Intent(this, SpeakerFeedActivity.class);
               intent.putParcelableArrayListExtra("speakerList", speakerList);
               startActivity(intent);
               finish();
               return;
           }else if (destinationView.equalsIgnoreCase("Map")) {
               intent = new Intent(this, BoothMapActivity.class);
               startActivity(intent);
               finish();
               return;
           } else {
               intent = new Intent(this, DiscoverActivity.class);
               startActivity(intent);
               finish();
               return;
           }
       }

        Log.d(CLASS_NAME, "Exiting handleReceivedPushNotification");
    }

    private void removeScheduled(){
        Uri baseUri = Uri.parse("content://com.android.calendar/events");
        //Session Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);;
        JSONArray sessionJSON;
        String prefJson = prefs.getString("sessionJSON",null);
        if(prefJson != null){
            try {
                sessionJSON = new JSONArray(prefJson);
                for (int i = 0; i < sessionJSON.length(); i++) {
                    JSONObject explrObject = sessionJSON.getJSONObject(i);
                    if(explrObject.getInt("SessionId") != 0){

                        Uri scheduleUri = IgniteContract.Schedules.buildSessionUri(explrObject.getInt("SessionId"));
                        this.getContentResolver().delete(scheduleUri, null, null);
                        long eventId = Long.valueOf(explrObject.getString("CalendarEventId"));
                        this.getContentResolver().delete(ContentUris.withAppendedId(baseUri, eventId), null, null);

                        // Cancel Local Notification
                        ((BaseActivity) this).cancelLocalNotification(explrObject.getString("SessionName"), explrObject.getString("SessionName") + " will start in 15 minutes.", explrObject.getInt("SessionId"));
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showProgressBar() {
        Log.d(CLASS_NAME, "Entering showProgressBar");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.show();
            }
        });
        Log.d(CLASS_NAME, "Exiting showProgressBar");
    }

    public void hideProgressBar(){
        Log.d(CLASS_NAME, "Entering hideProgressBar");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.hide();
            }
        });
        Log.d(CLASS_NAME, "Exiting hideProgressBar");
    }
    
    public void refreshUI(){

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(CLASS_NAME, "Entering onItemClick");

        Log.d("On Item CLicked", parent.getClass().toString()+"");
        if(menuPosition[position] == getSelfMenuBarItem()){
            //already in the correct screen
            hideMenuBar();
            return;
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogLoading.show();
                }
            });
        }
        Log.d("menuPosition/position",menuPosition[position]+"/"+position);
        goToMenuBarItem(menuPosition[position]);

        Log.d(CLASS_NAME, "Exiting onItemClick");
    }

    private void goToMenuBarItem(int menuBarItem){
        Log.d(CLASS_NAME, "Entering goToMenuBarItem");

        Intent intent;
        switch (menuBarItem) {
            case MENU_BAR_ITEM_WELCOME:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_welcome), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_DISCOVER:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_discover), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DiscoverActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_BREAKOUT:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_breakout), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, BreakoutActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_AGENDA:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_agenda), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AgendaActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_KEYNOTESPEAKERS:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_keynotespeakers), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, KeynoteSpeakersActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_EVENTS:
                intent = new Intent(this, EventsActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_MY_SCHEDULE:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_my_schedule), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MyScheduleActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_SPEAKERS:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_speakers), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, SpeakerFeedActivity.class);
                intent.putParcelableArrayListExtra("speakerList", speakerList);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_ABOUT:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_about), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_PARTNERS:
               // Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_partners), Toast.LENGTH_SHORT).show();
                intent = new Intent(this,PartnersActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_SPONSORS:
                // Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_sponsors), Toast.LENGTH_SHORT).show();
                intent = new Intent(this,SponsorsActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_MAPS:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_maps), Toast.LENGTH_SHORT).show();
                intent = new Intent(this,MapsActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_REGISTER_PARTICIPANT:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_qrscan), Toast.LENGTH_SHORT).show();
                intent = new Intent(this,RegisterParticipantActivity.class);
                startActivity(intent);
                finish();
                return;
            case MENU_BAR_ITEM_SHARE_CONTACT:
                //Toast.makeText(this, "Item Clicked: " + getString(R.string.menu_bar_qrscan), Toast.LENGTH_SHORT).show();
                intent = new Intent(this,ShareContactActivity.class);
                startActivity(intent);
                finish();
                return;
        }

        Log.d(CLASS_NAME, "Exiting goToMenuBarItem");
    }

    /**
     * Returns the Menu Bar item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what Menu bar item corresponds to them
     * Return MENU_BAR_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfMenuBarItem() {
        return MENU_BAR_ITEM_INVALID;
    }

    public void setupActionBar() {
        Log.d(CLASS_NAME, "Entering setupActionBar");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_custom_actionbar);

        //Set up Action Bar title
        try {
            String titleText = this.getString(getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
            TextView titleBar = (TextView) this.findViewById(R.id.mytext);
            titleBar.setText(titleText);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(CLASS_NAME, "Exiting setupActionBar");
    }

    public void setupMenuBar() {
        Log.d(CLASS_NAME, "Entering setupMenuBar");

        // titles for menubar items (indices must correspond to the above)
        final int[] MENU_BAR_TITLE_RES_ID = new int[menuItemCount];
        Log.d(CLASS_NAME+"count: ",menuItemCount+"");

        menuPosition = new int[menuItemCount];

        int count = 0;
        if(loggedInAs!=null) {
            if (!loggedInAs.equalsIgnoreCase("partner") && !loggedInAs.equalsIgnoreCase(" partner")) {
                Log.d("Inside", loggedInAs);
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_welcome;
                menuPosition[count] = MENU_BAR_ITEM_WELCOME;
                count++;
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_discover;
                menuPosition[count] = MENU_BAR_ITEM_DISCOVER;
                count++;
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_keynotespeakers;
                menuPosition[count] = MENU_BAR_ITEM_KEYNOTESPEAKERS;
                count++;
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_agenda;
                menuPosition[count] = MENU_BAR_ITEM_AGENDA;
                count++;
                /*MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_breakout;
                menuPosition[count] = MENU_BAR_ITEM_BREAKOUT;
                count++;
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_events;
                menuPosition[count] = MENU_BAR_ITEM_EVENTS;
                count++;
                MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_my_schedule;
                menuPosition[count] = MENU_BAR_ITEM_MY_SCHEDULE;
                count++;*/
            }
        }
        /*MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_speakers;
        menuPosition[count] = MENU_BAR_ITEM_SPEAKERS;
        count++;*/
        if (sponsors.equals("true")) {
            MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_sponsors;
            menuPosition[count] = MENU_BAR_ITEM_SPONSORS;
            count++;
        }
        if (maps.equals("true")) {
            MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_maps;
            menuPosition[count] = MENU_BAR_ITEM_MAPS;
            count++;
        }
        if (shareContact.equals("true")) {
            MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_share_contact;
            menuPosition[count] = MENU_BAR_ITEM_SHARE_CONTACT;
            count++;
        }
        if (aboutUs.equals("true")||contactUs.equals("true")||developmentTeam.equals("true")||softwareUsed.equals("true")) {
            MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_about;
            menuPosition[count] = MENU_BAR_ITEM_ABOUT;
            count++;
        }
        MENU_BAR_TITLE_RES_ID[count] = R.string.menu_bar_regisrer_participant;
        menuPosition[count] = MENU_BAR_ITEM_REGISTER_PARTICIPANT;


        mMenuBar = findViewById(R.id.MenuAnimate);

        line1 = findViewById(R.id.menu_line1);
        line2 = findViewById(R.id.menu_line2);
        line3 = findViewById(R.id.menu_line3);
        wrap = findViewById(R.id.menu_wrap);

        if (mMenuBar == null) {
            return;
        }

        int selfItem = getSelfMenuBarItem();

        if (selfItem == MENU_BAR_ITEM_INVALID) {
            // do not show a menu bar
            ((ViewGroup) mMenuBar.getParent()).removeView(mMenuBar);
            return;
        }

        mPopupMenuBar = new PopupWindow(this);

        // the drop down list is a list view
        menuListView = new ListView(this);
        menuListView.setPadding(16, 0, 16, 0);
        menuListView.setBackgroundColor(getResources().getColor(R.color.theme_primary_black));
        menuListView.setDivider(new ColorDrawable(getResources().getColor(R.color.white)));
        menuListView.setDividerHeight(1);


        menuListView.setSelector(new ColorDrawable(getResources().getColor(R.color.theme_secondary_blue)));
        menuListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);



        /*
        menuListView.setOnScrollListener(new ListView.OnScrollListener(){
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mLastFirstVisibleItem < firstVisibleItem) {
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                }

                if (mLastFirstVisibleItem > firstVisibleItem) {
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        getActionBar().show();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        getActionBar().hide();
                        break;
                }
            }
        });
        */

        // set our adapter and pass our pop up window contents
        menuBarAdapter = new MenuBarAdapter(this, R.layout.menu_bar_item, MENU_BAR_TITLE_RES_ID);
        menuListView.setAdapter(menuBarAdapter);

        // set the item click listener
        menuListView.setOnItemClickListener(this);

        // some other visual settings
        mPopupMenuBar.setFocusable(true);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                mPopupMenuBar.setWidth(800);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                mPopupMenuBar.setWidth(600);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                mPopupMenuBar.setWidth(500);
                break;
            default:
                mPopupMenuBar.setWidth(500);
        }

        mPopupMenuBar.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


        // set the list view as pop up window content
        mPopupMenuBar.setContentView(menuListView);

        mPopupMenuBar.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //ImageButton img = (ImageButton)mMenuBar;
                //img.setBackgroundResource(R.drawable.menu_open1);
                crossToLine();
            }
        });

        mMenuBar.setOnClickListener(this);
        mMenuBar.bringToFront();
        ((ViewGroup) mMenuBar.getParent()).requestLayout();
        ((ViewGroup) mMenuBar.getParent()).invalidate();

        Log.d(CLASS_NAME, "Exiting setupMenuBar");
    }


    private void showMenuBar(View view) {
        Log.d(CLASS_NAME, "Entering showMenuBar");

        // Finally show the PopupMenu
        mPopupMenuBar.showAsDropDown(view, -5, 0);


        menuListView.setItemChecked(getSelfMenuBarItem(),true);

        Log.d(CLASS_NAME, "Exiting showMenuBar");
    }

    private void hideMenuBar(){
        Log.d(CLASS_NAME, "Entering hideMenuBar");

        View img = (View)mMenuBar;
        if(img!=null){
        //img.setBackgroundResource(R.drawable.menu_open1);
            crossToLine();
        }

        if(mPopupMenuBar!=null && mPopupMenuBar.isShowing()){
            mPopupMenuBar.dismiss();
        }

        Log.d(CLASS_NAME, "Exiting hideMenuBar");
    }


    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Log.d(CLASS_NAME, "Entering intentToFragmentArguments");

        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        Log.d(CLASS_NAME, "Exiting intentToFragmentArguments");

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Log.d(CLASS_NAME, "Entering fragmentArgumentsToIntent");

        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");

        Log.d(CLASS_NAME, "Exiting fragmentArgumentsToIntent");

        return intent;
    }

    class MenuBarAdapter extends ArrayAdapter<String> {

        LayoutInflater inflater;
        Context context;
        int[] stringIds;
        int selfMenuBarItem;

        class ViewHolder {
            TextView title;
        }

        public MenuBarAdapter(Context context, int resource, int[] stringIds) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.stringIds = stringIds;
            this.selfMenuBarItem = getSelfMenuBarItem();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.menu_bar_item, null);

                ViewHolder holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.tvMenuBarTitle);
                convertView.setTag(holder);

            }

            viewHolder = (ViewHolder) convertView.getTag();


            if (viewHolder.title != null) {
                viewHolder.title.setText(getItem(position));
            }

            if(selfMenuBarItem==menuPosition[position]) {
                convertView.setBackgroundColor(getResources().getColor(R.color.theme_secondary_blue));
            }else{
                convertView.setBackgroundColor(getResources().getColor(R.color.theme_primary_black));
            }
            //TODO
            convertView.setMinimumWidth(600);
            return convertView;
        }

        @Override
        public int getCount() {

            passcode = prefs.getString("passcode", null);
            if(passcode != null){
                return stringIds.length;
            } else {
                return stringIds.length - 1;
            }

        }

        @Override
        public String getItem(int position) {
            return context.getString(stringIds[position]);
        }

    }

    /*
    Closes current activity and takes you back to previous.
     */
    @Override
    public void onBackPressed() {
        if(isFirstLevelActivity) {
            if (backCount == 0) {
                backCount++;
                Toast.makeText(BaseActivity.this, "Press Back again to Quit", Toast.LENGTH_SHORT).show();
            } else {
                backCount--;
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void onBackPressed(View v) { this.onBackPressed(); }

    /**
     * This method schedules a local notification for the date.
     * @param title - Title to display for notification
     * @param desc - Description of notification
     * @param date - date for when the event should fire
     * @param mId - notification identifier
     */
    public void scheduleLocalNotification(String title, String desc, Date date, Date endDate, Integer mId) {
        Log.d(CLASS_NAME, "Entering scheduleLocalNotification");

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("desc", desc);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("mId", mId);
        Log.d("Desc/Title: ", desc + title);
        Intent alarmIntentSurvey = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("desc", "Survey For the Session");
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("mId", mId);
        alarmIntent.putExtra("flag", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentSurvey = PendingIntent.getBroadcast(this, 100, alarmIntentSurvey, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(getBaseContext().ALARM_SERVICE);

        /*Date eventDate = new Date();
        long t=eventDate.getTime();
        SimpleDateFormat fromTime = new SimpleDateFormat("h:mm a");
        Date afterAddingTwoMins=new Date(t + (1 * 60000));
        Log.d("Time to Alert:",fromTime.format(afterAddingTwoMins));
        eventDate.setTime(t + (1 * 60000));*/

        // Convert time to ms, (timeInterval x 60s/m) x 1000ms/s
        Long atTime = (15 * 60) * 1000L;
        atTime = date.getTime() - atTime;


        Long endTime = (5 * 60) * 1000L;
        endTime = endDate.getTime() - endTime;

        /*Date eDate = new Date();
        long tTime = (1 * 60) * 1000L;
        tTime = eDate.getTime() + tTime;*/

        Date eventDate = new Date();
        if(date.getTime() > eventDate.getTime()) {
            // Set Alarm for 15 min before event date
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, atTime, pendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, endTime, pendingIntentSurvey);
                SimpleDateFormat fromTime = new SimpleDateFormat("h:mm:ss a dd/MM/yy");
                Log.d("Time to Alert Exact:", fromTime.format(atTime));
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, atTime, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endTime, pendingIntentSurvey);
                SimpleDateFormat fromTime = new SimpleDateFormat("h:mm:ss a dd/MM/yy");
                Log.d("Time to Alert:", fromTime.format(atTime));
            }
        }else{
            Toast.makeText(BaseActivity.this, "Invalid Date, Please select a future event", Toast.LENGTH_SHORT).show();
        }
        Log.d(CLASS_NAME, "Exiting scheduleLocalNotification");
    }

    /**
     * This method cancels the notification with id mId.
     * @param mId - notification identifier
     */
    public void cancelLocalNotification(String title, String desc, Integer mId){
        Log.d(CLASS_NAME, "Entering cancelLocalNotification");

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(getBaseContext().ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("desc", desc);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("mId", mId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent alarmIntentSurvey = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("desc", desc);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("mId", mId);
        alarmIntent.putExtra("flag", true);
        PendingIntent pendingIntentSurvey = PendingIntent.getBroadcast(this, 0, alarmIntentSurvey, PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel Alarm
        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntentSurvey);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mId);

        Log.d(CLASS_NAME, "Exiting cancelLocalNotification");
    }
    private void goToMySchedule(){
        Intent intent = new Intent(this, MyScheduleActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogLoading.hide();
    }
}