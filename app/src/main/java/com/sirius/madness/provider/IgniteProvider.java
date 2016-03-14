package com.sirius.madness.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sirius.madness.util.SelectionBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.sirius.madness.provider.IgniteContract.*;
import static com.sirius.madness.provider.IgniteDatabase.*;
import static com.sirius.madness.util.LogUtils.LOGV;
import static com.sirius.madness.util.LogUtils.makeLogTag;

/**
 * Created by 915655 on 05/01/15.
 */
public class IgniteProvider extends ContentProvider{


    private static final String TAG = makeLogTag(IgniteProvider.class);
    private IgniteDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CATEGORIES = 100;
    private static final int CATEGORY_ID = 101;

    private static final int EVENTS = 200;
    private static final int EVENT_ID = 201;

    private static final int PARTNERS = 300;
    private static final int PARTNER_ID = 301;

    private static final int SPONSORS = 1500;
    private static final int SPONSOR_ID = 1501;

    private static final int SURVEY_QUESTIONS = 1600;
    private static final int SURVEY_QUESTIONS_ID = 1601;

    private static final int SESSIONS = 400;
    private static final int SESSION_ID = 401;

    private static final int PRESENTERS = 500;
    private static final int PRESENTERS_ID = 501;
    private static final int SESSION_PRESENTERS = 502;

    private static final int SESSIONS_PRESENTERS = 600;
    private static final int EVENTS_PARTNERS = 700;

    private static final int AVNET_SERVICES = 800;
    private static final int PROMOTIONS = 900;
    private static final int DEVELOPERS = 1000;

    private static final int SCHEDULES = 1100;
    private static final int SCHEDULE_SESSION_ID=1200;

    private static final int IMAGE_META_DATA = 1300;
    private static final int IMAGE_META_DATA_ID = 1301;

    private static final int IMAGE_BINARY = 1400;
    private static final int IMAGE_BINARY_ID = 1401;


    /**
     * Build and return a {@link UriMatcher} that catches all {@link android.net.Uri}
     * variations supported by this {@link android.content.ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "avnet_services", AVNET_SERVICES);
        matcher.addURI(authority, "promotions", PROMOTIONS);
        matcher.addURI(authority, "developers", DEVELOPERS);

        matcher.addURI(authority, "categories", CATEGORIES);
        matcher.addURI(authority, "categories/*", CATEGORY_ID);

        matcher.addURI(authority, "events", EVENTS);
        matcher.addURI(authority, "events/*", EVENT_ID);

        matcher.addURI(authority, "partners", PARTNERS);
        matcher.addURI(authority, "partners/*", PARTNER_ID);

        matcher.addURI(authority, "sponsors", SPONSORS);
        matcher.addURI(authority, "sponsors/*", SPONSOR_ID);

        matcher.addURI(authority, "survey_questions", SURVEY_QUESTIONS);
        matcher.addURI(authority, "survey_questions/*", SURVEY_QUESTIONS_ID);

        matcher.addURI(authority, "sessions", SESSIONS);
        matcher.addURI(authority, "sessions/*", SESSION_ID);

        matcher.addURI(authority, "presenters",PRESENTERS);
        matcher.addURI(authority, "presenters/*", PRESENTERS_ID);

        matcher.addURI(authority, "schedules",SCHEDULES);
        matcher.addURI(authority, "schedules/*",SCHEDULE_SESSION_ID);

        matcher.addURI(authority, "sessions_presenters",SESSIONS_PRESENTERS);
        matcher.addURI(authority, "sessions_presenters/*",SESSION_PRESENTERS);
        matcher.addURI(authority, "events_partners",EVENTS_PARTNERS);

        matcher.addURI(authority, "image_meta_data", IMAGE_META_DATA);
        matcher.addURI(authority, "image_meta_data/*", IMAGE_META_DATA_ID);

        matcher.addURI(authority, "image_binary", IMAGE_BINARY);
        matcher.addURI(authority, "image_binary/*", IMAGE_BINARY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new IgniteDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        IgniteDatabase.deleteDatabase(context);
        mOpenHelper = new IgniteDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        // avoid the expensive string concatenation below if not loggable
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            LOGV(TAG, "uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +
                    " selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");
        }


        switch (match) {
            default: {
                // Most cases are handled with SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);

                boolean distinct = !TextUtils.isEmpty(
                        uri.getQueryParameter(QUERY_PARAMETER_DISTINCT));

                Cursor cursor = builder
                        .where(selection, selectionArgs)
                        .query(db, distinct, projection, sortOrder, null);
                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case AVNET_SERVICES:
                db.insertOrThrow(Tables.AVNET_SERVICES, null, values);
                notifyChange(uri);
                return AvnetServices.CONTENT_URI;
                //return Sponsors.buildSessionUri(values.getAsLong(AvnetServices._ID));
            case PROMOTIONS:
                db.insertOrThrow(Tables.PROMOTIONS, null, values);
                notifyChange(uri);
                return Promotions.CONTENT_URI;
                //return Promotions.buildSessionUri(values.getAsLong(Promotions.PROMOTION_ID));
            case DEVELOPERS:
                db.insertOrThrow(Tables.DEVELOPERS, null, values);
                notifyChange(uri);
                return Developers.CONTENT_URI;
            case CATEGORIES:
                db.insertOrThrow(Tables.CATEGORIES, null, values);
                notifyChange(uri);
                return Categories.buildSessionUri(values.getAsLong(Categories.CATEGORY_ID));

            case EVENTS:
                db.insertOrThrow(Tables.EVENTS, null, values);
                notifyChange(uri);
                return Events.buildSessionUri(values.getAsLong(Events.EVENT_ID));

            case PARTNERS:
                db.insertOrThrow(Tables.PARTNERS, null, values);
                notifyChange(uri);
                return Partners.buildSessionUri(values.getAsLong(Partners.PARTNER_ID));

            case SPONSORS:
                db.insertOrThrow(Tables.SPONSORS, null, values);
                notifyChange(uri);
                return Sponsors.buildSessionUri(values.getAsLong(Sponsors.SPONSOR_ID));

            case SURVEY_QUESTIONS:
                db.insertOrThrow(Tables.SURVEY_QUESTIONS, null, values);
                notifyChange(uri);
                return SurveyQuestions.buildSessionUri(values.getAsLong(SurveyQuestions.SURVEY_QUESTIONS_QID));

            case SESSIONS:
                db.insertOrThrow(Tables.SESSIONS, null, values);
                notifyChange(uri);
                return Sessions.buildSessionUri(values.getAsLong(Sessions.SESSION_ID));

            case PRESENTERS:
                db.insertOrThrow(Tables.PRESENTERS, null, values);
                notifyChange(uri);
                return Presenters.buildPresenterUri(values.getAsLong(Presenters.PRESENTER_ID));

            case SCHEDULES:
                db.insertOrThrow(Tables.SCHEDULES, null, values);
                notifyChange(uri);
                return Schedules.CONTENT_URI;

            case SESSIONS_PRESENTERS:
                db.insertOrThrow(Tables.SESSIONS_PRESENTERS, null, values);
                notifyChange(uri);
                return SessionsPresenters.CONTENT_URI;
            case EVENTS_PARTNERS:
                db.insertOrThrow(Tables.EVENTS_PARTNERS, null, values);
                notifyChange(uri);
                return EventsPartners.CONTENT_URI;
            case IMAGE_META_DATA:
                db.insertOrThrow(Tables.IMAGE_META_DATA, null, values);
                notifyChange(uri);
                return ImageMetaData.CONTENT_URI;
            case IMAGE_BINARY:
                db.insertOrThrow(Tables.IMAGE_BINARY, null, values);
                notifyChange(uri);
                return ImageBinary.CONTENT_URI;
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);


        final SelectionBuilder builder = buildSimpleSelection(uri);

        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ", account=" + ")");
        if (uri == BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);

        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    /**
     * Apply the given set of {@link android.content.ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case AVNET_SERVICES: {
                return builder.table(Tables.AVNET_SERVICES);
            }
            case PROMOTIONS: {
                return builder.table(Tables.PROMOTIONS);
            }
            case DEVELOPERS: {
                return builder.table(Tables.DEVELOPERS);
            }
            case CATEGORIES: {
                return builder.table(Tables.CATEGORIES);
            }
            case EVENTS: {
                return builder.table(Tables.EVENTS);
            }
            case PARTNERS: {
                return builder.table(Tables.PARTNERS);
            }
            case SPONSORS: {
                return builder.table(Tables.SPONSORS);
            }
            case SURVEY_QUESTIONS: {
                return builder.table(Tables.SURVEY_QUESTIONS);
            }
            case SESSIONS: {
                return builder.table(Tables.SESSIONS);
            }
            case SCHEDULES: {
                return builder.table(Tables.SCHEDULES);
            }
            case IMAGE_META_DATA: {
                return builder.table(Tables.IMAGE_META_DATA);
            }
            case IMAGE_BINARY: {
                return builder.table(Tables.IMAGE_BINARY);
            }
            case SCHEDULE_SESSION_ID:{
                long sessionId = Sessions.getSessionId(uri);
                return builder.table(Tables.SCHEDULES)
                        .where(Schedules.SESSION_ID_FK + "=?",String.valueOf(sessionId));
            }
            case SESSION_ID: {
                final long sessionId = Sessions.getSessionId(uri);
                return builder.table(Tables.SESSIONS)
                        .where(Sessions.SESSION_ID + "=?", String.valueOf(sessionId));
            }
            case SESSIONS_PRESENTERS: {
                final long sessionId = Sessions.getSessionId(uri);
                return builder.table(Tables.SESSIONS_PRESENTERS)
                        .where(Sessions.SESSION_ID + "=?", String.valueOf(sessionId));
            }
            case EVENTS_PARTNERS: {
                final long eventId = Events.getEventId(uri);
                return builder.table(Tables.EVENTS_PARTNERS)
                        .where(Events.EVENT_ID + "=?", String.valueOf(eventId));
            }
            case PRESENTERS: {
                return builder.table(Tables.PRESENTERS);
            }
            case PRESENTERS_ID: {
                final long presenterId = Presenters.getPresenterId(uri);
                return builder.table(Tables.PRESENTERS)
                        .where(Presenters.PRESENTER_ID + "=?", String.valueOf(presenterId));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }



    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case AVNET_SERVICES: {
                return builder.table(Tables.AVNET_SERVICES);
            }
            case CATEGORIES: {
                return builder.table(Tables.CATEGORIES);
            }
            case PROMOTIONS: {
                return builder.table(Joins.IMAGE_PROMOTIONS);
            }
            case DEVELOPERS: {
                return builder.table(Joins.DEVELOPER_WITH_PARTNER);
            }
            case EVENTS: {
                return builder.table(Joins.EVENT_IMAGE);
            }
            case PARTNERS: {
                return builder.table(Joins.PARTNER_PROMOTIONS);
            }
            case SPONSORS: {
                return builder.table(Joins.SPONSOR_PROMOTIONS);
            }
            case SESSIONS: {
                return builder.table(Joins.SESSION_WITH_CATEGORY_AND_SCHEDULE);
            }
            case SCHEDULES: {
                return builder.table(Joins.SCHEDULE_WITH_SESSION);
            }
            case IMAGE_META_DATA: {
                return builder.table(Tables.IMAGE_META_DATA);
            }
            case IMAGE_BINARY: {
                return builder.table(Tables.IMAGE_BINARY);
            }
            case SURVEY_QUESTIONS: {
                return builder.table(Tables.SURVEY_QUESTIONS);
            }
            case SCHEDULE_SESSION_ID:{
                long sessionId = Sessions.getSessionId(uri);
                return builder.table(Tables.SCHEDULES)
                        .where(Schedules.SESSION_ID_FK + "=?", String.valueOf(sessionId));
            }
            case SESSION_ID: {
                final long sessionId = Sessions.getSessionId(uri);
                return builder.table(Joins.SESSION_DETAIL,String.valueOf(sessionId));
            }
            case PRESENTERS: {
                return builder.table(Joins.PRESENTERS_WITH_PARTNER);
            }
            case PRESENTERS_ID: {
                final long presenterId = Presenters.getPresenterId(uri);
                return builder.table(Joins.PRESENTERS_WITH_PARTNER)
                        .where(Presenters.PRESENTER_ID + "=?", String.valueOf(presenterId));
            }
            case SESSION_PRESENTERS:{
                final long sessionId = SessionsPresenters.get(uri);
                return builder.table(Joins.SESSION_PRESENTERS,String.valueOf(sessionId))
                        .mapToTable(Presenters.PRESENTER_ID, Tables.PRESENTERS);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }


}