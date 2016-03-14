package com.sirius.madness.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

public class EventDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().hide();

        int eventID = Integer.parseInt(getIntent().getStringExtra("EventID"));

        try {
            JSONArray events = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < events.length(); i++) {
                final JSONObject event = events.getJSONObject(i);
                Log.d("Details-->", event.getString("title"));
                final int id = event.getInt("id");
                if(eventID == id){
                    String title = event.getString("title");
                    String date = event.getString("date");
                    String time = event.getString("time");
                    String image = event.getString("image");
                    String desc = event.getString("desc");
                    String location = event.getString("location");
                    String attire = event.getString("attire");

                    ImageView eventBG = (ImageView) findViewById(R.id.eventBG);
                    TextView dateText = (TextView) findViewById(R.id.date);
                    TextView timeText = (TextView) findViewById(R.id.time);
                    TextView titleText = (TextView) findViewById(R.id.title);
                    TextView descText = (TextView) findViewById(R.id.desc);
                    TextView locText = (TextView) findViewById(R.id.detail1);
                    TextView attText = (TextView) findViewById(R.id.detail2);
                    if(id==2){
                        ImageView iv = (ImageView) findViewById(R.id.awards);
                        iv.setVisibility(View.VISIBLE);
                    }
                    if(desc.equalsIgnoreCase("")){
                        LinearLayout ll = (LinearLayout) findViewById(R.id.partners);
                        ll.setVisibility(View.VISIBLE);
                    }

                    eventBG.setImageDrawable(getDrawable(image));
                    dateText.setText(date);
                    timeText.setText(time);
                    titleText.setText(title);
                    descText.setText(desc);
                    locText.setText(location);
                    attText.setText(attire);
                    break;
                }
            }
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

}
