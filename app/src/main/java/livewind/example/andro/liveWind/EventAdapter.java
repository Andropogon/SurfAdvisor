package livewind.example.andro.liveWind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import livewind.example.andro.liveWind.firebase.FirebaseHelp;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.user.Windsurfer;

import static livewind.example.andro.liveWind.ExtraInfoHelp.putInfoToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;

/**
 * Created by JGJ on 10/10/18.
 * Header added during refactoring add 24/04/2019 by JGJ.
 *
 * Event (coverages and trips) adapter
 *
 */
public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.EventViewHolder> {

    private final int VIEW_TYPE_COVERAGE = 0;
    private final int VIEW_TYPE_TRIP = 1;
    private final int VIEW_TYPE_EMPTY = 2;

    private final Context context;
    private Windsurfer windsurfer;
    //private final Query query;
    private int mColorResourceId;
    private ProgressBar progressBar;
    private View emptyView;
    private int mColorWhite;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mCurrentTimeReference= mFirebaseDatabase.getReference().child("currentTime");

    public EventAdapter(Context context, Query ref, Windsurfer windsurfer, ProgressBar progressBar, View emptyView) {
        super(new  FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(ref, new SnapshotParser<Event>() {
                            @NonNull
                            @Override
                            public Event parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Event event = snapshot.getValue(Event.class);
                                return event;
                            }
                        })
                        .build());
        this.context=context;
        this.windsurfer = windsurfer;
        this.progressBar = progressBar;
        this.emptyView = emptyView;
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_COVERAGE:
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            break;
            case VIEW_TYPE_TRIP:
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_trip_item, parent, false);
            break;
            case VIEW_TYPE_EMPTY:
                if(getItemCount()==0){
                    emptyView.setVisibility(View.VISIBLE);
                }
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_empty, parent, false);
            break;
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_empty, parent, false);
                break;
        }

        return new EventViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(EventAdapter.EventViewHolder viewHolder, int position, Event event) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int displayTripsOptions = Integer.valueOf(sharedPref.getString(context.getString(R.string.settings_display_trips_key), "1"));
        boolean displayBoolean = sharedPref.getBoolean(context.getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
        Set<String> selectedCountries;
        if (displayBoolean == EventContract.EventEntry.IT_IS_TRIP) {
            selectedCountries = sharedPref.getStringSet(context.getString(R.string.settings_display_countries_key), new HashSet<String>());
        } else {
            selectedCountries = sharedPref.getStringSet(context.getString(R.string.settings_display_coverages_countries_key), new HashSet<String>());
        }
        final String checkEventOrTrip = "DEFAULT";
        if (displayBoolean) {
            if (event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD))) {
                viewHolder.setEvent(event);
            } else {
            }
        } else {

            if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO) {
                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                    if (CatalogActivity.checkFilters(event)) viewHolder.setEvent(event);
                }
            } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM) {
                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                    if (CatalogActivity.checkFilters(event)) viewHolder.setEvent(event);
                }
            } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_TO) {
                if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getCountry())))) {
                    if (CatalogActivity.checkFilters(event)) viewHolder.setEvent(event);
                }
            }
        }
    }
        @Override
        public int getItemViewType(int position) {
            //note: classes should start with uppercase
            Event event = getItem(position);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int displayTripsOptions = Integer.valueOf(sharedPref.getString(context.getString(R.string.settings_display_trips_key), "1"));
            boolean displayBoolean = sharedPref.getBoolean(context.getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
            Set<String> selectedCountries;
            if (displayBoolean == EventContract.EventEntry.IT_IS_TRIP) {
                selectedCountries = sharedPref.getStringSet(context.getString(R.string.settings_display_countries_key), new HashSet<String>());
            } else {
                selectedCountries = sharedPref.getStringSet(context.getString(R.string.settings_display_coverages_countries_key), new HashSet<String>());
            }
            final String checkEventOrTrip = "DEFAULT";
            if (displayBoolean) {
                if (event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD))) {
                    return VIEW_TYPE_COVERAGE;
                } else {
                }
            } else {
                if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM_AND_TO) {
                    if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                        if (CatalogActivity.checkFilters(event)) return VIEW_TYPE_TRIP;
                    }
                } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_FROM) {
                    if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                        if (CatalogActivity.checkFilters(event)) return VIEW_TYPE_TRIP;
                    }
                } else if (displayTripsOptions == EventContract.EventEntry.DISPLAY_TRIPS_TO) {
                    if (!event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getCountry())))) {
                        if (CatalogActivity.checkFilters(event)) return VIEW_TYPE_TRIP;
                    }
                }
            }
            return VIEW_TYPE_EMPTY;
        }



    public class EventViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "EventViewHolder";
        //Coverages views
        TextView placeTextView;
        TextView dateTextView;
        ImageView typeImageView;
        TextView wind_powerTextView;
        TextView wave_sizeTextView;
        TextView conditionsTextView;
        TextView wind_power_unitTextView;
        TextView commentTextView;
        TextView thanksTextView;
        //Trip views
        TextView placeStartTextView;
        TextView dateStartTextView ;
        TextView placeTripTextView;
        TextView dateTripTextView;
        TextView dateCounterTextView;
        TextView costTextView;
        ImageView characterImageView;
        ImageView transportImageView;
        TextView currencyTextView;
        ImageView mWindsurfingAvailableImageView;
        ImageView mKitesurfingAvailableImageView;
        ImageView mSurfingAvailableImageView;
        TextView fromTextView;
        TextView toTextView;

        Intent singleEventIntent;
        public EventViewHolder(View view) {
            super(view);
            //Init coverages views
            placeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_place_text_view);
            dateTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_date_text_view);
            typeImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_image_view);
            wind_powerTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_text_view);
            wave_sizeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wave_size_text_view);
            conditionsTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_conditions_text_view);
            wind_power_unitTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_unit_text_view);
            commentTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_comment_text_view);
            thanksTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_thanks_number_text_view);
            //Init trip views
            placeStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_place_text_view);
            dateStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_date_text_view);
            placeTripTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_place_text_view);
            dateTripTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_date_text_view);
            dateCounterTextView = view.findViewById(R.id.list_trip_date_counter);
            costTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_text_view);
            characterImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_character_image_view);
            transportImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_transport_image_view);
            currencyTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_currency_text_view);
            mWindsurfingAvailableImageView = view.findViewById(R.id.list_trip_type_windsurfing_image_view);
            mKitesurfingAvailableImageView = view.findViewById(R.id.list_trip_type_kitesurfing_image_view);
            mSurfingAvailableImageView = view.findViewById(R.id.list_trip_type_surfing_image_view);
            fromTextView = view.findViewById(R.id.list_trip_from_text_view);
            toTextView = view.findViewById(R.id.list_trip_to_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItem(getAdapterPosition()).getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT)) {
                        singleEventIntent = new Intent(CatalogActivity.getContext(), EventActivity.class);
                    } else {
                        singleEventIntent = new Intent(CatalogActivity.getContext(), EventTripActivity.class);
                    }
                    //Put Extra information about clicked event and who is clicking.
                    singleEventIntent = putInfoToIntent(singleEventIntent,getItem(getAdapterPosition()),windsurfer,context);
                    putWindsurferToIntent(singleEventIntent,windsurfer,context);
                   // singleEventIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(singleEventIntent);
                }
            });
        }

        void setEvent( Event event ){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean displayBoolean = sharedPref.getBoolean(context.getString(R.string.settings_display_boolean_key), true);
            int windPowerUnit = Integer.parseInt(sharedPref.getString(context.getString(R.string.settings_display_wind_power_key),"1"));
                if (event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && displayBoolean ) {
                    setupCoverage(event,windPowerUnit);
                } else if (!event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && !displayBoolean) {
                    setupTrip(event);
                } else {
                    //Empty view
                }
            }

        private void setupCoverage(Event event, int windPowerUnit){

            //Get place and country label
            String countryPlaceString = Event.getPlaceWithCountryCutoff(event);
            //Set place label size
            if(countryPlaceString.length()<=10){
                placeTextView.setTextSize(20);
            } else if (countryPlaceString.length()<=15){
                placeTextView.setTextSize(16);
            } else {
                placeTextView.setTextSize(12);
            }

            //Set texts to views
            setEventDurationOnDateTextView(event,dateTextView);
            placeTextView.setText(countryPlaceString);
            wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
            commentTextView.setText(event.getComment());
            setEventDurationOnDateTextView(event,dateTextView);
            placeTextView.setText(countryPlaceString);
            wind_powerTextView.setText(Integer.toString(event.getWindPower()));
            wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
            commentTextView.setText(event.getComment());
            thanksTextView.setText(Integer.toString(event.getmThanksSize()));

            // Set type image of event
            switch (event.getType()) {
                case EventContract.EventEntry.TYPE_WINDSURFING:
                    typeImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.windsurfing_icon);
                    break;
                case EventContract.EventEntry.TYPE_KITESURFING:
                    typeImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.kitesurfing_icon);
                    break;
                case EventContract.EventEntry.TYPE_SURFING:
                    typeImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.surfing_icon);
                    break;
                default:
                    typeImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.others_icon);
                    break;
            }

            //Set image background color during to wind power
            int windPowerInBft = 0;
            double windPowerInSailSize = 0;
            if (event.getWindPower() >= 0 && event.getWindPower() <= 3) {
                mColorResourceId = livewind.example.andro.liveWind.R.color.kts0_3;
                windPowerInBft=1;
                windPowerInSailSize=13;
            } else if (event.getWindPower() >= 4 && event.getWindPower() <= 6) {
                mColorResourceId = R.color.kts4_6;
                windPowerInBft=2;
                windPowerInSailSize=12.5;
            } else if (event.getWindPower() >= 7 && event.getWindPower() <= 10) {
                mColorResourceId = R.color.kts7_10;
                windPowerInBft=3;
                windPowerInSailSize=11.5;
            } else if (event.getWindPower() >= 11 && event.getWindPower() <= 13) {
                mColorResourceId = R.color.kts11_13;
                windPowerInBft=4;
                windPowerInSailSize=9.5;
            } else if (event.getWindPower() >= 14 && event.getWindPower() <= 16) {
                mColorResourceId = R.color.kts14_16;
                windPowerInBft=4;
                windPowerInSailSize=7.5;
            }else if (event.getWindPower() >= 17 && event.getWindPower() <= 19) {
                mColorResourceId = R.color.kts17_19;
                windPowerInBft=5;
                windPowerInSailSize=6.0;
            } else if (event.getWindPower() >= 20 && event.getWindPower() <= 21) {
                mColorResourceId = R.color.kts20_21;
                windPowerInBft=5;
                windPowerInSailSize=5.3;
            } else if (event.getWindPower() >= 22 && event.getWindPower() <= 24) {
                mColorResourceId = R.color.kts22_24;
                windPowerInBft=6;
                windPowerInSailSize=4.8;
            } else if (event.getWindPower() >= 25 && event.getWindPower() <= 27) {
                mColorResourceId = R.color.kts25_27;
                windPowerInBft=6;
                windPowerInSailSize=4.2;
            } else if (event.getWindPower() >= 28 && event.getWindPower() <= 30) {
                mColorResourceId = R.color.kts28_30;
                windPowerInBft=7;
                windPowerInSailSize=3.7;
            } else if (event.getWindPower() >= 31 && event.getWindPower() <= 33) {
                mColorResourceId = R.color.kts31_33;
                windPowerInBft=7;
                windPowerInSailSize=3.5;
            } else if (event.getWindPower() >= 34 && event.getWindPower() <= 36) {
                mColorResourceId = R.color.kts34_36;
                windPowerInBft=8;
                windPowerInSailSize=3.3;
            } else if (event.getWindPower() >= 37 && event.getWindPower() <= 40) {
                mColorResourceId = R.color.kts37_40;
                windPowerInBft=8;
                windPowerInSailSize=3.0;
            } else if (event.getWindPower() >= 41 && event.getWindPower() <= 47) {
                mColorResourceId = R.color.kts41_47;
                windPowerInBft=9;
                windPowerInSailSize=3.0;
            }else if (event.getWindPower() >= 48 && event.getWindPower() <= 55) {
                mColorResourceId = R.color.kts50;
                windPowerInBft=10;
                windPowerInSailSize=3.0;
            }else if (event.getWindPower() >= 56 && event.getWindPower() <= 63) {
                mColorResourceId = R.color.kts50;
                windPowerInBft=11;
                windPowerInSailSize=3.0;
            }else if (event.getWindPower() >= 64) {
                mColorResourceId = R.color.kts50;
                windPowerInBft=12;
                windPowerInSailSize=-1.0;
            } else {
                mColorResourceId = livewind.example.andro.liveWind.R.color.ktsINCORRECT;
            }

            //Set wind power unit during to user preferences
            switch (windPowerUnit){
                case EventContract.EventEntry.UNIT_KNOTS:
                    wind_power_unitTextView.setText(R.string.unit_wind_kn);
                    wind_powerTextView.setText(Integer.toString(event.getWindPower()));
                    break;
                case EventContract.EventEntry.UNIT_BEAUFORT:
                    wind_power_unitTextView.setText(R.string.unit_wind_bft);
                    wind_powerTextView.setText(Integer.toString(windPowerInBft));
                    break;
                case EventContract.EventEntry.UNIT_SAILS_SIZE:
                    wind_power_unitTextView.setText(R.string.unit_wind_sail_size);
                    wind_powerTextView.setText(Double.toString(windPowerInSailSize));
                    break;
                default:
                    wind_power_unitTextView.setText(R.string.unit_wind_kn);
                    wind_powerTextView.setText(Integer.toString(windPowerInBft));
                    break;
            }

            int color = ContextCompat.getColor(context, mColorResourceId);
            mColorWhite = livewind.example.andro.liveWind.R.color.whiteColor;
            Drawable iconBackground = typeImageView.getBackground();
            iconBackground.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            typeImageView.setBackground(iconBackground);

            //Set coverage wind direction
            switch (event.getConditions()) {
                case EventContract.EventEntry.CONDITIONS_ONSHORE:
                    conditionsTextView.setText(R.string.conditions_onshore);
                    break;
                case EventContract.EventEntry.CONDITIONS_SIDESHORE:
                    conditionsTextView.setText(R.string.conditions_sideshore);
                    break;
                case EventContract.EventEntry.CONDITIONS_OFFSHORE:
                    conditionsTextView.setText(R.string.conditions_offshore);
                    break;
                case EventContract.EventEntry.CONDITIONS_N:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_north);
                    break;
                case EventContract.EventEntry.CONDITIONS_NNE:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_NNE);
                    break;
                case EventContract.EventEntry.CONDITIONS_NE:
                    conditionsTextView.setText(R.string.conditions_NE);
                    break;
                case EventContract.EventEntry.CONDITIONS_ENE:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_ENE);
                    break;
                case EventContract.EventEntry.CONDITIONS_E:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_east);
                    break;
                case EventContract.EventEntry.CONDITIONS_ESE:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_ESE);
                    break;
                case EventContract.EventEntry.CONDITIONS_SE:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_SE);
                    break;
                case EventContract.EventEntry.CONDITIONS_SSE:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_SSE);
                    break;
                case EventContract.EventEntry.CONDITIONS_S:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_south);
                    break;
                case EventContract.EventEntry.CONDITIONS_SSW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_SSW);
                    break;
                case EventContract.EventEntry.CONDITIONS_SW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_SW);
                    break;
                case EventContract.EventEntry.CONDITIONS_WSW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_WSW);
                    break;
                case EventContract.EventEntry.CONDITIONS_W:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_west);
                    break;
                case EventContract.EventEntry.CONDITIONS_WNW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_WNW);
                    break;
                case EventContract.EventEntry.CONDITIONS_NW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_NW);
                    break;
                case EventContract.EventEntry.CONDITIONS_NNW:
                    conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_NNW);
                    break;
                default:
                    conditionsTextView.setText(R.string.conditions_unknown);
                    break;
            }
        }
        private void setupTrip(Event event){
            //Set colors and icons of sport views
            int backgroundColor;
            //Set trip display type during to tripType
            //If startCountry == 1000 it is camp.
            int tripType = event.getStartCountry();
            if(tripType==EventContract.EventEntry.IT_IS_CAMP){
                placeStartTextView.setVisibility(View.GONE);
                if (event.getDisplayAs()==1) {
                    fromTextView.setText(R.string.event_trip_camp_from);
                } else {
                    fromTextView.setText(R.string.event_trip_training_from);
                }

                toTextView.setText(R.string.event_trip_camp_to);
            }

            //Set date duration in days - dateCounterTextView
            long timestampEnd = getDateTimestamp(event.getDate());
            long numberOfDays = twoTimestampsToDays(event.getTimestampStartDate(),timestampEnd);
            dateCounterTextView.setText(Long.toString(numberOfDays));

            int goodColor;
            int instructorColor;
            Drawable mWindsurfingAvailableImageViewBackground = mWindsurfingAvailableImageView.getBackground();
            switch (event.getWindsurfingAvailable()){
                case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                    backgroundColor = R.color.sport_available_yes;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                    mWindsurfingAvailableImageView.setVisibility(View.GONE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                    backgroundColor = R.color.sport_available_instructor_course;
                    instructorColor = ContextCompat.getColor(context,backgroundColor);
                    mWindsurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                    mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    mWindsurfingAvailableImageView.setVisibility(View.GONE);
                    break;
            }
            mWindsurfingAvailableImageView.setBackground(mWindsurfingAvailableImageViewBackground);

            Drawable mKitesurfingAvailableImageViewBackground = mKitesurfingAvailableImageView.getBackground();
            switch (event.getKitesurfingAvailable()){
                case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                    backgroundColor = R.color.sport_available_yes;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                    mKitesurfingAvailableImageView.setVisibility(View.GONE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                    backgroundColor = R.color.sport_available_instructor_course;
                    instructorColor = ContextCompat.getColor(context,backgroundColor);
                    mKitesurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                    mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    mKitesurfingAvailableImageView.setVisibility(View.GONE);
                    break;
            }
            mKitesurfingAvailableImageView.setBackground(mKitesurfingAvailableImageViewBackground);

            Drawable mSurfingAvailableImageViewBackground = mSurfingAvailableImageView.getBackground();
            switch (event.getSurfingAvailable()){
                case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                    backgroundColor = R.color.sport_available_course;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                    backgroundColor = R.color.sport_available_yes;
                    goodColor = ContextCompat.getColor(context,backgroundColor);
                    mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                    mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                    mSurfingAvailableImageView.setVisibility(View.GONE);
                    break;
                case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                    backgroundColor = R.color.sport_available_instructor_course;
                    instructorColor = ContextCompat.getColor(context,backgroundColor);
                    mSurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                    mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    mSurfingAvailableImageView.setVisibility(View.GONE);
                    break;
            }
            mSurfingAvailableImageView.setBackground(mSurfingAvailableImageViewBackground);

            //Set place and country text views
            //Departure country
            String countryStartPlaceString = EventTrip.getStartPlaceWithCountryCutoff(event);
            //Arrival country
            String countryPlaceString = Event.getPlaceWithCountryCutoff(event);

            //Set countries texts size during to texts length
            if(countryPlaceString.length()<=15){
                placeTripTextView.setTextSize(20);
            } else {
                placeTripTextView.setTextSize(16);
            }
            if(countryStartPlaceString.length()<=15){
                placeStartTextView.setTextSize(20);
            } else {
                placeStartTextView.setTextSize(16);
            }
            placeStartTextView.setText(countryStartPlaceString);
            dateStartTextView.setText(event.getStartDate().substring(8,13));
            placeTripTextView.setText(countryPlaceString);
            dateTripTextView.setText(event.getDate().substring(0,5));

            //Set trip cost
            if(event.getCostDiscount()>0) {
                costTextView.setText(Integer.toString(event.getCost() - event.getCostDiscount()));
            } else {
                costTextView.setText(Integer.toString(event.getCost()));
            }

            //Set trip transport icons
            switch (event.getTransport()) {
                case EventContract.EventEntry.TRANSPORT_CAR:
                    transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                    break;
                case EventContract.EventEntry.TRANSPORT_PLANE:
                    transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_plane_ic);
                    break;
                case EventContract.EventEntry.TRANSPORT_OWN:
                    transportImageView.setVisibility(View.GONE); //TODO set special own transport icon
                    break;
                default:
                    transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                    break;
            }

            //Set trip character icon
            switch (event.getCharacter()) {
                case EventContract.EventEntry.CHARACTER_PRIVATE:
                    characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                    break;
                case EventContract.EventEntry.CHARACTER_ORGANIZED:
                    characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_organised_ic);
                    break;
                default:
                    characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                    break;
            }

            //Set currency of trip cost TODO make it based on user preferences
            switch (event.getCurrency()) {
                case EventContract.EventEntry.CURRENCY_ZL:
                    currencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
                    break;
                case EventContract.EventEntry.CURRENCY_EURO:
                    currencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_euro);
                    break;
                case EventContract.EventEntry.CURRENCY_USD:
                    currencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_usd);
                    break;
                default:
                    currencyTextView.setText(livewind.example.andro.liveWind.R.string.currency_zl);
                    break;
            }
        }
        }

    public void setEventDurationOnDateTextView(final Event event,final TextView view){
        ValueEventListener listener = new ValueEventListener() {
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
                        eventDurationString=context.getString(R.string.event_activity_time_now);
                    } else if (eventDurationInMinutes<60){
                        eventDurationString=Long.toString(eventDurationInMinutes)+context.getString(R.string.event_activity_time_min_ago);
                    } else if (eventDurationInMinutes>60){
                        int hours = 0;
                        while (eventDurationInMinutes>60){
                            hours++;
                            eventDurationInMinutes=eventDurationInMinutes-60;
                        }
                        eventDurationString=Integer.toString(hours)+ context.getString(R.string.event_activity_time_h_and)+Long.toString(eventDurationInMinutes)+context.getString(R.string.event_activity_time_min_ago);
                    } else {
                        eventDurationString = context.getString(R.string.event_activity_time_unknown);
                    }
                    view.setText(eventDurationString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mCurrentTimeReference.addListenerForSingleValueEvent(listener);
    }

    private long getDateTimestamp(String mDate){
            String day = mDate.substring(0, 2);
            String month = mDate.substring(3, 5);
            String year = mDate.substring(6, 10);
            int dayS = Integer.parseInt(day);
            int monthS = Integer.parseInt(month) -1 ; //because months are indexing from 0
            int yearS = Integer.parseInt(year);
            GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS,24,59,59);
            Calendar dataC = dataGC;
            long timestamp = dataC.getTimeInMillis();
            return timestamp;
    }

    private long twoTimestampsToDays(long startTimestamp, long endTimestamp){
        long numberOfDays = endTimestamp - startTimestamp;
        numberOfDays = numberOfDays / 86400000; //One day in milliseconds
            numberOfDays++;
            return numberOfDays;
    }

    @Override
    public void onDataChanged() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        int x = 0;
        for(int i=0; i<getItemCount(); i++){
            if(getItemViewType(i)==2){
                x++;
            }
        }
        if(x==getItemCount()){
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void setEmptyView(View emptyView){
        this.emptyView = emptyView;
    }

}