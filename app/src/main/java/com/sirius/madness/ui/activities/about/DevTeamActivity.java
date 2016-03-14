package com.sirius.madness.ui.activities.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.beans.DeveloperBean;
import com.sirius.madness.ui.activities.BaseActivity;

import java.util.ArrayList;

public class DevTeamActivity extends BaseActivity {

    private LinearLayout mGetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_team);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DevTeamFragment())
                    .commit();
        }*/
        LinearLayout mobileAd = (LinearLayout) findViewById(R.id.mobile_ad);
        mobileAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevTeamActivity.this, MobileDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView arrow = (TextView) findViewById(R.id.rightArrow);
        arrow.setText(getResources().getString(R.string.custom_font_icon_right_arrow));

        addDevelopers();
    }

    public void addDevelopers(){

        LinearLayout holder = (LinearLayout) findViewById(R.id.devContainer);
        LayoutInflater inflater = this.getLayoutInflater();

        View rowView;

        ArrayList<DeveloperBean> developers = initDevelopers();

        for(int i = 0; i < developers.size(); i++) {

            rowView = inflater.inflate(R.layout.layout_dev_team, holder, false);

            final DeveloperBean bean = developers.get(i);

            TextView nameView = (TextView) rowView.findViewById(R.id.DevName);
            nameView.setText(bean.getFirstName() + " " + bean.getLastName());

            TextView titleView = (TextView) rowView.findViewById(R.id.DevTitle);
            titleView.setText(bean.getTitle());

            TextView companyView = (TextView) rowView.findViewById(R.id.DevCompany);
            companyView.setText(bean.getCompany());
            Log.d("Company: ", bean.getCompany() + "");

            ImageView devPhoto = (ImageView) rowView.findViewById(R.id.DevPhoto);
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
                                getPackageManager().getPackageInfo("com.twitter.android", 0);
                                twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + bean.getTwitterId()));
                                twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            } catch (PackageManager.NameNotFoundException e) {
                                //twitter app not installed -> show in browser
                                twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + bean.getTwitterId()));
                            }
                        } else {
                            //we don't have a twitter id -> but we have a link so show in browser
                            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getTwitterLink()));
                        }
                        startActivity(twitterIntent);
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
                                getPackageManager().getPackageInfo("com.linkedin.android", 0);
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
                        startActivity(linkedInIntent);
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
                            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
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

            LinearLayout container = (LinearLayout) rowView.findViewById(R.id.container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGetConnected = (LinearLayout) v.findViewById(R.id.GetConnected);
                    if(mGetConnected.getVisibility()==View.VISIBLE){
                        mGetConnected.setVisibility(View.GONE);
                    }else {
                        mGetConnected.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.addView(rowView);
        }
    }

    private ArrayList<DeveloperBean> initDevelopers(){
        ArrayList<DeveloperBean> developers = new ArrayList<DeveloperBean>();

        DeveloperBean bean1 = new DeveloperBean();
        bean1.setFirstName("Niranjan");
        bean1.setLastName("Karam");
        bean1.setCompany("Sirius Computer Solutions");
        bean1.setEmail("Niranjan.Karam@siriuscom.com");
        bean1.setTwitterLink("https://twitter.com/karmanj");
        bean1.setTwitterId("karamnj");
        bean1.setLinkedInId("274639836");
        bean1.setLinkedInLink("http://in.linkedin.com/in/niranjan");
        bean1.setTitle("Consultant");
        bean1.setImage("developer_3001");
        DeveloperBean bean2 = new DeveloperBean();
        bean2.setFirstName("Chintan");
        bean2.setLastName("Shah");
        bean2.setCompany("Sirius Computer Solutions");
        bean2.setEmail("Chintan.Shah@siriuscom.com");
        bean2.setTwitterLink("https://twitter.com/ckshah7");
        bean2.setTwitterId("ckshah7");
        bean2.setLinkedInId("274639836");
        bean2.setLinkedInLink("http://in.linkedin.com/in/iamcks");
        bean2.setTitle("Consultant");
        bean2.setImage("developer_3002");
        DeveloperBean bean3 = new DeveloperBean();
        bean3.setFirstName("Antwae");
        bean3.setLastName("Mangaroo");
        bean3.setCompany("Sirius Computer Solutions");
        bean3.setEmail("Antwae.mangaroo@siriuscom.com");
        bean3.setTwitterLink("https://www.twitter.com/twaedizzle");
        bean3.setTwitterId("twaedizzle");
        bean3.setLinkedInId("274639836");
        bean3.setLinkedInLink("http://www.linkedin.com/in/antwaemangaroo");
        bean3.setTitle("Consultant");
        bean3.setImage("developer_3003");
        DeveloperBean bean4 = new DeveloperBean();
        bean4.setFirstName("David");
        bean4.setLastName("Laffranchi");
        bean4.setCompany("Sirius Computer Solutions");
        bean4.setEmail("David.Laffranchi@siriuscom.com");
        bean4.setTwitterLink("");
        bean4.setTwitterId("");
        bean4.setLinkedInId("274639836");
        bean4.setLinkedInLink("http://www.linkedin.com/in/davidlaffranchi");
        bean4.setTitle("Consultant");
        bean4.setImage("developer_3004");

        developers.add(bean1);
        developers.add(bean2);
        developers.add(bean3);
        developers.add(bean4);

        return developers;
    }

    private Drawable getDrawable(String name) {
        int resourceId =  getResources().getIdentifier(name, "drawable", getPackageName());
        return getResources().getDrawable(resourceId);
    }


    /*public static class DevTeamFragment extends ListFragment {

        private static final String CLASS_NAME = DevTeamFragment.class.getSimpleName();

        private LinearLayout mGetConnected;
        private int mPosition;
        private int mCount;

        private DevTeamAdapter mListAdapter;

        *//*private final String[] mProjection = {
                IgniteContract.Developers.DEVELOPER_ID,
                IgniteContract.Developers.DEVELOPER_FIRST_NAME,
                IgniteContract.Developers.DEVELOPER_LAST_NAME,
                IgniteContract.Partners.PARTNER_SHORT_TITLE,
                IgniteContract.Developers.DEVELOPER_EMAIL,
                IgniteContract.Developers.DEVELOPER_TWITTER_LINK,
                IgniteContract.Developers.DEVELOPER_TWITTER_ID,
                IgniteContract.Developers.DEVELOPER_LINKED_IN_ID,
                IgniteContract.Developers.DEVELOPER_LINKED_IN_LINK,
                IgniteContract.Developers.DEVELOPER_DESIGNATION,
                IgniteContract.ImageBinary.IMAGE_DATA
        };*//*

        public DevTeamFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            mGetConnected = null;
            mPosition = -1;
            mCount = 0;

            *//*Cursor cursor = getActivity().getContentResolver().query(IgniteContract.Developers.CONTENT_URI, mProjection, null, null, IgniteContract.Developers.DEVELOPER_ID);
            cursor.moveToFirst();

            mCount = cursor.getCount();*//*

            List developers = new ArrayList<DeveloperBean>();

            DeveloperBean bean1 = new DeveloperBean();
            bean1.setFirstName("Niranjan");
            bean1.setLastName("Karam");
            bean1.setCompany("Sirius Computer Solutions");
            bean1.setEmail("Niranjan.Karam@siriuscom.com");
            bean1.setTwitterLink("https://twitter.com/karmanj");
            bean1.setTwitterId("karamnj");
            bean1.setLinkedInId("274639836");
            bean1.setLinkedInLink("http://in.linkedin.com/in/niranjan");
            bean1.setTitle("Consultant");
            bean1.setImage("developer_3001");
            DeveloperBean bean2 = new DeveloperBean();
            bean2.setFirstName("Chintan");
            bean2.setLastName("Shah");
            bean2.setCompany("Sirius Computer Solutions");
            bean2.setEmail("Chintan.Shah@siriuscom.com");
            bean2.setTwitterLink("https://twitter.com/ckshah7");
            bean2.setTwitterId("ckshah7");
            bean2.setLinkedInId("274639836");
            bean2.setLinkedInLink("http://in.linkedin.com/in/iamcks");
            bean2.setTitle("Consultant");
            bean2.setImage("developer_3002");
            DeveloperBean bean3 = new DeveloperBean();
            bean3.setFirstName("Antwae");
            bean3.setLastName("Mangaroo");
            bean3.setCompany("Sirius Computer Solutions");
            bean3.setEmail("Antwae.mangaroo@siriuscom.com");
            bean3.setTwitterLink("https://www.twitter.com/twaedizzle");
            bean3.setTwitterId("twaedizzle");
            bean3.setLinkedInId("274639836");
            bean3.setLinkedInLink("http://www.linkedin.com/in/antwaemangaroo");
            bean3.setTitle("Consultant");
            bean3.setImage("developer_3003");
            DeveloperBean bean4 = new DeveloperBean();
            bean4.setFirstName("David");
            bean4.setLastName("Laffranchi");
            bean4.setCompany("Sirius Computer Solutions");
            bean4.setEmail("David.Laffranchi@siriuscom.com");
            bean4.setTwitterLink("");
            bean4.setTwitterId("");
            bean4.setLinkedInId("274639836");
            bean4.setLinkedInLink("http://www.linkedin.com/in/davidlaffranchi");
            bean4.setTitle("Consultant");
            bean4.setImage("developer_3004");

            developers.add(bean1);
            developers.add(bean2);
            developers.add(bean3);
            developers.add(bean4);
            mCount = developers.size();

            *//*
            if (cursor.getCount() > 0) {
                do {
                    DeveloperBean bean = new DeveloperBean();
                    bean.setFirstName(cursor.getString(1));
                    bean.setLastName(cursor.getString(2));
                    bean.setCompany(cursor.getString(3));
                    bean.setEmail(cursor.getString(4));
                    bean.setTwitterLink(cursor.getString(5));
                    bean.setTwitterId(cursor.getString(6));
                    bean.setLinkedInId(cursor.getString(7));
                    bean.setLinkedInLink(cursor.getString(8));
                    bean.setTitle(cursor.getString(9));
                    bean.setImage(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                    developers.add(bean);
                } while (cursor.moveToNext());
            }*//*
            View rootView = inflater.inflate(R.layout.fragment_dev_team, container, false);
            mListAdapter = new DevTeamAdapter(getActivity(), R.layout.layout_dev_team, developers);

            setListAdapter(mListAdapter);

            return rootView;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            if (mGetConnected != null) {
                mGetConnected.setVisibility(View.GONE);
            }

            if (this.mPosition != position) {
                this.mPosition = position;
                mGetConnected = (LinearLayout) v.findViewById(R.id.GetConnected);
                mGetConnected.setVisibility(View.VISIBLE);
            } else {
                this.mPosition = -1;
            }

            //Selects the last row if it is clicked so that you can see the expanded view.
            if (this.mPosition == mCount -1) {
                l.setSelection(this.mPosition);
            }
        }


    }*/
}
