package livewind.example.andro.liveWind.user;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import livewind.example.andro.liveWind.R;

public class IconsAdapter extends ArrayAdapter<ProfileIcon> {
    private int mColorResourceId;

    public IconsAdapter(Activity context, ArrayList<ProfileIcon> profileIcons,int resource) {

        super(context, 0, profileIcons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_user_dialog_select_photo_icon_item, parent, false);
        }
        ProfileIcon currentProfileIcon = getItem(position);


        ImageView iconImageView = (ImageView) listItemView.findViewById(R.id.select_icon_image_view);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.select_name_text_view);
        ImageView lockImageView = (ImageView) listItemView.findViewById(R.id.select_lock_image_view);

        iconImageView.setImageResource(currentProfileIcon.getIconId());
        nameTextView.setText(currentProfileIcon.getIconName());
        if(currentProfileIcon.isIconUnlock()) {
            lockImageView.setImageResource(R.drawable.ic_lock_open_black_24dp);
            mColorResourceId=R.color.light_green;
        } else{
            lockImageView.setImageResource(R.drawable.ic_lock_outline_black_24dp);
            mColorResourceId=R.color.light_red;
        }
        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        lockImageView.setColorFilter(color);
        return listItemView;
    }
}