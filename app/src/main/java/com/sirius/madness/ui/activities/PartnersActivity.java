package com.sirius.madness.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.PartnerBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.PartnersAdapter;

import java.util.ArrayList;
import java.util.List;

public class PartnersActivity extends BaseActivity {

    private static final String CLASS_NAME = "PartnersActivity";

    private String[] projectionList = {
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.ImageMetaData.IMAGE_HEIGHT,
            IgniteContract.ImageMetaData.IMAGE_WIDTH,
            IgniteContract.Partners.PARTNER_WEBSITE_LINK
    };

    private List<PartnerBean> promotions;
    ListView partnerPromotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partners);
        isFirstLevelActivity = true;
        Cursor cursor = getContentResolver().query(IgniteContract.Partners.CONTENT_URI,projectionList,null,null,null);
        promotions = new ArrayList<PartnerBean>();
        initDataSet(cursor);

        partnerPromotions = (ListView) findViewById(R.id.partnersList);

        if(promotions != null && promotions.size() > 0) {

            PartnersAdapter adapter = new PartnersAdapter(this, R.layout.layout_partner_promotion, promotions);
            partnerPromotions.setAdapter(adapter);

        }else {
            partnerPromotions.setVisibility(ListView.GONE);
            TextView partnerError = (TextView) findViewById(R.id.partnersErrorMessage);
            partnerError.setVisibility(View.VISIBLE);
        }
    }

    public void initDataSet(Cursor cursor){
        Log.d(CLASS_NAME, "Entering initDataSet");

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                Log.d("Image ID",String.valueOf(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                byte[] imageData = cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA));
                if (imageData != null) {
                    PartnerBean partner = new PartnerBean();
                    partner.setImage(imageData);
                    partner.setImageHeight(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_HEIGHT)));
                    partner.setImageWidth(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_WIDTH)));
                    partner.setWebLink(cursor.getString(cursor.getColumnIndex(IgniteContract.Partners.PARTNER_WEBSITE_LINK)));
                    promotions.add(partner);
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
        return MENU_BAR_ITEM_PARTNERS;
    }
}
