package livewind.example.andro.liveWind.promotions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import livewind.example.andro.liveWind.R;
import livewind.example.andro.liveWind.data.EventContract;

public class PromotionsAdapter extends ArrayAdapter<Promotion> {
    private int mColorResourceId;
    private final List<Promotion> promotions;
    public PromotionsAdapter(Activity context, List<Promotion> promotions, int resource) {

        super(context, 0, promotions);
        this.promotions=promotions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_promotions_item, parent, false);
        }
        Promotion currentPromotion = getItem(position);

        ImageView promotionImageView = listItemView.findViewById(R.id.promotions_item_image_view);
        TextView promotionTitleTextView = listItemView.findViewById(R.id.promotions_item_title_text_view);
        TextView promotionDescriptionTextView = listItemView.findViewById(R.id.promotions_item_description_text_view);
        if(Locale.getDefault().getLanguage().equals("pl")) {
            promotionTitleTextView.setText(currentPromotion.getTitlePL());
            promotionDescriptionTextView.setText(currentPromotion.getDescriptionPL());
        } else {
            promotionTitleTextView.setText(currentPromotion.getTitleENG());
            promotionDescriptionTextView.setText(currentPromotion.getDescriptionENG());
        }

        switch (currentPromotion.getType()){
            case EventContract.EventEntry.PROMOTION_TYPE_SURFADVISOR:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("app_icon_v3","drawable",getContext().getPackageName()));
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String discountCode = sharedPrefs.getString(getContext().getString(R.string.settings_discount_code_key),"1");
                promotionTitleTextView.setText(promotionTitleTextView.getText()+discountCode);
                break;
            case EventContract.EventEntry.PROMOTION_TYPE_SURFOTEKA:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("logo_surfoteka","drawable",getContext().getPackageName()));
            break;
                case EventContract.EventEntry.PROMOTION_TYPE_TWO_WAVES:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("icon_promotion_1","drawable",getContext().getPackageName()));
                    break;
            case EventContract.EventEntry.PROMOTION_TYPE_LOCALISATION:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("icon_promotion_2","drawable",getContext().getPackageName()));
                break;
            case EventContract.EventEntry.PROMOTION_TYPE_CAR_FRONT:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("icon_promotion_3","drawable",getContext().getPackageName()));
                break;
            case EventContract.EventEntry.PROMOTION_TYPE_CAR_SIDE:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("icon_promotion_4","drawable",getContext().getPackageName()));

                break;
            case EventContract.EventEntry.PROMOTION_TYPE_HYDROSFERA:
                promotionImageView.setImageResource(getContext().getResources().getIdentifier("logo_hydrosfera","drawable",getContext().getPackageName()));
                break;
        default:
            promotionImageView.setImageResource(getContext().getResources().getIdentifier("app_icon_v3","drawable",getContext().getPackageName()));
            break;
        }
        return listItemView;
    }

    public void sort() {
        Collections.sort(promotions, new PromotionsComparator());
    }
}
