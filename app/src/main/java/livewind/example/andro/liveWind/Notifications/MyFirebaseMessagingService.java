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
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "MY_FIREBASE_MESSAGING_SERVICE_TAG";
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
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
            Map<String, String > payload = remoteMessage.getData();
            if(notificationsBoolean) {
                showNotification(payload);
            } else {}
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
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

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
}