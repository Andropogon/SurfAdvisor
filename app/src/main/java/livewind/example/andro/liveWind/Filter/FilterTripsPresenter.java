package livewind.example.andro.liveWind.Filter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

import livewind.example.andro.liveWind.R;

/**
 * Created by JGJ on 20/03/19.
 * Presenter of Filter MVP
 * Filter MVP is responsible for giving the user the possibility to filter displayed trips
 */
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
        mView.displaySports(sports);
    }
    @Override
    public Set<String>getSports(){
        return mFilterTrips.getmSports();
    }
    @Override
    public ArrayList<String>getCountries(){
        return new ArrayList<>(mFilterTrips.getmCountries());
    }

    @Override
    public void sendPreferences(){
        mFilterTrips.setFilterTripsPreferences();
    }

    @Override
    public void loadPreferences() {
        mFilterTrips.getFilterTripsPreferences();
        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmCountries());
        mView.displaySports(mFilterTrips.getmSports());
    }
}