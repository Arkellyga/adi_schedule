package net.arkellyga.adischedule;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DataBaseHandler db = new DataBaseHandler(getApplicationContext());
        Lesson lesson = db.getLesson(DayHelper.getCurrentDayTable(), DayHelper.getCurrentWeek(),
                intent.getIntExtra(Values.INTENT_EXTRA_ORDER, 0));
        // if current lesson isn't window
        if (lesson.getWindow() == 1) {
            String contentText = "В " + lesson.getRoom() +
                    " аудитории  \"" + lesson.getName() +
                    "\"";
            Resources resources = getResources();
            Intent i = MainActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Log.d(TAG, "onHandleIntent() works");
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(resources.getString(R.string.lesson))
                    .setContentText(contentText)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 500, 500})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
        }
        // Set new alarm for next day.
        String time = lesson.getTimeFrom();
        int lessonOrder = lesson.getOrder();
        updateServiceAlarm(getApplicationContext(), lessonOrder, time);
    }

    public static void updateServiceAlarm(Context context, int pendingId, String time) {
        Intent i = newIntent(context);
        // Fill extra order of intent;
        i.putExtra(Values.INTENT_EXTRA_ORDER, pendingId);
        PendingIntent pi = PendingIntent.getService(context, pendingId, i, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(ALARM_SERVICE);

        alarmManager.cancel(pi);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("hh:mm", Locale.US)).parse(time));
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 15);
            calendar1.set(Calendar.SECOND, 0);
            Log.d(TAG, "time: " + calendar1.get(Calendar.HOUR_OF_DAY) + ":" + calendar1.get(Calendar.MINUTE)
                    + " " + calendar1.getTimeInMillis() + " < " + System.currentTimeMillis());
            if (calendar1.getTimeInMillis() < System.currentTimeMillis()) {
                // If next day isn't friday,then alarm will set on next day,else on monday
                int numberDays = 1;
                switch (calendar1.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.FRIDAY:
                        numberDays = 3; break;
                    case Calendar.SATURDAY:
                        numberDays = 2; break;
                }
                Log.d(TAG, "alarm will be set on " + numberDays + " days");
                calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) + numberDays);
            }
            alarmManager.set(AlarmManager.RTC, calendar1.getTimeInMillis(), pi);

        } catch (ParseException e) { e.printStackTrace(); }
    }

    public static boolean checkServiceAlarms(Context context) {
        Intent i = newIntent(context);
        boolean lesson1 = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null;
        boolean lesson2 = PendingIntent.getService(context, 1, i, PendingIntent.FLAG_NO_CREATE) != null;
        boolean lesson3 = PendingIntent.getService(context, 2, i, PendingIntent.FLAG_NO_CREATE) != null;
        boolean lesson4 = PendingIntent.getService(context, 3, i, PendingIntent.FLAG_NO_CREATE) != null;
        Log.d(TAG, "Alarms from 1 to 4: " + lesson1 + " " + lesson2 + " " + lesson3 + " " + lesson4);
        return lesson1 && lesson2 && lesson3 && lesson4;
    }

    public static void deleteServiceAlarms(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pi;
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < 4; i++) {
            pi = PendingIntent.getService(context, i, intent, 0);
            alarmManager.cancel(pi);
            pi.cancel();
        }
        Log.d(TAG, "deleting services alarm was done.");
    }

    public static void updateAllServiceAlarms(Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        List<TimeLesson> list = db.getTimes();
        for (TimeLesson time : list)
            updateServiceAlarm(context, time.getOrder(), time.getTimeFrom());
    }
}
