package livewind.example.andro.liveWind;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import livewind.example.andro.liveWind.FAQ.FAQActivity;
import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import livewind.example.andro.liveWind.user.UserActivity;
import livewind.example.andro.liveWind.user.Windsurfer;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import livewind.example.andro.liveWind.data.EventContract;


public class CatalogActivity extends AppCompatActivity  {


    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static String FACEBOOK_URL = "https://www.facebook.com/pg/SurfAdvisorAPP";
    public static String FACEBOOK_PAGE_ID = "553065221866671";


    private ListView mEventListView;
    private ImageView mSelectCountryImageView;
    private Spinner mTripsOptionsSpinner;
    private int mTripsOptions;
    private ImageView mCheckBoxImageView;
    private EventAdapter mEventAdapter;
    private View mEmptyView;
    private View mEmptyViewNoRecordsRelations;
    private View mEmptyViewNoRecordsTrips;
    private Button mEmptyViewNoConnectionButton;
    private ProgressBar mProgressBar;
    private int choseIntentFromDrawerLayout=-1;
    private boolean checkConection = true;

    /** Navigation Drawer */
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Button tripsButton;
    private Button eventsButton;

    private String CHANNEL_ID = "3";


    /** FIREBASE **/
    private FirebaseDatabase mFirebaseDatabase;
    ChildEventListener mChildEventListener;
    private DatabaseReference mEventsDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseHelp mFirebaseHelp = new FirebaseHelp(); //FirebaseHelp for removing active event/tip info when is deleting old event/trip
    /** FIREBASE TO DELETING OLD IMAGES **/
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mEventsStorageReference;
    /** FOR USERS DATABASE*/
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mUsersNicknamesDatabaseReference;
    /** LOGIN **/
    public static final int RC_SIGN_IN = 1;
    private Windsurfer mWindsurfer = new Windsurfer();
    // Initialize events ListView and its adapter
    private List<Event> events = new ArrayList<>();
    /** HELP FOR INTENT PUT EXTRA */
    ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_catalog);
        context = getApplicationContext();
        mEventListView = (ListView) findViewById(livewind.example.andro.liveWind.R.id.list);
        mEmptyView = (View) findViewById(livewind.example.andro.liveWind.R.id.empty_view_no_connection);
        mEmptyViewNoRecordsRelations = (View) findViewById(R.id.empty_view_no_records_relations);
        mEmptyViewNoRecordsTrips = (View) findViewById(R.id.empty_view_no_records_trips);
        mEmptyViewNoConnectionButton = (Button) findViewById(R.id.empty_view_no_connection_button);
        mProgressBar = (ProgressBar) findViewById(livewind.example.andro.liveWind.R.id.loading_indicator);
        mSelectCountryImageView = (ImageView) findViewById(R.id.select_country_image_view);



        /**
         * Navigation Drawer
         */
        //New appbar
        mToolbar = findViewById(livewind.example.andro.liveWind.R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionbar = this.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(livewind.example.andro.liveWind.R.drawable.ic_menu_black_24dp);

        mDrawerLayout = findViewById(livewind.example.andro.liveWind.R.id.drawer_layout);
        final NavigationView navigationView = findViewById(livewind.example.andro.liveWind.R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case android.R.id.home:
                                mDrawerLayout.openDrawer(GravityCompat.START);
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_user:
                                //Save info about clicked menu item to open correct activity when "onDrawerClosed" listener will be call
                                choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_USER_PROFIL;
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_settings:
                                choseIntentFromDrawerLayout=EventContract.EventEntry.MENU_SETTINGS;
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_about:
                                choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_ABOUT;
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_faq:
                                choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_FAQ;
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_log_out:
                                choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_LOG_OUT;
                                return true;
                            case R.id.action_fb:
                                choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_FACEBOOK;
                                return true;
                                default:
                                    choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_NOTHING;
                        }
                        return true;

                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        choseIntentFromDrawerLayout= EventContract.EventEntry.MENU_NOTHING;
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //Uncheck all items
                        int size = navigationView.getMenu().size();
                        for (int i = 0; i < size; i++) {
                            navigationView.getMenu().getItem(i).setChecked(false);
                        }
                        // Respond when the drawer is closed
                        switch (choseIntentFromDrawerLayout) {
                            case EventContract.EventEntry.MENU_USER_PROFIL:
                                Intent intentUser = new Intent(CatalogActivity.this,UserActivity.class);
                                mExtraInfoHelp.putWindsurferToIntent(intentUser,mWindsurfer,getApplicationContext());
                                startActivity(intentUser);
                                break;
                            case EventContract.EventEntry.MENU_SETTINGS:
                                Intent intentSettings = new Intent(CatalogActivity.this,SettingsActivity.class);
                                startActivity(intentSettings);
                                break;
                            case EventContract.EventEntry.MENU_ABOUT:
                                Intent intentAbout = new Intent(CatalogActivity.this,AboutActivity.class);
                                startActivity(intentAbout);
                                break;
                            case EventContract.EventEntry.MENU_FAQ:
                                Intent intentFAQ = new Intent(CatalogActivity.this,FAQActivity.class);
                                startActivity(intentFAQ);
                                break;
                            case EventContract.EventEntry.MENU_LOG_OUT:
                                AuthUI.getInstance().signOut(CatalogActivity.this);
                                break;
                            case EventContract.EventEntry.MENU_FACEBOOK:
                                try{
                                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                                String facebookUrl = getFacebookPageURL(getApplicationContext());
                                facebookIntent.setData(Uri.parse(facebookUrl));
                                facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(facebookIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(CatalogActivity.this, getApplicationContext().getResources().getString(R.string.toast_share_facebook_error), Toast.LENGTH_SHORT).show();
                                 }
                                break;
                            case EventContract.EventEntry.MENU_NOTHING:
                                break;
                        }

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                        int size = navigationView.getMenu().size();
                        for (int i = 0; i < size; i++) {
                            navigationView.getMenu().getItem(i).setChecked(false);
                        }
                    }
                }
        );

        /**
         * End of navigation drawer code - Initialization of other variables
         */

        // Initialize events ListView and its adapter
   //     final List<Event> events = new ArrayList<>();
        mEventAdapter = new EventAdapter(this, events,0);
        mEventListView.setAdapter(mEventAdapter);
        /**SETTING PREFERENCE*/
        PreferenceManager.setDefaultValues(this, livewind.example.andro.liveWind.R.xml.pref_general, false);


        /**FIREBASE DATABASE **/
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child("events");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mUsersNicknamesDatabaseReference = mFirebaseDatabase.getReference().child("usernames");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mEventsStorageReference = mFirebaseStorage.getReference().child("events_photos");
        //It is for offline options normally but I use it to get actually user points in UserActivity without refreshing.
        mFirebaseAuth = FirebaseAuth.getInstance();
        removingOldEvents();
        /** FIREBASE AUTH **/
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Checking internet connection
                checkConection = isOnline();
                if (!checkConection) {
                    mProgressBar.setVisibility(View.GONE);
                    mEventListView.setEmptyView(mEmptyView);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_no_connection), Toast.LENGTH_SHORT).show();
                    mEmptyViewNoConnectionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            recreate();
                        }
                    });
                    onPause();
                } else {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        //Check if it's a new user or not
                        checkUser(user.getDisplayName(),user.getEmail(), user.getUid());
                        final String userUid = user.getUid();
                        OnSignedInInitialize();
                        //Download actual discount code from database
                        DatabaseReference mRemovingReference= mFirebaseDatabase.getReference().child("discount_code");
                        mRemovingReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String discountCode = (String) dataSnapshot.getValue();
                                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = displayOptions.edit();
                                editor.putString(getString(R.string.settings_discount_code_key),discountCode);
                                editor.apply();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });
                        SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = displayOptions.edit();
                        editor.putString(getString(livewind.example.andro.liveWind.R.string.user_uid_shared_preference), user.getUid());
                        // Commit the edits!
                        editor.apply();
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(CatalogActivity.this, new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String userToken = instanceIdResult.getToken();
                                mWindsurfer.setUserToken(userToken);
                                Log.d("FCM_TOKEN", userToken);
                                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = displayOptions.edit();
                                editor.putString(getString(livewind.example.andro.liveWind.R.string.user_tokenId_shared_preference), userToken);
                                editor.apply();
                                mUsersDatabaseReference.child(userUid).child("userToken").setValue(userToken, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                                        if (databaseError != null) {
                                            Log.e("CATALOG_ACTIVITY", "Failed to write message", databaseError.toException());
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        OnSignedOutCleanUp();
                        // Windsurfer is signed out
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                               // new AuthUI.IdpConfig.FacebookBuilder().build()))
                                        .setTheme(livewind.example.andro.liveWind.R.style.AppThemeFirebaseAuth)
                                        .setLogo(livewind.example.andro.liveWind.R.drawable.logo_d)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            }
        };



        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, CatalogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        // Setup events button to display events in the place of trips
        eventsButton = (Button) findViewById(livewind.example.andro.liveWind.R.id.catalog_events_button);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SharedPreferences displayOptions = getSharedPreferences(getString(R.string.settings_display_events), 0);
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                editor.putBoolean(getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key),true);
                // Commit the edits!
                editor.apply();
                recreate();

            }
        });

        // Setup trips button to display trips in the place of trips
        tripsButton = (Button) findViewById(livewind.example.andro.liveWind.R.id.catalog_trips_button);
        tripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SharedPreferences displayOptions = getSharedPreferences(getString(R.string.settings_display_events), 0);
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                editor.putBoolean(getString(R.string.settings_display_boolean_key),false);
                // Commit the edits!
                editor.apply();
                recreate();
            }
        });
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean displayBoolean = sharedPrefs.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
        if (displayBoolean) {
            SpannableString contentEvent = new SpannableString(getResources().getString(R.string.catalog_header_forecast));
            contentEvent.setSpan(new UnderlineSpan(), 0, contentEvent.length(), 0);
            eventsButton.setText(contentEvent);
        } else {
            SpannableString contentTrip = new SpannableString(getResources().getString(R.string.catalog_header_trips));
            contentTrip.setSpan(new UnderlineSpan(), 0, contentTrip.length(), 0);
            tripsButton.setText(contentTrip);
        }

        // Setup FAB to open EditorActivity to make new event
        FloatingActionButton fab = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorChoose.class);
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                startActivity(intent);
                /**
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean displayBoolean = sharedPref.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean), true);
                if(displayBoolean){
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                //PutExtra user data to know who make event
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                startActivity(intent);


            } else {
                    Intent intent = new Intent(CatalogActivity.this, EditorTripActivity.class);
                    //PutExtra user data to know who make event
                    mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                    startActivity(intent);
                }
                 */
        }});

        // Setup the item click listener to open EventActivity or EventTripActivity
        mEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                //Load clicked event
                Event clickedEvent = mEventAdapter.getItem(position);
                if(clickedEvent.getStartDate().equals("DEFAULT")) {
                    Intent intent = new Intent(CatalogActivity.this, EventActivity.class);
                    //Put Extra information about clicked event and who is clicking.
                    intent = mExtraInfoHelp.putInfoToIntent(intent,clickedEvent,mWindsurfer,getApplicationContext());
                    mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CatalogActivity.this, EventTripActivity.class);
                    intent = mExtraInfoHelp.putInfoToIntent(intent,clickedEvent,mWindsurfer,getApplicationContext());
                    mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                    startActivity(intent);
                }
            }
        });

        // Setup the item long click listener to open EditorActivity
        mEventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Load clicked event
                Event clickedEvent = mEventAdapter.getItem(position);
                //Check that it is your event
                if (!mWindsurfer.getUsername().equals(clickedEvent.getmUsername())) {
                    Toast.makeText(CatalogActivity.this, "You can edit only your events", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (mWindsurfer.getUsername().equals(clickedEvent.getmUsername()) && clickedEvent.getStartDate().equals("DEFAULT")) {
                    Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                    mExtraInfoHelp.putInfoToIntent(intent,clickedEvent,mWindsurfer,getApplicationContext());
                    startActivity(intent);
                    //Because onItemLongClick has type boolean in place of void in onItemClick
                    return true;
                } else if((mWindsurfer.getUsername().equals(clickedEvent.getmUsername()) && !clickedEvent.getStartDate().equals("DEFAULT"))){
                    Intent intent = new Intent(CatalogActivity.this, EditorTripActivity.class);
                    mExtraInfoHelp.putInfoToIntent(intent,clickedEvent,mWindsurfer,getApplicationContext());
                    startActivity(intent);
                    return true;
                } else{return false;}
            }
        });

        mSelectCountryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showSelectCountryDialog();
            }
        });



    }

    //Open navigation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Windsurfer clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Open menu
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, getString(R.string.toast_login_in),Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED){
                Toast.makeText(this, getString(R.string.toast_login_in_faild),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        dettachDatabaseReadListener();
        mEventAdapter.clear();
    }

    private void OnSignedInInitialize(){
        attachDatabaseReadListener();
        AppRater.app_launched(this);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
        if(selectedCountries.contains("0")&&selectedCountries.size()!=1){
            showCountryChangesConfirmationDialog();
        }
        //printHashKey(getApplicationContext());
    }
    private void OnSignedOutCleanUp(){
        mEventAdapter.clear();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Event event = dataSnapshot.getValue(Event.class);
                    event.setId(dataSnapshot.getKey());
                    mProgressBar.setVisibility(View.GONE);
                    //Filters and emptyView when no matching records
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int displayTripsOptions = Integer.valueOf(sharedPref.getString(getApplicationContext().getString(R.string.settings_display_trips_key),"1"));
                    Set<String> selectedCountries = sharedPrefs.getStringSet(getApplicationContext().getString(R.string.settings_display_countries_key), new HashSet<String>());
                    boolean displayBoolean = sharedPref.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
                    String checkEventOrTrip = "DEFAULT";
                        if (displayBoolean) {
                            if (event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                                mEventAdapter.add(event);
                            } else {}
                        } else {

                            if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO) {
                                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                                    mEventAdapter.add(event);
                                }
                            } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM) {
                                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                                    mEventAdapter.add(event);
                                }
                            } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_TO) {
                                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getCountry())))) {
                                    mEventAdapter.add(event);
                                }
                            }
                        }
                        if (mEventAdapter.isEmpty()) {
                            if (displayBoolean) {
                                mEventListView.setEmptyView(mEmptyViewNoRecordsRelations);
                            } else {
                                mEventListView.setEmptyView(mEmptyViewNoRecordsTrips);
                            }
                        }
                        mEventAdapter.sort();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                  //  Log.i("CHANGE", String.valueOf(events.size()));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //AUTO REMOVING OLD EVENTS AND TRIPS
                    if (dataSnapshot.exists()) {
                        final Event event = dataSnapshot.getValue(Event.class);
                        if(event.getStartDate().equals("DEFAULT")) {
                            //EVENT
                            //Delete photo from storage
                            if(!event.getPhotoUrl().equals("")) {
                                StorageReference ref = mFirebaseStorage.getReferenceFromUrl(event.getPhotoUrl());
                                Log.i("AUTO REMOVING", "EVENT   =   " + ref.toString());
                                ref.delete();
                            }
                            mFirebaseHelp.removeOnlyActiveData(event.getmUserUId(), EventContract.EventEntry.IT_IS_EVENT);
                        }else{
                            //TRIP
                            mFirebaseHelp.removeOnlyActiveData(event.getmUserUId(), EventContract.EventEntry.IT_IS_TRIP);
                        }
                    }
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.i("CHANGE", String.valueOf(events.size()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i("CHANGE", String.valueOf(events.size()));
                }
            };
            mEventsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void dettachDatabaseReadListener(){
        if(mChildEventListener != null){
            mEventsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }
    //Function that remove old trips and events from datab
    private void removingOldEvents(){
        DatabaseReference mRemovingReference= mFirebaseDatabase.getReference().child("currentTime");

        mRemovingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final Long cutoff = (Long) snapshot.getValue();
                // Trips
              //  System.out.println(cutoff);
                Query oldTrips = mEventsDatabaseReference.orderByChild("timestampStartDate").endAt(cutoff);
                oldTrips.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                            itemSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
                //Events
                final Long cutoffEvents = (Long) snapshot.getValue() - TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);
                final Query oldEvents = mEventsDatabaseReference.orderByChild("timestamp").endAt(cutoffEvents);
                oldEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /**
                        Log.i("PHOTO DELETING","SNAPSHOT: "+snapshot.child("photoUrl").getValue().toString());
                        mEventsStorageReference.child(snapshot.child("photoUrl").getValue().toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("PHOTO DELETING","SUCCESS");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.i("PHOTO DELETING","FAIL");
                            }
                        });
                         */
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                            itemSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRemovingReference.setValue(ServerValue.TIMESTAMP);
    }

    //Function for gets user data and create user in firebase if user is new
    private void checkUser(final String loggedUserNick, final String loggedUserEmail, final String loggedUserUid){
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(loggedUserUid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //old user
                    mWindsurfer = dataSnapshot.child(loggedUserUid).getValue(Windsurfer.class);
                }
                else {
                    //new user
                    //String id = mUsersDatabaseReference.push().getKey();

                    mWindsurfer = new Windsurfer(loggedUserUid,loggedUserNick, loggedUserEmail, 500, 0, 0,getApplicationContext() );
                    mUsersDatabaseReference.child(loggedUserUid).setValue(mWindsurfer);
                    mUsersNicknamesDatabaseReference.child(loggedUserNick).setValue(loggedUserUid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CHANNEL NAME";
            String description = "CHANNEL DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * SELECT COUNTRY DIALOG
     */
    // Show select photo action
    private void showSelectCountryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_catalog_dialog_select_country,null);
        // Set grid view to alertDialog
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int displayTripsOptions = Integer.valueOf(sharedPrefs.getString(getApplicationContext().getString(R.string.settings_display_trips_key),"1"));
        mTripsOptions=displayTripsOptions;
        mTripsOptionsSpinner = (Spinner) dialogView.findViewById(R.id.spinner_trip_display_options);
        setupTripOptionsSpinner();

        switch (mTripsOptions) {
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
        mList.add(new Country(getString(R.string.country_number_0),R.drawable.flag_world));
        mList.add(new Country(getString(R.string.country_number_1),R.drawable.flag_pl));
        mList.add(new Country(getString(R.string.country_number_2),R.drawable.flag_gr));
        mList.add(new Country(getString(R.string.country_number_3),R.drawable.flag_es));
        mList.add(new Country(getString(R.string.country_number_4),R.drawable.flag_hr));
        mList.add(new Country(getString(R.string.country_number_5),R.drawable.flag_pt));
        mList.add(new Country(getString(R.string.country_number_6),R.drawable.flag_de));
        mList.add(new Country(getString(R.string.country_number_7),R.drawable.flag_fr));
        mList.add(new Country(getString(R.string.country_number_8),R.drawable.flag_za));
        mList.add(new Country(getString(R.string.country_number_9),R.drawable.flag_ma));
        mList.add(new Country(getString(R.string.country_number_10),R.drawable.flag_it));
        mList.add(new Country(getString(R.string.country_number_11),R.drawable.flag_eg));
        mList.add(new Country(getString(R.string.country_number_12),R.drawable.flag_uk));
        mList.add(new Country(getString(R.string.country_number_13),R.drawable.flag_tr));
        mList.add(new Country(getString(R.string.country_number_14),R.drawable.flag_at));
        mList.add(new Country(getString(R.string.country_number_15),R.drawable.flag_dk));
        mList.add(new Country(getString(R.string.country_number_16),R.drawable.flag_br));
        mList.add(new Country(getString(R.string.country_number_17),R.drawable.flag_us));
        mList.add(new Country(getString(R.string.country_number_18),R.drawable.flag_vn));
        mList.add(new Country(getString(R.string.country_number_19),R.drawable.flag_mt));
        mList.add(new Country(getString(R.string.country_number_20),R.drawable.flag_world));
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
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }

    /**
     * Dialog showed when user click apply on SelectCountryDialog and check "All world" and one or more other country.
     */
    private void showCountryChangesConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.catalog_activity_changes_confrimity_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_confrimity_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                selectedCountries.remove(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                editor.putStringSet(getString(R.string.settings_display_countries_key),selectedCountries);
                // Commit the edits!
                editor.apply();
                recreate();
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog();
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
    private void showCountryChangesNullDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.catalog_activity_changes_null_dialog_msg);

        builder.setPositiveButton(R.string.catalog_activity_changes_null_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                selectedCountries.add(EventContract.EventEntry.COUNTRY_ALL_WORLD);
                editor.putStringSet(getString(R.string.settings_display_countries_key),selectedCountries);
                // Commit the edits!
                editor.apply();
                recreate();
            }
        });

        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectCountryDialog();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                return "fb://page/" + FACEBOOK_PAGE_ID;
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupTripOptionsSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter tripOptionsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_trips_display_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        tripOptionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTripsOptionsSpinner.setAdapter(tripOptionsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTripsOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.display_trips_from_and_to))) {
                        mTripsOptions = EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO;
                    } else if (selection.equals(getString(R.string.display_trips_from))) {
                        mTripsOptions = EventContract.EventEntry.DISPLAY_TRIPS_FROM;
                    } else if (selection.equals(getString(R.string.display_trips_to))){
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
    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
    }
*/
}