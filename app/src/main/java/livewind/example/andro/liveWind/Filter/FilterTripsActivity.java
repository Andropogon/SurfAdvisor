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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

/** An implementation of the View */

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
    private Set<String> mSports;

    boolean[] checkedItems = new boolean[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        // Creates presenter

        initViews();

        mPresenter = new FilterTripsPresenter(this);
        mPresenter.loadPreferences();

    }

    private void initViews() {
        mCostView = (EditText) findViewById(R.id.filter_price_value_edit_text);
        mDateFromTextView = findViewById(R.id.filter_date_from_text_view);
        mDateFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                showDatePickerDialog(mDateFromTextView,calendar);
            }
        });
        mDateToTextView = findViewById(R.id.filter_date_to_text_view);
        mDateToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                //TODO Change calendar date to "from date - mDateFrom"
                showDatePickerDialog(mDateToTextView,calendar);
            }
        });
        mSportsTextView = findViewById(R.id.filter_sports_text_view);
        mSportsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultiSelectSportsList();
                //TODO SAVE AND DISPLAY RESULTS
            }
        });
        mWindsurfingImageView = findViewById(R.id.windsurfing_image_view);
        mKitesurfingImageView = findViewById(R.id.kitesurfing_image_view);
        mSurfingImageView = findViewById(R.id.surfing_image_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_save:
                mPresenter.savePreferences(mCostView.getText().toString(),dateToTimestamp(mDateFromTextView.getText().toString()),dateToTimestamp(mDateToTextView.getText().toString()),mSports);
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
    public void displayPreferences(String cost, long dateFromTimestamp, long dateToTimestamp, Set<String> sports){
        //mCostView.setText(cost);
        mDateFromTextView.setText(timestampToDate(dateFromTimestamp));
        mDateToTextView.setText(timestampToDate(dateToTimestamp));
        mCostView.setText(String.valueOf(sports.size()));
        mSports = sports;
    }

    /**
     * DATE PICKER
     **/
    public void showDatePickerDialog(final TextView dateTextView, final Calendar calendar) {
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(FilterTripsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;// datapicker dialog gives month from 0
                        String monthString = "";
                        String dayString = "";
                        if (month < 10) {
                            monthString = "0" + Integer.toString(month);
                        } else {
                            monthString = Integer.toString(month);
                        }
                        if (day < 10) {
                            dayString = "0" + Integer.toString(day);
                        } else {
                            dayString = Integer.toString(day);
                        }
                        dateTextView.setText(dayString + "." + monthString + "." + Integer.toString(year));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });
    }

    private long dateToTimestamp(String date){
        String day = date.substring(0, 2);
        String month = date.substring(3, 5);
        String year = date.substring(6, 10);
        int dayS = Integer.parseInt(day);
        int monthS = Integer.parseInt(month) -1 ; //because months are indexing from 0
        int yearS = Integer.parseInt(year);
        GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS,0,0,0);
        Calendar dataC = dataGC;
        long timestamp = dataC.getTimeInMillis();
        return timestamp;
    }
    private String timestampToDate(long timestamp){
        Date date = new Date();
        date.setTime(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        java.lang.String printableDate = formatter.format(date);
        String dateString =printableDate;
        return dateString;
    }

    private void createMultiSelectSportsList(){
        final String[] listItems;
        listItems = getResources().getStringArray(R.array.array_filter_sports);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FilterTripsActivity.this);
        mBuilder.setTitle("Wybierz interesujące Cie sporty HC");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked) {
                    if (!mSports.contains(position)) {
                        mSports.add(String.valueOf(position));
                    }
                }
                else if(mSports.contains(position)){
                    mSports.remove(String.valueOf(position));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                mCostView.setText(String.valueOf(mSports.size()));
                int backgroundColor = R.color.sport_available_yes;

                int goodColor = ContextCompat.getColor(FilterTripsActivity.this,backgroundColor);
                int instructorColor = ContextCompat.getColor(FilterTripsActivity.this,backgroundColor);

                if(mSports.contains("0")) {
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(FilterTripsActivity.this, backgroundColor);
                    mWindsurfingImageView.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    Drawable mWindsurfingAvailableImageViewBackground = mWindsurfingImageView.getBackground();
                    mWindsurfingImageView.setBackground(mWindsurfingAvailableImageViewBackground);
                } else if(mSports.contains("1")) {
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(FilterTripsActivity.this, backgroundColor);
                    mKitesurfingImageView.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    Drawable mKitesurfingAvailableImageViewBackground = mKitesurfingImageView.getBackground();
                    mKitesurfingImageView.setBackground( mKitesurfingAvailableImageViewBackground);
                } else if(mSports.contains("2")) {
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(FilterTripsActivity.this, backgroundColor);
                    mSurfingImageView.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    Drawable mSurfingAvailableImageViewBackground = mSurfingImageView.getBackground();
                    mSurfingImageView.setBackground(mSurfingAvailableImageViewBackground);
                }
            }
        });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("CLEAR ALL HC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    //mUserItems.clear();
                   // mItemSelected.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}