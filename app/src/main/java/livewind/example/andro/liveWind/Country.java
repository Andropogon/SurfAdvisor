package livewind.example.andro.liveWind;

public class Country {
    private int flagId;
    private int nameId;
    private String name;
    private boolean checked;

    public Country(String mCountryName, int mCountryFlagId){
        name=mCountryName;
        flagId=mCountryFlagId;
        checked=false;
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
