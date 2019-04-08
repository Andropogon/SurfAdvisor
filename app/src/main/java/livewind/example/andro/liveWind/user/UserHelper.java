package livewind.example.andro.liveWind.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import livewind.example.andro.liveWind.R;

public class UserHelper {
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
    //Function for gets user data using shared pref
    public static void downloadUserData(final Windsurfer currentWindsurfer, final Context activityContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activityContext);
        final String uid = sharedPrefs.getString(activityContext.getString(R.string.user_uid_shared_preference), "0");

        if (uid.equals("0")) {
            Toast.makeText(activityContext.getApplicationContext(), "Restart app", Toast.LENGTH_SHORT).show();
        } else {
            Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(uid);
            usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Windsurfer tempWindsurfer = dataSnapshot.child(uid).getValue(Windsurfer.class);
                        currentWindsurfer.loadWindsurferData(tempWindsurfer.getUsername(),tempWindsurfer.getEmail(),tempWindsurfer.getPoints(),tempWindsurfer.getCreatedEvents(),tempWindsurfer.getCreatedTrips(),activityContext);
                    } else {
                        Toast.makeText(activityContext.getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
