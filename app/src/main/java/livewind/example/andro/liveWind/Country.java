package livewind.example.andro.liveWind;

public class Country {
    private int flagId;
    private int nameId;
    private String name;
    private boolean checked;
    private String topicKey;

    public Country(String countryName, int countryFlagId,String topicKey){
        this.name=countryName;
        this.flagId=countryFlagId;
        this.checked=false;
        this.topicKey=topicKey;
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

    public String getTopicKey() {
        return topicKey;
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
