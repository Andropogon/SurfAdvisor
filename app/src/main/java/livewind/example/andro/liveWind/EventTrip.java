package livewind.example.andro.liveWind;

import android.widget.Spinner;

import livewind.example.andro.liveWind.data.EventContract;

public class EventTrip{
    protected String startPlace;
    protected int startCountry;
    protected String startDate;
    protected int character;
    protected int transport;
    protected long timestampStartDate;
    protected int windsurfingAvailable;
    protected int kitesurfingAvailable;
    protected int surfingAvailable;

    protected int cost;
    protected int costDiscount;
    protected int currency;
    protected String costAbout;

    protected int displayAs;

    EventTrip(){
        startDate="DEFAULT";
    }

    void setStartPlace(String mStartPlace){
        startPlace=mStartPlace;
    }
    void setStartDate(String mStartDate){
        startDate=mStartDate;
    }
    void setTransport(int mTransport){
        transport=mTransport;
    }
    void setCharacter(int mCharacter){
        character=mCharacter;
    }
    void setCost(int mCost){cost=mCost;}

    public void setCostAbout(String costAbout) {
        this.costAbout = costAbout;
    }

    public String getStartPlace(){
        return startPlace;
    }
    public String getStartDate(){
        return startDate;
    }
    public int getTransport(){
        return transport;
    }
    public int getCharacter(){
        return character;
    }

    public int getCost() {
        return cost;
    }

    public String getCostAbout() {
        return costAbout;
    }

    public long getTimestampStartDate() {
        return timestampStartDate;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getCurrency() {
        return currency;
    }

    public void setTimestampStartDate(long timestampStartDate) {
        this.timestampStartDate = timestampStartDate;
    }

    public void setStartCountry(int startCountry) {
        this.startCountry = startCountry;
    }

    public int getStartCountry() {
        return startCountry;
    }

    public int getKitesurfingAvailable() {
        return kitesurfingAvailable;
    }

    public int getSurfingAvailable() {
        return surfingAvailable;
    }

    public int getWindsurfingAvailable() {
        return windsurfingAvailable;
    }

    public void setKitesurfingAvailable(int kitesurfingAvailable) {
        this.kitesurfingAvailable = kitesurfingAvailable;
    }

    public void setSurfingAvailable(int surfingAvailable) {
        this.surfingAvailable = surfingAvailable;
    }

    public void setWindsurfingAvailable(int windsurfingAvailable) {
        this.windsurfingAvailable = windsurfingAvailable;
    }

    public int getCostDiscount() {
        return costDiscount;
    }

    public void setCostDiscount(int costDiscount) {
        this.costDiscount = costDiscount;
    }

    public void setDisplayAs(int displayAs) {
        this.displayAs = displayAs;
    }

    public int getDisplayAs() {
        return displayAs;
    }

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
