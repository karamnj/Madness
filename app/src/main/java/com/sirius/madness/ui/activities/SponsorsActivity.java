package com.sirius.madness.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.SponsorBean;
import com.sirius.madness.provider.IgniteContract;

import java.util.ArrayList;
import java.util.List;

public class SponsorsActivity extends BaseActivity {

    private static final String CLASS_NAME = "SponsorsActivity";

    private String[] projectionList = {
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.ImageMetaData.IMAGE_HEIGHT,
            IgniteContract.ImageMetaData.IMAGE_WIDTH,
            IgniteContract.Sponsors.SPONSOR_NAME,
            IgniteContract.Sponsors.SPONSOR_WEBSITE_LINK,
            IgniteContract.Sponsors.SPONSOR_CATEGORY
    };

    private List<SponsorBean> sponsors0, sponsors1, sponsors2, sponsors3, sponsors4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors);
        isFirstLevelActivity = true;
        Cursor cursor = getContentResolver().query(IgniteContract.Sponsors.CONTENT_URI,projectionList,null,null,IgniteContract.Sponsors.SPONSOR_CATEGORY);
        sponsors0 = new ArrayList<SponsorBean>();
        sponsors1 = new ArrayList<SponsorBean>();
        sponsors2 = new ArrayList<SponsorBean>();
        initDataSet(cursor);

        LinearLayout holder = (LinearLayout) findViewById(R.id.sponsor_holder);

        LayoutInflater inflater = this.getLayoutInflater();

        View row;
        for(int i = 2; i >= 0; i--) {
            row = inflater.inflate(R.layout.layout_sponsors, holder, false);
            List<ImageView> spon = new ArrayList<ImageView>();
            GridView gridview = (GridView) row.findViewById(R.id.gridview);
            TextView header = (TextView) row.findViewById(R.id.sponsorHeader);
            LinearLayout bg = (LinearLayout) row.findViewById(R.id.sponsorItemBackground);
            switch (i){
                case 0:
                    header.setText("Orange Level");
                    gridview.setAdapter(new ImageAdapter(this, sponsors0));
                    bg.setBackgroundColor(getResources().getColor(R.color.theme_tertiary_dark_orange));
                    break;
                case 1:
                    header.setText("White Level");
                    header.setTextColor(getResources().getColor(R.color.black));
                    gridview.setAdapter(new ImageAdapter(this, sponsors1));
                    bg.setBackgroundColor(getResources().getColor(R.color.white));
                    break;
                case 2:
                    header.setText("Green Level");
                    gridview.setAdapter(new ImageAdapter(this, sponsors2));
                    bg.setBackgroundColor(getResources().getColor(R.color.theme_tertiary_dark_green));
                    break;
            }
            holder.addView(row);
        }
        View margin = new View(this);
        margin.setMinimumHeight(100);
        holder.addView(margin);
    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<SponsorBean> sponsorsInd;

        public ImageAdapter(Context c, List<SponsorBean> sInd) {
            mContext = c;
            sponsorsInd = sInd;
        }

        public int getCount() {
            return sponsorsInd.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,180,Gravity.CENTER_HORIZONTAL));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(20, 20, 0, 20);
            } else {
                imageView = (ImageView) convertView;
            }

            final SponsorBean sb = (SponsorBean) sponsorsInd.get(position);

            Bitmap bMap = BitmapFactory.decodeByteArray(sb.getImage(), 0, sb.getImage().length);
            Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
            imageView.setImageDrawable(image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SponsorsActivity.this, SponsorsWebViewActivity.class);

                    intent.putExtra("webLink", sb.getWebLink());

                    SponsorsActivity.this.startActivity(intent);
                }
            });
            //imageView.setImageResource(mThumbIds[position]);
            return imageView;
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
                    sponsor.setCategory(cursor.getInt(cursor.getColumnIndex(IgniteContract.Sponsors.SPONSOR_CATEGORY)));
                    switch(sponsor.getCategory()){
                        case 0:
                            sponsors0.add(sponsor);
                            break;
                        case 1:
                            sponsors1.add(sponsor);
                            break;
                        case 2:
                            sponsors2.add(sponsor);
                            break;
                    }
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
