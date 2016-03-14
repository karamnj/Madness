package com.sirius.madness.ui.activities;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.sirius.madness.R;
import com.sirius.madness.beans.ScheduleBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.ScheduleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyScheduleActivity extends BaseActivity {
    private static final String CLASS_NAME = "MyScheduleActivity";

    private static final String SCHEDULE_UPDATES = "Schedule Updates";
    private static ArrayList<ScheduleBean> schedules;

    private ScheduleAdapter adapter;
    private ListView scheduleList;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_MY_SCHEDULE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstLevelActivity = true;
        setContentView(R.layout.activity_my_schedule);

        scheduleList = (ListView)findViewById(R.id.scheduleList);

        adapter = populateAdapter();
        scheduleList.setAdapter(adapter);
     }

    private ScheduleAdapter populateAdapter() {
        schedules = new ArrayList<ScheduleBean>();

        String projectionList[] = {
                IgniteContract.Schedules.CALENDAR_EVENT_ID,
                "Schedules."+IgniteContract.Schedules.EVENT_ID_FK,
                IgniteContract.Schedules.SESSION_ID_FK,
                IgniteContract.Schedules.EVENT_TITLE,
                "Schedules."+ IgniteContract.Schedules.FROM_TIME,
                "Schedules."+ IgniteContract.Schedules.TO_TIME,
                "Schedules."+ IgniteContract.Schedules.MANDATORY
        };

        Cursor cursor = getContentResolver().query(IgniteContract.Schedules.CONTENT_URI, projectionList, null, null, "Schedules.fromTime");
        populateScheduleBeanList(cursor);
        adapter = new ScheduleAdapter(this,schedules);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                Log.d(SCHEDULE_UPDATES, "Updating adapter");

                adapter = populateAdapter();
                scheduleList.setAdapter(adapter);
            }
        });

        return adapter;
    }

    public void populateScheduleBeanList(Cursor cursor){
        List<String> sections = new ArrayList<String>();
        cursor.moveToFirst();
        int headerIndex=-1;
        if (cursor.getCount() > 0) {
            do {
                Date date = new Date(cursor.getLong(4));
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                String sectionHeader = format.format(date);
                if(!sections.contains(sectionHeader)){
                    headerIndex++;
                    ScheduleBean section = new ScheduleBean();
                    section.setHeaderTitle(sectionHeader);
                    section.setHeader(true);
                    section.setHeaderIndex(headerIndex);
                    schedules.add(section);
                    sections.add(sectionHeader);
                }
                ScheduleBean schedule = new ScheduleBean();
                schedule.setHeaderIndex(headerIndex);
                schedule.setEventId(cursor.getString(1));
                schedule.setCalendarEventId(cursor.getString(0));
                schedule.setHeaderTitle(cursor.getString(3));
                schedule.setSessionId(cursor.getInt(2));
                schedule.setFromTime(date);
                schedule.setToTime(new Date(cursor.getLong(5)));
                schedule.setMandatory(cursor.getString(6));
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
