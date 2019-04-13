package livewind.example.andro.liveWind.Filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.data.EventContract;

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
    public void savePreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp) {
        mFilterTrips.setmCost(cost);
        mFilterTrips.setmCurrency(currency);
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
        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmCurrency(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmCountries());
        mView.displaySports(mFilterTrips.getmSports());
    }

    @Override
    public void loadDefaultPreferences(){
        mFilterTrips.setmCost("20000");
        mFilterTrips.setmCurrency(EventContract.EventEntry.CURRENCY_ZL);
        mFilterTrips.setmDateFromTimestamp(System.currentTimeMillis());
        mFilterTrips.setmDateToTimestamp(System.currentTimeMillis() + 7889229000L);
        Set<String> defaultSports = new HashSet<String>();
        defaultSports.add("0");
        defaultSports.add("1");
        defaultSports.add("2");
        mFilterTrips.setmSports(defaultSports);
        Set<String> defaultCountries = new HashSet<String>();
        defaultCountries.add("0");
        mFilterTrips.setmCountries(defaultCountries);

        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmCurrency(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmCountries());
        mView.displayCountries();
        mView.displaySports(mFilterTrips.getmSports());
    }
}