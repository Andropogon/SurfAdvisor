package livewind.example.andro.liveWind.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Event;
import livewind.example.andro.liveWind.EventActivity;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.user.Windsurfer;

import static livewind.example.andro.liveWind.ExtraInfoHelp.putCoverageToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putNotificationToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;

/**
 * Created by JGJ on 10/11/18.
 * Header added during refactoring add 08/04/2019 by JGJ.
 *
 * Receive and show notifications in correct way.
 * Currently notifications types:
 * -New like notification
 * -New coverage notification
 *
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String NOTIFICATION_NEW_LIKE_CHANNEL_ID = "like_noti_id";
    final String NOTIFICATION_NEW_COVERAGE_CHANNEL_ID = "coverage_noti_id";
    final String NOTIFICATION_NEW_CONTENT_CHANNEL_ID = "content_noti_id";
    /**
     * Notification codes
     */
    public final static String NOTIFICATION_NEW_LIKE = "NOTIFICATION_NEW_LIKE";
    public final static String NOTIFICATION_NEW_COVERAGE = "NOTIFICATION_NEW_COVERAGE";
    public final static String NOTIFICATION_NEW_CONTENT = "NOTIFICATION_NEW_CONTENT";
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
    private Windsurfer mWindsurfer= new Windsurfer();

    public MyFirebaseMessagingService(){}

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Send registration token to users database
     * @param token
     */
    private void sendRegistrationToServer(String token) {
        SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = displayOptions.edit();
        editor.putString(getString(livewind.example.andro.liveWind.R.string.user_tokenId_shared_preference),token);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userUid = sharedPref.getString(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.user_uid_shared_preference),"DEFAULT");
        mUsersDatabaseReference.child(userUid).child("tokenId").setValue(token);
    }

    /**
     * Method that check user notification settings and call correct showNotification method.
     * @param remoteMessage - received message
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> payload = remoteMessage.getData();
            //Check that user allow notifications
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean notificationsBoolean = sharedPref.getBoolean(getApplicationContext().getString(R.string.settings_notifications_allow_key), true);
            if (notificationsBoolean) {
                //Check what type of notification is this one
                String payloadCode = payload.get("notificationCode");
                switch (payloadCode) {
                    case NOTIFICATION_NEW_LIKE:
                        showNewLikeNotification(payload);
                        break;
                    case NOTIFICATION_NEW_COVERAGE:
                        boolean notificationsNewCoverageBoolean = sharedPref.getBoolean(getApplicationContext().getString(R.string.settings_notifications_allow_about_new_coverage_key), true);
                        if(notificationsNewCoverageBoolean) {
                            showNewCoverageNotification(payload);
                        }
                        break;
                    case NOTIFICATION_NEW_CONTENT:
                        boolean notificationsNewContentBoolean = sharedPref.getBoolean(getApplicationContext().getString(R.string.settings_notifications_allow_about_new_content_key), true);
                        if(notificationsNewContentBoolean) {
                            showNewContentNotification(payload);
                        }
                        break;
                    default:
                        showNewLikeNotification(payload);
                }
            }
        }
    }

    /**
     * Create and show notification about new like under your coverage
     * @param payload Map which has the message payload in it
     */
    private void showNewLikeNotification(Map<String, String> payload) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_NEW_LIKE_CHANNEL_ID, "New like notification", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Notification channel for new like notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 500, 500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, CatalogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_NEW_LIKE_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_thumb_up_black_24dp)
                .setColor(getColor(R.color.light_green))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon_v3))
                .setTicker(getApplicationContext().getString(R.string.notification_relation_liked_title))
                .setContentTitle(getApplicationContext().getString(R.string.notification_relation_liked_title))
                .setContentText(getApplicationContext().getString(R.string.notification_relation_liked_text1) +" "+ payload.get("eventName") +" "+ getApplicationContext().getString(R.string.notification_relation_liked_text2)+payload.get("eventThanksSize")+").")
                .setContentInfo("Info")
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(payload.hashCode(), notificationBuilder.build());
    }

    /**
     * Create and show notification about new coverage
     * @param payload Map which has the message payload in it about created coverage
     */
    private void showNewCoverageNotification(Map<String, String> payload) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_NEW_COVERAGE_CHANNEL_ID, "New coverage notification", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Notification channel for new coverage notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Data to show in notification
        String place = payload.get("place");
        String windPower = payload.get("wind_power");
        String waveSize = payload.get("wave_size");
        String comment = payload.get("comment");
        //Make event to put to pendingIntent
        Event newEvent = new Event(getApplicationContext(), payload.get("id"), payload.get("username"), payload.get("uid"), place, Integer.valueOf(payload.get("country")), Integer.valueOf(payload.get("type")), Integer.valueOf(windPower), Double.valueOf(waveSize), Integer.valueOf(payload.get("conditions")), comment, payload.get("photoUrl"), "user_icon_goya_banzai_24");
        newEvent.setmSharesCounter(Integer.valueOf(payload.get("sharesCounter")));
        //Check if user has uid in sharedPref
        if (checkUser()) {
            //If we got user uid from sharedPref
            intent = putCoverageToIntent(intent, newEvent, mWindsurfer, getApplicationContext());
            //Set temporary user username to download payload in eventActivity
            mWindsurfer.setUsername(MyFirebaseMessagingService.NOTIFICATION_NEW_COVERAGE);
            putWindsurferToIntent(intent, mWindsurfer, getApplicationContext());

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_NEW_COVERAGE_CHANNEL_ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_thumb_up_black_24dp)
                    .setColor(getColor(R.color.notifications_main_color))
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.notification_new_coverage))
                    .setTicker(getApplicationContext().getString(R.string.notification_new_coverage_title))
                    .setContentTitle(getApplicationContext().getString(R.string.notification_new_coverage_title) + place)
                    .setContentText(place + " - " + windPower + "kn, " + waveSize + "m, " + comment)
                    .setTimeoutAfter(28800000L) //After 8h notification should be canceled
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(payload.hashCode(), notificationBuilder.build());
        }
    }

    /**
     * Function for gets user uid from sharedPref
     * @return true if user has uid in sharedPref
     */
    private boolean checkUser() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String uid = sharedPrefs.getString(getString(R.string.user_uid_shared_preference), "0");
        if (uid.equals("0")) {
            //Don't create notification
            return false;
        }
        mWindsurfer.setUid(uid); // Set only uid, other windsurfer data will be set in EventActivity
        return true;
    }

    /**
     * Create and show notification about new content
     * @param payload Map which has the message payload in it about new content.
     */
    private void showNewContentNotification(Map<String, String> payload) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_NEW_CONTENT_CHANNEL_ID, "New content notification", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Notification channel for new content notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, CatalogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Make {@NewContentNotification} to put to pendingIntent
        NewContentNotification newContentNotification = new NewContentNotification(payload.get(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_TITLE),
                payload.get(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_DESCRIPTION),
                payload.get(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_ACTION_TITLE),
                payload.get(NewContentNotification.NewContentNotificationEntry.NEW_CONTENT_ACTION_LINK));

        putNotificationToIntent(intent, newContentNotification);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_NEW_COVERAGE_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_thumb_up_black_24dp)
                .setColor(getColor(R.color.notifications_new_content_color))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon_v3)) //TODO maybe main app icon?
                .setTicker(getApplicationContext().getString(R.string.notification_new_content_title))
                .setContentTitle(newContentNotification.getTitle())
                .setTimeoutAfter(28800000L) //After 8h notification should be canceled
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        if(newContentNotification.getDescription().length()>=40) {
            notificationBuilder.setContentText(getApplicationContext().getString(R.string.notification_new_content_click_to_get_more));
        } else {
            notificationBuilder.setContentText(newContentNotification.getDescription());
        }
        notificationManager.notify(payload.hashCode(), notificationBuilder.build());
    }
}