package com.sirius.madness.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.util.SocialMediaLoginMethods;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginSelectorActivity extends BaseActivity {

    TextView loginTwitter;
    TwitterLoginButton loginButton;
    TextView loginLinkedIn;

    private String serviceType;

    private static final String LOG_PREFIX = "LoginSelectorActivity";

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);

        loginTwitter = (TextView) findViewById(R.id.login_twitter_button);
        loginLinkedIn = (TextView) findViewById(R.id.login_with_linkedin_button);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                //TODO: Implement

                Log.d(LOG_PREFIX, "Entering Twitter callback success");

                Log.d(LOG_PREFIX, "User ID: " + result.data.getUserId());
                Log.d(LOG_PREFIX, "Username: " + result.data.getUserName());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("loginButtonText", "Welcome, " + result.data.getUserName() + "!");
                returnIntent.putExtra("isTwitter", "true");
                setResult(RESULT_OK, returnIntent);

                serviceType = SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING;

                Log.d(LOG_PREFIX, "Exiting Twitter callback success");

                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                //TODO: Implement

                Log.d(LOG_PREFIX, "Entering Twitter callback failure");

                Log.d(LOG_PREFIX, "Error Message: " + exception.getMessage());

                Intent returnIntent = new Intent();

                returnIntent.putExtra("errorText", exception.getMessage());
                setResult(SocialMediaLoginMethods.RESULT_ERROR_CODE, returnIntent);

                serviceType = SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING;

                Log.d(LOG_PREFIX, "Exiting Twitter callback failure");

                finish();
            }
        });

        // Store a reference to the current activity
        final Activity thisActivity = this;
        
        // Build the list of member required permissions
        final Scope scope = Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);

        loginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        //======================================================
        //Triggering Twitter Login Directly
        loginButton.performClick();
        //======================================================

        /*loginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceType = SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING;

                HashMap<String, String> existingTokens = checkForExistingLogin(serviceType);

                if(existingTokens != null && !existingTokens.isEmpty()) {
                    final String twitterToken = existingTokens.get(SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME);
                    final String twitterSecret = existingTokens.get(SocialMediaLoginMethods.TWITTER_PREF_SECRET_NAME);

                    Log.d(LOG_PREFIX, "Twitter Token: " + twitterToken);
                    Log.d(LOG_PREFIX, "Twitter Secret: " + twitterSecret);

                    final Token accessToken = new Token(twitterToken, twitterToken);

                    Thread t1 = new Thread() {
                        public void run() {

                            OAuthService service = new ServiceBuilder()
                                    .provider(TwitterApi.SSL.class)
                                    .apiKey(SocialMediaLoginMethods.getTwitterApiKey())
                                    .apiSecret(SocialMediaLoginMethods.getTwitterApiSecret())
                                    .callback(SocialMediaLoginMethods.getTwitterCallback())
                                    .build();

                            OAuthRequest request = new OAuthRequest(Verb.GET, SocialMediaLoginMethods.getTwitterResourceUrl());
                            service.signRequest(accessToken, request);

                            Log.d(LOG_PREFIX, "Parameters on request set, attempting to get response");

                            Response response = request.send();

                            Log.d(LOG_PREFIX, "Response returned");

                            String responseBody = response.getBody();

                            Log.d(LOG_PREFIX, "Response contents: " + responseBody);
                        }
                    };

                    t1.start();

                }else {
                    Intent twitterIntent = new Intent(LoginSelectorActivity.this, LoginWebViewActivity.class);
                    twitterIntent.putExtra(SocialMediaLoginMethods.SERVICE_TYPE_STRING, SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING);
                    startActivityForResult(twitterIntent, SocialMediaLoginMethods.CREDENTIALS_RESULT_CODE);
                }
            }
        });*/

        loginLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceType = SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING;

                HashMap<String, String> existingTokens = checkForExistingLogin(serviceType);

                if(existingTokens != null && !existingTokens.isEmpty()) {
                    String linkedInToken = existingTokens.get(SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME);
                    String linkedInSecret = existingTokens.get(SocialMediaLoginMethods.LINKED_IN_PREF_SECRET_NAME);

                    Log.d(LOG_PREFIX, "Token: " + linkedInToken);
                    Log.d(LOG_PREFIX, "Secret: " + linkedInSecret);

                    final AccessToken accessToken = AccessToken.buildAccessToken(linkedInToken);
                    //final Token accessToken = new Token(linkedInToken, linkedInToken);
                    LISessionManager sessionManager =  LISessionManager.getInstance(getApplicationContext());
                    sessionManager.init(accessToken);
                }else{
                    LISessionManager sessionManager =  LISessionManager.getInstance(getApplicationContext());
                    sessionManager.init(thisActivity, scope, new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                            Log.d(LOG_PREFIX, "Successfully Logged In using LinkedIn");
                            // Make authenticated REST call to get
                            String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)";
                            APIHelper helper = APIHelper.getInstance(getApplicationContext());
                            helper.getRequest(thisActivity, url, new ApiListener() {
                                @Override
                                public void onApiSuccess(ApiResponse apiResponse) {
                                    String userName = null;
                                    String profileImgUrl = null;
                                    if (apiResponse != null){
                                        JSONObject data = apiResponse.getResponseDataAsJson();
                                        try {
                                            userName = data.getString("firstName") + " " + data.getString("lastName");
                                            profileImgUrl =  data.getString("pictureUrl");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // Store username and profile pic
                                    SharedPreferences mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);
                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString("username", userName);
                                    editor.putString("imgUrl", profileImgUrl);
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("loginButtonText", "Welcome, " + userName + "!");
                                    returnIntent.putExtra("isTwitter", "false");
                                    returnIntent.putExtra("imgUrl", profileImgUrl);
                                    setResult(RESULT_OK, returnIntent);
                                }

                                @Override
                                public void onApiError(LIApiError LIApiError) {

                                }
                            });
                        }

                        @Override
                        public void onAuthError(LIAuthError error) {
                            // Handle authentication errors
                            Log.d(LOG_PREFIX, "Failed to Log In using LinkedIn with error " + error.toString());
                        }
                    }, true);
                }/*
                else {

                    Intent linkedInIntent = new Intent(LoginSelectorActivity.this, LoginWebViewActivity.class);
                    linkedInIntent.putExtra(SocialMediaLoginMethods.SERVICE_TYPE_STRING, SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING);
                    startActivityForResult(linkedInIntent, SocialMediaLoginMethods.CREDENTIALS_RESULT_CODE);

                }
                */
            }
/*
                    Thread t1 = new Thread() {
                        public void run() {

                            OAuthService service = new ServiceBuilder()
                                    .provider(TwitterApi.SSL.class)
                                    .apiKey(SocialMediaLoginMethods.getTwitterApiKey())
                                    .apiSecret(SocialMediaLoginMethods.getTwitterApiSecret())
                                    .callback(SocialMediaLoginMethods.getTwitterCallback())
                                    .build();


                            OAuthRequest request = new OAuthRequest(Verb.GET, SocialMediaLoginMethods.getLinkedinResourceUrl());
                            service.signRequest(accessToken, request);

                            Log.d(LOG_PREFIX, "Parameters on request set, attempting to get response");

                            Response response = request.send();

                            Log.d(LOG_PREFIX, "Response returned");

                            String responseBody = response.getBody();

                            Log.d(LOG_PREFIX, "Response contents: " + responseBody);
                        }
                    };

                    t1.start();


                }else {

                    Intent linkedInIntent = new Intent(LoginSelectorActivity.this, LoginWebViewActivity.class);
                    linkedInIntent.putExtra(SocialMediaLoginMethods.SERVICE_TYPE_STRING, SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING);
                    startActivityForResult(linkedInIntent, SocialMediaLoginMethods.CREDENTIALS_RESULT_CODE);

                }
            }*/

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

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         // Handle responses from the LinkedIn app
         LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

         Log.d(LOG_PREFIX, "Entering 'onActivityResult'");

         loginButton.onActivityResult(requestCode, resultCode, data);

         if(!serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING)) {

             Log.d(LOG_PREFIX, "Opening SharedPreferences");

             SharedPreferences mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);

             Log.d(LOG_PREFIX, "Checking for RESULT_OK");

             if (resultCode == RESULT_OK /*&& requestCode == LINKEDIN_LOGIN_RESULT_CODE*/) {
                 Log.d(LOG_PREFIX, "Found RESULT_OK, continuing...");

                 //finishActivity(SOCIAL_LOGIN_RESULT_CODE);

                 String access_token;
                 String access_secret;
                 String socialResponseType;
                 String socialResponse;
                 String prefTokenType;
                 String prefSecretType;

                 if (serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING)) {
                     access_token = data.getStringExtra("token");
                     access_secret = data.getStringExtra("access_secret");
                 }else{
                     access_token = data.getStringExtra("access_token");
                     access_secret = data.getStringExtra("access_secret");
                 }

                 if (!isNullOrEmpty(access_token) && !isNullOrEmpty(access_secret)) {

                     if (serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING)) {
                         socialResponseType = "linkedinResponse";

                         prefTokenType = SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME;
                         prefSecretType = SocialMediaLoginMethods.LINKED_IN_PREF_SECRET_NAME;
                     } else if (serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING)) {
                         socialResponseType = "twitterResponse";

                         prefTokenType = SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME;
                         prefSecretType = SocialMediaLoginMethods.TWITTER_PREF_SECRET_NAME;
                     } else {
                         serviceType = null;
                         socialResponseType = null;

                         prefTokenType = null;
                         prefSecretType = null;
                     }

                     if (serviceType != null) {
                         socialResponse = data.getStringExtra(socialResponseType);

                         Log.d(LOG_PREFIX, "Previous intent yields access_token = " + access_token +
                                 "\naccess_secret = " + access_secret + "\n" + socialResponseType + " = " +
                                 socialResponse);

                         Log.d(LOG_PREFIX, "Attempting to open editor for SharedPreferences");

                         // Store the tokens in preferences for further use
                         SharedPreferences.Editor editor = mPrefs.edit();

                         Log.d(LOG_PREFIX, "Successfully opened SharedPreferences, attempting to store new values");

                         if (prefTokenType != null && prefSecretType != null) {
                             editor.putString(prefTokenType, access_token);
                             editor.putString(prefSecretType, access_secret);
                             editor.commit();

                             Log.d(LOG_PREFIX, "New values stored successfully in SharedPreferences");
                         } else {
                             Log.d(LOG_PREFIX, "SharedPreferences not found for invalid service");
                         }

                         Log.d(LOG_PREFIX, "Creating new intent with values:\nserviceType = " +
                                 serviceType + "\n" + socialResponseType + " = " + socialResponse);

                     // Start activity
                     Intent mainActivityIntent = new Intent(this, HomeActivity.class);
                     mainActivityIntent.putExtra(SocialMediaLoginMethods.SERVICE_TYPE_STRING, serviceType);
                     mainActivityIntent.putExtra(socialResponseType, socialResponse);
                     if(getIntent().getStringExtra("Previous Activity") != null){
                        mainActivityIntent.putExtra("Previous Activity", DiscoverActivity.class.getName());
                         // Store Login state
                         editor = mPrefs.edit();
                         editor.putBoolean("Login State", true);
                         editor.commit();
                     }

                         Log.d(LOG_PREFIX, "Successfully created new intent, starting HomeActivity");

                         startActivity(mainActivityIntent);
                         finish();
                     } else {
                         Log.e(LOG_PREFIX, "Invalid service type returned, no data to display");
                         socialResponse = null;
                     }
                 }
             } else if (resultCode == SocialMediaLoginMethods.RESULT_ERROR_CODE) {
                 Log.d(LOG_PREFIX, "WebView returned an error, returning to login credentials");

                 String errorString = data.getStringExtra("failure");

                 Log.d(LOG_PREFIX, errorString);
             } else if (resultCode == SocialMediaLoginMethods.CREDENTIALS_RESULT_CODE) {
                 finish();
             } else {
                 Log.d(LOG_PREFIX, "WebView was cancelled, returning to login credentials");
             }
         }

         Log.d(LOG_PREFIX, "Exiting 'onActivityResult'");
    }

    private HashMap<String, String> checkForExistingLogin(String serviceType) {
        HashMap<String, String> result;

        SharedPreferences mPrefs = getSharedPreferences(SocialMediaLoginMethods.IGNITE_PREFS_FILE_NAME, 0);

        if(serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_LINKED_IN_STRING)) {
            if(mPrefs.contains(SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME)) {
                result = new HashMap<String, String>();
                result.put(SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME, mPrefs.getString(SocialMediaLoginMethods.LINKED_IN_PREF_TOKEN_NAME, null));
                result.put(SocialMediaLoginMethods.LINKED_IN_PREF_SECRET_NAME, mPrefs.getString(SocialMediaLoginMethods.LINKED_IN_PREF_SECRET_NAME, null));
            }else {
                result = null;
            }
        }else if (serviceType.equals(SocialMediaLoginMethods.SERVICE_TYPE_TWITTER_STRING)) {
            if(mPrefs.contains(SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME)) {
                result = new HashMap<String, String>();
                result.put(SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME, mPrefs.getString(SocialMediaLoginMethods.TWITTER_PREF_TOKEN_NAME, null));
                result.put(SocialMediaLoginMethods.TWITTER_PREF_SECRET_NAME, mPrefs.getString(SocialMediaLoginMethods.TWITTER_PREF_SECRET_NAME, null));
            }else {
                result = null;
            }
        }else {
            result = null;
        }

        return result;
    }

    private boolean isNullOrEmpty(String testString) {
        boolean result = false;

        if(testString == null || testString.isEmpty()) {
            result = false;
        }

        return result;
    }
}
