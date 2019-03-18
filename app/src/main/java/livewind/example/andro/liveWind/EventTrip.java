package livewind.example.andro.liveWind;

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
}
