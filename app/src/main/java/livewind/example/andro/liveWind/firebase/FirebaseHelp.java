package livewind.example.andro.liveWind.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.user.Windsurfer;

public class FirebaseHelp {
    // "Price" for increasing limit:
    public static int POINTS_INCREASE_LIMIT_PRICE = 300;
    private static int POINTS_ADD_RELATION_PRIZE = 50;
    public FirebaseHelp(){}
    /** FIREBASE **/
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    /** FOR USERS DATABASE*/
    private static DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("test/users");
    private static DatabaseReference mEventsDatabaseReference = mFirebaseDatabase.getReference().child("test/events");


    //Add points for created an event
    public static void addPoints(final String uid, final boolean EVENTorTRIP){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(uid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int userPoints = dataSnapshot.child(uid).child("points").getValue(int.class);
                    userPoints = userPoints + POINTS_ADD_RELATION_PRIZE;
                    mUsersDatabaseReference.child(uid).child("points").setValue(userPoints);

                    if (EVENTorTRIP) {
                        int userCreatedEvents = dataSnapshot.child(uid).child("createdEvents").getValue(int.class);
                        int userActiveEvents = dataSnapshot.child(uid).child("activeEvents").getValue(int.class);
                        userCreatedEvents = userCreatedEvents + 1;
                        userActiveEvents = userActiveEvents +1;
                        mUsersDatabaseReference.child(uid).child("createdEvents").setValue(userCreatedEvents);
                        mUsersDatabaseReference.child(uid).child("activeEvents").setValue(userActiveEvents);
                    } else {
                        int userCreatedTrips = dataSnapshot.child(uid).child("createdTrips").getValue(int.class);
                        int userActiveTrips = dataSnapshot.child(uid).child("activeTrips").getValue(int.class);
                        userCreatedTrips = userCreatedTrips + 1;
                        userActiveTrips = userActiveTrips +1;
                        mUsersDatabaseReference.child(uid).child("createdTrips").setValue(userCreatedTrips);
                        mUsersDatabaseReference.child(uid).child("activeTrips").setValue(userActiveTrips);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Remove points for deleting an event
    public static void removePoints(final String uid, final boolean EVENTorTRIP){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(uid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int userPoints = dataSnapshot.child(uid).child("points").getValue(int.class);
                    if(userPoints>= POINTS_ADD_RELATION_PRIZE) {
                        userPoints = userPoints - POINTS_ADD_RELATION_PRIZE;
                    } else {
                        userPoints = 0;
                    }
                    mUsersDatabaseReference.child(uid).child("points").setValue(userPoints);
                    if (EVENTorTRIP) {
                        int userCreatedEvents = dataSnapshot.child(uid).child("createdEvents").getValue(int.class);
                        int userActiveEvents = dataSnapshot.child(uid).child("activeEvents").getValue(int.class);
                        if(userCreatedEvents >0){
                            userCreatedEvents = userCreatedEvents - 1;
                        } else {
                            userCreatedEvents = 0;
                        }
                        if(userActiveEvents >0){
                            userActiveEvents--;
                        } else {
                            userActiveEvents = 0;
                        }
                        mUsersDatabaseReference.child(uid).child("createdEvents").setValue(userCreatedEvents);
                        mUsersDatabaseReference.child(uid).child("activeEvents").setValue(userActiveEvents);
                    } else {
                        int userCreatedTrips = dataSnapshot.child(uid).child("createdTrips").getValue(int.class);
                        int userActiveTrips = dataSnapshot.child(uid).child("activeTrips").getValue(int.class);
                        if(userCreatedTrips>0) {
                            userCreatedTrips = userCreatedTrips - 1;
                        } else {
                            userCreatedTrips = 0;
                        }
                        if(userActiveTrips>0) {
                            userActiveTrips--;
                        } else {
                            userActiveTrips = 0;
                        }
                        mUsersDatabaseReference.child(uid).child("createdTrips").setValue(userCreatedTrips);
                        mUsersDatabaseReference.child(uid).child("activeTrips").setValue(userActiveTrips);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Remove only active event/trip
    public static void removeOnlyActiveData(final String uid, final boolean EVENTorTRIP) {
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        if(EVENTorTRIP) {
            DatabaseReference ref = mUsersDatabaseReference.child(uid).child("activeEvents");
            ref.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentValue = mutableData.getValue(Integer.class);
                    if (currentValue == null || currentValue <= 0) {
                        mutableData.setValue(0);
                    } else {
                        mutableData.setValue(currentValue - 1);
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(
                        DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    System.out.println("Transaction completed");
                }
            });
        } else {
            DatabaseReference ref = mUsersDatabaseReference.child(uid).child("activeTrips");
            ref.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentValue = mutableData.getValue(Integer.class);
                    if (currentValue == null || currentValue <= 0) {
                        mutableData.setValue(0);
                    } else {
                        mutableData.setValue(currentValue - 1);
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(
                        DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    System.out.println("Transaction completed");
                }
            });
        }
    }


    //Increase relations limit for points
    public static void increaseRelationsLimit(final String uid) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(uid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int userPoints = dataSnapshot.child(uid).child("points").getValue(int.class);
                    if (userPoints >= POINTS_INCREASE_LIMIT_PRICE) {
                        userPoints = userPoints - POINTS_INCREASE_LIMIT_PRICE;
                        mUsersDatabaseReference.child(uid).child("points").setValue(userPoints);
                            int userEventsLimit = dataSnapshot.child(uid).child("activeEventsLimit").getValue(int.class);
                            userEventsLimit++;
                            mUsersDatabaseReference.child(uid).child("activeEventsLimit").setValue(userEventsLimit);
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Increase trips limit for points
    public static void increaseTripsLimit(final String uid) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(uid);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int userPoints = dataSnapshot.child(uid).child("points").getValue(int.class);
                    if (userPoints >= POINTS_INCREASE_LIMIT_PRICE) {
                        userPoints = userPoints - POINTS_INCREASE_LIMIT_PRICE;
                        mUsersDatabaseReference.child(uid).child("points").setValue(userPoints);
                        int userTripsLimit = dataSnapshot.child(uid).child("activeTripsLimit").getValue(int.class);
                        userTripsLimit++;
                        mUsersDatabaseReference.child(uid).child("activeTripsLimit").setValue(userTripsLimit);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
