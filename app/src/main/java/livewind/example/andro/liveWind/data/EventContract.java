package livewind.example.andro.liveWind.data;
import android.provider.BaseColumns;


public final class EventContract {

    private EventContract() {}

    /**
     * Inner class that defines constant values for the event database.
     */
    public static final class EventEntry implements BaseColumns {

        /**
         * Points system
         */
        public static final int POINTS_GET_LIKE = 10;
        /**
         * Possible values for the type of relation.
         */
        public static final int TYPE_WINDSURFING = 0;
        public static final int TYPE_KITESURFING = 1;
        public static final int TYPE_SURFING = 2;


        /**
         * Possible values for the wave conditions.
         */
        public static final int CONDITIONS_ONSHORE= 0;
        public static final int CONDITIONS_SIDESHORE = 1;
        public static final int CONDITIONS_OFFSHORE = 2;
        public static final int CONDITIONS_N = 3;
        public static final int CONDITIONS_NNE = 4;
        public static final int CONDITIONS_NE = 5;
        public static final int CONDITIONS_ENE = 6;
        public static final int CONDITIONS_E = 7;
        public static final int CONDITIONS_ESE = 8;
        public static final int CONDITIONS_SE = 9;
        public static final int CONDITIONS_SSE = 10;
        public static final int CONDITIONS_S = 11;
        public static final int CONDITIONS_SSW = 12;
        public static final int CONDITIONS_SW = 13;
        public static final int CONDITIONS_WSW = 14;
        public static final int CONDITIONS_W = 15;
        public static final int CONDITIONS_WNW = 16;
        public static final int CONDITIONS_NW = 17;
        public static final int CONDITIONS_NNW = 18;


        /**
         * Possible wind power units
         */
        public static final int UNIT_KNOTS = 1;
        public static final int UNIT_BEAUFORT = 2;
        public static final int UNIT_SAILS_SIZE = 3;
        /**
         * Possible values for transport.
         */
        public static final int TRANSPORT_CAR= 0;
        public static final int TRANSPORT_PLANE = 1;
        public static final int TRANSPORT_OWN = 10;

        /**
         * Possible values for character value.
         */
        public static final int CHARACTER_PRIVATE = 0;
        public static final int CHARACTER_ORGANIZED = 1;

        /**
         *
         */
        public static final boolean IT_IS_EVENT = true;
        public static final boolean IT_IS_TRIP = false;

        /**
         * Possible values for currencies:
         */
        public static final int CURRENCY_ZL= 0;
        public static final int CURRENCY_EURO = 1;
        public static final int CURRENCY_USD = 2;

        /**
         * Possible values for menu items:
         */
        public static final int MENU_NOTHING = -1;
        public static final int MENU_USER_PROFIL = 0;
        public static final int MENU_SETTINGS = 1;
        public static final int MENU_ABOUT = 2;
        public static final int MENU_FAQ = 3;
        public static final int MENU_LOG_OUT = 4;
        public static final int MENU_FACEBOOK = 5;


        /**
         * Extras values for users
         */
        public static final int EXTRA_ICON_NUMBER_1 = 1;
        public static final int EXTRA_ICON_NUMBER_2 = 2;
        public static final int EXTRA_ICON_NUMBER_3 = 3;
        public static final int EXTRA_ICON_NUMBER_4 = 4;
        public static final int EXTRA_ICON_NUMBER_5 = 5;
        public static final int EXTRA_ICON_NUMBER_6 = 6;
        public static final int EXTRA_ICON_CLASSIC_PRICE = 500;

        //HARDCODED DEFAULT STRING TO CHECKING EVENT TYPE
        public static final String IS_IT_EVENT = "DEFAULT";

        /**
         * Consts for countries:
         */
        public static final int COUNTRY_BAD = -2;
        public static final int COUNTRY_OTHER_COUNTRIES = -1;
        public static final int COUNTRY_WORLD = 0;
        public static final String COUNTRY_ALL_WORLD = "0";
        public static final int COUNTRY_POLAND = 1;
        public static final int COUNTRY_GREECE = 2;
        public static final int COUNTRY_SPAIN = 3;
        public static final int COUNTRY_CROATIA = 4;
        public static final int COUNTRY_PORTUGAL = 5;
        public static final int COUNTRY_GERMANY = 6;
        public static final int COUNTRY_FRANCE = 7;
        public static final int COUNTRY_SOUTH_AFRICA = 8;
        public static final int COUNTRY_MOROCCO = 9;
        public static final int COUNTRY_ITALY = 10;
        public static final int COUNTRY_EGYPT = 11;
        public static final int COUNTRY_UK = 12;
        public static final int COUNTRY_TURKEY = 13;
        public static final int COUNTRY_AUSTRIA = 14;
        public static final int COUNTRY_DENMARK = 15;
        public static final int COUNTRY_BRAZIL = 16;
        public static final int COUNTRY_USA = 17;
        public static final int COUNTRY_VIETNAM = 18;
        public static final int COUNTRY_MALTA = 19;

        /**
         * TRIP AVAILABLE:
         */
        public static final int TRIP_AVAILABLE_NO_INFO = -1;
        public static final int TRIP_AVAILABLE_COURSE = 0;
        public static final int TRIP_AVAILABLE_YES = 1;
        public static final int TRIP_AVAILABLE_NO = 2;
        public static final int TRIP_AVAILABLE_INSTRUCTOR_COURSE = 3;

        public static final int IT_IS_CAMP = 1000;

        /**
         * DISPLAY TRIPS OPTIONS
         */
        public static final int DISPLAY_TRIPS_FROM_AND_TO = 1;
        public static final int DISPLAY_TRIPS_FROM = 2;
        public static final int DISPLAY_TRIPS_TO = 3;


        public static final double CURRENCY_MULTIPLER_EURO = 4.3;
        public static final double CURRENCY_MULTIPLER_USD = 3.8;

        /**
         * PROMOTION TYPES
         */
        public static final int PROMOTION_TYPE_SURFADVISOR = 0;
        public static final int PROMOTION_TYPE_SURFOTEKA = 1;
        public static final int PROMOTION_TYPE_TWO_WAVES = 2;
        public static final int PROMOTION_TYPE_LOCALISATION = 3;
        public static final int PROMOTION_TYPE_CAR_FRONT = 4;
        public static final int PROMOTION_TYPE_CAR_SIDE = 5;
        public static final int PROMOTION_TYPE_HYDROSFERA = 6;

        /**
         * DISPLAY AS TYPES
         */
        public static final int DISPLAY_AS_NO_INFO = -1;
        public static final int DISPLAY_AS_NO_AVAILABLE = 0;
        public static final int DISPLAY_AS_CAMP = 1;
        public static final int DISPLAY_AS_TRAINING = 2;
    }

}
