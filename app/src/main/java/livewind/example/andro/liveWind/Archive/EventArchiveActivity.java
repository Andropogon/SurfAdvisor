package livewind.example.andro.liveWind.Archive;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.firebase.FirebaseContract;

public class EventArchiveActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsArchiveDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_event);

        initViews();
        initFirebase();
    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsArchiveDatabaseReference = mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_ARCHIVE_EVENTS);
    }

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.archive_swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
    }
}
