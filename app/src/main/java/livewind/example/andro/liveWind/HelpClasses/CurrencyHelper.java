package livewind.example.andro.liveWind.HelpClasses;

import livewind.example.andro.liveWind.data.EventContract;

public class CurrencyHelper {
    public static int currencyToPLN(int cost, int currency){
        switch (currency) {
            case EventContract.EventEntry.CURRENCY_ZL:
                return cost;
            case EventContract.EventEntry.CURRENCY_EURO:
                return (int) (cost * EventContract.EventEntry.CURRENCY_MULTIPLER_EURO);
            case EventContract.EventEntry.CURRENCY_USD:
                return (int) (cost * EventContract.EventEntry.CURRENCY_MULTIPLER_USD);
            default:
                return cost;
        }
    }
}
