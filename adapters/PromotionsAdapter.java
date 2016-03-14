package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.sirius.madness.R;
import com.sirius.madness.beans.PromotionBean;
import com.sirius.madness.ui.activities.LoginTutorial02Activity;

import java.util.List;

/**
 * Created by admin on 20/10/15.
 */
public class PromotionsAdapter extends ArrayAdapter {

    private List<PromotionBean> promotions;
    private Context mContext;
    private int mResource;

    public PromotionsAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        promotions = objects;
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View rowView = inflater.inflate(mResource, null, false);

        ImageView promoImage = (ImageView) rowView.findViewById(R.id.imagePromotion);
        Button continueBtn = (Button) rowView.findViewById(R.id.continueBtn);
        Button rsvpBtn = (Button) rowView.findViewById(R.id.rsvpBtn);
        final ListView listView = (ListView) ((Activity) mContext).findViewById(R.id.promotionList);

        Integer size = promotions.size();

        final PromotionBean bean = promotions.get(position);

        if(!bean.getRsvpLink().equals("")){
            rsvpBtn.setEnabled(true);
            rsvpBtn.setVisibility(View.VISIBLE);
            rsvpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getRsvpLink()));
                    mContext.startActivity(intent);
                }
            });
        }else{
            rsvpBtn.setEnabled(false);
            rsvpBtn.setVisibility(View.GONE);
        }
        //Image settings
        Bitmap bMap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);
        Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        promoImage.setImageDrawable(image);

        if(position==(size-1)){
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginTutorial02Activity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }
            });
        }else {
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.smoothScrollToPosition(position+1);
                }
            });
        }

        return rowView;
    }
}