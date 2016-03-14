package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sirius.madness.R;
import com.sirius.madness.util.SocialMediaLoginMethods;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * Created by 039398 on 2/11/15.
 */
public class LoginWebViewActivity extends Activity {

    private String serviceType;

    private static final String LOG_PREFIX = "LoginWebViewActivity";

    private UserLoginTask mAuthTask = null;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_PREFIX, "Entering 'onCreate'");

        setContentView(R.layout.activity_login_webview);

        Log.d(LOG_PREFIX, "Finding WebView by ID");

        mWebView = (WebView) findViewById(R.id.login_web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Log.d(LOG_PREFIX, "WebView found, JavaScript enabled");

        Intent intent = getIntent();
        serviceType = intent.getStringExtra("serviceType");

        Log.d(LOG_PREFIX, "Got string from LoginActivity: " + serviceType);

        mAuthTask = new UserLoginTask(serviceType);
        mAuthTask.execute((Void) null);

        Log.d(LOG_PREFIX, "Exiting 'onCreate'");
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mServiceType;

        String authURL = "";
        private OAuthService service;
        private Token requestToken;

        //boolean clearHistory = false;

        UserLoginTask(String serviceType) {
            mServiceType = serviceType;

            Log.d(LOG_PREFIX, "UserLoginTask initiated with:\nService Type = " + serviceType);
        }

        @Override
        protected String doInBackground(Void... params) {

            Log.d(LOG_PREFIX, "Entering 'doInBackground'");

            try {

                if(mServiceType.contentEquals(SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING)) {
                    Log.d(LOG_PREFIX, "Attempting LinkedIn authentication");

                    service = new ServiceBuilder()
                            .provider(LinkedInApi.withScopes(SocialMediaLoginMethods.getLinkedinApiScope1(), SocialMediaLoginMethods.getLinkedinApiScope2()))
                            .apiKey(SocialMediaLoginMethods.getLinkedinApiKey())
                            .apiSecret(SocialMediaLoginMethods.getLinkedinApiSecret())
                            .callback(SocialMediaLoginMethods.getLinkedinCallback())
                            .build();

                    requestToken = service.getRequestToken();

                    Log.d(LOG_PREFIX, "Request token from LinkedIn = " + requestToken.getToken());

                    authURL = service.getAuthorizationUrl(requestToken);

                    Log.d(LOG_PREFIX, "Authorization URL from LinkedIn = " + authURL);

                    Log.d(LOG_PREFIX, "Exiting 'doInBackground'");
                    return authURL;
                }
                else if(mServiceType.contentEquals(SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING)) {
                    Log.d(LOG_PREFIX, "Attempting Twitter authentication");

                    service = new ServiceBuilder()
                            .provider(TwitterApi.SSL.class)
                            .apiKey(SocialMediaLoginMethods.getTwitterApiKey())
                            .apiSecret(SocialMediaLoginMethods.getTwitterApiSecret())
                            .callback(SocialMediaLoginMethods.getTwitterCallback())
                            .build();

                    requestToken = service.getRequestToken();

                    Log.d(LOG_PREFIX, "Request token from Twitter = " + requestToken.getToken());

                    authURL = service.getAuthorizationUrl(requestToken);

                    Log.d(LOG_PREFIX, "Authorization URL from Twitter = " + authURL);

                    Log.d(LOG_PREFIX, "Exiting 'doInBackground'");
                    return authURL;
                }
                else {
                    Log.d(LOG_PREFIX, "Service type was invalid, exiting");
                    return "Invalid Service";
                }
            } catch (OAuthException e) {
                e.printStackTrace();
                return "ERROR";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Log.d(LOG_PREFIX, "Entering 'onPostExecute'");

            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    super.shouldOverrideUrlLoading(view, url);

                    Log.d(LOG_PREFIX, "Entering shouldOverrideUrlLoading");

                    Log.d(LOG_PREFIX, "URL passed into shouldOverrideUrlLoading = " + url);

                    if( url.startsWith(SocialMediaLoginMethods.getLinkedinCallback()) ) {

                        Log.d(LOG_PREFIX, "Attempting oauth interception");

                        //mWebView.setVisibility(WebView.GONE);

                        final String currentURL = url;
                        final String linkedInName = "LinkedIn";

                        //clearHistory = true;

                        Log.d(LOG_PREFIX, "OAuth URL = " + url);

                        Thread t1 = new Thread() {
                            public void run() {
                                Log.d(LOG_PREFIX, "Beginning LinkedIn thread");

                                if(currentURL.contains("oauth_problem")) {
                                    String oauthProblem = currentURL.substring(
                                            currentURL.indexOf("oauth_problem")
                                                    + "oauth_problem".length() + 1);

                                    Log.d(LOG_PREFIX, "LinkedIn auth failed with message: " +
                                                    oauthProblem);

                                    Intent errorIntent = new Intent();
                                    errorIntent.putExtra("failure", oauthProblem);

                                    setResult(SocialMediaLoginMethods.RESULT_ERROR_CODE, errorIntent);
                                }else{
                                    Intent linkedInIntent = SocialMediaLoginMethods.retrieveServiceResponseIntent(
                                            service, requestToken, currentURL, SocialMediaLoginMethods.getLinkedinResourceUrl(),
                                            linkedInName);

                                    Log.d(LOG_PREFIX, "Successfully retrieved " + linkedInName +
                                            " data: " + linkedInIntent.getStringExtra(
                                            linkedInName.toLowerCase() + "Response"));

                                    setResult(RESULT_OK, linkedInIntent);
                                }

                                Log.d(LOG_PREFIX, "Exiting LinkedIn thread");

                                finish();
                            }
                        };

                        t1.start();

                        return true;
                    }else if(url.startsWith(SocialMediaLoginMethods.getTwitterCallback())) {
                        Log.d(LOG_PREFIX, "Attempting oauth interception");

                        //mWebView.setVisibility(WebView.GONE);

                        final String currentURL = url;
                        final String twitterName = "Twitter";
                        Log.d(LOG_PREFIX, "OAuth URL = " + url);

                        Thread t1 = new Thread() {
                            public void run() {

                                Log.d(LOG_PREFIX, "Beginning Twitter thread");

                                Intent twitterIntent = SocialMediaLoginMethods.retrieveServiceResponseIntent(
                                        service, requestToken, currentURL, SocialMediaLoginMethods.getTwitterResourceUrl(),
                                        twitterName);

                                Log.d(LOG_PREFIX, "Successfully retrieved Twitter data: " +
                                        twitterIntent.getStringExtra(
                                                twitterName.toLowerCase() + "Response"));

                                setResult(RESULT_OK, twitterIntent);
                                Log.d(LOG_PREFIX, "Exiting Twitter thread");
                                finish();
                            }
                        };

                        t1.start();
                    }

                    Log.d(LOG_PREFIX, "Exiting shouldOverrideUrlLoading");

                    return false;
                }

                public void onPageFinished(WebView view, String url){


                    /*if (clearHistory)
                    {
                        clearHistory = false;
                        mWebView.clearHistory();
                    }
                    super.onPageFinished(view, url);*/

                    //mWebView.goBackOrForward(Integer.MIN_VALUE);
                    //mWebView.clearHistory();

                    super.onPageFinished(view, url);

                    Log.d(LOG_PREFIX, "Entering onPageFinished");
                    Log.d(LOG_PREFIX, "onPageFinished current URL: " + url);

                    Log.d(LOG_PREFIX, "Is there a previous item: " + mWebView.canGoBack());
                    Log.d(LOG_PREFIX, "Is there a next item: " + mWebView.canGoForward());

                    if( url.startsWith(SocialMediaLoginMethods.getLinkedinAuthUrl()) ) {

                        Log.d(LOG_PREFIX, "Current URL matched '" + SocialMediaLoginMethods.getLinkedinAuthUrl() + "'");

                    }else if( url.startsWith(SocialMediaLoginMethods.getTwitterAuthUrl()) ) {

                        Log.d(LOG_PREFIX, "Current URL matched '" + SocialMediaLoginMethods.getTwitterAuthUrl() + "'");

                    }else if( url.startsWith(SocialMediaLoginMethods.getLinkedinCallback()) ) {
                        Log.d(LOG_PREFIX, "TRYING TO LEAVE");
                        //clearHistory = true;
                    }
                    else {
                        Log.d(LOG_PREFIX, "No URL matches");
                    }

                    //Call to a function defined on my myJavaScriptInterface
                    //mWebView.loadUrl("javascript: window.CallToAnAndroidFunction.setVisible()");

                    //mWebView.clearHistory();

                    Log.d(LOG_PREFIX, "Exiting onPageFinished");
                }
            });

            Log.d(LOG_PREFIX, "Exiting 'onPostExecute'");
            mWebView.loadUrl(authURL);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
