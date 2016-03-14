package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.util.SocialMediaLoginMethods;

/**
 * Created by 039398 on 2/12/15.
 */
public class LoginTutorial02Activity extends BaseActivity {

    private static String LOG_PREFIX = "LoginTutorial02Activity";
    private SharedPreferences prefs;
    TextView nextButton;
    AlertDialog dialog;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_PREFIX, "Entering onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tutorial_02);

        nextButton = (TextView) findViewById(R.id.login_tutorial_02_next);
        nextButton.setVisibility(View.INVISIBLE);

        prefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        Boolean gpsEnabled = prefs.getBoolean("isGPSEnabled", false);
        if(!gpsEnabled) {
            showGPSDialog(LoginTutorial02Activity.this);
        }else{
            Intent nextIntent = new Intent(LoginTutorial02Activity.this, LoginSiriusActivity.class);
            startActivity(nextIntent);
            finish();
        }

        /*nextButton = (TextView) findViewById(R.id.login_tutorial_02_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
                Boolean gpsEnabled = prefs.getBoolean("isGPSEnabled", false);
                if(!gpsEnabled) {
                    showGPSDialog(LoginTutorial02Activity.this);
                }else{
                    Intent nextIntent = new Intent(LoginTutorial02Activity.this, DiscoverActivity.class);
                    startActivity(nextIntent);
                    finish();
                }
            }
        });*/
        Log.d(LOG_PREFIX, "Exiting onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    @Override
    protected void onResume(){
        super.onResume();
        final SharedPreferences mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
        if (isGPSEnabled()) {
            Log.d(LOG_PREFIX, "GPS already enabled, continuing");

            SharedPreferences.Editor editPrefs = mPrefs.edit();
            editPrefs.putBoolean("isGPSEnabled", true);
            boolean isPrefsWriteSuccessful = editPrefs.commit();

            if(isPrefsWriteSuccessful) {
                Log.d(LOG_PREFIX, "Successfully wrote GPS preference to SharedPreferences");
            }else {
                Log.d(LOG_PREFIX, "Failed to write GPS preference to SharedPreferences");
            }

            Intent nextIntent = new Intent(LoginTutorial02Activity.this, LoginSiriusActivity.class);
            startActivity(nextIntent);
            finish();
        }else{
            Log.d(LOG_PREFIX, "GPS is not enabled");
            SharedPreferences.Editor editPrefs = mPrefs.edit();
            editPrefs.putBoolean("isGPSEnabled", false);
            boolean isPrefsWriteSuccessful = editPrefs.commit();
            if(isPrefsWriteSuccessful) {
                Log.d(LOG_PREFIX, "Successfully wrote GPS preference to SharedPreferences");
            }else {
                Log.d(LOG_PREFIX, "Failed to write GPS preference to SharedPreferences");
            }
            showGPSDialog(LoginTutorial02Activity.this);
        }
    }

    @Override
    public void setupActionBar() {
        getSupportActionBar().hide();
    }

    public void showGPSDialog(final Activity activity)
    {
        Log.d(LOG_PREFIX, "Entering showGPSDialog");

        String message;
        String okMessage;
        String cancelMessage;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;

        message = "Ignite\" would like to use your current location";
        okMessage = "OK";
        cancelMessage = "Don't Allow";

        final SharedPreferences mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);

        builder.setMessage(message)
                .setPositiveButton(okMessage,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();

                                Log.d(LOG_PREFIX, "User accepted GPS option");

                                if (isGPSEnabled()) {
                                    Log.d(LOG_PREFIX, "GPS already enabled, continuing");

                                    SharedPreferences.Editor editPrefs = mPrefs.edit();
                                    editPrefs.putBoolean("isGPSEnabled", true);
                                    boolean isPrefsWriteSuccessful = editPrefs.commit();

                                    if(isPrefsWriteSuccessful) {
                                        Log.d(LOG_PREFIX, "Successfully wrote GPS preference to SharedPreferences");
                                    }else {
                                        Log.d(LOG_PREFIX, "Failed to write GPS preference to SharedPreferences");
                                    }

                                    Intent nextIntent = new Intent(LoginTutorial02Activity.this, LoginSiriusActivity.class);
                                    startActivity(nextIntent);
                                    finish();
                                }else {
                                    Log.d(LOG_PREFIX, "GPS not enabled, directing user to Settings screen");

                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            }
                        })
                .setNegativeButton(cancelMessage,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {

                                Log.d(LOG_PREFIX, "User declined GPS option");

                                SharedPreferences.Editor editPrefs = mPrefs.edit();
                                editPrefs.putBoolean("isGPSEnabled", false);
                                boolean isPrefsWriteSuccessful = editPrefs.commit();

                                if(isPrefsWriteSuccessful) {
                                    Log.d(LOG_PREFIX, "Successfully wrote GPS preference to SharedPreferences");
                                }else {
                                    Log.d(LOG_PREFIX, "Failed to write GPS preference to SharedPreferences");
                                }

                                Intent nextIntent = new Intent(LoginTutorial02Activity.this, LoginSiriusActivity.class);
                                startActivity(nextIntent);
                                finish();
                            }
                        });

        //GPS is already enabled
        /*if(isGPSEnabled()) {
            message = "Location services are already enabled. Would you like to keep them on?";
            okMessage = "Yes";
            cancelMessage = "No (Disable)";

            builder.setMessage(message)
                    .setPositiveButton(okMessage,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();

                                    Intent nextIntent = new Intent(LoginTutorial02Activity.this, HomeActivity.class);
                                    startActivity(nextIntent);
                                    finish();
                                }
                            })
                    .setNegativeButton(cancelMessage,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            });
        }*/
        //GPS is disabled
        /*else {
            message = "Location services are disabled. Would you like to turn them on?";
            okMessage = "Yes";
            cancelMessage = "No";

            builder.setMessage(message)
                    .setPositiveButton(okMessage,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton(cancelMessage,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();

                                    Intent nextIntent = new Intent(LoginTutorial02Activity.this, HomeActivity.class);
                                    startActivity(nextIntent);
                                    finish();
                                }
                            });
        }*/

        //builder.create().show();
        dialog = builder.create();
        dialog.show();

        Log.d(LOG_PREFIX, "Exiting showGPSDialog");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean isGPSEnabled() {
        Log.d(LOG_PREFIX, "Entering isGPSEnabled");

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isEnabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.d(LOG_PREFIX, "Exiting isGPSEnabled");

        return isEnabled;
    }
}
