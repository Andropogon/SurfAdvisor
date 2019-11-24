package livewind.example.andro.liveWind;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EventLoader extends AsyncTaskLoader<List<Event>> {

    /** Tag for log messages */
    private static final String LOG_TAG = EventLoader.class.getName();

    private FirebaseDatabase mFirebaseDatabase;
    ChildEventListener mChildEventListener;
    private DatabaseReference mEventsDatabaseReference;
    final List<Event> events = new ArrayList<>();
    /**
     *
     */
    public EventLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Event> loadInBackground() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference= mFirebaseDatabase.getReference().child("test/events");
      //  mEventsDatabaseReference.keepSynced(true);
        attachDatabaseReadListener();
        Log.i("w loaderze sobie siedze","Ilosc eventow:" + events.size());
        return events;
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Event event = dataSnapshot.getValue(Event.class);
                    event.setId(dataSnapshot.getKey());

                    events.add(event);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Event event = dataSnapshot.getValue(Event.class);
                    events.add(event);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mEventsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }
}