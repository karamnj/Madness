package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.ParticipantBean;
import com.google.gson.Gson;

public class LoginParticipantActivity extends BaseActivity {

    private static final String CLASS_NAME = "LoginParticipantActivity";
    private static final int LOGIN_QR_REQUEST = 3;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_contact);
        isFirstLevelActivity = true;

        Button scan = (Button) findViewById(R.id.scanContact);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = PreferenceManager.getDefaultSharedPreferences(LoginParticipantActivity.this);
                String participantId = prefs.getString("participantId", null);
                if(participantId==null) {
                    Intent intent = new Intent(LoginParticipantActivity.this, QRScannerActivity.class);
                    intent.putExtra("RegisterParticipant", false);
                    intent.putExtra("toast", "Please Scan Your ID to Login");
                    startActivityForResult(intent, LOGIN_QR_REQUEST);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_QR_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra("ScanResult");
                try {
                    Gson object = new Gson();
                    ParticipantBean bean = object.fromJson(scanResult, ParticipantBean.class);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("participantId", bean.getEmpId());
                    editor.commit();

                    Toast.makeText(LoginParticipantActivity.this,"Welcome "+bean.getFirstName(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("JsonParsingException : ",e.getMessage());
                }
            }else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(LoginParticipantActivity.this, "QR-Code is invalid", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected int getSelfMenuBarItem() {
        return MENU_BAR_ITEM_SHARE_CONTACT;
    }
}
