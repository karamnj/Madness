package com.sirius.madness.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.sirius.madness.provider.IgniteContract.AvnetServiceColumns;
import com.sirius.madness.provider.IgniteContract.Categories;
import com.sirius.madness.provider.IgniteContract.CategoryColumns;
import com.sirius.madness.provider.IgniteContract.DeveloperColumns;
import com.sirius.madness.provider.IgniteContract.Developers;
import com.sirius.madness.provider.IgniteContract.EventColumns;
import com.sirius.madness.provider.IgniteContract.Events;
import com.sirius.madness.provider.IgniteContract.EventsPartnersColumns;
import com.sirius.madness.provider.IgniteContract.PartnerColumns;
import com.sirius.madness.provider.IgniteContract.SponsorColumns;
import com.sirius.madness.provider.IgniteContract.Partners;
import com.sirius.madness.provider.IgniteContract.Sponsors;
import com.sirius.madness.provider.IgniteContract.PresenterColumns;
import com.sirius.madness.provider.IgniteContract.Presenters;
import com.sirius.madness.provider.IgniteContract.Promotions;
import com.sirius.madness.provider.IgniteContract.PromotionColumns;
import com.sirius.madness.provider.IgniteContract.ScheduleColumns;
import com.sirius.madness.provider.IgniteContract.SessionColumns;
import com.sirius.madness.provider.IgniteContract.Sessions;
import com.sirius.madness.provider.IgniteContract.SessionsPresenters;
import com.sirius.madness.provider.IgniteContract.SurveyQuestionsColumns;
import com.sirius.madness.provider.IgniteContract.SessionsPresentersColumns;
import com.sirius.madness.provider.IgniteContract.ImageMetaData;
import com.sirius.madness.provider.IgniteContract.ImageMetaDataColumns;
import com.sirius.madness.provider.IgniteContract.ImageBinary;
import com.sirius.madness.provider.IgniteContract.ImageBinaryColumns;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link com.sirius.madness.provider.IgniteProvider}.
 */
public class IgniteDatabase extends SQLiteOpenHelper {

    // Used for debugging and logging
    private static final String TAG = "IgniteDatabase";

    private static final String DATABASE_NAME = "kickoff.db";

    private static final int DATABASE_VERSION = 1;

    private final Context mContext;

    SharedPreferences pref;

    interface Tables{
        String AVNET_SERVICES = "avnet_services";
        String PROMOTIONS = "promotions";
        String DEVELOPERS = "developers";
        String EVENTS = "events";
        String PARTNERS = "partners";
        String SPONSORS = "sponsors";
        String CATEGORIES = "categories";
        String SESSIONS = "sessions";
        String SURVEY_QUESTIONS = "survey_questions";
        String PRESENTERS = "presenters";
        String SCHEDULES = "schedules";
        String IMAGE_META_DATA = "image_meta_data";
        String IMAGE_BINARY = "image_binary";

        String EVENTS_PARTNERS = "events_partners";
        String SESSIONS_PRESENTERS = "sessions_presenters";
    }

    interface Joins{
        String PRESENTERS =
                Tables.PRESENTERS
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.PARTNERS+"."+ IgniteContract.Presenters.PRESENTER_IMAGE+"="+Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID;

        String SESSION_PRESENTERS =
                Tables.SESSIONS_PRESENTERS
                +" LEFT OUTER JOIN " + Tables.PRESENTERS +" ON " + Tables.SESSIONS_PRESENTERS+"."+SessionsPresenters.PRESENTER_ID +"=" +Tables.PRESENTERS+"."+Presenters.PRESENTER_ID
                +" LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.PRESENTERS+"."+Presenters.PRESENTER_IMAGE+"="+Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID
                +" WHERE "+Tables.SESSIONS_PRESENTERS+"."+SessionsPresenters.SESSION_ID+"=?";

        String PRESENTERS_WITH_PARTNER =
                Tables.PRESENTERS
                + " LEFT OUTER JOIN " + Tables.PARTNERS + " ON " +Tables.PRESENTERS+"."+Presenters.PARTNER_ID_FK +"=" +Tables.PARTNERS+"."+ Partners.PARTNER_ID
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.PRESENTERS+"."+Presenters.PRESENTER_IMAGE+"="+Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID;

        String DEVELOPER_WITH_PARTNER =
                Tables.DEVELOPERS
                + " LEFT OUTER JOIN " + Tables.PARTNERS + " ON " + Tables.DEVELOPERS+"."+Developers.PARTNER_ID_FK + "=" +Tables.PARTNERS+"."+ Partners.PARTNER_ID
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.DEVELOPERS+"."+Developers.DEVELOPER_IMAGE + "=" + Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID;

        String SESSION_WITH_CATEGORY_AND_SCHEDULE =
                Tables.SESSIONS
                + " LEFT OUTER JOIN " + Tables.CATEGORIES + " ON " + Tables.SESSIONS+"."+Sessions.CATEGORY_ID_FK + "=" + Tables.CATEGORIES+"."+Categories.CATEGORY_ID
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.SESSIONS+"."+Sessions.SESSION_IMAGE+"="+Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID
                + " LEFT OUTER JOIN " + Tables.SCHEDULES + " ON " + Tables.SESSIONS+"."+Sessions.SESSION_ID+"="+Tables.SCHEDULES+"."+ IgniteContract.Schedules.SESSION_ID_FK;

        String SESSION_DETAIL = SESSION_WITH_CATEGORY_AND_SCHEDULE + " WHERE " + Tables.SESSIONS+"."+Sessions.SESSION_ID+"=?";

        String SCHEDULE_WITH_SESSION =
                Tables.SCHEDULES
                + " LEFT OUTER JOIN " + Tables.SESSIONS + " ON " + Tables.SCHEDULES+"."+ScheduleColumns.SESSION_ID_FK + "=" + Tables.SESSIONS+"."+Sessions.SESSION_ID;

        String IMAGE_META_DATA_WITH_IMAGE_BINARY =
                Tables.IMAGE_META_DATA
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.IMAGE_META_DATA + "." +ImageMetaData.IMAGE_ID + "=" + Tables.IMAGE_BINARY + "." + ImageBinary.IMAGE_ID;

        String SPONSOR_PROMOTIONS =
                Tables.SPONSORS
                        + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.SPONSORS+"."+ Sponsors.SPONSOR_LOGO + "=" + Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID
                        + " LEFT OUTER JOIN " + Tables.IMAGE_META_DATA + " ON " + Tables.SPONSORS+"."+Sponsors.SPONSOR_LOGO+"="+Tables.IMAGE_META_DATA+"."+ImageMetaData.IMAGE_ID;
        String PARTNER_PROMOTIONS =
                Tables.PARTNERS
                + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.PARTNERS+"."+ Partners.PROMOTION_IMAGE + "=" + Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID
                + " LEFT OUTER JOIN " + Tables.IMAGE_META_DATA + " ON " + Tables.PARTNERS+"."+Partners.PROMOTION_IMAGE+"="+Tables.IMAGE_META_DATA+"."+ImageMetaData.IMAGE_ID;
        String EVENT_IMAGE =
                Tables.EVENTS
                        + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.EVENTS+"."+ Events.EVENT_MAP + "=" + Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID
                        + " LEFT OUTER JOIN " + Tables.IMAGE_META_DATA + " ON " + Tables.EVENTS+"."+Events.EVENT_MAP+"="+Tables.IMAGE_META_DATA+"."+ImageMetaData.IMAGE_ID;
        String IMAGE_PROMOTIONS =
                Tables.PROMOTIONS
                        + " LEFT OUTER JOIN " + Tables.IMAGE_BINARY + " ON " + Tables.PROMOTIONS+"."+ Promotions.PROMOTION_IMAGE + "=" + Tables.IMAGE_BINARY+"."+ImageBinary.IMAGE_ID;
    }


    /** Fully-qualified field names. */
    private interface Qualified {

    }

    /** {@code REFERENCES} clauses. */
    private interface References {
        String SESSION_ID = " REFERENCES " + Tables.SESSIONS + "(" + Sessions.SESSION_ID + ")";
        String PRESENTER_ID = " REFERENCES " + Tables.PRESENTERS + "(" + Presenters.PRESENTER_ID + ")";
        String EVENT_ID = " REFERENCES " + Tables.EVENTS + "(" + Events.EVENT_ID + ")";
        String IMAGE_BINARY_ID = " REFERENCES " + Tables.IMAGE_BINARY + "(" + ImageBinary.IMAGE_ID + ")";
        String SESSION_EVENT_ID_FK = " REFERENCES " + Tables.EVENTS + "(" + Events.EVENT_ID + ")";
        String SESSION_CATEGORY_ID_FK = " REFERENCES " + Tables.CATEGORIES + "(" + Categories.CATEGORY_ID + ")";
        String PRESENTER_PARTNER_ID_FK = " REFERENCES " + Tables.PARTNERS + "(" + Partners.PARTNER_ID + ")";
        String DEVELOPER_PARTNER_ID_FK = " REFERENCES " + Tables.PARTNERS + "(" + Partners.PARTNER_ID + ")";
        String SESSION_PARTNER_ID_FK = " REFERENCES " + Tables.PARTNERS + "(" + Partners.PARTNER_ID + ")";
        String SCHEDULE_SESSION_ID_FK = " REFERENCES " + Tables.SESSIONS + "(" + Sessions.SESSION_ID + ")";
        String SCHEDULE_EVENT_ID_FK = " REFERENCES " + Tables.EVENTS + "(" + Events.EVENT_ID + ")";
    }

    public IgniteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(pref.getInt("database_version",0)==0) {
            pref.edit().putInt("database_version", 1).commit();
        }
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Tables.AVNET_SERVICES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AvnetServiceColumns.AVNET_SERVICE_ADDRESS_TITLE + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_ADDRESS_LINE_1 + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_ADDRESS_LINE_2 + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_SHORT_TITLE + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_LONG_TITLE + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_SHORT_DESCRIPTION + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_LONG_DESCRIPTION + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_LOGO + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_LINKED_IN_LINK + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_TWITTER_HASH_TAG + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_WEBSITE_LINK + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_PHONE + " TEXT NOT NULL,"
                + AvnetServiceColumns.AVNET_SERVICE_EMAIL + " TEXT NOT NULL)"
        );

        db.execSQL("CREATE TABLE " + Tables.PROMOTIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PromotionColumns.PROMOTION_RSVP_LINK + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_SHOW_FROM + " INTEGER NOT NULL,"
                + PromotionColumns.PROMOTION_SHOW_TO + " INTEGER NOT NULL,"
                + PromotionColumns.PROMOTION_ID + " INTEGER NOT NULL,"
                + PromotionColumns.PROMOTION_CALENDAREVENTID + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_VENUE + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_EVENTTITLE + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_FROMTIME + " INTEGER NOT NULL,"
                + PromotionColumns.PROMOTION_TOTIME + " INTEGER NOT NULL,"
                + PromotionColumns.PROMOTION_LATITUDE + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_LONGITUDE + " TEXT NOT NULL,"
                + PromotionColumns.PROMOTION_IMAGE + " INTEGER " + References.IMAGE_BINARY_ID + ")"
        );

        db.execSQL("CREATE TABLE " + Tables.DEVELOPERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DeveloperColumns.DEVELOPER_ID + " INTEGER NOT NULL,"
                + DeveloperColumns.PARTNER_ID_FK + " INTEGER " + References.DEVELOPER_PARTNER_ID_FK + ","
                + DeveloperColumns.DEVELOPER_EMAIL + " TEXT,"
                + DeveloperColumns.DEVELOPER_FIRST_NAME + " TEXT,"
                + DeveloperColumns.DEVELOPER_IMAGE + " INTEGER " + References.IMAGE_BINARY_ID +","
                + DeveloperColumns.DEVELOPER_LAST_NAME + " TEXT,"
                + DeveloperColumns.DEVELOPER_LINKED_IN_ID + " TEXT,"
                + DeveloperColumns.DEVELOPER_LINKED_IN_LINK + " TEXT,"
                + DeveloperColumns.DEVELOPER_LONG_DESCRIPTION + " TEXT,"
                + DeveloperColumns.DEVELOPER_PHONE + " TEXT,"
                + DeveloperColumns.DEVELOPER_SHORT_DESCRIPTION + " TEXT,"
                + DeveloperColumns.DEVELOPER_TWITTER_ID + " TEXT,"
                + DeveloperColumns.DEVELOPER_TWITTER_LINK + " TEXT,"
                + DeveloperColumns.DEVELOPER_DESIGNATION + " TEXT,"
                + "UNIQUE (" + DeveloperColumns.DEVELOPER_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.EVENTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EventColumns.EVENT_ID + " INTEGER NOT NULL,"
                + EventColumns.EVENT_IMAGE + " TEXT NOT NULL,"
                + EventColumns.EVENT_MAP + " INTEGER " + References.IMAGE_BINARY_ID+","
                + EventColumns.EVENT_HALL + " TEXT NOT NULL,"
                + EventColumns.EVENT_FROM_TIME + " INTEGER NOT NULL,"
                + EventColumns.EVENT_TO_TIME + " INTEGER NOT NULL,"
                + EventColumns.EVENT_SHORT_TITLE + " TEXT NOT NULL,"
                + EventColumns.EVENT_LONG_TITLE + " TEXT NOT NULL,"
                + EventColumns.EVENT_SHORT_DESCRIPTION + " TEXT NOT NULL,"
                + EventColumns.EVENT_LONG_DESCRIPTION + " TEXT NOT NULL,"
                + EventColumns.EVENT_LATITUDE + " TEXT NOT NULL,"
                + EventColumns.EVENT_LONGITUDE + " TEXT NOT NULL,"
                + "UNIQUE (" + EventColumns.EVENT_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.PARTNERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PartnerColumns.PARTNER_ID + " INTEGER NOT NULL,"
                + PartnerColumns.PARTNER_IMAGE + " INTEGER ,"
                + PartnerColumns.PROMOTION_IMAGE + " INTEGER " + References.IMAGE_BINARY_ID+","
                + PartnerColumns.PARTNER_SHORT_TITLE + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_LONG_TITLE + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_SHORT_DESCRIPTION + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_LONG_DESCRIPTION + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_LINKEDIN_LINK + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_TWITTER_HASH_TAG + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_WEBSITE_LINK + " TEXT NOT NULL,"
                + PartnerColumns.PARTNER_IS_PARTNER + " INTEGER NOT NULL,"
                + "UNIQUE (" + PartnerColumns.PARTNER_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SPONSORS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SponsorColumns.SPONSOR_ID + " INTEGER NOT NULL,"
                + SponsorColumns.SPONSOR_NAME + " TEXT NOT NULL,"
                + SponsorColumns.SPONSOR_LOGO + " INTEGER " + References.IMAGE_BINARY_ID + ","
                + SponsorColumns.SPONSOR_WEBSITE_LINK + " TEXT NOT NULL,"
                + "UNIQUE (" + SponsorColumns.SPONSOR_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SURVEY_QUESTIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SurveyQuestionsColumns.SURVEY_QUESTIONS_QID + " TEXT NOT NULL,"
                + SurveyQuestionsColumns.SURVEY_QUESTIONS_QUESTION + " TEXT NOT NULL,"
                + SurveyQuestionsColumns.SURVEY_QUESTIONS_OPTION1 + " TEXT NOT NULL,"
                + SurveyQuestionsColumns.SURVEY_QUESTIONS_OPTION2 + " TEXT NOT NULL,"
                + SurveyQuestionsColumns.SURVEY_QUESTIONS_OPTION3 + " TEXT NOT NULL,"
                + "UNIQUE (" + SurveyQuestionsColumns.SURVEY_QUESTIONS_QID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.EVENTS_PARTNERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EventsPartnersColumns.EVENT_ID + " INTEGER NOT NULL,"
                +EventsPartnersColumns.PARTNER_ID + " INTEGER NOT NULL,"
                + "UNIQUE (" + EventsPartnersColumns.EVENT_ID + "," + EventsPartnersColumns.PARTNER_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CategoryColumns.CATEGORY_ID + " INTEGER NOT NULL,"
                + CategoryColumns.CATEGORY_NAME + " TEXT NOT NULL,"
                + "UNIQUE (" + CategoryColumns.CATEGORY_ID + ") ON CONFLICT REPLACE)");


        db.execSQL("CREATE TABLE " + Tables.SESSIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SessionColumns.SESSION_ID + " INTEGER NOT NULL,"
                + SessionColumns.EVENT_ID_FK + " INTEGER " + References.SESSION_EVENT_ID_FK + ","
                + SessionColumns.CATEGORY_ID_FK + " INTEGER " + References.SESSION_CATEGORY_ID_FK + ","
                + SessionColumns.PARTNER_ID_FK + " INTEGER " + References.SESSION_PARTNER_ID_FK + ","
                + SessionColumns.SESSION_FROM_TIME + " INTEGER NOT NULL,"
                + SessionColumns.SESSION_HALL + " TEXT,"
                + SessionColumns.SESSION_IMAGE + " INTEGER " + References.IMAGE_BINARY_ID + ","
                + SessionColumns.SESSION_LATITUDE +" REAL,"
                + SessionColumns.SESSION_LONG_DESCRIPTION + " TEXT,"
                + SessionColumns.SESSION_LONG_TITLE + " TEXT NOT NULL,"
                + SessionColumns.SESSION_LONGITUDE + " REAL,"
                + SessionColumns.SESSION_SHORT_DESCRIPTION + " TEXT,"
                + SessionColumns.SESSION_SHORT_TITLE + " TEXT,"
                + SessionColumns.SESSION_TO_TIME + " INTEGER NOT NULL,"
                + SessionColumns.SESSION_IS_HIDDEN + " TEXT,"
                + "UNIQUE (" + SessionColumns.SESSION_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.PRESENTERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PresenterColumns.PRESENTER_ID + " INTEGER NOT NULL,"
                + PresenterColumns.PARTNER_ID_FK + " INTEGER " + References.PRESENTER_PARTNER_ID_FK + ","
                + PresenterColumns.PRESENTER_DESIGNATION + " TEXT,"
                + PresenterColumns.PRESENTER_EMAIL + " TEXT,"
                + PresenterColumns.PRESENTER_FIRST_NAME + " TEXT,"
                + PresenterColumns.PRESENTER_IMAGE + " INTEGER " + References.IMAGE_BINARY_ID + ","
                + PresenterColumns.PRESENTER_LAST_NAME + " TEXT,"
                + PresenterColumns.PRESENTER_LINKED_IN_LINK + " TEXT,"
                + PresenterColumns.PRESENTER_LONG_DESCRIPTION + " TEXT,"
                + PresenterColumns.PRESENTER_PHONE + " TEXT,"
                + PresenterColumns.PRESENTER_SHORT_DESCRIPTION + " TEXT,"
                + PresenterColumns.PRESENTER_TWITTER_LINK + " TEXT,"
                + "UNIQUE (" + PresenterColumns.PRESENTER_ID + ") ON CONFLICT REPLACE)");


        db.execSQL("CREATE TABLE " + Tables.SESSIONS_PRESENTERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SessionsPresentersColumns.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
                + SessionsPresentersColumns.PRESENTER_ID + " TEXT NOT NULL " + References.PRESENTER_ID + ","
                + "UNIQUE (" + SessionsPresentersColumns.SESSION_ID + ","
                + SessionsPresentersColumns.PRESENTER_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SCHEDULES + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + ScheduleColumns.CALENDAR_EVENT_ID + " INTEGER NOT NULL,"
                        + ScheduleColumns.EVENT_ID_FK + " INTEGER " + References.SCHEDULE_EVENT_ID_FK+","
                        + ScheduleColumns.SESSION_ID_FK + " INTEGER " + References.SCHEDULE_SESSION_ID_FK+","
                        + ScheduleColumns.EVENT_TITLE + " TEXT NOY NULL,"
                        + ScheduleColumns.FROM_TIME + " INTEGER NOT NULL,"
                        + ScheduleColumns.TO_TIME + " INTEGER NOT NULL,"
                        + ScheduleColumns.MANDATORY + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.IMAGE_BINARY + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +ImageBinaryColumns.IMAGE_ID + " INTEGER NOT NULL,"
                +ImageBinaryColumns.IMAGE_DATA + " BLOB NOT NULL,"
                + "UNIQUE (" + ImageBinaryColumns.IMAGE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.IMAGE_META_DATA + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ImageMetaDataColumns.IMAGE_ID + " INTEGER NOT NULL " + References.IMAGE_BINARY_ID + ","
                + ImageMetaDataColumns.IMAGE_NAME + " TEXT NOT NULL,"
                + ImageMetaDataColumns.IMAGE_EXTENSION + " TEXT NOT NULL,"
                + ImageMetaDataColumns.IMAGE_HEIGHT + " INTEGER NOT NULL,"
                + ImageMetaDataColumns.IMAGE_WIDTH + " INTEGER NOT NULL,"
                + ImageMetaDataColumns.UPLOADED_DATE + " INTEGER NOT NULL,"
                + ImageMetaDataColumns.MODIFIED_DATE + " INTEGER NOT NULL,"
                + "UNIQUE (" + ImageMetaDataColumns.IMAGE_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Logs that the database is being upgraded
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + Tables.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PARTNERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SPONSORS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SURVEY_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PRESENTERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.DEVELOPERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PROMOTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.AVNET_SERVICES);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.EVENTS_PARTNERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_PRESENTERS);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.IMAGE_BINARY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.IMAGE_META_DATA);

        // Recreates the database with a new version
        onCreate(db);
    }

    public void upgradeDatabase(SQLiteDatabase db) {

        int oldVersion = pref.getInt("database_version",1);
        int newVersion = oldVersion + 1;
        pref.edit().putInt("database_version",newVersion).commit();

        // Logs that the database is being upgraded
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + Tables.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PARTNERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SPONSORS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SURVEY_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PRESENTERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.DEVELOPERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PROMOTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.AVNET_SERVICES);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.EVENTS_PARTNERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_PRESENTERS);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.IMAGE_BINARY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.IMAGE_META_DATA);

        // Recreates the database with a new version
        onCreate(db);
    }

    public void deleteSchedule(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULES);
        db.execSQL("CREATE TABLE " + Tables.SCHEDULES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ScheduleColumns.CALENDAR_EVENT_ID + " INTEGER NOT NULL,"
                + ScheduleColumns.EVENT_ID_FK + " INTEGER " + References.SCHEDULE_EVENT_ID_FK+","
                + ScheduleColumns.SESSION_ID_FK + " INTEGER " + References.SCHEDULE_SESSION_ID_FK+","
                + ScheduleColumns.EVENT_TITLE + " TEXT NOY NULL,"
                + ScheduleColumns.FROM_TIME + " INTEGER NOT NULL,"
                + ScheduleColumns.TO_TIME + " INTEGER NOT NULL,"
                + ScheduleColumns.MANDATORY + " TEXT NOT NULL)");
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


}
