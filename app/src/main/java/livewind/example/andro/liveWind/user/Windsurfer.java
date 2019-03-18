package livewind.example.andro.liveWind.user;


import android.content.Context;

import java.util.ArrayList;

import livewind.example.andro.liveWind.R;

public class Windsurfer {
    private String username;
    private String email;
    private String uid;
    private int points;
    private int createdEvents;
    private int createdTrips;
    private int activeEvents;
    private int activeTrips;
    private int activeEventsLimit;
    private int activeTripsLimit;
    private String photoName;
    private String photoLargeName;
    private String userToken;
    private ArrayList <Integer> extras =new ArrayList<Integer>();
    public final int LIMIT_OK = 1;
    public final int LIMIT_OUT = 0;
    public final int LIMIT_NO_CONNECTION = -1;
    public Windsurfer(){}
    public Windsurfer(String mId, String mUsername, String mEmail, int mPoints, int mCreatedEvents, int mCreatedTrips, Context context){
        uid=mId;
        username=mUsername;
        email = mEmail;
        points=mPoints;
        createdEvents=mCreatedEvents;
        createdTrips=mCreatedTrips;
        activeEventsLimit=2;
        activeTripsLimit=1;
        activeEvents=0;
        activeTrips=0;
        photoName = context.getResources().getResourceEntryName(R.drawable.user_icon_shaka_24);
        photoLargeName = context.getResources().getResourceEntryName(R.drawable.user_icon_shaka_100);

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public int getCreatedEvents() {
        return createdEvents;
    }

    public int getCreatedTrips() {
        return createdTrips;
    }

    public void setCreatedEvents(int createdEvents) {
        this.createdEvents = createdEvents;
    }

    public void setCreatedTrips(int createdTrips) {
        this.createdTrips = createdTrips;
    }

    public int getActiveEvents() {
        return activeEvents;
    }

    public int getActiveEventsLimit() {
        return activeEventsLimit;
    }

    public int getActiveTrips() {
        return activeTrips;
    }

    public int getActiveTripsLimit() {
        return activeTripsLimit;
    }

    public void setActiveEvents(int activeEvents) {
        this.activeEvents = activeEvents;
    }

    public void setActiveEventsLimit(int activeEventsLimit) {
        this.activeEventsLimit = activeEventsLimit;
    }

    public void setActiveTrips(int activeTrips) {
        this.activeTrips = activeTrips;
    }

    public void setActiveTripsLimit(int activeTripsLimit) {
        this.activeTripsLimit = activeTripsLimit;
    }

    public void setPhotoName(String mPhotoId){
        photoName = mPhotoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public int checkEventsLimitAdvanced(){
        if(activeEvents<activeEventsLimit) {
            return LIMIT_OK;
        } else if (activeEventsLimit==0){
            return LIMIT_NO_CONNECTION;
        } else {
            return LIMIT_OUT;
        }
    }
    public int checkTripsLimitAdvanced(){
        if(activeTrips<activeTripsLimit) {
            return LIMIT_OK;
        } else if (activeTripsLimit==0){
            return LIMIT_NO_CONNECTION;
        } else {
            return LIMIT_OUT;
        }
    }
    public ArrayList<Integer> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<Integer> extras) {
        this.extras = extras;
    }
    public void addExtras(Integer extra){
        this.extras.add(extra);
    }
    public void substractPoints(int diff){
        this.points=this.points-diff;
    }

    public boolean checkExtras(Integer extra){
        if (extras.contains(extra)){
            return true;
        } else
        {
            return false;
        }
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPhotoLargeName(String photoLargeName) {
        this.photoLargeName = photoLargeName;
    }

    public String getPhotoLargeName() {
        return photoLargeName;
    }
}
