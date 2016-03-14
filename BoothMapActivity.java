package com.sirius.madness.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

import com.sirius.madness.R;

public class BoothMapActivity extends BaseActivity {
    private static final String CLASS_NAME = "BoothMapActivity";

    private ImageView imageView;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_map);
        isFirstLevelActivity = true;

        ImageView touch = (ImageView)findViewById(R.id.boothMapImage);
        touch.setImageDrawable(getResources().getDrawable(R.drawable.mobtest));

        /*Bitmap bMap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);
        Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        sponsorPromotionImage.setImageDrawable(image)*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /*public void initDataSet(Cursor cursor){
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
    }*/
}
