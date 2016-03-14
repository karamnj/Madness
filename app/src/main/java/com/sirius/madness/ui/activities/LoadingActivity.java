package com.sirius.madness.ui.activities;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.provider.IgniteDatabase;
import com.sirius.madness.receiver.models.BluemixAvnetService;
import com.sirius.madness.receiver.models.BluemixEvent;
import com.sirius.madness.receiver.models.BluemixImageBinary;
import com.sirius.madness.receiver.models.BluemixImageMetaData;
import com.sirius.madness.receiver.models.BluemixPartner;
import com.sirius.madness.receiver.models.BluemixPresenter;
import com.sirius.madness.receiver.models.BluemixPromotion;
import com.sirius.madness.receiver.models.BluemixSession;
import com.sirius.madness.receiver.models.BluemixSessionCategory;
import com.sirius.madness.receiver.models.BluemixSponsor;
import com.sirius.madness.receiver.models.BluemixSurveyQuestions;
import com.sirius.madness.receiver.models.LocalAvnetService;
import com.sirius.madness.receiver.models.LocalEvent;
import com.sirius.madness.receiver.models.LocalImageBinary;
import com.sirius.madness.receiver.models.LocalImageMetaData;
import com.sirius.madness.receiver.models.LocalPartner;
import com.sirius.madness.receiver.models.LocalPresenter;
import com.sirius.madness.receiver.models.LocalPromotion;
import com.sirius.madness.receiver.models.LocalSession;
import com.sirius.madness.receiver.models.LocalSessionCategory;
import com.sirius.madness.receiver.models.LocalSponsor;
import com.sirius.madness.receiver.models.LocalSurveyQuestions;
import com.sirius.madness.util.BlueListApplication;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by 039398 on 2/18/15.
 */
public class LoadingActivity extends BaseActivity {

    private static final String CLASS_NAME = LoadingActivity.class.getSimpleName();

    private LoadingActivity currentObject;
    ProgressBar bar;
    SharedPreferences settings;
    boolean isInternetReachable;
    boolean updateComplete;

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        currentObject = this;
        Log.d(CLASS_NAME, "Set current object");

        blApplication = (BlueListApplication) getApplication();
        Log.d(CLASS_NAME, "Set Bluemix application");

        bar = (ProgressBar) findViewById(R.id.loading_progress);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        boolean hasBluemixData = settings.getBoolean("initial_setup_complete", false);

        updateComplete = settings.getBoolean("update_complete", true);
        Log.d("UpdateComplete",String.valueOf(updateComplete));
        if(!updateComplete){
            IgniteDatabase db = new IgniteDatabase(this);
            db.upgradeDatabase(db.getWritableDatabase());
            hasBluemixData = false;
        }
        Log.d(CLASS_NAME, "initial setup completed: " + hasBluemixData);

        if(hasBluemixData == false) {

            AsyncTask<Void, Void, Boolean> internetCheck = new InternetConnectionTestTask().execute();

            try {
                internetCheck.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.d(CLASS_NAME, "No Bluemix data detected, checking internet connection");

            if(isInternetReachable) {
                Log.d(CLASS_NAME, "Internet connection detected, getting data");
                getBlueMixData();
            }else {
                Log.d(CLASS_NAME, "Could not connect to internet, displaying warning to user");
                bar.setProgress(100);
                Boolean viewedPromotions = settings.getBoolean("viewedPromotions", false);
                if (!viewedPromotions) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("viewedPromotions", true);
                    editor.commit();
                    Intent promoIntent = new Intent(LoadingActivity.this, PromotionsActivity.class);
                    startActivity(promoIntent);
                } else {
                    Intent intent = new Intent(LoadingActivity.this, LoginTutorial02Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }else {
            bar.setProgress(100);
            Boolean viewedPromotions = settings.getBoolean("viewedPromotions", false);
            if (!viewedPromotions) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("viewedPromotions", true);
                editor.commit();
                Intent promoIntent = new Intent(LoadingActivity.this, PromotionsActivity.class);
                startActivity(promoIntent);
            } else {
                Intent intent = new Intent(LoadingActivity.this, LoginTutorial02Activity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void setupActionBar() {
        getSupportActionBar().hide();
    }

    public void getBlueMixData(){
        Log.d(CLASS_NAME, "Beginning to pull Bluemix data");
        getImageBinary();
    }

    public void getImageBinary(){
        try {
            Log.d(CLASS_NAME, "Beginning ImageBinary query");
            IBMQuery<BluemixImageBinary> query = IBMQuery.queryForClass(BluemixImageBinary.class);
            // Query all the BluemixImageBinary objects from the server
            query.find().continueWith(new Continuation<List<BluemixImageBinary>, Void>() {
                @Override
                public Void then(Task<List<BluemixImageBinary>> task) throws Exception {
                    final List<BluemixImageBinary> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixImageBinary : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixImageBinary : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.ImageBinary.CONTENT_URI;

                        Log.d(CLASS_NAME, "Parsing BluemixImageBinary objects");

                        for (IBMDataObject BluemixImageBinary : objects) {
                            try {
                                BluemixImageBinary item = (BluemixImageBinary) BluemixImageBinary;
                                item.initialize();
                                LocalImageBinary imageBinary = item.convertToLocal();
                                ContentValues values = new ContentValues();

                                values.put(IgniteContract.ImageBinary.IMAGE_ID, imageBinary.getImageId());
                                values.put(IgniteContract.ImageBinary.IMAGE_DATA, imageBinary.getImageData());
                                Log.d(CLASS_NAME,imageBinary.getImageData().toString());
                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading an image : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                    }
                    currentObject.getImageMetaData();
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixImageBinary");
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixImageBinary : " + error);
        }
    }

    public void getImageMetaData(){
        try {

            IBMQuery<BluemixImageMetaData> query = IBMQuery.queryForClass(BluemixImageMetaData.class);
            // Query all the BluemixImageMetaData objects from the server
            query.find().continueWith(new Continuation<List<BluemixImageMetaData>, Void>() {
                @Override
                public Void then(Task<List<BluemixImageMetaData>> task) throws Exception {
                    final List<BluemixImageMetaData> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixImageMetaData : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixImageMetaData : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.ImageMetaData.CONTENT_URI;
                        for (IBMDataObject BluemixImageMetaData : objects) {
                            try {
                                BluemixImageMetaData item = (BluemixImageMetaData) BluemixImageMetaData;
                                item.initialize();
                                LocalImageMetaData imageMetaData = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.ImageMetaData.IMAGE_ID, imageMetaData.getImageId());
                                values.put(IgniteContract.ImageMetaData.IMAGE_NAME, imageMetaData.getImageName());
                                values.put(IgniteContract.ImageMetaData.IMAGE_EXTENSION, imageMetaData.getImageExtension());
                                values.put(IgniteContract.ImageMetaData.IMAGE_HEIGHT, imageMetaData.getImageHeight());
                                values.put(IgniteContract.ImageMetaData.IMAGE_WIDTH, imageMetaData.getImageWidth());
                                values.put(IgniteContract.ImageMetaData.UPLOADED_DATE, imageMetaData.getUploadedDate().getTime());
                                values.put(IgniteContract.ImageMetaData.MODIFIED_DATE, imageMetaData.getModifiedDate().getTime());

                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a metadata from an image : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getSpeakers();
                        currentObject.getPromotions();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixImageMetaData");
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixImageMetaData : " + error.getMessage());
        }
    }

    public void getPromotions(){
        try {

            IBMQuery<BluemixPromotion> query = IBMQuery.queryForClass(BluemixPromotion.class);
            // Query all the BluemixPresenter objects from the server
            query.find().continueWith(new Continuation<List<BluemixPromotion>, Void>() {
                @Override
                public Void then(Task<List<BluemixPromotion>> task) throws Exception {
                    Log.d(CLASS_NAME, "Entering then");
                    final List<BluemixPromotion> objects = task.getResult();

                    Log.d(CLASS_NAME, "Checking task result");
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixPromotion : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        task.getError().printStackTrace();
                        Log.e(CLASS_NAME, "Exception in BluemixPromotion 1 : " + task.getError().getMessage());
                    } else {
                        Log.d(CLASS_NAME, "Created ArrayList");
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Log.d(CLASS_NAME, "Created URI");
                        Uri uri = IgniteContract.Promotions.CONTENT_URI;
                        Log.d(CLASS_NAME, "Attempting to check objects");
                        for (IBMDataObject BluemixPromotionItem : objects) {
                            try {
                                BluemixPromotion item = (BluemixPromotion) BluemixPromotionItem;
                                item.initialize();
                                LocalPromotion promotion = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Promotions.PROMOTION_SHOW_FROM, promotion.getShowFrom().getTime());
                                values.put(IgniteContract.Promotions.PROMOTION_SHOW_TO, promotion.getShowTo().getTime());
                                values.put(IgniteContract.Promotions.PROMOTION_RSVP_LINK, promotion.getRsvpLink());
                                values.put(IgniteContract.Promotions.PROMOTION_IMAGE, Long.valueOf(promotion.getImageId()));
                                values.put(IgniteContract.Promotions.PROMOTION_CALENDAREVENTID, promotion.getCalendarId());
                                values.put(IgniteContract.Promotions.PROMOTION_EVENTTITLE, promotion.getEventTitle());
                                values.put(IgniteContract.Promotions.PROMOTION_VENUE, promotion.getVenue());
                                values.put(IgniteContract.Promotions.PROMOTION_ID, Integer.valueOf(promotion.getPromotionId()));
                                values.put(IgniteContract.Promotions.PROMOTION_FROMTIME, promotion.getFromTime().getTime());
                                values.put(IgniteContract.Promotions.PROMOTION_TOTIME, promotion.getToTime().getTime());
                                values.put(IgniteContract.Promotions.PROMOTION_LATITUDE, promotion.getLocation().getLatitude() + "");
                                values.put(IgniteContract.Promotions.PROMOTION_LONGITUDE, promotion.getLocation().getLongitude() + "");

                                Log.d(CLASS_NAME, "Adding " + IgniteContract.Promotions.PROMOTION_RSVP_LINK);
                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                      .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a promotion : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixPromotion");
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixPromotion 2 : " + error.getMessage());
        }
    }

    public void getSpeakers(){
        try {

            IBMQuery<BluemixPresenter> query = IBMQuery.queryForClass(BluemixPresenter.class);
            // Query all the BluemixPresenter objects from the server
            query.find().continueWith(new Continuation<List<BluemixPresenter>, Void>() {
                @Override
                public Void then(Task<List<BluemixPresenter>> task) throws Exception {
                    final List<BluemixPresenter> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixPresenter : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixPresenter : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.Presenters.CONTENT_URI;
                        for (IBMDataObject BluemixSpeakerItem : objects) {
                            try {
                                BluemixPresenter item = (BluemixPresenter) BluemixSpeakerItem;
                                item.initialize();
                                LocalPresenter presenter = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Presenters.PRESENTER_ID, presenter.getPresenterId());
                                values.put(IgniteContract.Presenters.PRESENTER_FIRST_NAME, presenter.getFirstName());
                                values.put(IgniteContract.Presenters.PRESENTER_LAST_NAME, presenter.getLastName());
                                /*values.put(IgniteContract.Presenters.PRESENTER_DESIGNATION, presenter.getDesignation());
                                values.put(IgniteContract.Presenters.PRESENTER_EMAIL, presenter.getEmail());
                                values.put(IgniteContract.Presenters.PRESENTER_PHONE, presenter.getPhone());
                                values.put(IgniteContract.Presenters.PRESENTER_IMAGE, Long.valueOf(presenter.getImage()));
                                values.put(IgniteContract.Presenters.PRESENTER_SHORT_DESCRIPTION, presenter.getShortDesc());
                                values.put(IgniteContract.Presenters.PRESENTER_LONG_DESCRIPTION, presenter.getLongDesc());
                                values.put(IgniteContract.Presenters.PRESENTER_LINKED_IN_LINK, presenter.getLinkedInLink());
                                values.put(IgniteContract.Presenters.PRESENTER_TWITTER_LINK, presenter.getTwitterLink());*/

                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a speaker : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getSessions();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixPresenter");
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixPresenter : " + error.getMessage());
        }
    }

    public void getSessions(){
        try {

            IBMQuery<BluemixSession> query = IBMQuery.queryForClass(BluemixSession.class);

            // Query all the BluemixSession objects from the server
            query.find().continueWith(new Continuation<List<BluemixSession>, Void>() {
                @Override
                public Void then(Task<List<BluemixSession>> task) throws Exception {
                    final List<BluemixSession> objects = task.getResult();
                    Log.d("SessionsBluemixCount", objects.size()+"");

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSession : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception BluemixSession : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri sessionUri = IgniteContract.Sessions.CONTENT_URI;
                        Uri sessionPresenterMapUri = IgniteContract.SessionsPresenters.CONTENT_URI;
                        for (IBMDataObject BluemixSessionItem : objects) {
                            try {
                                BluemixSession item = (BluemixSession) BluemixSessionItem;
                                item.initialize();
                                LocalSession session = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Sessions.SESSION_ID, session.getSessionId());
                                values.put(IgniteContract.Sessions.SESSION_SHORT_TITLE, session.getShortTitle());
                                values.put(IgniteContract.Sessions.SESSION_LONG_TITLE, session.getLongTitle());
                                values.put(IgniteContract.Sessions.SESSION_SHORT_DESCRIPTION, session.getShortDesc());
                                values.put(IgniteContract.Sessions.SESSION_LONG_DESCRIPTION, session.getLongDesc());
                                values.put(IgniteContract.Sessions.SESSION_HALL, session.getHall());
                                values.put(IgniteContract.Sessions.SESSION_IMAGE, session.getImage());
                                values.put(IgniteContract.Sessions.SESSION_FROM_TIME, session.getFromTime().getTime());
                                values.put(IgniteContract.Sessions.SESSION_TO_TIME, session.getToTime().getTime());
                                values.put(IgniteContract.Sessions.SESSION_LATITUDE, session.getLocation().getLatitude() + "");
                                values.put(IgniteContract.Sessions.SESSION_LONGITUDE, session.getLocation().getLongitude() + "");
                                values.put(IgniteContract.Sessions.SESSION_IS_HIDDEN, session.getIsHidden());
                                values.put(IgniteContract.Sessions.SESSION_IS_GENERAL_SESSION, session.getIsGeneralSession());
                                values.put(IgniteContract.Sessions.SESSION_SLOT, session.getSlot());

                                operations.add(ContentProviderOperation
                                        .newInsert(sessionUri)
                                        .withValues(values)
                                        .build());
                                Log.d("LocalSession",session.toString());

                                int presenters[] = session.getPresenters();
                                for (int i = 0; i < presenters.length; i++) {
                                    ContentValues presenterValues = new ContentValues();
                                    presenterValues.put(IgniteContract.SessionsPresenters.SESSION_ID, session.getSessionId());
                                    presenterValues.put(IgniteContract.SessionsPresenters.PRESENTER_ID, presenters[i]);

                                    operations.add(ContentProviderOperation
                                            .newInsert(sessionPresenterMapUri)
                                            .withValues(presenterValues)
                                            .build());
                                }
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a session : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getSessionCategories();
                        currentObject.getPartners();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixSession");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixSession : " + error.getMessage());
        }
    }

    public void getSessionCategories(){
        try {

            IBMQuery<BluemixSessionCategory> query = IBMQuery.queryForClass(BluemixSessionCategory.class);
            // Query all the BluemixSession objects from the server
            query.find().continueWith(new Continuation<List<BluemixSessionCategory>, Void>() {
                @Override
                public Void then(Task<List<BluemixSessionCategory>> task) throws Exception {
                    final List<BluemixSessionCategory> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSessionCategory : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSessionCategory : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> categoryInsertOperations = new ArrayList<ContentProviderOperation>();
                        ArrayList<ContentProviderOperation> sessionsUpdateOperations = new ArrayList<ContentProviderOperation>();
                        Uri categoryUri = IgniteContract.Categories.CONTENT_URI;
                        for (IBMDataObject BluemixCategoryItem : objects) {
                            BluemixSessionCategory item = (BluemixSessionCategory) BluemixCategoryItem;
                            item.initialize();
                            LocalSessionCategory category = item.convertToLocal();
                            ContentValues values = new ContentValues();
                            values.put(IgniteContract.Categories.CATEGORY_ID, category.getCategoryId());
                            values.put(IgniteContract.Categories.CATEGORY_NAME, category.getCategoryName());

                            categoryInsertOperations.add(ContentProviderOperation
                                    .newInsert(categoryUri)
                                    .withValues(values)
                                    .build());

                            int sessions[] = category.getSessions();
                            for (int i = 0; i < sessions.length; i++) {
                                try {
                                    Log.d("Category", "" + category.getCategoryId() + "-" + sessions[i]);
                                    Uri sessionUri = IgniteContract.Sessions.buildSessionUri(sessions[i]);
                                    ContentValues sessionValues = new ContentValues();
                                    sessionValues.put(IgniteContract.Sessions.CATEGORY_ID_FK, category.getCategoryId());
                                    sessionsUpdateOperations.add(ContentProviderOperation
                                            .newUpdate(sessionUri)
                                            .withValues(sessionValues)
                                            .build());
                                }catch (Exception error) {
                                    Log.e(CLASS_NAME, "Exception loading a session category : " + error.getMessage());
                                }
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, categoryInsertOperations);
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, sessionsUpdateOperations);
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixSessionCategory");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixSessionCategory : " + error.getMessage());
        }
    }

    public void getPartners(){
        try {

            IBMQuery<BluemixPartner> query = IBMQuery.queryForClass(BluemixPartner.class);
            // Query all the BluemixSession objects from the server
            query.find().continueWith(new Continuation<List<BluemixPartner>, Void>() {
                @Override
                public Void then(Task<List<BluemixPartner>> task) throws Exception {
                    final List<BluemixPartner> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixPartner : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixPartner : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri partnerUri = IgniteContract.Partners.CONTENT_URI;
                        for (IBMDataObject bluemixPartnerItem : objects) {
                            try {
                                BluemixPartner item = (BluemixPartner) bluemixPartnerItem;
                                item.initialize();
                                LocalPartner partner = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Partners.PARTNER_ID, partner.getPartnerId());
                                values.put(IgniteContract.Partners.PARTNER_IMAGE, Long.valueOf(partner.getLogo()));
                                values.put(IgniteContract.Partners.PROMOTION_IMAGE, Long.valueOf(partner.getPromotionImage()));
                                values.put(IgniteContract.Partners.PARTNER_SHORT_TITLE, partner.getShortTitle());
                                values.put(IgniteContract.Partners.PARTNER_LONG_TITLE, partner.getLongTitle());
                                values.put(IgniteContract.Partners.PARTNER_SHORT_DESCRIPTION, partner.getShortDesc());
                                values.put(IgniteContract.Partners.PARTNER_LONG_DESCRIPTION, partner.getLongDesc());
                                values.put(IgniteContract.Partners.PARTNER_LINKEDIN_LINK, partner.getLinkedInLink());
                                values.put(IgniteContract.Partners.PARTNER_TWITTER_HASH_TAG, partner.getTwitterHashTag());
                                values.put(IgniteContract.Partners.PARTNER_WEBSITE_LINK, partner.getWebsiteLink());
                                values.put(IgniteContract.Partners.PARTNER_IS_PARTNER, (partner.isPartner() == true ? 1 : 0));

                                operations.add(ContentProviderOperation
                                        .newInsert(partnerUri)
                                        .withValues(values)
                                        .build());

                                int presenters[] = partner.getPresenters();
                                for (int i = 0; i < presenters.length; i++) {
                                    Uri presenterUri = IgniteContract.Presenters.buildPresenterUri(presenters[i]);
                                    ContentValues presenterValues = new ContentValues();
                                    presenterValues.put(IgniteContract.Presenters.PARTNER_ID_FK, partner.getPartnerId());
                                    operations.add(ContentProviderOperation
                                            .newUpdate(presenterUri)
                                            .withValues(presenterValues)
                                            .build());
                                }

                                int sessions[] = partner.getSessions();
                                for (int i = 0; i < sessions.length; i++) {
                                    Uri sessionUri = IgniteContract.Sessions.buildSessionUri(sessions[i]);
                                    ContentValues sessionValues = new ContentValues();
                                    sessionValues.put(IgniteContract.Sessions.PARTNER_ID_FK, partner.getPartnerId());
                                    operations.add(ContentProviderOperation
                                            .newUpdate(sessionUri)
                                            .withValues(sessionValues)
                                            .build());
                                }
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a partner : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getSponsors();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixPartner");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixPartner: " + error.getMessage());
        }
    }

    public void getSponsors(){
        try {

            IBMQuery<BluemixSponsor> query = IBMQuery.queryForClass(BluemixSponsor.class);
            // Query all the BluemixSession objects from the server
            query.find().continueWith(new Continuation<List<BluemixSponsor>, Void>() {
                @Override
                public Void then(Task<List<BluemixSponsor>> task) throws Exception {
                    final List<BluemixSponsor> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSponsor : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSponsor : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri sponsorUri = IgniteContract.Sponsors.CONTENT_URI;
                        for (IBMDataObject bluemixSponsorItem : objects) {
                            try {
                                BluemixSponsor item = (BluemixSponsor) bluemixSponsorItem;
                                item.initialize();
                                LocalSponsor sponsor = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Sponsors.SPONSOR_ID, sponsor.getSponsorId());
                                values.put(IgniteContract.Sponsors.SPONSOR_LOGO, Long.valueOf(sponsor.getLogo()));
                                values.put(IgniteContract.Sponsors.SPONSOR_NAME, sponsor.getName());
                                values.put(IgniteContract.Sponsors.SPONSOR_WEBSITE_LINK, sponsor.getWebsiteLink());
                                values.put(IgniteContract.Sponsors.SPONSOR_CATEGORY, sponsor.getCategory());

                                operations.add(ContentProviderOperation
                                        .newInsert(sponsorUri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a sponsor : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getSurveyQuestions();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixSponsor");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixSponsor: " + error.getMessage());
        }
    }
    public void getSurveyQuestions(){
        try {

            IBMQuery<BluemixSurveyQuestions> query = IBMQuery.queryForClass(BluemixSurveyQuestions.class);
            // Query all the BluemixSurveyQuestions objects from the server
            query.find().continueWith(new Continuation<List<BluemixSurveyQuestions>, Void>() {
                @Override
                public Void then(Task<List<BluemixSurveyQuestions>> task) throws Exception {
                    final List<BluemixSurveyQuestions> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSurveyQuestions : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSurveyQuestions : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri sponsorUri = IgniteContract.SurveyQuestions.CONTENT_URI;
                        for (IBMDataObject bluemixSurveyItem : objects) {
                            try {
                                BluemixSurveyQuestions item = (BluemixSurveyQuestions) bluemixSurveyItem;
                                item.initialize();
                                LocalSurveyQuestions surveyQuestions = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QID, surveyQuestions.getQid());
                                values.put(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QUESTION, surveyQuestions.getQuestion());
                                operations.add(ContentProviderOperation
                                        .newInsert(sponsorUri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a survey question : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getAvnetServices();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixSurveyQuestions");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixSurveyQuestions: " + error.getMessage());
        }
    }

    public void getAvnetServices(){
        try {

            IBMQuery<BluemixAvnetService> query = IBMQuery.queryForClass(BluemixAvnetService.class);
            // Query all the BluemixAvnetService objects from the server
            query.find().continueWith(new Continuation<List<BluemixAvnetService>, Void>() {
                @Override
                public Void then(Task<List<BluemixAvnetService>> task) throws Exception {
                    final List<BluemixAvnetService> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixAvnetService : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixAvnetService : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri AvnetServicesUri = IgniteContract.AvnetServices.CONTENT_URI;
                        for (IBMDataObject bluemixAvnetItem : objects) {
                            try {
                                BluemixAvnetService item = (BluemixAvnetService) bluemixAvnetItem;
                                item.initialize();
                                LocalAvnetService AvnetService = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_TITLE, AvnetService.getAddressTitle());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_LINE_1, AvnetService.getAddressLine1());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_ADDRESS_LINE_2, AvnetService.getAddressLine2());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_SHORT_TITLE, AvnetService.getShortTitle());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_LONG_TITLE, AvnetService.getLongTitle());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_SHORT_DESCRIPTION, AvnetService.getShortDesc());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_LONG_DESCRIPTION, AvnetService.getLongDesc());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_LOGO, AvnetService.getLogo());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_LINKED_IN_LINK, AvnetService.getLinkedInLink());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_TWITTER_HASH_TAG, AvnetService.getTwitterHashTag());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_WEBSITE_LINK, AvnetService.getWebsiteLink());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_PHONE, AvnetService.getPhone());
                                values.put(IgniteContract.AvnetServices.AVNET_SERVICE_EMAIL, AvnetService.getEmail());

                                operations.add(ContentProviderOperation
                                        .newInsert(AvnetServicesUri)
                                        .withValues(values)
                                        .build());
                                Log.d(CLASS_NAME, AvnetServicesUri.toString());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a Avnet Details : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        currentObject.getEvents();
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixAvnetService");
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixAvnetService: " + error.getMessage());
        }
    }

    public void getEvents(){

        try {
            IBMQuery<BluemixEvent> query = IBMQuery.queryForClass(BluemixEvent.class);
            // Query all the BluemixSession objects from the server
            query.find().continueWith(new Continuation<List<BluemixEvent>, Void>() {
                @Override
                public Void then(Task<List<BluemixEvent>> task) throws Exception {
                    final List<BluemixEvent> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixEvent : Task " + task.toString() + " was cancelled.");
                    }else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception BluemixEvent : " + task.getError().getMessage());
                    }else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri eventUri = IgniteContract.Events.CONTENT_URI;
                        Uri eventPartnerUri = IgniteContract.EventsPartners.CONTENT_URI;
                        for (IBMDataObject bluemixEventItem : objects) {
                            try {
                                BluemixEvent item = (BluemixEvent) bluemixEventItem;
                                item.initialize();
                                LocalEvent event = item.convertToLocal();
                                ContentValues values = new ContentValues();

                                values.put(IgniteContract.Events.EVENT_ID,event.getEventId());
                                values.put(IgniteContract.Events.EVENT_IMAGE,event.getImage());
                                values.put(IgniteContract.Events.EVENT_MAP,event.getMap());
                                values.put(IgniteContract.Events.EVENT_SHORT_TITLE,event.getShortDesc());
                                values.put(IgniteContract.Events.EVENT_LONG_TITLE,event.getLongDesc());
                                values.put(IgniteContract.Events.EVENT_SHORT_DESCRIPTION,event.getShortDesc());
                                values.put(IgniteContract.Events.EVENT_LONG_DESCRIPTION, event.getLongDesc());
                                values.put(IgniteContract.Events.EVENT_HALL,event.getHall());
                                values.put(IgniteContract.Events.EVENT_FROM_TIME,event.getFromTime().getTime());
                                values.put(IgniteContract.Events.EVENT_TO_TIME,event.getToTime().getTime());
                                values.put(IgniteContract.Events.EVENT_LATITUDE,event.getLocation().getLatitude());
                                values.put(IgniteContract.Events.EVENT_LONGITUDE,event.getLocation().getLongitude());

                                operations.add(ContentProviderOperation
                                        .newInsert(eventUri)
                                        .withValues(values)
                                        .build());

                                int partners[] = event.getPartners();
                                for(int i=0; i<partners.length;i++){
                                    ContentValues eventsPartnersValues = new ContentValues();
                                    eventsPartnersValues.put(IgniteContract.EventsPartners.EVENT_ID,event.getEventId());
                                    eventsPartnersValues.put(IgniteContract.EventsPartners.PARTNER_ID,partners[i]);
                                    operations.add(ContentProviderOperation
                                            .newInsert(eventPartnerUri)
                                            .withValues(eventsPartnersValues)
                                            .build());
                                }

                                int sessions[] = event.getSessions();
                                for(int i=0; i<sessions.length;i++){
                                    Uri sessionUri = IgniteContract.Sessions.buildSessionUri(sessions[i]);
                                    ContentValues sessionValues = new ContentValues();
                                    sessionValues.put(IgniteContract.Sessions.EVENT_ID_FK,event.getEventId());
                                    operations.add(ContentProviderOperation
                                            .newUpdate(sessionUri)
                                            .withValues(sessionValues)
                                            .build());
                                }
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading an event : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        //currentObject.getSchedules();
                        Boolean viewedPromotions = settings.getBoolean("viewedPromotions", false);
                        if (!viewedPromotions) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("viewedPromotions", true);
                            editor.commit();
                            Intent promoIntent = new Intent(LoadingActivity.this, PromotionsActivity.class);
                            startActivity(promoIntent);
                        } else {
                            Intent intent = new Intent(LoadingActivity.this, LoginTutorial02Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixEvent");

                    // record the fact that the app has completed first time initial set up
                    settings.edit().putBoolean("initial_setup_complete", true).commit();
                    if(!updateComplete) {
                        settings.edit().putBoolean("update_complete", true).commit();
                        Toast.makeText(LoadingActivity.this, "The Ignite Backend has been updated.", Toast.LENGTH_LONG).show();
                        reschedule();
                    }
                    finish();
                    return null;
                }
            });
        }catch (Exception error) {
            Log.e(CLASS_NAME, "Exception in BluemixEvent : " + error.getMessage());
        }
    }

    /*public void getSchedules(){
        try {

            IBMQuery<BluemixSchedule> query = IBMQuery.queryForClass(BluemixSchedule.class);
            // Query all the BluemixPresenter objects from the server
            query.find().continueWith(new Continuation<List<BluemixSchedule>, Void>() {
                @Override
                public Void then(Task<List<BluemixSchedule>> task) throws Exception {
                    final List<BluemixSchedule> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSchedule : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixSchedule : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.Schedules.CONTENT_URI;
                        for (IBMDataObject BluemixScheduleItem : objects) {
                            try {
                                BluemixSchedule item = (BluemixSchedule) BluemixScheduleItem;
                                item.initialize();
                                LocalSchedule schedule = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Schedules.EVENT_ID_FK, schedule.getEventId());
                                values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, schedule.getCalendarEventId());
                                values.put(IgniteContract.Schedules.EVENT_TITLE, schedule.getEventTitle());
                                values.put(IgniteContract.Schedules.FROM_TIME, schedule.getFromTime().getTime());
                                values.put(IgniteContract.Schedules.TO_TIME, schedule.getToTime().getTime());
                                values.put(IgniteContract.Schedules.MANDATORY, "true");

                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            }catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a schedule : " + error.getMessage());
                            }
                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        //currentObject.getDevelopers();

                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixSchedule");

                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixSchedule : " + error.getMessage());
        }
    }

    public void getDevelopers(){
        try {

            IBMQuery<BluemixDeveloper> query = IBMQuery.queryForClass(BluemixDeveloper.class);
            // Query all the BluemixPresenter objects from the server
            query.find().continueWith(new Continuation<List<BluemixDeveloper>, Void>() {
                @Override
                public Void then(Task<List<BluemixDeveloper>> task) throws Exception {
                    final List<BluemixDeveloper> objects = task.getResult();

                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception in BluemixDeveloper : Task " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception in BluemixDeveloper : " + task.getError().getMessage());
                    } else {
                        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                        Uri uri = IgniteContract.Developers.CONTENT_URI;
                        for (IBMDataObject BluemixDeveloperItem : objects) {
                            try {
                                BluemixDeveloper item = (BluemixDeveloper) BluemixDeveloperItem;
                                item.initialize();
                                LocalDeveloper developer = item.convertToLocal();
                                ContentValues values = new ContentValues();
                                values.put(IgniteContract.Developers.DEVELOPER_ID, developer.getDeveloperId());
                                values.put(IgniteContract.Developers.PARTNER_ID_FK, developer.getPartnerId());
                                values.put(IgniteContract.Developers.DEVELOPER_FIRST_NAME, developer.getFirstName());
                                values.put(IgniteContract.Developers.DEVELOPER_LAST_NAME, developer.getLastName());
                                values.put(IgniteContract.Developers.DEVELOPER_EMAIL, developer.getEmail());
                                values.put(IgniteContract.Developers.DEVELOPER_PHONE, developer.getPhone());
                                values.put(IgniteContract.Developers.DEVELOPER_IMAGE, developer.getImage());
                                values.put(IgniteContract.Developers.DEVELOPER_SHORT_DESCRIPTION, developer.getShortDesc());
                                values.put(IgniteContract.Developers.DEVELOPER_LONG_DESCRIPTION, developer.getLongDesc());
                                values.put(IgniteContract.Developers.DEVELOPER_LINKED_IN_ID, developer.getLinkedInId());
                                values.put(IgniteContract.Developers.DEVELOPER_LINKED_IN_LINK, developer.getLinkedInLink());
                                values.put(IgniteContract.Developers.DEVELOPER_TWITTER_ID, developer.getTwitterId());
                                values.put(IgniteContract.Developers.DEVELOPER_TWITTER_LINK, developer.getTwitterLink());
                                values.put(IgniteContract.Developers.DEVELOPER_DESIGNATION, developer.getDesignation());

                                operations.add(ContentProviderOperation
                                        .newInsert(uri)
                                        .withValues(values)
                                        .build());
                            } catch (Exception error) {
                                Log.e(CLASS_NAME, "Exception loading a developer : " + error.getMessage());
                            }

                        }
                        getContentResolver().applyBatch(IgniteContract.CONTENT_AUTHORITY, operations);
                        refreshUI();

                        Boolean viewedPromotions = settings.getBoolean("viewedPromotions", false);
                        if (!viewedPromotions) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("viewedPromotions", true);
                            editor.commit();
                            Intent promoIntent = new Intent(LoadingActivity.this, PromotionsActivity.class);
                            startActivity(promoIntent);
                        } else {
                            Intent intent = new Intent(LoadingActivity.this, LoginTutorial02Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR).onSuccess(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> voidTask) throws Exception {
                    updateLoadingBar("BluemixDeveloper");

                    // record the fact that the app has completed first time initial set up
                    settings.edit().putBoolean("initial_setup_complete", true).commit();
                    if (!updateComplete) {
                        settings.edit().putBoolean("update_complete", true).commit();
                        Toast.makeText(LoadingActivity.this, "The Ignite Backend has been updated.", Toast.LENGTH_LONG).show();
                        reschedule();
                    }
                    finish();
                    return null;
                }
            });

        }  catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception in BluemixDeveloper : " + error.getMessage());
        }
    }*/

    private void reschedule(){
        Uri baseUri = Uri.parse("content://com.android.calendar/events");
        //Session Preferences
        SharedPreferences prefs = settings;
        JSONArray sessionJSON;
        String prefJson = prefs.getString("sessionJSON",null);
        if(prefJson != null){
            try {
                sessionJSON = new JSONArray(prefJson);
                for (int i = 0; i < sessionJSON.length(); i++) {
                    JSONObject explrObject = sessionJSON.getJSONObject(i);
                    if(explrObject.getInt("SessionId") != 0){
                        final ContentValues event = new ContentValues();
                        event.put(CalendarContract.Events.CALENDAR_ID, 1);
                        event.put(CalendarContract.Events.TITLE, explrObject.getString("SessionName"));
                        event.put(CalendarContract.Events.DESCRIPTION, explrObject.getString("SessionDescription"));
                        event.put(CalendarContract.Events.DTSTART, explrObject.getLong("FromTime"));
                        event.put(CalendarContract.Events.DTEND, explrObject.getLong("ToTime"));
                        event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
                        event.put(CalendarContract.Events.HAS_ALARM, 0); // 0 for false, 1 for true

                        String timeZone = TimeZone.getDefault().getID();
                        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
                        Uri uri = this.getContentResolver().insert(baseUri, event);
                        Uri scheduleUri = IgniteContract.Schedules.CONTENT_URI;
                        ContentValues values = new ContentValues();
                        values.put(IgniteContract.Schedules.CALENDAR_EVENT_ID, uri.getLastPathSegment());
                        values.put(IgniteContract.Schedules.EVENT_ID_FK, 1);
                        values.put(IgniteContract.Schedules.SESSION_ID_FK, explrObject.getInt("SessionId"));
                        values.put(IgniteContract.Schedules.EVENT_TITLE, explrObject.getString("SessionName"));
                        values.put(IgniteContract.Schedules.FROM_TIME, explrObject.getLong("FromTime"));
                        values.put(IgniteContract.Schedules.TO_TIME, explrObject.getLong("ToTime"));
                        values.put(IgniteContract.Schedules.MANDATORY, "false");
                        this.getContentResolver().insert(scheduleUri, values);

                        // Schedule Local notification
                        String desc = "Session starts in 15 minutes.";
                        Date eventDate = new Date();
                        eventDate.setTime(explrObject.getLong("FromTime"));
                        Date eventEndDate = new Date();
                        eventEndDate.setTime(explrObject.getLong("ToTime"));
                        ((BaseActivity) this).scheduleLocalNotification(explrObject.getString("SessionName"), desc, eventDate, eventEndDate, explrObject.getInt("SessionId"));

                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //sessionJSON.put(eventJSON);
        //prefs.edit().putString("sessionJSON", sessionJSON.toString()).commit();
    }
    private void updateLoadingBar(String nameOfClass) {
        Log.d(CLASS_NAME, "Successfully loaded data from: " + nameOfClass);

        bar.incrementProgressBy(10);
    }
    public void updateOnNotification(){
        AsyncTask<Void, Void, Boolean> internetCheck = new InternetConnectionTestTask().execute();

        try {
            internetCheck.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(isInternetReachable) {
            Log.d(CLASS_NAME, "Internet connection detected, getting data");
            getBlueMixData();
        }
    }

    class InternetConnectionTestTask extends AsyncTask<Void, Void, Boolean> {

        private static final String SUB_CLASS = "InternetConnectionTest";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(SUB_CLASS, "Entering isInternetReachable");

            isInternetReachable = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            isInternetReachable = false;
            InetAddress[] addr;

            try {
                addr = InetAddress.getAllByName("google.com");

                if (addr != null && addr.length > 0) {
                    Log.d(CLASS_NAME, addr[0].toString());
                    Log.d(CLASS_NAME, "Size: " + addr.length);
                    isInternetReachable = true;
                } else {
                    isInternetReachable = false;
                }

                Log.d(SUB_CLASS, "isInternetReachable returned " + String.valueOf(isInternetReachable));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(SUB_CLASS, "Exiting isInternetReachable");

            return isInternetReachable;
        }
    }
}
