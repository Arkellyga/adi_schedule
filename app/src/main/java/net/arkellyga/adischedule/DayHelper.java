package net.arkellyga.adischedule;


import java.util.Calendar;

public class DayHelper {

    static public String getCurrentWeek() {
        if ((Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2 == 0) &&
                ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) &&
                        (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)))
            return "up";
        else
            return "down";
    }

    // Get actually day of week. ( - 2 because week start from 1 and it sunday.)
    static public int getCurrentDay() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            // Return 0 if we have sunday and saturday.
            default:
                return 0;
        }
    }

    static public String getTableDayByNumber(int day) {
        switch (day) {
            case 0:
                return Values.TABLE_MONDAY;
            case 1:
                return Values.TABLE_TUESDAY;
            case 2:
                return Values.TABLE_WEDNESDAY;
            case 3:
                return Values.TABLE_THURSDAY;
            case 4:
                return Values.TABLE_FRIDAY;
            // Return 0 if we have sunday and saturday.
            default:
                return null;
        }
    }

    static public String getCurrentDayTable() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return Values.TABLE_MONDAY;
            case Calendar.TUESDAY:
                return Values.TABLE_TUESDAY;
            case Calendar.WEDNESDAY:
                return Values.TABLE_WEDNESDAY;
            case Calendar.THURSDAY:
                return Values.TABLE_THURSDAY;
            case Calendar.FRIDAY:
                return Values.TABLE_FRIDAY;
            // Return if we have sunday and saturday.
            default:
                return Values.TABLE_MONDAY;
        }
    }

    static public String getNameCurrentDay() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Понедельник";
            case Calendar.TUESDAY:
                return "Вторник";
            case Calendar.WEDNESDAY:
                return "Среда";
            case Calendar.THURSDAY:
                return "Четверг";
            case Calendar.FRIDAY:
                return "Пятница";
            default:
                return "Понедельник";
        }

    }
}
