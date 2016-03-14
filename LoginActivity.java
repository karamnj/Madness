package com.sirius.madness.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;

public class LoginActivity extends ActionBarActivity {

    private EditText password;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        button = (Button) findViewById(R.id.loginButton);
        textView = (TextView) findViewById(R.id.statusText);
        password = (EditText) findViewById(R.id.loginPwd);


        String passcode = prefs.getString("passcode",null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passCode = String.valueOf(password.getText());

                if("1234".equals(passCode)){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("passcode", "1234");
                    editor.commit();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                    Toast.makeText(LoginActivity.this,"Successfully Loggedin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, DiscoverActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                    textView.setText("Password entered is incorrect");
                }
            }
        });
        if(null != passcode){
            textView.setText("Already Loggedin");
            password.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }


}
