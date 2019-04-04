package livewind.example.andro.liveWind.HelpClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelp {
    /**
     * Date to timestamp
     * @param date Date in format dd.MM.yyyy
     * @return Date timestamp
     */
    public static long dateToTimestamp(String date){
        String day = date.substring(0, 2);
        String month = date.substring(3, 5);
        String year = date.substring(6, 10);
        int dayS = Integer.parseInt(day);
        int monthS = Integer.parseInt(month) -1 ; //because months are indexing from 0
        int yearS = Integer.parseInt(year);
        GregorianCalendar dataGC = new GregorianCalendar(yearS, monthS, dayS,0,0,0);
        Calendar dataC = dataGC;
        long timestamp = dataC.getTimeInMillis();
        return timestamp;
    }

    /**
     * Timestamp to date
     * @param timestamp in milliseconds
     * @return date in format dd.MM.yyyy
     */
    public static String timestampToDate(long timestamp){
        Date date = new Date();
        date.setTime(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        java.lang.String printableDate = formatter.format(date);
        String dateString =printableDate;
        return dateString;
    }
}
