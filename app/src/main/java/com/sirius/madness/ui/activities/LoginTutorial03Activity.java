package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.sirius.madness.R;

/**
 * Created by 039398 on 2/12/15.
 */
public class LoginTutorial03Activity extends BaseActivity {

    TextView nextButton;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tutorial_03);

        nextButton = (TextView) findViewById(R.id.login_tutorial_03_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(LoginTutorial03Activity.this, LoginSelectorActivity.class);
                startActivity(nextIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void setupActionBar() {
        getSupportActionBar().hide();
    }
}
