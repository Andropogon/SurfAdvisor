package livewind.example.andro.liveWind.Countries;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.Country;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

public class CountryGridAdapter extends ArrayAdapter<String> {
    private int mColorResourceId;
    public CountryGridAdapter(Activity context, ArrayList<String> countryNumbers,int resource) {

        super(context, 0, countryNumbers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_countries, parent, false);
        }
        int currentCountryNumber = Integer.valueOf(getItem(position));
        ImageView countryFlagImageView= gridItemView.findViewById(R.id.grid_countries_flag_image_view);
        switch (currentCountryNumber) {
            case EventContract.EventEntry.COUNTRY_POLAND:
                countryFlagImageView.setImageResource(R.drawable.flag_pl);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                countryFlagImageView.setImageResource(R.drawable.flag_gr);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                countryFlagImageView.setImageResource(R.drawable.flag_es);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                countryFlagImageView.setImageResource(R.drawable.flag_hr);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                countryFlagImageView.setImageResource(R.drawable.flag_pt);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                countryFlagImageView.setImageResource(R.drawable.flag_de);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                countryFlagImageView.setImageResource(R.drawable.flag_fr);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                countryFlagImageView.setImageResource(R.drawable.flag_za);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                countryFlagImageView.setImageResource(R.drawable.flag_ma);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                countryFlagImageView.setImageResource(R.drawable.flag_it);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                countryFlagImageView.setImageResource(R.drawable.flag_eg);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                countryFlagImageView.setImageResource(R.drawable.flag_uk);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                countryFlagImageView.setImageResource(R.drawable.flag_tr);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                countryFlagImageView.setImageResource(R.drawable.flag_at);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                countryFlagImageView.setImageResource(R.drawable.flag_dk);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                countryFlagImageView.setImageResource(R.drawable.flag_br);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                countryFlagImageView.setImageResource(R.drawable.flag_us);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                countryFlagImageView.setImageResource(R.drawable.flag_vn);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                countryFlagImageView.setImageResource(R.drawable.flag_mt);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                countryFlagImageView.setImageResource(R.drawable.flag_pl);
                break;
            default:
                countryFlagImageView.setImageResource(R.drawable.flag_world);
                break;
        }
        return gridItemView;
    }
}
