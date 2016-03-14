package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.SponsorBean;

import java.util.List;

/**
 * Created by 915649 on 16/02/15.
 */
public class SponsorsAdapter extends ArrayAdapter {

    private List<SponsorBean> sponsors;
    private Context mContext;
    private int mResource;

    public SponsorsAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        sponsors = objects;
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View rowView = inflater.inflate(mResource, null, false);

        LinearLayout sponsorItemBackground = (LinearLayout) rowView.findViewById(R.id.sponsorItemBackground);
        /*int color;
        if(position%2==0){
            color = getColorForCell(0);
        }else{
            color = getColorForCell(1);
        }*/
        sponsorItemBackground.setBackgroundResource(R.color.theme_secondary_base);

        final SponsorBean bean = sponsors.get(position);

        final ImageView sponsorPromotionImage = (ImageView)rowView.findViewById(R.id.sponsorPromotionImage);
        final TextView sponsorPromotionText = (TextView)rowView.findViewById(R.id.sponsorPromotionText);
        ViewTreeObserver vto = sponsorPromotionImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                sponsorPromotionImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = sponsorPromotionImage.getMeasuredWidth();
                sponsorPromotionImage.getLayoutParams().height = (bean.getImageHeight() * finalWidth / bean.getImageWidth());
                return true;
            }
        });
        Bitmap bMap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);
        Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        sponsorPromotionImage.setImageDrawable(image);
        sponsorPromotionText.setText(bean.getName());

        return rowView;
    }
}
