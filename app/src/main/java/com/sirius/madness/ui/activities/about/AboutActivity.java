package com.sirius.madness.ui.activities.about;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.sirius.madness.R;
import com.sirius.madness.ui.activities.BaseActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class AboutActivity extends BaseActivity {

    private static final String CLASS_NAME = AboutActivity.class.getSimpleName();

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_ABOUT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutFragment())
                    .commit();
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollContainer);
        scrollView.smoothScrollTo(0,0);
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

    public static class AboutFragment extends ListFragment {

        private static final String PROPS_FILE = "menu.properties";
        String aboutUs, contactUs, developmentTeam, softwareUsed;
        private ArrayAdapter<String> listAdapter;
        int menuItemCount = 0;
        int[] menuPosition;

        final int MORE_ABOUT_US = 0;
        final int CONTACT_US = 1;
        final int DEVELOPMENT_TEAM = 2;
        final int SOFTWARE_USED = 3;

        public AboutFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_about, container, false);

            readResourceProperties();

            menuPosition = new int[menuItemCount];

            int count = 0;

            ArrayList list = new ArrayList<String>();

            if (aboutUs.equals("true")) {
                list.add(getString(R.string.more_about_us));
                menuPosition[count] = MORE_ABOUT_US;
                count++;
            }
            if (contactUs.equals("true")) {
                list.add(getString(R.string.contact_us));
                menuPosition[count] = CONTACT_US;
                count++;
            }
            if (developmentTeam.equals("true")) {
                list.add(getString(R.string.development_team));
                menuPosition[count] = DEVELOPMENT_TEAM;
                count++;
            }
            if (softwareUsed.equals("true")) {
                list.add(getString(R.string.software_used));
                menuPosition[count] = SOFTWARE_USED;
                count++;
            }

            listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_about,
                    R.id.AboutItem, list);

            setListAdapter(listAdapter);

            return rootView;

        }
        private void readResourceProperties(){
            // Read from properties file.
            Properties props = new java.util.Properties();
            Context context = this.getContext();
            try {
                AssetManager assetManager = context.getAssets();
                props.load(assetManager.open(PROPS_FILE));
                Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
            } catch (FileNotFoundException e2) {
                Log.e(CLASS_NAME, "The menu.properties file was not found.", e2);
            } catch (IOException e) {
                Log.e(CLASS_NAME, "The menu.properties file could not be read properly.", e);
            }

            // Get settings from properties file
            aboutUs = (props.getProperty("aboutUs") != null) ? props.getProperty("aboutUs") : "";
            contactUs = (props.getProperty("contactUs") != null) ? props.getProperty("contactUs") : "";
            developmentTeam = (props.getProperty("developmentTeam") != null) ? props.getProperty("developmentTeam") : "";
            softwareUsed = (props.getProperty("softwareUsed") != null) ? props.getProperty("softwareUsed") : "";
            if (aboutUs.equals("true")) {
                menuItemCount++;
            }
            if (contactUs.equals("true")) {
                menuItemCount++;
            }
            if (developmentTeam.equals("true")) {
                menuItemCount++;
            }
            if (softwareUsed.equals("true")) {
                menuItemCount++;
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            switch (menuPosition[position]) {
                case 0:
                    Intent moreIntent = new Intent(this.getActivity(), MoreAboutActivity.class);
                    startActivity(moreIntent);
                    return;
                case 1:
                    Intent contactIntent = new Intent(this.getActivity(), ContactUsActivity.class);
                    startActivity(contactIntent);
                    return;
                case 2:
                    Intent devTeamIntent = new Intent(this.getActivity(), DevTeamActivity.class);
                    startActivity(devTeamIntent);
                    return;
                case 3:
                    Intent softwareIntent = new Intent(this.getActivity(), SoftwareUsedActivity.class);
                    startActivity(softwareIntent);
                    return;
            }
        }
    }
}


