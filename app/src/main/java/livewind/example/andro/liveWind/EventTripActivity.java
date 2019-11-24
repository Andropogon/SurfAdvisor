package livewind.example.andro.liveWind;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import livewind.example.andro.liveWind.ListView_help.ListViewHelp;
import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import livewind.example.andro.liveWind.user.Windsurfer;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import livewind.example.andro.liveWind.data.EventContract;

import static livewind.example.andro.liveWind.ExtraInfoHelp.getWindsurferFromIntent;

public class EventTripActivity extends AppCompatActivity {

    private ImageView mCountryImageView;
    private TextView mPlaceTextView;
    private TextView mDateTextView;
    private TextView mDateStartCampTextView;
    private ImageView mStartCountryImageView;
    private TextView mPlaceStartTextView;
    private TextView mDateStartTextView;
    private ImageView mWindsurfingAvailableImageView;
    private ImageView mKitesurfingAvailableImageView;
    private ImageView mSurfingAvailableImageView;
    private TextView mWindsurfingAvailableTextView;
    private TextView mKitesurfingAvailableTextView;
    private TextView mSurfingAvailableTextView;
    private TextView mCommentTextView;
    private ImageView mTransportImageView;
    private TextView mTransportTextView;
    private ImageView mCharacterImageView;
    private TextView mCharacterTextView;
    private TextView mCostTextView;
    private TextView mCostDiscountTextView;
    private ImageView mCostDiscountHelpImageView;
    private TextView mCurrencyDiscountTextView;
    private TextView mCurrencyTextView;
    private TextView mCostAboutTextView;
    private TextView mContactPhoneTextView;
    private ImageView mContactPhoneImageView;
    private TextView mContactEmailTextView;
    private ImageView mContactEmailImageView;
    private TextView mContactWebTextView;
    private ImageView mContactWebImageView;
    /**
     * FOR ADDING MEMBERS
     */

  //  private String yourUsername = ""
    private Windsurfer mWindsurfer = new Windsurfer();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMembersDatabaseReference;
    private ListView mMemberListView;
    private MemberAdapter mMemberAdapter;
    ChildEventListener mChildMemberListener;

    /**
     * FOR EDITING EVENTS
     */
    private Event mEvent;
    private DatabaseReference mEventsDatabaseReference;
    private ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();
    FirebaseHelp mFirebaseHelp = new FirebaseHelp();

    /**
     * SHARING
     */
    private String quoteToShare = "";
    private String defaultPhotoUrl = "http://surf-advisor.info/";
    private String avaiableSportsToQuote = "";
    private String countryEmoji = "";
    private String startCountryEmoji = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_trip_event);

        final Intent intent = getIntent();

        //  getLoaderManager().initLoader(EXISTING_EVENT_LOADER, null, this);
      //  yourUsername = intent.getStringExtra("EXTRA_MY_USERNAME");
        // Find all relevant views that we will need to read user input from
        mCountryImageView = findViewById(R.id.event_trip_country_image_view);
        mStartCountryImageView= findViewById(R.id.event_trip_start_country_image_view);
        mPlaceTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_place_text_view);
        mPlaceStartTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_start_place_text_view);
        mDateTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_date_text_view);
        mDateStartTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_start_date_text_view);
        mDateStartCampTextView = findViewById(R.id.event_trip_camp_start_date_text_view);
        mCommentTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_comment_text_view);
        mTransportImageView = findViewById(R.id.event_trip_transport_image_view);
        mTransportTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_transport_text_view);
        mCharacterImageView = findViewById(R.id.event_trip_character_image_view);
        mCharacterTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_character_text_view);
        mCostTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_cost_text_view);
        mCostDiscountTextView = findViewById(R.id.event_trip_discount_text_view);
        mCostDiscountHelpImageView = findViewById(R.id.event_trip_discount_help_image_view);
        mCostAboutTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_about_cost_text_view);
        mCurrencyTextView = findViewById(R.id.event_trip_currency_text_view);
        mCurrencyDiscountTextView = findViewById(R.id.event_trip_discount_currency_text_view);
        mContactPhoneTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_contact_phone_text_view);
        mContactPhoneImageView = findViewById(R.id.event_trip_contact_phone_image_view);
        mContactEmailTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_contact_email_text_view);
        mContactEmailImageView = findViewById(R.id.event_trip_contact_email_image_view);
        mContactWebTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.event_trip_contact_web_text_view);
        mContactWebImageView = findViewById(R.id.event_trip_contact_web_image_view);
        mWindsurfingAvailableImageView = findViewById(R.id.event_trip_icon_windsurfing_available_image_view);
        //mWindsurfingAvailableSmallImageView = findViewById(R.id.event_trip_icon_windsurfing);
        mWindsurfingAvailableTextView = findViewById(R.id.event_trip_icon_windsurfing_available_text_view);
        mKitesurfingAvailableImageView = findViewById(R.id.event_trip_icon_kitesurfing_available_image_view);
        //mKitesurfingAvailableSmallImageView = findViewById(R.id.event_trip_icon_kitesurfing);
        mKitesurfingAvailableTextView = findViewById(R.id.event_trip_icon_kitesurfing_available_text_view);
        mSurfingAvailableImageView = findViewById(R.id.event_trip_icon_surfing_available_image_view);
       // mSurfingAvailableSmallImageView = findViewById(R.id.event_trip_icon_surfing);
        mSurfingAvailableTextView = findViewById(R.id.event_trip_icon_surfing_available_text_view);

        TextView mFromTextView = findViewById(R.id.trip_from_text_view);
        TextView mFromCampTextView = findViewById(R.id.trip_camp_from_text_view);
        TextView mToTextView = findViewById(R.id.trip_to_text_view);
        TextView mToCampTextView = findViewById(R.id.trip_camp_to_text_view);
        //Load event data
        mEvent = new Event();
        mExtraInfoHelp.getInfoFromIntent(intent, mEvent,getApplicationContext());
        getWindsurferFromIntent(intent, mWindsurfer,getApplicationContext());
        // Update the views on the screen with the values from the database
        mPlaceTextView.setText(mEvent.getPlace());
        mDateTextView.setText(mEvent.getDate());
        mPlaceStartTextView.setText(mEvent.getStartPlace());
        mDateStartTextView.setText(mEvent.getStartDate());
        mCommentTextView.setText(mEvent.getComment());
        mCostTextView.setText(Integer.toString(mEvent.getCost()));
        if(mEvent.getCostDiscount()>0){
            mCostDiscountTextView.setText(Integer.toString(mEvent.getCost()-mEvent.getCostDiscount()));
            mCostTextView.setPaintFlags(mCostTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mCurrencyTextView.setPaintFlags(mCurrencyTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            TextView discountTitle = findViewById(R.id.event_trip_discount_title_text_view);
            ImageView discountImage = findViewById(R.id.event_trip_discount_image_view);
            discountTitle.setVisibility(View.GONE);
            discountImage.setVisibility(View.GONE);
            mCostDiscountHelpImageView.setVisibility(View.GONE);
            mCostDiscountTextView.setVisibility(View.GONE);
            mCurrencyDiscountTextView.setVisibility(View.GONE);
        }
        //Help Discount dialog
        ImageView mDiscountHelpImageView = findViewById(R.id.event_trip_discount_help_image_view);
        mDiscountHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpDiscountDialog();
            }
        });
        //Help interested dialog
        ImageView mInterestedHelpImageView = findViewById(R.id.event_trip_interested_help_image_view);
        mInterestedHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpInterestedDialog();
            }
        });

        mCostAboutTextView.setText(mEvent.getCostAbout());
        if(mEvent.getContact().getPhoneNumber().isEmpty()){
            mContactPhoneTextView.setVisibility(View.GONE);
            mContactPhoneImageView.setVisibility(View.GONE);
        } else {
            mContactPhoneTextView.setText(mEvent.getContact().getPhoneNumber());
        }
        if(mEvent.getContact().getEmailAddress().isEmpty()){
            mContactEmailTextView.setVisibility(View.GONE);
            mContactEmailImageView.setVisibility(View.GONE);
        } else {
            mContactEmailTextView.setText(mEvent.getContact().getEmailAddress());

        }
        if(mEvent.getContact().getWebAddress().isEmpty()){
            mContactWebTextView.setVisibility(View.GONE);
            mContactWebImageView.setVisibility(View.GONE);
        } else {
            mContactWebTextView.setText(mEvent.getContact().getWebAddress());
        }

        //if startCountry == 1000 it is CAMP
        if(mEvent.getStartCountry()==EventContract.EventEntry.IT_IS_CAMP){
            if(mEvent.getDisplayAs()==EventContract.EventEntry.DISPLAY_AS_CAMP) {
                //setTitle(getString(R.string.event_trip_activity_title_camp));
                android.support.v7.app.ActionBar actionbar = this.getSupportActionBar();
                actionbar.setTitle(Html.fromHtml("<small><small>"+getResources().getString(R.string.event_trip_activity_title_camp)+"</small></small>"));
            } else {
                //setTitle(getString(R.string.event_trip_activity_title_training));
                android.support.v7.app.ActionBar actionbar = this.getSupportActionBar();
                actionbar.setTitle(Html.fromHtml("<small><small>"+getResources().getString(R.string.event_trip_activity_title_training)+"</small></small>"));
            }
            mDateStartCampTextView.setText(mEvent.getStartDate());
            mStartCountryImageView.setVisibility(View.GONE);
            mPlaceStartTextView.setVisibility(View.GONE);
            mDateStartTextView.setVisibility(View.GONE);
            mFromTextView.setVisibility(View.GONE);
            mToTextView.setText(R.string.event_trip_camp_to);
        } else if (mEvent.getType()==EditorChoose.TRIP_TYPE_PRIVATE) {
            setTitle(getString(R.string.event_trip_activity_title_private));
            mDateStartCampTextView.setVisibility(View.GONE);
            mFromCampTextView.setVisibility(View.GONE);
            mToCampTextView.setVisibility(View.GONE);
        } else if (mEvent.getType()==EditorChoose.TRIP_TYPE_ORGANIZED) {
            setTitle(getString(R.string.event_trip_activity_title_organized));
            mDateStartCampTextView.setVisibility(View.GONE);
            mFromCampTextView.setVisibility(View.GONE);
            mToCampTextView.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.event_trip_activity_title));
            mDateStartCampTextView.setVisibility(View.GONE);
            mFromCampTextView.setVisibility(View.GONE);
            mToCampTextView.setVisibility(View.GONE);
        }

        Drawable mWindsurfingAvailableImageViewBackground = mWindsurfingAvailableImageView.getBackground();
        switch (mEvent.getWindsurfingAvailable()){
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_windsurfing);
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mWindsurfingAvailableTextView.setText(R.string.available_trip_private_with_equipment);
                } else {
                    mWindsurfingAvailableTextView.setText(R.string.avaiable_course);
                }
                mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_course), PorterDuff.Mode.MULTIPLY);
                //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_green_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mWindsurfingAvailableTextView.setTextSize(10);
                    mWindsurfingAvailableTextView.setText(R.string.available_trip_private_without_equipment);
                } else {
                    mWindsurfingAvailableTextView.setText(R.string.avaiable_yes);
                }
                mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_yes), PorterDuff.Mode.MULTIPLY);
                //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_yellow_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mWindsurfingAvailableTextView.setTextSize(12);
                mWindsurfingAvailableTextView.setText(R.string.avaiable_no);
                mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no), PorterDuff.Mode.MULTIPLY);
                //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mWindsurfingAvailableTextView.setText(R.string.avaiable_no_info);
                mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no_info), PorterDuff.Mode.MULTIPLY);
                //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_windsurfing);
                mWindsurfingAvailableTextView.setTextSize(12);
                mWindsurfingAvailableTextView.setText(R.string.avaiable_course_instructor);
                mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_instructor_course), PorterDuff.Mode.MULTIPLY);
                //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
                default:
                    mWindsurfingAvailableTextView.setText(R.string.avaiable_no_info);
                    mWindsurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no_info), PorterDuff.Mode.MULTIPLY);
                    //mWindsurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                    break;
        }
        mWindsurfingAvailableImageView.setBackground(mWindsurfingAvailableImageViewBackground);

        Drawable mKitesurfingAvailableImageViewBackground = mKitesurfingAvailableImageView.getBackground();
        switch (mEvent.getKitesurfingAvailable()){
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_kitesurfing);
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mKitesurfingAvailableTextView.setText(R.string.available_trip_private_with_equipment);
                } else {
                    mKitesurfingAvailableTextView.setText(R.string.avaiable_course);
                }
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_course), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_green_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mKitesurfingAvailableTextView.setTextSize(10);
                    mKitesurfingAvailableTextView.setText(R.string.available_trip_private_without_equipment);
                } else {
                    mKitesurfingAvailableTextView.setText(R.string.avaiable_yes);
                }
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_yes), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_yellow_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mKitesurfingAvailableTextView.setTextSize(12);
                mKitesurfingAvailableTextView.setText(R.string.avaiable_no);
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mKitesurfingAvailableTextView.setText(R.string.avaiable_no_info);
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no_info), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_kitesurfing);
                mKitesurfingAvailableTextView.setTextSize(12);
                mKitesurfingAvailableTextView.setText(R.string.avaiable_course_instructor);
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_instructor_course), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            default:
                mKitesurfingAvailableTextView.setText(R.string.avaiable_no_info);
                mKitesurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no_info), PorterDuff.Mode.MULTIPLY);
                //mKitesurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
        }
        mKitesurfingAvailableImageView.setBackground(mKitesurfingAvailableImageViewBackground);

        Drawable mSurfingAvailableImageViewBackground = mSurfingAvailableImageView.getBackground();
        switch (mEvent.getSurfingAvailable()){
            case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_surfing);
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mSurfingAvailableTextView.setText(R.string.available_trip_private_with_equipment);
                } else {
                    mSurfingAvailableTextView.setText(R.string.avaiable_course);
                }
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_course), PorterDuff.Mode.MULTIPLY);
               //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_green_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                if(mEvent.getCharacter()==EventContract.EventEntry.CHARACTER_PRIVATE) {
                    mSurfingAvailableTextView.setTextSize(10);
                    mSurfingAvailableTextView.setText(R.string.available_trip_private_without_equipment);
                } else {
                    mSurfingAvailableTextView.setText(R.string.avaiable_yes);
                }
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_yes), PorterDuff.Mode.MULTIPLY);
                //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_yellow_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                mSurfingAvailableTextView.setTextSize(12);
                mSurfingAvailableTextView.setText(R.string.avaiable_no);
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no), PorterDuff.Mode.MULTIPLY);
                //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_NO_INFO:
                mSurfingAvailableTextView.setText(R.string.avaiable_no_info);
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no_info), PorterDuff.Mode.MULTIPLY);
                //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                avaiableSportsToQuote = avaiableSportsToQuote + getResources().getString(R.string.share_trip_quote_surfing);
                mSurfingAvailableTextView.setText(R.string.avaiable_course_instructor);
                mSurfingAvailableTextView.setTextSize(12);
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_instructor_course), PorterDuff.Mode.MULTIPLY);
                //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
            default:
                mSurfingAvailableTextView.setText(R.string.avaiable_no_info);
                mSurfingAvailableImageViewBackground.setColorFilter(getColor(R.color.sport_available_no), PorterDuff.Mode.MULTIPLY);
                //mSurfingAvailableImageView.setBackgroundColor(getApplicationContext().getColor(R.color.app_red_color));
                break;
        }
        mSurfingAvailableImageView.setBackground(mSurfingAvailableImageViewBackground);
        /** Transport of event*/
        switch (mEvent.getTransport()) {
            case EventContract.EventEntry.TRANSPORT_CAR:
                mTransportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                mTransportImageView.setImageResource(R.drawable.trip_car_ic);
                break;
            case EventContract.EventEntry.TRANSPORT_PLANE:
                mTransportTextView.setText(livewind.example.andro.liveWind.R.string.transport_plane);
                mTransportImageView.setImageResource(R.drawable.trip_plane_ic);
                break;
            case EventContract.EventEntry.TRANSPORT_OWN:
                //mTransportTextView.setTextSize(12);
                mTransportTextView.setVisibility(View.GONE);
                mTransportImageView.setVisibility(View.GONE); //TODO Make own transport icon
                findViewById(R.id.event_transport_relative_layout).setVisibility(View.GONE);
                break;
            default:
                mTransportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                mTransportImageView.setImageResource(R.drawable.trip_car_ic);
                break;
        }

        /** character of event*/
        switch (mEvent.getCharacter()) {
            case EventContract.EventEntry.CHARACTER_PRIVATE:
                mCharacterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                mCharacterImageView.setImageResource(R.drawable.trip_private_ic);
                break;
            case EventContract.EventEntry.CHARACTER_ORGANIZED:
                mCharacterTextView.setText(livewind.example.andro.liveWind.R.string.character_organized);
                mCharacterImageView.setImageResource(R.drawable.trip_organised_ic);
                break;
            default:
                mCharacterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                mCharacterImageView.setImageResource(R.drawable.trip_private_ic);
                break;
        }
        /** currency of cost */
        switch (mEvent.getCurrency()) {
            case EventContract.EventEntry.CURRENCY_ZL:
                mCurrencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
                mCurrencyDiscountTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
                break;
            case EventContract.EventEntry.CURRENCY_EURO:
                mCurrencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_euro);
                mCurrencyDiscountTextView.setText(R.string.currency_euro);
                break;
            case EventContract.EventEntry.CURRENCY_USD:
                mCurrencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_usd);
                mCurrencyDiscountTextView.setText(R.string.currency_usd);
                break;
            default:
                mCurrencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
                mCurrencyDiscountTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
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
        switch(mEvent.getStartCountry()) {
            case EventContract.EventEntry.COUNTRY_WORLD:
                mStartCountryImageView.setImageResource(R.drawable.flag_world);
                break;
            case EventContract.EventEntry.COUNTRY_POLAND:
                mStartCountryImageView.setImageResource(R.drawable.flag_pl);
                startCountryEmoji = getEmojiByUnicode(0x1F1F5 )+getEmojiByUnicode(0x1F1F1);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                mStartCountryImageView.setImageResource(R.drawable.flag_gr);
                startCountryEmoji = getEmojiByUnicode(0x1F1EC)+getEmojiByUnicode(0x1F1F7	);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                mStartCountryImageView.setImageResource(R.drawable.flag_es);
                startCountryEmoji = getEmojiByUnicode(0x1F1EA)+getEmojiByUnicode(0x1F1F8);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                mStartCountryImageView.setImageResource(R.drawable.flag_hr);
                startCountryEmoji = getEmojiByUnicode(0x1F1ED )+getEmojiByUnicode(0x1F1F7);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                mStartCountryImageView.setImageResource(R.drawable.flag_pt);
                startCountryEmoji = getEmojiByUnicode(0x1F1F5 )+getEmojiByUnicode(0x1F1F9);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                startCountryEmoji = getEmojiByUnicode(0x1F1E9  )+getEmojiByUnicode(0x1F1EA);
                mStartCountryImageView.setImageResource(R.drawable.flag_de);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                startCountryEmoji = getEmojiByUnicode(0x1F1EB  )+getEmojiByUnicode(0x1F1F7);
                mStartCountryImageView.setImageResource(R.drawable.flag_fr);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                startCountryEmoji = getEmojiByUnicode(0x1F1FF  )+getEmojiByUnicode(0x1F1E6);
                mStartCountryImageView.setImageResource(R.drawable.flag_za);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                startCountryEmoji = getEmojiByUnicode(0x1F1F2  )+getEmojiByUnicode(0x1F1E6);
                mStartCountryImageView.setImageResource(R.drawable.flag_ma);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                startCountryEmoji = getEmojiByUnicode(0x1F1EE  )+getEmojiByUnicode(0x1F1F9);
                mStartCountryImageView.setImageResource(R.drawable.flag_it);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                startCountryEmoji = getEmojiByUnicode(0x1F1EA  )+getEmojiByUnicode(0x1F1EC);
                mStartCountryImageView.setImageResource(R.drawable.flag_eg);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                startCountryEmoji = getEmojiByUnicode(0x1F1EC  )+getEmojiByUnicode(0x1F1E7);
                mStartCountryImageView.setImageResource(R.drawable.flag_uk);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                startCountryEmoji = getEmojiByUnicode(0x1F1F9  )+getEmojiByUnicode(0x1F1F7);
                mStartCountryImageView.setImageResource(R.drawable.flag_tr);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                startCountryEmoji = getEmojiByUnicode(0x1F1E6  )+getEmojiByUnicode(0x1F1F9);
                mStartCountryImageView.setImageResource(R.drawable.flag_at);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                startCountryEmoji = getEmojiByUnicode(0x1F1E9  )+getEmojiByUnicode(0x1F1F0);
                mStartCountryImageView.setImageResource(R.drawable.flag_dk);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                startCountryEmoji = getEmojiByUnicode(0x1F1E7   )+getEmojiByUnicode(0x1F1F7);
                mStartCountryImageView.setImageResource(R.drawable.flag_br);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                startCountryEmoji = getEmojiByUnicode(0x1F1FA   )+getEmojiByUnicode(0x1F1F8	);
                mStartCountryImageView.setImageResource(R.drawable.flag_us);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                startCountryEmoji = getEmojiByUnicode(0x1F1FB   )+getEmojiByUnicode(0x1F1F3);
                mStartCountryImageView.setImageResource(R.drawable.flag_vn);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                startCountryEmoji = getEmojiByUnicode(0x1F1F2   )+getEmojiByUnicode(0x1F1F9);
                mStartCountryImageView.setImageResource(R.drawable.flag_mt);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                mStartCountryImageView.setImageResource(R.drawable.flag_world);
                break;
            default:
                mStartCountryImageView.setImageResource(R.drawable.flag_world);
                break;
        }

        //FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();
   //     mEventId = intent.getStringExtra("EXTRA_EVENT_ID");
        //For adding members
        mMembersDatabaseReference = mFirebaseDatabase.getReference().child("test/events").child(mEvent.getId()).child("mMembers");
        //For deleting events
        mEventsDatabaseReference = mFirebaseDatabase.getReference().child("test/events");

        //MEMBERS
        mMemberListView = (ListView) findViewById(livewind.example.andro.liveWind.R.id.members_list_view);
        // Initialize events ListView and its adapter
        final List<MyMember> myMembers = new ArrayList<>();
        mMemberAdapter = new MemberAdapter(this, myMembers, 0);
        mMemberListView.setAdapter(mMemberAdapter);
        attachDatabaseReadListener();

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
                            MyMember joinMember = new MyMember(mWindsurfer.getUsername(), 0, mWindsurfer.getPhotoName(),getApplicationContext());
                            mMemberAdapter.add(joinMember);
                            mMembersDatabaseReference.setValue(myMembers);
                            ListViewHelp.setListViewHeightBasedOnChildren(mMemberListView);
                            Toast.makeText(EventTripActivity.this, getString(R.string.toast_trip_joined), Toast.LENGTH_SHORT).show();
                            mMembersDatabaseReference.removeEventListener(mChildMemberListener);
                            query.removeEventListener(this);
                        } else {
                            Toast.makeText(EventTripActivity.this, getString(R.string.toast_trip_joined_fail), Toast.LENGTH_SHORT).show();
                            query.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        // Trip type = 1000 = it is camp
        int tripType = mEvent.getStartCountry();
        if(tripType==EventContract.EventEntry.IT_IS_CAMP){
            if(mEvent.getDisplayAs()==EventContract.EventEntry.DISPLAY_AS_CAMP) {
                quoteToShare =
                        getEmojiByUnicode(0x1F3D6) +
                                getResources().getString(R.string.share_trip_quote_camp_start_place) + mEvent.getPlace() + " " + countryEmoji;
            } else {
                quoteToShare =
                        getEmojiByUnicode(0x1F3D6) +
                                getResources().getString(R.string.share_trip_quote_training_start_place) + mEvent.getPlace() + " " + countryEmoji;
            }
            } else {

            quoteToShare =
                    getEmojiByUnicode(0x1F3D6) +
                            getResources().getString(R.string.share_trip_quote_start_place) + mEvent.getStartPlace() + " " + startCountryEmoji +
                            getResources().getString(R.string.share_trip_quote_to) + mEvent.getPlace() + " " +countryEmoji;
        }
            quoteToShare = quoteToShare + ".\n" +
                        getEmojiByUnicode(0x1F4C6)+
                        getResources().getString(R.string.share_trip_quote_date)+mDateStartTextView.getText().toString()+
                        getResources().getString(R.string.share_trip_quote_to)+mDateTextView.getText().toString()+".\n" +
                        getEmojiByUnicode(0x1F4B8)+
                        getResources().getString(R.string.share_trip_quote_cost)+Integer.toString(mEvent.getCost()-mEvent.getCostDiscount())+mCurrencyDiscountTextView.getText().toString()+ "\n";

        if(!avaiableSportsToQuote.isEmpty()){
            quoteToShare = quoteToShare + getEmojiByUnicode(0x1F3C4) + getResources().getString(R.string.share_trip_quote_sports) + avaiableSportsToQuote;
        }
                        quoteToShare = quoteToShare+ "\n" +
                        getEmojiByUnicode(0x1F4DD)+
                        getResources().getString(R.string.share_trip_quote_comment)+mEvent.getComment()+ "\n";
        if(!(mEvent.getContact().getPhoneNumber().isEmpty() && mEvent.getContact().getEmailAddress().isEmpty() && mEvent.getContact().getWebAddress().isEmpty())){
            quoteToShare = quoteToShare +getResources().getString(R.string.share_trip_quote_contact) + "\n";
        }
        if(!mEvent.getContact().getPhoneNumber().isEmpty()){
            quoteToShare = quoteToShare + getEmojiByUnicode(0x1F4DE) +" "+ mEvent.getContact().getPhoneNumber()+ "\n";
        }
        if(!mEvent.getContact().getEmailAddress().isEmpty()){
            quoteToShare = quoteToShare + getEmojiByUnicode(0x1F4E7) +" "+ mEvent.getContact().getEmailAddress()+ "\n";
        }
        if(!mEvent.getContact().getWebAddress().isEmpty()){
            quoteToShare = quoteToShare + getEmojiByUnicode(0x1F310) +" "+ mEvent.getContact().getWebAddress()+ "\n";
        }
                        quoteToShare = quoteToShare + "\n" +
                        getEmojiByUnicode(0x1F30A)+
                        getResources().getString(R.string.share_trip_quote_foot);
    }

    private void attachDatabaseReadListener() {
        if (mChildMemberListener == null) {
            mChildMemberListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MyMember myMember = dataSnapshot.getValue(MyMember.class);
                    mMemberAdapter.add(myMember);
                    ListViewHelp.setListViewHeightBasedOnChildren(mMemberListView);
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
        if (!(mEvent.getmUsername().equals(mWindsurfer.getUsername()))) {
            MenuItem menuItemDelete = menu.findItem(livewind.example.andro.liveWind.R.id.action_trip_delete);
            menuItemDelete.setVisible(false);
            MenuItem menuItemEdit = menu.findItem(livewind.example.andro.liveWind.R.id.action_trip_edit);
            menuItemEdit.setVisible(false);
            MenuItem menuItemCopy = menu.findItem(R.id.action_trip_copy);
            menuItemCopy.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(livewind.example.andro.liveWind.R.menu.menu_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Windsurfer clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_trip_edit:
                // Open event editor
                editEvent();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case livewind.example.andro.liveWind.R.id.action_trip_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_trip_copy:
                copyEvent();
                finish();
                return true;
            case R.id.action_trip_share_facebook:
                //SHARING TO FACEBOOK
                if(!mEvent.getContact().getWebAddress().isEmpty()){
                    defaultPhotoUrl=mEvent.getContact().getWebAddress();
                }
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(defaultPhotoUrl))
                        .setQuote(quoteToShare)
                        .build();
                ShareDialog share = new ShareDialog(EventTripActivity.this);
                if(share.canShow(content,ShareDialog.Mode.NATIVE)) {
                    share.show(content, ShareDialog.Mode.NATIVE);
                } else if(share.canShow(content,ShareDialog.Mode.AUTOMATIC)) {
                    share.show(content, ShareDialog.Mode.AUTOMATIC);
                } else if(share.canShow(content,ShareDialog.Mode.WEB)){
                    share.show(content, ShareDialog.Mode.WEB);
                } else {
                    Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_share_facebook_error),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_trip_share_messenger:
                try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, quoteToShare);
                sendIntent.setType("text/plain");
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.setPackage("com.facebook.orca");
                startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_share_messenger_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_trip_share_other:
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, quoteToShare);
                    sendIntent.setType("text/plain");
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(sendIntent,getString(R.string.share)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.toast_share_other_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                NavUtils.navigateUpFromSameTask(EventTripActivity.this);
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
            mFirebaseHelp.removePoints(mEvent.getmUserUId(), EventContract.EventEntry.IT_IS_TRIP);
            mEventsDatabaseReference.child(mEvent.getId()).removeValue();

            // Show a toast message depending on whether or not the delete was successful.
            if (mEventsDatabaseReference.child(mEvent.getId()).getKey().equals("")) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(livewind.example.andro.liveWind.R.string.toast_editor_delete_trip_failed),
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

    private void editEvent(){
        Intent intent = new Intent(EventTripActivity.this, EditorTripActivity.class);
        mExtraInfoHelp.putInfoToIntent(intent, mEvent, mWindsurfer.getUsername(), getApplicationContext());
        mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
        startActivity(intent);
    }

    private void copyEvent(){
        Intent intent = new Intent(EventTripActivity.this, EditorTripActivity.class);
        mEvent.setStartDate("");
        mEvent.setDate("");
        mEvent.setTimestampStartDate(0);
        mExtraInfoHelp.putInfoToIntent(intent, mEvent, mWindsurfer.getUsername(), getApplicationContext());
        mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
        startActivity(intent);
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
    public void showHelpInterestedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_trip_interested, null))
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

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}