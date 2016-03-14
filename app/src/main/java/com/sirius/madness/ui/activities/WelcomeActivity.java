package com.sirius.madness.ui.activities;

import android.os.Bundle;
import android.view.Menu;

import com.sirius.madness.R;

public class WelcomeActivity extends BaseActivity {

    private static final String CLASS_NAME = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        isFirstLevelActivity = true;

    }

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_WELCOME;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
