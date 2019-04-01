package com.stauss.simon.stundenplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar c = Calendar.getInstance();

            // Setup Notifications for each day
            for (int i = 1; i <= 5; i++) {
                int h = getMain().getSharedPreferences().getInt("scheduleNotificationHour", 7);
                int m = getMain().getSharedPreferences().getInt("scheduleNotificationMinute", 0);

                c.set(Calendar.DAY_OF_WEEK, i + 1);
                c.set(Calendar.HOUR_OF_DAY, h);
                c.set(Calendar.MINUTE, m);
                c.set(Calendar.SECOND, 0);

                Date date = c.getTime();

                // This Intent will be opened when the alarm fires
                // -> onReceive() in NotificationReceiver will be called
                Intent intent1 = new Intent(context, NotificationReceiver.class);

                // Put dayNr as extra
                // 1 = Monday ... 5 = Friday
                intent.putExtra("day", i);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent1, 0);

                // Set weekly (7* daily interval) repeating alarm for the specified date executing the pendingIntent
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }
        }
    }

    private static Main getMain() {
            return new Main();
    }
}
