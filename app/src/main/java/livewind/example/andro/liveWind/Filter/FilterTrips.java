package livewind.example.andro.liveWind.Filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;

/**
 * Model for FilterTripsPresenter and FilterTripsActivity MVP
 */
public class FilterTrips {

    private Context mContext;
    private String mCost; //Max cost

    public FilterTrips(){
        mContext = CatalogActivity.getContext();
    }


    // Set functions
    public void setmCost(String mCost) {
        this.mCost = mCost;
    }

    /**
     * Set all filter preferences
     */
    public void setFilterTripsPreferences(){
        SharedPreferences filterPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = filterPref.edit();
        editor.putString(mContext.getString(R.string.settings_filter_cost_key),mCost);
        editor.apply();
    }
}
