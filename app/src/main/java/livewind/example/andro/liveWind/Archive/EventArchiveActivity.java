package livewind.example.andro.liveWind.Archive;

import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.firebase.FirebaseContract;
/**
 * Created by JGJ on 29/05/19.
 *
 * Archive events (coverages) activity
 *
 */
public class EventArchiveActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EventArchiveAdapter mEventArchiveAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private Query mEventsArchiveQuery;

    private PagedList.Config mPagedConfig = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5)
            .setPageSize(10)
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_events);

        initFirebase();
        initViews();
        initListners();
        mRecyclerView.setAdapter(mEventArchiveAdapter);

    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsArchiveQuery = mFirebaseDatabase.getReference().child(FirebaseContract.FirebaseEntry.TABLE_ARCHIVE_EVENTS);
    }

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.archive_swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        mEventArchiveAdapter = new EventArchiveAdapter(this, mEventsArchiveQuery,mSwipeRefreshLayout,this,mPagedConfig);
    }

    private void initListners(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEventArchiveAdapter.refresh();
            }
        });
    }


    //Start Listening Adapter
    @Override
    protected void onStart() {
        super.onStart();
        mEventArchiveAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    protected void onStop() {
        super.onStop();
        mEventArchiveAdapter.stopListening();
    }


}
