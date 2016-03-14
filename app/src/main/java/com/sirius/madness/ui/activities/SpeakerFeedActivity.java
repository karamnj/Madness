package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.SpeakerFeedListAdapter;

import java.util.HashMap;

public class SpeakerFeedActivity extends BaseActivity {

    private SpeakerFeedListAdapter mSpeakersAdapter;
    private RecyclerView mSpeakerListRecyclerView;
    private LinearLayoutManager mSpeakerLayoutManager;

    //protected String[] mDataset;
    protected HashMap<String, String[]> mDataset;
    protected byte[][] mPresenterImages;
    private int datasetCount = 10;
    private static final String CLASS_NAME = "SpeakerFeedActivity";
    private static final String SPEAKER_UPDATES = "Speaker Updates";

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_SPEAKERS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_feed);
        isFirstLevelActivity = true;

        Intent i = getIntent();

        String [] projectionList= {
                IgniteContract.Presenters.PRESENTER_FIRST_NAME,
                IgniteContract.Presenters.PRESENTER_LAST_NAME,
                IgniteContract.Presenters.PRESENTER_DESIGNATION,
                IgniteContract.Presenters.PRESENTER_ID,
                IgniteContract.Partners.PARTNER_LONG_TITLE,
                IgniteContract.ImageBinary.IMAGE_DATA
        };
        Cursor cursor = getContentResolver().query(IgniteContract.Presenters.CONTENT_URI,projectionList,null,null, null);
        initDataset(cursor);
        mSpeakerListRecyclerView = (RecyclerView) findViewById(R.id.SpeakerFeedList);
        mSpeakerLayoutManager = new LinearLayoutManager(this);
        mSpeakerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if(mDataset != null) {

            mSpeakersAdapter = new SpeakerFeedListAdapter(this, mDataset, mPresenterImages);

            mSpeakerListRecyclerView.setAdapter(mSpeakersAdapter);
            mSpeakerListRecyclerView.setLayoutManager(mSpeakerLayoutManager);

            blApplication.bluemixPushSubscribeTo(SPEAKER_UPDATES);
        }else {
           TextView mSpeakerError = (TextView) findViewById(R.id.speakerErrorMessage);
            mSpeakerListRecyclerView.setLayoutManager(mSpeakerLayoutManager);
           //mSpeakerListRecyclerView.setVisibility(View.GONE);
           mSpeakerError.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private void initDataset(Cursor cursor) {

        Log.d(CLASS_NAME, "Entering initDataset");

        datasetCount = cursor.getCount();

        if(datasetCount > 0) {

            Log.d(CLASS_NAME, "Cursor returned " + datasetCount + " results");

            String[] names = new String[datasetCount];
            String[] titles = new String[datasetCount];
            String[] organizations = new String[datasetCount];
            String[] presenterId = new String[datasetCount];

            mDataset = new HashMap<String, String[]>();
            mPresenterImages = new byte[cursor.getCount()][];
            int i = 0;
            cursor.moveToFirst();
            do {
                names[i] = cursor.getString(0) + " " + cursor.getString(1);
                titles[i] = cursor.getString(2);
                organizations[i] = cursor.getString(4);
                presenterId[i] = cursor.getString(3);
                mPresenterImages[i] = cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA));
                i++;
            } while (cursor.moveToNext());

            mDataset.put("names", names);
            mDataset.put("titles", titles);
            mDataset.put("organizations", organizations);
            mDataset.put("presenterId", presenterId);
        }else {
            Log.d(CLASS_NAME, "Cursor returned no results");
            mDataset = null;
        }

        Log.d(CLASS_NAME, "Exiting initDataset");
    }
}
