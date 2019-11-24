package livewind.example.andro.liveWind.user;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import livewind.example.andro.liveWind.ExtraInfoHelp;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.NoConnectionPendingException;
import java.util.ArrayList;
import java.util.List;

import livewind.example.andro.liveWind.ListView_help.ListViewHelp;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.firebase.FirebaseHelp;
import livewind.example.andro.liveWind.promotions.Promotion;
import livewind.example.andro.liveWind.promotions.PromotionsAdapter;

import static livewind.example.andro.liveWind.ExtraInfoHelp.getWindsurferFromIntent;

public class UserActivity extends AppCompatActivity {
    private Windsurfer mWindsurfer = new Windsurfer();
    private ExtraInfoHelp mExtraInfoHelp = new ExtraInfoHelp();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("test/users");
    private ImageView photoImageView;
    private FirebaseHelp mFirebaseHelp = new FirebaseHelp();

    private TextView pointsTextView;
    private TextView activeEventsLimitTextView;
    private TextView activeTripsLimitTextView;
    //private static final int INCREASE_LIMIT_PRICE=300;

    //PROMOTIONS
    private ListView promotionsListView;
    private PromotionsAdapter promotionsAdapter;
    final List<Promotion> myPromotions = new ArrayList<>();
    private DatabaseReference promotionsDatabaseReference = mFirebaseDatabase.getReference().child("test/promotions");
    ChildEventListener childPromotionsListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_user);
        Intent intent = new Intent();
        try {
            intent = getIntent();
        } catch (NoConnectionPendingException e){
            Toast.makeText(UserActivity.this, getString(R.string.toast_no_connection),Toast.LENGTH_SHORT).show();
        }
        getWindsurferFromIntent(intent,mWindsurfer,getApplicationContext());


        TextView nickTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_nickname_text_view);
        pointsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_points_text_view);
        TextView createdEventsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_created_events_amount_text_view);
        TextView createdTripsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_created_trips_amount_text_view);
        TextView activeEventsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_active_events_amount_text_view);
        TextView activeTripsTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_active_trips_amount_text_view);
        activeEventsLimitTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_active_events_limit_amount_text_view);
        activeTripsLimitTextView = (TextView) findViewById(livewind.example.andro.liveWind.R.id.user_active_trips_limit_text_view);
        ImageView helpPointsImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.user_points_help_image_view);
        ImageView changePhotoImageView = (ImageView) findViewById(livewind.example.andro.liveWind.R.id.change_user_photo_image_view);

        //BUY HELP ICONS
        ImageView helpBuyIcon1ImageView = findViewById(R.id.user_activity_to_buy_1_help_image_view);
        ImageView helpBuyIcon2ImageView = findViewById(R.id.user_activity_to_buy_2_help_image_view);
        ImageView helpBuyIcon3ImageView = findViewById(R.id.user_activity_to_buy_3_help_image_view);
        ImageView helpBuyIcon4ImageView = findViewById(R.id.user_activity_to_buy_4_help_image_view);
        ImageView helpBuyIcon5ImageView = findViewById(R.id.user_activity_to_buy_5_help_image_view);
        ImageView helpBuyIcon6ImageView = findViewById(R.id.user_activity_to_buy_6_help_image_view);
        TextView contactWithDeveloperTextView = findViewById(R.id.user_created_trips_info_for_owners_contact_text_view);

        contactWithDeveloperTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                contactWithDevelopers(UserActivity.this);
            }
        });

        //PROMOTIONS
        promotionsListView = findViewById(R.id.user_promotions_list);
        View promotionsEmptyView = findViewById(R.id.user_promotions_list_empty_view);
        promotionsListView.setEmptyView(promotionsEmptyView);
        promotionsAdapter = new PromotionsAdapter(this, myPromotions, 0);
        promotionsListView.setAdapter(promotionsAdapter);
        try {
            attachDatabaseReadListener();
        } catch (NoConnectionPendingException e){
            Toast.makeText(UserActivity.this, getString(R.string.toast_no_connection),Toast.LENGTH_SHORT).show();
        }
        helpBuyIcon1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        helpBuyIcon2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        helpBuyIcon3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        helpBuyIcon4ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        helpBuyIcon5ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        helpBuyIcon6ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpBuyIconDialog();
            }
        });
        /**
         * Increase trip / relations limit
         */
        TextView increaseRelations = (TextView) findViewById(R.id.user_active_events_limit_increase_text_view);
        TextView increaseTrips = (TextView) findViewById(R.id.user_active_trips_limit_increase_text_view);

        increaseRelations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showApproveLimitIncreaseDialog(EventContract.EventEntry.IT_IS_EVENT);
            }
        });
        increaseTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showApproveLimitIncreaseDialog(EventContract.EventEntry.IT_IS_TRIP);
            }
        });
        photoImageView = findViewById(livewind.example.andro.liveWind.R.id.user_photo_image_view);
        TextView extra1Button = findViewById(R.id.user_activity_to_buy_1_click_text_view);
        TextView extra2Button = findViewById(R.id.user_activity_to_buy_2_click_text_view);
        TextView extra3Button = findViewById(R.id.user_activity_to_buy_3_click_text_view);
        TextView extra4Button = findViewById(R.id.user_activity_to_buy_4_click_text_view);
        TextView extra5Button = findViewById(R.id.user_activity_to_buy_5_click_text_view);
        TextView extra6Button = findViewById(R.id.user_activity_to_buy_6_click_text_view);

        try {
            photoImageView.setImageResource(getResources().getIdentifier(mWindsurfer.getPhotoLargeName(), "drawable", getPackageName()));
        } catch (NullPointerException e){
            Toast.makeText(UserActivity.this, getString(R.string.toast_no_connection),Toast.LENGTH_SHORT).show();
        }
        nickTextView.setText(mWindsurfer.getUsername());
        pointsTextView.setText(String.valueOf(mWindsurfer.getPoints()));
        createdEventsTextView.setText(String.valueOf(mWindsurfer.getCreatedEvents()));
        createdTripsTextView.setText(String.valueOf(mWindsurfer.getCreatedTrips()));
        activeEventsTextView.setText(String.valueOf(mWindsurfer.getActiveEvents()));
        activeTripsTextView.setText(String.valueOf(mWindsurfer.getActiveTrips()));
        activeEventsLimitTextView.setText(String.valueOf(mWindsurfer.getActiveEventsLimit()));
        activeTripsLimitTextView.setText(String.valueOf(mWindsurfer.getActiveTripsLimit()));

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showSelectIconDialog();
            }
        });

        changePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showSelectIconDialog();
            }
        });

        helpPointsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showHelpPointsDialog();
            }
        });

        // Z tego chciałbym zrobić uniwersalną funkcje
        extra1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_1)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_1);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_1)){
                Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
        extra2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_2)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_2);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_2)){
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
        extra3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_3)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_3);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_3)){
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
        extra4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_4)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_4);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_4)){
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
        extra5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_5)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_5);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_5)){
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
        extra6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(mWindsurfer.getPoints()>= EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE && !mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_6)) {
                    showBuyConfirmationDialog(EventContract.EventEntry.EXTRA_ICON_NUMBER_6);
                } else if (mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_6)){
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_1, Toast.LENGTH_SHORT).show();
                } else if (mWindsurfer.getPoints()< EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE) {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showHelpPointsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(livewind.example.andro.liveWind.R.layout.help_dialog_user_activity_points, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }
    public void showHelpBuyIconDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_user_activity_buy_icon, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }
    /**
    public void showHelpBuySurfotekaDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog_user_activity_buy_surfoteka, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }
     */

    // Show select photo action
    private void showSelectIconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(livewind.example.andro.liveWind.R.layout.activity_user_dialog_select_photo,null);
        // Set grid view to alertDialog
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                //builder.setView(gridView)
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        ListView listView = dialogView.findViewById(livewind.example.andro.liveWind.R.id.dialog_select_photo_list_view);
        final ArrayList<ProfileIcon> mList = new ArrayList<ProfileIcon>();
        mList.add(new ProfileIcon(livewind.example.andro.liveWind.R.drawable.user_icon_shaka_24,getApplicationContext().getString(R.string.icon_number_0),true,livewind.example.andro.liveWind.R.drawable.user_icon_shaka_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_goya_banzai_24,getApplicationContext().getString(R.string.icon_number_1),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_1),R.drawable.user_icon_goya_banzai_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_ns_disco_24,getApplicationContext().getString(R.string.icon_number_2),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_2),R.drawable.user_icon_ns_disco_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_kite_north_mono_24,getApplicationContext().getString(R.string.icon_number_3),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_3),R.drawable.user_icon_kite_north_mono_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_kite_north_vegas_24,getApplicationContext().getString(R.string.icon_number_4),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_4),R.drawable.user_icon_kite_north_vegas_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_surf_classic_green_24,getApplicationContext().getString(R.string.icon_number_5),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_5),R.drawable.user_icon_surf_classic_green_100));
        mList.add(new ProfileIcon(R.drawable.user_icon_surf_modern_red_24,getApplicationContext().getString(R.string.icon_number_6),mWindsurfer.checkExtras(EventContract.EventEntry.EXTRA_ICON_NUMBER_6),R.drawable.user_icon_surf_modern_red_100));
        IconsAdapter adapter = new IconsAdapter(this, mList,0);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(android.R.color.darker_gray);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //jesli position bedzie tez zawieralo sie w extras (checkExtras) to zatwirdz inaczej daj dialog ze trzeba kupic / albo toast
                // Animate the background color of clicked Item
                if (mList.get(position).isIconUnlock()) {
                    view.setSelected(true);
                    mWindsurfer.setPhotoName(getResources().getResourceEntryName(mList.get(position).getIconId()));
                    mWindsurfer.setPhotoLargeName(getResources().getResourceEntryName(mList.get(position).getIconLargeId()));
                    photoImageView.setImageResource(mList.get(position).getIconLargeId());
                    changePhoto();
                } else {
                    //icon isn't unlocked
                    Toast.makeText(UserActivity.this, R.string.user_activity_icon_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }

    //Add points for created an event
    public void changePhoto(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(mWindsurfer.getUid());
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUsersDatabaseReference.child(mWindsurfer.getUid()).child("photoName").setValue(mWindsurfer.getPhotoName());
                    mUsersDatabaseReference.child(mWindsurfer.getUid()).child("photoLargeName").setValue(mWindsurfer.getPhotoLargeName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void buyExtra(int extra, int extraPrice){
        mWindsurfer.addExtras(extra);
        mWindsurfer.substractPoints(extraPrice);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query usersQuery = mUsersDatabaseReference.orderByChild("uid").equalTo(mWindsurfer.getUid());
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUsersDatabaseReference.child(mWindsurfer.getUid()).setValue(mWindsurfer);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void showApproveLimitIncreaseDialog(final Boolean eventOrTrip){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_user_dialog_approve_increase, null))
                // Add action buttons
                .setPositiveButton(livewind.example.andro.liveWind.R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(eventOrTrip==EventContract.EventEntry.IT_IS_EVENT){
                            increaseRelationsLimit();
                        } else {
                            increaseTripsLimit();
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Windsurfer clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
        //((Button)alertDialog.findViewById(android.R.id.button2)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }

    private void showBuyConfirmationDialog(final int extraNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setMessage(R.string.dialog_confirmation_user_buy_icon_text);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (extraNumber){
                    case 1:
                buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_1, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                break;
                    case 2:
                        buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_2, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                        pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                        break;
                    case 3:
                        buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_3, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                        pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                        break;
                    case 4:
                        buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_4, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                        pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                        break;
                    case 5:
                        buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_5, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                        pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                        break;
                    case 6:
                        buyExtra(EventContract.EventEntry.EXTRA_ICON_NUMBER_6, EventContract.EventEntry.EXTRA_ICON_CLASSIC_PRICE);
                        pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
                        break;
                        default:
                            break;
            }
            }
        });
        builder.setNegativeButton(livewind.example.andro.liveWind.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void increaseRelationsLimit(){
        if (mWindsurfer.getPoints()< FirebaseHelp.POINTS_INCREASE_LIMIT_PRICE) {
            Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseHelp.increaseRelationsLimit(mWindsurfer.getUid());
            mWindsurfer.substractPoints(FirebaseHelp.POINTS_INCREASE_LIMIT_PRICE);
            int limit = mWindsurfer.getActiveEventsLimit() + 1;
            mWindsurfer.setActiveEventsLimit(limit);
            activeEventsLimitTextView.setText(Integer.toString(mWindsurfer.getActiveEventsLimit()));
            pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
            Toast.makeText(UserActivity.this, R.string.toast_successful_increasing_limit, Toast.LENGTH_SHORT).show();
        }
    }
    public void increaseTripsLimit() {
        if (mWindsurfer.getPoints()< FirebaseHelp.POINTS_INCREASE_LIMIT_PRICE) {
            Toast.makeText(UserActivity.this, R.string.user_activity_buying_error_2, Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseHelp.increaseTripsLimit(mWindsurfer.getUid());
            mWindsurfer.substractPoints(FirebaseHelp.POINTS_INCREASE_LIMIT_PRICE);
            int limit = mWindsurfer.getActiveTripsLimit() + 1;
            mWindsurfer.setActiveTripsLimit(limit);
            activeTripsLimitTextView.setText(Integer.toString(mWindsurfer.getActiveTripsLimit()));
            pointsTextView.setText(Integer.toString(mWindsurfer.getPoints()));
            Toast.makeText(UserActivity.this, R.string.toast_successful_increasing_limit, Toast.LENGTH_SHORT).show();
        }
    }

    // FOR PROMOTIONS
    private void attachDatabaseReadListener() {
        if (childPromotionsListener == null) {
            childPromotionsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Promotion promotion = dataSnapshot.getValue(Promotion.class);
                    promotionsAdapter.add(promotion);
                    ListViewHelp.setListViewHeightBasedOnChildren(promotionsListView);
                    promotionsAdapter.sort();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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
            promotionsDatabaseReference.addChildEventListener(childPromotionsListener);
        }
    }

    private static void contactWithDevelopers(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.contact_our_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, "[SurfAdvisor] Increase limit of trips");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(livewind.example.andro.liveWind.R.string.toast_contact_error), Toast.LENGTH_LONG).show();
            }
    }
}
