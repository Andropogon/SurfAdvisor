package livewind.example.andro.liveWind.Filter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Countries.CountryDialog;
import livewind.example.andro.liveWind.Countries.CountryGridAdapter;
import livewind.example.andro.liveWind.HelpClasses.DateHelp;
import livewind.example.andro.liveWind.R;

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
    private TextView mDateFromTextView;
    private TextView mDateToTextView;
    private TextView mSportsTextView;
    private ImageView mWindsurfingImageView;
    private ImageView mKitesurfingImageView;
    private ImageView mSurfingImageView;
    private TextView mCountriesTextView;
    private GridView mCountriesGridView;
    private CountryGridAdapter mCountryGridAdapter;

    boolean[] checkedItems = new boolean[3];

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
        initClickListeners();
    }

    private void initViews() {
        mCostView = findViewById(R.id.filter_price_value_edit_text);

        mDateFromTextView = findViewById(R.id.filter_date_from_text_view);
        mDateToTextView = findViewById(R.id.filter_date_to_text_view);

        mSportsTextView = findViewById(R.id.filter_sports_text_view);
        mWindsurfingImageView = findViewById(R.id.windsurfing_image_view);
        mKitesurfingImageView = findViewById(R.id.kitesurfing_image_view);
        mSurfingImageView = findViewById(R.id.surfing_image_view);

        mCountriesTextView = findViewById(R.id.filter_countries_text_view);
        mCountriesGridView = findViewById(R.id.filter_countries_grid_view);


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
                CountryDialog.showSelectCountryDialog(FilterTripsActivity.this);
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
                mPresenter.savePreferences(mCostView.getText().toString(),DateHelp.dateToTimestamp(mDateFromTextView.getText().toString()),DateHelp.dateToTimestamp(mDateToTextView.getText().toString()));
                mPresenter.sendPreferences();
                Intent intentCatalog = new Intent(FilterTripsActivity.this,CatalogActivity.class);
                startActivity(intentCatalog);
                    return true;
            case android.R.id.home:
                // TODO Add checking unsaved edits if (!mEventHasChanged) {
                    finish();
                    return true;
                }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayPreferences(String cost, long dateFromTimestamp, long dateToTimestamp, Set<String> countries){
        mCostView.setText(cost);
        mDateFromTextView.setText(DateHelp.timestampToDate(dateFromTimestamp));
        mDateToTextView.setText(DateHelp.timestampToDate(dateToTimestamp));
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
        if(sports.contains("0")) {
            interestedWindsurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[0] = true;
        } else {
            interestedWindsurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[0] = false;
        }
        if(sports.contains("1")) {
            interestedKitesurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[1] = true;
        } else {
            interestedKitesurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[1] = false;
        }
        if(sports.contains("2")) {
            interestedSurfingBackgroundView.setColorFilter(interestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[2] = true;
        } else {
            interestedSurfingBackgroundView.setColorFilter(noInterestedColorCode, PorterDuff.Mode.MULTIPLY);
            checkedItems[2] = false;
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
        mBuilder.setTitle("Wybierz interesujące Cie sporty HC");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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


}