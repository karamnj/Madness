package com.sirius.madness.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.DiscoverAdapter;
import com.sirius.madness.util.DiscoverSingleton;
import com.sirius.madness.util.SocialMediaLoginMethods;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DiscoverActivity extends BaseActivity {

    private static final String CLASS_NAME = "DiscoverActivity";

    private static final String DISCOVER_UPDATES = "Discover Updates";
    private DiscoverAdapter discoverAdapter;
    private SessionBean session_data[];
    private ArrayList<SessionBean> sessions;
    private ListView discoverListView;
    SharedPreferences mPrefs;
    Parcelable state;
    private View filterBar;
    private TextView filterBarValue;
    private boolean flag;
    private ListView filterListView;
    private int filterItemCount = 0;
    int[] filterPosition;
    int selectedFilter;
    private PopupWindow mPopupFilterBar;
    private FilterBarAdapter filterBarAdapter;

    protected static final int FILTER_BAR_ITEM_ALL = 0;
    protected static final int FILTER_BAR_ITEM_A = 1;
    protected static final int FILTER_BAR_ITEM_B = 2;
    protected static final int FILTER_BAR_ITEM_C = 3;
    protected static final int FILTER_BAR_ITEM_D = 4;
    protected static final int FILTER_BAR_ITEM_E = 5;
    /*protected static final int FILTER_BAR_ITEM_MONDAY = 1;
    protected static final int FILTER_BAR_ITEM_TUESDAY = 2;
    protected static final int FILTER_BAR_ITEM_WEDNESDAY = 3;
    protected static final int FILTER_BAR_ITEM_THURSDAY = 4;
    protected static final int FILTER_BAR_ITEM_FRIDAY = 5;
    protected static final int FILTER_BAR_ITEM_SATURDAY = 6;
    protected static final int FILTER_BAR_ITEM_SUNDAY = 7;*/
    protected static final int FILTER_BAR_ITEM_INVALID = -1;

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
            IgniteContract.Sessions.SESSION_IS_GENERAL_SESSION,
            IgniteContract.Sessions.SESSION_SLOT,
            IgniteContract.Categories.CATEGORY_NAME,
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.Schedules.CALENDAR_EVENT_ID
    };

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_DISCOVER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "Entering onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        isFirstLevelActivity = true;
        selectedFilter = 0;
        DiscoverSingleton.getInstance().selfItem = 0;
        if (savedInstanceState == null) {

        }
        mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        discoverListView = (ListView) findViewById(R.id.discoverList);

        /*Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
        initDataset(cursor);

        //If we have session data, show it to the user
        if(session_data.length != 0) {
            Log.d(CLASS_NAME, "session_data is > 0");
            discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);
            discoverListView.setAdapter(discoverAdapter);
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            discoverListView.setVisibility(ListView.GONE);
            TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
            discoverError.setVisibility(View.VISIBLE);
            //discoverListView.setAdapter();
        }*/

        //refreshUI();

        //blApplication.bluemixPushSubscribeTo(DISCOVER_UPDATES);
        /*if (session_data.length == 0) {
            getBlueMixData();
        }*/
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupFilterBar();
            }
        },1000);*/

        Log.d(CLASS_NAME, "Exiting onCreate");
    }

    @Override
    public void refreshUI() {
        Log.d(CLASS_NAME, "Entering refreshUI");

        runOnUiThread(new Runnable() {
            public void run() {
                sessions = new ArrayList<SessionBean>();
                String mSelectionClause;
                Cursor cursor;
                if(DiscoverSingleton.getInstance().selfItem==0){
                    Log.d("Condition", "self == 0");
                    mSelectionClause =  IgniteContract.Sessions.SESSION_IS_HIDDEN+" != 'true'";
                    cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, mSelectionClause, null, IgniteContract.Sessions.SESSION_FROM_TIME);
                }else{
                    Log.d("Condition", "self != 0");
                    //Long nextDay = DiscoverSingleton.getInstance().selfDateList.get(DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem))+ 30 * 60 * 1000;

                    mSelectionClause =  IgniteContract.Sessions.SESSION_IS_HIDDEN+" != 'true' and "+IgniteContract.Sessions.SESSION_SLOT+" == '"+DiscoverSingleton.getInstance().slot.get(DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem))+"'";
                    cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, mSelectionClause, null, IgniteContract.Sessions.SESSION_FROM_TIME);
                }
                initDataset(cursor);
                if (sessions.size() != 0) {
                    Log.d("GeneralSessions", sessions.size() + "");
                    session_data = sessions.toArray(new SessionBean[sessions.size()]);
                    discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);
                    discoverListView.setAdapter(discoverAdapter);
                    // Restore previous state (including selected item index and scroll position)
                    if(state != null) {
                        Log.d("ListView", "trying to restore listview state..");
                        discoverListView.onRestoreInstanceState(state);
                    }
                } else {
                    Log.d(CLASS_NAME, "session_data is null or empty");
                    discoverListView.setVisibility(ListView.GONE);
                    TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
                    discoverError.setVisibility(View.VISIBLE);
                }
            }
        });
        Intent intent = getIntent();

        Log.d(CLASS_NAME, "Exiting refreshUI");
/*
        finish();
        startActivity(intent);*/
    }
    public void initDataset(Cursor cursor) {

        Log.d(CLASS_NAME, "Entering initDataset "+cursor.getCount());

        String scheduleProjection[] = {
                IgniteContract.Schedules.CALENDAR_EVENT_ID
        };
        //session_data = new SessionBean[cursor.getCount()];
        List<String> sections = new ArrayList<String>();
        cursor.moveToFirst();
        SessionBean section;
        String header = "";
        if(DiscoverSingleton.getInstance().notInitialized) {
            filterItemCount = 0;
            DiscoverSingleton.getInstance().filterDates = new HashMap<Integer, String>();
            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "All");
            //DiscoverSingleton.getInstance().selfDateList = new HashMap<Integer, Long>();
            DiscoverSingleton.getInstance().slot = new HashMap<Integer, String>();
            DiscoverSingleton.getInstance().slot.put(filterItemCount, "All");

            filterItemCount++;
        }
        if (cursor.getCount() > 0) {
            do {
                Log.d("isGeneralSession", cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_GENERAL_SESSION))+" id:"+cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID))+" name:"+cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                Date date2 = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_TO_TIME)));
                SimpleDateFormat format = new SimpleDateFormat("h:mma");
                SimpleDateFormat format2 = new SimpleDateFormat("h:mm");
                //String sectionHeader = format.format(date);
                String sectionHeader = cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_SLOT));
                if (header.equals("") || !header.equals(sectionHeader)) {
                    if(DiscoverSingleton.getInstance().notInitialized) {
                        Log.d("Filter", format.format(date)+" "+ sectionHeader);
                        if(sectionHeader.equalsIgnoreCase("A")){
                            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "Breakout A - "+format2.format(date)+" - "+format2.format(date2));
                            DiscoverSingleton.getInstance().slot.put(filterItemCount, sectionHeader);
                            //DiscoverSingleton.getInstance().selfDateList.put(filterItemCount, cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                            Log.d("FilterDate", format2.format(date));
                            filterItemCount++;
                            header = sectionHeader;
                        }else if(sectionHeader.equalsIgnoreCase("B")){
                            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "Breakout B - "+format2.format(date)+" - "+format2.format(date2));
                            DiscoverSingleton.getInstance().slot.put(filterItemCount, sectionHeader);
                            //DiscoverSingleton.getInstance().selfDateList.put(filterItemCount, cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                            Log.d("FilterDate", format2.format(date));
                            filterItemCount++;
                            header = sectionHeader;
                        }else if(sectionHeader.equalsIgnoreCase("C")){
                            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "Breakout C - "+format2.format(date)+" - "+format2.format(date2));
                            DiscoverSingleton.getInstance().slot.put(filterItemCount, sectionHeader);
                            //DiscoverSingleton.getInstance().selfDateList.put(filterItemCount, cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                            Log.d("FilterDate", format2.format(date));
                            filterItemCount++;
                            header = sectionHeader;
                        }else if(sectionHeader.equalsIgnoreCase("D")){
                            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "Breakout D - "+format2.format(date)+" - "+format2.format(date2));
                            DiscoverSingleton.getInstance().slot.put(filterItemCount, sectionHeader);
                            //DiscoverSingleton.getInstance().selfDateList.put(filterItemCount, cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                            Log.d("FilterDate", format2.format(date));
                            filterItemCount++;
                            header = sectionHeader;
                        }else if(sectionHeader.equalsIgnoreCase("E")){
                            DiscoverSingleton.getInstance().filterDates.put(filterItemCount, "Breakout E - "+format2.format(date)+" - "+format2.format(date2));
                            DiscoverSingleton.getInstance().slot.put(filterItemCount, sectionHeader);
                            //DiscoverSingleton.getInstance().selfDateList.put(filterItemCount, cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                            Log.d("FilterDate", format2.format(date));
                            filterItemCount++;
                            header = sectionHeader;
                        }
                    }
                    /*section = new SessionBean();
                    section.setHeaderTitle(sectionHeader);
                    section.setFromTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                    section.setIsHeader(true);
                    section.setIsHidden("false");
                    sessions.add(section);*/
                }
                SessionBean session = new SessionBean();
                session.setIsHeader(false);
                session.setSessionId(cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)));
                session.setSessionName(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
                session.setSessionCategory(cursor.getString(cursor.getColumnIndex(IgniteContract.Categories.CATEGORY_NAME)));
                session.setSessionDescription(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION)));
                session.setFromTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                session.setToTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_TO_TIME)));
                session.setSessionBg(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                session.setCalendarEventId(cursor.getString(cursor.getColumnIndex(IgniteContract.Schedules.CALENDAR_EVENT_ID)));
                session.setIsHidden(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));
                session.setIsGeneralSession(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_GENERAL_SESSION)));
                sessions.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if(DiscoverSingleton.getInstance().notInitialized) {
            DiscoverSingleton.getInstance().notInitialized = false;
        }
        setupFilterBar();
        Log.d(CLASS_NAME, "Exiting initDataset");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(CLASS_NAME, "Entering onCreateOptionsMenu");

        super.onCreateOptionsMenu(menu);

        Log.d(CLASS_NAME, "Exiting onCreateOptionsMenu");

        return true;
    }

    @Override
    protected void onResume() {
        Log.d(CLASS_NAME, "Entering onResume");
        super.invalidateOptionsMenu();
        super.onResume();
        refreshUI();
        if(filterBarAdapter != null) {
            filterBarAdapter.notifyDataSetChanged();
        }
        /*setContentView(R.layout.activity_discover);
        isFirstLevelActivity = true;

        mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        discoverListView = (ListView) findViewById(R.id.discoverList);

        Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
        initDataset(cursor);

        //If we have session data, show it to the user
        if(session_data.length != 0) {
            Log.d(CLASS_NAME, "session_data is > 0");
            discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);
            discoverListView.setAdapter(discoverAdapter);
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            discoverListView.setVisibility(ListView.GONE);
            TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
            discoverError.setVisibility(View.VISIBLE);
        }*/

        Log.d(CLASS_NAME, "Exiting onResume");
    }

    private void goToFilterBarItem(int filterBarItem){
        Log.d(CLASS_NAME, "Entering goToFilterBarItem");

        switch (filterBarItem) {
            case FILTER_BAR_ITEM_ALL:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_all+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_ALL;
                filterBarValue.setText(R.string.filter_bar_all);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_A:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_a+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_A;
                filterBarValue.setText(R.string.filter_bar_a);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_B:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_b+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_B;
                filterBarValue.setText(R.string.filter_bar_b);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_C:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_c+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_C;
                filterBarValue.setText(R.string.filter_bar_c);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_D:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_d+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_D;
                filterBarValue.setText(R.string.filter_bar_d);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_E:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_e+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_E;
                filterBarValue.setText(R.string.filter_bar_e);
                filterRefresh();
                return;
            /*case FILTER_BAR_ITEM_TUESDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_tuesday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_TUESDAY;
                filterBarValue.setText(R.string.filter_bar_tuesday);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_WEDNESDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_wednesday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_WEDNESDAY;
                filterBarValue.setText(R.string.filter_bar_wednesday);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_THURSDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_thursday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_THURSDAY;
                filterBarValue.setText(R.string.filter_bar_thursday);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_FRIDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_friday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_FRIDAY;
                filterBarValue.setText(R.string.filter_bar_friday);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_SATURDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_saturday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_SATURDAY;
                filterBarValue.setText(R.string.filter_bar_saturday);
                filterRefresh();
                return;
            case FILTER_BAR_ITEM_SUNDAY:
                //TODO logic for selection
                Toast.makeText(this,R.string.filter_bar_sunday+" Selected",Toast.LENGTH_SHORT);
                DiscoverSingleton.getInstance().selfItem = FILTER_BAR_ITEM_SUNDAY;
                filterBarValue.setText(R.string.filter_bar_sunday);
                filterRefresh();
                return;*/
        }

        Log.d(CLASS_NAME, "Exiting goToFilterBarItem");
    }

    private void filterRefresh(){
        selectedFilter = DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem);
        filterListView.setItemChecked(selectedFilter, true);
        hideFilterBar();
        refreshUI();
    }

    public void setupFilterBar() {
        Log.d(CLASS_NAME, "Entering setupFilterBar");

        HashMap<Integer,String> filterDates = DiscoverSingleton.getInstance().filterDates;
        HashMap<Integer,String> filterSlots = DiscoverSingleton.getInstance().slot;
        filterItemCount = filterSlots.size();
        // titles for filterbar items (indices must correspond to the above)
        final int[] FILTER_BAR_TITLE_RES_ID = new int[filterItemCount];
        Log.d(CLASS_NAME,"count: "+filterItemCount);

        filterPosition = new int[filterItemCount];

        filterBar = findViewById(R.id.filterBar);
        filterBarValue = (TextView) findViewById(R.id.filterVal);

        DiscoverSingleton.getInstance().selfList = new HashMap<Integer,Integer>();

        int count = 0;
        Log.d("FilterItemCount", ""+filterSlots.size()+" "+filterSlots.get(count)+" - "+filterItemCount);
        while(count < filterItemCount) {
            switch(filterSlots.get(count)){

                case "All":
                    Log.d("FilterItem", "All");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_all;
                    filterPosition[count] = FILTER_BAR_ITEM_ALL;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_ALL ,count);
                    count++;
                    Log.d("FilterItem", "All "+count);
                    break;
                case "A":
                    Log.d("FilterItem", "Breakout A - 10:25 - 11:10");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_a;
                    filterPosition[count] = FILTER_BAR_ITEM_A;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_A ,count);
                    count++;
                    break;
                case "B":
                    Log.d("FilterItem", "Breakout B - 11:25 - 12:10");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_b;
                    filterPosition[count] = FILTER_BAR_ITEM_B;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_B ,count);
                    count++;
                    break;
                case "C":
                    Log.d("FilterItem", "Breakout C - 2:00 - 2:50");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_c;
                    filterPosition[count] = FILTER_BAR_ITEM_C;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_C ,count);
                    count++;
                    break;
                case "D":
                    Log.d("FilterItem", "Breakout D - 3:00 - 3:45");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_d;
                    filterPosition[count] = FILTER_BAR_ITEM_D;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_D ,count);
                    count++;
                    break;
                case "E":
                    Log.d("FilterItem", "Breakout E - 3:55 - 4:40");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_e;
                    filterPosition[count] = FILTER_BAR_ITEM_E;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_E ,count);
                    count++;
                    break;
                default:
                    Log.d("filterSlots.get(count)", filterSlots.get(count));
                    count++;
                    break;
                /*case "Monday":
                    Log.d("FilterItem", "Mon");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_monday;
                    filterPosition[count] = FILTER_BAR_ITEM_MONDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_MONDAY ,count);
                    count++;
                    break;
                case "Tuesday":
                    Log.d("FilterItem", "Tue");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_tuesday;
                    filterPosition[count] = FILTER_BAR_ITEM_TUESDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_TUESDAY ,count);
                    count++;
                    break;
                case "Wednesday":
                    Log.d("FilterItem", "Wed");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_wednesday;
                    filterPosition[count] = FILTER_BAR_ITEM_WEDNESDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_WEDNESDAY ,count);
                    count++;
                    break;
                case "Thursday":
                    Log.d("FilterItem", "Thu");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_thursday;
                    filterPosition[count] = FILTER_BAR_ITEM_THURSDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_THURSDAY ,count);
                    count++;
                    break;
                case "Friday":
                    Log.d("FilterItem", "Fri");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_friday;
                    filterPosition[count] = FILTER_BAR_ITEM_FRIDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_FRIDAY ,count);
                    count++;
                    break;
                case "Saturday":
                    Log.d("FilterItem", "Sat");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_saturday;
                    filterPosition[count] = FILTER_BAR_ITEM_SATURDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_SATURDAY ,count);
                    count++;
                    break;
                case "Sunday":
                    Log.d("FilterItem", "Sun");
                    FILTER_BAR_TITLE_RES_ID[count] = R.string.filter_bar_sunday;
                    filterPosition[count] = FILTER_BAR_ITEM_SUNDAY;
                    DiscoverSingleton.getInstance().selfList.put(FILTER_BAR_ITEM_SUNDAY ,count);
                    count++;
                    break;*/
            }
        }

        if (filterBar == null) {
            return;
        }

        /*if (selfItem == FILTER_BAR_ITEM_INVALID) {
            // do not show a menu bar
            ((ViewGroup) filterBar.getParent()).removeView(filterBar);
            return;
        }*/

        mPopupFilterBar = new PopupWindow(this);

        // the drop down list is a list view
        filterListView = new ListView(this);
        filterListView.setPadding(16, 0, 16, 0);
        filterListView.setBackgroundColor(getResources().getColor(R.color.theme_primary_black));
        filterListView.setDivider(new ColorDrawable(getResources().getColor(R.color.white)));
        filterListView.setDividerHeight(1);

        //filterListView.setSelector(new ColorDrawable(getResources().getColor(R.color.theme_secondary_blue)));
        int dpi = getResources().getConfiguration().densityDpi;
        if(dpi<=320) {
            Log.d("DPI/BGSize",dpi+"/small filterListView");
            filterListView.setSelector(getResources().getDrawable(R.drawable.menu_selection_small));
        }else{
            Log.d("DPI/BGSize",dpi+"/normal filterListView");
            filterListView.setSelector(getResources().getDrawable(R.drawable.menu_selection));
        }
        filterListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        // set our adapter and pass our pop up window contents
        filterBarAdapter = new FilterBarAdapter(this, R.layout.menu_bar_item, FILTER_BAR_TITLE_RES_ID);
        filterListView.setAdapter(filterBarAdapter);
        Log.d(CLASS_NAME, "Adapter Set");

        // set the item click listener

        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CLASS_NAME, "Entering onItemClick");
                Toast.makeText(DiscoverActivity.this,"Loading...",Toast.LENGTH_SHORT);
                Log.d("On Item CLicked", parent.getClass().toString()+"");
                if(filterPosition[position] == DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem)){
                    //already in the correct screen
                    Log.d("Filter","Already in position");
                    hideFilterBar();
                    return;
                }

                Log.d("filterPosition/position", filterPosition[position]+"/"+position);
                goToFilterBarItem(filterPosition[position]);
                hideFilterBar();
                Log.d(CLASS_NAME, "Exiting onItemClick");
            }
        });

        // some other visual settings
        mPopupFilterBar.setFocusable(true);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        Log.d("DPI", getResources().getConfiguration().densityDpi+"");
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                mPopupFilterBar.setWidth(800);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:

                if(dpi<=320){
                    Log.d("DPI/Size",dpi+"/500");
                    mPopupFilterBar.setWidth(500);
                }else{
                    Log.d("DPI/Size",dpi+"/700");
                    mPopupFilterBar.setWidth(700);
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                mPopupFilterBar.setWidth(500);
                break;
            default:
                mPopupFilterBar.setWidth(500);
        }

        mPopupFilterBar.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


        // set the list view as pop up window content
        mPopupFilterBar.setContentView(filterListView);

        /*mPopupFilterBar.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //ImageButton img = (ImageButton)mMenuBar;
                //img.setBackgroundResource(R.drawable.menu_open1);
                crossToLine();
            }
        });*/

        filterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(CLASS_NAME, "Entering onClick");

                //Onclick for Menu Bar
                // Load the ImageView that will host the animation and
                // set its background to our AnimationDrawable XML resource.
                //ImageButton img = (ImageButton)mMenuBar;
                //img.setBackgroundResource(R.drawable.menu_close1);

                showFilterBar(v);

                //v.setElevation(Float.valueOf("2"));
                //v.bringToFront();
                //v.invalidate();
                //ViewCompat.setTranslationZ(v, Float.valueOf("10.0"));

                Log.d(CLASS_NAME, "Exiting onClick");
            }
        });
        //filterBar.bringToFront();
        //((ViewGroup) filterBar.getParent()).requestLayout();
        //((ViewGroup) filterBar.getParent()).invalidate();

        Log.d(CLASS_NAME, "Exiting setupMenuBar");
    }


    private void showFilterBar(View view) {
        Log.d(CLASS_NAME, "Entering showFilterBar");

        // Finally show the PopupMenu
        mPopupFilterBar.showAsDropDown(view, 0, 5);
        //mPopupFilterBar.showAtLocation(view, Gravity.LEFT, 0, 80);

        Log.d("SelfItem", DiscoverSingleton.getInstance().selfItem + "/" + DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem));
        filterListView.setItemChecked(DiscoverSingleton.getInstance().selfList.get(DiscoverSingleton.getInstance().selfItem), true);

        Log.d(CLASS_NAME, "Exiting showFilterBar");
    }

    private void hideFilterBar(){
        Log.d(CLASS_NAME, "Entering hideFilterBar");

        if(mPopupFilterBar!=null && mPopupFilterBar.isShowing()){
            mPopupFilterBar.dismiss();
        }

        Log.d(CLASS_NAME, "Exiting hideFilterBar");
    }

    class FilterBarAdapter extends ArrayAdapter<String> {

        LayoutInflater inflater;
        Context context;
        int[] stringIds;
        int selfFilterBarItem;

        class ViewHolder {
            TextView title;
        }

        public FilterBarAdapter(Context context, int resource, int[] stringIds) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.stringIds = stringIds;
            Log.d("selfFilterBarItem", "in Constructor "+selfFilterBarItem);
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
            selfFilterBarItem = DiscoverSingleton.getInstance().selfItem;
            if(selfFilterBarItem==filterPosition[position]) {
                Log.d("selfFilterBarItem", selfFilterBarItem+" Position:"+filterPosition[position]);
                int dpi = getResources().getConfiguration().densityDpi;
                if(dpi<=320) {
                    Log.d("DPI/BGSize",dpi+"/small Adapter");
                    convertView.setBackgroundResource(R.drawable.menu_selection_small);
                }else{
                    Log.d("DPI/BGSize",dpi+"/normal Adapter");
                    convertView.setBackgroundResource(R.drawable.menu_selection);
                }

            }else{
                convertView.setBackgroundColor(getResources().getColor(R.color.theme_primary_black));
            }
            //TODO
            convertView.setMinimumWidth(400);
            return convertView;
        }

        @Override
        public int getCount() {

            /*passcode = prefs.getString("passcode", null);
            if(passcode != null){*/
            return stringIds.length;
            /*} else {
                return stringIds.length - 1;
            }*/

        }

        @Override
        public String getItem(int position) {
            return context.getString(stringIds[position]);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CLASS_NAME, "Entering onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 55 && data != null) {
            Button loginButton;
            String loginButtonText;

            loginButton = (Button) findViewById(R.id.userName);

            if(resultCode == RESULT_OK) {

                loginButtonText = data.getStringExtra("loginButtonText");

                if(loginButtonText != null && !loginButtonText.isEmpty()) {
                    Log.d(CLASS_NAME, "Login text should be: 'Welcome, <username>!'");
                    String twitterString = data.getStringExtra("isTwitter");

                    if(data.getStringExtra("isTwitter") != null) {
                        boolean isTwitterService = Boolean.parseBoolean(data.getStringExtra("isTwitter"));

                        if(isTwitterService) {
                            HashMap<String, String> updateLoginState = new HashMap<String, String>();
                            updateLoginState.put("isTwitter", "true");
                            updateLoginState.put("loginState", "true");
                            Log.d(CLASS_NAME, "updateLoginState is: " + updateLoginState);
                            discoverAdapter.setLoginState(updateLoginState);
                            discoverListView.setAdapter(discoverAdapter);
                            Log.d("Inside LoggedIn ", "----------");
                            Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<User>() {
                                @Override
                                public void success(Result<User> userResult) {
                                    User user = userResult.data;
                                    String profileImageUrl = user.profileImageUrl;
                                    Log.d("profileImage URL: ", profileImageUrl);
                                    profileImageUrl = profileImageUrl.replace("_normal", "");

                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString("imgUrl", profileImageUrl);
                                    editor.putBoolean("imgFlag", false);
                                    editor.commit();
                                    ImageView profileImg = (ImageView) findViewById(R.id.imageView);
                                    if (profileImageUrl != null && !profileImageUrl.equalsIgnoreCase("")) {
                                        Drawable drawable = LoadImageFromWebOperations(profileImageUrl);
                                        profileImg.setImageDrawable(drawable);
                                    } else {
                                        profileImg.setImageDrawable(null);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                }
                            });
                        }else {
                            Log.d(CLASS_NAME, "isTwitterService was false");
                        }
                    }else {
                        Log.d(CLASS_NAME, "twitterString was null");
                    }

                    SpannableString loginText = new SpannableString(loginButtonText);
                    loginText.setSpan(new UnderlineSpan(), 0, loginButtonText.length(), 0);
                    loginButton.setText(loginText);
                }else {
                    Log.d(CLASS_NAME, "loginButtonText was null");
                }
            }else {
                Log.d(CLASS_NAME, "Login attempt resulted in an error");

                SpannableString loginText;
                String errorText = data.getStringExtra("errorText");
                AlertDialog errorDialog;

                if(errorText != null && !errorText.isEmpty()) {
                    errorDialog = new AlertDialog.Builder(this)
                            .setTitle("Twitter Login Failed")
                            .setMessage(errorText)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                loginText = new SpannableString("LOGIN UNSUCCESSFUL\nTRY AGAIN?");
                loginText.setSpan(new UnderlineSpan(), 0, "LOGIN UNSUCCESSFUL\nTRY AGAIN?".length(), 0);
                loginButton.setText(loginText);
            }
        }else if (requestCode == 100 && resultCode == RESULT_OK){ //Gallery
            if(data.getData() != null){
                Uri selectedImageUri = data.getData();
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                String selectedPath = selectedImageUri.getPath();
                Log.d("selectedPath1 : " ,selectedImageUri.toString());
                Log.d("selectedPath : " ,selectedPath);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("imgUrl", selectedImageUri.toString());
                editor.putBoolean("imgFlag",true);
                editor.commit();
            }else{
                Log.d("selectedPath1 : ","Came here its null !");
                Toast.makeText(getApplicationContext(), "failed to get Image!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == 200 && resultCode == RESULT_OK){ //Camera
            Log.d(CLASS_NAME, "Obtained Result from Camera");
        } else {
            Log.d(CLASS_NAME, "Intent was null - this probably means the LoginSelector was cancelled via 'Back' button");
        }

        Log.d(CLASS_NAME, "Exiting onActivityResult");
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        Log.d(CLASS_NAME, "Entering LoadImageFromWebOperations");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src_name");

            Log.d(CLASS_NAME, "Exiting LoadImageFromWebOperations"+url);

            return d;
        } catch (Exception e) {
            Log.d(CLASS_NAME, "Exiting LoadImageFromWebOperations"+ e);

            return null;
        }
    }

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d("ListView", "saving listview state @ onPause");
        state = discoverListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogLoading.hide();
    }
}