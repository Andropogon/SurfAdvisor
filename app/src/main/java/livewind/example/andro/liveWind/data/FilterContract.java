package livewind.example.andro.liveWind.data;

/**
 * Created by JGJ on 14/04/19.
 * Contract class for trip filters
 */
public class FilterContract {

    public static final class FilterTripsEntry {

        /**
         * Default filter cost
         */
        public static final String DEFAULT_COST = "10000";

        /**
         * Default value to add to current date timestamp to get "date_to_timestamp"
         */
        public static final Long DEFAULT_DURATION_TIMESTAMP = 7889229000L;

        /**
         * Possible filter sports values
         */
        public static final String SPORT_WINDSURFING = "0";
        public static final String SPORT_KITESURFING = "1";
        public static final String SPORT_SURFING = "2";

        /**
         * Default value of countries filter
         */
        public static final String COUNTRIES_ALL = "0";
    }
}
