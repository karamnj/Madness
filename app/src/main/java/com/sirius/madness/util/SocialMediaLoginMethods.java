package com.sirius.madness.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * Created by 039398 on 2/13/15.
 */
public class SocialMediaLoginMethods {

    public static final String SERVICE_TYPE_STRING = "serviceType";
    public static final String SERVICE_TYPE_TWITTER_STRING = "twitter";
    public static final String SERVICE_TYPE_LINKED_IN_STRING = "linkedin";

    private static final String LOG_PREFIX = "SocialMediaLoginMethods";

    public static final String LINKED_IN_PREF_TOKEN_NAME = "linkedin_access_token";
    public static final String LINKED_IN_PREF_SECRET_NAME = "linkedin_access_secret";
    public static final String TWITTER_PREF_TOKEN_NAME = "twitter_access_token";
    public static final String TWITTER_PREF_SECRET_NAME = "twitter_access_secret";
    public static final String IGNITE_PREFS_FILE_NAME = "IgniteAppPrefsFile";

    public static final int SOCIAL_LOGIN_RESULT_CODE = 42;
    public static final int TWITTER_RESULT_CODE = 21;
    public static final int LINKED_IN_RESULT_CODE = 22;
    public static final int GUEST_RESULT_CODE = 23;
    public static final int CREDENTIALS_RESULT_CODE = 25;
    public static final int RESULT_ERROR_CODE = 26;

    //The following fields should be accessible on LinkedIn with only the "r_basicprofile"
    //  and "r_emailaddress" permissions enabled
    private static final String LINKED_IN_FIELDS = "id,first-name,last-name,headline,picture-url," +
            "email-address";
    private static String mServiceStatus = "Success";

    private static final String LINKEDIN_RESOURCE_URL = "http://api.linkedin.com/v1/" +
            "people/~:(" + LINKED_IN_FIELDS + ")";

    private static final String LINKEDIN_API_SCOPE1 = "r_basicprofile";
    private static final String LINKEDIN_API_SCOPE2 = "r_contactinfo";
    private static final String LINKEDIN_API_KEY = "75a3e675z7z5x8";
    private static final String LINKEDIN_API_SECRET = "Cw6fE4p8qIgdCWDX";
    private static final String LINKEDIN_CALLBACK = "https://sirius.com/oauthredirect";
    private static final String LINKEDIN_AUTH_URL = "https://www.linkedin.com/uas/oauth";

    private static final String TWITTER_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final String TWITTER_API_KEY = "ESiYHRgtEQCljxtvKWA9NM4LN";
    private static final String TWITTER_API_SECRET = "Gz3LmsXJOsdUQFFpgIH4S9gV6lguFAL9htAwpWP7vTCPuUikHK";
    private static final String TWITTER_CALLBACK = "https://www.sirius.com/oauthredirect";
    private static final String TWITTER_AUTH_URL = "https://api.twitter.com/oauth/authorize";

    public static String getLinkedInFields() {
        return LINKED_IN_FIELDS;
    }

    public static String getmServiceStatus() {
        return mServiceStatus;
    }

    public static String getLinkedinResourceUrl() {
        return LINKEDIN_RESOURCE_URL;
    }

    public static String getLinkedinApiScope1() {
        return LINKEDIN_API_SCOPE1;
    }

    public static String getLinkedinApiScope2() {
        return LINKEDIN_API_SCOPE2;
    }

    public static String getLinkedinApiKey() {
        return LINKEDIN_API_KEY;
    }

    public static String getLinkedinApiSecret() {
        return LINKEDIN_API_SECRET;
    }

    public static String getLinkedinCallback() {
        return LINKEDIN_CALLBACK;
    }

    public static String getLinkedinAuthUrl() {
        return LINKEDIN_AUTH_URL;
    }

    public static String getTwitterResourceUrl() {
        return TWITTER_RESOURCE_URL;
    }

    public static String getTwitterApiKey() {
        return TWITTER_API_KEY;
    }

    public static String getTwitterApiSecret() {
        return TWITTER_API_SECRET;
    }

    public static String getTwitterCallback() {
        return TWITTER_CALLBACK;
    }

    public static String getTwitterAuthUrl() {
        return TWITTER_AUTH_URL;
    }

    public static Intent retrieveServiceResponseIntent(OAuthService service, Token requestToken,
                                                 String currentURL, String resourceURL, String serviceName) {
        Log.d(LOG_PREFIX, "Entering 'retrieveServiceResponseData'");

        String responseBody = null;
        Token accessToken = null;
        Intent intent = new Intent();

        try {

            Uri uri = Uri.parse(currentURL);

            String verifier = uri.getQueryParameter("oauth_verifier");

            Log.d(LOG_PREFIX, "Verifier = " + verifier);

            Verifier v = new Verifier(verifier);
            Log.d(LOG_PREFIX, "Verifier after parse = " + v.getValue());
            Log.d(LOG_PREFIX, "Request token = " + requestToken.getToken());

            accessToken = service.getAccessToken(requestToken, v);

            Log.d(LOG_PREFIX, "Token = " + accessToken.getToken());

            Log.d(LOG_PREFIX, "Attempting to retrieve " + serviceName + " data");

            OAuthRequest request = new OAuthRequest(Verb.GET, resourceURL);
            service.signRequest(accessToken, request);

            Log.d(LOG_PREFIX, "Parameters on request set, attempting to get response");

            Response response = request.send();

            Log.d(LOG_PREFIX, "Response returned");

            responseBody = response.getBody();

            Log.d(LOG_PREFIX, "Response contents: " + responseBody);
        }catch (OAuthException e) {
            e.printStackTrace();
        }

        if(responseBody == null) {
            responseBody = "ERROR:  " + serviceName + " data could not be retrieved";
        }else if(responseBody.isEmpty()) {
            responseBody = "ERROR: " + serviceName + " data was empty when returned";
        }else {

            Log.d(LOG_PREFIX, "Initiating new Intent");

            if (accessToken != null && !accessToken.isEmpty()) {
                intent.putExtra("access_token", accessToken.getToken());
                intent.putExtra("access_secret", accessToken.getSecret());
                intent.putExtra(serviceName.toLowerCase() + "Response", responseBody);

                Log.d(LOG_PREFIX, "Intent successfully populated with access_token = " +
                        accessToken.getToken() + "\naccess_secret = " +
                        accessToken.getSecret() + "\n" + serviceName.toLowerCase() + "Response = " +
                        responseBody);
            }else {
                Log.e(LOG_PREFIX, "ERROR: accessToken was null or empty");
            }
        }

        Log.d(LOG_PREFIX, "Exiting 'retrieveServiceResponseData'");

        return intent;
    }
}
