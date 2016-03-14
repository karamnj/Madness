package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.SponsorBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.SponsorsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SponsorsActivity extends BaseActivity {

    private static final String CLASS_NAME = "SponsorsActivity";

    private String[] projectionList = {
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.ImageMetaData.IMAGE_HEIGHT,
            IgniteContract.ImageMetaData.IMAGE_WIDTH,
            IgniteContract.Sponsors.SPONSOR_NAME,
            IgniteContract.Sponsors.SPONSOR_WEBSITE_LINK
    };

    private List<SponsorBean> promotions;
    ListView sponsorPromotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors);
        isFirstLevelActivity = true;
        Cursor cursor = getContentResolver().query(IgniteContract.Sponsors.CONTENT_URI,projectionList,null,null,null);
        promotions = new ArrayList<SponsorBean>();
        initDataSet(cursor);

        sponsorPromotions = (ListView) findViewById(R.id.sponsorsList);

        if(promotions != null && promotions.size() > 0) {

            SponsorsAdapter adapter = new SponsorsAdapter(this, R.layout.layout_sponsor_promotion, promotions);
            sponsorPromotions.setAdapter(adapter);

            sponsorPromotions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("IGNITE", "onClick...");

                    Intent intent = new Intent(SponsorsActivity.this, SponsorsWebViewActivity.class);

                    intent.putExtra("webLink", promotions.get(position).getWebLink());

                    ((Activity) SponsorsActivity.this).startActivity(intent);
                }
            });

        }else {
            sponsorPromotions.setVisibility(ListView.GONE);
            TextView sponsorError = (TextView) findViewById(R.id.sponsorsErrorMessage);
            sponsorError.setVisibility(View.VISIBLE);
        }
    }

    public void initDataSet(Cursor cursor){
        Log.d(CLASS_NAME, "Entering initDataSet");

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                byte[] imageData = cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA));
                if(imageData != null){
                    SponsorBean sponsor = new SponsorBean();
                    sponsor.setImage(imageData);
                    sponsor.setImageHeight(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_HEIGHT)));
                    sponsor.setImageWidth(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_WIDTH)));
                    sponsor.setName(cursor.getString(cursor.getColumnIndex(IgniteContract.Sponsors.SPONSOR_NAME)));
                    sponsor.setWebLink(cursor.getString(cursor.getColumnIndex(IgniteContract.Sponsors.SPONSOR_WEBSITE_LINK)));
                    promotions.add(sponsor);
                }
            } while (cursor.moveToNext());
        }else {
            Log.d(CLASS_NAME, "Cursor returned no results!");
        }

        Log.d(CLASS_NAME, "Exiting initDataSet");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected int getSelfMenuBarItem() {
        return MENU_BAR_ITEM_SPONSORS;
    }
}
