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
        void displayPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp,Set<String> countries,int sortingPreferences);
        void displaySports(Set <String> sports);
        void displayCountries();
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void savePreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp,int sortingPreferences);

        void saveSports(Set<String>sports);

        Set<String> getSports();
        ArrayList<String> getCountries();

        void sendPreferences();

        void loadPreferences();

        void loadDefaultPreferences();

    }
}
