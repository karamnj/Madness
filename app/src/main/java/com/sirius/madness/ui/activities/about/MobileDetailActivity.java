package com.sirius.madness.ui.activities.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.ui.activities.BaseActivity;

public class MobileDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_detail);
        getSupportActionBar().hide();

        LinearLayout mobileTeam = (LinearLayout) findViewById(R.id.mobile_team);
        //Clicking on e-mail allows user to choose e-mail program to use
        mobileTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SpeakerInfoActivity.this, "Item Clicked: Email", Toast.LENGTH_SHORT).show();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "Rick.Helmick@siriuscom.com, Wendi.ONeill@siriuscom.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        TextView arrow = (TextView) findViewById(R.id.rightArrow);
        arrow.setText(getResources().getString(R.string.custom_font_icon_right_arrow));
    }
}
