package com.sirius.madness.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class IgniteContract {

    /**
     * Query parameter to create a distinct query.
     */
    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    interface AvnetServiceColumns{
        String AVNET_SERVICE_ADDRESS_TITLE = "avnet_service_address_title";
        String AVNET_SERVICE_ADDRESS_LINE_1 = "avnet_service_address_line_1";
        String AVNET_SERVICE_ADDRESS_LINE_2 = "avnet_service_address_line_2";
        String AVNET_SERVICE_SHORT_TITLE = "avnet_service_short_title";
        String AVNET_SERVICE_LONG_TITLE = "avnet_service_long_title";
        String AVNET_SERVICE_SHORT_DESCRIPTION = "avnet_service_short_desc";
        String AVNET_SERVICE_LONG_DESCRIPTION = "avnet_service_long_desc";
        String AVNET_SERVICE_LOGO = "avnet_service_logo";
        String AVNET_SERVICE_LINKED_IN_LINK = "avnet_service_linked_in_link";
        String AVNET_SERVICE_TWITTER_HASH_TAG = "avnet_service_twitter_hash_tag";
        String AVNET_SERVICE_WEBSITE_LINK = "avnet_service_website_link";
        String AVNET_SERVICE_PHONE = "avnet_service_phone";
        String AVNET_SERVICE_EMAIL = "avnet_service_email";
    }

    interface PromotionColumns{
        String PROMOTION_RSVP_LINK = "promotion_rsvp_link";
        String PROMOTION_SHOW_FROM = "promotion_show_from";
        String PROMOTION_SHOW_TO = "promotion_show_to";
        String PROMOTION_IMAGE = "promotion_image";
        String PROMOTION_ID = "promotion_id";
        String PROMOTION_CALENDAREVENTID = "promotion_calendarEventId";
        String PROMOTION_VENUE = "promotion_venue";
        String PROMOTION_EVENTTITLE = "promotion_eventTitle";
        String PROMOTION_FROMTIME = "promotion_fromTime";
        String PROMOTION_TOTIME = "promotion_toTime";
        String PROMOTION_LATITUDE = "promotion_latitude";
        String PROMOTION_LONGITUDE = "promotion_longitude";
    }

    interface DeveloperColumns{
        String PARTNER_ID_FK = "partner_id_fk";
        String DEVELOPER_EMAIL = "developer_email";
        String DEVELOPER_FIRST_NAME = "developer_first_name";
        String DEVELOPER_IMAGE = "developer_image";
        String DEVELOPER_LAST_NAME = "developer_last_name";
        String DEVELOPER_LINKED_IN_ID = "developer_linked_in_id";
        String DEVELOPER_LINKED_IN_LINK = "developer_linked_in_link";
        String DEVELOPER_LONG_DESCRIPTION = "developer_long_description";
        String DEVELOPER_PHONE = "developer_phone";
        String DEVELOPER_ID = "developer_id";
        String DEVELOPER_SHORT_DESCRIPTION = "developer_short_desc";
        String DEVELOPER_TWITTER_ID = "developer_twitter_id";
        String DEVELOPER_TWITTER_LINK = "developer_twitter_link";
        String DEVELOPER_DESIGNATION = "developer_designation";
    }

    interface EventColumns{
        String EVENT_ID = "event_id";
        String EVENT_IMAGE = "event_image";
        String EVENT_MAP = "event_map";
        String EVENT_HALL = "event_hall";
        String EVENT_SHORT_TITLE = "event_short_title";
        String EVENT_LONG_TITLE = "event_long_title";
        String EVENT_SHORT_DESCRIPTION = "event_short_desc";
        String EVENT_LONG_DESCRIPTION = "event_long_desc";
        String EVENT_FROM_TIME = "event_from_time";
        String EVENT_TO_TIME = "event_to_time";
        String EVENT_LATITUDE = "event_latitude";
        String EVENT_LONGITUDE = "event_longitude";
    }

    interface CategoryColumns{
        String CATEGORY_ID = "category_id";
        String CATEGORY_NAME = "category_name";
    }

    interface PartnerColumns{
        String PARTNER_ID = "partner_id";
        String PARTNER_IMAGE = "partner_image";
        String PROMOTION_IMAGE = "promotion_image";
        String PARTNER_SHORT_TITLE = "partner_short_title";
        String PARTNER_LONG_TITLE = "partner_long_title";
        String PARTNER_SHORT_DESCRIPTION = "partner_short_desc";
        String PARTNER_LONG_DESCRIPTION = "partner_long_desc";
        String PARTNER_LINKEDIN_LINK = "linkedin_link";
        String PARTNER_TWITTER_HASH_TAG = "twitter_hash_tag";
        String PARTNER_WEBSITE_LINK = "website_link";
        String PARTNER_IS_PARTNER = "is_partner";
    }
    interface SponsorColumns{
        String SPONSOR_ID = "sponsor_id";
        String SPONSOR_NAME = "sponsor_name";
        String SPONSOR_LOGO = "sponsor_logo";
        String SPONSOR_WEBSITE_LINK = "website_link";
    }

    interface SurveyQuestionsColumns{
        String SURVEY_QUESTIONS_QID = "survey_questions_id";
        String SURVEY_QUESTIONS_QUESTION = "survey_questions_question";
        String SURVEY_QUESTIONS_OPTION1 = "survey_questions_option1";
        String SURVEY_QUESTIONS_OPTION2 = "survey_questions_option2";
        String SURVEY_QUESTIONS_OPTION3 = "survey_questions_option3";
    }

    interface SessionColumns{
        String SESSION_ID = "session_id";
        String EVENT_ID_FK = "event_id_fk";
        String CATEGORY_ID_FK = "category_id_fk";
        String PARTNER_ID_FK = "partner_id_fk";
        String SESSION_FROM_TIME = "session_from_time";
        String SESSION_HALL = "session_hall";
        String SESSION_IMAGE = "session_image";
        String SESSION_LATITUDE = "session_latitude";
        String SESSION_LONG_DESCRIPTION = "session_long_desc";
        String SESSION_LONGITUDE = "session_longitude";
        String SESSION_LONG_TITLE = "session_long_title";
        String SESSION_SHORT_DESCRIPTION = "session_short_desc";
        String SESSION_SHORT_TITLE = "session_short_title";
        String SESSION_TO_TIME = "session_to_time";
        String SESSION_IS_HIDDEN = "session_is_hidden";
    }


    interface PresenterColumns {
        String PARTNER_ID_FK = "partner_id_fk";
        String PRESENTER_DESIGNATION = "presenter_designation";
        String PRESENTER_EMAIL = "presenter_email";
        String PRESENTER_FIRST_NAME = "presenter_first_name";
        String PRESENTER_IMAGE = "presenter_image";
        String PRESENTER_LAST_NAME = "presenter_last_name";
        String PRESENTER_LINKED_IN_LINK = "presenter_linked_in_link";
        String PRESENTER_LONG_DESCRIPTION = "presenter_long_description";
        String PRESENTER_PHONE = "presenter_phone";
        String PRESENTER_ID = "presenter_id";
        String PRESENTER_SHORT_DESCRIPTION = "presenter_short_desc";
        String PRESENTER_TWITTER_LINK = "presenter_twitter_link";
    }

    interface ScheduleColumns{
        String CALENDAR_EVENT_ID = "calendar_event_id";
        String EVENT_ID_FK = "event_id_fk";
        String SESSION_ID_FK = "session_id_fk";
        String EVENT_TITLE = "event_title";
        String FROM_TIME = "fromTime";
        String TO_TIME = "toTime";
        String MANDATORY = "mandatory";
    }

    /** Many to many mapping of Sessions and its Presenters */
    interface SessionsPresentersColumns {
        String SESSION_ID = "session_id";
        String PRESENTER_ID = "presenter_id";
    }

    interface EventsPartnersColumns{
        String EVENT_ID = "event_id";
        String PARTNER_ID = "partner_id";
    }

    interface ImageMetaDataColumns {
        String IMAGE_ID = "image_id";
        String IMAGE_NAME = "image_name";
        String IMAGE_EXTENSION = "image_extension";
        String IMAGE_HEIGHT = "image_height";
        String IMAGE_WIDTH = "image_width";
        String UPLOADED_DATE = "uploaded_date";
        String MODIFIED_DATE = "modified_date";
    }

    interface ImageBinaryColumns {
        String IMAGE_ID = "image_id";
        String IMAGE_DATA = "image_data";
    }

    public static final String CONTENT_AUTHORITY = "com.sirius.kickoff.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_AVNET_SERVICES = "avnet_services";
    public static final String PATH_PROMOTIONS = "promotions";
    public static final String PATH_DEVELOPERS = "developers";
    public static final String PATH_EVENTS = "events";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_PARTNERS = "partners";
    public static final String PATH_SPONSORS = "sponsors";
    public static final String PATH_SURVEY_QUESTIONS = "survey_questions";
    public static final String PATH_SESSIONS = "sessions";
    public static final String PATH_PRESENTERS = "presenters";
    public static final String PATH_SCHEDULES = "schedules";
    public static final String PATH_EVENTS_PARTNERS = "events_partners";
    public static final String PATH_SESSIONS_PRESENTERS = "sessions_presenters";
    public static final String PATH_IMAGE_META_DATA = "image_meta_data";
    public static final String PATH_IMAGE_BINARY = "image_binary";

    public static class AvnetServices implements AvnetServiceColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AVNET_SERVICES).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.avnet_services";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.avnet_services";

    }

    public static class Schedules implements ScheduleColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULES).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.schedules";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.schedules";


        public static Uri buildSessionUri(long sessionId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sessionId)).build();
        }

    }

    public static class Promotions implements PromotionColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROMOTIONS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.promotions";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.promotions";

        /** Build {@link Uri} for requested {@link #PROMOTION_ID}. */
        public static Uri buildSessionUri(long promotionId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(promotionId)).build();
        }
    }

    public static class Developers implements DeveloperColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEVELOPERS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.developers";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.developers";

    }

    public static class Events implements EventColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.events";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.events";

        /** Build {@link Uri} for requested {@link #EVENT_ID}. */
        public static Uri buildSessionUri(long eventId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(eventId)).build();
        }

        public static long getEventId(Uri uri) {
            return Long.getLong(uri.getPathSegments().get(1));
        }
    }

    public static class Categories implements CategoryColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.categories";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.categories";

        /** Build {@link Uri} for requested {@link #CATEGORY_ID}. */
        public static Uri buildSessionUri(long categoryId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(categoryId)).build();
        }


    }

    public static class Partners implements PartnerColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARTNERS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.partners";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.partners";

        /** Build {@link Uri} for requested {@link #PARTNER_ID}. */
        public static Uri buildSessionUri(long partnerId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(partnerId)).build();
        }
    }

    public static class Sponsors implements SponsorColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPONSORS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.sponsors";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.sponsors";

        /** Build {@link Uri} for requested {@link #SPONSOR_ID}. */
        public static Uri buildSessionUri(long sponsorId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sponsorId)).build();
        }
    }

    public static class SurveyQuestions implements SurveyQuestionsColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SURVEY_QUESTIONS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.survey_questions";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.survey_questions";

        /** Build {@link Uri} for requested {@link #SURVEY_QUESTIONS_QID}. */
        public static Uri buildSessionUri(long survey_questons_Id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(survey_questons_Id)).build();
        }
    }

    public static class Sessions implements SessionColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.session";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.session";



        /** Build {@link Uri} for requested {@link #SESSION_ID}. */
        public static Uri buildSessionUri(long sessionId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sessionId)).build();
        }

        /**
         * Build {@link Uri} that references any {@link com.sirius.madness.provider.IgniteContract.Presenters} associated
         * with the requested {@link #SESSION_ID}.
         */
        public static Uri buildPresentersDirUri(long sessionId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sessionId)).appendPath(PATH_PRESENTERS).build();
        }


        /** Read {@link #SESSION_ID} from {@link com.sirius.madness.provider.IgniteContract.Sessions} {@link Uri}. */
        public static long getSessionId(Uri uri) {
            return Long.valueOf(uri.getLastPathSegment().toString());
        }
    }

    public static class Presenters implements PresenterColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRESENTERS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.presenter";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.presenter";


        /** Build {@link Uri} for requested {@link #PRESENTER_ID}. */
        public static Uri buildPresenterUri(long presenterId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(presenterId)).build();
        }


        public static long getPresenterId(Uri uri){
            return Long.valueOf(uri.getLastPathSegment().toString());
        }

    }

    public static class SessionsPresenters implements SessionsPresentersColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONS_PRESENTERS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.sessions_presenters";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.sessions_presenters";


        /** Build {@link Uri} for requested {@link #PRESENTER_ID}. */
        public static Uri buildSessionsPresentersUri(long sessionId, long presenterId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sessionId)).appendPath(String.valueOf(presenterId)).build();
        }

        public static Uri buildSessionPresentersUri(long sessionId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sessionId)).build();
        }


        public static long get(Uri uri){
            return Long.valueOf(uri.getLastPathSegment().toString());
        }

    }

    public static class EventsPartners implements EventsPartnersColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS_PARTNERS).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.events_partners";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.events_partners";


        /** Build {@link Uri} for requested {@link #PARTNER_ID}. */
        public static Uri buildSessionsPresentersUri(long eventId, long partnerId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(eventId)).appendPath(String.valueOf(partnerId)).build();
        }


        public static long get(Uri uri){
            return Long.valueOf(uri.getLastPathSegment().toString());
        }
    }

    public static class ImageMetaData implements ImageMetaDataColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE_META_DATA).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.image_meta_data";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.image_meta_data";


        public static Uri buildImageMetaDataUri(long imageId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(imageId)).build();
        }
    }

    public static class ImageBinary implements ImageBinaryColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE_BINARY).build();

        /*
         * MIME type definitions
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.sirius.kickoff.provider.image_binary";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.sirius.kickoff.provider.image_binary";


        public static Uri buildImageBinaryUri(long imageId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(imageId)).build();
        }

    }

    // This class cannot be instantiated
    private IgniteContract() {
    }


}
