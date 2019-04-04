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

    private Context mContext;
    private String mCost; //Max cost
    private int mCurrency;
    private long mDateFromTimestamp;
    private long mDateToTimestamp;
    private Set<String> mSports;
    private Set<String> mCountries;

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
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = filterPref.edit();
        editor.putStringSet(mContext.getString(R.string.settings_filter_sports_key),sports);
        editor.apply();
        this.mSports = sports;
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
    public Set<String> getmSports() {
        return mSports;
    }
    public Set<String> getmCountries() {
        return mCountries;
    }

    /**
     * Get filter data from preferences
     */
    public void getFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCost = filterPref.getString(mContext.getString(R.string.settings_filter_cost_key),"-1");
        mCurrency = filterPref.getInt(mContext.getString(R.string.settings_filter_currency_key),-1);
        //TODO Add min. timestamp = today, checking
        mDateFromTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_from_key),-1);
        mDateToTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_to_key),-1);
        mSports = filterPref.getStringSet(mContext.getString(R.string.settings_filter_sports_key), new HashSet<String>());
        mCountries = filterPref.getStringSet(mContext.getString(R.string.settings_display_countries_key), new HashSet<String>());
    }
}
