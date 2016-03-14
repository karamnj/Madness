package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class HomeActivity extends ActionBarActivity {

    private static final String CLASS_NAME = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "Entering onCreate");

        super.onCreate(savedInstanceState);

        String viewName = getIntent().getStringExtra("Previous Activity");
        viewName = (viewName != null) ? viewName : getActivityToLaunch();

        Intent intent = new Intent();
        Class<?> launch;
        try {
            String className = viewName;
            launch = Class.forName(className);
        } catch (ClassNotFoundException e) {
            launch = DiscoverActivity.class;
        }
        intent.setClass(getApplicationContext(), launch);
        startActivity(intent);

        Log.d(CLASS_NAME, "Exiting onCreate");

        //we are done with this class so call finish();
        finish();
    }


    private String getActivityToLaunch() {
        Log.d(CLASS_NAME, "Entering getActivityToLaunch");

        //TODO Add logic here to get data from Bluemix and determine whether or not to show the promotion

        Log.v(CLASS_NAME, "getActivityToLaunch()");

        Log.d(CLASS_NAME, "Exiting getActivityToLaunch");

        //return PromotionsActivity.class.getName();
        return LoadingActivity.class.getName();

    }
}
