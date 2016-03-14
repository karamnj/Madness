package com.sirius.madness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.ParticipantBean;
import com.google.gson.Gson;

public class ShareContactActivity extends BaseActivity {

    private static final String CLASS_NAME = "ShareContactActivity";
    private static final int SHARE_CONTACT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_contact);
        isFirstLevelActivity = true;

        Button scan = (Button) findViewById(R.id.scanContact);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareContactActivity.this, QRScannerActivity.class);
                intent.putExtra("RegisterParticipant", false);
                startActivityForResult(intent, SHARE_CONTACT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SHARE_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra("ScanResult");
                try {
                    Gson object = new Gson();
                    ParticipantBean bean = object.fromJson(scanResult, ParticipantBean.class);
                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, bean.getFirstName() + " " + bean.getLastName());
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, bean.getPhone());
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, bean.getMailId());
                    intent.putExtra(ContactsContract.Intents.Insert.COMPANY, bean.getCompany());
                    intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, bean.getJobTitle());
                    intent.putExtra(ContactsContract.Intents.Insert.NOTES, bean.getLinkedInLink() + " " + bean.getTwitterLink());
                    startActivity(intent);
                }catch (Exception e){
                    Log.e("JsonParsingException : ",e.getMessage());
                }
            }else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(ShareContactActivity.this, "QR-Code is invalid", Toast.LENGTH_LONG).show();
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
