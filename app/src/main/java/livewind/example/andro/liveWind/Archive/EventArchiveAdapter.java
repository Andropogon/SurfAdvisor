package livewind.example.andro.liveWind.Archive;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Event;
import livewind.example.andro.liveWind.EventActivity;
import livewind.example.andro.liveWind.EventAdapter;
import livewind.example.andro.liveWind.EventTrip;
import livewind.example.andro.liveWind.EventTripActivity;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.user.Windsurfer;

import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;

/**
 * Created by JGJ on 29/05/19.
 *
 * Event (coverages) archive adapter
 */
public class EventArchiveAdapter extends FirebaseRecyclerPagingAdapter<Event, EventArchiveAdapter.EventViewHolder> {

    private final int VIEW_TYPE_COVERAGE = 0;
    private final int VIEW_TYPE_TRIP = 1;
    private final int VIEW_TYPE_EMPTY = 2;

    private final Context context;
    //private final Query query;
    private int mColorResourceId;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private ProgressBar progressBar;
    //private View emptyView;
    private int mColorWhite;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mCurrentTimeReference= mFirebaseDatabase.getReference().child("currentTime");

    public EventArchiveAdapter(Context context, Query ref, SwipeRefreshLayout swipeRefreshLayout, LifecycleOwner lifecycleOwner, PagedList.Config config) {
        super(new DatabasePagingOptions.Builder<Event>()
                        .setLifecycleOwner(lifecycleOwner)
                        .setQuery(ref,config, new SnapshotParser<Event>() {
                            @NonNull
                            @Override
                            public Event parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Event event = snapshot.getValue(Event.class);
                                return event;
                            }
                        })
                    .build());
        this.context=context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        //this.progressBar = progressBar;
        //this.emptyView = emptyView;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventArchiveAdapter.EventViewHolder viewHolder, int position,@NonNull Event event) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> selectedCountries;
        selectedCountries = sharedPref.getStringSet(context.getString(R.string.settings_display_countries_key), new HashSet<String>());
        //if (event.getStartDate().equals(checkEventOrTrip) && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD))) {
        viewHolder.setEvent(event);
        //}
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:

                break;
            case LOADING_MORE:
                // Do your loading animation
                swipeRefreshLayout.setRefreshing(true);
                break;

            case LOADED:
                // Stop Animation
                swipeRefreshLayout.setRefreshing(false);
                break;

            case FINISHED:
                //Reached end of Data set
                swipeRefreshLayout.setRefreshing(false);
                break;

            case ERROR:
                retry();
                break;
        }
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
            placeTextView = (TextView) view.findViewById(R.id.list_place_text_view);
            dateTextView = (TextView) view.findViewById(R.id.list_date_text_view);
            typeImageView = (ImageView) view.findViewById(R.id.list_image_view);
            wind_powerTextView = (TextView) view.findViewById(R.id.list_wind_power_text_view);
            wave_sizeTextView = (TextView) view.findViewById(R.id.list_wave_size_text_view);
            conditionsTextView = (TextView) view.findViewById(R.id.list_conditions_text_view);
            wind_power_unitTextView = (TextView) view.findViewById(R.id.list_wind_power_unit_text_view);
            commentTextView = (TextView) view.findViewById(R.id.list_comment_text_view);
            thanksTextView = (TextView) view.findViewById(R.id.list_thanks_number_text_view);
            //Init trip views
            placeStartTextView = (TextView) view.findViewById(R.id.list_trip_start_place_text_view);
            dateStartTextView = (TextView) view.findViewById(R.id.list_trip_start_date_text_view);
            placeTripTextView = (TextView) view.findViewById(R.id.list_trip_place_text_view);
            dateTripTextView = (TextView) view.findViewById(R.id.list_trip_date_text_view);
            dateCounterTextView = view.findViewById(R.id.list_trip_date_counter);
            costTextView = (TextView) view.findViewById(R.id.list_trip_cost_text_view);
            characterImageView = (ImageView) view.findViewById(R.id.list_trip_character_image_view);
            transportImageView = (ImageView) view.findViewById(R.id.list_trip_transport_image_view);
            currencyTextView = (TextView) view.findViewById(R.id.list_trip_cost_currency_text_view);
            mWindsurfingAvailableImageView = view.findViewById(R.id.list_trip_type_windsurfing_image_view);
            mKitesurfingAvailableImageView = view.findViewById(R.id.list_trip_type_kitesurfing_image_view);
            mSurfingAvailableImageView = view.findViewById(R.id.list_trip_type_surfing_image_view);
            fromTextView = view.findViewById(R.id.list_trip_from_text_view);
            toTextView = view.findViewById(R.id.list_trip_to_text_view);
/*
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
            });*/
        }

        void setEvent( Event event ){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int windPowerUnit = Integer.parseInt(sharedPref.getString(context.getString(R.string.settings_display_wind_power_key),"1"));
            setupCoverage(event,windPowerUnit);
            }

        private void setupCoverage(Event event, int windPowerUnit) {

            //Get place and country label
            String countryPlaceString = Event.getPlaceWithCountryCutoff(event);
            //Set place label size
            if (countryPlaceString.length() <= 10) {
                placeTextView.setTextSize(20);
            } else if (countryPlaceString.length() <= 15) {
                placeTextView.setTextSize(16);
            } else {
                placeTextView.setTextSize(12);
            }

            //Set texts to views
            setEventDurationOnDateTextView(event, dateTextView);
            placeTextView.setText(countryPlaceString);
            wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
            commentTextView.setText(event.getComment());
            setEventDurationOnDateTextView(event, dateTextView);
            placeTextView.setText(countryPlaceString);
            wind_powerTextView.setText(Integer.toString(event.getWindPower()));
            wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
            commentTextView.setText(event.getComment());
            thanksTextView.setText(Integer.toString(event.getmThanksSize()));

            // Set type image of event
            switch (event.getType()) {
                case EventContract.EventEntry.TYPE_WINDSURFING:
                    typeImageView.setImageResource(R.drawable.windsurfing_icon);
                    break;
                case EventContract.EventEntry.TYPE_KITESURFING:
                    typeImageView.setImageResource(R.drawable.kitesurfing_icon);
                    break;
                case EventContract.EventEntry.TYPE_SURFING:
                    typeImageView.setImageResource(R.drawable.surfing_icon);
                    break;
                default:
                    typeImageView.setImageResource(R.drawable.others_icon);
                    break;
            }

            //Set image background color during to wind power
            int windPowerInBft = 0;
            double windPowerInSailSize = 0;
            if (event.getWindPower() >= 0 && event.getWindPower() <= 3) {
                mColorResourceId = R.color.kts0_3;
                windPowerInBft = 1;
                windPowerInSailSize = 13;
            } else if (event.getWindPower() >= 4 && event.getWindPower() <= 6) {
                mColorResourceId = R.color.kts4_6;
                windPowerInBft = 2;
                windPowerInSailSize = 12.5;
            } else if (event.getWindPower() >= 7 && event.getWindPower() <= 10) {
                mColorResourceId = R.color.kts7_10;
                windPowerInBft = 3;
                windPowerInSailSize = 11.5;
            } else if (event.getWindPower() >= 11 && event.getWindPower() <= 13) {
                mColorResourceId = R.color.kts11_13;
                windPowerInBft = 4;
                windPowerInSailSize = 9.5;
            } else if (event.getWindPower() >= 14 && event.getWindPower() <= 16) {
                mColorResourceId = R.color.kts14_16;
                windPowerInBft = 4;
                windPowerInSailSize = 7.5;
            } else if (event.getWindPower() >= 17 && event.getWindPower() <= 19) {
                mColorResourceId = R.color.kts17_19;
                windPowerInBft = 5;
                windPowerInSailSize = 6.0;
            } else if (event.getWindPower() >= 20 && event.getWindPower() <= 21) {
                mColorResourceId = R.color.kts20_21;
                windPowerInBft = 5;
                windPowerInSailSize = 5.3;
            } else if (event.getWindPower() >= 22 && event.getWindPower() <= 24) {
                mColorResourceId = R.color.kts22_24;
                windPowerInBft = 6;
                windPowerInSailSize = 4.8;
            } else if (event.getWindPower() >= 25 && event.getWindPower() <= 27) {
                mColorResourceId = R.color.kts25_27;
                windPowerInBft = 6;
                windPowerInSailSize = 4.2;
            } else if (event.getWindPower() >= 28 && event.getWindPower() <= 30) {
                mColorResourceId = R.color.kts28_30;
                windPowerInBft = 7;
                windPowerInSailSize = 3.7;
            } else if (event.getWindPower() >= 31 && event.getWindPower() <= 33) {
                mColorResourceId = R.color.kts31_33;
                windPowerInBft = 7;
                windPowerInSailSize = 3.5;
            } else if (event.getWindPower() >= 34 && event.getWindPower() <= 36) {
                mColorResourceId = R.color.kts34_36;
                windPowerInBft = 8;
                windPowerInSailSize = 3.3;
            } else if (event.getWindPower() >= 37 && event.getWindPower() <= 40) {
                mColorResourceId = R.color.kts37_40;
                windPowerInBft = 8;
                windPowerInSailSize = 3.0;
            } else if (event.getWindPower() >= 41 && event.getWindPower() <= 47) {
                mColorResourceId = R.color.kts41_47;
                windPowerInBft = 9;
                windPowerInSailSize = 3.0;
            } else if (event.getWindPower() >= 48 && event.getWindPower() <= 55) {
                mColorResourceId = R.color.kts50;
                windPowerInBft = 10;
                windPowerInSailSize = 3.0;
            } else if (event.getWindPower() >= 56 && event.getWindPower() <= 63) {
                mColorResourceId = R.color.kts50;
                windPowerInBft = 11;
                windPowerInSailSize = 3.0;
            } else if (event.getWindPower() >= 64) {
                mColorResourceId = R.color.kts50;
                windPowerInBft = 12;
                windPowerInSailSize = -1.0;
            } else {
                mColorResourceId = R.color.ktsINCORRECT;
            }

            //Set wind power unit during to user preferences
            switch (windPowerUnit) {
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
            mColorWhite = R.color.whiteColor;
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
                    conditionsTextView.setText(R.string.conditions_north);
                    break;
                case EventContract.EventEntry.CONDITIONS_NNE:
                    conditionsTextView.setText(R.string.conditions_NNE);
                    break;
                case EventContract.EventEntry.CONDITIONS_NE:
                    conditionsTextView.setText(R.string.conditions_NE);
                    break;
                case EventContract.EventEntry.CONDITIONS_ENE:
                    conditionsTextView.setText(R.string.conditions_ENE);
                    break;
                case EventContract.EventEntry.CONDITIONS_E:
                    conditionsTextView.setText(R.string.conditions_east);
                    break;
                case EventContract.EventEntry.CONDITIONS_ESE:
                    conditionsTextView.setText(R.string.conditions_ESE);
                    break;
                case EventContract.EventEntry.CONDITIONS_SE:
                    conditionsTextView.setText(R.string.conditions_SE);
                    break;
                case EventContract.EventEntry.CONDITIONS_SSE:
                    conditionsTextView.setText(R.string.conditions_SSE);
                    break;
                case EventContract.EventEntry.CONDITIONS_S:
                    conditionsTextView.setText(R.string.conditions_south);
                    break;
                case EventContract.EventEntry.CONDITIONS_SSW:
                    conditionsTextView.setText(R.string.conditions_SSW);
                    break;
                case EventContract.EventEntry.CONDITIONS_SW:
                    conditionsTextView.setText(R.string.conditions_SW);
                    break;
                case EventContract.EventEntry.CONDITIONS_WSW:
                    conditionsTextView.setText(R.string.conditions_WSW);
                    break;
                case EventContract.EventEntry.CONDITIONS_W:
                    conditionsTextView.setText(R.string.conditions_west);
                    break;
                case EventContract.EventEntry.CONDITIONS_WNW:
                    conditionsTextView.setText(R.string.conditions_WNW);
                    break;
                case EventContract.EventEntry.CONDITIONS_NW:
                    conditionsTextView.setText(R.string.conditions_NW);
                    break;
                case EventContract.EventEntry.CONDITIONS_NNW:
                    conditionsTextView.setText(R.string.conditions_NNW);
                    break;
                default:
                    conditionsTextView.setText(R.string.conditions_unknown);
                    break;
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


/*    @Override
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
    }*/

}
    }