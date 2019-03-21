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

    boolean[] checkedItems = new boolean[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initViews();
        // Creates presenter
        mPresenter = new FilterTripsPresenter(this);
        mPresenter.loadPreferences();

        initClickListeners();
    }

    private void initViews() {
        mCostView = (EditText) findViewById(R.id.filter_price_value_edit_text);
        mDateFromTextView = findViewById(R.id.filter_date_from_text_view);

        mDateToTextView = findViewById(R.id.filter_date_to_text_view);

        mWindsurfingImageView = findViewById(R.id.windsurfing_image_view);
        mKitesurfingImageView = findViewById(R.id.kitesurfing_image_view);
        mSurfingImageView = findViewById(R.id.surfing_image_view);

        mSportsTextView = findViewById(R.id.filter_sports_text_view);

    }
    private void initClickListeners(){
        mDateFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                showDatePickerDialog(mDateFromTextView,calendar);
            }
        });
        mDateToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open showDatePickerDialog with start date = current
                Calendar calendar = Calendar.getInstance();
                //TODO Change calendar date to "from date - mDateFrom"
                showDatePickerDialog(mDateToTextView,calendar);
            }
        });
        mSportsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mWindsurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mKitesurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultiSelectSportsList(mPresenter.getSports());
            }
        });
        mSurfingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultiSelectSportsList(mPresenter.getSports());
            }
        });
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
                mPresenter.savePreferences(mCostView.getText().toString(),dateToTimestamp(mDateFromTextView.getText().toString()),dateToTimestamp(mDateToTextView.getText().toString()));
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
        mCostView.setText(cost);
        mDateFromTextView.setText(timestampToDate(dateFromTimestamp));
        mDateToTextView.setText(timestampToDate(dateToTimestamp));
        displaySports(sports);
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

    private void createMultiSelectSportsList(final Set<String> sports){
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
                displaySports(sports);
            }

        });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
/**
        mBuilder.setNeutralButton("CLEAR ALL HC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
            }
        });
 */

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void displaySports(Set <String> sports){
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
}