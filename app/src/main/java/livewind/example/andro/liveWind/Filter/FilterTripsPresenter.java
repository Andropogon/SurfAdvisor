package livewind.example.andro.liveWind.Filter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import livewind.example.andro.liveWind.R;

/** An implementation of the Presenter */
public class FilterTripsPresenter implements FilterTripsContract.Presenter{

    private FilterTrips mFilterTrips;

    public FilterTripsPresenter() {
        mFilterTrips = new FilterTrips();
    }

    @Override
    public void saveCost(String cost) {
        mFilterTrips.setmCost(cost);
    }

    @Override
    public void sendPreferences(){
        mFilterTrips.setFilterTripsPreferences();
    }
}