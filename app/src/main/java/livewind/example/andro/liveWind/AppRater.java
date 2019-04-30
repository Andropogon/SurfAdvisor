package livewind.example.andro.liveWind;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Locale;

import livewind.example.andro.liveWind.Countries.CountryDialog;
import livewind.example.andro.liveWind.Notifications.NewContentNotification;

import static livewind.example.andro.liveWind.Countries.CountryDialog.loadCountriesToList;

public class AppRater {
    private final static String APP_TITLE = " SurfAdvisor";// App Name
    private final static String APP_PNAME = "livewind.example.andro.liveWind";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 4;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 7;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();
        // Set new content notifications to true after update
        Long date_firstLaunchAfterUpdate = prefs.getLong("date_firstLaunchAfterUpdate", 0);
        if (date_firstLaunchAfterUpdate == 0) {
            editor.putBoolean(mContext.getString(R.string.settings_notifications_allow_about_new_content_key),true);
            date_firstLaunchAfterUpdate = System.currentTimeMillis();
            editor.putLong("date_firstLaunchAfterUpdate", date_firstLaunchAfterUpdate);
            editor.apply();
        }

        if (prefs.getBoolean("dontshowagain", false)) { return ; }
        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch2", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch2", date_firstLaunch);
        }


        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(mContext.getString(R.string.app_rater_rate) + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        //ll.setMinimumWidth(3600);
        ll.setPadding(20,20,20,40);
        ll.setBackgroundColor((mContext.getColor(R.color.app_main_color)));
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setGravity(Gravity.CENTER | Gravity.TOP);

        ImageView iv= new ImageView(mContext);
        iv.setImageResource(R.drawable.app_icon_v3);
        ll.addView(iv);
        TextView tvTitle = new TextView(mContext);
        tvTitle.setText(mContext.getString(R.string.app_rater_rate)+ APP_TITLE );
        tvTitle.setBackgroundColor((mContext.getColor(R.color.app_main_color)));
        tvTitle.setGravity(Gravity.CENTER | Gravity.TOP);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(800,ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle.setPadding(8, 10, 8, 20);
        ll.addView(tvTitle);

        TextView tv = new TextView(mContext);
        tv.setText(mContext.getString(R.string.app_rater_text_1)+ APP_TITLE + mContext.getString(R.string.app_rater_text_2));
        tv.setBackgroundColor((mContext.getColor(R.color.app_main_color)));
        tv.setGravity(Gravity.CENTER | Gravity.TOP);
        tv.setLayoutParams(new LinearLayout.LayoutParams(800,ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(8, 10, 8, 20);
        ll.addView(tv);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(600,150);
        lp.setMargins(0, 10, 0, 10);

        Button b1 = new Button(mContext);
        b1.setBackground(mContext.getDrawable(R.drawable.custom_button_rate));
        b1.setLayoutParams(lp);
        //b1.setLayoutParams(new TableRow.LayoutParams(600,ViewGroup.LayoutParams.WRAP_CONTENT));
        b1.setGravity(Gravity.CENTER );
        b1.setText(mContext.getString(R.string.app_rater_rate) + APP_TITLE);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME)));
                }
                //https://developer.android.com/distribute/marketing-tools/linking-to-google-play#java
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setLayoutParams(lp);
        b2.setGravity(Gravity.CENTER);
        b2.setBackground(mContext.getDrawable(R.drawable.custom_button_rate));
        b2.setText(mContext.getString(R.string.app_rater_later));
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                SharedPreferences.Editor editor = prefs.edit();
                long launch_count = 1;
                editor.putLong("launch_count", 0);
                editor.apply();
                Long date_firstLaunch = prefs.getLong("date_firstlaunch2", 0);
                date_firstLaunch = System.currentTimeMillis();
                editor.putLong("date_firstlaunch2", date_firstLaunch);
                editor.apply();
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setLayoutParams(lp);
        b3.setGravity(Gravity.CENTER);
        b3.setBackground(mContext.getDrawable(R.drawable.custom_button_rate));
        b3.setText(mContext.getString(R.string.app_rater_no));
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
    }
}