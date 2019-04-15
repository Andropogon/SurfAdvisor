package livewind.example.andro.liveWind.Filter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Countries.CountryDialog;
import livewind.example.andro.liveWind.Countries.CountryGridAdapter;
import livewind.example.andro.liveWind.HelpClasses.DateHelp;
import livewind.example.andro.liveWind.ListView_help.ListViewHelp;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.data.FilterContract;

/**
 * Created by JGJ on 20/03/19.
 * View (part) of Filter MVP
 * Filter MVP is responsible for giving the user the possibility to filter displayed trips
 */
public class FilterTripsActivity extends AppCompatActivity
        implements FilterTripsContract.View {

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
    private CountryGridAdapter mCountryGridAdapter;
    private Spinner mSortingSpinner;

    private TextView mSetDefaultTextView;
    private TextView mSearchButtonTextView;

    boolean[] mCheckedItems = new boolean[3];
    int mCurrency = EventContract.EventEntry.CURRENCY_ZL;
    int mSortingPreferences = FilterContract.FilterTripsEntry.SORTING_DATE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initViews();
        // Creates presenter
        mPresenter = new FilterTripsPresenter(this);
        mPresenter.loadPreferences();
        mCountryGridAdapter = new CountryGridAdapter(this, mPresenter.getCountries(),0);
        mCountriesGridView.setAdapter(mCountryGridAdapter);
        ListViewHelp.setListViewHeightBasedOnChildren(mCountriesGridView,6);
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

        mSortingSpinner = findViewById(R.id.filter_sort_spinner);

        mSearchButtonTextView = findViewById(R.id.filter_search_button_text_view);
        mSetDefaultTextView = findViewById(R.id.filter_set_default_text_view);

        setupCurrencySpinner();
        setupSortingSpinner();
        loadCurrencySpinner();
        loadSortingSpinner();
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
                CountryDialog.showSelectCountryDialog(FilterTripsActivity.this,EventContract.EventEntry.IT_IS_TRIP);
            }
        });
        mCountriesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //CountryDialog is saving checked countries as interesting.
                CountryDialog.showSelectCountryDialog(FilterTripsActivity.this,EventContract.EventEntry.IT_IS_TRIP);
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
                mPresenter.loadDefaultPreferences();
            }
        });
    }

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
                // TODO Add checking unsaved edits if (!mEventHasChanged) {
                    finish();
                    return true;
                }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayPreferences(String cost, int currency, long dateFromTimestamp, long dateToTimestamp, Set<String> countries, int sortingPreferences){
        mCostView.setText(cost);
        mCurrencySpinner.setSelection(currency);
        mDateFromTextView.setText(DateHelp.timestampToDate(dateFromTimestamp));
        mDateToTextView.setText(DateHelp.timestampToDate(dateToTimestamp));
        mSortingSpinner.setSelection(sortingPreferences);
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
        if(sports.contains(FilterContract.FilterTripsEntry.SPORT_WINDSURFING)) {
            interestedWindsurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[0] = true;
        } else {
            interestedWindsurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[0] = false;
        }
        if(sports.contains(FilterContract.FilterTripsEntry.SPORT_KITESURFING)) {
            interestedKitesurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[1] = true;
        } else {
            interestedKitesurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[1] = false;
        }
        if(sports.contains(FilterContract.FilterTripsEntry.SPORT_SURFING)) {
            interestedSurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[2] = true;
        } else {
            interestedSurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            mCheckedItems[2] = false;
        }
    }

    @Override
    public void displayCountries(){
        mCountryGridAdapter = new CountryGridAdapter(this, mPresenter.getCountries(),0);
        mCountriesGridView.setAdapter(mCountryGridAdapter);
        ListViewHelp.setListViewHeightBasedOnChildren(mCountriesGridView,6);
    }

    /**
     * Save data to preferences and open CatalogActivity
     */
    private void saveAndOpenCatalogActivity(){
        mPresenter.savePreferences(mCostView.getText().toString(), mCurrency,DateHelp.dateToTimestamp(mDateFromTextView.getText().toString()),DateHelp.dateToTimestamp(mDateToTextView.getText().toString()),mSortingPreferences);
        mPresenter.sendPreferences();
        Intent intentCatalog = new Intent(FilterTripsActivity.this,CatalogActivity.class);
        startActivity(intentCatalog);
    }

    /**
     * Dialog with multiselect list of sports
     * @param sports sports to display in dialog
     */
    private void showMultiSelectSportsList(final Set<String> sports){
        final String[] listItems;
        listItems = getResources().getStringArray(R.array.array_filter_sports);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FilterTripsActivity.this);
        mBuilder.setTitle("Wybierz interesujące Cie sporty HC");
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
                mPresenter.saveSports(sports);

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
     * Setup the dropdown spinner that allows the user to select the sorting preferences.
     */
    private void setupSortingSpinner() {
        ArrayAdapter sortingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sorting_trips_options, android.R.layout.simple_spinner_item);

        sortingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSortingSpinner.setAdapter(sortingSpinnerAdapter);

        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sorting_trips_by_date))) {
                        mSortingPreferences = FilterContract.FilterTripsEntry.SORTING_DATE;
                    } else if (selection.equals(getString(R.string.sorting_trips_by_cost))) {
                        mSortingPreferences = FilterContract.FilterTripsEntry.SORTING_COST;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSortingPreferences = FilterContract.FilterTripsEntry.SORTING_DATE;
            }
        });
    }

    /**
     * Load the dropdown spinner that allows the user to select the sorting preferences.
     */
    private void loadSortingSpinner(){
        switch (mSortingPreferences) {
            case FilterContract.FilterTripsEntry.SORTING_DATE:
                mCurrencySpinner.setSelection(0);
                break;
            case FilterContract.FilterTripsEntry.SORTING_COST:
                mCurrencySpinner.setSelection(1);
                break;
            default:
                mCurrencySpinner.setSelection(0);
                break;
        }
    }
}