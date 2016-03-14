package com.sirius.madness.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sirius.madness.receiver.models.BluemixAvnetService;
import com.sirius.madness.receiver.models.BluemixDeveloper;
import com.sirius.madness.receiver.models.BluemixEvent;
import com.sirius.madness.receiver.models.BluemixImageBinary;
import com.sirius.madness.receiver.models.BluemixImageMetaData;
import com.sirius.madness.receiver.models.BluemixParticipant;
import com.sirius.madness.receiver.models.BluemixPartner;
import com.sirius.madness.receiver.models.BluemixPresenter;
import com.sirius.madness.receiver.models.BluemixPromotion;
import com.sirius.madness.receiver.models.BluemixSchedule;
import com.sirius.madness.receiver.models.BluemixSession;
import com.sirius.madness.receiver.models.BluemixSessionCategory;
import com.sirius.madness.receiver.models.BluemixSessionParticipants;
import com.sirius.madness.receiver.models.BluemixSponsor;
import com.sirius.madness.receiver.models.BluemixSurveyAnswers;
import com.sirius.madness.receiver.models.BluemixSurveyQuestions;
import com.sirius.madness.receiver.models.BluemixUserSchedules;
import com.sirius.madness.ui.activities.BaseActivity;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.push.IBMPush;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import bolts.Continuation;
import bolts.Task;
import io.fabric.sdk.android.Fabric;

/**
 * This class contains the setup and wrapper functions for IBMBluemix core
 * data, and push services.
 */
public final class BlueListApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "c0wijgFtmzdBGUKDvxM4CyGst";
    private static final String TWITTER_SECRET = "aU4ElvmTyGY2W0lTuRQY31hNTdah9x4QUOCN6t3rhY8ch8VvUA";
	public static final int EDIT_ACTIVITY_RC = 1;
	public static IBMPush push = null;
    private Activity mActivity;
	private static final String CLASS_NAME = BlueListApplication.class.getSimpleName();
	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "bluelist.properties";
    private static final String UPDATE_TAG_NAME = "Urgent Updates";

	private IBMPushNotificationListener notificationListener = null;

	public BlueListApplication() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity,Bundle savedInstanceState) {
				Log.d(CLASS_NAME, "Activity created: " + activity.getLocalClassName());
				mActivity = activity;
				
				// Define IBMPushNotificationListener behavior on push notifications.
				notificationListener = new IBMPushNotificationListener() {
					@Override
					public void onReceive(final IBMSimplePushNotification message) {
                        if(mActivity instanceof BaseActivity) {
                            ((BaseActivity) mActivity).handleReceivedPushNotification(message);
                        }
					}					
				};
			}
			@Override
			public void onActivityStarted(Activity activity) {
				mActivity = activity;
				Log.d(CLASS_NAME, "Activity started: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityResumed(Activity activity) {
				mActivity = activity;
				Log.d(CLASS_NAME, "Activity resumed: " + activity.getLocalClassName());
                // Request IBMPush to deliver incoming push messages to notificationListener.onReceive() method
				if (push != null) {
					push.listen(notificationListener);
				}
			}
			@Override
			public void onActivitySaveInstanceState(Activity activity,Bundle outState) {
				Log.d(CLASS_NAME, "Activity saved instance state: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityPaused(Activity activity) {
                // Request IBMPush to stop delivering incoming push messages to notificationListener.onReceive() method.
                // After hold(), IBMPush will store the latest push message in private shared preference
                // and deliver that message during the next listen().
				if (push != null) {
					push.hold();
				}
				Log.d(CLASS_NAME, "Activity paused: " + activity.getLocalClassName());
				if (activity != null && activity.equals(mActivity))
		    		mActivity = null;
			}
			@Override
			public void onActivityStopped(Activity activity) {
				Log.d(CLASS_NAME, "Activity stopped: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityDestroyed(Activity activity) {
				Log.d(CLASS_NAME, "Activity destroyed: " + activity.getLocalClassName());
			}
		});
	}
	
	/**
	 * (non-Javadoc)
	 * Called when the application is starting, before any activity, service, 
	 * or receiver objects (excluding content providers) have been created.
	 * 
	 * @see android.app.Application#onCreate()
	 * 
	 */
	@Override
	public void onCreate() {
        Log.d(CLASS_NAME, "Entering onCreate");

		super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));

        // Check if first time app launched if so do Initialize Bluemix services
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isSetup = settings.getBoolean("initial_setup_complete", false);
        Log.d(CLASS_NAME, "Property 'initial_setup_complete' = " + isSetup);
        setUpBluemixConfiguration();
        /*if (isSetup == false) {
            //the app is being launched for first time Setup Bluemix Config for Push Notifications
            setUpBluemixConfiguration();

            // record the fact that the app has completed first time initial set up
            settings.edit().putBoolean("initial_setup_complete", true).commit();
        }*/
        initSingletons();

        Log.d(CLASS_NAME, "Exiting onCreate");
	}

    protected void initSingletons()
    {
        // Initialize the instance of Singletons
        DiscoverSingleton.initInstance();
        BreakoutSingleton.initInstance();
    }

    /**
     * This method initializes the Bluemix Core, Data, and Push services.
     * Also registers this device with the Bluemix server.
     */
    public void setUpBluemixConfiguration(){
        Log.d(CLASS_NAME, "Entering setUpBluemixConfiguration");

        // Initalize IBMBluemix and IBMPush
       initializeBluemixServices();

        // Register the Item Specialization.
        BluemixImageBinary.registerSpecialization(BluemixImageBinary.class);
        BluemixImageMetaData.registerSpecialization(BluemixImageMetaData.class);
        BluemixSchedule.registerSpecialization(BluemixSchedule.class);
        BluemixDeveloper.registerSpecialization(BluemixDeveloper.class);
        BluemixEvent.registerSpecialization(BluemixEvent.class);
        BluemixPartner.registerSpecialization(BluemixPartner.class);
        BluemixSponsor.registerSpecialization(BluemixSponsor.class);
        BluemixSurveyQuestions.registerSpecialization(BluemixSurveyQuestions.class);
        BluemixSurveyAnswers.registerSpecialization(BluemixSurveyAnswers.class);
        BluemixParticipant.registerSpecialization(BluemixParticipant.class);
        BluemixPresenter.registerSpecialization(BluemixPresenter.class);
        BluemixSession.registerSpecialization(BluemixSession.class);
        BluemixSessionParticipants.registerSpecialization(BluemixSessionParticipants.class);
        BluemixSessionCategory.registerSpecialization(BluemixSessionCategory.class);
        BluemixAvnetService.registerSpecialization(BluemixAvnetService.class);
        BluemixPromotion.registerSpecialization(BluemixPromotion.class);
        BluemixUserSchedules.registerSpecialization(BluemixUserSchedules.class);


        // Initialize the IBM Push and register device
        String deviceModel = Build.MODEL + " - " + Build.BRAND;
        String deviceId = (Build.ID != null) ? Build.ID : "";
        if(push != null) {
            push.register(deviceModel, deviceId).continueWith(new Continuation<String, Void>() {

                @Override
                public Void then(Task<String> task) throws Exception {
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Error registering with Push Service. " + task.getError().getMessage() + "\n"
                                + "Push notifications will not be received.");
                    } else {
                        Log.i(CLASS_NAME, "Device is registered with Push Service" + "\n" + "Device Id : " + task.getResult());
                        // Subscribe to Urgent Updates
                        bluemixPushSubscribeTo(UPDATE_TAG_NAME);
                    }
                    return null;
                }
            });
        }

        Log.d(CLASS_NAME, "Exiting setUpBluemixConfiguration");
    }

    /**
     * Opens bluelist.properties file and Registers Bluemix core and push services.
     */
    protected void initializeBluemixServices(){

        Log.d(CLASS_NAME, "Entering initializeBluemixServices");

        // Read from properties file.
        Properties props = new java.util.Properties();
        Context context = getApplicationContext();
        try {
            AssetManager assetManager = context.getAssets();
            props.load(assetManager.open(PROPS_FILE));
            Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
        } catch (FileNotFoundException e) {
            Log.e(CLASS_NAME, "The bluelist.properties file was not found.", e);
        } catch (IOException e) {
            Log.e(CLASS_NAME, "The bluelist.properties file could not be read properly.", e);
        }

        // Get AppId, App Secret, and App Route from properties file
        String appId = (props.getProperty(APP_ID) != null) ? props.getProperty(APP_ID) : "";
        String appSecret = (props.getProperty(APP_SECRET) != null) ? props.getProperty(APP_SECRET) : "";
        String appRoute = (props.getProperty(APP_ROUTE) != null) ? props.getProperty(APP_ROUTE) : "";

        // Initialize the IBM core backend-as-a-service with AppId, AppSecret, and AppRoute.
        IBMBluemix.initialize(this, appId, appSecret, appRoute);

        // Initialize the IBM Push service
        push = IBMPush.initializeService();

        //Initialize Bluemix Data service
        IBMData ibmDataService = IBMData.initializeService();

        Log.d(CLASS_NAME, "Exiting initializeBluemixServices");
    }

    /**
     * This method is used to subscribe to a tag with tagName.
     * @param tagName
     */
    public void bluemixPushSubscribeTo(final String tagName) {
        Log.d(CLASS_NAME, "Entering bluemixPushSubscribeTo");

         // Check if Push service has been initialized
        if(push == null) {
            initializeBluemixServices();
            Log.d(CLASS_NAME, "Successfully Initialized");
        }

        // Subscribe to Bluemix tag wth tagName
        push.subscribe(tagName).continueWith(new Continuation<String, Void>() {

            @Override
            public Void then(Task<String> task) throws Exception {
                if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Error subscribing to Tag.."
                            + task.getError().getMessage());
                    return null;
                } else {
                    Log.i(CLASS_NAME, "Successfully Subscribed to tag " + tagName);
                }
                return null;
            }
        });

        Log.d(CLASS_NAME, "Exiting bluemixPushSubscribeTo");
    }

    /**
     * This method is used to unsubscribe from a tag with tagName.
     * @param tagName
     */
    public void bluemixPushUnsubscribeFrom(final String tagName) {
        Log.d(CLASS_NAME, "Entering bluemixPushUnsubscribeFrom");

        // Check if Push service has been initialized
        if(push == null) {
            initializeBluemixServices();
        }

        // Unsubscribe from Bluemix tag wth tagName
        push.unsubscribe(tagName).continueWith(new Continuation<String, Void>() {

            @Override
            public Void then(Task<String> task) throws Exception {
                if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Error unsubscribing from Tag.."
                            + task.getError().getMessage());
                    return null;
                } else {
                    Log.i(CLASS_NAME, "Successfully Unsubscribed from " + tagName);
                }
                return null;
            }
        });

        Log.d(CLASS_NAME, "Exiting bluemixPushUnsubscribeFrom");

    }
}