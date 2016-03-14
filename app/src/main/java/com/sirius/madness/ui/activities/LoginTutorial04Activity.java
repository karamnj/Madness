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
public class LoginTutorial04Activity extends BaseActivity {

    TextView finishButton;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tutorial_04);

        finishButton = (TextView) findViewById(R.id.login_tutorial_04_finish);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent finishIntent = new Intent(LoginTutorial04Activity.this, HomeActivity.class);
                startActivity(finishIntent);
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
