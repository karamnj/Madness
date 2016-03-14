package com.sirius.madness.ui.activities.about;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.activities.BaseActivity;

public class ContactUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ContactUsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContactUsFragment extends Fragment {

        private String email;
        private String phoneNumber;
        private String linkedIn;
        private String twitter;

        private static final String CLASS_NAME = ContactUsFragment.class.getSimpleName();

        private final String[] mProjection = {
                IgniteContract.AvnetServices.AVNET_SERVICE_SHORT_TITLE,
                IgniteContract.AvnetServices.AVNET_SERVICE_LONG_TITLE,
                IgniteContract.AvnetServices.AVNET_SERVICE_SHORT_DESCRIPTION,
                IgniteContract.AvnetServices.AVNET_SERVICE_EMAIL,
                IgniteContract.AvnetServices.AVNET_SERVICE_LINKED_IN_LINK,
                IgniteContract.AvnetServices.AVNET_SERVICE_TWITTER_HASH_TAG,
                IgniteContract.AvnetServices.AVNET_SERVICE_PHONE,
                IgniteContract.AvnetServices.AVNET_SERVICE_WEBSITE_LINK,
                IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_TITLE,
                IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_LINE_1,
                IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_LINE_2
        };

        public ContactUsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Cursor cursor = getActivity().getContentResolver().query(IgniteContract.AvnetServices.CONTENT_URI, mProjection, null, null, null);
            cursor.moveToFirst();

            View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);

            TextView contactHeader1 = (TextView) rootView.findViewById(R.id.contact_header1);
            TextView contactDisc = (TextView) rootView.findViewById(R.id.contact_disc);
            TextView contactEmail = (TextView) rootView.findViewById(R.id.contact_email);
            TextView addressTitle = (TextView) rootView.findViewById(R.id.contact_addressTitle);
            TextView shortTitle = (TextView) rootView.findViewById(R.id.contact_shortTitle);
            TextView addressLine1 = (TextView) rootView.findViewById(R.id.contact_addressLine1);
            TextView addressLine2 = (TextView) rootView.findViewById(R.id.contact_addressLine2);
            TextView phoneView = (TextView) rootView.findViewById(R.id.contact_phone);
            Button twitterLink = (Button) rootView.findViewById(R.id.TwitterLink);
            Button linkedInLink = (Button) rootView.findViewById(R.id.LinkedInLink);

            if (cursor.getCount() > 0) {
                contactHeader1.setText(cursor.getString(1));
                contactDisc.setText(cursor.getString(2));
                contactEmail.setText(cursor.getString(3));
                addressTitle.setText(cursor.getString(8));
                shortTitle.setText(cursor.getString(0));
                addressLine1.setText(cursor.getString(9));
                addressLine2.setText(cursor.getString(10));
                phoneView.setText(cursor.getString(6));

                email=cursor.getString(2);
                phoneNumber=cursor.getString(6);
                twitter=cursor.getString(5);
                linkedIn=cursor.getString(4);
            }else{
                Log.d(CLASS_NAME, "Cursor returned No Result");
            }
            /*
                contactHeader1.setText("Sirius Computer Solutions");//cursor.getString(1));
                contactDisc.setText("Sirius Computer Solutions is a national IT solutions integrator dedicated to helping clients implement advanced infrastructure solutions built around industry-leading servers, storage, networking, software and financing, and supporting them with a comprehensive suite of infrastructure and software consulting services. Sirius is a private company.");//cursor.getString(2));
                contactEmail.setText("support@siriuscom.com");//cursor.getString(3));
                addressTitle.setText("San Antonio, TX");//cursor.getString(8));
                shortTitle.setText("Sirius Computer Solutions");//cursor.getString(0));
                addressLine1.setText("10100 Reunion Place, Suite 500");//cursor.getString(9));
                addressLine2.setText("San Antonio, TX 78216");//cursor.getString(10));
                phoneView.setText("+1-800-460-1237");//cursor.getString(6));

                email="support@siriuscom.com";//cursor.getString(2);
                phoneNumber="+1-800-460-1237";//cursor.getString(6);
                twitter="http://twitter.com/SiriusNews";//cursor.getString(5);
                linkedIn="http://www.linkedin.com/company/sirius-computer-solutions";//cursor.getString(4);
            */

            phoneView.setPaintFlags(phoneView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            contactEmail.setPaintFlags(contactEmail.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            //Clicking on e-mail allows user to choose e-mail program to use
            contactEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: Email", Toast.LENGTH_SHORT).show();
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", email, null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });

            //Perform call on click of number
            phoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber)));
                }
            });

            //Clicking on LinkedIn takes user to LinkedIn in external browser
            linkedInLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (linkedIn != null && !linkedIn.isEmpty()) {
                        //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: LinkedIn", Toast.LENGTH_SHORT).show();

                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedIn));
                        startActivity(webIntent);
                    } else {
                        //Toast.makeText(SpeakerInfoActivity.this, "LinkedIn URL not provided", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Clicking on Twitter takes user to Twitter in external browser
            twitterLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (twitter != null && !twitter.isEmpty()) {
                        //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: Twitter", Toast.LENGTH_SHORT).show();

                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                        startActivity(webIntent);
                    } else {
                        //Toast.makeText(SpeakerInfoActivity.this, "Twitter URL not provided", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return rootView;
        }
    }
}