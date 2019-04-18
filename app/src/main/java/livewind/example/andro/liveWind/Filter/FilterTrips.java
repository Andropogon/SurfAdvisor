package livewind.example.andro.liveWind.Filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;

/**
 * Created by JGJ on 20/03/19.
 * Model of Filter MVP
 * Filter MVP is responsible for giving the user the possibility to filter displayed trips
 */
public class FilterTrips {

    //Context to have possibility to change sharedPreferences
    private Context mContext;
    //Max possible cost and its currency
    private String mCost;
    private int mCurrency;
    //Possible date period
    private long mDateFromTimestamp;
    private long mDateToTimestamp;
    //User interesting sports
    private Set<String> mSports;
    //User interesting countries
    private Set<String> mCountries;
    //Sorting and sorting order preferences
    private int mSortingPreferences;
    private int mSortingOrderPreferences;

    public FilterTrips(){
        mContext = CatalogActivity.getContext();
    }

    /**
     * Set methods
     */
    public void setmCost(String mCost) {
        this.mCost = mCost;
    }
    public void setmCurrency(int mCurrency) {
        this.mCurrency = mCurrency;
    }
    public void setmDateFromTimestamp(long mDateFromTimestamp) {
        this.mDateFromTimestamp = mDateFromTimestamp;
    }
    public void setmDateToTimestamp(long mDateToTimestamp) {
        this.mDateToTimestamp = mDateToTimestamp;
    }
    public void setmSports(Set<String> sports) {
        this.mSports = sports;
    }
    public void setmCountries(Set<String> countries){
        this.mCountries = countries;
    }
    public void setmSortingPreferences(int mSortingPreferences) {
        this.mSortingPreferences = mSortingPreferences;
    }
    public void setmSortingOrderPreferences(int mSortingOrderPreferences) {
        this.mSortingOrderPreferences = mSortingOrderPreferences;
    }


    /**
     * Set all filter preferences
     */
    public void setFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = filterPref.edit();
        editor.putString(mContext.getString(R.string.settings_filter_cost_key),mCost);
        editor.putInt(mContext.getString(R.string.settings_filter_currency_key),mCurrency);
        editor.putLong(mContext.getString(R.string.settings_filter_date_from_key),mDateFromTimestamp);
        editor.putLong(mContext.getString(R.string.settings_filter_date_to_key),mDateToTimestamp);
        editor.putStringSet(mContext.getString(R.string.settings_filter_sports_key),mSports);
        editor.putStringSet(mContext.getString(R.string.settings_display_countries_key),mCountries);
        editor.putString(mContext.getString(R.string.settings_display_sorting_trips_by_key),String.valueOf(mSortingPreferences + 1));
        editor.putString(mContext.getString(R.string.settings_display_sorting_order_trips_by_key),String.valueOf(mSortingOrderPreferences + 1));
        editor.apply();
    }
    /**
     * Get functions
     */
    public String getmCost(){
        return mCost;
    }
    public int getmCurrency() {
        return mCurrency;
    }
    public long getmDateFromTimestamp() {
        return mDateFromTimestamp;
    }
    public long getmDateToTimestamp() {
        return mDateToTimestamp;
    }
    //It need to be new HashSet otherwise possibility to dismiss Filter sports and countries changes didn't work
    public Set<String> getmSports() {
        return new HashSet<String>(mSports);
    }
    public Set<String> getmCountries() {
        return new HashSet<String>(mCountries);
    }
    public int getmSortingPreferences() {
        return mSortingPreferences;
    }
    public int getmSortingOrderPreferences() {
        return mSortingOrderPreferences;
    }

    /**
     * Get filter data from preferences
     */
    public void getFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCost = filterPref.getString(mContext.getString(R.string.settings_filter_cost_key),FilterTripsContract.FilterTripsEntry.DEFAULT_COST);
        mCurrency = filterPref.getInt(mContext.getString(R.string.settings_filter_currency_key),0);
        //TODO Add min. timestamp = today, checking
        mDateFromTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_from_key),System.currentTimeMillis());
        mDateToTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_to_key),System.currentTimeMillis() + FilterTripsContract.FilterTripsEntry.DEFAULT_DURATION_TIMESTAMP);
        mSports = filterPref.getStringSet(mContext.getString(R.string.settings_filter_sports_key),new HashSet<String>());
        mCountries = filterPref.getStringSet(mContext.getString(R.string.settings_display_countries_key), new HashSet<String>());
        mSortingPreferences = Integer.valueOf(filterPref.getString(mContext.getString(R.string.settings_display_sorting_trips_by_key),String.valueOf(FilterTripsContract.FilterTripsEntry.SORTING_DATE)))-1;
        mSortingOrderPreferences = Integer.valueOf(filterPref.getString(mContext.getString(R.string.settings_display_sorting_order_trips_by_key),String.valueOf(FilterTripsContract.FilterTripsEntry.ORDER_DECREASE)))-1;
    }

}
