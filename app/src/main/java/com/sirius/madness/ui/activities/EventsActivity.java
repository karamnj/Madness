package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class EventsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        isFirstLevelActivity = true;

        LinearLayout holder = (LinearLayout) findViewById(R.id.events_holder);
        LayoutInflater inflater = this.getLayoutInflater();

        try {
            JSONArray events = new JSONArray(loadJSONFromAsset());
            View row;
            for (int i = 0; i < events.length(); i++) {
                final JSONObject event = events.getJSONObject(i);
                Log.d("Details-->", event.getString("title"));
                final int id = event.getInt("id");
                String title = event.getString("title");
                String date = event.getString("date");
                String time = event.getString("time");
                String image = event.getString("image");

                row = inflater.inflate(R.layout.layout_events, holder, false);
                ImageView eventBG = (ImageView) row.findViewById(R.id.eventBackground);
                TextView dateText = (TextView) row.findViewById(R.id.date);
                TextView timeText = (TextView) row.findViewById(R.id.time);
                TextView titleText = (TextView) row.findViewById(R.id.title);
                TextView categoryText = (TextView) row.findViewById(R.id.eventCategory);

                eventBG.setImageDrawable(getDrawable(image));
                dateText.setText(date);
                timeText.setText(time);
                titleText.setText(title);
                categoryText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/customIconFont.ttf"));
                categoryText.setText(getResources().getString(R.string.custom_font_icon_right_arrow));

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
                        intent.putExtra("EventID", id+"");
                        startActivity(intent);
                    }
                });

                holder.addView(row);
            }
            View padSpace = new View(this);
            padSpace.setBackgroundColor(getResources().getColor(R.color.tw__transparent));
            padSpace.setMinimumHeight(120);
            holder.addView(padSpace);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("Events.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Drawable getDrawable(String name) {
        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        return getResources().getDrawable(resourceId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected int getSelfMenuBarItem() {
        return MENU_BAR_ITEM_EVENTS;
    }
}
