package com.sirius.madness.ui.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.provider.IgniteDatabase;
import com.sirius.madness.ui.activities.BaseActivity;
import com.sirius.madness.ui.activities.DiscoverActivity;
import com.sirius.madness.ui.activities.LoginSiriusActivity;
import com.sirius.madness.ui.activities.MyScheduleActivity;
import com.sirius.madness.ui.activities.SessionDetailActivity;
import com.sirius.madness.util.SocialMediaLoginMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by 915642 on 29/12/14.
 */
public class DiscoverAdapter extends ArrayAdapter<SessionBean> {

    Context context;
    int resource;
    SessionBean[] sessions;

    private static final String CLASS_NAME = "DiscoverAdapter";

    private static final int TYPE_INFO = 0;
    private static final int TYPE_SESSION = 1;
    private static final int TYPE_MAX_COUNT = 2;
    protected LocationManager locationManager;
    private Typeface iconFont;
    private String filledStar;
    private String emptyStar;
    //private Boolean loginState;
    private HashMap<String, String> loginState;
    AlertDialog settingsDialog;
    Boolean loggedIn;
    ImageView profileImageHolder;
    SharedPreferences prefs;


    // Flag for GPS status
    boolean canGetLocation = false;
    SharedPreferences mPrefs = getContext().getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);


    public DiscoverAdapter(Context context, int resource, SessionBean[] sessions) {
        super(context, resource, sessions);
        this.context = context;
        this.resource = resource;
        this.sessions = sessions;
        iconFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/customIconFont.ttf");
        filledStar = context.getResources().getString(R.string.custom_font_icon_star_filled);
        emptyStar = context.getResources().getString(R.string.custom_font_icon_star_empty);
        loginState = null;
        //createSettingsAlert();
    }

    @Override
    public int getCount() {
        int actualLength = sessions.length + 1;

        //Don't count any "hidden" sessions in the Discover page's view count
        for (int i = 0; i < sessions.length; i++) {
            if (sessions[i].getIsHidden().equalsIgnoreCase("true")) {
                actualLength--;
            }
        }

        return actualLength;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        Log.d(CLASS_NAME, "Entering getView");

        Log.d(CLASS_NAME, "position = " + position);

        View row = convertView;
        final SessionHolder holder;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int arrayIndex = (position == 0) ? position : position - 1;

        //Make sure to skip any "hidden" views
        if (!sessions[arrayIndex].getIsHidden().equalsIgnoreCase("true")) {

            final int index = arrayIndex;

            final SessionBean session = sessions[index];

            if (loginState == null) {
                loginState = new HashMap<String, String>();
            }

            if (row == null) {
                LayoutInflater inflater;
                holder = new SessionHolder();
                int type = getItemViewType(position);

                switch (type) {
                    case 0:
                        // Add Info Buttons to View first
                        inflater = ((Activity) context).getLayoutInflater();
                        row = inflater.inflate(R.layout.discover_info_button_layout, parent, false);

                        /*TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
                        LISession linkedInSession = LISessionManager.getInstance(context).getSession();
                        if (twitterSession != null) {
                            Log.d(CLASS_NAME, "Twitter session isn't null");
                            loginState.put("loginState", "true");
                            loginState.put("isTwitter", "true");
                            Log.d(CLASS_NAME, "loginState is: " + loginState);
                        } else if (linkedInSession.isValid()) {
                            Log.d(CLASS_NAME, "LinkedIn session isn't null");
                            loginState.put("loginState", "true");
                            loginState.put("isTwitter", "false");
                            Log.d(CLASS_NAME, "loginState is: " + loginState);
                        } else {
                            Log.d(CLASS_NAME, "Twitter and LinkedIn sessions are null");
                            loginState.put("loginState", "false");
                            loginState.put("isTwitter", "false");
                            Log.d(CLASS_NAME, "loginState is: " + loginState);
                        }

                        loggedIn = Boolean.parseBoolean(loginState.get("loginState"));
                        //Boolean isTwitter = Boolean.parseBoolean(loginState.get("isTwitter"));

                        holder.loginUserInfo = (Button) row.findViewById(R.id.userLoginInfo);
                        holder.loginDuplicate= (TextView) row.findViewById(R.id.userLoginDuplicate);
                        holder.profileImageUpdate = (Button) row.findViewById(R.id.editProfileImage);
                        holder.postTweet = (Button) row.findViewById(R.id.postTweet);
                        //String login = (loginState) ? "LOGOUT" : "LOGIN";

                        // Set User profile image
                        final ImageView profileImg = (ImageView) row.findViewById(R.id.profileImage);
                        profileImageHolder = profileImg;
                        //profileImg.setImageDrawable(null);
                        //profileImg.setBackgroundColor(Color.argb(255, 5, 147, 188));*/

                        holder.locationInfo = (TextView) row.findViewById(R.id.userLocation);
                        TextView userName = (TextView) row.findViewById(R.id.userName);
                        String uName = prefs.getString("participantName","");
                        SpannableString city = new SpannableString(uName);
                        city.setSpan(new UnderlineSpan(), 0, uName.length(), 0);
                        userName.setText(city);

                        holder.yourSchedule = (TextView) row.findViewById(R.id.yourSchedule);

                        final TextView logoutIcon = (TextView) row.findViewById(R.id.userlogout);
                        logoutIcon.setTypeface(iconFont);
                        //final TextView scheduleIcon = (TextView) row.findViewById(R.id.scheduleIcon);
                        //scheduleIcon.setTypeface(iconFont);
                        TextView locIcon = (TextView) row.findViewById(R.id.userLocationIcon);
                        locIcon.setTypeface(iconFont);


                        /*// Set text and image of login button
                        String login;
                        if (loggedIn) {
                            //if (isTwitter) {
                                //login = "Welcome, " + twitterSession.getUserName() + "!";

                            //login = "";
                            logoutIcon.setEnabled(true);
                            logoutIcon.setVisibility(View.VISIBLE);
                            holder.postTweet.setEnabled(true);
                            holder.postTweet.setVisibility(View.VISIBLE);
                            boolean localImageFlag = mPrefs.getBoolean("imgFlag",false);
                            Log.d("Inside LoggedIn ","localImageFlag is "+localImageFlag);
                            String url = mPrefs.getString("imgUrl", "");
                            if (url != null && !url.equalsIgnoreCase("")) {
                                if(localImageFlag){
                                    Log.d("Inside LoggedIn ", "localImageFlag is set");
                                    try {
                                        profileImg.setImageBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(url)));
                                    }catch (Exception e){
                                        Log.d("File Exception",e.getMessage());
                                    }
                                }else {
                                    Drawable drawable = LoadImageFromWebOperations(url);
                                    profileImg.setImageDrawable(drawable);
                                }
                            }
                            holder.loginUserInfo.setVisibility(View.GONE);
                            holder.loginDuplicate.setVisibility(View.GONE);
                            holder.loginUserInfo.setEnabled(false);
                            holder.loginDuplicate.setEnabled(false);

                            Log.d(CLASS_NAME, "loginState is: " + loginState);
                        } else {
                            logoutIcon.setEnabled(false);
                            logoutIcon.setVisibility(View.GONE);
                            holder.postTweet.setEnabled(false);
                            holder.postTweet.setVisibility(View.GONE);
                            boolean localImageFlag = mPrefs.getBoolean("imgFlag",false);
                            Log.d("outside LoggedIn ","localImageFlag is "+localImageFlag);
                            String url = mPrefs.getString("imgUrl", "");
                            if (url != null && !url.equalsIgnoreCase("")) {
                                if(localImageFlag){
                                    Log.d("Inside LoggedIn ", "localImageFlag is set");
                                    try {
                                        profileImg.setImageBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(url)));
                                    }catch (Exception e){
                                        Log.d("File Exception",e.getMessage());
                                    }
                                }else {
                                    Drawable drawable = LoadImageFromWebOperations(url);
                                    profileImg.setImageDrawable(drawable);
                                }
                            }
                            holder.loginUserInfo.setVisibility(View.VISIBLE);
                            holder.loginDuplicate.setVisibility(View.VISIBLE);
                            holder.loginUserInfo.setEnabled(true);
                            holder.loginDuplicate.setEnabled(true);
                            *//*
                            Log.d(CLASS_NAME, "loginState is: " + loginState);
                            holder.loginUserInfo.setVisibility(View.VISIBLE);
                            holder.loginDuplicate.setVisibility(View.VISIBLE);
                            holder.loginUserInfo.setEnabled(true);
                            holder.loginDuplicate.setEnabled(true);
                            logoutIcon.setEnabled(false);
                            logoutIcon.setVisibility(View.GONE);
                            holder.postTweet.setEnabled(false);
                            holder.postTweet.setVisibility(View.GONE);
                            profileImg.setImageDrawable(null);
                            profileImg.setScaleType(ImageView.ScaleType.CENTER);*//*
                        }

                        holder.postTweet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isTwitterInstalled()) {
                                    TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                                    Fabric.with(context, new TwitterCore(authConfig), new TweetComposer());

                                    Uri myImageUri = Uri.parse(mPrefs.getString("imgUrl", ""));
                                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text("Attending @kickoff")
                                            .image(myImageUri);
                                    builder.show();
                                }else{
                                    Toast.makeText(context,"Please install the Twitter App to Tweet",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        holder.profileImageUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Complete Action Using")
                                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent;
                                                if (Build.VERSION.SDK_INT < 19) {
                                                    intent = new Intent();
                                                    intent.setType("image/*");
                                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                                } else {
                                                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                                    intent.setType("image/*");
                                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                }
                                                ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select file to upload "), 100);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // dismiss dialog with no action
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                                                    // Create the File where the photo should go
                                                    File photoFile = null;
                                                    try {
                                                        photoFile = createImageFile();
                                                    } catch (IOException ex) {
                                                        // Error occurred while creating the File
                                                        Log.i(CLASS_NAME, "IOException File");
                                                    }
                                                    // Continue only if the File was successfully created
                                                    if (photoFile != null) {
                                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                                        ((Activity) context).startActivityForResult(cameraIntent, 200);
                                                    }
                                                }
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });

                        holder.loginUserInfo.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View loginView) {
                                final View loginViewFinal = loginView;

                                Log.d(CLASS_NAME, "User clicked identity button");
                                Log.d(CLASS_NAME, "loginState is: " + loginState);

                                if (loginState != null && !loginState.isEmpty()) {
                                    // Go to login screen if not logged in
                                    if (loggedIn == false) {
                                        Intent intent = new Intent(loginView.getContext(), LoginSelectorActivity.class);
                                        intent.putExtra("Previous Activity", DiscoverActivity.class.getName());
                                        intent.putExtra("isTwitter", loginState.get("isTwitter"));
                                        ((Activity) context).startActivityForResult(intent, 55);
                                    }*//* else {
                                        AlertDialog errorDialog = new AlertDialog.Builder(context)
                                                .setTitle("Log out?")
                                                .setMessage("Do you wish to sign out?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with sign-out

                                                        SpannableString loginText = new SpannableString("LOGIN");
                                                        loginText.setSpan(new UnderlineSpan(), 0, loginText.length(), 0);
                                                        ((Button) loginViewFinal).setText(loginText);

                                                        boolean localImageFlag = mPrefs.getBoolean("imgFlag",false);
                                                        if(localImageFlag){
                                                            String url = mPrefs.getString("imgUrl", "");
                                                            if (url != null && !url.equalsIgnoreCase("")) {
                                                                Drawable drawable = LoadImageFromWebOperations(url);
                                                                profileImage.setImageDrawable(drawable);
                                                            }
                                                        }else {
                                                            updateProfileImage("");
                                                        }
                                                        loginState.put("loginState", "false");
                                                        loginState.put("isTwitter", "false");
                                                        Log.d(CLASS_NAME, "loginState is: " + loginState);

                                                        //Clear Twitter session
                                                        Twitter.getSessionManager().clearActiveSession();

                                                        //Clear LinkedIn session
                                                        LISessionManager.getInstance(context).clearSession();

                                                        //Clear all saved credentials
                                                        SharedPreferences.Editor editor = mPrefs.edit();
                                                        editor.putString(SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME, null);
                                                        editor.putString(SocialMediaLoginMethods.LINKED_IN_PREF_SECRET_NAME, null);
                                                        editor.putString(SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME, null);
                                                        editor.putString(SocialMediaLoginMethods.TWITTER_PREF_SECRET_NAME, null);
                                                        editor.putString("username", null);
                                                        editor.putString("imgUrl", null);
                                                        editor.commit();
                                                        loggedIn = false;

                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // dismiss dialog with no action
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }*//*
                                } else {
                                    loginViewFinal.setVisibility(View.VISIBLE);
                                    holder.loginDuplicate.setVisibility(View.VISIBLE);
                                    loginViewFinal.setEnabled(true);
                                    holder.loginDuplicate.setEnabled(true);
                                }

                                // Launch User Settings
                                //Intent settingsIntent = new Intent(context, null);
                                //context.startActivity(settingsIntent);
                            }
                        });

                        holder.loginDuplicate.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View loginView) {
                                final View loginViewFinal = loginView;

                                Log.d(CLASS_NAME, "User clicked identity button");
                                Log.d(CLASS_NAME, "loginState is: " + loginState);

                                if (loginState != null && !loginState.isEmpty()) {
                                    // Go to login screen if not logged in
                                    if (loggedIn == false) {
                                        Intent intent = new Intent(loginView.getContext(), LoginSelectorActivity.class);
                                        intent.putExtra("Previous Activity", DiscoverActivity.class.getName());
                                        intent.putExtra("isTwitter", loginState.get("isTwitter"));
                                        ((Activity) context).startActivityForResult(intent, 55);
                                    }
                                } else {
                                    loginViewFinal.setVisibility(View.VISIBLE);
                                    holder.loginDuplicate.setVisibility(View.VISIBLE);
                                    loginViewFinal.setEnabled(true);
                                    holder.loginDuplicate.setEnabled(true);
                                }

                            }
                        });*/

                        logoutIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final View loginViewFinal = holder.loginUserInfo;
                                new AlertDialog.Builder(context)
                                        .setTitle("Log out?")
                                        .setMessage("Do you wish to sign out?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with sign-out
                                                loggedIn = false;
                                                SharedPreferences.Editor editor = mPrefs.edit();
                                                editor.putString("participantId", "");
                                                editor.putString("participantName", "");
                                                editor.putString("participantNumber", "");
                                                editor.putString("participantEmail", "");
                                                editor.commit();

                                                IgniteDatabase db = new IgniteDatabase(context);
                                                db.deleteSchedule(db.getWritableDatabase());

                                                //TODO PerformLogout
                                                Intent intent = new Intent(context, LoginSiriusActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                context.startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // dismiss dialog with no action
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        });

                        holder.yourSchedule.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, MyScheduleActivity.class));
                            }
                        });

                        holder.locationInfo.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(final View locationView) {
                                if (!isGPSEnabled()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Location:");
                                    builder.setMessage("Enter Location (City, State):");

                                    // Set up the input
                                    final EditText input = new EditText(context);
                                    // Specify the type of input expected
                                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(input);

                                    // Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String userSpecifiedLocation = input.getText().toString();
                                            // Save location
                                            SharedPreferences.Editor editor = mPrefs.edit();
                                            editor.putString("Location", userSpecifiedLocation);
                                            editor.commit();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                }

                            }
                        });
                        holder.viewType = "Info Cell";
                        row.setTag(holder);
                        break;
                    case 1:
                        // Add Session Cell to view
                        inflater = ((Activity) context).getLayoutInflater();
                        row = inflater.inflate(resource, parent, false);

                        holder.viewType = "Session Cell";
                        holder.sessionBackground = (ImageView) row.findViewById(R.id.sessionBackground);

                        holder.sessionName = (TextView) row.findViewById(R.id.sessionName);
                        holder.sessionDate = (TextView) row.findViewById(R.id.sessionDate);
                        holder.sessionTimings = (TextView) row.findViewById(R.id.sessionTime);
                        holder.sessionCategory = (TextView) row.findViewById(R.id.sessionCategory);
                        holder.frameLayout = (FrameLayout) row.findViewById(R.id.dateLayout);
                        holder.shareAction = (Button) row.findViewById(R.id.shareAction);
                        holder.addToCalendar = (Button) row.findViewById(R.id.addTocalendar);

                        // Set Background Image and Image filter
                        int color = getColorForCell(index);
                        Drawable image = context.getResources().getDrawable(context.getResources().getIdentifier("mobtest", "drawable", context.getPackageName()));
                        //image.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                        holder.sessionBackground.setImageDrawable(image);

                        // Set Background Color
                        holder.sessionBackground.setBackgroundColor(color);
                        //color = getColorForDatabar(index);
                        //holder.frameLayout.setBackgroundColor(color);
                        row.setTag(holder);
                        break;
                    default:
                        break;
                }
            } else {
                holder = (SessionHolder) row.getTag();
            }

            if (holder.viewType == null || holder.viewType.equalsIgnoreCase("")) {
                return row;
            }
            // Set up view for Session Cell
            else if (holder.viewType.equalsIgnoreCase("Session Cell")) {
                //    holder.sessionBackground.setBackgroundColor(session.getSessionBg());
                holder.sessionName.setText(session.getSessionName());
                Date date = new Date(session.getFromTime());
                SimpleDateFormat fromTime = new SimpleDateFormat("MMM dd");
                Log.d("Date:", fromTime.format(date));
                holder.sessionDate.setText(fromTime.format(date)+getDaySuffix(date.getDay()));

                Date from = new Date(session.getFromTime());
                Date to = new Date(session.getToTime());
                fromTime = new SimpleDateFormat("k:mm");
                Log.d("Time:", fromTime.format(from) + " = " + fromTime.format(to));

                holder.sessionTimings.setText(fromTime.format(from) + " - " + fromTime.format(to));
                String sessionIcon = "";
                if (session.getSessionCategory().equalsIgnoreCase("Mobile")) {
                    sessionIcon = context.getResources().getString(R.string.custom_font_icon_phone);
                } else if (session.getSessionCategory().equalsIgnoreCase("Commerce")) {
                    sessionIcon = context.getResources().getString(R.string.custom_font_icon_shopping_car);
                }else{
                    sessionIcon = context.getResources().getString(R.string.custom_font_icon_thumbs_up);
                }
                if (session.getCalendarEventId() == null) {
                    holder.addToCalendar.setText(emptyStar);
                } else {
                    holder.addToCalendar.setText(filledStar);
                }
                holder.sessionCategory.setText(sessionIcon);
                //holder.sessionCategory.setText(sessionIcon + " " + session.getSessionCategory());

                // Set Background Image and Image filter
                int color = getColorForCell(index);
                Bitmap bMap = BitmapFactory.decodeByteArray(session.getSessionBg(), 0, session.getSessionBg().length);
                Drawable image = new BitmapDrawable(context.getResources(), bMap);
                //image.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                holder.sessionBackground.setImageDrawable(image);

                holder.addToCalendar.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        Uri baseUri = Uri.parse("content://com.android.calendar/events");
                        Button addButton = (Button) v;
                        if (session.getCalendarEventId() == null) {
                            final ContentValues event = new ContentValues();
                            event.put(Events.CALENDAR_ID, 1);
                            event.put(Events.TITLE, session.getSessionName());
                            event.put(Events.DESCRIPTION, session.getSessionDescription());
                            event.put(Events.DTSTART, session.getFromTime());
                            event.put(Events.DTEND, session.getToTime());
                            event.put(Events.ALL_DAY, 0);   // 0 for false, 1 for true
                            event.put(Events.HAS_ALARM, 0); // 0 for false, 1 for true

                            String timeZone = TimeZone.getDefault().getID();
                            event.put(Events.EVENT_TIMEZONE, timeZone);
                            Uri uri = context.getContentResolver().insert(baseUri, event);
                            sessions[index].setCalendarEventId(uri.getLastPathSegment());
                            Uri scheduleUri = IgniteContract.Schedules.CONTENT_URI;
                            ContentValues values = new ContentValues();
                            values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, uri.getLastPathSegment());
                            values.put(IgniteContract.Schedules.EVENT_ID_FK, 1);
                            values.put(IgniteContract.Schedules.SESSION_ID_FK, session.getSessionId());
                            values.put(IgniteContract.Schedules.EVENT_TITLE, session.getSessionName());
                            values.put(IgniteContract.Schedules.FROM_TIME, session.getFromTime());
                            values.put(IgniteContract.Schedules.TO_TIME, session.getToTime());
                            values.put(IgniteContract.Schedules.MANDATORY, "false");
                            context.getContentResolver().insert(scheduleUri, values);
                            addButton.setText(filledStar);

                            //Store Session to Preferences
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            JSONObject eventJSON = new JSONObject();
                            try {
                                eventJSON.put("SessionId", session.getSessionId());
                                eventJSON.put("SessionName", session.getSessionName());
                                eventJSON.put("SessionDescription", session.getSessionDescription());
                                eventJSON.put("FromTime", session.getFromTime());
                                eventJSON.put("ToTime", session.getToTime());
                                eventJSON.put("CalendarEventId", uri.getLastPathSegment());
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            JSONArray sessionJSON = null;
                            String prefJson = prefs.getString("sessionJSON",null);
                            if(prefJson != null){
                                try {
                                    sessionJSON = new JSONArray(prefJson);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else{
                                sessionJSON = new JSONArray();
                            }
                            sessionJSON.put(eventJSON);
                            prefs.edit().putString("sessionJSON", sessionJSON.toString()).commit();

                            // Schedule Local notification
                            String desc = "Session starts in 15 minutes.";
                            Date eventDate = new Date();
                            eventDate.setTime(session.getFromTime());
                            Date eventEndDate = new Date();
                            eventEndDate.setTime(session.getToTime());
                            ((BaseActivity) context).scheduleLocalNotification(session.getSessionName(), desc, eventDate, eventEndDate, session.getSessionId());
                        } else {
                            Uri scheduleUri = IgniteContract.Schedules.buildSessionUri(session.getSessionId());
                            context.getContentResolver().delete(scheduleUri, null, null);
                            long eventId = Long.valueOf(session.getCalendarEventId());
                            context.getContentResolver().delete(ContentUris.withAppendedId(baseUri, eventId), null, null);
                            sessions[index].setCalendarEventId(null);
                            addButton.setText(emptyStar);

                            //Remove Session Preference
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            JSONArray sessionJSON = null;
                            String prefJson = prefs.getString("sessionJSON", null);
                            try {
                                if(prefJson != null){

                                    sessionJSON = new JSONArray(prefJson);
                                } else{
                                    sessionJSON = new JSONArray();
                                }
                                for (int i = 0; i < sessionJSON.length(); i++) {
                                    JSONObject explrObject = sessionJSON.getJSONObject(i);
                                    if(explrObject.getInt("SessionId")==session.getSessionId()){
                                        sessionJSON.remove(i);
                                        break;
                                    }
                                }
                                prefs.edit().putString("sessionJSON", sessionJSON.toString()).commit();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // Cancel Local Notification
                            ((BaseActivity) context).cancelLocalNotification(session.getSessionName(), session.getSessionName() + " will start in 15 minutes.", session.getSessionId());
                        }
                    }
                });

                // Set OnClick event for share button
                holder.shareAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create Share Intent on click
                        SimpleDateFormat fromTime = new SimpleDateFormat("MMM dd h:mm a");
                        String sessionName = session.getSessionName();
                        String sessionDescription = "@kickoff"+session.getSessionName()+" ON:"+fromTime.format(session.getFromTime());
                        createShareIntent(v, sessionDescription, sessionName);
                    }
                });
                FrameLayout sessionDataBar = (FrameLayout) row.findViewById(R.id.sessionHolder);
                sessionDataBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SessionDetailActivity.class);
                        intent.putExtra("Session Id", session.getSessionId());
                        context.startActivity(intent);
                    }
                });

                // Set Background Color
                //holder.sessionBackground.setBackgroundColor(color);
                //color = getColorForDatabar(index);
                //holder.frameLayout.setBackgroundColor(color);

                // Set Icon Fonts
                holder.shareAction.setTypeface(iconFont);
                holder.addToCalendar.setTypeface(iconFont);
                holder.sessionCategory.setTypeface(iconFont);

            } else if (holder.viewType.equalsIgnoreCase("Info Cell")) {
                String userSpecifiedLocation = mPrefs.getString("Location", "");
                String location = (userSpecifiedLocation.equalsIgnoreCase("")) ? getLocation() : userSpecifiedLocation;
                if (isGPSEnabled()) {
                    SpannableString city = new SpannableString(location);
                    city.setSpan(new UnderlineSpan(), 0, location.length(), 0);
                    holder.locationInfo.setText(city);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("Location", location);
                    editor.commit();
                } else {
                    SpannableString city = new SpannableString(location);
                    city.setSpan(new UnderlineSpan(), 0, location.length(), 0);
                    holder.locationInfo.setText(city);
                    //showSettingsAlert();
                }

                TextView loginIcon = (TextView) row.findViewById(R.id.userlogout);
                loginIcon.setTypeface(iconFont);
                TextView locIcon = (TextView) row.findViewById(R.id.userLocationIcon);
                locIcon.setTypeface(iconFont);
            }
        }

        Log.d(CLASS_NAME, "Exiting getView");

        return row;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d("selectedPath2 : ", mCurrentPhotoPath);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("imgUrl", mCurrentPhotoPath);
        editor.putBoolean("imgFlag", true);
        editor.commit();
        return image;
    }

    String getDaySuffix(final int n) {
        if(n < 1 || n > 31)
            return "Invalid date";
        if (n >= 11 && n <= 13)
            return "th";

        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_INFO : TYPE_SESSION;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public boolean isTwitterInstalled() {
        boolean twitterInstalled = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            String getPackageName = packageInfo.toString();
            Log.d("TwitterPackageName", getPackageName);
            if (getPackageName.contains("com.twitter.android")) {
                Toast.makeText(context, "Twitter App is installed on device!", Toast.LENGTH_SHORT).show();
                twitterInstalled = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TwitterPackageName", "Error");
        }
        return twitterInstalled;
    }

    public boolean isGPSEnabled() {
        Log.d(CLASS_NAME, "Entering isGPSEnabled");

        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.d(CLASS_NAME, "Exiting isGPSEnabled");

        return isEnabled;
    }

    private String getLocation() {
        Log.d(CLASS_NAME, "Entering getLocation");

        Location location = null;
        double latitude = 0;
        double longitude = 0;
        String locString = "Unknown";


        locationManager = (LocationManager) context
                .getSystemService(context.LOCATION_SERVICE);

        // Getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // No network provider is enabled
            canGetLocation = false;
        } else {
            canGetLocation = true;
            if (isNetworkEnabled) {
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        }
        locationManager = null;

        if(location != null && canGetLocation) {
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

                if (addresses.size() > 0) {
                    //String cityState = addresses.get(0).getAddressLine(1);
                    //cityState = cityState.replaceAll("\\d+.*", "");
                    //cityState.trim();
                    //String country = addresses.get(0).getCountryCode();
                    String city = addresses.get(0).getLocality();
                    locString = city;
                }


            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        Log.d(CLASS_NAME, "Exiting getLocation");

        return locString;
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void createSettingsAlert(){
        Log.d(CLASS_NAME, "Entering createSettingsAlert");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);



        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Creating Alert Message
        settingsDialog = alertDialog.create();

        Log.d(CLASS_NAME, "Exiting createSettingsAlert");
    }
    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert(){
        Log.d(CLASS_NAME, "Entering showSettingsAlert");
        if(!settingsDialog.isShowing()) {
            settingsDialog.show();
        }
        Log.d(CLASS_NAME, "Exiting showSettingsAlert");
    }

    /**
     * This method returns an int representing a color for filtering
     * for the given cell at the index.
     * @param index
     * @return int representing a color for filtering
     */
    private int getColorForCell(int index) {
        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 3) {
            index = index%4;
        }

        switch (index) {
            case 0:
                red = 5;
                green = 147;
                blue = 188;
                break;
            case 1:
                red = 140;
                green = 179;
                blue = 26;
                break;
            case 2:
                red = 2;
                green = 64;
                blue = 110;
                break;
            case 3:
                red = 235;
                green = 103;
                blue = 48;
                break;

            default:
                red = 140;
                green = 179;
                blue = 26;
                break;
        }


        return Color.argb(alpha, red, green, blue);
    }

    /**
     * This method returns an int representing a color for filtering
     * for the given databar at the index.
     * @param index
     * @return int representing a color for filtering
     */
    private int getColorForDatabar(int index) {
        int red;
        int blue;
        int green;
        int alpha = 204; // 80% opaque

        if (index > 3) {
            index = index%4;
        }

        switch (index) {
            case 0:
                red = 7;
                green = 131;
                blue = 160;
                break;
            case 1:
                red = 137;
                green = 175;
                blue = 71;
                break;
            case 2:
                red = 2;
                green = 56;
                blue = 97;
                break;
            case 3:
                red = 245;
                green = 146;
                blue = 66;
                break;

            default:
                red = 7;
                green = 131;
                blue = 160;
                break;
        }


        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Creates a share intent to share the session summary
     * @param v - current view
     * @param shareBody - session summary
     * @param shareSubject - session title
     */
    private void createShareIntent(View v, String shareBody, String shareSubject){
        Log.d(CLASS_NAME, "Entering createShareIntent");

        Resources resources = context.getResources();
        int twitterCharCount = 140;

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    String twitBody = (shareBody.length() > twitterCharCount) ? shareBody.substring(0, twitterCharCount) : shareBody;
                    intent.putExtra(Intent.EXTRA_TEXT, twitBody);
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        context.startActivity(openInChooser);

        Log.d(CLASS_NAME, "Exiting createShareIntent");
    }

    public void setLoginState(HashMap<String, String> loginState) {
        Log.d(CLASS_NAME, "Entering setLoginState");

        this.loginState.putAll(loginState);

        Log.d(CLASS_NAME, "Exiting setLoginState");
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

class SessionHolder{
    ImageView sessionBackground;
    TextView sessionName;
    TextView sessionCategory;
    TextView sessionDate;
    TextView sessionTimings;
    TextView loginDuplicate;
    FrameLayout frameLayout;
    TextView yourSchedule;
    Button addToCalendar;
    Button shareAction;
    Button loginUserInfo;
    TextView locationInfo;
    Button profileImageUpdate;
    Button postTweet;
    String viewType;
}