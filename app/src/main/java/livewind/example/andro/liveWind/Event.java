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

/**
 * Created by JGJ on 10/10/18.
 * Header added during refactoring add 04/04/2019 by JGJ.
 *
 * Class for coverages and trips, with wrongly designed the inheritance tree on which the database is built. (now I couldn't change it because users use database)
 * It's why Event extends EventTrip (not opposite) and I use strange initEventTrip 'constructors'
 *
 */
public class Event extends EventTrip implements Comparable<Event>{

    private String mId;
    private String mUsername;
    private String mUserUId;
    private String mPlace;
    private int mCountry;
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
    private int mSharesCounter;

    /**
     *  COVERAGE
     */
    public Event () {
        mId = "new_event";
    }
    public Event(Context context, String id, String username, String userUId, String place, int country, int type, int windPower, double waveSize, int conditions, String comment, String url,String windsurferPhotoName) {
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
        //unreal big timestamp (because Events are deleting when current time>= timestampStartDate
        timestampStartDate = 100000000000000L;
        if(!comment.isEmpty()) {
            Comment creatorComment = new Comment(mUsername, windsurferPhotoName, mComment, "0");
            mUsersComments.put("creatorComment", creatorComment);
        }
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

    /**
     * EVENT TRIP
     */
    public Event(Context context, String id, Windsurfer creatorWindsurfer, String startPlace, int startCountry, String place, int country, String startDate, String date, String comment, int transport, int character, int cost, int costDiscount, int currency, String costAbout, Contact mContact, int windsurfingAvailable, int kitesurfingAvailable, int surfingAvailable, int displayAs){
        //Init EventTrip attributes
        super.initEventTrip(startPlace,startCountry,startDate,transport,character,cost,costDiscount,currency,costAbout,windsurfingAvailable,kitesurfingAvailable,surfingAvailable,displayAs);
        //Init shared attributes
        mId = id;
        mUsername = creatorWindsurfer.getUsername();
        mUserUId = creatorWindsurfer.getUid();
        mPlace = place;
        mCountry = country;
        mDate = date;
        mComment = comment;
        contact = mContact;
        timestampStartDate = startDateTimestamp(startDate);
        MyMember creator = new MyMember(mUsername,1, creatorWindsurfer.getPhotoName(),context);
        mMembers.add(creator);
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

    /**
     *  Set methods
     */
    public void setId (String id){
        mId = id;
    }
    public void setUsername(String username){
        mUsername=username;
    }
    public void setmUserUId(String mUserUId) {
        this.mUserUId = mUserUId;
    }
    public void setPlace (String place){
        mPlace =place;
    }
    public void setCountry(int mCountry) {
        this.mCountry = mCountry;
    }
    public void setDate(String date){
        mDate=date;
    }
    public void setTimestamp(long mTimestamp){
        timestamp =mTimestamp;
    }
    public void setType(int type){
        mType=type;
    }
    public void setWindPower(int windPower){
        mWindPower=windPower;
    }
    public void setWaveSize(double waveSize){
        mWaveSize=waveSize;
    }
    public void setConditions(int conditions){
        mConditions=conditions;
    }
    public void setComment(String comment){
        mComment=comment;
    }
    public void setPhotoUrl(String url){
        mPhotoUrl = url;
    }
    public void setContact(String mPhone, String mEmail, String mWeb){
        contact = new Contact(mPhone,mEmail,mWeb);
    }
    public void setmSharesCounter(int mSharesCounter) {
        this.mSharesCounter = mSharesCounter;
    }

    /**
     * Get methods
     */
    public String getId() { return mId; }
    public String getmUsername() {return mUsername;}
    public String getmUserUId() {
        return mUserUId;
    }
    public String getPlace() { return mPlace; }
    public int getCountry() {
        return mCountry;
    }
    public String getDate() {
        return mDate;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public int getType()
    {
        return  mType;
    }
    public int getWindPower()
    {
        return  mWindPower;
    }
    public double getWaveSize()
    {
        return mWaveSize;
    }
    public int getConditions()
    {
        return mConditions;
    }
    public String getComment()
    {
        return mComment;
    }
    public String getPhotoUrl()
    {
        return mPhotoUrl;
    }
    public Contact getContact(){
        return contact;
    }

    public List<MyMember> getmMembers(){
        return mMembers;
    }
    public int getmSharesCounter() {
        return mSharesCounter;
    }
    public int getTimestampINT() {
        int timestampINT = (int)timestamp/1000;
        return timestampINT;
    }
    //Without this method counter on EventAdapter doesn't works
    public List<MyMember> getmThanks() {return mThanks;}
    public int getmThanksSize() {
        return mThanks.size();
    }

    /**
     * Additional methods for data-time-timestamp processing
     */
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

    /**
     * Additional methods for spinner - //TODO this method should be deleted from here
     */
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

    /**
     * Additional methods for comparing - not used at all but needed for other Comparators
     */
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
}
