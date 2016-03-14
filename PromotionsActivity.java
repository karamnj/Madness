package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sirius.madness.R;
import com.sirius.madness.beans.PromotionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.PromotionsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromotionsActivity extends Activity {

    private static final String CLASS_NAME = "PromotionsActivity";

    private String[] projectionList = {
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.Promotions.PROMOTION_SHOW_FROM,
            IgniteContract.Promotions.PROMOTION_SHOW_TO,
            IgniteContract.Promotions.PROMOTION_RSVP_LINK
    };

    private List<PromotionBean> promotions;
    ListView promotionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        Cursor cursor = getContentResolver().query(IgniteContract.Promotions.CONTENT_URI,projectionList,null,null,IgniteContract.Promotions.PROMOTION_ID);
        promotions = new ArrayList<PromotionBean>();
        initDataSet(cursor);

        promotionsList = (ListView) findViewById(R.id.promotionList);

        if(promotions != null && promotions.size() > 0) {

            PromotionsAdapter adapter = new PromotionsAdapter(this, R.layout.promotions_list_item, promotions);
            promotionsList.setAdapter(adapter);

        }else {
            Intent intent = new Intent(this, LoginTutorial02Activity.class);
            this.startActivity(intent);
            finish();
            /*promotionsList.setVisibility(ListView.GONE);
            TextView promotionError = (TextView) findViewById(R.id.promotionsError);
            promotionError.setVisibility(View.VISIBLE);*/
        }
    }
    public void initDataSet(Cursor cursor){
        Log.d(CLASS_NAME, "Entering initDataSet");

        cursor.moveToFirst();
        Log.d("Count Cursor: ",cursor.getCount()+"");
        if (cursor.getCount() > 0) {
            do {
                Date currentDate = new Date();
                Date fromDate = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Promotions.PROMOTION_SHOW_FROM)));
                Date toDate = new Date(cursor.getLong(cursor.getColumnIndex(IgniteContract.Promotions.PROMOTION_SHOW_TO)));

                if(currentDate.after(fromDate) && currentDate.before(toDate)) {
                    byte[] imageData = cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA));
                    if (imageData != null) {
                        PromotionBean promotion = new PromotionBean();
                        promotion.setImage(imageData);
                        promotion.setRsvpLink(cursor.getString(cursor.getColumnIndex(IgniteContract.Promotions.PROMOTION_RSVP_LINK)));
                        promotion.setShow_from(cursor.getLong(cursor.getColumnIndex(IgniteContract.Promotions.PROMOTION_SHOW_FROM)));
                        promotion.setShow_to(cursor.getLong(cursor.getColumnIndex(IgniteContract.Promotions.PROMOTION_SHOW_TO)));
                        promotions.add(promotion);
                    }
                }
            } while (cursor.moveToNext());
        }else {
            Log.d(CLASS_NAME, "Cursor returned no results!");
        }

        Log.d(CLASS_NAME, "Exiting initDataSet");
    }

    public void onContinue(View v) {
        Intent intent = new Intent(this, LoginTutorial02Activity.class);
        this.startActivity(intent);
        finish();

    }

    public void onRsvp(View v) {

        //TODO replace with actual URL from database
        String url = "http://www.sirius.com";

        //Toast.makeText(getApplicationContext(), "RSVP clicked...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        this.startActivity(intent);
    }


}
