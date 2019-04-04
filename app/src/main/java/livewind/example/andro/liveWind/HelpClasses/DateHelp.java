package livewind.example.andro.liveWind.HelpClasses;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import livewind.example.andro.liveWind.Filter.FilterTripsActivity;

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

    public static void showDatePickerDialog(final Context context, final TextView dateTextView, final Calendar calendar) {
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;// datapicker dialog gives month from 0
                        String monthString = "";
                        String dayString = "";
                        if (month < 10) {
                            monthString = "0" + Integer.toString(month);
                        } else {
                            monthString = Integer.toString(month);
                        }
                        if (day < 10) {
                            dayString = "0" + Integer.toString(day);
                        } else {
                            dayString = Integer.toString(day);
                        }
                        dateTextView.setText(dayString + "." + monthString + "." + Integer.toString(year));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });
    }
}
