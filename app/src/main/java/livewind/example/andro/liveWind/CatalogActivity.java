package livewind.example.andro.liveWind;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import livewind.example.andro.liveWind.Countries.CountryDialog;
import livewind.example.andro.liveWind.FAQ.FAQActivity;
import livewind.example.andro.liveWind.Filter.FilterTrips;
import livewind.example.andro.liveWind.Filter.FilterTripsActivity;
import livewind.example.andro.liveWind.Filter.FilterTripsContract;
import livewind.example.andro.liveWind.Helpers.CurrencyHelper;
import livewind.example.andro.liveWind.Helpers.DateHelp;
import livewind.example.andro.liveWind.Helpers.SocialHelper;
import livewind.example.andro.liveWind.Notifications.MyFirebaseMessagingService;
import livewind.example.andro.liveWind.Notifications.NewContentNotification;
import livewind.example.andro.liveWind.Notifications.NewContentNotificationDialog;
import livewind.example.andro.liveWind.firebase.FirebaseContract;
import livewind.example.andro.liveWind.firebase.FirebasePromotions;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import livewind.example.andro.liveWind.data.EventContract;

import static livewind.example.andro.liveWind.ExtraInfoHelp.getNewContentNotificationFromIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putInfoToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;

/**
 * Created by JGJ on 10/10/18.
 * Header added during refactoring add 08/04/2019 by JGJ.
 *
 * Main activity - download data from firebase and display list of trips or coverages
 *
 */
public class CatalogActivity extends AppCompatActivity  {
    private static final String TAG = "CatalogActivity";

    //Only to give model classes possibility to access SharedPreferences
    private static Context context;

    public static Context getContext() {
        return context;
    }

    /** Views **/
    private RecyclerView mEventRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
    private ImageView mFiltersImageView;
    private ProgressBar mProgressBar;
    //EmptyViews (when any record match filters)
    private View mEmptyView;
    private View mEmptyViewNoRecordsRelations;
    private View mEmptyViewNoRecordsTrips;
    //EmptyView - no connection
    private Button mEmptyViewNoConnectionButton;
    private boolean checkConnection = true;

    /** Declaration of events ListView and its Adapter */
    private EventAdapter mEventAdapter;

    /** Navigation Drawer */
    private DrawerLayout mDrawerLayout;
    private int choseIntentFromDrawerLayout=-1;

    /** FIREBASE **/
    private FirebaseDatabase mFirebaseDatabase;
    ChildEventListener mChildEventListener;
    private DatabaseReference mEventsDatabaseReference;
    private Query mEventQueryRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    /** FIREBASE TO DELETING OLD IMAGES **/
    private FirebaseStorage mFirebaseStorage;
    /** FOR USERS DATABASE*/
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mUsersNicknamesDatabaseReference;
    /** LOGIN **/
    public static final int RC_SIGN_IN = 1;
    private Windsurfer mWindsurfer = new Windsurfer();

    /** New content notification **/
    private NewContentNotification mNewContentNotification = new NewContentNotification();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_catalog);
        context = getApplicationContext();

        //Check that intent have any newContentNotifications (have when user open app from new content notification)
        Intent intent = getIntent();
        if(getNewContentNotificationFromIntent(intent,mNewContentNotification)){
            NewContentNotificationDialog.showNewContentNotificationDialog(CatalogActivity.this,mNewContentNotification);
            intent.removeExtra(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_TITLE);
            intent.removeExtra(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_DESCRIPTION);
            intent.removeExtra(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_ACTION_TITLE);
            intent.removeExtra(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_ACTION_LINK);
            intent.removeExtra(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_DATE);
        }

        //Set default settings preferences values - called only on first open
        PreferenceManager.setDefaultValues(this, livewind.example.andro.liveWind.R.xml.pref_general, false);

        initViews();
        initFirebaseVariables();
        MyFirebaseMessagingService.topicSubscriptionService(CatalogActivity.this);
        setupNavigationDrawer();

        removingOldEvents(); //Remove old coverages and trips
        setupFirebaseAuth(); //Login user
        initClickListeners();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // dettachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventAdapter.stopListening();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    /**
     * Init methods
     */
    private void initViews() {
        mEventRecyclerView = (RecyclerView) findViewById(livewind.example.andro.liveWind.R.id.list);
        mEmptyView = (View) findViewById(livewind.example.andro.liveWind.R.id.empty_view_no_connection);
        mEmptyViewNoRecordsRelations = (View) findViewById(R.id.empty_view_no_records_relations);
        mEmptyViewNoRecordsTrips = (View) findViewById(R.id.empty_view_no_records_trips);
        mEmptyViewNoConnectionButton = (Button) findViewById(R.id.empty_view_no_connection_button);
        mProgressBar = (ProgressBar) findViewById(livewind.example.andro.liveWind.R.id.loading_indicator);
        mFiltersImageView = (ImageView) findViewById(R.id.filters_image_view);
        //Check current catalogActivity mode (coverage or trip)
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean displayBoolean = sharedPrefs.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
        if (displayBoolean==EventContract.EventEntry.IT_IS_TRIP) {
            mFiltersImageView.setImageResource(R.drawable.ic_filter_list_black_24dp);
        } else {
            mFiltersImageView.setImageResource(R.drawable.flag_world);
        }
    }

    private void initFirebaseVariables(){
        /**FIREBASE DATABASE **/
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_EVENTS);
        mEventQueryRef = mEventsDatabaseReference;
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_USERS);
        mUsersNicknamesDatabaseReference = mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_USERNAMES);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void initClickListeners(){
        // Setup events button to display events in the place of trips
        Button eventsButton = (Button) findViewById(livewind.example.andro.liveWind.R.id.catalog_events_button);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                editor.putBoolean(getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key),true);
                editor.apply();
                recreate();

            }
        });

        // Setup trips button to display trips in the place of trips
        Button tripsButton = (Button) findViewById(livewind.example.andro.liveWind.R.id.catalog_trips_button);
        tripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = displayOptions.edit();
                editor.putBoolean(getString(R.string.settings_display_boolean_key),false);
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
                putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                startActivity(intent);
            }});

        //Setup click listener on filter countries image view
        mFiltersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Check current catalogActivity mode (coverage or trip)
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean displayBoolean = sharedPrefs.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
                if (displayBoolean==EventContract.EventEntry.IT_IS_EVENT) {
                    CountryDialog.showSelectCountryDialog(CatalogActivity.this);
                } else {
                    Intent intentFilter = new Intent(CatalogActivity.this,FilterTripsActivity.class);
                    startActivity(intentFilter);
                }
            }
        });
    }

    /**
     * Setup firebase auth
     */
    private void setupFirebaseAuth(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkConnection = isOnline();
                //Checking internet connection
                if (!checkConnection) {
                    setupOfflineViews();
                    onPause();
                } else {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        //Check if it's a new user or not
                        checkUser(user.getDisplayName(),user.getEmail(), user.getUid());
                        final String userUid = user.getUid();
                        OnSignedInInitialize();
                        //Download actual discount code from database and put them to sharedPref
                        FirebasePromotions.getSurfAdvisorPromotionCode(CatalogActivity.this);
                        //Put user uid to sharedPref
                        SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = displayOptions.edit();
                        editor.putString(getString(livewind.example.andro.liveWind.R.string.user_uid_shared_preference), user.getUid());
                        editor.apply();

                        //Put userToken to sharedPref and user database
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(CatalogActivity.this, new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String userToken = instanceIdResult.getToken();
                                mWindsurfer.setUserToken(userToken);
                                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = displayOptions.edit();
                                editor.putString(getString(livewind.example.andro.liveWind.R.string.user_tokenId_shared_preference), userToken);
                                editor.apply();
                                mUsersDatabaseReference.child(userUid).child(FirebaseContract.FirebaseEntry.COLUMN_USERS_USER_TOKEN).setValue(userToken, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                                        if (databaseError != null) {
                                            Log.e(TAG, "Failed to write message", databaseError.toException());
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
    }

     // Auth login onActivityResult method
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

    private void OnSignedInInitialize(){
        AppRater.app_launched(this); //Display request to rate the app if conditions are accomplish
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> selectedCountries = sharedPrefs.getStringSet(getString(R.string.settings_display_countries_key), new HashSet<String>());
        if(selectedCountries.contains("0")&&selectedCountries.size()!=1){
            //Check if is selected "all world" and other country if yes - show dialog to change it
            CountryDialog.showCountryChangesConfirmationDialog(CatalogActivity.this);
        }
        attachDatabaseReadListener();
    }

    private void OnSignedOutCleanUp(){
    }
    /**
     * Setup Navigation Drawer
     */
    private void setupNavigationDrawer(){
        //New appbar
        Toolbar mToolbar = findViewById(livewind.example.andro.liveWind.R.id.toolbar);
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
                        //Save info about clicked menu item to open correct activity when "onDrawerClosed" listener will be call
                        switch (menuItem.getItemId()) {
                            case android.R.id.home:
                                mDrawerLayout.openDrawer(GravityCompat.START);
                                return true;
                            case livewind.example.andro.liveWind.R.id.action_user:
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
                                putWindsurferToIntent(intentUser,mWindsurfer,getApplicationContext());
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
                                    String facebookUrl = SocialHelper.getFacebookPageURL(getApplicationContext());
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
    }

    //Open navigation drawer
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

    private void attachDatabaseReadListener() {
        //Check current catalogActivity mode (coverage or trip)
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean displayBoolean = sharedPrefs.getBoolean(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
        if(displayBoolean) {
            mEventQueryRef = checkFiltersOnCoverageDatabaseReference();
        } else {
            mEventQueryRef = checkFiltersOnTripsDatabaseReference();
        }
        // Initialize events ListView and its adapter
        mEventAdapter = new EventAdapter(this, mEventQueryRef, mWindsurfer,mProgressBar,mEmptyView);
        mEventRecyclerView.setAdapter(mEventAdapter);
        mEventAdapter.startListening();

         if (displayBoolean==EventContract.EventEntry.IT_IS_TRIP) {
            mEventAdapter.setEmptyView(mEmptyViewNoRecordsTrips);
        } else {
            mEventAdapter.setEmptyView(mEmptyViewNoRecordsRelations);
        }
    }

    private void dettachDatabaseReadListener(){
        if(mChildEventListener != null){
            mEventQueryRef.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

    /**
     * Method that remove old trips and coverages from database
     */
    private void removingOldEvents(){
        DatabaseReference mRemovingReference= mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_CURRENT_TIME);
        mRemovingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final Long cutoff = (Long) snapshot.getValue();
                // Trips - delete if currentTime timestamp > trip start date timestamp
                Query oldTrips = mEventsDatabaseReference.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_EVENTS_TIMESTAMP_START_DATE).endAt(cutoff);
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
                //Coverages - delete 8h after create
                final Long cutoffEvents = (Long) snapshot.getValue() - TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);
                final Query oldEvents = mEventsDatabaseReference.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_EVENTS_TIMESTAMP).endAt(cutoffEvents);
                oldEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

    /**
     * Method that gets user data and create user in firebase if user is new
     * @param loggedUserNick - nick from FirebaseAuth
     * @param loggedUserEmail - email from FirebaseAuth
     * @param loggedUserUid - uid from FirebaseAuth
     */
    private void checkUser(final String loggedUserNick, final String loggedUserEmail, final String loggedUserUid){
        Query usersQuery = mUsersDatabaseReference.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_USERS_UID).equalTo(loggedUserUid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //old user
                    mWindsurfer = dataSnapshot.child(loggedUserUid).getValue(Windsurfer.class);
                    mEventAdapter.setWindsurfer(mWindsurfer);
                }
                else {
                    //new user
                    mWindsurfer = new Windsurfer(loggedUserUid,loggedUserNick, loggedUserEmail, 500, 0, 0,getApplicationContext() );
                    mUsersDatabaseReference.child(loggedUserUid).setValue(mWindsurfer);
                    mUsersNicknamesDatabaseReference.child(loggedUserNick).setValue(loggedUserUid);
                    mEventAdapter.setWindsurfer(mWindsurfer);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Users nick and uid database
        mUsersNicknamesDatabaseReference.child(loggedUserNick).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(loggedUserUid);
                    return Transaction.success(mutableData);
                }

                return Transaction.abort();
            }
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean commited, @Nullable DataSnapshot dataSnapshot) {
                if (commited) {
                    Log.i(TAG,"USERNAME SAVED");
                } else {
                    Log.i(TAG,"USERNAME EXIST");
                }
            }
        });
    }

    /**
     * Check internet connection
     * @return false if offline
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setupOfflineViews(){
        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_no_connection), Toast.LENGTH_SHORT).show();
        mEmptyView.setVisibility(View.VISIBLE);
        mEventRecyclerView.setVisibility(View.GONE);
        mEmptyViewNoConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventRecyclerView.setVisibility(View.VISIBLE);
                recreate();
            }
        });
    }

    /**
     * Check event data on app filters from @FilterTripsActivity
     * @param event - this method decide to display them or not
     * @return true if event pass filters
     */
    public static boolean checkFilters(Event event){
        //Load all filters from SharedPreferences
        FilterTrips filterTrips = new FilterTrips();
        filterTrips.getFilterTripsPreferences();
        //Check filters
        if(!(event.getTimestampStartDate()>=filterTrips.getmDateFromTimestamp())){
            return false;
        }

        if(!(DateHelp.dateToTimestamp(event.getDate())<=filterTrips.getmDateToTimestamp())){
            return false;
        }

        if(!(CurrencyHelper.currencyToPLN(Integer.valueOf(filterTrips.getmCost()),filterTrips.getmCurrency())>=CurrencyHelper.currencyToPLN(event.getCost(),event.getCurrency()))){
            return false;
        }
        if(filterTrips.getmSports().contains("0") && event.checkWindsurfingAvailability()){
            return true;
        } else if(filterTrips.getmSports().contains("1") && event.checkKitesurfingAvailability()){
            return true;
        } else if(filterTrips.getmSports().contains("2") && event.checkSurfingAvailability()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Make coverages firebase query. To download only coverages (without trips).
     * @return new Query ref
     */
    private Query checkFiltersOnCoverageDatabaseReference(){
        Query eventsDatabaseReferenceWithFilters = mEventsDatabaseReference;
        eventsDatabaseReferenceWithFilters = eventsDatabaseReferenceWithFilters.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_EVENTS_START_DATE).equalTo("DEFAULT");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mLayoutManager);
        return eventsDatabaseReferenceWithFilters;
    }

    /**
     * Make trips firebase query, to display only first 20 records - to download less data.
     * @return new Query ref
     */
    private Query checkFiltersOnTripsDatabaseReference() {
        //Load all filters from SharedPreferences
        FilterTrips filterTrips = new FilterTrips();
        filterTrips.getFilterTripsPreferences();
        Query eventsDatabaseReferenceWithFilters = mEventsDatabaseReference;

        //FilterTrips sorting and filter by start date
        if (filterTrips.getmSortingPreferences() == FilterTripsContract.FilterTripsEntry.SORTING_DATE) {
            eventsDatabaseReferenceWithFilters = eventsDatabaseReferenceWithFilters.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_EVENTS_TIMESTAMP_START_DATE).startAt(filterTrips.getmDateFromTimestamp());
            if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_INCREASE) {
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
            else if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_DECREASE) {
                ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
                ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        //Only filter trips sorting because costs have different currencies
        else if (filterTrips.getmSortingPreferences() == FilterTripsContract.FilterTripsEntry.SORTING_COST) {
            eventsDatabaseReferenceWithFilters = eventsDatabaseReferenceWithFilters.orderByChild(FirebaseContract.FirebaseEntry.COLUMN_EVENTS_COST);
            if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_INCREASE) {
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
            else if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_DECREASE) {
                ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
                ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
        } else {
            if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_INCREASE) {
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
            else if (filterTrips.getmSortingOrderPreferences() == FilterTripsContract.FilterTripsEntry.ORDER_DECREASE) {
                ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
                ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            } else {
                mEventRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        return eventsDatabaseReferenceWithFilters;
    }

    /** Double click back to app exit method */
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.toast_double_click_back_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}