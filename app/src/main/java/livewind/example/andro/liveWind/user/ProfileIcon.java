package livewind.example.andro.liveWind.user;

public class ProfileIcon {
    private int iconId;
    private int iconLargeId;
    private String iconName;
    private boolean iconUnlock;

    public ProfileIcon(int mIconId, String mIconName, boolean mIconUnlock, int mIconLargeId){
        iconId=mIconId;
        iconName=mIconName;
        iconUnlock=mIconUnlock;
        iconLargeId = mIconLargeId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setIconUnlock(boolean iconLock) {
        this.iconUnlock = iconLock;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public String getIconName() {
        return iconName;
    }

    public boolean isIconUnlock() {
        return iconUnlock;
    }

    public void setIconLargeId(int iconLargeId) {
        this.iconLargeId = iconLargeId;
    }

    public int getIconLargeId() {
        return iconLargeId;
    }
}
