package livewind.example.andro.liveWind.HelpClasses;

import android.content.Context;
import android.content.pm.PackageManager;

public class SocialHelper {
    public static String FACEBOOK_URL = "https://www.facebook.com/pg/SurfAdvisorAPP";
    public static String FACEBOOK_PAGE_ID = "553065221866671";


    /**
     * Method to get the right facebook URL to use in the intent
     * @param context
     * @return
     */
    public static String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            return "fb://page/" + FACEBOOK_PAGE_ID;
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
