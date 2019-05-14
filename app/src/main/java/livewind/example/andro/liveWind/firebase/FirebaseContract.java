package livewind.example.andro.liveWind.firebase;

/**
 * Created by JGJ on 09/05/19.
 * Contract of Firebase database
 * Mainly for constants strings for firebase paths.
 */
public interface FirebaseContract {


    /**
     * Constants and default values
     */
    final class FirebaseEntry {

        /**
         * Firebase realtime database table names (paths)
         */
        public static final String TABLE_EVENTS = "events";
        public static final String TABLE_USERS = "users";
        public static final String TABLE_USERNAMES = "usernames";
        public static final String TABLE_CURRENT_TIME = "currentTime";


        /**
         * Firebase realtime database column names (paths)
         * Events table
         */
        public static final String COLUMN_EVENTS_START_DATE = "startDate";
        public static final String COLUMN_EVENTS_TIMESTAMP = "timestamp";
        public static final String COLUMN_EVENTS_TIMESTAMP_START_DATE = "timestampStartDate";

        /**
         * Firebase realtime database column names (paths)
         * Users table
         */
        public static final String COLUMN_USERS_USER_TOKEN = "userToken";
        public static final String COLUMN_USERS_UID= "uid";
    }

}
