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

    //Model
    private FilterTrips mFilterTrips;

    //View in activity
    private FilterTripsContract.View mView;

    public FilterTripsPresenter(FilterTripsContract.View view) {
        mFilterTrips = new FilterTrips();
        mView = view;
    }

    @Override
    public void savePreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp, int sortingPreferences, int sortingOrderPreferences) {
        mFilterTrips.setmCost(cost);
        mFilterTrips.setmCurrency(currency);
        mFilterTrips.setmDateFromTimestamp(dateFromTimestamp);
        mFilterTrips.setmDateToTimestamp(dateToTimestamp);
        mFilterTrips.setmSortingPreferences(sortingPreferences);
        mFilterTrips.setmSortingOrderPreferences(sortingOrderPreferences);
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
    public void saveCountries(Set<String> countries){
        mFilterTrips.setmCountries(countries);
        mView.displayCountries();
    }
    @Override
    public ArrayList<String> getCountriesArray(){
        return new ArrayList<>(mFilterTrips.getmCountries());
    }
    @Override
    public Set<String>getCountries(){
        return mFilterTrips.getmCountries();
    }

    @Override
    public void sendPreferences(){
        mFilterTrips.setFilterTripsPreferences();
    }
    /**
     * Load default preferences from
     */
    @Override
    public void loadPreferences() {
        mFilterTrips.getFilterTripsPreferences();
        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmCurrency(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmCountries(),mFilterTrips.getmSortingPreferences(), mFilterTrips.getmSortingOrderPreferences());
        mView.displaySports(mFilterTrips.getmSports());
    }

    /**
     * Load default preferences
     */
    @Override
    public void loadDefaultPreferences(){
        //Set default values to FilterTrips Model
        mFilterTrips.setmCost(FilterTripsContract.FilterTripsEntry.DEFAULT_COST);
        mFilterTrips.setmCurrency(EventContract.EventEntry.CURRENCY_ZL);
        mFilterTrips.setmDateFromTimestamp(System.currentTimeMillis());
        mFilterTrips.setmDateToTimestamp(System.currentTimeMillis() + FilterTripsContract.FilterTripsEntry.DEFAULT_DURATION_TIMESTAMP);
        Set<String> defaultSports = new HashSet<String>();
        defaultSports.add(FilterTripsContract.FilterTripsEntry.SPORT_WINDSURFING);
        defaultSports.add(FilterTripsContract.FilterTripsEntry.SPORT_KITESURFING);
        defaultSports.add(FilterTripsContract.FilterTripsEntry.SPORT_SURFING);
        mFilterTrips.setmSports(defaultSports);
        Set<String> defaultCountries = new HashSet<String>();
        defaultCountries.add(FilterTripsContract.FilterTripsEntry.COUNTRIES_ALL);
        mFilterTrips.setmCountries(defaultCountries);
        mFilterTrips.setmSortingPreferences(FilterTripsContract.FilterTripsEntry.SORTING_DATE);
        mFilterTrips.setmSortingOrderPreferences(FilterTripsContract.FilterTripsEntry.ORDER_DECREASE);

        //Display default values on FilterTripsActivity
        mView.displayPreferences(mFilterTrips.getmCost(),mFilterTrips.getmCurrency(),mFilterTrips.getmDateFromTimestamp(),mFilterTrips.getmDateToTimestamp(),mFilterTrips.getmCountries(),mFilterTrips.getmSortingPreferences(),mFilterTrips.getmSortingOrderPreferences());
        mView.displayCountries();
        mView.displaySports(mFilterTrips.getmSports());
    }

    @Override
    public void dismissChanges(){
        mCopyFilterTrips.setFilterTripsPreferences();
    }
}