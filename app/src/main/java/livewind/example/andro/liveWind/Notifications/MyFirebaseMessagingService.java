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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.Event;
import livewind.example.andro.liveWind.EventActivity;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.user.Windsurfer;

import static livewind.example.andro.liveWind.ExtraInfoHelp.putCoverageToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putInfoToIntent;
import static livewind.example.andro.liveWind.ExtraInfoHelp.putWindsurferToIntent;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "MY_FIREBASE_MESSAGING_SERVICE_TAG";
    final String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    /**
     * Notification codes
     */
    final static String NOTIFICATION_NEW_LIKE = "NEW_LIKE";
    final static String NOTIFICATION_NEW_COVERAGE = "NEW_COVERAGE";
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
    private DatabaseReference mUsersNicknamesDatabaseReference = mFirebaseDatabase.getReference().child("usernames");
    private Windsurfer mWindsurfer= new Windsurfer();

    public MyFirebaseMessagingService(){};
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

    private void sendRegistrationToServer(String token) {
        SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = displayOptions.edit();
        editor.putString(getString(livewind.example.andro.liveWind.R.string.user_tokenId_shared_preference),token);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userUid = sharedPref.getString(getApplicationContext().getString(livewind.example.andro.liveWind.R.string.user_uid_shared_preference),"DEFAULT");
        Log.d(TAG, "Refreshed token of " + userUid + " TOKEN: " + token);

        mUsersDatabaseReference.child(userUid).child("tokenId").setValue(token, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null) {
                    Log.e(TAG, "Failed to write message", databaseError.toException());
                }
            }
        });
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean notificationsBoolean = sharedPref.getBoolean(getApplicationContext().getString(R.string.settings_notifications_allow_key), true);
        // Check if message contains a data payload.
        Log.i("NOTIFICATION",Boolean.toString(notificationsBoolean));
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> payload = remoteMessage.getData();
            if (notificationsBoolean) {
                String payloadCode = payload.get("notificationCode");
                switch (payloadCode) {
                    case NOTIFICATION_NEW_LIKE:
                        showNotification(payload);
                        break;
                    case NOTIFICATION_NEW_COVERAGE:
                        showNewCoverageNotification(payload);
                        break;
                    default:
                        showNotification(payload);
                }
            }else{
            }
/**
 if ( Check if data needs to be processed by long running job  true) {
 // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
 scheduleJob();
 } else{
 // Handle message within 10 seconds
 handleNow();
 }
 */

        }
/**
 // Check if message contains a notification payload.
 if (remoteMessage.getNotification() != null) {
 Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
 }

 // Also if you intend on generating your own notifications as a result of a received FCM
 // message, here is where that should be initiated. See sendNotification method below.
 */

    }

    private void showNotification(Map<String, String> payload) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, CatalogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_thumb_up_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon_v3))
                .setTicker(getApplicationContext().getString(R.string.notification_relation_liked_title))
                //     .setPriority(Notification.PRIORITY_MAX)
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
     *
     * @param data Map which has the message data in it
     */
    private void showNewCoverageNotification(Map<String, String> data) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create the pending intent to launch the activity


        String place = data.get("place");
        String windPower = data.get("wind_power");
        String waveSize = data.get("wave_size");
        String comment = data.get("comment");
        Event newEvent = new Event(getApplicationContext(), data.get("id"), data.get("username"), data.get("uid"), place, Integer.valueOf(data.get("country")), Integer.valueOf(data.get("type")), Integer.valueOf(windPower), Double.valueOf(waveSize), Integer.valueOf(data.get("conditions")), comment, data.get("photoUrl"), "user_icon_goya_banzai_24");
        newEvent.setmSharesCounter(Integer.valueOf(data.get("sharesCounter")));
        //Check if it's a new user or not

        if (checkUser()) {
            //If we get user data from firebase lets make notification
            intent = putCoverageToIntent(intent, newEvent, mWindsurfer, getApplicationContext());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String uid = sharedPrefs.getString(getString(R.string.user_uid_shared_preference), "0");
            mWindsurfer.setUid(uid); // Set only uid, other windsurfer data will be set in EventActivity
            mWindsurfer.setUsername("NOTIFICATION");
            putWindsurferToIntent(intent, mWindsurfer, getApplicationContext());

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_thumb_up_black_24dp)
                    .setColor(getColor(R.color.notifications_color))
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.notifiaction_new_coverage))
                    .setTicker(getApplicationContext().getString(R.string.notification_new_coverage_title))
                    //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(getApplicationContext().getString(R.string.notification_new_coverage_title) + place)
                    .setContentText(place + " - " + windPower + "kn, " + waveSize + "m, " + comment)
                    .setContentInfo("Info")
                    .setTimeoutAfter(28800000L) //After 8h notification should be canceled
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(data.hashCode(), notificationBuilder.build());
        }
    }

    //Function for gets user data using shared pref
    private boolean checkUser() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String uid = sharedPrefs.getString(getString(R.string.user_uid_shared_preference), "0");
        if (uid.equals("0")) {
            //Don't create notification
            return false;
        }
        return true;
    }
}