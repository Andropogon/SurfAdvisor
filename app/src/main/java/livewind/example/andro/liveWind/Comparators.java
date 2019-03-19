package livewind.example.andro.liveWind;

import java.util.Comparator;

import livewind.example.andro.liveWind.data.EventContract;
import livewind.example.andro.liveWind.promotions.Promotion;

class EventDateComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2) {
        return -(event1.getTimestampINT()-event2.getTimestampINT());
    }
}

class EventMembersComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2) {
        return event1.getmMembers().size() - event1.getmMembers().size();
    }
}

class EventWindPowerComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2) {
        return -(event1.getWindPower() - event2.getWindPower());
    }
}
    class EventThanksSizeComparator implements Comparator<Event> {
        public int compare(Event event1, Event event2) {
            return -(event1.getmThanksSize() - event2.getmThanksSize());
        }
    }
class TripsDateComparator implements Comparator<Event> {
    public int compare(Event trip1, Event trip2) {
        return trip1.startDateToGC().compareTo(trip2.startDateToGC());
    }
}

class TripsCostComparator implements Comparator<Event> {

    public int compare(Event trip1, Event trip2) {
        int costWithCurrency1 = 0;
        int costWithCurrency2 = 0;
        switch (trip1.getCurrency()) {
            case EventContract.EventEntry.CURRENCY_ZL:
                if (trip1.getCostDiscount() > 0) {
                    costWithCurrency1 = trip1.getCost() - trip1.getCostDiscount();
                } else {
                    costWithCurrency1 = trip1.getCost();
                }
                break;
            case EventContract.EventEntry.CURRENCY_EURO:
                if (trip1.getCostDiscount() > 0) {
                    costWithCurrency1 = (int) ((trip1.getCost() - trip1.getCostDiscount()) * EventContract.EventEntry.CURRENCY_MULTIPLER_EURO);
                } else {
                    costWithCurrency1 = (int) (trip1.getCost() * EventContract.EventEntry.CURRENCY_MULTIPLER_EURO);
                }
                break;
            case EventContract.EventEntry.CURRENCY_USD:
                if (trip1.getCostDiscount() > 0) {
                    costWithCurrency1 = (int) ((trip1.getCost() - trip1.getCostDiscount()) * EventContract.EventEntry.CURRENCY_MULTIPLER_USD);
                } else {
                    costWithCurrency1 = (int) (trip1.getCost() * EventContract.EventEntry.CURRENCY_MULTIPLER_USD);
                }
                break;
            default:
                if (trip1.getCostDiscount() > 0) {
                    costWithCurrency1 = trip1.getCost() - trip1.getCostDiscount();
                } else {
                    costWithCurrency1 = trip1.getCost();
                }
                break;
        }
        switch (trip2.getCurrency()) {
            case EventContract.EventEntry.CURRENCY_ZL:
                if (trip2.getCostDiscount() > 0) {
                    costWithCurrency2 = trip2.getCost() - trip2.getCostDiscount();
                } else {
                    costWithCurrency2 = trip2.getCost();
                }
                break;
            case EventContract.EventEntry.CURRENCY_EURO:
                if (trip2.getCostDiscount() > 0) {
                    costWithCurrency2 = (int) ((trip2.getCost() - trip2.getCostDiscount()) * EventContract.EventEntry.CURRENCY_MULTIPLER_EURO);
                } else {
                    costWithCurrency2 = (int) (trip2.getCost() * EventContract.EventEntry.CURRENCY_MULTIPLER_EURO);
                }
                break;
            case EventContract.EventEntry.CURRENCY_USD:
                if (trip2.getCostDiscount() > 0) {
                    costWithCurrency2 = (int) ((trip2.getCost() - trip2.getCostDiscount()) * EventContract.EventEntry.CURRENCY_MULTIPLER_USD);
                } else {
                    costWithCurrency2 = (int) (trip2.getCost() * EventContract.EventEntry.CURRENCY_MULTIPLER_USD);
                }
                break;
            default:
                if (trip2.getCostDiscount() > 0) {
                    costWithCurrency2 = trip2.getCost() - trip2.getCostDiscount();
                } else {
                    costWithCurrency2 = trip2.getCost();
                }
                break;
        }
        return costWithCurrency1 - costWithCurrency2;
    }
}
/**
class TripsCharakterComparator implements Comparator<Event> {
    public int compare(Event trip1, Event trip2) {

        return trip1.getCharacter() - trip2.getCharacter();
    }

} */

