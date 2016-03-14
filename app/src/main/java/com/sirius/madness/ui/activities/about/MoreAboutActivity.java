package com.sirius.madness.ui.activities.about;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.ui.activities.BaseActivity;

public class MoreAboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoreAboutFragment())
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
    public static class MoreAboutFragment extends Fragment {

        private static final String CLASS_NAME = MoreAboutActivity.class.getSimpleName();

        private final String[] mProjection = {
                IgniteContract.AvnetServices.AVNET_SERVICE_LONG_DESCRIPTION
        };
        /*private final String[] mProjection2 = {
                IgniteContract.Partners.PARTNER_SHORT_TITLE
        };*/

        public MoreAboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Cursor cursor = getActivity().getContentResolver().query(IgniteContract.AvnetServices.CONTENT_URI, mProjection, null, null, null);
            cursor.moveToFirst();

            View rootView = inflater.inflate(R.layout.fragment_more_about, container, false);

            TextView descView = (TextView) rootView.findViewById(R.id.Description);

            String descString = cursor.getString(0);
            //String descString = "Sirius is a national integrator of technology-based business solutions that span the enterprise, including the data center and lines of business. Built on products and services from the world’s top technology companies, Sirius solutions are installed, configured and supported by our dedicated teams of highly certified experts. \n\nWith the right people and the right partners, Sirius is able to focus on solutions to help IT professionals cut costs, increase reliability, ease the burden of management, maximize flexibility, mitigate risk and improve service. \n\nWe require our teams to be certified in multiple disciplines and products, so they can help you get from the high-level architecture of a solution all the way down to the technical configuration and implementation of point-products. \n\nSince it’s founding in 1980, Sirius has grown to be one of the largest IT solutions integrators in the U.S. Today, Sirius offers integrated, multivendor technology solutions that meet the requirements of the full range of organizations, from small businesses with fewer than 500 employees to large enterprises with thousands of employees and hundreds of locations.";//cursor.getString(0);
            //Log.d("Company String: ",""+cursor.getString(1));

            descView.setText(descString);

            //if we need to scroll
            descView.setMovementMethod(new ScrollingMovementMethod());

            return rootView;
        }
    }
}
