package livewind.example.andro.liveWind;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import livewind.example.andro.liveWind.Comments.Comment;
import livewind.example.andro.liveWind.Comments.CommentAdapter;
import livewind.example.andro.liveWind.ListView_help.ListViewHelp;
import livewind.example.andro.liveWind.Notifications.MyFirebaseMessagingService;
import livewind.example.andro.liveWind.user.UserHelper;
import livewind.example.andro.liveWind.user.Windsurfer;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livewind.example.andro.liveWind.data.EventContract;

import static livewind.example.andro.liveWind.ExtraInfoHelp.getInfoFromIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.getWindsurferFromIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putInfoToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;
import static livewind.example.andro.liveWind.firebase.FirebaseHelp.removePoints;

/**
 * Created by JGJ on 10/10/18.
 * Header added during refactoring add 10/04/2019 by JGJ.
 *
 * Display single coverage and give users possibility to like, comment and "join it" it.
 *
 */
public class EventActivity extends AppCompatActivity {

    /** Views **/
    //Event Views
    private TextView mPlaceTextView;
    private TextView mDateTextView;
    private TextView mWaveSizeTextView;
    private TextView mWindPowerTextView;
    private TextView mWindPowerUnitTextView;
    private TextView mConditionsTextView;
    private ImageView mPhotoImageView;
    private ImageView mNoPhotoImageView;
    private ImageView mCountryImageView;
    //"Joint it" views
    private TextView mMemberNumberTextView;
    private ListView mMemberListView;
    //"Thanks / Like" views
    private TextView mThanksNumberTextView;
    //"Comment" views
    private ListView mCommentsListView;
    private TextView hideAllCommentsTextView;
    private TextView showAllCommentsTextView;
    private TextView mCommentsTitleTextView;
    //"Share" views
    private TextView mSharesNumberTextView;
    //FABs
    private FloatingActionButton mJoinFAB;
    private FloatingActionButton mThanksFAB;
    private FloatingActionButton mCommentFAB;
    //EmptyView -> Coverage isn't exist yet (for deleted coverages opened from notification)
    private TextView mDeletedCoverageInfoTextView;

    /** Current user and event data **/
    private Event mEvent;
    private Windsurfer mWindsurfer = new Windsurfer();

    /** FIREBASE **/
    //TODO Add dbHelper and Contract and clean it...
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mMembersDatabaseReference;
    private DatabaseReference mEventsDatabaseReference;
    private DatabaseReference mThanksDatabaseReference;
    private DatabaseReference mThanksSizeDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildThanksListener;
    private ChildEventListener mChildMemberListener;
    private ChildEventListener mChildCommentListener;
    private DatabaseReference mCurrentTimeReference;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mSharesDatabaseReference;
    private DatabaseReference mCommentsDatabaseReference;

    /** "JOIN IT" Option */
    private final List<MyMember> myMembers = new ArrayList<>();
    private MemberAdapter mMemberAdapter;

    /** "Thanks / Like" Option */
    private final List<MyMember> myThanks = new ArrayList<>();

    /** "Comment" Option */
    private final List<Comment> myComments = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    /** "Share" Option */
    private String defaultPhotoUrl = "http://surf-advisor.info/";
    private String countryEmoji = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_event);
        initViews();
        //Load coverage data from intent
        final Intent intent = getIntent();
        mEvent = new Event();
        getInfoFromIntent(intent, mEvent, getApplicationContext());

        //Firebase
        initFirebaseVariables();

        //Init adapters and set them to listViews
        mCommentAdapter = new CommentAdapter(this, myComments, 0);
        mCommentsListView.setAdapter(mCommentAdapter);
        mMemberAdapter = new MemberAdapter(this, myMembers, 0);
        mMemberListView.setAdapter(mMemberAdapter);

        displayCoverage();
        attachDatabaseReadListener();

        //Load user data from intent or from firebase
        getWindsurferFromIntent(intent,mWindsurfer, getApplicationContext());
        if(mWindsurfer.getUsername().equals(MyFirebaseMessagingService.NOTIFICATION_NEW_COVERAGE)){
            //If this activity is open from notification we have to download data from firebase
            UserHelper.downloadUserData(mWindsurfer,EventActivity.this);
            checkEventExist(); // Check that event still exist -> if exist initialize FABs
        } else {
            setupFABs();
        }

    }

    /**
     * Init methods
     */
    private void initViews() {
        mPlaceTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_place_text_view);
        mDateTextView = (TextView) findViewById(R.id.event_date_text_view);
        mWindPowerTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_wind_power_text_view);
        mWaveSizeTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_wave_size_text_view);
        mConditionsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_conditions_text_view);
        mPhotoImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.main_photo_image_view);
        mNoPhotoImageView = findViewById(R.id.no_photo_image_view);
        mThanksNumberTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_thanks_number_text_view);
        mMemberNumberTextView = (TextView) findViewById(R.id.event_members_number_text_view);
        mCountryImageView = (ImageView) findViewById(R.id.event_flag_image_view);
        mWindPowerUnitTextView = (TextView) findViewById(R.id.event_wind_power_unit_text_view);
        mCommentsTitleTextView = findViewById(R.id.event_activity_comments_title);
        mCommentsListView = (ListView) findViewById(R.id.event_comments_list);
        mSharesNumberTextView = findViewById(R.id.event_shares_number_text_view);
        mMemberListView = (ListView) findViewById(livewind.example.andro.liveWind.R.id.members_list_view);
        mJoinFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.join_event_fab);
        mThanksFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.thanks_event_fab);
        mDeletedCoverageInfoTextView = findViewById(R.id.event_activity_coverage_no_exist);
    }
    private void initFirebaseVariables() {
        //For adding members
        mMembersDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mMembers");
        //For thanks
        mThanksDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mThanks");
        mThanksSizeDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mThanksSize");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        //For deleting events
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child("events");
        mSharesDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mSharesCounter");
        mCurrentTimeReference = mFirebaseDatabase.getReference().child("currentTime");
        //For comments
        mCommentsDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mUsersComments");

        //For deleting photos with relation
        mFirebaseStorage = FirebaseStorage.getInstance();
    }

    /**
     * Display coverage using data from mEvent
     */
    private void displayCoverage(){
        mPlaceTextView.setText(mEvent.getPlace());
        mSharesNumberTextView.setText(Integer.toString(mEvent.getmSharesCounter()));
        setEventDurationOnDateTextView(mEvent,mDateTextView);
        //Wind power and wind unit
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int windPowerUnit = Integer.parseInt(sharedPrefs.getString(getApplicationContext().getString(R.string.settings_display_wind_power_key),"1"));
        switch (windPowerUnit){
            case EventContract.EventEntry.UNIT_KNOTS:
                mWindPowerUnitTextView.setText(R.string.unit_wind_kn);
                mWindPowerTextView.setText(Integer.toString(mEvent.getWindPower()));
                break;
            case EventContract.EventEntry.UNIT_BEAUFORT:
                mWindPowerUnitTextView.setText(R.string.unit_wind_bft);
                mWindPowerTextView.setText(Integer.toString(Event.knotsToBft(mEvent.getWindPower())));
                break;
            case EventContract.EventEntry.UNIT_SAILS_SIZE:
                mWindPowerUnitTextView.setText(R.string.unit_wind_sail_size);
                mWindPowerTextView.setText(Double.toString(Event.knotsToSailSize(mEvent.getWindPower())));
                break;
            default:
                mWindPowerUnitTextView.setText(R.string.unit_wind_kn);
                mWindPowerTextView.setText(Integer.toString(mEvent.getWindPower()));
                break;
        }
        //Wave size
        mWaveSizeTextView.setText(Double.toString(mEvent.getWaveSize()));

        //Wind direction:
        switch (mEvent.getConditions()) {
            case EventContract.EventEntry.CONDITIONS_ONSHORE:
                mConditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_onshore);
                break;
            case EventContract.EventEntry.CONDITIONS_SIDESHORE:
                mConditionsTextView.setText(R.string.conditions_sideshore);
                break;
            case EventContract.EventEntry.CONDITIONS_OFFSHORE:
                mConditionsTextView.setText(R.string.conditions_offshore);
                break;
            case EventContract.EventEntry.CONDITIONS_N:
                mConditionsTextView.setText(R.string.conditions_north);
                break;
            case EventContract.EventEntry.CONDITIONS_NNE:
                mConditionsTextView.setText(R.string.conditions_NNE);
                break;
            case EventContract.EventEntry.CONDITIONS_NE:
                mConditionsTextView.setText(R.string.conditions_north_east);
                break;
            case EventContract.EventEntry.CONDITIONS_ENE:
                mConditionsTextView.setText(R.string.conditions_ENE);
                break;
            case EventContract.EventEntry.CONDITIONS_E:
                mConditionsTextView.setText(R.string.conditions_east);
                break;
            case EventContract.EventEntry.CONDITIONS_ESE:
                mConditionsTextView.setText(R.string.conditions_ESE);
                break;
            case EventContract.EventEntry.CONDITIONS_SE:
                mConditionsTextView.setText(R.string.conditions_south_east);
                break;
            case EventContract.EventEntry.CONDITIONS_SSE:
                mConditionsTextView.setText(R.string.conditions_SSE);
                break;
            case EventContract.EventEntry.CONDITIONS_S:
                mConditionsTextView.setText(R.string.conditions_south);
                break;
            case EventContract.EventEntry.CONDITIONS_SSW:
                mConditionsTextView.setText(R.string.conditions_SSW);
                break;
            case EventContract.EventEntry.CONDITIONS_WSW:
                mConditionsTextView.setText(R.string.conditions_WSW);
                break;
            case EventContract.EventEntry.CONDITIONS_SW:
                mConditionsTextView.setText(R.string.conditions_south_west);
                break;
            case EventContract.EventEntry.CONDITIONS_WNW:
                mConditionsTextView.setText(R.string.conditions_WNW);
                break;
            case EventContract.EventEntry.CONDITIONS_NW:
                mConditionsTextView.setText(R.string.conditions_north_west);
                break;
            case EventContract.EventEntry.CONDITIONS_NNW:
                mConditionsTextView.setText(R.string.conditions_NNW);
                break;
            default:
                mConditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_onshore);
                break;
        }

        //Country
        switch(mEvent.getCountry()) {
            case EventContract.EventEntry.COUNTRY_WORLD:
                mCountryImageView.setImageResource(R.drawable.flag_world);
                break;
            case EventContract.EventEntry.COUNTRY_POLAND:
                mCountryImageView.setImageResource(R.drawable.flag_pl);
                countryEmoji = getEmojiByUnicode(0x1F1F5 )+getEmojiByUnicode(0x1F1F1);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                mCountryImageView.setImageResource(R.drawable.flag_gr);
                countryEmoji = getEmojiByUnicode(0x1F1EC)+getEmojiByUnicode(0x1F1F7	);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                mCountryImageView.setImageResource(R.drawable.flag_es);
                countryEmoji = getEmojiByUnicode(0x1F1EA)+getEmojiByUnicode(0x1F1F8);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                mCountryImageView.setImageResource(R.drawable.flag_hr);
                countryEmoji = getEmojiByUnicode(0x1F1ED )+getEmojiByUnicode(0x1F1F7);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                mCountryImageView.setImageResource(R.drawable.flag_pt);
                countryEmoji = getEmojiByUnicode(0x1F1F5 )+getEmojiByUnicode(0x1F1F9);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                countryEmoji = getEmojiByUnicode(0x1F1E9  )+getEmojiByUnicode(0x1F1EA);
                mCountryImageView.setImageResource(R.drawable.flag_de);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                countryEmoji = getEmojiByUnicode(0x1F1EB  )+getEmojiByUnicode(0x1F1F7);
                mCountryImageView.setImageResource(R.drawable.flag_fr);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                countryEmoji = getEmojiByUnicode(0x1F1FF  )+getEmojiByUnicode(0x1F1E6);
                mCountryImageView.setImageResource(R.drawable.flag_za);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                countryEmoji = getEmojiByUnicode(0x1F1F2  )+getEmojiByUnicode(0x1F1E6);
                mCountryImageView.setImageResource(R.drawable.flag_ma);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                countryEmoji = getEmojiByUnicode(0x1F1EE  )+getEmojiByUnicode(0x1F1F9);
                mCountryImageView.setImageResource(R.drawable.flag_it);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                countryEmoji = getEmojiByUnicode(0x1F1EA  )+getEmojiByUnicode(0x1F1EC);
                mCountryImageView.setImageResource(R.drawable.flag_eg);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                countryEmoji = getEmojiByUnicode(0x1F1EC  )+getEmojiByUnicode(0x1F1E7);
                mCountryImageView.setImageResource(R.drawable.flag_uk);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                countryEmoji = getEmojiByUnicode(0x1F1F9  )+getEmojiByUnicode(0x1F1F7);
                mCountryImageView.setImageResource(R.drawable.flag_tr);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                countryEmoji = getEmojiByUnicode(0x1F1E6  )+getEmojiByUnicode(0x1F1F9);
                mCountryImageView.setImageResource(R.drawable.flag_at);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                countryEmoji = getEmojiByUnicode(0x1F1E9  )+getEmojiByUnicode(0x1F1F0);
                mCountryImageView.setImageResource(R.drawable.flag_dk);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                countryEmoji = getEmojiByUnicode(0x1F1E7   )+getEmojiByUnicode(0x1F1F7);
                mCountryImageView.setImageResource(R.drawable.flag_br);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                countryEmoji = getEmojiByUnicode(0x1F1FA   )+getEmojiByUnicode(0x1F1F8	);
                mCountryImageView.setImageResource(R.drawable.flag_us);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                countryEmoji = getEmojiByUnicode(0x1F1FB   )+getEmojiByUnicode(0x1F1F3);
                mCountryImageView.setImageResource(R.drawable.flag_vn);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                countryEmoji = getEmojiByUnicode(0x1F1F2   )+getEmojiByUnicode(0x1F1F9);
                mCountryImageView.setImageResource(R.drawable.flag_mt);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                mCountryImageView.setImageResource(R.drawable.flag_world);
                break;
            default:
                mCountryImageView.setImageResource(R.drawable.flag_world);
                break;
        }

        //Coverage photo
        loadPhoto();

        //Join, likes, shares counters:
        mMemberNumberTextView.setText(Integer.toString(myMembers.size()));
        mThanksNumberTextView.setText(Integer.toString(myThanks.size()));
        //Comments
        setupCommentsViews();
    }
    private void loadPhoto(){
        final ProgressBar progressBar = (ProgressBar) findViewById(livewind.example.andro.liveWind.R.id.progress);
        if(!mEvent.getPhotoUrl().isEmpty()) {
            Glide.with(mPhotoImageView.getContext())
                    .load(mEvent.getPhotoUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mNoPhotoImageView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mNoPhotoImageView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mPhotoImageView);
        } else {
            mNoPhotoImageView.setVisibility(View.VISIBLE);
            mPhotoImageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
    private void setupCommentsViews(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 16, 48, 16);
        mCommentsListView.setLayoutParams(lp);
        hideAllCommentsTextView = findViewById(R.id.event_hide_all_comments_text_view);
        hideAllCommentsTextView.setVisibility(View.GONE);
        showAllCommentsTextView = findViewById(R.id.event_show_all_comments_text_view);
        if(mCommentAdapter.isEmpty()){
            mCommentsTitleTextView.setVisibility(View.GONE);
            showAllCommentsTextView.setVisibility(View.GONE);
            ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
        } else if (mCommentAdapter.getCount()==1) {
            mCommentsTitleTextView.setVisibility(View.VISIBLE);
            showAllCommentsTextView.setVisibility(View.GONE);
            ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
        } else {
            mCommentsTitleTextView.setVisibility(View.VISIBLE);
            showAllCommentsTextView.setVisibility(View.VISIBLE);
        }
        showAllCommentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAllCommentsTextView.setVisibility(View.VISIBLE);
                showAllCommentsTextView.setVisibility(View.GONE);
                ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
            }
        });
        hideAllCommentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAllCommentsTextView.setVisibility(View.GONE);
                showAllCommentsTextView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(48, 16, 48, 16);
                mCommentsListView.setLayoutParams(lp);
            }
        });
    }
    private void setupFABs(){
        mJoinFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Query query = mMembersDatabaseReference.orderByChild("mName").equalTo(mWindsurfer.getUsername());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            //Type = 1 because creator should be known
                            MyMember joinMember = new MyMember(mWindsurfer.getUsername(), 0, mWindsurfer.getPhotoName(), EventActivity.this);
                            mMemberAdapter.add(joinMember);
                            mMembersDatabaseReference.setValue(myMembers);
                            ListViewHelp.setListViewHeightBasedOnChildren(mMemberListView);
                            mMemberNumberTextView.setText(Integer.toString(myMembers.size()));
                            Toast.makeText(EventActivity.this, getString(R.string.toast_relation_joined), Toast.LENGTH_SHORT).show();
                            mMembersDatabaseReference.removeEventListener(mChildMemberListener);
                            query.removeEventListener(this);

                        } else {
                            Toast.makeText(EventActivity.this, getString(R.string.toast_relation_joined_fail), Toast.LENGTH_SHORT).show();
                            query.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


        mThanksFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Query query = mThanksDatabaseReference.orderByChild("mName").equalTo(mWindsurfer.getUsername());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            MyMember thanksMember = new MyMember(mWindsurfer.getUsername(), 0);
                            myThanks.add(thanksMember);
                            mThanksDatabaseReference.setValue(myThanks);
                            mThanksSizeDatabaseReference.setValue(myThanks.size());
                            mThanksNumberTextView.setText(Integer.toString(myThanks.size()));
                            Toast.makeText(EventActivity.this, getString(R.string.toast_relation_like), Toast.LENGTH_SHORT).show();
                            mThanksDatabaseReference.removeEventListener(mChildThanksListener);
                            query.removeEventListener(this);
                            //Give one point to user who liked event
                            mUserDatabaseReference.child(mWindsurfer.getUid()).child("points").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Integer points = mutableData.getValue(Integer.class);
                                    if (points == null || points < 0) {
                                        mutableData.setValue(0);
                                    } else {
                                        mutableData.setValue(points + 1);
                                    }

                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(
                                        DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                    System.out.println("Transaction completed");
                                }
                            });
                        } else {
                            Toast.makeText(EventActivity.this, getString(R.string.toast_relation_like_fail), Toast.LENGTH_SHORT).show();
                            query.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        mCommentFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.comment_event_fab);
        mCommentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCommentDialog();
            }
        });
    }
    private void attachDatabaseReadListener() {
        //TODO clean it...
        if (mChildMemberListener == null) {
            mChildMemberListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MyMember myMember = dataSnapshot.getValue(MyMember.class);
                    mMemberAdapter.add(myMember);
                    ListViewHelp.setListViewHeightBasedOnChildren(mMemberListView);
                    mMemberNumberTextView.setText(Integer.toString(myMembers.size()));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mMembersDatabaseReference.addChildEventListener(mChildMemberListener);
        }
        if (mChildThanksListener == null) {
            mChildThanksListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MyMember singleThanks = dataSnapshot.getValue(MyMember.class);
                    myThanks.add(singleThanks);
                    mThanksNumberTextView.setText(Integer.toString(myThanks.size()));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mThanksDatabaseReference.addChildEventListener(mChildThanksListener);
        }
        if (mChildCommentListener == null) {
            mChildCommentListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Comment singleComment = dataSnapshot.getValue(Comment.class);
                    //ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
                    mCommentAdapter.add(singleComment);
                    mCommentAdapter.sort();
                    if(mCommentAdapter.isEmpty()){
                        mCommentsTitleTextView.setVisibility(View.GONE);
                        showAllCommentsTextView.setVisibility(View.GONE);
                        ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
                    } else if (mCommentAdapter.getCount()==1) {
                        mCommentsTitleTextView.setVisibility(View.VISIBLE);
                        showAllCommentsTextView.setVisibility(View.GONE);
                        ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
                    } else {
                        mCommentsTitleTextView.setVisibility(View.VISIBLE);
                        showAllCommentsTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mCommentsDatabaseReference.addChildEventListener(mChildCommentListener);
        }
    }

    /**
     * ************************ MENU ***************************
     */

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (!(mEvent.getmUsername().equals(mWindsurfer.getUsername()))) {
            MenuItem menuItemDelete = menu.findItem(livewind.example.andro.liveWind.R.id.action_delete);
            menuItemDelete.setVisible(false);
            MenuItem menuItemEdit = menu.findItem(livewind.example.andro.liveWind.R.id.action_edit);
            menuItemEdit.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(livewind.example.andro.liveWind.R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Windsurfer clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_edit:
                // Open event editor
                editEvent();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case livewind.example.andro.liveWind.R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_share_facebook:
                //SHARING TO FACEBOOK
                if(!mEvent.getPhotoUrl().isEmpty()){
                    defaultPhotoUrl=mEvent.getPhotoUrl();
                }
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(defaultPhotoUrl))
                        .setQuote(getShareToFbQuote())
                        .build();
                ShareDialog share = new ShareDialog(EventActivity.this);
                if(share.canShow(content,ShareDialog.Mode.NATIVE)) {
                    sharesPlusOne();
                    share.show(content, ShareDialog.Mode.NATIVE);
                } else if(share.canShow(content,ShareDialog.Mode.AUTOMATIC)) {
                    sharesPlusOne();
                    share.show(content, ShareDialog.Mode.AUTOMATIC);
                } else if(share.canShow(content,ShareDialog.Mode.WEB)){
                    sharesPlusOne();
                    share.show(content, ShareDialog.Mode.WEB);
                } else {
                    Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_share_facebook_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share_messenger:
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getShareToMessengerQuote());
                    sendIntent.setType("text/plain");
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.setPackage("com.facebook.orca");
                    sharesPlusOne();
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_share_messenger_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share_other:
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getShareToMessengerQuote());
                    sendIntent.setType("text/plain");
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sharesPlusOne();
                    startActivity(Intent.createChooser(sendIntent,getString(R.string.share)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.toast_share_other_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                Intent intent = new Intent(EventActivity.this,CatalogActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventActivity.this,CatalogActivity.class);
        startActivity(intent);
        return;
    }


    /**
     * Prompt the user to confirm that they want to delete this event.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(livewind.example.andro.liveWind.R.string.delete_dialog_msg);
        builder.setPositiveButton(livewind.example.andro.liveWind.R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Delete" button, so delete the pet.
                deleteEvent();
            }
        });
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.cancel, new DialogInterface.OnClickListener() {
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

    /**
     * Perform the deletion of the coverage in the database - available only for creator
     */
    private void deleteEvent() {
        // Only perform the delete if this is an existing event.
        if (mEvent.getId() != null) {
            if(!mEvent.getPhotoUrl().equals("")) {
                StorageReference ref = mFirebaseStorage.getReferenceFromUrl(mEvent.getPhotoUrl());
                ref.delete();
            }
            removePoints(mEvent.getmUserUId(), EventContract.EventEntry.IT_IS_EVENT);
            mEventsDatabaseReference.child(mEvent.getId()).removeValue();

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

    /**
     * Edit coverage - available only for creator
     */
    private void editEvent() {
        Intent intent = new Intent(EventActivity.this, EditorActivity.class);
        putInfoToIntent(intent, mEvent, mWindsurfer.getUsername(), getApplicationContext());
        putWindsurferToIntent(intent, mWindsurfer, getApplicationContext());
        // Launch the {@link EditorActivity} to display the data for the current event
        startActivity(intent);
    }

    /**
     * Calculate and set event duration (current time is downloaded from firebase)
     * @param event - coverage from them we can get time of creation
     * @param view - view to set duration time
     */
    public void setEventDurationOnDateTextView(final Event event,final TextView view){
        mCurrentTimeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long eventDurationInMinutes = 0;
                    long currentTime = dataSnapshot.getValue(long.class);
                    long eventCreationTime = event.getTimestamp();
                    currentTime = currentTime - eventCreationTime;
                    eventDurationInMinutes = currentTime / 1000;
                    eventDurationInMinutes = eventDurationInMinutes / 60;
                    String eventDurationString = "";
                    if(eventDurationInMinutes<2){
                        eventDurationString=getApplicationContext().getString(R.string.event_activity_time_now);
                    } else if (eventDurationInMinutes<60){
                        eventDurationString=getApplicationContext().getString(R.string.event_activity_time_added) + Long.toString(eventDurationInMinutes)+getApplicationContext().getString(R.string.event_activity_time_min_ago);
                    } else if (eventDurationInMinutes>60){
                        int hours = 0;
                        while (eventDurationInMinutes>60){
                            hours++;
                            eventDurationInMinutes=eventDurationInMinutes-60;
                        }
                        eventDurationString=getApplicationContext().getString(R.string.event_activity_time_added) +Integer.toString(hours)+ getApplicationContext().getString(R.string.event_activity_time_h_and)+Long.toString(eventDurationInMinutes) +getApplicationContext().getString(R.string.event_activity_time_min_ago);
                    } else {
                        eventDurationString = getApplicationContext().getString(R.string.event_activity_time_unknown);
                    }
                    view.setText(eventDurationString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    /**
     * Method for creation comment dialog
     */
    private void showAddCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_event_comment_dialog,null);

        final EditText commentEditText = dialogView.findViewById(R.id.activity_event_dialog_add_comment_edit_text);
        builder.setView(dialogView)
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        final String commentText = commentEditText.getText().toString().trim();
                        if(!commentText.isEmpty()) {
                            final Query query = mCommentsDatabaseReference.orderByChild("commentNumber").equalTo(myComments.size());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        String id = mCommentsDatabaseReference.push().getKey();

                                        Comment newComment = new Comment(mWindsurfer.getUsername(), mWindsurfer.getPhotoName(), commentText, id);
                                        mCommentAdapter.add(newComment);
                                        mCommentAdapter.sort();
                                        mCommentsDatabaseReference.child(id).setValue(newComment);
                                        Toast.makeText(EventActivity.this, getString(R.string.event_activity_comment_added), Toast.LENGTH_SHORT).show();
                                        mCommentsDatabaseReference.removeEventListener(mChildCommentListener);
                                        query.removeEventListener(this);
                                        Map<String, Object> value = new HashMap<>();
                                        value.put("timestamp", ServerValue.TIMESTAMP);
                                        mCommentsDatabaseReference.child(newComment.getCommentNumber()).updateChildren(value);
                                        //Change comment displaying:
                                        if(mCommentAdapter.getCount()==1){
                                            mCommentsTitleTextView.setVisibility(View.VISIBLE);
                                            ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
                                        } else {
                                            mCommentsTitleTextView.setVisibility(View.VISIBLE);
                                            hideAllCommentsTextView.setVisibility(View.VISIBLE);
                                            showAllCommentsTextView.setVisibility(View.GONE);
                                            ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
                                        }
                                    } else {
                                        query.removeEventListener(this);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {
                            Toast.makeText(EventActivity.this, getString(R.string.event_activity_comment_empty), Toast.LENGTH_SHORT).show();
                            }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }

    /**
     * Get Emoji by them unicode
     * @param unicode - unicode of emoji
     * @return String that is emoji (if unicode is correct)
     */
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    /**
     * Make quote to share on facebook
     * @return quote to share on fb
     */
    private String getShareToFbQuote(){
        String quoteToShareFB =
                getEmojiByUnicode(0x1F30D)+
                        getResources().getString(R.string.share_quote_place)+mEvent.getPlace() +" "+ countryEmoji+ ":\n" +
                        getEmojiByUnicode(0x1F55B)+
                        getResources().getString(R.string.share_quote_time)+mDateTextView.getText().toString().trim()+ "\n" +
                        getEmojiByUnicode(0x1F4A8)+
                        getResources().getString(R.string.share_quote_wind)+mEvent.getWindPower()+ "kn\n" +
                        getEmojiByUnicode(0x1F30A)+
                        getResources().getString(R.string.share_quote_wave)+mEvent.getWaveSize()+ "m\n" +
                        getEmojiByUnicode(0x1F6A9)+
                        getResources().getString(R.string.share_quote_conditions)+mConditionsTextView.getText().toString()+ "\n" +
                        getEmojiByUnicode(0x1F4DD)+
                        getResources().getString(R.string.share_quote_comment)+mEvent.getComment()+ "\n\n" +
                        getEmojiByUnicode(0x1F3C4)+
                        getResources().getString(R.string.share_quote_foot);
        return quoteToShareFB;
    }
    /**
     * Make quote to share on messenger
     * @return quote to share on messenger
     */
    private String getShareToMessengerQuote(){
        String quoteToShareM =
                getEmojiByUnicode(0x1F30D)+
                        getResources().getString(R.string.share_quote_place)+mEvent.getPlace()+" "+ countryEmoji+ ":\n" +
                        getEmojiByUnicode(0x1F55B)+
                        getResources().getString(R.string.share_quote_time)+mDateTextView.getText().toString().trim()+ "\n" +
                        getEmojiByUnicode(0x1F4A8)+
                        getResources().getString(R.string.share_quote_wind)+mEvent.getWindPower()+ "kn\n" +
                        getEmojiByUnicode(0x1F30A)+
                        getResources().getString(R.string.share_quote_wave)+mEvent.getWaveSize()+ "m\n" +
                        getEmojiByUnicode(0x1F6A9)+
                        getResources().getString(R.string.share_quote_conditions)+mConditionsTextView.getText().toString()+ "\n" +
                        getEmojiByUnicode(0x1F4DD)+
                        getResources().getString(R.string.share_quote_comment)+mEvent.getComment()+ "\n";
        if(!mEvent.getPhotoUrl().isEmpty()){
            quoteToShareM = quoteToShareM + getEmojiByUnicode(0x1F4F7) + getResources().getString(R.string.share_quote_photo)+mEvent.getPhotoUrl() +"\n\n";
        }
        quoteToShareM = quoteToShareM +
                getEmojiByUnicode(0x1F3C4)+
                getResources().getString(R.string.share_quote_foot);
        return quoteToShareM;
    }

    /**
     * Change shares counter method
     */
    public void sharesPlusOne(){
        mSharesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int eventShares = dataSnapshot.getValue(int.class);
                    eventShares++;
                    mSharesDatabaseReference.setValue(eventShares);
                    mSharesNumberTextView.setText(Integer.toString(eventShares));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Method to handle coverages open from notification that doesn't exist now:
     * If coverage isn't exist in firebase method isn't initialize FABs and show Toast and special textView
     */

    public void checkEventExist() {
        mEventsDatabaseReference.child(mEvent.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEvent = dataSnapshot.getValue(Event.class);
                    setupFABs();
                    displayCoverage(); // Updated displayed coverage data
                } else {
                    mDeletedCoverageInfoTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Sorry, this coverage was deleted... HC", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}