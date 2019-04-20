package livewind.example.andro.liveWind.Countries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.Country;
import livewind.example.andro.liveWind.Event;
import livewind.example.andro.liveWind.Filter.FilterTrips;
import livewind.example.andro.liveWind.Filter.FilterTripsActivity;
import livewind.example.andro.liveWind.Filter.FilterTripsPresenter;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

/**
 * Created by JGJ on 10/01/19.
 * Dialog to select interesting countries for coverages and trips
 */
public class CountryDialog {
    //For displaying trips from/to/from-to
    private static int mTripsOptions = 0;

    /**
     * Select countries dialog for trips - used in {@FilterTripsActvity}
     * @param context - FilterTripsActivity
     * @param selectedCountries - loaded selected countries filter
     * @return - {@param selectedCountries} after changes
     */
    public static Set<String> showSelectCountryDialog(final FilterTripsActivity context, final Set<String> selectedCountries) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogeTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_catalog_dialog_select_country,null);

        //Show select trip display options
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int displayTripsOptions = Integer.valueOf(sharedPrefs.getString(context.getString(R.string.settings_display_trips_key), "1"));
        Spinner mTripsOptionsSpinner = (Spinner) dialogView.findViewById(R.id.spinner_trip_display_options);
        setupTripOptionsSpinner(context, mTripsOptionsSpinner);
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

        //Make list with countries
        ListView listView = dialogView.findViewById(R.id.dialog_catalog_activity_select_country_list_view);
        final ArrayList<Country> mList = new ArrayList<Country>();
        loadCountriesToList(context,mList);
        CountryAdapter adapter = new CountryAdapter(context, mList,EventContract.EventEntry.IT_IS_TRIP,selectedCountries);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.color.app_primary_color);

        //Set click listeners to check / uncheck country
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mList.get(position).isChecked()) {
                    //Uncheck country and countryItemView
                    mList.get(position).setChecked(false);
                    ImageView mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                    mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                    selectedCountries.remove(Integer.toString(position));
                } else {
                    //Check country and countryItemView
                    mList.get(position).setChecked(true);
                    ImageView mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                    mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_white_24dp);
                    //Set this country in "interested_countries" in sharedPref
                    selectedCountries.add(Integer.toString(position));
                }
            }
        });

        //Set approve button
        builder.setView(dialogView)
                //builder.setView(gridView)
                .setPositiveButton(R.string.dialog_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(selectedCountries.contains("0")&&selectedCountries.size()!=1){
                            showCountryChangesConfirmationDialog(context,selectedCountries);
                        } else if (selectedCountries.isEmpty()) {
                            showCountryChangesNullDialog(context,selectedCountries);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
        return selectedCountries;
    }

    /**
     * Dialog showed when user click apply on SelectCountryDialog and check "All world" and one or more other country.
     * (For trips country dialog)
     */
    public static void showCountryChangesConfirmationDialog(final FilterTripsActivity context, final Set<String> selectedCountries) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.catalog_activity_changes_confrimity_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_confrimity_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectedCountries.remove(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                if (dialog != null) {
                    //Display countries in {@FilterTripsActivity} grid view
                    context.displayCountries();
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog(context,selectedCountries);
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
     * (For trips country dialog)
     */
    private static void showCountryChangesNullDialog(final FilterTripsActivity context, final Set<String> selectedCountries) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.catalog_activity_changes_null_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_null_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectedCountries.add(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                if (dialog != null) {
                    //Strange construction but i can't find alternative solution.
                    context.displayCountries();
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog(context, selectedCountries );
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
     * Setup the dropdown spinner that allows the user to select the countries display preferences.
     * (For trips country dialog)
     */
    private static void setupTripOptionsSpinner(final Activity context, Spinner tripOptionsSpinner) {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter tripOptionsSpinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.array_trips_display_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        tripOptionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        tripOptionsSpinner.setAdapter(tripOptionsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        tripOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(context.getString(R.string.display_trips_from_and_to))) {
                        mTripsOptions = EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO;
                    } else if (selection.equals(context.getString(R.string.display_trips_from))) {
                        mTripsOptions = EventContract.EventEntry.DISPLAY_TRIPS_FROM;
                    } else if (selection.equals(context.getString(R.string.display_trips_to))){
                        mTripsOptions = EventContract.EventEntry.DISPLAY_TRIPS_TO;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTripsOptions = EventContract.EventEntry.TYPE_WINDSURFING;
            }
        });
    }

    /**
     * Select countries dialog for coverages - used in {@CatalogActivity}
     * @param context - CatalogActivity, but could be other
     * @return - nothing - changes will be send to sharedPreferences
     */
    //TODO add set off notification option
    public static void showSelectCountryDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogeTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_catalog_dialog_select_country,null);

        //Make listView with countries
        ListView listView = dialogView.findViewById(R.id.dialog_catalog_activity_select_country_list_view);
        final ArrayList<Country> mList = new ArrayList<Country>();
        loadCountriesToList(context,mList);
        CountryAdapter adapter = new CountryAdapter(context, mList,EventContract.EventEntry.IT_IS_EVENT);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.color.app_primary_color);

        //Set on clickListeners to check / uncheck country
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (mList.get(position).isChecked()) {
                        //Uncheck country and countryItemView
                        mList.get(position).setChecked(false);
                        ImageView mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                        mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                        //Topic (notifications) unsubscribe:
                        if(position==0) {
                            //If position 0 -> unsubscribe all topics
                            for (int i = 1; i <= 20; i++) {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(mList.get(i).getTopicKey());
                            }
                        } else {
                            //Else -> unsubscribe one topic
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(mList.get(position).getTopicKey());
                        }
                        //Remove this country from "interested_coverages_countries" in sharedPref
                        //Get interested countries Set
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Set<String> selectedCountries = sharedPrefs.getStringSet(context.getString(R.string.settings_display_coverages_countries_key), new HashSet<String>());
                        selectedCountries.remove(Integer.toString(position));
                        //Set interested countries Set without unchecked country
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putStringSet(context.getString(R.string.settings_display_coverages_countries_key), selectedCountries);
                        editor.apply();
                    } else {
                        //Check country and countryItemView
                        mList.get(position).setChecked(true);
                        ImageView mCheckBoxImageView = view.findViewById(R.id.select_country_list_check_box_image_view);
                        mCheckBoxImageView.setImageResource(R.drawable.ic_check_box_white_24dp);
                        //Topic (notifications) subscription
                        if(position==0){
                            //If position 0 -> subscribe all topics
                            for(int i=1; i<=20;i++){
                                FirebaseMessaging.getInstance().subscribeToTopic(mList.get(i).getTopicKey());
                            }
                        } else {
                            //Else -> subscribe one topic
                            FirebaseMessaging.getInstance().subscribeToTopic(mList.get(position).getTopicKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = mList.get(position).getTopicKey();
                                    if (!task.isSuccessful()) {
                                        msg = context.getString(R.string.country_number_20);
                                    }
                                    Toast.makeText(context, "Country subscribed: " + msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //Set this country in "interested_coverages_countries" in sharedPref
                        //Get interested countries Set
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Set<String> selectedCountries = sharedPrefs.getStringSet(context.getString(R.string.settings_display_coverages_countries_key), new HashSet<String>());
                        selectedCountries.add(Integer.toString(position));
                        //Set interested countries Set with checked country
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putStringSet(context.getString(R.string.settings_display_coverages_countries_key), selectedCountries);
                        editor.apply();
                    }
            }
        });

        //Set approve button
        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Set<String> selectedCountries = sharedPrefs.getStringSet(context.getString(R.string.settings_display_coverages_countries_key), new HashSet<String>());
                        if(selectedCountries.contains("0")&&selectedCountries.size()!=1){
                            showCountryChangesConfirmationDialog(context);
                        } else if (selectedCountries.isEmpty()) {
                            showCountryChangesNullDialog(context);
                        } else {
                            context.recreate();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
    }

    /**
     * Dialog showed when user click apply on SelectCountryDialog and check "All world" and one or more other country.
     * (For coverages)
     */
    public static void showCountryChangesConfirmationDialog(final Activity context) {
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
                context.recreate();
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
     * (For coverages)
     */
    private static void showCountryChangesNullDialog(final Activity context) {
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
                context.recreate();
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
     * Get ArrayList with countries add all countries and return
     * @param context - Activity
     * @return - ArrayList with loaded countries
     */
    private static ArrayList<Country> loadCountriesToList(Context context,final ArrayList<Country> mList) {
        mList.add(new Country(context.getString(R.string.country_number_0), R.drawable.flag_world, context.getString(R.string.country_number_0_key)));
        mList.add(new Country(context.getString(R.string.country_number_1), R.drawable.flag_pl, context.getString(R.string.country_number_1_key)));
        mList.add(new Country(context.getString(R.string.country_number_2), R.drawable.flag_gr, context.getString(R.string.country_number_2_key)));
        mList.add(new Country(context.getString(R.string.country_number_3), R.drawable.flag_es, context.getString(R.string.country_number_3_key)));
        mList.add(new Country(context.getString(R.string.country_number_4), R.drawable.flag_hr, context.getString(R.string.country_number_4_key)));
        mList.add(new Country(context.getString(R.string.country_number_5), R.drawable.flag_pt, context.getString(R.string.country_number_5_key)));
        mList.add(new Country(context.getString(R.string.country_number_6), R.drawable.flag_de, context.getString(R.string.country_number_6_key)));
        mList.add(new Country(context.getString(R.string.country_number_7), R.drawable.flag_fr, context.getString(R.string.country_number_7_key)));
        mList.add(new Country(context.getString(R.string.country_number_8), R.drawable.flag_za, context.getString(R.string.country_number_8_key)));
        mList.add(new Country(context.getString(R.string.country_number_9), R.drawable.flag_ma, context.getString(R.string.country_number_9_key)));
        mList.add(new Country(context.getString(R.string.country_number_10), R.drawable.flag_it, context.getString(R.string.country_number_10_key)));
        mList.add(new Country(context.getString(R.string.country_number_11), R.drawable.flag_eg, context.getString(R.string.country_number_11_key)));
        mList.add(new Country(context.getString(R.string.country_number_12), R.drawable.flag_uk, context.getString(R.string.country_number_12_key)));
        mList.add(new Country(context.getString(R.string.country_number_13), R.drawable.flag_tr, context.getString(R.string.country_number_13_key)));
        mList.add(new Country(context.getString(R.string.country_number_14), R.drawable.flag_at, context.getString(R.string.country_number_14_key)));
        mList.add(new Country(context.getString(R.string.country_number_15), R.drawable.flag_dk, context.getString(R.string.country_number_15_key)));
        mList.add(new Country(context.getString(R.string.country_number_16), R.drawable.flag_br, context.getString(R.string.country_number_16_key)));
        mList.add(new Country(context.getString(R.string.country_number_17), R.drawable.flag_us, context.getString(R.string.country_number_17_key)));
        mList.add(new Country(context.getString(R.string.country_number_18), R.drawable.flag_vn, context.getString(R.string.country_number_18_key)));
        mList.add(new Country(context.getString(R.string.country_number_19), R.drawable.flag_mt, context.getString(R.string.country_number_19_key)));
        mList.add(new Country(context.getString(R.string.country_number_20), R.drawable.flag_world, context.getString(R.string.country_number_20_key)));
    return mList;
    }
}
