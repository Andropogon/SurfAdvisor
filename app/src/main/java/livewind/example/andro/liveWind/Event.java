package livewind.example.andro.liveWind;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Spinner;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livewind.example.andro.liveWind.Comments.Comment;
import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.user.Windsurfer;


public class Event extends EventTrip implements Comparable<Event>{

    private String mId;
    private String mUsername;
    private String mUserUId;
    private String mPlace;
    private String mDate;
    private long timestamp;
    private int mType;
    private int mWindPower;
    private double mWaveSize;
    private int mConditions;
    private String mComment;
    private HashMap <String,Comment> mUsersComments = new HashMap<>();
    private String mPhotoUrl;
    private List <MyMember> mMembers = new ArrayList<>();
    private List <MyMember> mThanks = new ArrayList<>();
    private Contact contact = new Contact();
    private int mCountry;
    private int mSharesCounter;

    public Event (){
        mId = "default";
        mUsername = "default";
        mPlace = "default";
        mDate = "default";
        mType = 2;
        mWindPower = 69;
        mWaveSize = 69;
        mConditions = 2;
        mComment = "default";
        mPhotoUrl = "";

    }
    public Event(String id, String username, String userUId, String place, int country, String date, int type, int windPower, double waveSize, int conditions, String comment, String url, String windsurferPhotoName,Context context) {
        mId = id;
        mUsername = username;
        mUserUId = userUId;
        mPlace = place;
        mCountry = country;
        mDate = date;
        mType = type;
        mWindPower = windPower;
        mWaveSize = waveSize;
        mConditions = conditions;
        mComment = comment;
        mPhotoUrl = url;
        MyMember creator = new MyMember(mUsername,0, windsurferPhotoName,context);
        mMembers.add(creator);
        mSharesCounter = 0;
        //StartDate timestamp
        long timestamp = 1000000000;
        timestampStartDate = timestamp * 100000;
        if(!comment.isEmpty()) {
            Comment creatorComment = new Comment(mUsername, windsurferPhotoName, mComment, "0");
            mUsersComments.put("creatorComment", creatorComment);
        }
    }

    //Without date
    public Event(String id, String username, String userUId, String place, int country, int type, int windPower, double waveSize, int conditions, String comment, String url,String windsurferPhotoName,Context context) {
        mId = id;
        mUsername = username;
        mUserUId = userUId;
        mPlace = place;
        mCountry = country;
        mType = type;
        mWindPower = windPower;
        mWaveSize = waveSize;
        mConditions = conditions;
        mComment = comment;
        mPhotoUrl = url;
        MyMember creator = new MyMember(mUsername,0, windsurferPhotoName,context);
        mMembers.add(creator);
        mSharesCounter = 0;
        //StartDate timestamp
        long timestamp = 1000000000;
        timestampStartDate = timestamp * 100000;
        if(!comment.isEmpty()) {
            Comment creatorComment = new Comment(mUsername, windsurferPhotoName, mComment, "0");
            mUsersComments.put("creatorComment", creatorComment);
        }
    }

    public Event(Context mContext, String id, String username, String userUId, String place, int country, String date, int type, int windPower, double waveSize, int conditions, String comment) {
        mId = id;
        mUsername = username;
        mUserUId = userUId;
        mPlace = place;
        mCountry = country;
        mDate = date;
        mType = type;
        mWindPower = windPower;
        mWaveSize = waveSize;
        mConditions = conditions;
        mComment = comment;
        mSharesCounter = 0;
        if(!comment.isEmpty()) {
            Comment creatorComment = new Comment(mUsername, mComment, mContext);
            mUsersComments.put("creatorComment", creatorComment);
        }
        long timestamp = 1000000000;
        timestampStartDate = timestamp * 100000;
    }

    //Without date
    public Event(Context mContext,String id, String username, String userUId, String place, int country, int type, int windPower, double waveSize, int conditions, String comment) {
        mId = id;
        mUsername = username;
        mUserUId = userUId;
        mPlace = place;
        mCountry = country;
        mType = type;
        mWindPower = windPower;
        mWaveSize = waveSize;
        mConditions = conditions;
        mComment = comment;
        mSharesCounter = 0;
        if(!comment.isEmpty()) {
            Comment creatorComment = new Comment(mUsername, mComment,mContext);
            mUsersComments.put("creatorComment", creatorComment);
        }
        long timestamp = 1000000000;
        timestampStartDate = timestamp * 100000;
    }

    public String getmUsername() {return mUsername;}
    public void setUsername(String username){
        mUsername=username;
    }

    public String getId() { return mId; }

    public void setId (String id){
        mId = id;
    }

    public String getPlace() { return mPlace; }



    public void setPlace (String place){
        mPlace =place;
    }
    public String getDate() {
        return mDate;
    }

    public void setDate(String date){
        mDate=date;
    }

    public int getWindPower()
    {
        return  mWindPower;
    }

    public void setWindPower(int windPower){
        mWindPower=windPower;
    }
    public int getType()
    {
        return  mType;
    }

    public void setType(int type){
        mType=type;
    }
    public double getWaveSize()
    {
        return mWaveSize;
    }

    public void setWaveSize(double waveSize){
        mWaveSize=waveSize;
    }
    public int getConditions()
    {
        return mConditions;
    }
    public void setConditions(int conditions){
        mConditions=conditions;
    }

    public String getComment()
    {
        return mComment;
    }
    public void setComment(String comment){
        mComment=comment;
    }
    public String getPhotoUrl()
    {
        return mPhotoUrl;
    }
    public void setPhotoUrl(String url){
        mPhotoUrl = url;
    }

    public List<MyMember> getmMembers(){
        return mMembers;
    }

    public HashMap<String,Comment> getmUsersComments() {
        return mUsersComments;
    }

    public void setmUsersComments(HashMap<String,Comment> mComments) {
        this.mUsersComments = mComments;
    }

    public List<MyMember> getmThanks() {return mThanks;}
    public int getmThanksSize() {
        return mThanks.size();
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", mId);
        result.put("mUsername", mUsername);
        result.put("place", mPlace);
        result.put("date", mDate);
        result.put("windPower", mWindPower);
        result.put("waveSize", mWaveSize);
        result.put("type",mType);
        result.put("comment",mComment);
        result.put("conditions",mConditions);
        result.put("photoUrl",mPhotoUrl);
        result.put("country",mCountry);
        result.put("mSharesCounter", mSharesCounter);
        return result;
    }


    @Override
    public int compareTo(@NonNull Event event) {
        if(event.getStartDate().equals("DEFAULT")) {
            return ComparisonChain.start()
                    .compare(mMembers.size(), event.getmMembers().size(), Ordering.natural().reverse().nullsLast())
                    .result();
        } else {
            return ComparisonChain.start()
                    .compare(startDate, event.getStartDate(), Ordering.natural())
                    .result();
        }
    }

    /** EVENT TRIP **/
    public Event(String id, String username,String userUId, String mStartPlace, int mStartCountry, String place, int country, String mStartDate, String date, String comment, int mTransport, int mCharacter, int mCost,int mCostDiscount,int mCurrency, String mCostAbout,Contact mContact,int mWindsurfingAvailable, int mKitesurfingAvailable, int mSurfingAvailable,int mDisplayAs){
        mId = id;
        mUsername = username;
        mUserUId=userUId;
        startPlace = mStartPlace;
        startCountry =mStartCountry;
        mPlace = place;
        mCountry = country;
        startDate = mStartDate;
        mDate = date;
        mComment = comment;
        character = mCharacter;
        transport = mTransport;
        cost = mCost;
        costDiscount = mCostDiscount;
        currency = mCurrency;
        costAbout = mCostAbout;
        contact = mContact;
        windsurfingAvailable=mWindsurfingAvailable;
        kitesurfingAvailable=mKitesurfingAvailable;
        surfingAvailable=mSurfingAvailable;
        timestampStartDate = startDateTimestamp(mStartDate);
        MyMember creator = new MyMember(mUsername,1);
        mMembers.add(creator);
        displayAs = mDisplayAs;
    }

    /** EVENT TRIP **/
    public Event(String id, Windsurfer creatorWindsurfer, String mStartPlace, int mStartCountry, String place, int country, String mStartDate, String date, String comment, int mTransport, int mCharacter, int mCost, int mCostDiscount, int mCurrency, String mCostAbout, Contact mContact, int mWindsurfingAvailable, int mKitesurfingAvailable, int mSurfingAvailable, int mDisplayAs, Context context){
        mId = id;
        mUsername = creatorWindsurfer.getUsername();
        mUserUId = creatorWindsurfer.getUid();
        startPlace = mStartPlace;
        startCountry =mStartCountry;
        mPlace = place;
        mCountry = country;
        startDate = mStartDate;
        mDate = date;
        mComment = comment;
        character = mCharacter;
        transport = mTransport;
        cost = mCost;
        costDiscount = mCostDiscount;
        currency = mCurrency;
        costAbout = mCostAbout;
        contact = mContact;
        windsurfingAvailable=mWindsurfingAvailable;
        kitesurfingAvailable=mKitesurfingAvailable;
        surfingAvailable=mSurfingAvailable;
        timestampStartDate = startDateTimestamp(mStartDate);
        MyMember creator = new MyMember(mUsername,1, creatorWindsurfer.getPhotoName(),context);
        mMembers.add(creator);
        displayAs = mDisplayAs;
    }

    @Exclude
    public Map<String, Object> tripToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", mId);
        result.put("mUsername", mUsername);
        result.put("startPlace", startPlace);
        result.put("startCountry",startCountry);
        result.put("place", mPlace);
        result.put("country", mCountry);
        result.put("startDate", startDate);
        result.put("date", mDate);
        result.put("comment",mComment);
        result.put("transport",transport);
        result.put("character",character);
        result.put("cost",cost);
        result.put("costDiscount",costDiscount);
        result.put("currency",currency);
        result.put("costAbout",costAbout);
        result.put("windsurfingAvailable",windsurfingAvailable);
        result.put("kitesurfingAvailable",kitesurfingAvailable);
        result.put("surfingAvailable",surfingAvailable);
        result.put("contact",contact);
        result.put("displayAs",displayAs);
        return result;
    }

    public void setContact(String mPhone, String mEmail, String mWeb){
        contact = new Contact(mPhone,mEmail,mWeb);
    }
    public Contact getContact(){
        return contact;
    }

    public Calendar startDateToGC() {
        if(startDate.equals("DEFAULT"))
        {
            GregorianCalendar dataGC = new GregorianCalendar(0,0,0);
            Calendar dataC = dataGC;
            return dataGC;
        } else {
            String mStartDate = startDate.substring(8);
            String day = mStartDate.substring(0, 2);
            String month = mStartDate.substring(3, 5);
            String year = mStartDate.substring(6, 10);
            int dayS = Integer.parseInt(day);
            int monthS = Integer.parseInt(month);
            int yearS = Integer.parseInt(year);
            GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS);
            Calendar dataC = dataGC;
            return dataGC;
        }
    }

    private long startDateTimestamp(String mStartDateBadFormat){
        if(mStartDateBadFormat.equals("DEFAULT"))
        {
            long timestamp = 1000000000;
            timestamp = timestamp * 100000;
            return timestamp;
        } else {
            String mStartDate = mStartDateBadFormat.substring(8);
            String day = mStartDate.substring(0, 2);
            String month = mStartDate.substring(3, 5);
            String year = mStartDate.substring(6, 10);
            int dayS = Integer.parseInt(day);
            int monthS = Integer.parseInt(month) -1 ; //because months are indexing from 0
            int yearS = Integer.parseInt(year);
            GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS,24,59,59);
            Calendar dataC = dataGC;
            long timestamp = dataC.getTimeInMillis();
            return timestamp;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long mTimestamp){
        timestamp =mTimestamp;
    }

    public int getTimestampINT() {
        int timestampINT = (int)timestamp/1000;
        return timestampINT;
    }

    public int getCountry() {
        return mCountry;
    }

    public void setCountry(int mCountry) {
        this.mCountry = mCountry;
    }


    public String getmUserUId() {
        return mUserUId;
    }

    public void setmUserUId(String mUserUId) {
        this.mUserUId = mUserUId;
    }

    public void setmSharesCounter(int mSharesCounter) {
        this.mSharesCounter = mSharesCounter;
    }

    public int getmSharesCounter() {
        return mSharesCounter;
    }

    public void loadCountrySpinner(Spinner countrySpinner) {
        switch (mCountry) {
            case EventContract.EventEntry.COUNTRY_POLAND:
                countrySpinner.setSelection(1);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                countrySpinner.setSelection(2);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                countrySpinner.setSelection(3);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                countrySpinner.setSelection(4);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                countrySpinner.setSelection(5);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                countrySpinner.setSelection(6);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                countrySpinner.setSelection(7);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                countrySpinner.setSelection(8);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                countrySpinner.setSelection(9);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                countrySpinner.setSelection(10);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                countrySpinner.setSelection(11);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                countrySpinner.setSelection(12);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                countrySpinner.setSelection(13);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                countrySpinner.setSelection(14);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                countrySpinner.setSelection(15);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                countrySpinner.setSelection(16);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                countrySpinner.setSelection(17);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                countrySpinner.setSelection(18);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                countrySpinner.setSelection(19);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                countrySpinner.setSelection(20);
                break;
            default:
                countrySpinner.setSelection(20);
                break;
        }
    }
}
