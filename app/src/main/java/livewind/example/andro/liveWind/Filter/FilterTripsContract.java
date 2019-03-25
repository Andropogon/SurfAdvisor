package livewind.example.andro.liveWind.Filter;

import java.util.Set;

public interface FilterTripsContract {
    /** Represents the View in MVP. */
    interface View {
        void displayPreferences(String cost,long dateFromTimestamp, long dateToTimestamp,Set<String> sports,Set<String> countries);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void savePreferences(String cost,long dateFromTimestamp, long dateToTimestamp);

        void saveSports(Set<String>sports);
        void saveCountries(Set<String>countries);

        Set<String> getSports();
        Set<String> getCountries();

        void sendPreferences();

        void loadPreferences();

    }
}
