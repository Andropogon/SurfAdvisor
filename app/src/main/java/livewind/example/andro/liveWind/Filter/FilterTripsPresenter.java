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

    /**
     * Set all filter trips preferences (without sports and countries - methods below) to {@FilterTrips} model
     * @return true if saved correctly
     * @return false if one or more filters have bad value
     */
    @Override
    public boolean setPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp, int sortingPreferences, int sortingOrderPreferences) {
        //Check that cost is >0
        if(Integer.valueOf(cost)<=0) {
            mView.showBadFilterToast(FilterTripsContract.FilterTripsEntry.BAD_FILTER_COST);
            return false;
        }
        //Check that "fromDate" is smaller than "toDate"
        if(dateToTimestamp<dateFromTimestamp){
            mView.showBadFilterToast(FilterTripsContract.FilterTripsEntry.BAD_FILTER_DATE);
            return false;
        }
        //Everything is correct - save filter preferences
        mFilterTrips.setmCost(cost);
        mFilterTrips.setmCurrency(currency);
        mFilterTrips.setmDateFromTimestamp(dateFromTimestamp);
        mFilterTrips.setmDateToTimestamp(dateToTimestamp);
        mFilterTrips.setmSortingPreferences(sortingPreferences);
        mFilterTrips.setmSortingOrderPreferences(sortingOrderPreferences);
        return true;
    }

    /**
     * Check and save to {@FilterTrips} model sports filters if they aren't empty
     * @return false if sports are empty
     */
    @Override
    public boolean saveSports(Set<String> sports) {
        //If empty show toast message and don't save
        if (sports.isEmpty()) {
            mView.showBadFilterToast(FilterTripsContract.FilterTripsEntry.BAD_FILTER_NO_SPORTS);
            return false;
        } else {
            //If correct - save
            mFilterTrips.setmSports(sports);
            mView.displaySports(sports);
            return true;
        }
    }
    /**
     * Save to {@FilterTrips} model countries filters if they aren't empty
     * They correcties was checked in {@CountryDialog}
     */
    @Override
    public void saveCountries(Set<String> countries){
        mFilterTrips.setmCountries(countries);
        mView.displayCountries();
    }

    /**
     * Some get methods - to load filters from {@FilterTrips} model that have them from sharedPreferences
     */
    @Override
    public Set<String>getSports(){
        return mFilterTrips.getmSports();
    }

    @Override
    public ArrayList<String> getCountriesArray(){
        return new ArrayList<>(mFilterTrips.getmCountries());
    }
    @Override
    public Set<String>getCountries(){
        return mFilterTrips.getmCountries();
    }

    /**
     * Send all preferences to {@FilterTrips} model to save them to sharedPreferences
     */
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

    /**
     * Empty method - if some don't want to dismiss changes don't do anythingS
     */
    @Override
    public void dismissChanges(){
        //Do nothing
    }
}