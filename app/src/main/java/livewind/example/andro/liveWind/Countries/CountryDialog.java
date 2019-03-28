package livewind.example.andro.liveWind.Countries;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.Country;
import livewind.example.andro.liveWind.CountryAdapter;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

public class CountryDialog {
    /**
     * SELECT COUNTRY DIALOG
     */
    // Show select photo action
    public void showSelectCountryDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_catalog_dialog_select_country,null);
        // Set grid view to alertDialog
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int displayTripsOptions = Integer.valueOf(sharedPrefs.getString(context.getString(R.string.settings_display_trips_key),"1"));
        Spinner mTripsOptionsSpinner = (Spinner) dialogView.findViewById(R.id.spinner_trip_display_options);
        setupTripOptionsSpinner();

        switch (displayTripsOptions) {
            case EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO:
                mTripsOptionsSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.DISPLAY_TRIPS_FROM:
                mTripsOptionsSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.DISPLAY_TRIPS_TO:
                mTripsOptionsSpinner.setSelection(2);
                break;
            default:
                mTripsOptionsSpinner.setSelection(0);
                break;
        }

        ListView listView = dialogView.findViewById(R.id.dialog_catalog_activity_select_country_list_view);
        final ArrayList<Country> mList = new ArrayList<Country>();
        mList.add(new Country(context.getString(R.string.country_number_0),R.drawable.flag_world));
        mList.add(new Country(context.getString(R.string.country_number_1),R.drawable.flag_pl));
        mList.add(new Country(context.getString(R.string.country_number_2),R.drawable.flag_gr));
        mList.add(new Country(context.getString(R.string.country_number_3),R.drawable.flag_es));
        mList.add(new Country(context.getString(R.string.country_number_4),R.drawable.flag_hr));
        mList.add(new Country(context.getString(R.string.country_number_5),R.drawable.flag_pt));
        mList.add(new Country(context.getString(R.string.country_number_6),R.drawable.flag_de));
        mList.add(new Country(context.getString(R.string.country_number_7),R.drawable.flag_fr));
        mList.add(new Country(context.getString(R.string.country_number_8),R.drawable.flag_za));
        mList.add(new Country(context.getString(R.string.country_number_9),R.drawable.flag_ma));
        mList.add(new Country(context.getString(R.string.country_number_10),R.drawable.flag_it));
        mList.add(new Country(context.getString(R.string.country_number_11),R.drawable.flag_eg));
        mList.add(new Country(context.getString(R.string.country_number_12),R.drawable.flag_uk));
        mList.add(new Country(context.getString(R.string.country_number_13),R.drawable.flag_tr));
        mList.add(new Country(context.getString(R.string.country_number_14),R.drawable.flag_at));
        mList.add(new Country(context.getString(R.string.country_number_15),R.drawable.flag_dk));
        mList.add(new Country(context.getString(R.string.country_number_16),R.drawable.flag_br));
        mList.add(new Country(context.getString(R.string.country_number_17),R.drawable.flag_us));
        mList.add(new Country(context.getString(R.string.country_number_18),R.drawable.flag_vn));
        mList.add(new Country(context.getString(R.string.country_number_19),R.drawable.flag_mt));
        mList.add(new Country(context.getString(R.string.country_number_20),R.drawable.flag_world));
        CountryAdapter adapter = new CountryAdapter(this, mList,0);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.color.app_primary_color);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).isChecked()) {
                    mList.get(position).setChecked(false);
                    mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                    mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
                    SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = displayOptions.edit();
                    selectedCountries.remove(Integer.toString(position));
                    editor.putStringSet(getString(R.string.settings_display_countries_key),selectedCountries);
                    // Commit the edits!
                    editor.apply();
                    //   recreate();
                } else {
                    mList.get(position).setChecked(true);
                    mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                    mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_white_24dp);
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
                    SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = displayOptions.edit();
                    selectedCountries.add(Integer.toString(position));
                    editor.putStringSet(getString(R.string.settings_display_countries_key),selectedCountries);
                    // Commit the edits!
                    editor.apply();
                }
            }
        });
        builder.setView(dialogView)
                //builder.setView(gridView)
                .setPositiveButton(R.string.dialog_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
                        editor.putString(getString(R.string.settings_display_trips_key),Integer.toString(mTripsOptions));
                        editor.apply();
                        if(selectedCountries.contains("0")&&selectedCountries.size()!=1){
                            showCountryChangesConfirmationDialog();
                        } else if (selectedCountries.isEmpty()) {
                            showCountryChangesNullDialog();
                        } else {
                            recreate();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
    }

    /**
     * Dialog showed when user click apply on SelectCountryDialog and check "All world" and one or more other country.
     */
    private void showCountryChangesConfirmationDialog(final Context context) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.catalog_activity_changes_confrimity_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_confrimity_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                Set<String> selectedCountries = sharedPrefs.getStringSet(context.getString(R.string.settings_display_countries_key), new HashSet<String>());
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = displayOptions.edit();
                selectedCountries.remove(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                editor.putStringSet(context.getString(R.string.settings_display_countries_key),selectedCountries);
                // Commit the edits!
                editor.apply();
                recreate();
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog(context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Dialog showed when user click apply on SelectCountryDialog and check 0 countries.
     */
    private void showCountryChangesNullDialog(final Context context) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.catalog_activity_changes_null_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_null_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                Set<String> selectedCountries = sharedPrefs.getStringSet(context.getString(R.string.settings_display_countries_key), new HashSet<String>());
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = displayOptions.edit();
                selectedCountries.add(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                editor.putStringSet(context.getString(R.string.settings_display_countries_key),selectedCountries);
                // Commit the edits!
                editor.apply();
                recreate();
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog(context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
