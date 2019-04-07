package livewind.example.andro.liveWind;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.nio.channels.NoConnectionPendingException;

import livewind.example.andro.liveWind.user.UserActivity;
import livewind.example.andro.liveWind.user.Windsurfer;

public class ExtraInfoHelp {
    public ExtraInfoHelp(){}

    public static Intent putInfoToIntent(Intent intent, Event event, Windsurfer windsurfer, Context context){
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_ID), event.getId());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_USERNAME), event.getmUsername());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PLACE), event.getPlace());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_DATE), event.getDate());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_CREATION_TIMESTAMP),event.getTimestamp());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TYPE), event.getType());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WIND_POWER), event.getWindPower());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WAVE_SIZE), event.getWaveSize());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONDITIONS), event.getConditions());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_COMMENT),event.getComment());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PHOTO_URL), event.getPhotoUrl());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_PHONE), event.getContact().getPhoneNumber());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_EMAIL), event.getContact().getEmailAddress());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_WEB), event.getContact().getWebAddress());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_PLACE), event.getStartPlace());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_START_COUNTRY),event.getStartCountry());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_DATE), event.getStartDate());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CHARACTER), event.getCharacter());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_TRANSPORT), event.getTransport());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST), event.getCost());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_DISCOUNT), event.getCostDiscount());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CURRENCY),event.getCurrency());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST_ABOUT), event.getCostAbout());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_COUNTRY),event.getCountry());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_WINDSURFING),event.getWindsurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_KITESURFING),event.getKitesurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_SURFING),event.getSurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_DISPLAY_AS),event.getDisplayAs());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_NICKNAME), windsurfer.getUsername());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_UID),event.getmUserUId());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_SHARES_COUNTER),event.getmSharesCounter());
        return intent;
    }

    public static Intent putInfoToIntent(Intent intent, Event event, String username, Context context){
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_ID), event.getId());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_USERNAME), event.getmUsername());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PLACE), event.getPlace());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_DATE), event.getDate());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_CREATION_TIMESTAMP),event.getTimestamp());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TYPE), event.getType());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WIND_POWER), event.getWindPower());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WAVE_SIZE), event.getWaveSize());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONDITIONS), event.getConditions());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_COMMENT),event.getComment());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PHOTO_URL), event.getPhotoUrl());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_PHONE), event.getContact().getPhoneNumber());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_EMAIL), event.getContact().getEmailAddress());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_WEB), event.getContact().getWebAddress());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_PLACE), event.getStartPlace());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_DATE), event.getStartDate());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CHARACTER), event.getCharacter());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_TRANSPORT), event.getTransport());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST), event.getCost());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_DISCOUNT), event.getCostDiscount());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CURRENCY),event.getCurrency());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST_ABOUT), event.getCostAbout());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_COUNTRY),event.getCountry());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_START_COUNTRY),event.getStartCountry());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_WINDSURFING),event.getWindsurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_KITESURFING),event.getKitesurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_SURFING),event.getSurfingAvailable());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_TRIP_DISPLAY_AS),event.getDisplayAs());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_UID),event.getmUserUId());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_SHARES_COUNTER),event.getmSharesCounter());
        //intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_NICKNAME), username);

        return intent;
    }

    public static Intent putCoverageToIntent(Intent intent, Event event, Windsurfer windsurfer, Context context){
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_ID), event.getId());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_USERNAME), event.getmUsername());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PLACE), event.getPlace());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TYPE), event.getType());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WIND_POWER), event.getWindPower());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WAVE_SIZE), event.getWaveSize());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONDITIONS), event.getConditions());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_COMMENT),event.getComment());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PHOTO_URL), event.getPhotoUrl());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_COUNTRY),event.getCountry());
    //    intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_NICKNAME), windsurfer.getUsername());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_UID),event.getmUserUId());
        intent.putExtra(context.getString(R.string.EXTRA_EVENT_SHARES_COUNTER),event.getmSharesCounter());
        return intent;
    }

    public static void getInfoFromIntent(Intent intent, Event event,Context context){
        //Get all event information
        event.setId(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_ID)));
        event.setUsername(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_USERNAME)));
        event.setPlace(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PLACE)));
        event.setDate(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_DATE)));
        event.setTimestamp(intent.getLongExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_CREATION_TIMESTAMP)),-1));
        event.setType(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TYPE)),-1));
        event.setWindPower(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WIND_POWER)), -1));
        event.setWaveSize(intent.getDoubleExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_WAVE_SIZE)), -1));
        event.setConditions(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONDITIONS)), -1));
        event.setComment(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_COMMENT)));
        event.setPhotoUrl(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_PHOTO_URL)));
        event.setContact(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_PHONE)),intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_EMAIL)),intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_CONTACT_WEB)));
        event.setStartPlace(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_PLACE)));
        event.setStartDate(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_START_DATE)));
        event.setCharacter(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CHARACTER)),-1));
        event.setTransport(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_TRANSPORT)),-1));
        event.setCost(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST)),-1));
        event.setCostDiscount(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_DISCOUNT)),0));
        event.setCurrency(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_CURRENCY)),-1));
        event.setCostAbout(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_EVENT_TRIP_COST_ABOUT)));
        event.setCountry(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_COUNTRY)),-1));
        event.setStartCountry(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_START_COUNTRY)),-1));
        event.setWindsurfingAvailable(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_WINDSURFING)),-1));
        event.setKitesurfingAvailable(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_KITESURFING)),-1));
        event.setSurfingAvailable(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_AVAILABLE_SURFING)),-1));
        event.setDisplayAs(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_TRIP_DISPLAY_AS)),-1));
        event.setmUserUId(intent.getStringExtra(context.getString(R.string.EXTRA_WINDSURFER_UID)));
        event.setmSharesCounter(intent.getIntExtra(String.valueOf(context.getString(R.string.EXTRA_EVENT_SHARES_COUNTER)),-1));
    }

    public static Intent putWindsurferToIntent(Intent intent, Windsurfer windsurfer, Context context){
        try{
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_NICKNAME),windsurfer.getUsername());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_POINTS),windsurfer.getPoints());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_CREATED_EVENTS),windsurfer.getCreatedEvents());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_CREATED_TRIPS),windsurfer.getCreatedTrips());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_EVENTS),windsurfer.getActiveEvents());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_TRIPS),windsurfer.getActiveTrips());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_EVENTS_LIMIT),windsurfer.getActiveEventsLimit());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_TRIPS_LIMIT),windsurfer.getActiveTripsLimit());
        intent.putExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_PHOTO_ID),windsurfer.getPhotoName());
        intent.putIntegerArrayListExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_EXTRAS_ARRAY_LIST),windsurfer.getExtras());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_UID),windsurfer.getUid());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_TOKEN),windsurfer.getUserToken());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_EMAIL),windsurfer.getEmail());
        intent.putExtra(context.getString(R.string.EXTRA_WINDSURFER_PHOTO_LARGE_ID),windsurfer.getPhotoLargeName());
    } catch (NoConnectionPendingException e){
        Toast.makeText(context, context.getString(R.string.toast_no_connection),Toast.LENGTH_SHORT).show();
    }
        return intent;
    }

    public static Windsurfer getWindsurferFromIntent(Intent intent,Context context){
        Windsurfer windsurfer = new Windsurfer();
        windsurfer.setUsername(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_NICKNAME)));
        windsurfer.setPoints(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_POINTS)),-1));
        windsurfer.setCreatedEvents(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_CREATED_EVENTS)),-1));
        windsurfer.setCreatedTrips(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_CREATED_TRIPS)),-1));
        windsurfer.setActiveEvents(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_EVENTS)),-1));
        windsurfer.setActiveTrips(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_TRIPS)),-1));
        windsurfer.setActiveEventsLimit(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_EVENTS_LIMIT)),-1));
        windsurfer.setActiveTripsLimit(intent.getIntExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_ACTIVE_TRIPS_LIMIT)),-1));
        windsurfer.setPhotoName(intent.getStringExtra(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_PHOTO_ID)));
        windsurfer.setExtras(intent.getIntegerArrayListExtra(String.valueOf(context.getString(livewind.example.andro.liveWind.R.string.EXTRA_WINDSURFER_EXTRAS_ARRAY_LIST))));
        windsurfer.setUid(intent.getStringExtra(context.getString(R.string.EXTRA_WINDSURFER_UID)));
        windsurfer.setUserToken(intent.getStringExtra(context.getString(R.string.EXTRA_WINDSURFER_TOKEN)));
        windsurfer.setEmail(intent.getStringExtra(context.getString(R.string.EXTRA_WINDSURFER_EMAIL)));
        windsurfer.setPhotoLargeName(intent.getStringExtra(context.getString(R.string.EXTRA_WINDSURFER_PHOTO_LARGE_ID)));
        return windsurfer;
    }

}
