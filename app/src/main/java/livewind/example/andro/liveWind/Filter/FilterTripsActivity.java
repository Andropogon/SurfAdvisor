package livewind.example.andro.liveWind.Filter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Countries.CountryDialog;
import livewind.example.andro.liveWind.Countries.CountryGridAdapter;
import livewind.example.andro.liveWind.HelpClasses.DateHelp;
import livewind.example.andro.liveWind.ListView_help.ListViewHelp;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

/**
 * Created by JGJ on 20/03/19.
 * View (part) of Filter MVP
 * Filter MVP is responsible for giving the user the possibility to filter displayed trips
 */
public class FilterTripsActivity extends AppCompatActivity
        implements FilterTripsContract.View {
    private static final String TAG = "FilterTripsActivity";
    private FilterTripsContract.Presenter mPresenter;

    //UI properties
    private EditText mCostView;
    private Spinner mCurrencySpinner;
    private TextView mDateFromTextView;
    private TextView mDateToTextView;
    private TextView mSportsTextView;
    private ImageView mWindsurfingImageView;
    private ImageView mKitesurfingImageView;
    private ImageView mSurfingImageView;
    private TextView mCountriesTextView;
    private GridView mCountriesGridView;
    private Spinner mCountriesDisplaySpinner;
    private CountryGridAdapter mCountryGridAdapter;
    private Spinner mSortingSpinner;
    private Spinner mSortingOrderSpinner;

    private TextView mSetDefaultTextView;
    private TextView mSearchButtonTextView;

    // Helper global variables for spinner and multiselect lists
    boolean[] mCheckedItems = new boolean[3];
    int mCurrency = EventContract.EventEntry.CURRENCY_ZL;
    int mCountriesDisplayPreferences = FilterTripsContract.FilterTripsEntry.DISPLAY_FROM_AND_TO;
    int mSortingPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;
    int mSortingOrderPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;


    /**
     * Boolean flag that keeps track of whether the filters has been edited (true) or not (false)
     */
    private boolean mFiltersHasChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initViews();
        // Create presenter
        mPresenter = new FilterTripsPresenter(this);
        mPresenter.loadPreferences();
        // Create adapter for CountryGridView
        mCountryGridAdapter = new CountryGridAdapter(this, mPresenter.getCountriesArray(),0);
        mCountriesGridView.setAdapter(mCountryGridAdapter);
        ListViewHelp.setListViewHeightBasedOnChildren(mCountriesGridView,6);
        //Initialize click listeners on views
        initClickListeners();
    }

    private void initViews() {
        mCostView = findViewById(R.id.filter_price_value_edit_text);
        mCurrencySpinner = findViewById(R.id.filter_price_currency_spinner);

        mDateFromTextView = findViewById(R.id.filter_date_from_text_view);
        mDateToTextView = findViewById(R.id.filter_date_to_text_view);

        mSportsTextView = findViewById(R.id.filter_sports_text_view);
        mWindsurfingImageView = findViewById(R.id.windsurfing_image_view);
        mKitesurfingImageView = findViewById(R.id.kitesurfing_image_view);
        mSurfingImageView = findViewById(R.id.surfing_image_view);

        mCountriesTextView = findViewById(R.id.filter_countries_text_view);
        mCountriesGridView = findViewById(R.id.filter_countries_grid_view);

        mCountriesDisplaySpinner = findViewById(R.id.filter_countries_display_preferences_spinner);

        mSortingSpinner = findViewById(R.id.filter_sort_spinner);
        mSortingOrderSpinner = findViewById(R.id.filter_sort_order_spinner);

        mSearchButtonTextView = findViewById(R.id.filter_search_button_text_view);
        mSetDefaultTextView = findViewById(R.id.filter_set_default_text_view);

        //Set touch listeners to all views that could change @FilterTrips
        mCostView.setOnTouchListener(mTouchListener);
        mCurrencySpinner.setOnTouchListener(mTouchListener);
        mDateFromTextView.setOnTouchListener(mTouchListener);
        mDateToTextView.setOnTouchListener(mTouchListener);
        mSportsTextView.setOnTouchListener(mTouchListener);
        mWindsurfingImageView.setOnTouchListener(mTouchListener);
        mKitesurfingImageView.setOnTouchListener(mTouchListener);
        mSurfingImageView.setOnTouchListener(mTouchListener);
        mCountriesTextView.setOnTouchListener(mTouchListener);
        mCountriesDisplaySpinner.setOnTouchListener(mTouchListener);
        mSortingSpinner.setOnTouchListener(mTouchListener);
        mSortingOrderSpinner.setOnTouchListener(mTouchListener);
        mSetDefaultTextView.setOnTouchListener(mTouchListener);

        setupCurrencySpinner();
        setupCountriesDisplaySpinner();
        setupSortingSpinner();
        setupSortingOrderSpinner();
        loadCurrencySpinner();
        loadCountriesDisplaySpinner();
        loadSortingSpinner();
        loadSortingOrderSpinner();
    }
    private void initClickListeners(){
        mDateFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                DateHelp.showDatePickerDialog(FilterTripsActivity.this,mDateFromTextView,calendar);
            }
        });
        mDateToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                //TODO Change calendar date to "from date - mDateFrom"
                DateHelp.showDatePickerDialog(FilterTripsActivity.this,mDateToTextView,calendar);
            }
        });
        mSportsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mWindsurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mKitesurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mSurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mCountriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CountryDialog is saving checked countries as interesting.
                mPresenter.saveCountries(CountryDialog.showSelectCountryDialog(FilterTripsActivity.this,mPresenter.getCountries()));
            }
        });
        mCountriesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //CountryDialog is saving checked countries as interesting.
                mPresenter.saveCountries(CountryDialog.showSelectCountryDialog(FilterTripsActivity.this,mPresenter.getCountries()));
            }
        });
        mSearchButtonTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveAndOpenCatalogActivity();
            }
        });
        mSetDefaultTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDefaultWarningDialog();
            }
        });
    }

    /**
     * Some menu methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_save:
                saveAndOpenCatalogActivity();
                    return true;
            case android.R.id.home:
                    if (!mFiltersHasChanged) {
                        NavUtils.navigateUpFromSameTask(FilterTripsActivity.this);
                        return true;
                    } else {
                        showUnsavedChangesDialog();
                        return true;
                    }
                }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mFiltersHasChanged) {
            NavUtils.navigateUpFromSameTask(FilterTripsActivity.this);
            return;
        } else {
            showUnsavedChangesDialog();
            return;
        }
    }

    /**
     * Display loaded filters on start method
     */
    @Override
    public void displayPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp, Set<String> countries, int sortingPreferences, int sortingOrderPreferences, int countriesDisplayPreferences){
        mCostView.setText(cost);
        mCurrencySpinner.setSelection(currency);
        mDateFromTextView.setText(DateHelp.timestampToDate(dateFromTimestamp));
        mDateToTextView.setText(DateHelp.timestampToDate(dateToTimestamp));
        mCountriesDisplaySpinner.setSelection(countriesDisplayPreferences);
        mSortingSpinner.setSelection(sortingPreferences);
        mSortingOrderSpinner.setSelection(sortingOrderPreferences);
    }
    @Override
    public void displaySports(Set <String> sports){
        int interestedColor = R.color.sport_available_course;
        int noInterestedColor = R.color.sport_available_no;
        int interestedColorCode = ContextCompat.getColor(FilterTripsActivity.this, interestedColor);
        int noInterestedColorCode = ContextCompat.getColor(FilterTripsActivity.this, noInterestedColor);
        Drawable interestedWindsurfingBackgroundView = mWindsurfingImageView.getBackground();
        Drawable interestedKitesurfingBackgroundView = mKitesurfingImageView.getBackground();
        Drawable interestedSurfingBackgroundView = mSurfingImageView.getBackground();
        if(sports.contains(FilterTripsContract.FilterTripsEntry.SPORT_WINDSURFING)) {
            interestedWindsurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[0] = true;
        } else {
            interestedWindsurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[0] = false;
        }
        if(sports.contains(FilterTripsContract.FilterTripsEntry.SPORT_KITESURFING)) {
            interestedKitesurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[1] = true;
        } else {
            interestedKitesurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[1] = false;
        }
        if(sports.contains(FilterTripsContract.FilterTripsEntry.SPORT_SURFING)) {
            interestedSurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[2] = true;
        } else {
            interestedSurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[2] = false;
        }
    }
    @Override
    public void displayCountries(){
        mCountryGridAdapter = new CountryGridAdapter(this, mPresenter.getCountriesArray(),0);
        mCountriesGridView.setAdapter(mCountryGridAdapter);
        ListViewHelp.setListViewHeightBasedOnChildren(mCountriesGridView,6);
    }
    /**
     * Show toast message about bad set filter
     * @param errorCode - code of bad set filter error from {@FilterTripsContract}
     */
    @Override
    public void showBadFilterToast(int errorCode){
        switch (errorCode){
            case FilterTripsContract.FilterTripsEntry.BAD_FILTER_DATE:
                Toast.makeText(getApplicationContext(), getString(R.string.filter_trips_bad_date_toast), Toast.LENGTH_LONG).show();
                break;
            case FilterTripsContract.FilterTripsEntry.BAD_FILTER_NO_COUNTRIES:
                Toast.makeText(getApplicationContext(), getString(R.string.filter_trips_bad_countries_toast), Toast.LENGTH_LONG).show();
                break;
            case FilterTripsContract.FilterTripsEntry.BAD_FILTER_NO_SPORTS:
                Toast.makeText(getApplicationContext(), getString(R.string.filter_trips_bad_sports_toast), Toast.LENGTH_LONG).show();
                break;
            case FilterTripsContract.FilterTripsEntry.BAD_FILTER_COST:
                Toast.makeText(getApplicationContext(), getString(R.string.filter_trips_bad_cost_toast), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), getString(R.string.filter_trips_bad_unknown_toast), Toast.LENGTH_LONG).show();
                break;
        }
    }
    /**
     * Save data to preferences and open CatalogActivity
     */
    private void saveAndOpenCatalogActivity(){
        if(mPresenter.setPreferences(mCostView.getText().toString(), mCurrency,DateHelp.dateToTimestamp(mDateFromTextView.getText().toString()),DateHelp.dateToTimestamp(mDateToTextView.getText().toString()),mSortingPreferences, mSortingOrderPreferences, mCountriesDisplayPreferences)){
            mPresenter.sendPreferences();
            Intent intentCatalog = new Intent(FilterTripsActivity.this,CatalogActivity.class);
            startActivity(intentCatalog);
        } else {
            //Toast message with info about bad filter will be displayed (because of @setPreferences method
        }

    }

    /**
     * Dialog with multiselect list of sports
     * @param sports sports to display in dialog
     */
    private void showMultiSelectSportsList(final Set<String> sports){
        final String[] listItems;
        listItems = getResources().getStringArray(R.array.array_filter_sports);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FilterTripsActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.filter_trips_sports_dialog_title));
        mBuilder.setMultiChoiceItems(listItems, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked) {
                    if (!sports.contains(Integer.toString(position))) {
                        sports.add(Integer.toString(position));
                    }
                }
                else if(sports.contains(Integer.toString(position))){
                    sports.remove(Integer.toString(position));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //If sports aren't empties save them otherwise show dialog again
                if(mPresenter.saveSports(sports)) {
                    dialogInterface.dismiss();
                } else {
                    showMultiSelectSportsList(sports);
                }
            }

        });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the currency of max trip cost
     */
    private void setupCurrencySpinner() {
        ArrayAdapter currencySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_currency_options, android.R.layout.simple_spinner_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCurrencySpinner.setAdapter(currencySpinnerAdapter);
        mCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.currency_zl))) {
                        mCurrency = EventContract.EventEntry.CURRENCY_ZL;
                    } else if (selection.equals(getString(R.string.currency_euro))) {
                        mCurrency = EventContract.EventEntry.CURRENCY_EURO;
                    }else if (selection.equals(getString(R.string.currency_usd))) {
                        mCurrency = EventContract.EventEntry.CURRENCY_USD;
                    } else {
                        //Default
                        mCurrency = EventContract.EventEntry.CURRENCY_ZL;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrency = EventContract.EventEntry.CURRENCY_ZL;
            }
        });
    }
    /**
     * Load the dropdown spinner that allows the user to select the currency of max trip cost
     */
    private void loadCurrencySpinner(){
        switch (mCurrency) {
            case EventContract.EventEntry.CURRENCY_ZL:
                mCurrencySpinner.setSelection(0);
                break;
            case EventContract.EventEntry.CURRENCY_EURO:
                mCurrencySpinner.setSelection(1);
                break;
            case EventContract.EventEntry.CURRENCY_USD:
                mCurrencySpinner.setSelection(2);
                break;
            default:
                mCurrencySpinner.setSelection(0);
                break;
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the display countries preferences.
     */
    private void setupCountriesDisplaySpinner() {
        ArrayAdapter displayCountriesSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_trips_display_options, android.R.layout.simple_spinner_item);
        displayCountriesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCountriesDisplaySpinner.setAdapter(displayCountriesSpinnerAdapter);
        mCountriesDisplaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.display_trips_from_and_to))) {
                        mCountriesDisplayPreferences = EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO;
                    } else if (selection.equals(getString(R.string.display_trips_from))) {
                        mCountriesDisplayPreferences = EventContract.EventEntry.DISPLAY_TRIPS_FROM;
                    } else if (selection.equals(getString(R.string.display_trips_to))){
                        mCountriesDisplayPreferences = EventContract.EventEntry.DISPLAY_TRIPS_TO;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountriesDisplayPreferences = EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO;
            }
        });
    }

    /**
     * Load the dropdown spinner that allows the user to select the display countries preferences.
     */
    private void loadCountriesDisplaySpinner(){
        switch (mCountriesDisplayPreferences) {
            case FilterTripsContract.FilterTripsEntry.DISPLAY_FROM_AND_TO:
                mCountriesDisplaySpinner.setSelection(0);
                break;
            case FilterTripsContract.FilterTripsEntry.DISPLAY_FROM:
                mCountriesDisplaySpinner.setSelection(1);
                break;
            case FilterTripsContract.FilterTripsEntry.DISPLAY_TO:
                mCountriesDisplaySpinner.setSelection(2);
                break;
            default:
                mCountriesDisplaySpinner.setSelection(0);
                break;
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the sorting preferences.
     */
    private void setupSortingSpinner() {
        ArrayAdapter sortingSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_sorting_trips_options, android.R.layout.simple_spinner_item);
        sortingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSortingSpinner.setAdapter(sortingSpinnerAdapter);
        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sorting_trips_by_date))) {
                        mSortingPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;
                    } else if (selection.equals(getString(R.string.sorting_trips_by_cost))) {
                        mSortingPreferences = FilterTripsContract.FilterTripsEntry.SORTING_COST;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSortingPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;
            }
        });
    }

    /**
     * Load the dropdown spinner that allows the user to select the sorting preferences.
     */
    private void loadSortingSpinner(){
        switch (mSortingPreferences) {
            case FilterTripsContract.FilterTripsEntry.SORTING_DATE:
                mSortingSpinner.setSelection(0);
                break;
            case FilterTripsContract.FilterTripsEntry.SORTING_COST:
                mSortingSpinner.setSelection(1);
                break;
            default:
                mSortingSpinner.setSelection(0);
                break;
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the sorting order preferences.
     */
    private void setupSortingOrderSpinner() {
        ArrayAdapter sortingOrderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_sorting_order_options, android.R.layout.simple_spinner_item);
        sortingOrderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSortingOrderSpinner.setAdapter(sortingOrderSpinnerAdapter);
        mSortingOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sorting_order_trips_increase))) {
                        mSortingOrderPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;
                    } else if (selection.equals(getString(R.string.sorting_order_trips_decrease))) {
                        mSortingOrderPreferences = FilterTripsContract.FilterTripsEntry.SORTING_COST;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSortingOrderPreferences = FilterTripsContract.FilterTripsEntry.SORTING_DATE;
            }
        });
    }

    /**
     * Load the dropdown spinner that allows the user to select the sorting order preferences.
     */
    private void loadSortingOrderSpinner(){
        switch (mSortingOrderPreferences) {
            case FilterTripsContract.FilterTripsEntry.ORDER_INCREASE:
                mSortingOrderSpinner.setSelection(0);
                break;
            case FilterTripsContract.FilterTripsEntry.ORDER_DECREASE:
                mSortingOrderSpinner.setSelection(1);
                break;
            default:
                mSortingOrderSpinner.setSelection(0);
                break;
        }
    }

    /**
     * Show dialog about confirmation to set default filter values
     */
    private void showDefaultWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setMessage(R.string.filter_trips_set_default_warning);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the event.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User clicked that he want to load default values
                mPresenter.loadDefaultPreferences();
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
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mFiltersHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFiltersHasChanged = true;
            return false;
        }
    };

    /**
     * Show dialog about unsaved changes if user edited @FilterTrips and clicked cancel or back button.
     */
    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setMessage(R.string.filter_trips_dismiss_changes_warning);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.dismissChanges();
                finish();
            }
        });
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the filters.
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