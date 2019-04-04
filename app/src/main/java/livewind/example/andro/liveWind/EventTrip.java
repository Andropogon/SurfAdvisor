package livewind.example.andro.liveWind;

import android.widget.Spinner;
import livewind.example.andro.liveWind.data.EventContract;

/**
 * Created by JGJ on 25/10/18.
 * Header added during refactoring add 05/04/2019 by JGJ.
 *
 * Class for trips, with wrongly designed the inheritance tree on which the database is built. (now I couldn't change it because users use database)
 * It's why Event extends EventTrip (not opposite) and I use strange initEventTrip 'constructors'
 *
 */
public class EventTrip{
    protected String startPlace;
    protected int startCountry;
    protected String startDate;
    protected long timestampStartDate;
    protected int character;
    protected int transport;
    protected int windsurfingAvailable;
    protected int kitesurfingAvailable;
    protected int surfingAvailable;
    protected int cost;
    protected int costDiscount;
    protected int currency;
    protected String costAbout;
    protected int displayAs;

    EventTrip(){
        //It difference trip from coverage
        startDate="DEFAULT";
    }

    /**
     * This strange method is separating EventTrip attributes from Event attributes in Event constructor (for trips).
     * It is required because I have wrongly designed the inheritance tree on which the database is built. (now I couldn't change it because users use database
     */
    public void initEventTrip(String startPlace, int startCountry, String startDate, int transport, int character, int cost, int costDiscount, int currency, String costAbout, int windsurfingAvailable, int kitesurfingAvailable, int surfingAvailable, int displayAs){
        this.startPlace=startPlace;
        this.startCountry=startCountry;
        this.startDate=startDate;
        this.transport=transport;
        this.character=character;
        this.cost=cost;
        this.costDiscount=costDiscount;
        this.currency=currency;
        this.costAbout=costAbout;
        this.windsurfingAvailable=windsurfingAvailable;
        this.kitesurfingAvailable=kitesurfingAvailable;
        this.surfingAvailable=surfingAvailable;
        this.displayAs=displayAs;
    }

    /**
     *  Set methods
     */
    public void setStartPlace(String mStartPlace){
        startPlace=mStartPlace;
    }
    public void setStartCountry(int startCountry) {
        this.startCountry = startCountry;
    }
    public void setStartDate(String mStartDate){
        startDate=mStartDate;
    }
    public void setTimestampStartDate(long timestampStartDate) {
        this.timestampStartDate = timestampStartDate;
    }
    public void setCharacter(int mCharacter){
        character=mCharacter;
    }
    public void setTransport(int mTransport){
        transport=mTransport;
    }
    public void setWindsurfingAvailable(int windsurfingAvailable) {
        this.windsurfingAvailable = windsurfingAvailable;
    }
    public void setKitesurfingAvailable(int kitesurfingAvailable) {
        this.kitesurfingAvailable = kitesurfingAvailable;
    }
    public void setSurfingAvailable(int surfingAvailable) {
        this.surfingAvailable = surfingAvailable;
    }
    public void setCost(int mCost){cost=mCost;}
    public void setCostDiscount(int costDiscount) {
        this.costDiscount = costDiscount;
    }
    public void setCostAbout(String costAbout) {
        this.costAbout = costAbout;
    }
    public void setCurrency(int currency) {
        this.currency = currency;
    }
    public void setDisplayAs(int displayAs) {
        this.displayAs = displayAs;
    }

    /**
     * Get methods
     */
    public String getStartPlace(){
        return startPlace;
    }
    public int getStartCountry() {
        return startCountry;
    }
    public String getStartDate(){
        return startDate;
    }
    public long getTimestampStartDate() {
        return timestampStartDate;
    }
    public int getCharacter(){
        return character;
    }
    public int getTransport(){
        return transport;
    }
    public int getWindsurfingAvailable() {
        return windsurfingAvailable;
    }
    public int getKitesurfingAvailable() {
        return kitesurfingAvailable;
    }
    public int getSurfingAvailable() {
        return surfingAvailable;
    }
    public int getCost() {
        return cost;
    }
    public int getCostDiscount() {
        return costDiscount;
    }
    public String getCostAbout() {
        return costAbout;
    }
    public int getCurrency() {
        return currency;
    }
    public int getDisplayAs() {
        return displayAs;
    }


    /**
     * Additional method for spinner - //TODO this method should be deleted from here
     */
    public void loadStartCountrySpinner(Spinner startCountrySpinner) {
        switch (startCountry) {
            case EventContract.EventEntry.COUNTRY_POLAND:
                startCountrySpinner.setSelection(1);
                break;
            case EventContract.EventEntry.COUNTRY_GREECE:
                startCountrySpinner.setSelection(2);
                break;
            case EventContract.EventEntry.COUNTRY_SPAIN:
                startCountrySpinner.setSelection(3);
                break;
            case EventContract.EventEntry.COUNTRY_CROATIA:
                startCountrySpinner.setSelection(4);
                break;
            case EventContract.EventEntry.COUNTRY_PORTUGAL:
                startCountrySpinner.setSelection(5);
                break;
            case EventContract.EventEntry.COUNTRY_GERMANY:
                startCountrySpinner.setSelection(6);
                break;
            case EventContract.EventEntry.COUNTRY_FRANCE:
                startCountrySpinner.setSelection(7);
                break;
            case EventContract.EventEntry.COUNTRY_SOUTH_AFRICA:
                startCountrySpinner.setSelection(8);
                break;
            case EventContract.EventEntry.COUNTRY_MOROCCO:
                startCountrySpinner.setSelection(9);
                break;
            case EventContract.EventEntry.COUNTRY_ITALY:
                startCountrySpinner.setSelection(10);
                break;
            case EventContract.EventEntry.COUNTRY_EGYPT:
                startCountrySpinner.setSelection(11);
                break;
            case EventContract.EventEntry.COUNTRY_UK:
                startCountrySpinner.setSelection(12);
                break;
            case EventContract.EventEntry.COUNTRY_TURKEY:
                startCountrySpinner.setSelection(13);
                break;
            case EventContract.EventEntry.COUNTRY_AUSTRIA:
                startCountrySpinner.setSelection(14);
                break;
            case EventContract.EventEntry.COUNTRY_DENMARK:
                startCountrySpinner.setSelection(15);
                break;
            case EventContract.EventEntry.COUNTRY_BRAZIL:
                startCountrySpinner.setSelection(16);
                break;
            case EventContract.EventEntry.COUNTRY_USA:
                startCountrySpinner.setSelection(17);
                break;
            case EventContract.EventEntry.COUNTRY_VIETNAM:
                startCountrySpinner.setSelection(18);
                break;
            case EventContract.EventEntry.COUNTRY_MALTA:
                startCountrySpinner.setSelection(19);
                break;
            case EventContract.EventEntry.COUNTRY_OTHER_COUNTRIES:
                startCountrySpinner.setSelection(20);
                break;
            default:
                startCountrySpinner.setSelection(20);
                break;
        }
    }
}
