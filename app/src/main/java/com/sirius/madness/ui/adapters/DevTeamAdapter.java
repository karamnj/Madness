package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.DeveloperBean;

import java.util.List;

/**
 * Created by 038638 on 1/22/15
 */
public class DevTeamAdapter extends ArrayAdapter {

    private static final String CLASS_NAME = DevTeamAdapter.class.getSimpleName();

    private List<DeveloperBean> mObjects;
    private int mResource;
    private Context mContext;

    public DevTeamAdapter(Context context, int resource, List objects) {

        super(context, resource, objects);
        Log.v(CLASS_NAME, "DevTeamAdapter(...)");
        this.mObjects = objects;
        this.mResource = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.v(CLASS_NAME, "getView(...)");

        //super.getView(position, convertView, parent);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View rowView = inflater.inflate(mResource, parent, false);

        final DeveloperBean bean = mObjects.get(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.DevName);
        nameView.setText(bean.getFirstName() + " " + bean.getLastName());

        TextView titleView = (TextView) rowView.findViewById(R.id.DevTitle);
        titleView.setText(bean.getTitle());

        TextView companyView = (TextView) rowView.findViewById(R.id.DevCompany);
        companyView.setText(bean.getCompany());
        Log.d("Company: ",bean.getCompany()+"");

        ImageView devPhoto = (ImageView)rowView.findViewById(R.id.DevPhoto);
        devPhoto.setImageDrawable(getDrawable(bean.getImage()));
        //Bitmap bMap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);
        //Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        //devPhoto.setImageDrawable(image);

        //Set up Twitter icon
        ImageView twitter = (ImageView) rowView.findViewById(R.id.DevTwitter);
        if ((bean.getTwitterLink() != null && !bean.getTwitterLink().equals("")) ||
                (bean.getTwitterId() != null && !bean.getTwitterId().equals(""))) {
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //* http://stackoverflow.com/a/21088285 -> however use screen_name instead of user_id
                    //Toast.makeText(mContext,"twitter-id: " + bean.getTwitterId() + " link: " + bean.getTwitterLink(), Toast.LENGTH_SHORT).show();
                    Intent twitterIntent;
                    if (bean.getTwitterId() != null && !bean.getTwitterId().equals("")) {
                        try {
                            mContext.getPackageManager().getPackageInfo("com.twitter.android", 0);
                            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + bean.getTwitterId()));
                            twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (PackageManager.NameNotFoundException e) {
                            //twitter app not installed -> show in browser
                            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" +bean.getTwitterId()));
                        }
                    } else {
                        //we don't have a twitter id -> but we have a link so show in browser
                        twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getTwitterLink()));
                    }
                    mContext.startActivity(twitterIntent);
                }
            });
        } else {
           twitter.setVisibility(View.GONE);
        }

        //Set up Linked In icon
        ImageView linkedIn = (ImageView) rowView.findViewById(R.id.DevLinkedIn);
        if (bean.getLinkedInLink() != null && !bean.getLinkedInLink().equals("") ||
                (bean.getLinkedInId() != null && !bean.getLinkedInId().equals(""))) {
            linkedIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "lin id: " + bean.getLinkedInId() + " link: " + bean.getLinkedInLink(), Toast.LENGTH_SHORT).show();
                    Intent linkedInIntent;
                    if (bean.getLinkedInId() != null && !bean.getLinkedInId().equals("")) {
                        try {
                            mContext.getPackageManager().getPackageInfo("com.linkedin.android", 0);
                            linkedInIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/" + bean.getLinkedInId()));
                            linkedInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (PackageManager.NameNotFoundException e) {
                            //linked in app not installed -> show in browser
                            linkedInIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/profile/view?id=" + bean.getLinkedInId()));
                        }
                    } else {
                        //we don't have a linked in id -> but we have a link so show in browser
                        linkedInIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getLinkedInLink()));
                    }
                    mContext.startActivity(linkedInIntent);
                }
            });
        } else {
            linkedIn.setVisibility(View.GONE);
        }

        ImageView email = (ImageView) rowView.findViewById(R.id.DevEmail);
        if (bean.getEmail() != null && !bean.getEmail().equals("")) {
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Send Email
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{bean.getEmail()});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Avnet Ignite Android");
                    try {
                        if (emailIntent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(Intent.createChooser(emailIntent, mContext.getString(R.string.send_mail)));
                        } else {
                            //Toast.makeText(mContext, mContext.getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
                        }
                    } catch (android.content.ActivityNotFoundException ex) {
                        //Toast.makeText(mContext, mContext.getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            email.setVisibility(View.GONE);
        }

        return rowView;
    }
    private Drawable getDrawable(String name) {
        int resourceId =  mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
        return mContext.getResources().getDrawable(resourceId);
        //int res = mContext.getResources().getIdentifier(mContext.getPackageName()+":drawable/developer_3001", null, null);
        //return mContext.getResources().getDrawable(res);
    }
}
