package com.sirius.madness.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.adapters.DiscoverAdapter;
import com.sirius.madness.util.SocialMediaLoginMethods;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class DiscoverActivity extends BaseActivity {

    private static final String CLASS_NAME = "DiscoverActivity";

    private static final String DISCOVER_UPDATES = "Discover Updates";
    private DiscoverAdapter discoverAdapter;
    private SessionBean session_data[];
    private ListView discoverListView;
    SharedPreferences mPrefs;

    private final String[] projectionList = {
            IgniteContract.Sessions.SESSION_ID,
            IgniteContract.Sessions.SESSION_IMAGE,
            IgniteContract.Sessions.SESSION_SHORT_TITLE,
            IgniteContract.Sessions.SESSION_LONG_TITLE,
            IgniteContract.Sessions.SESSION_LONG_DESCRIPTION,
            IgniteContract.Sessions.SESSION_HALL,
            IgniteContract.Sessions.SESSION_FROM_TIME,
            IgniteContract.Sessions.SESSION_TO_TIME,
            IgniteContract.Sessions.SESSION_IS_HIDDEN,
            IgniteContract.Categories.CATEGORY_NAME,
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.Schedules.CALENDAR_EVENT_ID
    };


    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_DISCOVER;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "Entering onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        isFirstLevelActivity = true;
        if (savedInstanceState == null) {

        }
        mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        discoverListView = (ListView) findViewById(R.id.discoverList);


        /*Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
        initDataset(cursor);

        //If we have session data, show it to the user
        if(session_data.length != 0) {
            Log.d(CLASS_NAME, "session_data is > 0");
            discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);
            discoverListView.setAdapter(discoverAdapter);
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            discoverListView.setVisibility(ListView.GONE);
            TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
            discoverError.setVisibility(View.VISIBLE);
            //discoverListView.setAdapter();
        }*/

        //refreshUI();

        //blApplication.bluemixPushSubscribeTo(DISCOVER_UPDATES);
        /*if (session_data.length == 0) {
            getBlueMixData();
        }*/

        Log.d(CLASS_NAME, "Exiting onCreate");
    }

    @Override
    public void refreshUI() {
        Log.d(CLASS_NAME, "Entering refreshUI");

        runOnUiThread(new Runnable() {
            public void run() {
                Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
                initDataset(cursor);
                if (session_data.length != 0) {
                    discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);

                    //HashMap<String, String> initLoginState = new HashMap<String, String>();

                    //discoverAdapter.setLoginState(initLoginState);
                    discoverListView.setAdapter(discoverAdapter);
                } else {
                    Log.d(CLASS_NAME, "session_data is null or empty");
                    discoverListView.setVisibility(ListView.GONE);
                    TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
                    discoverError.setVisibility(View.VISIBLE);
                    //discoverListView.setAdapter();
                }
            }
        });
        Intent intent = getIntent();

        Log.d(CLASS_NAME, "Exiting refreshUI");
/*
        finish();
        startActivity(intent);*/
    }

    public void initDataset(Cursor cursor) {

        Log.d(CLASS_NAME, "Entering initDataset");

        String scheduleProjection[] = {
                IgniteContract.Schedules.CALENDAR_EVENT_ID
        };
        session_data = new SessionBean[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        if (cursor.getCount() > 0) {
            do {
                Log.d(CLASS_NAME, cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)) + "");
                Log.d(CLASS_NAME, cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));

                SessionBean session = new SessionBean();
                session.setSessionId(cursor.getInt(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_ID)));
                session.setSessionName(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_TITLE)));
                session.setSessionCategory(cursor.getString(cursor.getColumnIndex(IgniteContract.Categories.CATEGORY_NAME)));
                session.setSessionDescription(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION)));
                session.setFromTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_FROM_TIME)));
                session.setToTime(cursor.getLong(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_TO_TIME)));
                session.setSessionBg(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
                session.setCalendarEventId(cursor.getString(cursor.getColumnIndex(IgniteContract.Schedules.CALENDAR_EVENT_ID)));
                session.setIsHidden(cursor.getString(cursor.getColumnIndex(IgniteContract.Sessions.SESSION_IS_HIDDEN)));
                session_data[i] = session;
                i++;
            } while (cursor.moveToNext());
        }

        Log.d(CLASS_NAME, "Exiting initDataset");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(CLASS_NAME, "Entering onCreateOptionsMenu");

        super.onCreateOptionsMenu(menu);

        Log.d(CLASS_NAME, "Exiting onCreateOptionsMenu");

        return true;
    }

    @Override
    protected void onResume() {
        Log.d(CLASS_NAME, "Entering onResume");

        super.onResume();
        refreshUI();
        /*setContentView(R.layout.activity_discover);
        isFirstLevelActivity = true;

        mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        discoverListView = (ListView) findViewById(R.id.discoverList);

        Cursor cursor = getContentResolver().query(IgniteContract.Sessions.CONTENT_URI, projectionList, null, null, IgniteContract.Sessions.SESSION_ID);
        initDataset(cursor);

        //If we have session data, show it to the user
        if(session_data.length != 0) {
            Log.d(CLASS_NAME, "session_data is > 0");
            discoverAdapter = new DiscoverAdapter(DiscoverActivity.this, R.layout.discover_layout, session_data);
            discoverListView.setAdapter(discoverAdapter);
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            discoverListView.setVisibility(ListView.GONE);
            TextView discoverError = (TextView) findViewById(R.id.discoverErrorMessage);
            discoverError.setVisibility(View.VISIBLE);
        }*/

        Log.d(CLASS_NAME, "Exiting onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CLASS_NAME, "Entering onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 55 && data != null) {
            Button loginButton;
            String loginButtonText;

            loginButton = (Button) findViewById(R.id.userName);

            if(resultCode == RESULT_OK) {

                loginButtonText = data.getStringExtra("loginButtonText");

                if(loginButtonText != null && !loginButtonText.isEmpty()) {
                    Log.d(CLASS_NAME, "Login text should be: 'Welcome, <username>!'");
                    String twitterString = data.getStringExtra("isTwitter");

                    if(data.getStringExtra("isTwitter") != null) {
                        boolean isTwitterService = Boolean.parseBoolean(data.getStringExtra("isTwitter"));

                        if(isTwitterService) {
                            HashMap<String, String> updateLoginState = new HashMap<String, String>();
                            updateLoginState.put("isTwitter", "true");
                            updateLoginState.put("loginState", "true");
                            Log.d(CLASS_NAME, "updateLoginState is: " + updateLoginState);
                            discoverAdapter.setLoginState(updateLoginState);
                            discoverListView.setAdapter(discoverAdapter);
                            Log.d("Inside LoggedIn ", "----------");
                            Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<User>() {
                                @Override
                                public void success(Result<User> userResult) {
                                    User user = userResult.data;
                                    String profileImageUrl = user.profileImageUrl;
                                    Log.d("profileImage URL: ", profileImageUrl);
                                    profileImageUrl = profileImageUrl.replace("_normal", "");

                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString("imgUrl", profileImageUrl);
                                    editor.putBoolean("imgFlag", false);
                                    editor.commit();
                                    ImageView profileImg = (ImageView) findViewById(R.id.imageView);
                                    if (profileImageUrl != null && !profileImageUrl.equalsIgnoreCase("")) {
                                        Drawable drawable = LoadImageFromWebOperations(profileImageUrl);
                                        profileImg.setImageDrawable(drawable);
                                    } else {
                                        profileImg.setImageDrawable(null);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                }
                            });
                        }else {
                            Log.d(CLASS_NAME, "isTwitterService was false");
                        }
                    }else {
                        Log.d(CLASS_NAME, "twitterString was null");
                    }

                    SpannableString loginText = new SpannableString(loginButtonText);
                    loginText.setSpan(new UnderlineSpan(), 0, loginButtonText.length(), 0);
                    loginButton.setText(loginText);
                }else {
                    Log.d(CLASS_NAME, "loginButtonText was null");
                }
            }else {
                Log.d(CLASS_NAME, "Login attempt resulted in an error");

                SpannableString loginText;
                String errorText = data.getStringExtra("errorText");
                AlertDialog errorDialog;

                if(errorText != null && !errorText.isEmpty()) {
                    errorDialog = new AlertDialog.Builder(this)
                            .setTitle("Twitter Login Failed")
                            .setMessage(errorText)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                loginText = new SpannableString("LOGIN UNSUCCESSFUL\nTRY AGAIN?");
                loginText.setSpan(new UnderlineSpan(), 0, "LOGIN UNSUCCESSFUL\nTRY AGAIN?".length(), 0);
                loginButton.setText(loginText);
            }
        }else if (requestCode == 100 && resultCode == RESULT_OK){ //Gallery
            if(data.getData() != null){
                Uri selectedImageUri = data.getData();
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                String selectedPath = selectedImageUri.getPath();
                Log.d("selectedPath1 : " ,selectedImageUri.toString());
                Log.d("selectedPath : " ,selectedPath);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("imgUrl", selectedImageUri.toString());
                editor.putBoolean("imgFlag",true);
                editor.commit();
            }else{
                Log.d("selectedPath1 : ","Came here its null !");
                Toast.makeText(getApplicationContext(), "failed to get Image!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == 200 && resultCode == RESULT_OK){ //Camera
                Log.d(CLASS_NAME, "Obtained Result from Camera");
        } else {
            Log.d(CLASS_NAME, "Intent was null - this probably means the LoginSelector was cancelled via 'Back' button");
        }

        Log.d(CLASS_NAME, "Exiting onActivityResult");
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        Log.d(CLASS_NAME, "Entering LoadImageFromWebOperations");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src_name");

            Log.d(CLASS_NAME, "Exiting LoadImageFromWebOperations"+url);

            return d;
        } catch (Exception e) {
            Log.d(CLASS_NAME, "Exiting LoadImageFromWebOperations"+ e);

            return null;
        }
    }
}
