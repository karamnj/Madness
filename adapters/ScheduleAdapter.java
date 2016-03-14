package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.ScheduleBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.activities.SessionDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 915649 on 23/01/15.
 */
public class ScheduleAdapter /*implements ListAdapter*/ extends BaseAdapter {

    private static final String CLASS_NAME = "ScheduleAdapter";

    Context context;
    List<ScheduleBean> schedules;


    public ScheduleAdapter(Context context, List<ScheduleBean> schedules){
        super();
        this.context = context;
        this.schedules = schedules;
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final ScheduleBean schedule = schedules.get(position);
        View row = convertView;


        if(row == null) {
            if(schedule.isHeader()){
                row = inflater.inflate(R.layout.schedule_section, parent, false);
                row.setTag("Header");
            }else{
                row = inflater.inflate(R.layout.schedule_item, parent, false);
                row.setTag("Item");
            }
        }

        if(!("Header".equals(row.getTag())) && schedule.isHeader()){
            row = inflater.inflate(R.layout.schedule_section, parent, false);
            row.setTag("Header");
        } else if(!("Item".equals(row.getTag())) &&  (!schedule.isHeader())){
            row = inflater.inflate(R.layout.schedule_item, parent, false);
            row.setTag("Item");
        }

        if(schedule.isHeader()){
            TextView sectionTitle = (TextView)row.findViewById(R.id.sectionTitle);
            sectionTitle.setText(schedule.getHeaderTitle());
            int index = schedule.getHeaderIndex();
            if (index > 0) {
                index = index % 2;
            }
            int color;
            switch (index) {
                case 0:
                    color = getColorForHeader(schedule.getHeaderIndex());
                    sectionTitle.setBackgroundColor(color);
                    break;
                case 1:
                    color = getColorForCell(schedule.getHeaderIndex());
                    sectionTitle.setBackgroundColor(color);
                    break;

                default:
                    color = getColorForHeader(schedule.getHeaderIndex());
                    sectionTitle.setBackgroundColor(color);
                    break;
            }

        }else{
            LinearLayout sectionListBackground = (LinearLayout) row.findViewById(R.id.scheduleItemBackground);
            LinearLayout sectionListDarkBackground = (LinearLayout) row.findViewById(R.id.scheduleItemDarkBackground);
            TextView sectionTitle = (TextView)row.findViewById(R.id.eventTitle);
            TextView sectionTime = (TextView) row.findViewById(R.id.scheduleItemIconText);
            String headerTitle = schedule.getHeaderTitle();
            Log.d("HeaderTitle:",headerTitle);
            sectionTitle.setText(headerTitle);
            Log.d(CLASS_NAME, "sectionTitle = " + sectionTitle.getText().toString());
            int color = getColorForCell(schedule.getHeaderIndex());
            int color2 = getColorForHeader(schedule.getHeaderIndex());
            sectionListBackground.setBackgroundColor(color);
            SimpleDateFormat fromTime = new SimpleDateFormat("h:mm a");
            sectionTime.setText(fromTime.format(schedule.getFromTime()));
            sectionListDarkBackground.setBackgroundColor(color2);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(CLASS_NAME,"inside row.setOnClick");
                    Intent sessionDetailIntent = new Intent(context,SessionDetailActivity.class);
                    int sessionId = schedule.getSessionId();
                    Log.d(CLASS_NAME,"sessionId:"+sessionId);
                    if(sessionId > 0){
                        sessionDetailIntent.putExtra("Session Id",sessionId);
                        context.startActivity(sessionDetailIntent);
                    }
                }
            });

            //The "Happy hours" item is not supposed to be removable
            //if(!headerTitle.equals("Happy hours")) {
            if("false".equals(schedule.getMandatory())){

                Log.d(CLASS_NAME, "Item is not mandatory");

                row.setOnTouchListener(new View.OnTouchListener() {
                    private float initialTouchX;
                    private float initialTouchY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                if ((Math.abs(initialTouchX - event.getRawX()) < 5) && (Math.abs(initialTouchY - event.getRawY()) < 5)) {
                                    v.performClick();
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                float distX = event.getRawX() - initialTouchX;
                                float distY = event.getRawY() - initialTouchY;
                                if (distX < -150 && Math.abs(distX)>Math.abs(distY)) {
                                    Log.d(CLASS_NAME, "User swiped left");
                                    TextView eventRightArrow = (TextView) v.findViewById(R.id.eventRightArrow);
                                    LinearLayout scheduleItemDarkBackground = (LinearLayout) v.findViewById(R.id.scheduleItemDarkBackground);
                                    scheduleItemDarkBackground.setVisibility(View.GONE);
                                    LinearLayout scheduleHiddenTab = (LinearLayout) v.findViewById(R.id.scheduleHiddenTab);
                                    eventRightArrow.setVisibility(View.GONE);
                                    scheduleHiddenTab.setVisibility(View.VISIBLE);
                                } else if (distX > 150 && Math.abs(distX)>Math.abs(distY)) {
                                    Log.d(CLASS_NAME, "User swiped right");
                                    TextView eventRightArrow = (TextView) v.findViewById(R.id.eventRightArrow);
                                    LinearLayout scheduleItemDarkBackground = (LinearLayout) v.findViewById(R.id.scheduleItemDarkBackground);
                                    scheduleItemDarkBackground.setVisibility(View.VISIBLE);
                                    LinearLayout scheduleHiddenTab = (LinearLayout) v.findViewById(R.id.scheduleHiddenTab);
                                    scheduleHiddenTab.setVisibility(View.GONE);
                                    eventRightArrow.setVisibility(View.VISIBLE);
                                }
                                return true;
                        }
                        return false;
                    }
                });

                /*row.setOnTouchListener(new OnSwipeTouchListener(context) {
                    @Override
                    public void onSwipeLeft(View v) {
                        Log.d(CLASS_NAME, "User swiped left");
                        TextView eventRightArrow = (TextView) v.findViewById(R.id.eventRightArrow);
                        LinearLayout scheduleHiddenTab = (LinearLayout) v.findViewById(R.id.scheduleHiddenTab);
                        eventRightArrow.setVisibility(View.GONE);
                        scheduleHiddenTab.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSwipeRight(View v) {
                        Log.d(CLASS_NAME, "User swiped right");
                        TextView eventRightArrow = (TextView) v.findViewById(R.id.eventRightArrow);
                        LinearLayout scheduleHiddenTab = (LinearLayout) v.findViewById(R.id.scheduleHiddenTab);
                        scheduleHiddenTab.setVisibility(View.GONE);
                        eventRightArrow.setVisibility(View.VISIBLE);
                    }

                });*/

                row.findViewById(R.id.scheduleHiddenTab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri baseUri = Uri.parse("content://com.android.calendar/events");
                        Uri scheduleUri = IgniteContract.Schedules.buildSessionUri(schedule.getSessionId());
                        context.getContentResolver().delete(scheduleUri, null, null);
                        long eventId = Long.valueOf(schedule.getCalendarEventId());
                        context.getContentResolver().delete(ContentUris.withAppendedId(baseUri, eventId), null, null);
                        schedule.setCalendarEventId(null);
                        notifyDataSetChanged();
                    }
                });
            }else{
                row.findViewById(R.id.eventRightArrow).setVisibility(View.GONE);
            }
        }

        return row;
    }

    private int getColorForHeader(int index) {

        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 0) {
            index = index % 2;
        }

        switch (index) {
            case 0:
                red = 241;
                green = 92;
                blue = 39;
                //result = R.color.theme_schedule_background_orange;
                break;
            case 1:
                red = 17;
                green = 100;
                blue = 138;
                //result = R.color.theme_schedule_background_blue;
                break;

            default:
                red = 241;
                green = 92;
                blue = 39;
                //result = R.color.theme_schedule_background_orange;
                break;
        }


        return Color.argb(alpha, red, green, blue);
    }

    private int getColorForCell(int index) {

        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 0) {
            index = index % 2;
        }

        switch (index) {
            case 0:
                red = 248;
                green = 150;
                blue = 31;
                //result = R.color.theme_tertiary_light_orange;
                break;
            case 1:
                red = 25;
                green = 128;
                blue = 184;
                //result = R.color.theme_secondary_blue;
                break;

            default:
                red = 248;
                green = 150;
                blue = 31;
                //result = R.color.theme_tertiary_light_orange;
                break;
        }

        return Color.argb(alpha, red, green, blue);
    }


}
