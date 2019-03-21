package livewind.example.andro.liveWind.Filter;

import java.util.Set;

public interface FilterTripsContract {
    /** Represents the View in MVP. */
    interface View {
        void displayPreferences(String cost,long dateFromTimestamp, long dateToTimestamp,Set<String> sports);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void savePreferences(String cost,long dateFromTimestamp, long dateToTimestamp,Set<String>sports);

        void sendPreferences();

        void loadPreferences();

    }
}
