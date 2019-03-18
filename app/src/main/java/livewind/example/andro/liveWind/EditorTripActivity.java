package livewind.example.andro.liveWind;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import livewind.example.andro.liveWind.user.UserActivity;
import livewind.example.andro.liveWind.user.Windsurfer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import livewind.example.andro.liveWind.data.EventContract;

/**
 * Allows user to create a new tripEvent or edit an existing one.
 */
public class EditorTripActivity extends AppCompatActivity {
    /**
     * Trip Type
     *  0 = ORGANIZED TRIP
     *  1 = PRIVATE TRIP
     *  2 = CAMP
     */
    private int mTripType;
    /** Event that will be added or editing */
    private Event mEvent;
    /** All Views */
    private EditText mPlaceEditText;
    private TextView mDateTextView;
    private EditText mStartPlaceEditText;
    private TextView mStartDateTextView;
    private EditText mCommentEditText;
    private Spinner mTransportSpinner;
    private Spinner mCurrencySpinner;
    private EditText mCostEditText;
    private EditText mCostDiscountEditText;
    private RelativeLayout mDiscountRelativeView;
    private EditText mCostAboutEditText;
    private EditText mContactPhoneEditText;
    private EditText mContactEmailEditText;
    private EditText mContactWebEditText;
    private Spinner mCountrySpinner;
    private Spinner mStartCountrySpinner;
    private Spinner mWindsurfingSpinner;
    private Spinner mKitesurfingSpinner;
    private Spinner mSurfingSpinner;
    private Spinner mDisplayAsSpinner;
    private TextView mLabelTypeTextView;
    private TextView mLabelAvailableSportsTextView;
    /** Windsurfer who is creating this event */
    private Windsurfer mWindsurfer = new Windsurfer();
    /** Variables for spinners */
    private int mTransport = EventContract.EventEntry.TRANSPORT_CAR;
    private int mCharacter = EventContract.EventEntry.CONDITIONS_ONSHORE;
    private int mCurrency = EventContract.EventEntry.CURRENCY_ZL;
    private int mCountry = EventContract.EventEntry.COUNTRY_POLAND;
    private int mStartCountry = EventContract.EventEntry.COUNTRY_POLAND;
    private int mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
    private int mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
    private int mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
    private int mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_INFO;
    /**
     * Boolean flag that keeps track of whether the event has been edited (true) or not (false)
     */
    private boolean mEventHasChanged = false;
    private ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();
    /**
     * FIREBASE DATABASE
     */
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsDatabaseReference;
    //For adding and removing points
    private FirebaseHelp firebaseHelpForPointsAdd = new FirebaseHelp();

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mEventHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mEventHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_editor);
        Intent intent = getIntent();
        // Find all relevant views that we will need to read user input from
        mPlaceEditText = (EditText) findViewById(R.id.edit_event_place);
        mDateTextView = (TextView) findViewById(R.id.edit_event_date);
        mStartPlaceEditText = (EditText) findViewById(R.id.edit_event_start_place);
        mStartDateTextView = (TextView) findViewById(R.id.edit_event_start_date);
        mTransportSpinner = (Spinner) findViewById(R.id.spinner_transport);
        mCurrencySpinner = (Spinner) findViewById(R.id.spinner_currency);
        mCommentEditText = (EditText) findViewById(R.id.edit_event_comment);
        mCostEditText = (EditText) findViewById(R.id.edit_trip_event_cost);
        mCostDiscountEditText = findViewById(R.id.edit_trip_event_discount);
        mDiscountRelativeView = findViewById(R.id.edit_trip_event_discount_rl);
        mCostAboutEditText = (EditText) findViewById(R.id.edit_trip_event_cost_about);
        mContactPhoneEditText = (EditText) findViewById(R.id.edit_trip_event_contact_phone);
        mContactEmailEditText = (EditText) findViewById(R.id.edit_trip_event_contact_email);
        mContactWebEditText = (EditText) findViewById(R.id.edit_trip_event_contact_web);
        mStartCountrySpinner = (Spinner) findViewById(R.id.spinner_start_country);
        mCountrySpinner = (Spinner) findViewById(R.id.spinner_country);
        mWindsurfingSpinner = findViewById(R.id.spinner_windsurfing_available);
        mKitesurfingSpinner = findViewById(R.id.spinner_kitesurfing_available);
        mSurfingSpinner = findViewById(R.id.spinner_surfing_available);
        mDisplayAsSpinner = findViewById(R.id.spinner_display_as);
        mLabelTypeTextView = findViewById(R.id.editor_trip_label_display_as);
        mLabelAvailableSportsTextView = findViewById(R.id.trip_editor_label_available_sports_text_view);
        /**
         * Help Views
         */
        ImageView mCostAboutHelpImageView = (ImageView) findViewById(R.id.edit_event_trip_cost_about_help);
        ImageView mContactHelpImageView = (ImageView) findViewById(R.id.edit_event_trip_contact_help);
        ImageView mDiscountHelpImageView = findViewById(R.id.edit_event_trip_discount_about_help);
        ImageView mSportsAvailableHelpImageView = findViewById(R.id.edit_event_trip_sports_help);
        mCostAboutHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpCostAboutDialog();
            }
        });

        mContactHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpContactDialog();
            }
        });

        mSportsAvailableHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpSportsDialog();
            }
        });

        mDiscountHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpDiscountDialog();
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mPlaceEditText.setOnTouchListener(mTouchListener);
        mDateTextView.setOnTouchListener(mTouchListener);
        mStartPlaceEditText.setOnTouchListener(mTouchListener);
        mStartDateTextView.setOnTouchListener(mTouchListener);
        mTransportSpinner.setOnTouchListener(mTouchListener);
        mCurrencySpinner.setOnTouchListener(mTouchListener);
        mCommentEditText.setOnTouchListener(mTouchListener);
        mCostEditText.setOnTouchListener(mTouchListener);
        mCostAboutEditText.setOnTouchListener(mTouchListener);
        mContactPhoneEditText.setOnTouchListener(mTouchListener);
        mContactEmailEditText.setOnTouchListener(mTouchListener);
        mContactWebEditText.setOnTouchListener(mTouchListener);
        mStartCountrySpinner.setOnTouchListener(mTouchListener);
        mCountrySpinner.setOnTouchListener(mTouchListener);
        mWindsurfingSpinner.setOnTouchListener(mTouchListener);
        mKitesurfingSpinner.setOnTouchListener(mTouchListener);
        mSurfingSpinner.setOnTouchListener(mTouchListener);
        mSportsAvailableHelpImageView.setOnTouchListener(mTouchListener);
        mDiscountHelpImageView.setOnTouchListener(mTouchListener);
        mDisplayAsSpinner.setOnTouchListener(mTouchListener);

        setupTransportSpinner();
        setupCurrencySpinner();
        setupStartCountrySpinner();
        setupCountrySpinner();
        setupDisplayAsSpinner();

        //FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child("events");

        mWindsurfer = mExtraInfoHelp.getWindsurferFromIntent(intent,getApplicationContext());

        //Check that is new event or not.
        if (intent.getStringExtra(getString(R.string.EXTRA_EVENT_PLACE)) == null) {
            /**
             *  It is new event -> Setup Title according to mTripType
             */
            mTripType = intent.getIntExtra(getString(R.string.EXTRA_TRIP_TYPE),-1);
            if(mTripType==EditorChoose.TRIP_TYPE_ORGANIZED){
                setTitle(getString(R.string.editor_activity_title_new_trip_organized));
            } else if (mTripType==EditorChoose.TRIP_TYPE_PRIVATE){
                setTitle(getString(R.string.editor_activity_title_new_trip_private));

            } else if (mTripType==EditorChoose.TRIP_TYPE_CAMP){
                setTitle(getString(R.string.editor_activity_title_new_trip_camp));
            } else {
                setTitle(getString(R.string.editor_activity_title_new_trip_organized));
            }
            // Setup some Spinners according to mTripType
            setupWindsurfingSpinner();
            setupKitesurfingSpinner();
            setupSurfingSpinner();

            //Read user contact from settings
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String contactPhoneBySettings = sharedPref.getString(getString(R.string.settings_user_phone_key), "");
            String contactEmailAddressBySettings = sharedPref.getString(getString(R.string.settings_user_email_key), "");
            String contactWebAddressBySettings = sharedPref.getString(getString(R.string.settings_user_web_key), "");
            mContactPhoneEditText.setText(contactPhoneBySettings);
            mContactEmailEditText.setText(contactEmailAddressBySettings);
            mContactWebEditText.setText(contactWebAddressBySettings);

            //Make some changes according to mTripType
            switch (mTripType) {
                case EditorChoose.TRIP_TYPE_ORGANIZED:
                    itIsOrganizedTrip();
                    break;
                case EditorChoose.TRIP_TYPE_PRIVATE:
                    itIsPrivateTrip();
                    break;
                case EditorChoose.TRIP_TYPE_CAMP:
                    itIsCampTrip();
                    break;
                default:
                    break;
            }
        } else {
            /**
             * It is old event to edit or to copy so set title
             */
            if(mEvent.getDate().isEmpty() && mEvent.getStartDate().isEmpty()){
                //It is copy of event
                setTitle(getString(R.string.editor_activity_title_copy_trip));
            } else {
                //It is event to edit
                setTitle(getString(R.string.editor_activity_title_edit_trip));
            }
            mEvent = new Event();
            mExtraInfoHelp.getInfoFromIntent(intent,mEvent,getApplicationContext());
            loadExistingEvent(mEvent);
        }


        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditorTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month=month+1;// datapicker dialog gives month from 0
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
                        mDateTextView.setText(dayString + "." + monthString + "." + Integer.toString(year));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });


        TextView contactWithDeveloperTextView = findViewById(R.id.trip_editor_info_for_owners_contact_text_view);

        contactWithDeveloperTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                contactWithDevelopers(EditorTripActivity.this);
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupTransportSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter transportSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_transport_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        transportSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTransportSpinner.setAdapter(transportSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTransportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.transport_car))) {
                        mTransport = EventContract.EventEntry.TRANSPORT_CAR;
                    } else if (selection.equals(getString(R.string.transport_plane))) {
                        mTransport = EventContract.EventEntry.TRANSPORT_PLANE;
                    } else {
                        //TODO others
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTransport = EventContract.EventEntry.TRANSPORT_CAR;
            }
        });
    }
    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupCurrencySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter currencySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_currency_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCurrencySpinner.setAdapter(currencySpinnerAdapter);

        // Set the integer mSelected to the constant values
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

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrency = EventContract.EventEntry.CURRENCY_ZL;
            }
        });
    }

    private void setupStartCountrySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter startCountrySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_countries_options_editor, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        startCountrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStartCountrySpinner.setAdapter(startCountrySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mStartCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals(getString(R.string.country_number_1))) {
                            mStartCountry = EventContract.EventEntry.COUNTRY_POLAND;
                        } else if (selection.equals(getString(R.string.country_number_2))) {
                            mStartCountry = EventContract.EventEntry.COUNTRY_GREECE;
                        } else if (selection.equals(getString(R.string.country_number_3))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_SPAIN;
                        } else if (selection.equals(getString(R.string.country_number_4))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_CROATIA;
                        } else if (selection.equals(getString(R.string.country_number_5))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_PORTUGAL;
                        } else if (selection.equals(getString(R.string.country_number_6))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_GERMANY;
                        } else if (selection.equals(getString(R.string.country_number_7))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_FRANCE;
                        } else if (selection.equals(getString(R.string.country_number_8))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_SOUTH_AFRICA;
                        } else if (selection.equals(getString(R.string.country_number_9))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_MOROCCO;
                        } else if (selection.equals(getString(R.string.country_number_10))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_ITALY;
                        } else if (selection.equals(getString(R.string.country_number_11))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_EGYPT;
                        } else if (selection.equals(getString(R.string.country_number_12))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_UK;
                        } else if (selection.equals(getString(R.string.country_number_13))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_TURKEY;
                        } else if (selection.equals(getString(R.string.country_number_14))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_AUSTRIA;
                        } else if (selection.equals(getString(R.string.country_number_15))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_DENMARK;
                        } else if (selection.equals(getString(R.string.country_number_16))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_BRAZIL;
                        } else if (selection.equals(getString(R.string.country_number_17))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_USA;
                        } else if (selection.equals(getString(R.string.country_number_18))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_VIETNAM;
                        } else if (selection.equals(getString(R.string.country_number_19))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_MALTA;
                        } else if (selection.equals(getString(R.string.country_number_20))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES;
                        } else if (selection.equals(getString(R.string.country_number_minus_2))){
                            mStartCountry = EventContract.EventEntry.COUNTRY_BAD;
                        }
                    }
                }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStartCountry= EventContract.EventEntry.COUNTRY_WORLD;
            }
        });
    }

    private void setupCountrySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter countrySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_countries_options_editor, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCountrySpinner.setAdapter(countrySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.country_number_1))) {
                        mCountry = EventContract.EventEntry.COUNTRY_POLAND;
                    } else if (selection.equals(getString(R.string.country_number_2))) {
                        mCountry = EventContract.EventEntry.COUNTRY_GREECE;
                    } else if (selection.equals(getString(R.string.country_number_3))){
                        mCountry = EventContract.EventEntry.COUNTRY_SPAIN;
                    } else if (selection.equals(getString(R.string.country_number_4))){
                        mCountry = EventContract.EventEntry.COUNTRY_CROATIA;
                    } else if (selection.equals(getString(R.string.country_number_5))){
                        mCountry = EventContract.EventEntry.COUNTRY_PORTUGAL;
                    } else if (selection.equals(getString(R.string.country_number_6))){
                        mCountry = EventContract.EventEntry.COUNTRY_GERMANY;
                    } else if (selection.equals(getString(R.string.country_number_7))){
                        mCountry = EventContract.EventEntry.COUNTRY_FRANCE;
                    } else if (selection.equals(getString(R.string.country_number_8))){
                        mCountry = EventContract.EventEntry.COUNTRY_SOUTH_AFRICA;
                    } else if (selection.equals(getString(R.string.country_number_9))){
                        mCountry = EventContract.EventEntry.COUNTRY_MOROCCO;
                    } else if (selection.equals(getString(R.string.country_number_10))){
                        mCountry = EventContract.EventEntry.COUNTRY_ITALY;
                    } else if (selection.equals(getString(R.string.country_number_11))){
                        mCountry = EventContract.EventEntry.COUNTRY_EGYPT;
                    } else if (selection.equals(getString(R.string.country_number_12))){
                        mCountry = EventContract.EventEntry.COUNTRY_UK;
                    } else if (selection.equals(getString(R.string.country_number_13))){
                        mCountry = EventContract.EventEntry.COUNTRY_TURKEY;
                    } else if (selection.equals(getString(R.string.country_number_14))){
                        mCountry = EventContract.EventEntry.COUNTRY_AUSTRIA;
                    } else if (selection.equals(getString(R.string.country_number_15))){
                        mCountry = EventContract.EventEntry.COUNTRY_DENMARK;
                    } else if (selection.equals(getString(R.string.country_number_16))){
                        mCountry = EventContract.EventEntry.COUNTRY_BRAZIL;
                    } else if (selection.equals(getString(R.string.country_number_17))){
                        mCountry = EventContract.EventEntry.COUNTRY_USA;
                    } else if (selection.equals(getString(R.string.country_number_18))){
                        mCountry = EventContract.EventEntry.COUNTRY_VIETNAM;
                    } else if (selection.equals(getString(R.string.country_number_19))){
                        mCountry = EventContract.EventEntry.COUNTRY_MALTA;
                    } else if (selection.equals(getString(R.string.country_number_20))){
                        mCountry = EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES;
                    } else if (selection.equals(getString(R.string.country_number_minus_2))){
                        mCountry = EventContract.EventEntry.COUNTRY_BAD;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountry = EventContract.EventEntry.COUNTRY_WORLD;
            }
        });
    }

    private void setupWindsurfingSpinner() {
        ArrayAdapter windsurfingSpinnerAdapter;
        if(mTripType==EditorChoose.TRIP_TYPE_PRIVATE) {
            windsurfingSpinnerAdapter= ArrayAdapter.createFromResource(this,
                    R.array.array_available_private_windsurfing, android.R.layout.simple_spinner_item);
        } else {
            windsurfingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.array_available_windsurfing, android.R.layout.simple_spinner_item);
        }
        // Specify dropdown layout style - simple list view with 1 item per line

            windsurfingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mWindsurfingSpinner.setAdapter(windsurfingSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mWindsurfingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.avaiable_yes)) || selection.equals(getString(R.string.available_trip_private_without_equipment))) {
                        mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_YES;
                    } else if (selection.equals(getString(R.string.avaiable_no))|| selection.equals(getString(R.string.available_trip_private_no))) {
                        mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO;
                    }else if (selection.equals(getString(R.string.avaiable_course))|| selection.equals(getString(R.string.available_trip_private_with_equipment))) {
                        mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_COURSE;
                    }else if (selection.equals(getString(R.string.avaiable_course_instructor))) {
                        mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE;
                    } else {
                        //Default
                        mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWindsurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
            }
        });
    }

    private void setupKitesurfingSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter kitesurfingSpinnerAdapter;
        if(mTripType==EditorChoose.TRIP_TYPE_PRIVATE) {
            kitesurfingSpinnerAdapter= ArrayAdapter.createFromResource(this,
                    R.array.array_available_private_kitesurfing, android.R.layout.simple_spinner_item);
        } else {
            kitesurfingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.array_available_kitesurfing, android.R.layout.simple_spinner_item);
        }

        // Specify dropdown layout style - simple list view with 1 item per line
        kitesurfingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mKitesurfingSpinner.setAdapter(kitesurfingSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mKitesurfingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.avaiable_yes))|| selection.equals(getString(R.string.available_trip_private_without_equipment))) {
                        mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_YES;
                    } else if (selection.equals(getString(R.string.avaiable_no))|| selection.equals(getString(R.string.available_trip_private_no))) {
                        mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO;
                    }else if (selection.equals(getString(R.string.avaiable_course))|| selection.equals(getString(R.string.available_trip_private_with_equipment))) {
                        mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_COURSE;
                    }else if (selection.equals(getString(R.string.avaiable_course_instructor))) {
                        mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE;
                    } else {
                        //Default
                        mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mKitesurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
            }
        });
    }

    private void setupSurfingSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter surfingSpinnerAdapter;
        if(mTripType==EditorChoose.TRIP_TYPE_PRIVATE) {
            surfingSpinnerAdapter= ArrayAdapter.createFromResource(this,
                    R.array.array_available_private_surfing, android.R.layout.simple_spinner_item);
        } else {
            surfingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.array_available_surfing, android.R.layout.simple_spinner_item);
        }

        // Specify dropdown layout style - simple list view with 1 item per line
        surfingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSurfingSpinner.setAdapter(surfingSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSurfingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.avaiable_yes))|| selection.equals(getString(R.string.available_trip_private_without_equipment))) {
                        mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_YES;
                    } else if (selection.equals(getString(R.string.avaiable_no))|| selection.equals(getString(R.string.available_trip_private_no))) {
                        mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO;
                    }else if (selection.equals(getString(R.string.avaiable_course))|| selection.equals(getString(R.string.available_trip_private_with_equipment))) {
                        mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_COURSE;
                    }else if (selection.equals(getString(R.string.avaiable_course_instructor))) {
                        mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE;
                    } else {
                        //Default
                        mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSurfingAvailable = EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO;
            }
        });
    }

    private void setupDisplayAsSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter displayAsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_trips_display_as_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        displayAsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mDisplayAsSpinner.setAdapter(displayAsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mDisplayAsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.event_display_as_select))) {
                        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_INFO;
                    } else if (selection.equals(getString(R.string.event_trip_activity_title_camp))) {
                        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_CAMP;
                    }else if (selection.equals(getString(R.string.event_trip_activity_title_training))) {
                        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_TRAINING;
                    } else {
                        //Default
                        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_INFO;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_INFO;
            }
        });
    }
    /**
     * Get user input from editor and save event into database.
     */
    private boolean saveEvent() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String placeString = mPlaceEditText.getText().toString().trim();
        String startPlaceString = mStartPlaceEditText.getText().toString().trim();
        String dateString = mDateTextView.getText().toString().trim();
        String startDateString = mStartDateTextView.getText().toString().trim();
        String commentString = mCommentEditText.getText().toString().trim();
        String costString = mCostEditText.getText().toString().trim();
        String costDiscountString = mCostDiscountEditText.getText().toString().trim();
        String costAboutString = mCostAboutEditText.getText().toString().trim();
        String contactPhoneString = mContactPhoneEditText.getText().toString().trim();
        String contactEmailString = mContactEmailEditText.getText().toString().trim();
        String contactWebString = mContactWebEditText.getText().toString().trim();
        //To check that departure date is earlier than return date
        boolean correctData = true;

        // String to int
        int cost = 0;
        int costDiscount = 0;
        if (!TextUtils.isEmpty(costString)) {
            cost = Integer.parseInt(costString);
        }
        if (!TextUtils.isEmpty(costDiscountString)){
            costDiscount = Integer.parseInt(costDiscountString);
        }
        //Check that all required information has been provided
        if (startPlaceString.equals("")) {
            Toast.makeText(this, R.string.event_start_place_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if(startPlaceString.length()<3){
            Toast.makeText(this,R.string.event_place_3letters_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (startDateString.equals("")) {
            Toast.makeText(this, R.string.event_start_date_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if(startDateString.length()!=18){
            Toast.makeText(this, R.string.event_start_date_bad_format_tm, Toast.LENGTH_SHORT).show();
            return false;
        }else if (placeString.equals("")) {
            Toast.makeText(this, R.string.event_destination_place_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        }else if(placeString.length()<3){
            Toast.makeText(this, R.string.event_destination_place_3letters_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (dateString.equals("")) {
            Toast.makeText(this, R.string.event_return_date_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        }else if (mCountry==0|| mStartCountry==EventContract.EventEntry.COUNTRY_BAD ) {
            Toast.makeText(this, R.string.event_country_info_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (mStartCountry==0 || mStartCountry==EventContract.EventEntry.COUNTRY_BAD) {
            Toast.makeText(this, R.string.event_trip_start_country_info_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (contactPhoneString.isEmpty() && contactWebString.isEmpty() && contactEmailString.isEmpty()){
            Toast.makeText(this, R.string.event_trip_one_contact_info_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (costDiscount>=cost) {
            Toast.makeText(this, R.string.event_trip_cost_discount_tm, Toast.LENGTH_SHORT).show();
            return false;
        }else if (mDisplayAs==EventContract.EventEntry.DISPLAY_AS_NO_INFO) {
            Toast.makeText(this, R.string.event_trip_display_as_tm, Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            correctData = checkData(startDateString, dateString);
            if (correctData) {
                Toast.makeText(this, R.string.event_bad_date_tm, Toast.LENGTH_SHORT).show();
                return false;
            }

            //New event or edit of existing event
            if (mEvent == null || (mEvent.getStartDate().isEmpty() && mEvent.getDate().isEmpty())) {
                //This is new event or copy of other event
                //Check limit of active trips
                if (mWindsurfer.checkTripsLimitAdvanced() == mWindsurfer.LIMIT_OK) {
                    Contact loadContact = new Contact(contactPhoneString, contactEmailString, contactWebString);
                    String id = mEventsDatabaseReference.push().getKey();
                    //Event newEventData = new Event(id, mWindsurfer.getUsername(), startPlaceString, mStartCountry, placeString, mCountry, startDateString, dateString, commentString, mTransport, mCharacter, cost, mCurrency, costAboutString, loadContact, mWindsurfingAvailable, mKitesurfingAvailable,mSurfingAvailable);
                    Event newEventData = new Event(id, mWindsurfer, startPlaceString, mStartCountry, placeString, mCountry, startDateString, dateString, commentString, mTransport, mCharacter, cost, costDiscount, mCurrency, costAboutString, loadContact, mWindsurfingAvailable, mKitesurfingAvailable,mSurfingAvailable,mDisplayAs,EditorTripActivity.this);
                    mEventsDatabaseReference.child(id).setValue(newEventData);
                    Map<String, Object> value = new HashMap<>();
                    long myTimeStamp = 1000000000;
                    myTimeStamp = myTimeStamp * 1000000;
                    value.put("timestamp", myTimeStamp);
                    //Add 5 points for creating the event
                    firebaseHelpForPointsAdd.addPoints(mWindsurfer.getUid(), EventContract.EventEntry.IT_IS_TRIP);
                    mEventsDatabaseReference.child(id).updateChildren(value);
                    return true;
                } else if (mWindsurfer.checkTripsLimitAdvanced()==mWindsurfer.LIMIT_NO_CONNECTION){
                    Toast.makeText(this, R.string.empty_view_no_connection_title_text, Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    Snackbar snackbarTripsLimit = Snackbar.make(findViewById(R.id.myEditorTripsCoordinatorLayout), R.string.toast_trip_limit_reached, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbarTripsLimit.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setMaxLines(3);
                    snackbarTripsLimit.setAction(getString(R.string.user_activity_increase_limit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                            startActivity(intent);
                            finish();
                        }
                    });

                    snackbarTripsLimit.show();
                    return false;
                }
            } else {

                //Edit existing event
                mEvent.setPlace(placeString);
                mEvent.setCountry(mCountry);
                mEvent.setStartPlace(startPlaceString);
                mEvent.setStartCountry(mStartCountry);
                mEvent.setDate(dateString);
                mEvent.setStartDate(startDateString);
                mEvent.setComment(commentString);
                mEvent.setTransport(mTransport);
                mEvent.setCharacter(mCharacter);
                mEvent.setWindsurfingAvailable(mWindsurfingAvailable);
                mEvent.setKitesurfingAvailable(mKitesurfingAvailable);
                mEvent.setSurfingAvailable(mSurfingAvailable);
                mEvent.setCost(cost);
                mEvent.setCostDiscount(costDiscount);
                mEvent.setCurrency(mCurrency);
                mEvent.setCostAbout(costAboutString);
                mEvent.setContact(contactPhoneString,contactEmailString,contactWebString);
                mEvent.setDisplayAs(mDisplayAs);

                Map<String, Object> eventValues = mEvent.tripToMap();
                mEventsDatabaseReference.child(mEvent.getId()).updateChildren(eventValues);
                return true;
            }
        }
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mEvent == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Windsurfer clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //if user provide all required information save event otherwise will be displayed toast message
                if (saveEvent()) {
                    Toast.makeText(this, getString(R.string.toast_trip_created),Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else {
                    return false;
                }
            case R.id.action_help:
                showHelpDialog();
                return false;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mEventHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorTripActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Windsurfer clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorTripActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the event hasn't changed, continue with handling back button press
        if (!mEventHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Windsurfer clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the event.
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
     * Prompt the user to confirm that they want to delete this event.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Delete" button, so delete the event and remove 5 points.
                firebaseHelpForPointsAdd.removePoints(mEvent.getmUserUId(), EventContract.EventEntry.IT_IS_TRIP);
                deleteEvent();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showHelpDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_add_trip, null))
                // Add action buttons
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
    }

    public void showHelpCostAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_add_trip_cost_about, null))
                // Add action buttons
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
    }

    public void showHelpContactDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_add_trip_contact, null))
                // Add action buttons
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
    }
    public void showHelpSportsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_add_trip_sports, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }

    public void showHelpDiscountDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_trip_discount, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }
    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteEvent() {
        // Only perform the delete if this is an existing event.
        if (mEvent.getId() != null) {

            mEventsDatabaseReference.child(mEvent.getId()).removeValue();

            // Show a toast message depending on whether or not the delete was successful.
            if (mEventsDatabaseReference.child(mEvent.getId()).getKey().equals("")) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.toast_editor_delete_trip_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_trip_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    /**
     * Loading existing event
     */

    public void loadExistingEvent(Event event) {
        mPlaceEditText.setText(event.getPlace());
        //Check that it is a camp
        if(event.getStartPlace().equals(Integer.toString(EventContract.EventEntry.IT_IS_CAMP))) {
            itIsCampTrip();
        } else {
            switch (mEvent.getCharacter())
            {
            case EventContract.EventEntry.CHARACTER_ORGANIZED:
                itIsOrganizedTrip();
                break;
            case EventContract.EventEntry.CHARACTER_PRIVATE:
                //Change this variable to correctly setup sports spinners
                mTripType=EditorChoose.TRIP_TYPE_PRIVATE;
                itIsPrivateTrip();
                break;
            default:
                itIsOrganizedTrip();
                break;
            }
        }
        /** Setup spinners according to */
        setupWindsurfingSpinner();
        setupKitesurfingSpinner();
        setupSurfingSpinner();
        /** Load data from existing event */
        mStartPlaceEditText.setText(event.getStartPlace());
        mDateTextView.setText(event.getDate());
        mStartDateTextView.setText(event.getStartDate());
        mCommentEditText.setText(event.getComment());
        mCostEditText.setText(Integer.toString(event.getCost()));
        mCostDiscountEditText.setText(Integer.toString(event.getCostDiscount()));
        mCostAboutEditText.setText(event.getCostAbout());
        mContactPhoneEditText.setText(event.getContact().getPhoneNumber());
        mContactEmailEditText.setText(event.getContact().getEmailAddress());
        mContactWebEditText.setText(event.getContact().getWebAddress());

        //Load selected cases tp spinners
        switch (event.getTransport()) {
            case EventContract.EventEntry.TRANSPORT_CAR:
                mTransportSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.TRANSPORT_PLANE:
                mTransportSpinner.setSelection(1);
                break;
            default:
                mTransportSpinner.setSelection(0);
                break;
        }
        switch (event.getCurrency()) {
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
        event.loadCountrySpinner(mCountrySpinner);
        event.loadStartCountrySpinner(mStartCountrySpinner);

        switch (event.getWindsurfingAvailable()) {
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mWindsurfingSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                mWindsurfingSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mWindsurfingSpinner.setSelection(2);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                mWindsurfingSpinner.setSelection(3);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                mWindsurfingSpinner.setSelection(4);
                break;
            default:
                mWindsurfingSpinner.setSelection(0);
                break;
        }
        switch (event.getKitesurfingAvailable()) {
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mKitesurfingSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:

                mKitesurfingSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mKitesurfingSpinner.setSelection(2);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                mKitesurfingSpinner.setSelection(3);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                mKitesurfingSpinner.setSelection(4);
                break;
            default:
                mKitesurfingSpinner.setSelection(0);
                break;
        }
        switch (event.getSurfingAvailable()) {
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mSurfingSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                mSurfingSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mSurfingSpinner.setSelection(2);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                mSurfingSpinner.setSelection(3);
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                mSurfingSpinner.setSelection(4);
                break;
            default:
                mSurfingSpinner.setSelection(0);
                break;
        }

        switch (event.getDisplayAs()) {
            case EventContract.EventEntry.DISPLAY_AS_NO_INFO:
                mDisplayAsSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.DISPLAY_AS_CAMP:
                mDisplayAsSpinner.setSelection(EventContract.EventEntry.DISPLAY_AS_CAMP);
                break;
            case EventContract.EventEntry.DISPLAY_AS_TRAINING:
                mDisplayAsSpinner.setSelection(EventContract.EventEntry.DISPLAY_AS_TRAINING);
                break;
            case EventContract.EventEntry.DISPLAY_AS_NO_AVAILABLE:
                mDisplayAs=EventContract.EventEntry.DISPLAY_AS_NO_AVAILABLE;
                break;
            default:
                mDisplayAsSpinner.setSelection(0);
                break;
        }

        mCharacter=event.getCharacter();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Do something with the user chosen time
            //Get reference of host activity (XML Layout File) TextView widget
            TextView tv = (TextView) getActivity().findViewById(R.id.edit_event_start_date);
            String getTV = tv.getText().toString().trim();
            //Display the user changed time on TextView
            String hourString = "";
            String minuteString = "";
            if (hourOfDay < 10) {
                hourString = "0" + Integer.toString(hourOfDay);
            } else {
                hourString = Integer.toString(hourOfDay);
            }
            if (minute < 10) {
                minuteString = "0" + Integer.toString(minute);
            } else {
                minuteString = Integer.toString(minute);
            }
            tv.setText(hourString + ":" + minuteString + " - " + getTV);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    /**
     * DATE PICKER
     **/

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView tv = (TextView) getActivity().findViewById(R.id.edit_event_start_date);
            //Display the user changed time on TextView
            month=month+1;// datapicker dialog gives month from 0
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
            tv.setText(dayString + "." + monthString + "." + Integer.toString(year));
        }
    }

    public void showDateAndTimePickerDialog(View v) {
        DialogFragment newFragment2 = new TimePickerFragment();
        newFragment2.show(getSupportFragmentManager(), "timePicker");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    /**
     * Functions to check that date is correct
     */

    public Calendar StringToGC(String goodFormatDate) {
        String day = goodFormatDate.substring(0, 2);
        String month = goodFormatDate.substring(3, 5);
        String year = goodFormatDate.substring(6, 10);
        int dayS = Integer.parseInt(day);
        int monthS = Integer.parseInt(month);
        int yearS = Integer.parseInt(year);
        GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS);
        Log.i("COMPATE DATE", "GC start = " + dayS + "." + monthS + "." + yearS);
        Calendar dataC = dataGC;
        return dataGC;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public boolean checkData(String departureBadFormatData, String returnData) {
        if (departureBadFormatData.equals("")) {
            return false;
        } else {
            String departureData = departureBadFormatData.substring(8);
            Calendar departureDataC = StringToGC(departureData);
            Log.i("COMPARE DATES:",departureDataC.toString());
            Calendar returnDataC = StringToGC(returnData);
            Log.i("COMPARE DATES:",returnDataC.toString());
            if (returnDataC.compareTo(departureDataC) < 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static void contactWithDevelopers(Context context) {
        try {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.contact_our_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "[SurfAdvisor] Increase limit of trips");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(livewind.example.andro.liveWind.R.string.toast_contact_error), Toast.LENGTH_LONG).show();
        }
    }

    private void itIsOrganizedTrip(){
        mLabelTypeTextView.setVisibility(View.GONE);
        mTransportSpinner.setSelection(EventContract.EventEntry.TRANSPORT_PLANE);
        mCharacter = EventContract.EventEntry.CHARACTER_ORGANIZED;
        mDisplayAsSpinner.setVisibility(View.GONE);
        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_AVAILABLE;
    }

    private void itIsPrivateTrip(){
        mDiscountRelativeView.setVisibility(View.GONE);
        mLabelTypeTextView.setVisibility(View.GONE);
        mTransportSpinner.setSelection(EventContract.EventEntry.TRANSPORT_CAR);
        mCharacter=EventContract.EventEntry.CHARACTER_PRIVATE;
        mDisplayAsSpinner.setVisibility(View.GONE);
        mDisplayAs = EventContract.EventEntry.DISPLAY_AS_NO_AVAILABLE;
        mLabelAvailableSportsTextView.setText(R.string.available_trip_private);
    }

    private void itIsCampTrip(){
        //Change 'from' and 'to'
        TextView mLabelToTextView = findViewById(R.id.editor_trip_label_to);
        mLabelToTextView.setText(R.string.event_trip_camp_to);
        TextView mLabelFromTextView = findViewById(R.id.editor_trip_label_from);
        mLabelFromTextView.setText(R.string.event_trip_camp_from);
        //Hide transport label:
        TextView mTransportLabelTextView = findViewById(R.id.editor_trip_label_transport);
        mTransportLabelTextView.setVisibility(View.GONE);
        mCharacter=EventContract.EventEntry.CHARACTER_ORGANIZED;
        mStartPlaceEditText.setVisibility(View.GONE);
        mStartDateTextView.setHint(R.string.hint_event_trip_camp_start_date);
        mStartCountrySpinner.setVisibility(View.GONE);
        mPlaceEditText.setHint(R.string.hint_event_trip_camp_destination_place);
        mTransportSpinner.setVisibility(View.GONE);
        //Set this trip to camp
        mStartPlaceEditText.setText(Integer.toString(EventContract.EventEntry.IT_IS_CAMP));
        mStartCountry = EventContract.EventEntry.IT_IS_CAMP;
    }

}