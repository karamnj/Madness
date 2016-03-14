package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sirius.madness.R;

public class PartnersWebViewActivity extends Activity {

    private static final String LOG_PREFIX = "PartnersWebViewActivity";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_PREFIX, "Entering 'onCreate'");

        setContentView(R.layout.activity_partners_web_view);

        Log.d(LOG_PREFIX, "Finding WebView by ID");

        mWebView = (WebView) findViewById(R.id.partners_web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Log.d(LOG_PREFIX, "WebView found, JavaScript enabled");

        Intent intent = getIntent();
        String webLink = intent.getStringExtra("webLink");

        mWebView.loadUrl(webLink);

        Log.d(LOG_PREFIX, "Exiting 'onCreate'");
    }
}
