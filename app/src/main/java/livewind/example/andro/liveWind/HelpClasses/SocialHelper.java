package livewind.example.andro.liveWind.HelpClasses;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class SocialHelper {
    public static String FACEBOOK_URL = "https://www.facebook.com/pg/SurfAdvisorAPP";
    public static String FACEBOOK_PAGE_ID = "553065221866671";


    /**
     * Method to get the right facebook URL to use in the intent
     *
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

    /**
     * <p>Intent to open the official Facebook app. If the Facebook app is not installed then the
     * default web browser will be used.</p>
     *
     * <p>Example usage:</p>
     * <p>
     * {@code newFacebookIntent(ctx.getPackageManager(), "https://www.facebook.com/JRummyApps");}
     *
     * @param pm  The {@link PackageManager}. You can find this class through {@link
     *            Context#getPackageManager()}.
     * @param url The full URL to the Facebook page or profile.
     * @return An intent that will open the Facebook page/profile.
     */
    public static void openUrl(Context context, PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        if (url.substring(0, 24).equals("https://www.facebook.com")) {
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    // http://stackoverflow.com/a/24547437/1048340
                    uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}