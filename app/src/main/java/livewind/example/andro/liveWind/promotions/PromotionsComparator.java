package livewind.example.andro.liveWind.promotions;

import java.util.Comparator;

class PromotionsComparator implements Comparator<Promotion> {
    public int compare(Promotion promotion1, Promotion promotion2) {
        return promotion1.getType()-promotion2.getType();
    }
}