package livewind.example.andro.liveWind.Filter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import livewind.example.andro.liveWind.R;

/** An implementation of the Presenter */
public class FilterTripsPresenter implements FilterTripsContract.Presenter{

    private FilterTrips mFilterTrips;

    /**
     * Views
     */
    private FilterTripsContract.View mView;

    public FilterTripsPresenter(FilterTripsContract.View view) {
        mFilterTrips = new FilterTrips();
        mView = view;
    }

    @Override
    public void savePreferences(String cost, long dateFromTimestamp, long dateToTimestamp) {
        mFilterTrips.setmCost(cost);
        mFilterTrips.setmDateFromTimestamp(dateFromTimestamp);
        mFilterTrips.setmDateToTimestamp(dateToTimestamp);
    }
    @Override
    public void saveSports(Set<String> sports){
        mFilterTrips.setmSports(sports);
    }
    @Override
    public Set<String>getSports(){
        return mFilterTrips.getmSports();
    }
    @Override
    public Set<String>getCountries(){
        return mFilterTrips.getmCountries();
    }

    @Override
    public void sendPreferences(){
        mFilterTrips.setFilterTripsPreferences();
    }

    @Override
    public void loadPreferences() {
        mFilterTrips.getFilterTripsPreferences();
        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmSports(),mFilterTrips.getmCountries());
    }
}