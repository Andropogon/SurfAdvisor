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
import android.support.v4.app.NavUtils;
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
import livewind.example.andro.liveWind.firebase.FirebaseHelp;
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

public class EventActivity extends AppCompatActivity {

    private TextView mPlaceTextView;
    private TextView mDateTextView;
    private TextView mWaveSizeTextView;
    private TextView mWindPowerTextView;
    private TextView mWindPowerUnitTextView;
    private TextView mConditionsTextView;
    private ImageView mPhotoImageView;
    private ImageView mNoPhotoImageView;
    private ImageView mCountryImageView;

    /**
     * FOR ADDING MEMBERS
     */
    private Windsurfer mWindsurfer = new Windsurfer();

    private ListView mMemberListView;
    private MemberAdapter mMemberAdapter;
    final List<MyMember> myMembers = new ArrayList<>();
    private TextView mMemberNumberTextView;

    /**
     * FOR THANKS
     */
    private DatabaseReference mThanksDatabaseReference;
    private DatabaseReference mThanksSizeDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private TextView mThanksNumberTextView;
    final List<MyMember> myThanks = new ArrayList<>();
    ChildEventListener mChildThanksListener;
    /**
     * FOR EDITING EVENTS
     */
    private Event mEvent;
    private DatabaseReference mEventsDatabaseReference;
    ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();
    FirebaseHelp mFirebaseHelp = new FirebaseHelp();

    /**
     * FIREBASE
     */
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mMembersDatabaseReference;
    ChildEventListener mChildMemberListener;
    private DatabaseReference mCurrentTimeReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mEventsStorageReference;
    private DatabaseReference mSharesDatabaseReference;

    /**
     * COMMENTS
     */
    private DatabaseReference mCommentsDatabaseReference;
    private ListView mCommentsListView;
    final List<Comment> myComments = new ArrayList<>();
    ChildEventListener mChildCommentListener;
    private CommentAdapter mCommentAdapter;
    private TextView hideAllCommentsTextView;
    private TextView showAllCommentsTextView;
    private TextView mCommentsTitleTextView;

    /**
     * SHARING
     */
    private String defaultPhotoUrl = "http://surf-advisor.info/";
    private String countryEmoji = "";
    private TextView mSharesNumberTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_event);


        final Intent intent = getIntent();
        //  getLoaderManager().initLoader(EXISTING_EVENT_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
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
        mSharesNumberTextView = findViewById(R.id.event_shares_number_text_view);

        //Load event data
        mEvent = new Event();
        getInfoFromIntent(intent, mEvent, getApplicationContext());
        //FIREBASE
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
        //For deleting photos with relation
        mFirebaseStorage = FirebaseStorage.getInstance();
        mEventsStorageReference = mFirebaseStorage.getReference().child("events_photos");
        getWindsurferFromIntent(intent,mWindsurfer, getApplicationContext());
        if(mWindsurfer.getUsername().equals("NOTIFICATION")){
            UserHelper.downloadUserData(mWindsurfer,EventActivity.this);
        }
        // Update the views on the screen with the values from the database
        mPlaceTextView.setText(mEvent.getPlace());
        mSharesNumberTextView.setText(Integer.toString(mEvent.getmSharesCounter()));
        setEventDurationOnDateTextView(mEvent,mDateTextView);
        //mDateTextView.setText(mEvent.getDate());
        /**
         *  COMMENTS
         */
        mCommentsDatabaseReference = mFirebaseDatabase.getReference().child("events").child(mEvent.getId()).child("mUsersComments");
        mCommentsListView = (ListView) findViewById(R.id.event_comments_list);
        mCommentAdapter = new CommentAdapter(this, myComments, 0);
        mCommentsListView.setAdapter(mCommentAdapter);

        /** Color of icon according to wind power */
        int windPowerInBft = 0;
        double windPowerInSailSize = 0;
        if (mEvent.getWindPower() >= 0 && mEvent.getWindPower() <= 3) {
            windPowerInBft=1;
            windPowerInSailSize=13;
        } else if (mEvent.getWindPower() >= 4 && mEvent.getWindPower() <= 6) {
            windPowerInBft=2;
            windPowerInSailSize=12.5;
        } else if (mEvent.getWindPower() >= 7 && mEvent.getWindPower() <= 10) {
            windPowerInBft=3;
            windPowerInSailSize=11.5;
        } else if (mEvent.getWindPower() >= 11 && mEvent.getWindPower() <= 13) {
            windPowerInBft=4;
            windPowerInSailSize=9.5;
        } else if (mEvent.getWindPower() >= 14 && mEvent.getWindPower() <= 16) {
            windPowerInBft=4;
            windPowerInSailSize=7.5;
        }else if (mEvent.getWindPower() >= 17 && mEvent.getWindPower() <= 19) {
            windPowerInBft=5;
            windPowerInSailSize=6.0;
        } else if (mEvent.getWindPower() >= 20 && mEvent.getWindPower() <= 21) {
            windPowerInBft=5;
            windPowerInSailSize=5.3;
        } else if (mEvent.getWindPower() >= 22 && mEvent.getWindPower() <= 24) {
            windPowerInBft=6;
            windPowerInSailSize=4.8;
        } else if (mEvent.getWindPower() >= 25 && mEvent.getWindPower() <= 27) {
            windPowerInBft=6;
            windPowerInSailSize=4.2;
        } else if (mEvent.getWindPower() >= 28 && mEvent.getWindPower() <= 30) {
            windPowerInBft=7;
            windPowerInSailSize=3.7;
        } else if (mEvent.getWindPower() >= 31 && mEvent.getWindPower() <= 33) {
            windPowerInBft=7;
            windPowerInSailSize=3.5;
        } else if (mEvent.getWindPower() >= 34 && mEvent.getWindPower() <= 36) {
            windPowerInBft=8;
            windPowerInSailSize=3.3;
        } else if (mEvent.getWindPower() >= 37 && mEvent.getWindPower() <= 40) {
            windPowerInBft=8;
            windPowerInSailSize=3.0;
        } else if (mEvent.getWindPower() >= 41 && mEvent.getWindPower() <= 47) {
            windPowerInBft=9;
            windPowerInSailSize=3.0;
        }else if (mEvent.getWindPower() >= 48 && mEvent.getWindPower() <= 55) {
            windPowerInBft=10;
            windPowerInSailSize=3.0;
        }else if (mEvent.getWindPower() >= 56 && mEvent.getWindPower() <= 63) {
            windPowerInBft=11;
            windPowerInSailSize=3.0;
        }else if (mEvent.getWindPower() >= 64) {
            windPowerInBft=12;
            windPowerInSailSize=-1.0;
        } else {
        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int windPowerUnit = Integer.parseInt(sharedPrefs.getString(getApplicationContext().getString(R.string.settings_display_wind_power_key),"1"));
        switch (windPowerUnit){
            case EventContract.EventEntry.UNIT_KNOTS:
                mWindPowerUnitTextView.setText(R.string.unit_wind_kn);
                mWindPowerTextView.setText(Integer.toString(mEvent.getWindPower()));
                break;
            case EventContract.EventEntry.UNIT_BEAUFORT:
                mWindPowerUnitTextView.setText(R.string.unit_wind_bft);
                mWindPowerTextView.setText(Integer.toString(windPowerInBft));
                break;
            case EventContract.EventEntry.UNIT_SAILS_SIZE:
                mWindPowerUnitTextView.setText(R.string.unit_wind_sail_size);
                mWindPowerTextView.setText(Double.toString(windPowerInSailSize));
                break;
            default:
                mWindPowerUnitTextView.setText(R.string.unit_wind_kn);
                mWindPowerTextView.setText(Double.toString(windPowerInSailSize));
                break;
        }
        mWaveSizeTextView.setText(Double.toString(mEvent.getWaveSize()));

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
            //mPhotoImageView.setImageResource(R.drawable.event_activity_no_photo);
        }


        //MEMBERS
        mMemberListView = (ListView) findViewById(livewind.example.andro.liveWind.R.id.members_list_view);
        // Initialize events ListView and its adapter

        mMemberAdapter = new MemberAdapter(this, myMembers, 0);
        mMemberListView.setAdapter(mMemberAdapter);
        attachDatabaseReadListener();

        mMemberNumberTextView.setText(Integer.toString(myMembers.size()));
        // Setup FAB to open EventActivity to join an event
        FloatingActionButton joinFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.join_event_fab);
        joinFAB.setOnClickListener(new View.OnClickListener() {
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
        //Setup FAB THANKS to thank for add event

        mThanksNumberTextView.setText(Integer.toString(myThanks.size()));
        FloatingActionButton thanksFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.thanks_event_fab);
        thanksFAB.setOnClickListener(new View.OnClickListener() {
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

        FloatingActionButton commentFAB = (FloatingActionButton) findViewById(livewind.example.andro.liveWind.R.id.comment_event_fab);
        commentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCommentDialog();
            }
        });



    }

    private void attachDatabaseReadListener() {
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
                //SHARING TO FACEBOOK
                if(!mEvent.getPhotoUrl().isEmpty()){
                    defaultPhotoUrl=mEvent.getPhotoUrl();
                }
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(defaultPhotoUrl))
                        .setQuote(quoteToShareFB)
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
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, quoteToShareM);
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
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, quoteToShareM);
                    sendIntent.setType("text/plain");
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //sendIntent.setPackage("com.facebook.orca");
                    sharesPlusOne();
                    startActivity(Intent.createChooser(sendIntent,getString(R.string.share)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.toast_share_other_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                NavUtils.navigateUpFromSameTask(EventActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
     * Perform the deletion of the pet in the database.
     */
    private void deleteEvent() {
        // Only perform the delete if this is an existing event.
        if (mEvent.getId() != null) {
            if(!mEvent.getPhotoUrl().equals("")) {
                StorageReference ref = mFirebaseStorage.getReferenceFromUrl(mEvent.getPhotoUrl());
                ref.delete();
            }
            mFirebaseHelp.removePoints(mEvent.getmUserUId(), EventContract.EventEntry.IT_IS_EVENT);
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

    private void editEvent() {
        Intent intent = new Intent(EventActivity.this, EditorActivity.class);
        mExtraInfoHelp.putInfoToIntent(intent, mEvent, mWindsurfer.getUsername(), getApplicationContext());
        mExtraInfoHelp.putWindsurferToIntent(intent, mWindsurfer, getApplicationContext());
        // Launch the {@link EditorActivity} to display the data for the current event
        startActivity(intent);
    }

    /**
     * I THINK THAT I NEED CLOUD FUNCTIONS :(
     * public void showThanksNotification(){
     * // Create an explicit intent for an Activity in your app
     * Intent notificationIntent = new Intent(this, CatalogActivity.class);
     * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
     * PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
     * <p>
     * final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
     * .setSmallIcon(R.drawable.windsurfing_icon)
     * .setContentTitle("Someone thanks you!")
     * .setContentText("Forecast added by you in "+mEvent.getPlace()+" gets new thanks.")
     * .setPriority(NotificationCompat.PRIORITY_DEFAULT)
     * // Set the intent that will fire when the user taps the notification
     * .setContentIntent(pendingIntent)
     * .setAutoCancel(true);
     * <p>
     * NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     * notificationManager.notify(123, mBuilder.build());
     * }
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
       // mCurrentTimeReference.addListenerForSingleValueEvent(listener);
    }

    // Show select photo action
    private void showAddCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_event_comment_dialog,null);

        final EditText commentEditText = dialogView.findViewById(R.id.activity_event_dialog_add_comment_edit_text);
        builder.setView(dialogView)
                //builder.setView(gridView)
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
                                        //ListViewHelp.setListViewHeightBasedOnChildren(mCommentsListView);
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
                                        //Toast.makeText(EventActivity.this, "KOMENTARZ PODWÓJNY", Toast.LENGTH_SHORT).show();
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
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }



    //Add points for created an event
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

}