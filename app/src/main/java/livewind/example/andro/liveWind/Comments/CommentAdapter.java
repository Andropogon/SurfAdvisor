package livewind.example.andro.liveWind.Comments;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import livewind.example.andro.liveWind.R;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private int mColorResourceId;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mCurrentTimeReference = mFirebaseDatabase.getReference().child("currentTime");
    private final List<Comment> objects;

    public CommentAdapter(Activity context, List<Comment> comments, int resource) {

        super(context, 0, comments);
        objects = comments;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comment, parent, false);
        }
        Comment currentComment = getItem(position);

        ImageView userPhotoImageView = listItemView.findViewById(R.id.comment_list_item_image_view);
        TextView userUsernameTextView = listItemView.findViewById(R.id.comment_list_item_username_text_view);
        TextView commentTextTextView = listItemView.findViewById(R.id.comment_list_item_text_text_view);
        TextView commentDateTextView = listItemView.findViewById(R.id.comment_list_item_date_text_view);
        if(currentComment.getUserPhotoName().isEmpty()){
            userPhotoImageView.setImageResource(getContext().getResources().getIdentifier("ic_star_24dp","drawable",getContext().getPackageName()));
        } else {
            userPhotoImageView.setImageResource(getContext().getResources().getIdentifier(currentComment.getUserPhotoName(), "drawable", getContext().getPackageName()));
        }
        userUsernameTextView.setText(currentComment.getUserUsername());
        commentTextTextView.setText(currentComment.getCommentText());
        if(currentComment.getCommentNumber().equals("0") && currentComment.getTimestamp()==0){
            commentDateTextView.setText(getContext().getResources().getString(R.string.event_activity_comment_creator));
            commentDateTextView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_star_24dp, 0, 0, 0);
        } else if(currentComment.getTimestamp()==0) {
            commentDateTextView.setText(getContext().getText(R.string.event_activity_time_now));
            commentDateTextView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.baseline_access_time_black_18, 0, 0, 0);
        }else {
            setEventDurationOnDateTextView(currentComment.getTimestamp(), commentDateTextView);
        }
        return listItemView;
    }

    private void setEventDurationOnDateTextView(final Long commentTimestamp, final TextView view) {
        mCurrentTimeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long eventDurationInMinutes = 0;
                    long currentTime = dataSnapshot.getValue(long.class);
                    long eventCreationTime = commentTimestamp;
                    currentTime = currentTime - eventCreationTime;
                    eventDurationInMinutes = currentTime / 1000;
                    eventDurationInMinutes = eventDurationInMinutes / 60;
                    String eventDurationString = "";
                    if (eventDurationInMinutes < 2) {
                        eventDurationString = getContext().getString(R.string.event_activity_time_now);
                    } else if (eventDurationInMinutes < 60) {
                        eventDurationString = getContext().getString(R.string.event_activity_time_added) + Long.toString(eventDurationInMinutes) + getContext().getString(R.string.event_activity_time_min_ago);
                    } else if (eventDurationInMinutes > 60) {
                        int hours = 0;
                        while (eventDurationInMinutes > 60) {
                            hours++;
                            eventDurationInMinutes = eventDurationInMinutes - 60;
                        }
                        eventDurationString = getContext().getString(R.string.event_activity_time_added) + Integer.toString(hours) + getContext().getString(R.string.event_activity_time_h_and) + Long.toString(eventDurationInMinutes) + getContext().getString(R.string.event_activity_time_min_ago);
                    } else {
                        eventDurationString = getContext().getString(R.string.event_activity_time_unknown);
                    }
                    view.setText(eventDurationString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sort() {
        Collections.sort(objects,new CommentComparator());
    }
}
