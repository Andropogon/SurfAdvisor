package livewind.example.andro.liveWind;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import livewind.example.andro.liveWind.data.EventContract;

public class EventAdapter extends ArrayAdapter<Event> {
    private int mColorResourceId;
    private int mColorWhite;
    private FirebaseHelp mFirebaseHelp = new FirebaseHelp();
    private final List<Event> objects;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mCurrentTimeReference= mFirebaseDatabase.getReference().child("currentTime");

    public EventAdapter(Context context, List<Event> objects, int resource) {
        super(context, 0, objects);
        this.objects=objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> selectedCountries = sharedPrefs.getStringSet(getContext().getString(R.string.settings_display_countries_key), new HashSet<String>());
        boolean displayBoolean = sharedPref.getBoolean(getContext().getString(livewind.example.andro.liveWind.R.string.settings_display_boolean_key), true);
        int windPowerUnit = Integer.parseInt(sharedPref.getString(getContext().getString(R.string.settings_display_wind_power_key),"1"));
        int windPowerInBft = 0;
        double windPowerInSailSize = 0;
        Event event = getItem(position);
        if (view == null) {
            if (event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && displayBoolean && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD))) {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_item, parent, false);

                TextView placeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_place_text_view);
                TextView dateTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_date_text_view);
                ImageView typeImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_image_view);
                TextView wind_powerTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_text_view);
                TextView wave_sizeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wave_size_text_view);
                TextView conditionsTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_conditions_text_view);
                ImageView wind_powerImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_image_view);
                ImageView wave_sizeImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wave_size_image_view);
                ImageView conditionsImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_conditions_image_view);
                TextView wind_power_unitTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_unit_text_view);
                TextView commentTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_comment_text_view);
                TextView thanksTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_thanks_number_text_view);

                String countryPlaceString = "";
                switch (event.getCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryPlaceString = event.getPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryPlaceString = event.getPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryPlaceString = event.getPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryPlaceString = event.getPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryPlaceString = event.getPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryPlaceString = event.getPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryPlaceString = event.getPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryPlaceString = event.getPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryPlaceString = event.getPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryPlaceString = event.getPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryPlaceString = event.getPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryPlaceString = event.getPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryPlaceString = event.getPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryPlaceString = event.getPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryPlaceString = event.getPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryPlaceString = event.getPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryPlaceString = event.getPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryPlaceString = event.getPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryPlaceString = event.getPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryPlaceString = event.getPlace()+", MT";
                        break;
                    default:
                        countryPlaceString = event.getPlace();
                        break;
                }
                if(countryPlaceString.length()<=10){
                    placeTextView.setTextSize(20);
                } else if (countryPlaceString.length()<=15){
                    placeTextView.setTextSize(16);
                } else {
                    placeTextView.setTextSize(12);
                }
                setEventDurationOnDateTextView(event,dateTextView);
                placeTextView.setText(countryPlaceString);
                //  dateTextView.setText(event.getDate());
                //wind_powerTextView.setText(Integer.toString(event.getWindPower()));
                wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
                commentTextView.setText(event.getComment());
                thanksTextView.setText(Integer.toString(event.getmThanksSize()));
                //Set time from the creation of the event
                setEventDurationOnDateTextView(event,dateTextView);
                placeTextView.setText(countryPlaceString);
                wind_powerTextView.setText(Integer.toString(event.getWindPower()));
                wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
                commentTextView.setText(event.getComment());
                thanksTextView.setText(Integer.toString(event.getmThanksSize()));

                /** Type of event image */
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
                /** Color of icon according to wind power */

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
                int color = ContextCompat.getColor(getContext(), mColorResourceId);
                mColorWhite = livewind.example.andro.liveWind.R.color.whiteColor;
                int colorWhite = ContextCompat.getColor(getContext(), mColorWhite);
              //  typeImageView.setColorFilter(color);
                Drawable iconBackground = typeImageView.getBackground();
                iconBackground.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                typeImageView.setBackground(iconBackground);
                //wind_powerTextView.setTextColor(color);
                //wind_powerImageView.setColorFilter(color);
                //wave_sizeImageView.setColorFilter(colorWhite);
                //conditionsImageView.setColorFilter(colorWhite);
                //wind_power_unitTextView.setTextColor(color);

                /** Type of event conditions */
                switch (event.getConditions()) {
                    case EventContract.EventEntry.CONDITIONS_ONSHORE:
                        conditionsTextView.setText("ONSHORE");
                        break;
                    case EventContract.EventEntry.CONDITIONS_SIDESHORE:
                        conditionsTextView.setText("SIDESHORE");
                        break;
                    case EventContract.EventEntry.CONDITIONS_OFFSHORE:
                        conditionsTextView.setText("OFFSHORE");
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
                        conditionsTextView.setText("UNKNOWN");
                        break;
                }
            } else if (!event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && !displayBoolean && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_trip_item, parent, false);


                TextView placeStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_place_text_view);
                TextView dateStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_date_text_view);
                TextView placeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_place_text_view);
                TextView dateTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_date_text_view);
                TextView dateCounterTextView = view.findViewById(R.id.list_trip_date_counter);
                //TextView transportTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_transport_text_view);
                //TextView characterTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_character_text_view);
                TextView costTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_text_view);
                //TextView commentTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_comment_text_view);
                ImageView characterImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_character_image_view);
                ImageView transportImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_transport_image_view);
                TextView currencyTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_currency_text_view);
                ImageView mWindsurfingAvailableImageView = view.findViewById(R.id.list_trip_type_windsurfing_image_view);
                ImageView mKitesurfingAvailableImageView = view.findViewById(R.id.list_trip_type_kitesurfing_image_view);
                ImageView mSurfingAvailableImageView = view.findViewById(R.id.list_trip_type_surfing_image_view);
/**
                LinearLayout linearLayout1 = view.findViewById(R.id.event_trip_list_item_linear_layout_1);
                LinearLayout linearLayout2 = view.findViewById(R.id.event_trip_list_item_linear_layout_2);
                LinearLayout linearLayout3 = view.findViewById(R.id.event_trip_list_item_linear_layout_3);
                if(event.getComment().equals("")){
                    ViewGroup.LayoutParams params1 = linearLayout1.getLayoutParams();
                    int height = (int) (92 * getContext().getResources().getDisplayMetrics().density + 0.5f);
                    params1.height = height;
                    linearLayout1.setLayoutParams(params1);
                    ViewGroup.LayoutParams params2 = linearLayout2.getLayoutParams();
                    height = (int) (88 * getContext().getResources().getDisplayMetrics().density + 0.5f);
                    params2.height = height;
                    linearLayout2.setLayoutParams(params2);
                    ViewGroup.LayoutParams params3 = linearLayout3.getLayoutParams();
                    height = (int) (86 * getContext().getResources().getDisplayMetrics().density + 0.5f);
                    params3.height = height;
                    linearLayout3.setLayoutParams(params3);
                }
*/
                //If startCountry == 1000 it is camp.
                int tripType = event.getStartCountry();
                if(tripType==EventContract.EventEntry.IT_IS_CAMP){
                    placeStartTextView.setVisibility(View.GONE);
                    TextView fromTextView = view.findViewById(R.id.list_trip_from_text_view);
                    if (event.getDisplayAs()==1) {
                        fromTextView.setText(R.string.event_trip_camp_from);
                    } else {
                        fromTextView.setText(R.string.event_trip_training_from);
                    }
                    TextView toTextView = view.findViewById(R.id.list_trip_to_text_view);
                    toTextView.setText(R.string.event_trip_camp_to);
                }

                long timestampEnd = getDateTimestamp(event.getDate());
                long numberOfDays = twoTimestampsToDays(event.getTimestampStartDate(),timestampEnd);
                dateCounterTextView.setText(Long.toString(numberOfDays));

                int backgroundColor = R.color.sport_available_yes;

                int goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                int instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                Drawable mWindsurfingAvailableImageViewBackground = mWindsurfingAvailableImageView.getBackground();
                switch (event.getWindsurfingAvailable()){
                    case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                        backgroundColor = R.color.sport_available_course;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        //backgroundColor = R.color.sport_available_no;
                        //goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        //mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        //backgroundColor = R.color.sport_available_no_info;
                        //goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        //mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                }
                mWindsurfingAvailableImageView.setBackground(mWindsurfingAvailableImageViewBackground);

                Drawable mKitesurfingAvailableImageViewBackground = mKitesurfingAvailableImageView.getBackground();
                switch (event.getKitesurfingAvailable()){
                    case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                        backgroundColor = R.color.sport_available_course;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        mKitesurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
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
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        mSurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mSurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                }
                mSurfingAvailableImageView.setBackground(mSurfingAvailableImageViewBackground);
                String countryStartPlaceString = "";
                switch (event.getStartCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryStartPlaceString = event.getStartPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryStartPlaceString = event.getStartPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryStartPlaceString = event.getStartPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryStartPlaceString = event.getStartPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryStartPlaceString = event.getStartPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryStartPlaceString = event.getStartPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryStartPlaceString = event.getStartPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryStartPlaceString = event.getStartPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryStartPlaceString = event.getStartPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryStartPlaceString = event.getStartPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryStartPlaceString = event.getStartPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryStartPlaceString = event.getStartPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryStartPlaceString = event.getStartPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryStartPlaceString = event.getStartPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryStartPlaceString = event.getStartPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryStartPlaceString = event.getStartPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryStartPlaceString = event.getStartPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryStartPlaceString = event.getStartPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryStartPlaceString = event.getStartPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryStartPlaceString = event.getStartPlace()+", MT";
                        break;
                    default:
                        countryStartPlaceString = event.getStartPlace();
                        break;
                }

                String countryPlaceString = "";
                switch (event.getCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryPlaceString = event.getPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryPlaceString = event.getPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryPlaceString = event.getPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryPlaceString = event.getPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryPlaceString = event.getPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryPlaceString = event.getPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryPlaceString = event.getPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryPlaceString = event.getPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryPlaceString = event.getPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryPlaceString = event.getPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryPlaceString = event.getPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryPlaceString = event.getPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryPlaceString = event.getPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryPlaceString = event.getPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryPlaceString = event.getPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryPlaceString = event.getPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryPlaceString = event.getPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryPlaceString = event.getPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryPlaceString = event.getPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryPlaceString = event.getPlace()+", MT";
                        break;
                    default:
                        countryPlaceString = event.getPlace();
                        break;
                }
                if(countryPlaceString.length()<=15){
                    placeTextView.setTextSize(20);
                } else {
                    placeTextView.setTextSize(16);
                }
                if(countryStartPlaceString.length()<=15){
                    placeStartTextView.setTextSize(20);
                } else {
                    placeStartTextView.setTextSize(16);
                }
                placeStartTextView.setText(countryStartPlaceString);
                dateStartTextView.setText(event.getStartDate().substring(8,13));
                placeTextView.setText(countryPlaceString);
                dateTextView.setText(event.getDate().substring(0,5));
                if(event.getCostDiscount()>0) {
                    costTextView.setText(Integer.toString(event.getCost() - event.getCostDiscount()));
                } else {
                    costTextView.setText(Integer.toString(event.getCost()));
                }
                //commentTextView.setText(event.getComment());

                /** Transport of event*/
                switch (event.getTransport()) {
                    case EventContract.EventEntry.TRANSPORT_CAR:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                        break;
                    case EventContract.EventEntry.TRANSPORT_PLANE:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_plane);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_plane_ic);
                        break;
                    default:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                        break;
                }

                /** character of event*/
                switch (event.getCharacter()) {
                    case EventContract.EventEntry.CHARACTER_PRIVATE:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                        break;
                    case EventContract.EventEntry.CHARACTER_ORGANIZED:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_organized);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_organised_ic);
                        break;
                    default:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                        break;
                }

                /** currency of cost */
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
            } else {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_item_empty, parent, false);
          //      view.setVisibility(view.GONE);
            }
        } else {
            if (event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && displayBoolean&& (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD))) {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_item, parent, false);


                TextView placeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_place_text_view);
                TextView dateTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_date_text_view);
                ImageView typeImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_image_view);
                TextView wind_powerTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_text_view);
                TextView wave_sizeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wave_size_text_view);
                TextView conditionsTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_conditions_text_view);
                ImageView wind_powerImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_image_view);
                ImageView wave_sizeImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wave_size_image_view);
                ImageView conditionsImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_conditions_image_view);
                TextView wind_power_unitTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_wind_power_unit_text_view);
                TextView commentTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_comment_text_view);
                TextView thanksTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_thanks_number_text_view);

                switch (windPowerUnit){
                    case EventContract.EventEntry.UNIT_KNOTS:
                        wind_power_unitTextView.setText(R.string.unit_wind_kn);
                        break;
                    case EventContract.EventEntry.UNIT_BEAUFORT:
                        wind_power_unitTextView.setText(R.string.unit_wind_bft);
                        break;
                    case EventContract.EventEntry.UNIT_SAILS_SIZE:
                        wind_power_unitTextView.setText(R.string.unit_wind_sail_size);
                        break;
                    default:
                        wind_power_unitTextView.setText(R.string.unit_wind_kn);
                        break;
                }

                String countryPlaceString = "";
                switch (event.getCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryPlaceString = event.getPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryPlaceString = event.getPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryPlaceString = event.getPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryPlaceString = event.getPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryPlaceString = event.getPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryPlaceString = event.getPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryPlaceString = event.getPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryPlaceString = event.getPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryPlaceString = event.getPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryPlaceString = event.getPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryPlaceString = event.getPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryPlaceString = event.getPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryPlaceString = event.getPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryPlaceString = event.getPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryPlaceString = event.getPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryPlaceString = event.getPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryPlaceString = event.getPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryPlaceString = event.getPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryPlaceString = event.getPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryPlaceString = event.getPlace()+", MT";
                        break;
                    default:
                        countryPlaceString = event.getPlace();
                        break;
                }
                if(countryPlaceString.length()<=10){
                    placeTextView.setTextSize(20);
                } else if (countryPlaceString.length()<=15){
                    placeTextView.setTextSize(16);
                } else {
                    placeTextView.setTextSize(12);
                }
                setEventDurationOnDateTextView(event,dateTextView);
                placeTextView.setText(countryPlaceString);
                wind_powerTextView.setText(Integer.toString(event.getWindPower()));
                wave_sizeTextView.setText(Double.toString(event.getWaveSize()));
                commentTextView.setText(event.getComment());
                thanksTextView.setText(Integer.toString(event.getmThanksSize()));

                /** Type of event image */
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
                /** Color of icon according to wind power */

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
                        break;
                }
                int color = ContextCompat.getColor(getContext(), mColorResourceId);
                mColorWhite = livewind.example.andro.liveWind.R.color.whiteColor;
                int colorWhite = ContextCompat.getColor(getContext(), mColorWhite);
                Drawable iconBackground = typeImageView.getBackground();
                iconBackground.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                typeImageView.setBackground(iconBackground);
                //  typeImageView.setColorFilter(color);
               // wind_powerTextView.setTextColor(color);
               // wind_powerImageView.setColorFilter(color);
                //wave_sizeImageView.setColorFilter(colorWhite);
                //conditionsImageView.setColorFilter(colorWhite);
                //wind_power_unitTextView.setTextColor(color);

                /** Type of event conditions */
                switch (event.getConditions()) {
                    case EventContract.EventEntry.CONDITIONS_ONSHORE:
                        conditionsTextView.setText("ONSHORE");
                        break;
                    case EventContract.EventEntry.CONDITIONS_SIDESHORE:
                        conditionsTextView.setText("SIDESHORE");
                        break;
                    case EventContract.EventEntry.CONDITIONS_OFFSHORE:
                        conditionsTextView.setText("OFFSHORE");
                        break;
                    case EventContract.EventEntry.CONDITIONS_N:
                        conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_north);
                        break;
                    case EventContract.EventEntry.CONDITIONS_NNE:
                        conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_NNE);
                        break;
                    case EventContract.EventEntry.CONDITIONS_NE:
                        conditionsTextView.setText(livewind.example.andro.liveWind.R.string.conditions_NE);
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
                        conditionsTextView.setText("UNKNOWN");
                        break;
                }
            } else if (!event.getStartDate().equals(EventContract.EventEntry.IS_IT_EVENT) && !displayBoolean && (selectedCountries.contains(Integer.toString(event.getCountry())) || selectedCountries.contains(EventContract.EventEntry.COUNTRY_ALL_WORLD) || selectedCountries.contains(Integer.toString(event.getStartCountry())))) {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_trip_item, parent, false);


                TextView placeStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_place_text_view);
                TextView dateStartTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_start_date_text_view);
                TextView placeTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_place_text_view);
                TextView dateTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_date_text_view);
                TextView dateCounterTextView = view.findViewById(R.id.list_trip_date_counter);
                //TextView transportTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_transport_text_view);
                //TextView characterTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_character_text_view);
                TextView costTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_text_view);
                //TextView commentTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_comment_text_view);
                ImageView characterImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_character_image_view);
                ImageView transportImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_transport_image_view);
                TextView currencyTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.list_trip_cost_currency_text_view);
                ImageView mWindsurfingAvailableImageView = view.findViewById(R.id.list_trip_type_windsurfing_image_view);
                ImageView mKitesurfingAvailableImageView = view.findViewById(R.id.list_trip_type_kitesurfing_image_view);
                ImageView mSurfingAvailableImageView = view.findViewById(R.id.list_trip_type_surfing_image_view);
/**
 LinearLayout linearLayout1 = view.findViewById(R.id.event_trip_list_item_linear_layout_1);
 LinearLayout linearLayout2 = view.findViewById(R.id.event_trip_list_item_linear_layout_2);
 LinearLayout linearLayout3 = view.findViewById(R.id.event_trip_list_item_linear_layout_3);
 if(event.getComment().equals("")){
 ViewGroup.LayoutParams params1 = linearLayout1.getLayoutParams();
 int height = (int) (92 * getContext().getResources().getDisplayMetrics().density + 0.5f);
 params1.height = height;
 linearLayout1.setLayoutParams(params1);
 ViewGroup.LayoutParams params2 = linearLayout2.getLayoutParams();
 height = (int) (88 * getContext().getResources().getDisplayMetrics().density + 0.5f);
 params2.height = height;
 linearLayout2.setLayoutParams(params2);
 ViewGroup.LayoutParams params3 = linearLayout3.getLayoutParams();
 height = (int) (86 * getContext().getResources().getDisplayMetrics().density + 0.5f);
 params3.height = height;
 linearLayout3.setLayoutParams(params3);
 }
 */

                //If startCountry == 1000 it is camp.
                int tripType = event.getStartCountry();
                if(tripType==EventContract.EventEntry.IT_IS_CAMP){
                    placeStartTextView.setVisibility(View.GONE);
                    TextView fromTextView = view.findViewById(R.id.list_trip_from_text_view);
                    if (event.getDisplayAs()==EventContract.EventEntry.DISPLAY_AS_CAMP) {
                        fromTextView.setText(R.string.event_trip_camp_from);
                    } else {
                        fromTextView.setText(R.string.event_trip_training_from);
                    }
                    TextView toTextView = view.findViewById(R.id.list_trip_to_text_view);
                    toTextView.setText(R.string.event_trip_camp_to);
                }

                long timestampEnd = getDateTimestamp(event.getDate());
                long numberOfDays = twoTimestampsToDays(event.getTimestampStartDate(),timestampEnd);
                dateCounterTextView.setText(Long.toString(numberOfDays));
                int backgroundColor = R.color.sport_available_yes;

                int goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                int instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                Drawable mWindsurfingAvailableImageViewBackground = mWindsurfingAvailableImageView.getBackground();
                switch (event.getWindsurfingAvailable()){
                    case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                        backgroundColor = R.color.sport_available_course;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        //backgroundColor = R.color.sport_available_no;
                        //goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        //mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mWindsurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        //backgroundColor = R.color.sport_available_no_info;
                        //goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        //mWindsurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mWindsurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                }
                mWindsurfingAvailableImageView.setBackground(mWindsurfingAvailableImageViewBackground);

                Drawable mKitesurfingAvailableImageViewBackground = mKitesurfingAvailableImageView.getBackground();
                switch (event.getKitesurfingAvailable()){
                    case EventContract.EventEntry.TRIP_AVAILABLE_COURSE:
                        backgroundColor = R.color.sport_available_course;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mKitesurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mKitesurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        mKitesurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
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
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_YES:
                        backgroundColor = R.color.sport_available_yes;
                        goodColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(goodColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_NO:
                        mSurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                    case EventContract.EventEntry.TRIP_AVAILABLE_INSTRUCTOR_COURSE:
                        backgroundColor = R.color.sport_available_instructor_course;
                        instructorColor = ContextCompat.getColor(getContext(),backgroundColor);
                        mSurfingAvailableImageViewBackground.setColorFilter(instructorColor, PorterDuff.Mode.MULTIPLY);
                        mSurfingAvailableImageView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mSurfingAvailableImageView.setVisibility(View.GONE);
                        break;
                }
                mSurfingAvailableImageView.setBackground(mSurfingAvailableImageViewBackground);
                String countryStartPlaceString = "";
                switch (event.getStartCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryStartPlaceString = event.getStartPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryStartPlaceString = event.getStartPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryStartPlaceString = event.getStartPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryStartPlaceString = event.getStartPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryStartPlaceString = event.getStartPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryStartPlaceString = event.getStartPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryStartPlaceString = event.getStartPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryStartPlaceString = event.getStartPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryStartPlaceString = event.getStartPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryStartPlaceString = event.getStartPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryStartPlaceString = event.getStartPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryStartPlaceString = event.getStartPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryStartPlaceString = event.getStartPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryStartPlaceString = event.getStartPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryStartPlaceString = event.getStartPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryStartPlaceString = event.getStartPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryStartPlaceString = event.getStartPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryStartPlaceString = event.getStartPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryStartPlaceString = event.getStartPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryStartPlaceString = event.getStartPlace()+", MT";
                        break;
                    default:
                        countryStartPlaceString = event.getStartPlace();
                        break;
                }

                String countryPlaceString = "";
                switch (event.getCountry()){
                    case EventContract.EventEntry.COUNTRY_WORLD:
                        countryPlaceString = event.getPlace()+"";
                        break;
                    case EventContract.EventEntry.COUNTRY_POLAND:
                        countryPlaceString = event.getPlace()+", PL";
                        break;
                    case EventContract.EventEntry.COUNTRY_GREECE:
                        countryPlaceString = event.getPlace()+", GR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SPAIN:
                        countryPlaceString = event.getPlace()+", ES";
                        break;
                    case EventContract.EventEntry.COUNTRY_CROATIA:
                        countryPlaceString = event.getPlace()+", HR";
                        break;
                    case EventContract.EventEntry.COUNTRY_PORTUGAL:
                        countryPlaceString = event.getPlace()+", PT";
                        break;
                    case EventContract.EventEntry.COUNTRY_GERMANY:
                        countryPlaceString = event.getPlace()+", DE";
                        break;
                    case EventContract.EventEntry.COUNTRY_FRANCE:
                        countryPlaceString = event.getPlace()+", FR";
                        break;
                    case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                        countryPlaceString = event.getPlace()+", ZA";
                        break;
                    case EventContract.EventEntry.COUNTRY_MOROCCO:
                        countryPlaceString = event.getPlace()+", MA";
                        break;
                    case EventContract.EventEntry.COUNTRY_ITALY:
                        countryPlaceString = event.getPlace()+", IT";
                        break;
                    case EventContract.EventEntry.COUNTRY_EGYPT:
                        countryPlaceString = event.getPlace()+", EG";
                        break;
                    case EventContract.EventEntry.COUNTRY_UK:
                        countryPlaceString = event.getPlace()+", UK";
                        break;
                    case EventContract.EventEntry.COUNTRY_TURKEY:
                        countryPlaceString = event.getPlace()+", TR";
                        break;
                    case EventContract.EventEntry.COUNTRY_AUSTRIA:
                        countryPlaceString = event.getPlace()+", AT";
                        break;
                    case EventContract.EventEntry.COUNTRY_DENMARK:
                        countryPlaceString = event.getPlace()+", DK";
                        break;
                    case EventContract.EventEntry.COUNTRY_BRAZIL:
                        countryPlaceString = event.getPlace()+", BR";
                        break;
                    case EventContract.EventEntry.COUNTRY_USA:
                        countryPlaceString = event.getPlace()+", US";
                        break;
                    case EventContract.EventEntry.COUNTRY_VIETNAM:
                        countryPlaceString = event.getPlace()+", VN";
                        break;
                    case EventContract.EventEntry.COUNTRY_MALTA:
                        countryPlaceString = event.getPlace()+", MT";
                        break;
                    default:
                        countryPlaceString = event.getPlace();
                        break;
                }
                if(countryPlaceString.length()<=15){
                    placeTextView.setTextSize(20);
                } else {
                    placeTextView.setTextSize(16);
                }
                if(countryStartPlaceString.length()<=15){
                    placeStartTextView.setTextSize(20);
                } else {
                    placeStartTextView.setTextSize(16);
                }
                placeStartTextView.setText(countryStartPlaceString);
                dateStartTextView.setText(event.getStartDate().substring(8,13));
                placeTextView.setText(countryPlaceString);
                dateTextView.setText(event.getDate().substring(0,5));
                if(event.getCostDiscount()>0) {
                    costTextView.setText(Integer.toString(event.getCost() - event.getCostDiscount()));
                } else {
                    costTextView.setText(Integer.toString(event.getCost()));
                }
                //commentTextView.setText(event.getComment());

                /** Transport of event*/
                switch (event.getTransport()) {
                    case EventContract.EventEntry.TRANSPORT_CAR:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                        break;
                    case EventContract.EventEntry.TRANSPORT_PLANE:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_plane);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_plane_ic);
                        break;
                    default:
                        //transportTextView.setText(livewind.example.andro.liveWind.R.string.transport_car);
                        transportImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_car_ic);
                        break;
                }

                /** character of event*/
                switch (event.getCharacter()) {
                    case EventContract.EventEntry.CHARACTER_PRIVATE:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                        break;
                    case EventContract.EventEntry.CHARACTER_ORGANIZED:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_organized);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_organised_ic);
                        break;
                    default:
                        //characterTextView.setText(livewind.example.andro.liveWind.R.string.character_private);
                        characterImageView.setImageResource(livewind.example.andro.liveWind.R.drawable.trip_private_ic);
                        break;
                }
                /** currency of cost */
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
            } else {
                view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_item_empty, parent, false);
                //      view.setVisibility(view.GONE);
            }
        }
        return view;
    }
    public void sort() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortEventsString = sharedPref.getString(getContext().getString(livewind.example.andro.liveWind.R.string.settings_display_sorting_events_by_key),"1");
        int sortEventsInt = Integer.parseInt(sortEventsString);
        String sortTripsString = sharedPref.getString(getContext().getString(livewind.example.andro.liveWind.R.string.settings_display_sorting_trips_by_key),"1");
        int sortTripsInt = Integer.parseInt(sortTripsString);

        Collections.sort(objects);

         switch (sortEventsInt) {
             case 1:
                 Collections.sort(objects,new EventDateComparator());
                 break;
             case 2:
                 Collections.sort(objects, new EventMembersComparator());
                 break;
             case 3:
                 Collections.sort(objects, new EventWindPowerComparator());//.reversed());
                 break;
             case 4:
                 Collections.sort(objects, new EventThanksSizeComparator());
                 break;
             default:
                 Collections.sort(objects, new EventDateComparator());
                 break;
         }
         switch (sortTripsInt) {
             case 1:
                 Collections.sort(objects, new TripsDateComparator());
                 break;
             case 2:
                 Collections.sort(objects, new TripsCostComparator());
                 break;
             default:
                 Collections.sort(objects, new TripsDateComparator());
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
                        eventDurationString=getContext().getString(R.string.event_activity_time_now);
                    } else if (eventDurationInMinutes<60){
                        eventDurationString=Long.toString(eventDurationInMinutes)+getContext().getString(R.string.event_activity_time_min_ago);
                    } else if (eventDurationInMinutes>60){
                        int hours = 0;
                        while (eventDurationInMinutes>60){
                            hours++;
                            eventDurationInMinutes=eventDurationInMinutes-60;
                        }
                        eventDurationString=Integer.toString(hours)+ getContext().getString(R.string.event_activity_time_h_and)+Long.toString(eventDurationInMinutes)+getContext().getString(R.string.event_activity_time_min_ago);
                    } else {
                        eventDurationString = getContext().getString(R.string.event_activity_time_unknown);
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
            return numberOfDays;
        }
}