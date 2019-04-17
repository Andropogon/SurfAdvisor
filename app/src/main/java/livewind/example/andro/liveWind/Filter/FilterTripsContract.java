package livewind.example.andro.liveWind.Filter;

import java.util.ArrayList;
import java.util.Set;
/**
 * Created by JGJ on 20/03/19.
 * Contract of Filter MVP
 * Filter MVP is responsible for giving the user the possibility to filter displayed trips
 */
public interface FilterTripsContract {

    /** Represents the View in MVP. */
    interface View {
        void displayPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp,Set<String> countries,int sortingPreferences, int sortingOrderPreferences);
        void displaySports(Set <String> sports);
        void displayCountries();
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void savePreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp,int sortingPreferences, int sortingOrderPreferences);

        void saveSports(Set<String>sports);

        Set<String> getSports();
        ArrayList<String> getCountries();

        void sendPreferences();

        void loadPreferences();

        void loadDefaultPreferences();
    }

    /**
     * Constants and default values
     */
    public static final class FilterTripsEntry {

        /**
         * Default filter cost
         */
        public static final String DEFAULT_COST = "10000";

        /**
         * Default value to add to current date timestamp to get "date_to_timestamp"
         */
        public static final Long DEFAULT_DURATION_TIMESTAMP = 15552000000L;

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
        /**
         * Possible options for sorting spinner
         */
        public static final int SORTING_DATE = 0;
        public static final int SORTING_COST = 1;

        /**
         * Possible options for sorting order spinner
         */
        public static final int ORDER_DECREASE = 1;
        public static final int ORDER_INCREASE = 0;
    }
}
