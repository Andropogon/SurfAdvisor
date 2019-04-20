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
        void displayPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp,Set<String> countries,int sortingPreferences, int sortingOrderPreferences, int displayCountriesPreferences);
        void displaySports(Set <String> sports);
        void displayCountries();
        void showBadFilterToast(int errorCode);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        boolean setPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp, int sortingPreferences, int sortingOrderPreferences, int displayCountriesPreferences);
        boolean saveSports(Set<String>sports);
        void saveCountries(Set<String> countries);
        Set<String> getSports();
        ArrayList<String> getCountriesArray();
        Set<String> getCountries();
        void sendPreferences();
        void loadPreferences();
        void loadDefaultPreferences();
        void dismissChanges();
    }

    /**
     * Constants and default values
     */
    final class FilterTripsEntry {

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
         * Possible options for countries display spinner
         */
        public static final int DISPLAY_FROM_AND_TO = 0;
        public static final int DISPLAY_FROM = 1;
        public static final int DISPLAY_TO = 2;

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

        /**
         * Possible bad filter codes
         */
        public static final int BAD_FILTER_DATE = 1;
        public static final int BAD_FILTER_NO_COUNTRIES = 2;
        public static final int BAD_FILTER_NO_SPORTS = 3;
        public static final int BAD_FILTER_COST = 4;
    }
}
