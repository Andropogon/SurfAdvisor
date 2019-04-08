package livewind.example.andro.liveWind.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import livewind.example.andro.liveWind.R;

public class FirebasePromotions {
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    /**
     * Download shared pref promotions code and put them to shared pref
     * @param context
     */
    public static void getSurfAdvisorPromotionCode(final Context context){
        DatabaseReference mRemovingReference= mFirebaseDatabase.getReference().child("discount_code");
        mRemovingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String discountCode = (String) dataSnapshot.getValue();
                SharedPreferences displayOptions = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = displayOptions.edit();
                editor.putString(context.getString(R.string.settings_discount_code_key),discountCode);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
