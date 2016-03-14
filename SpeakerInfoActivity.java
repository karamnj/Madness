package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.views.CustomAnimationTextView;
import com.sirius.madness.ui.views.CustomFontTextView;

public class SpeakerInfoActivity extends BaseActivity {

    private static final String CLASS_NAME = "SpeakerInfoActivity";

    private TextView speakerFirstName;
    private TextView speakerLastName;
    private TextView speakerDesignation;
    private CustomFontTextView speakerEmail;
    private ImageView speakerImage;
    private CustomFontTextView speakerLinkedIn;
    private CustomFontTextView speakerTwitter;
    private TextView speakerBioButton;
    private TextView speakerOrganisation;
    private CustomAnimationTextView speakerBioDescription;
    private LinearLayout speakerBioDescriptionContainer;
    private ImageView mContainer;

    private String email;
    private String linkedIn;
    private String twitter;

    private boolean isBioDisplayed = false;

    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_info);

        Intent i = getIntent();
        long presenterId = Long.parseLong(i.getStringExtra("presenterId"));

        String [] projectionList= {IgniteContract.Presenters.PRESENTER_FIRST_NAME,
                IgniteContract.Presenters.PRESENTER_LAST_NAME,
                IgniteContract.Presenters.PRESENTER_DESIGNATION,
                IgniteContract.Presenters.PRESENTER_ID,
                IgniteContract.Presenters.PRESENTER_EMAIL,
                IgniteContract.Presenters.PRESENTER_IMAGE,
                IgniteContract.Presenters.PRESENTER_LINKED_IN_LINK,
                IgniteContract.Presenters.PRESENTER_TWITTER_LINK,
                IgniteContract.Partners.PARTNER_LONG_TITLE,
                IgniteContract.Presenters.PRESENTER_LONG_DESCRIPTION,
                IgniteContract.ImageBinary.IMAGE_DATA
        };
        Cursor cursor = getContentResolver().query(IgniteContract.Presenters.buildPresenterUri(presenterId),projectionList,null,null, null);
        //pass the presenterId in the above line IgniteContract.Presenters.buildPresenterUri(presenterId); cursor gives the required data
        mContainer = (ImageView)findViewById(R.id.speakerInfoContainer);
        speakerFirstName = (TextView) findViewById(R.id.speakerInfoFirstName);
        speakerLastName = (TextView) findViewById(R.id.speakerInfoLastName);
        speakerDesignation = (TextView) findViewById(R.id.speakerInfoTitle);
        speakerEmail = (CustomFontTextView) findViewById(R.id.speakerInfoEmail);
        speakerImage = (ImageView) findViewById(R.id.speakerInfoContainer);
        speakerLinkedIn = (CustomFontTextView) findViewById(R.id.speakerInfoLinkedIn);
        speakerTwitter = (CustomFontTextView) findViewById(R.id.speakerInfoTwitter);
        speakerOrganisation = (TextView)findViewById(R.id.speakerInfoOrganization);
        speakerBioDescription = (CustomAnimationTextView) findViewById(R.id.speakerBioDescription);
        speakerBioDescriptionContainer = (LinearLayout) findViewById(R.id.speakerInfoBioDescriptionContainer);

        speakerBioDescription.setMovementMethod(new ScrollingMovementMethod());

        speakerBioButton = (TextView) findViewById(R.id.speakerBioButton);

        //Clicking on e-mail allows user to choose e-mail program to use
        speakerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: Email", Toast.LENGTH_SHORT).show();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",email, null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });


        //Clicking on LinkedIn takes user to LinkedIn in external browser
        speakerLinkedIn.setOnClickListener(new View.OnClickListener() {
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
        speakerTwitter.setOnClickListener(new View.OnClickListener() {
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

        //Clicking Bio button shows speaker's biography (including animation transition)
        speakerBioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: Bio button", Toast.LENGTH_SHORT).show();

                Animation bioAnimationIn = AnimationUtils.loadAnimation(SpeakerInfoActivity.this, R.anim.fade_out);
                bioAnimationIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Log.d(CLASS_NAME, "Starting animation");
                        isBioDisplayed = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.d(CLASS_NAME, "Ending animation");
                        findViewById(R.id.speakerInfoButtonContainer).setVisibility(View.GONE);
                        findViewById(R.id.speakerInfoTitle).setVisibility(View.GONE);
                        findViewById(R.id.speakerInfoOrganization).setVisibility(View.GONE);
                        findViewById(R.id.speakerInfoBioButtonContainer).setVisibility(View.GONE);
                        findViewById(R.id.speakerInfoBioDescriptionContainer).setVisibility(View.VISIBLE);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) speakerBioDescriptionContainer.getLayoutParams();
                        params.weight = 7f;

                        speakerBioDescriptionContainer.setLayoutParams(params);

                        speakerBioDescription.show();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        Log.d("IGNITE", "Looping");
                    }
                });

                //speakerBioButton.startAnimation(bioAnimationIn);

                return true;
            }
        });

        initDataset(cursor);

        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    private void initDataset(Cursor cursor) {
        cursor.moveToFirst();
        do {
            speakerFirstName.setText(cursor.getString(0).toUpperCase());
            speakerLastName.setText(cursor.getString(1).toUpperCase());
            speakerDesignation.setText(cursor.getString(2).toUpperCase());
            email = cursor.getString(4);
            byte[] imageData = cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA));

            //Load image only if image can be deciphered from Bluemix
            if(imageData != null) {
                Bitmap bMap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                //Drawable image = new BitmapDrawable(this.getResources(), toGrayscale(bMap));
                Drawable image = new BitmapDrawable(this.getResources(), bMap);
                mContainer.setBackground(image);
            }else {
                Log.d(CLASS_NAME, "imageData is null");
            }

            linkedIn = cursor.getString(6);
            twitter = cursor.getString(7);
            speakerOrganisation.setText(cursor.getString(8));
            speakerBioDescription.setText(cursor.getString(9));
        }while (cursor.moveToNext());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isBioDisplayed) {
            //speakerBioDescription.hide();
            //findViewById(R.id.speakerInfoBioButtonContainer).setVisibility(View.VISIBLE);
            //findViewById(R.id.speakerInfoTextContainer).setVisibility(View.VISIBLE);

            Log.d("IGNITE", "Loading animation");

            Animation bioAnimationOut = AnimationUtils.loadAnimation(SpeakerInfoActivity.this, R.anim.fade_in);
            bioAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.d("IGNITE", "Setting isBioDisplayed to false");
                    isBioDisplayed = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.d("IGNITE", "Showing hidden containers");
                    findViewById(R.id.speakerInfoButtonContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.speakerInfoTitle).setVisibility(View.VISIBLE);
                    findViewById(R.id.speakerInfoOrganization).setVisibility(View.VISIBLE);
                    findViewById(R.id.speakerInfoBioButtonContainer).setVisibility(View.VISIBLE);
                    speakerBioDescription.hide();
                    findViewById(R.id.speakerInfoBioDescriptionContainer).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.d("IGNITE", "Looping");
                }
            });

            Log.d("IGNITE", "Starting animation");
            speakerBioButton.startAnimation(bioAnimationOut);
            //speakerBioDescriptionContainer.startAnimation(bioAnimationOut);

            return true;
        }else {
            Log.d("IGNITE", "Returning Back key");
            return super.onKeyDown(keyCode, event);
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("IGNITE", "onFling...");
            if(e2.getX() < e1.getX()) {
                /*Intent intent;
                intent = new Intent(SpeakerInfoActivity.this, SpeakerFeedActivity.class);
                //intent.putParcelableArrayListExtra("speakerList", speakerList);
                startActivity(intent);*/
                finish();
            }
            return true;
        }
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
