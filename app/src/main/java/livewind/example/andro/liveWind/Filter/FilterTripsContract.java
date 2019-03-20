package livewind.example.andro.liveWind.Filter;

public interface FilterTripsContract {
    /** Represents the View in MVP. */
    interface View {

    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void saveCost(String cost);

        void sendPreferences();

    }
}
