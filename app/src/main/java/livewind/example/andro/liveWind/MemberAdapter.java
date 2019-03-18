package livewind.example.andro.liveWind;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class MemberAdapter extends ArrayAdapter<MyMember> {
    private int mColorResourceId;
    public MemberAdapter(Context context, List <MyMember> objects, int resource) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        mColorResourceId = livewind.example.andro.liveWind.R.color.creatorTextColor;
        if (view == null) {
            view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.members_list_item, parent, false);
        }
        view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.members_list_item, parent, false);
        TextView nameTextView = (TextView) view.findViewById(livewind.example.andro.liveWind.R.id.member_name_text_view);
        ImageView iconImageView = (ImageView) view.findViewById(livewind.example.andro.liveWind.R.id.member_icon_image_view);

        MyMember member = getItem(position);

        nameTextView.setText(member.getmName());
        if(member.getmType()==1){
            //view.setVisibility(View.GONE);
            view = ((Activity) getContext()).getLayoutInflater().inflate(livewind.example.andro.liveWind.R.layout.list_item_empty, parent, false);
           // nameTextView.setTextColor(mColorResourceId);
        }else {}
        iconImageView.setImageResource(getContext().getResources().getIdentifier(member.getmPhotoName(), "drawable", getContext().getPackageName()));

        return view;
    }
}