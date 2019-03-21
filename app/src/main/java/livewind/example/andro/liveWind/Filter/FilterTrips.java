package livewind.example.andro.liveWind.Filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;

/**
 * Model for FilterTripsPresenter and FilterTripsActivity MVP
 */
public class FilterTrips {

    private Context mContext;
    private String mCost; //Max cost
    private long mDateFromTimestamp;
    private long mDateToTimestamp;
    private Set<String> mSports;

    public FilterTrips(){
        mContext = CatalogActivity.getContext();
    }


    // Set functions
    public void setmCost(String mCost) {
        this.mCost = mCost;
    }

    public void setmDateFromTimestamp(long mDateFromTimestamp) {
        this.mDateFromTimestamp = mDateFromTimestamp;
    }

    public void setmDateToTimestamp(long mDateToTimestamp) {
        this.mDateToTimestamp = mDateToTimestamp;
    }

    public void setmSports(Set<String> mSports) {
        this.mSports = mSports;
    }

    /**
     * Set all filter preferences
     */
    public void setFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = filterPref.edit();
        editor.putString(mContext.getString(R.string.settings_filter_cost_key),mCost);
        editor.putLong(mContext.getString(R.string.settings_filter_date_from_key),mDateFromTimestamp);
        editor.putLong(mContext.getString(R.string.settings_filter_date_to_key),mDateToTimestamp);
        editor.putStringSet(mContext.getString(R.string.settings_filter_sports_key),mSports);
        editor.apply();
    }
    /**
     * Get functions
     */
    public String getmCost(){
        return mCost;
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

    /**
     * Get filter data from preferences
     */
    public void getFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCost = filterPref.getString(mContext.getString(R.string.settings_filter_cost_key),"-1");
        mDateFromTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_from_key),-1);
        mDateToTimestamp = filterPref.getLong(mContext.getString(R.string.settings_filter_date_to_key),-1);
        mSports = filterPref.getStringSet(mContext.getString(R.string.settings_filter_sports_key), new HashSet<String>());
    }
}
