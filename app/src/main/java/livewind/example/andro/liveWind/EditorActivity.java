package livewind.example.andro.liveWind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import livewind.example.andro.liveWind.user.UserActivity;
import livewind.example.andro.liveWind.user.Windsurfer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import livewind.example.andro.liveWind.data.EventContract;

import static livewind.example.andro.liveWind.ExtraInfoHelp.getWindsurferFromIntent;

/**
 * Allows user to create a new event or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    private EditText mPlaceEditText;
    private EditText mWindPowerEditText;
    private EditText mWaveSizeEditText;
    private EditText mCommentEditText;
    private Spinner mTypeSpinner;
    private Spinner mConditionsSpinner;
    private Spinner mCountrySpinner;
    private ImageView mAddPhotoImageView;
    private ImageView mAddPhotoPlusImageView;
    private ImageView mWindPowerHelpImageView;
    private ImageView mTypeHelpImageView;
    private ImageView mWindConditionsHelpImageView;
    private Windsurfer mWindsurfer = new Windsurfer();

    private ProgressBar mProgressBar;
    private TextView mProgressTextView;
    private TextView mProgressPercentageTextView;
    private int mType = EventContract.EventEntry.TYPE_WINDSURFING;
    private int mConditions = EventContract.EventEntry.CONDITIONS_ONSHORE;
    private int mCountry = EventContract.EventEntry.COUNTRY_WORLD;

    /**
     * Boolean flag that keeps track of whether the event has been edited (true) or not (false)
     */
    private boolean mEventHasChanged = false;
    private Event mEvent;
    private ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();
    /**
     * FIREBASE DATABASE
     */
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsDatabaseReference;
    private DatabaseReference mUserDatabaseReference; //To get user data if user open this activity without internet connection
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mEventsStorageReference;
    //For adding and removing points
    private FirebaseHelp firebaseHelpForPointsAdd = new FirebaseHelp();

    /**
     * To add photos of events
     */
    private static final int RC_PHOTO_PICKER = 2;
    private String mEventPhotoUrl = "";

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
        setContentView(livewind.example.andro.liveWind.R.layout.activity_editor);
        Intent intent = getIntent();
        // Find all relevant views that we will need to read user input from
        mPlaceEditText = (EditText) findViewById(livewind.example.andro.liveWind.R.id.edit_event_place);
        mCountrySpinner = (Spinner) findViewById(R.id.spinner_country);
   //     mDateTextView = (TextView) findViewById(R.id.edit_event_date);
        mTypeSpinner = (Spinner) findViewById(livewind.example.andro.liveWind.R.id.spinner_type);
        mWindPowerEditText = (EditText) findViewById(livewind.example.andro.liveWind.R.id.edit_event_wind_power);
        mWaveSizeEditText = (EditText) findViewById(livewind.example.andro.liveWind.R.id.edit_event_wave_size);
        mConditionsSpinner = (Spinner) findViewById(livewind.example.andro.liveWind.R.id.spinner_conditions);
        mCommentEditText = (EditText) findViewById(livewind.example.andro.liveWind.R.id.edit_event_comment);
        mWindPowerHelpImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.edit_event_wind_power_help_ic);
        mTypeHelpImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.edit_event_type_help_ic);
        mWindConditionsHelpImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.edit_event_conditions_help_ic);
        // Add photo imageview
        mAddPhotoImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.add_photo_image_view);
        mAddPhotoPlusImageView = (ImageView) findViewById(R.id.change_user_photo_image_view);
        mProgressTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.progress_text_view);
        mProgressBar = (ProgressBar) findViewById(livewind.example.andro.liveWind.R.id.progressBar1);
        mProgressPercentageTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.progress_percentage_text_view);
        mProgressBar.setVisibility(View.GONE);
        mProgressTextView.setVisibility(View.GONE);
        mProgressPercentageTextView.setVisibility(View.GONE);
        /**
         * Add photo clicklistener
         * ith these four lines of code in the onClick method,
         * a file picker will be opened to help us choose between any locally stored JPEG images that are on the device.
         */
        mAddPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                addPhotoIntent.setType("image/jpeg");
                addPhotoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(addPhotoIntent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mWindPowerHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpWindDialog();
            }
        });
        mTypeHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpTypeDialog();
            }
        });
        mWindConditionsHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpDirectionDialog();
            }
        });
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mPlaceEditText.setOnTouchListener(mTouchListener);
     //   mDateTextView.setOnTouchListener(mTouchListener);
        mWindPowerEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mWaveSizeEditText.setOnTouchListener(mTouchListener);
        mConditionsSpinner.setOnTouchListener(mTouchListener);
        mCountrySpinner.setOnTouchListener(mTouchListener);
        mCommentEditText.setOnTouchListener(mTouchListener);

        //FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child("events");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mEventsStorageReference = mFirebaseStorage.getReference().child("events_photos");


        //Get windsurfer data
        getWindsurferFromIntent(intent,mWindsurfer,getApplicationContext());
        if(mWindsurfer.getUid()==null){
            getUserFromDatabase();
        }

        setupTypeSpinner();
        setupConditionsSpinner();
        setupCountrySpinner();
        //Check that is new event or not.
        if (intent.getStringExtra(getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PLACE)) == null) {
            // This is a new event, so change the app bar to say "Add an Event"
            setTitle(getString(livewind.example.andro.liveWind.R.string.editor_activity_title_new_event));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            // invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing event, so change app bar to say "Edit Event"
            setTitle(getString(livewind.example.andro.liveWind.R.string.editor_activity_title_edit_event));
            mEvent = new Event();
            mExtraInfoHelp.getInfoFromIntent(intent,mEvent,getApplicationContext());
            loadExistingEvent(mEvent);
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(livewind.example.andro.liveWind.R.string.type_windsurfing))) {
                        mType = EventContract.EventEntry.TYPE_WINDSURFING;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.type_kitesurfing))) {
                        mType = EventContract.EventEntry.TYPE_KITESURFING;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.type_surfing))){
                        mType = EventContract.EventEntry.TYPE_SURFING;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = EventContract.EventEntry.TYPE_WINDSURFING;
            }
        });
    }

    private void setupConditionsSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter conditionsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                livewind.example.andro.liveWind.R.array.array_conditions_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        conditionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mConditionsSpinner.setAdapter(conditionsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mConditionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_onshore))) {
                        mConditions = EventContract.EventEntry.CONDITIONS_ONSHORE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_sideshore))) {
                        mConditions = EventContract.EventEntry.CONDITIONS_SIDESHORE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_offshore))) {
                        mConditions = EventContract.EventEntry.CONDITIONS_OFFSHORE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_N))){
                        mConditions = EventContract.EventEntry.CONDITIONS_N;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_NNE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_NNE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_NE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_NE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_ENE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_ENE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_E))){
                        mConditions = EventContract.EventEntry.CONDITIONS_E;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_ESE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_ESE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_SE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_SE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_SSE))){
                        mConditions = EventContract.EventEntry.CONDITIONS_SSE;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_S))){
                        mConditions = EventContract.EventEntry.CONDITIONS_S;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_SSW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_SSW;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_SW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_SW;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_WSW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_WSW;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_W))){
                        mConditions = EventContract.EventEntry.CONDITIONS_W;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_WNW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_WNW;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_NW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_NW;
                    } else if (selection.equals(getString(livewind.example.andro.liveWind.R.string.conditions_NNW))){
                        mConditions = EventContract.EventEntry.CONDITIONS_NNW;
                    } else {
                        mConditions = EventContract.EventEntry.CONDITIONS_ONSHORE;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mConditions = EventContract.EventEntry.CONDITIONS_ONSHORE;
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

    /**
     * Get user input from editor and save event into database.
     */
    private boolean saveEvent() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String placeString = mPlaceEditText.getText().toString().trim();
        String wind_powerString = mWindPowerEditText.getText().toString().trim();
        String wave_sizeString = mWaveSizeEditText.getText().toString().trim();
        String commentString = mCommentEditText.getText().toString().trim();
        // String to int
        int wind_power = 0;
        if (!TextUtils.isEmpty(wind_powerString)) {
            wind_power = Integer.parseInt(wind_powerString);
        }

        double wave_size = 0;
        if (!TextUtils.isEmpty(wave_sizeString)) {
            wave_size = Double.parseDouble(wave_sizeString);
        }
        //Check that all required information has been provided
        if (placeString.equals("")) {
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_place_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if(placeString.length()<3){
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_place_3letters_tm,Toast.LENGTH_SHORT).show();
            return false;
        } else if(wind_powerString.equals("")) {
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_wind_power_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if(wind_power>150) {
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_wind_power_max_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if(wave_sizeString.equals("")) {
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_wave_size_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (wave_size>30){
            Toast.makeText(this, livewind.example.andro.liveWind.R.string.event_wave_size_max_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else if (mCountry==0 || mCountry==EventContract.EventEntry.COUNTRY_BAD) {
            Toast.makeText(this, R.string.event_country_info_required_tm, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //New event or edit of existing event
            if (mEvent == null) {
                //Check that mWindsurfer activeEvents<activeEventsLimit
                if (mWindsurfer.checkEventsLimitAdvanced()==mWindsurfer.LIMIT_OK) {
                    //New event
                    String id = mEventsDatabaseReference.push().getKey();
                    Event newEventData = new Event(EditorActivity.this, id, mWindsurfer.getUsername(), mWindsurfer.getUid(), placeString,mCountry, mType, wind_power, wave_size, mConditions, commentString, mEventPhotoUrl , mWindsurfer.getPhotoName());
                    mEventsDatabaseReference.child(id).setValue(newEventData);
                    //save creation timestamp
                    Map<String, Object> value = new HashMap<>();
                    value.put("timestamp", ServerValue.TIMESTAMP);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String tokenId = sharedPref.getString(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.user_tokenId_shared_preference),"DEFAULT");
                    value.put("userTokenId",tokenId);

                    mEventsDatabaseReference.child(id).updateChildren(value);
                    //Add 5 points for creating the event
                    firebaseHelpForPointsAdd.addPoints(mWindsurfer.getUid(), EventContract.EventEntry.IT_IS_EVENT);
                    return true;
                } else if(mWindsurfer.checkEventsLimitAdvanced()==mWindsurfer.LIMIT_NO_CONNECTION){
                    //Toast.makeText(this, R.string.empty_view_no_connection_title_text, Toast.LENGTH_LONG).show();
                    Snackbar noConnectionSnackBar = Snackbar.make(findViewById(R.id.myEditorRelationCoordinatorLayout), R.string.toast_no_connection_cant_to_make_event, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) noConnectionSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    getUserFromDatabase();
                    textView.setMaxLines(3);
                    noConnectionSnackBar.setAction(getString(R.string.toast_try_to_reconnect), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getUserFromDatabase();
                        }
                    });
                    noConnectionSnackBar.show();
                    return false;
                } else {
                    Snackbar snackbarEventLimit = Snackbar.make(findViewById(R.id.myEditorRelationCoordinatorLayout), R.string.toast_relation_limit_reached, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbarEventLimit.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setMaxLines(3);

                    snackbarEventLimit.setAction(getString(R.string.user_activity_increase_limit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                            startActivity(intent);
                            finish();
                        }
                    });
                    snackbarEventLimit.show();
                    return false;
                }
            } else {
                //Edit existing event
                mEvent.setPlace(placeString);
                mEvent.setType(mType);
                mEvent.setWindPower(wind_power);
                mEvent.setWaveSize(wave_size);
                mEvent.setConditions(mConditions);
                mEvent.setComment(commentString);
                mEvent.setCountry(mCountry);

                Map<String, Object> eventValues = mEvent.toMap();
                mEventsDatabaseReference.child(mEvent.getId()).updateChildren(eventValues);
                return true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(livewind.example.andro.liveWind.R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new event, hide the "Delete" menu item.
        if (mEvent == null) {
            MenuItem menuItem = menu.findItem(livewind.example.andro.liveWind.R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Windsurfer clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_save:
                //if user provide all required information save event otherwise will be displayed toast message
                if(saveEvent()) {
                    Toast.makeText(this, getString(R.string.toast_relation_created),Toast.LENGTH_SHORT).show();
               //     Intent intent = new Intent(EditorActivity.this,CatalogActivity.class);
                    finish();
               //     startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            // Respond to a click on the "Delete" menu option
            case livewind.example.andro.liveWind.R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case livewind.example.andro.liveWind.R.id.action_help:
                showHelpDialog();
                return false;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mEventHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setMessage(livewind.example.andro.liveWind.R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(livewind.example.andro.liveWind.R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.keep_editing, new DialogInterface.OnClickListener() {
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
        //((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_button);
        //((Button)alertDialog.findViewById(android.R.id.button2)).setBackgroundResource(R.drawable.custom_button);
    }

    /**
     * Prompt the user to confirm that they want to delete this event.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setMessage(livewind.example.andro.liveWind.R.string.delete_dialog_msg);
        builder.setPositiveButton(livewind.example.andro.liveWind.R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Delete" button, so delete the event and remove 5 points.
                deleteEvent();
            }
        });
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Cancel" button, so dismiss the dialog
                // and continue editing.
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_add_event, null))
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
    public void showHelpWindDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_add_event_wind, null))
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
    public void showHelpTypeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_add_event_type, null))
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
    public void showHelpDirectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_add_event_wind_direction, null))
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
     * Perform the deletion of the event in the database.
     */
    private void deleteEvent() {
        // Only perform the delete if this is an existing event.
        if (mEvent.getId() != null) {
            if(!mEvent.getPhotoUrl().equals("")) {
                StorageReference ref = mFirebaseStorage.getReferenceFromUrl(mEvent.getPhotoUrl());
                ref.delete();
            }
            mEventsDatabaseReference.child(mEvent.getId()).removeValue();
            firebaseHelpForPointsAdd.removePoints(mEvent.getmUserUId(), EventContract.EventEntry.IT_IS_EVENT);
            // Show a toast message depending on whether or not the delete was successful.
            if (mEventsDatabaseReference.child(mEvent.getId()).getKey().equals("")) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_editor_delete_relation_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_editor_delete_relation_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }


    public void loadExistingEvent(Event event) {

        mPlaceEditText.setText(event.getPlace());
     //   mDateTextView.setText(event.getDate());
        mWindPowerEditText.setText(Integer.toString(event.getWindPower()));
        mWaveSizeEditText.setText(Double.toString(event.getWaveSize()));
        mCommentEditText.setText(event.getComment());

        //Type and conditions are spinners, so:
        switch (event.getType()) {
            case EventContract.EventEntry.TYPE_WINDSURFING:
                mTypeSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.TYPE_KITESURFING:
                mTypeSpinner.setSelection(2);
                break;
            case EventContract.EventEntry.TYPE_SURFING:
                mTypeSpinner.setSelection(3);
                break;
            default:
                mTypeSpinner.setSelection(0);
                break;
        }
        switch (event.getConditions()) {
            case EventContract.EventEntry.CONDITIONS_ONSHORE:
                mConditionsSpinner.setSelection(0);
                break;
            case EventContract.EventEntry.CONDITIONS_SIDESHORE:
                mConditionsSpinner.setSelection(1);
                break;
            case EventContract.EventEntry.CONDITIONS_OFFSHORE:
                mConditionsSpinner.setSelection(2);
                break;
            case EventContract.EventEntry.CONDITIONS_N:
                mConditionsSpinner.setSelection(3);
                break;
            case EventContract.EventEntry.CONDITIONS_NNE:
                mConditionsSpinner.setSelection(4);
                break;
            case EventContract.EventEntry.CONDITIONS_NE:
                mConditionsSpinner.setSelection(5);
                break;
            case EventContract.EventEntry.CONDITIONS_ENE:
                mConditionsSpinner.setSelection(6);
                break;
            case EventContract.EventEntry.CONDITIONS_E:
                mConditionsSpinner.setSelection(7);
                break;
            case EventContract.EventEntry.CONDITIONS_ESE:
                mConditionsSpinner.setSelection(8);
                break;
            case EventContract.EventEntry.CONDITIONS_SE:
                mConditionsSpinner.setSelection(9);
                break;
            case EventContract.EventEntry.CONDITIONS_SSE:
                mConditionsSpinner.setSelection(10);
                break;
            case EventContract.EventEntry.CONDITIONS_S:
                mConditionsSpinner.setSelection(11);
                break;
            case EventContract.EventEntry.CONDITIONS_SSW:
                mConditionsSpinner.setSelection(12);
                break;
            case EventContract.EventEntry.CONDITIONS_WSW:
                mConditionsSpinner.setSelection(13);
                break;
            case EventContract.EventEntry.CONDITIONS_SW:
                mConditionsSpinner.setSelection(14);
                break;
            case EventContract.EventEntry.CONDITIONS_WNW:
                mConditionsSpinner.setSelection(15);
                break;
            case EventContract.EventEntry.CONDITIONS_NW:
                mConditionsSpinner.setSelection(16);
                break;
            case EventContract.EventEntry.CONDITIONS_NNW:
                mConditionsSpinner.setSelection(17);
                break;
            default:
                mConditionsSpinner.setSelection(0);
                break;
        }
        switch (event.getCountry()) {
            case EventContract.EventEntry.COUNTRY_POLAND:
                mCountrySpinner.setSelection(1);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                mCountrySpinner.setSelection(2);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                mCountrySpinner.setSelection(3);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                mCountrySpinner.setSelection(4);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                mCountrySpinner.setSelection(5);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                mCountrySpinner.setSelection(6);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                mCountrySpinner.setSelection(7);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                mCountrySpinner.setSelection(8);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                mCountrySpinner.setSelection(9);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                mCountrySpinner.setSelection(10);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                mCountrySpinner.setSelection(11);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                mCountrySpinner.setSelection(12);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                mCountrySpinner.setSelection(13);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                mCountrySpinner.setSelection(14);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                mCountrySpinner.setSelection(15);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                mCountrySpinner.setSelection(16);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                mCountrySpinner.setSelection(17);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                mCountrySpinner.setSelection(18);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                mCountrySpinner.setSelection(19);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                mCountrySpinner.setSelection(20);
                break;
            default:
                mCountrySpinner.setSelection(20);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
        {
            Toast.makeText(mAddPhotoImageView.getContext(), livewind.example.andro.liveWind.R.string.editor_back_on_adding_photo, Toast.LENGTH_SHORT).show();
            return;
        }else if (requestCode == RC_PHOTO_PICKER) {
            // Sign-in succeeded, set up the UI
            Uri selectedImageUri = data.getData();
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressTextView.setVisibility(View.VISIBLE);
            mAddPhotoImageView.setVisibility(View.GONE);
            mAddPhotoPlusImageView.setVisibility(View.GONE);
            mProgressPercentageTextView.setVisibility(View.VISIBLE);
            if (selectedImageUri != null) {
                final StorageReference imgReference = mEventsStorageReference.child(selectedImageUri.getLastPathSegment());
                UploadTask uploadTask = imgReference.putFile(selectedImageUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        int currentprogress = (int) progress;
                        mProgressBar.setProgress(currentprogress);
                        mProgressTextView.setText(Integer.toString(currentprogress));
                        //Drawable draw=getResources().getDrawable(R.drawable.customprogressbar);
                        //mProgressBar.setProgressDrawable(draw);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                });
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imgReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri taskResult = task.getResult();
                            mEventPhotoUrl = taskResult.toString();
                            //info o ladowaniu obrazka
                            mProgressBar.setVisibility(View.GONE);
                            mProgressTextView.setVisibility(View.GONE);
                            mProgressPercentageTextView.setVisibility(View.GONE);
                            mAddPhotoImageView.setVisibility(View.VISIBLE);
                            //mAddPhotoPlusImageView.setVisibility(View.VISIBLE);
                            mAddPhotoImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.baseline_done_black_48);
                            Toast.makeText(mAddPhotoImageView.getContext(), livewind.example.andro.liveWind.R.string.editor_photo_added, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
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
            TextView tv = (TextView) getActivity().findViewById(livewind.example.andro.liveWind.R.id.edit_event_date);
            //Display the user changed time on TextView
            String hourString = "";
            String minuteString = "";
            if(hourOfDay<10){
                hourString="0"+Integer.toString(hourOfDay);
            } else
            {
                hourString=Integer.toString(hourOfDay);
            }
            if(minute<10){
                minuteString="0"+Integer.toString(minute);
            } else
            {
                minuteString=Integer.toString(minute);
            }
            tv.setText(hourString+":"+minuteString + " - Today");
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    /**
     * If user active events limit = 0 that means that app doesn't have internet connection
     * User could try again to get connection by click snackbar that start this method
     * //TODO update this method to better work
     */

    public void getUserFromDatabase() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String userUid = sharedPref.getString(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.user_uid_shared_preference),"DEFAULT");
        Query usersQuery = mUserDatabaseReference.orderByChild("uid").equalTo(userUid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mWindsurfer = dataSnapshot.child(userUid).getValue(Windsurfer.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_internet_connection_recovered), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_internet_connection_no_recovered), Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}




