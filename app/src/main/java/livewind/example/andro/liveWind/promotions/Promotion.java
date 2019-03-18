package livewind.example.andro.liveWind.promotions;

public class Promotion {
    private String titlePL;
    private String titleENG;
    private String descriptionPL;
    private String descriptionENG;

    private int type;


    public Promotion(){};
    public Promotion(String mTitlePL, String mTitleENG, String mDescriptionPL, String mDescriptionENG, int mType){
        titlePL=mTitlePL;
        titleENG=mTitleENG;
        descriptionPL=mDescriptionPL;
        descriptionENG=mDescriptionENG;
        type = mType;
    }

    public void setTitlePL(String titlePL) {
        this.titlePL = titlePL;
    }

    public void setTitleENG(String titleENG) {
        this.titleENG = titleENG;
    }


    public String getDescriptionENG() {
        return descriptionENG;
    }

    public String getDescriptionPL() {
        return descriptionPL;
    }

    public String getTitleENG() {
        return titleENG;
    }

    public String getTitlePL() {
        return titlePL;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
