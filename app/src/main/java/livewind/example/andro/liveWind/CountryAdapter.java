package livewind.example.andro.liveWind;
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

public class CountryAdapter extends ArrayAdapter<Country> {
    private int mColorResourceId;
    public CountryAdapter(Activity context, ArrayList<Country> profileIcons,int resource) {

        super(context, 0, profileIcons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_catalog_dialog_select_country_list_item, parent, false);
        }
        Country currentCountry = getItem(position);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> selectedCountries = sharedPrefs.getStringSet(getContext().getString(R.string.settings_display_countries_key), new HashSet<String>());

        ImageView flagImageView = (ImageView) listItemView.findViewById(R.id.select_country_list_image_view);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.select_country_list_text_view);
        ImageView countryCheckBoxImageView = (ImageView) listItemView.findViewById(R.id.select_country_list_check_box_image_view);
        //Check checkbox if user select earlier this country
        if(Integer.toString(position)!=null) {
            if (selectedCountries.contains(Integer.toString(position))) {
                currentCountry.setChecked(true);
                countryCheckBoxImageView.setImageResource(R.drawable.ic_check_box_white_24dp);
            } else {
                countryCheckBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                currentCountry.setChecked(false);
            }
        } else {
            countryCheckBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
            currentCountry.setChecked(false);
        }
        flagImageView.setImageResource(currentCountry.getFlagId());
        nameTextView.setText(currentCountry.getName());
        return listItemView;
    }
}