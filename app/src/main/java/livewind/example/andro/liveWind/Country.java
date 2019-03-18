package livewind.example.andro.liveWind;

public class Country {
    private int flagId;
    private String name;
    private boolean checked;

    Country(String mCountryName, int mCountryFlagId){
        name=mCountryName;
        flagId=mCountryFlagId;
        checked=false;
    }

    Country(String mCountryName, int mCountryFlagId,boolean mChecked){
        name=mCountryName;
        flagId=mCountryFlagId;
        checked=mChecked;
    }

    public int getFlagId() {
        return flagId;
    }

    public String getName() {
        return name;
    }

    public void setFlagId(int flagId) {
        this.flagId = flagId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
