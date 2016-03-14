package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sirius.madness.R;
import com.sirius.madness.beans.PartnerBean;
import com.sirius.madness.ui.activities.PartnersActivity;
import com.sirius.madness.ui.activities.PartnersWebViewActivity;

import java.util.List;

/**
 * Created by 915649 on 16/02/15.
 */
public class PartnersAdapter extends ArrayAdapter {

    private List<PartnerBean> partners;
    private Context mContext;
    private int mResource;

    public PartnersAdapter(Context context,int resource, List objects) {
        super(context, resource, objects);
        partners = objects;
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View rowView = inflater.inflate(mResource, null, false);

        final PartnerBean bean = partners.get(position);

        final ImageView partnerPromotionImage = (ImageView)rowView.findViewById(R.id.partnerPromotionImage);
        ViewTreeObserver vto = partnerPromotionImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                partnerPromotionImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = partnerPromotionImage.getMeasuredWidth();
                partnerPromotionImage.getLayoutParams().height = (bean.getImageHeight()*finalWidth/bean.getImageWidth());
                return true;
            }
        });
        Bitmap bMap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);
        Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        partnerPromotionImage.setImageDrawable(image);

        if(partnerPromotionImage != null) {
            partnerPromotionImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("IGNITE", "onClick...");
                    Intent intent = new Intent((PartnersActivity) mContext, PartnersWebViewActivity.class);
                    //intent.setAction(Intent.ACTION_VIEW);
                    //intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    //intent.setData(Uri.parse(bean.getWebLink()));

                    intent.putExtra("webLink", bean.getWebLink());

                    ((Activity) mContext).startActivity(intent);
                }
            });
        }

        return rowView;
    }
}
