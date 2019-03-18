package livewind.example.andro.liveWind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import livewind.example.andro.liveWind.user.Windsurfer;

public class EditorChoose extends AppCompatActivity {

    private Windsurfer mWindsurfer;

    /**
     * Trip Type
     *  0 = ORGANIZED TRIP
     *  1 = PRIVATE TRIP
     *  2 = CAMP
     */
    public static final int TRIP_TYPE_ORGANIZED = 0;
    public static final int TRIP_TYPE_PRIVATE = 1;
    public static final int TRIP_TYPE_CAMP = 2;

    private int mTripType;

    /** HELP FOR INTENT PUT EXTRA */
    private ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_editor);
        //Get windsurfer data
        Intent intent = getIntent();
        mWindsurfer = mExtraInfoHelp.getWindsurferFromIntent(intent,getApplicationContext());

        RelativeLayout relationRelativeLayout = (RelativeLayout) findViewById(R.id.activity_choose_editor_relation_relative_layout);
        RelativeLayout organizedTripLayout = (RelativeLayout) findViewById(R.id.activity_choose_editor_organized_trip_relative_layout);
        RelativeLayout privateTripLayout = (RelativeLayout) findViewById(R.id.activity_choose_editor_private_trip_relative_layout);
        RelativeLayout campTripLayout = (RelativeLayout) findViewById(R.id.activity_choose_editor_trip_camp_relative_layout);

        relationRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(EditorChoose.this, EditorActivity.class);
                //PutExtra user data to know who make event
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                startActivity(intent);
                finish();
            }
        });
        organizedTripLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mTripType = TRIP_TYPE_ORGANIZED;
                Intent intent = new Intent(EditorChoose.this, EditorTripActivity.class);
                //PutExtra user data to know who make event
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                intent.putExtra(getString(R.string.EXTRA_TRIP_TYPE),mTripType);
                startActivity(intent);
                finish();
            }
        });
        privateTripLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mTripType = TRIP_TYPE_PRIVATE;
                Intent intent = new Intent(EditorChoose.this, EditorTripActivity.class);
                //PutExtra user data to know who make event
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                intent.putExtra(getString(R.string.EXTRA_TRIP_TYPE),mTripType);
                startActivity(intent);
                finish();
            }
        });
        campTripLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mTripType = TRIP_TYPE_CAMP;
                Intent intent = new Intent(EditorChoose.this, EditorTripActivity.class);
                //PutExtra user data to know who make event
                mExtraInfoHelp.putWindsurferToIntent(intent,mWindsurfer,getApplicationContext());
                intent.putExtra(getString(R.string.EXTRA_TRIP_TYPE),mTripType);
                startActivity(intent);
                finish();
            }
        });

        }
    }