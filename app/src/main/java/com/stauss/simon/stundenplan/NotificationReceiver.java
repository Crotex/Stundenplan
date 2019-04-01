package com.stauss.simon.stundenplan;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    // This method is called once the alarm fires
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a new Intent with Main.class
        // This Intent will later be opened once the notification is clicked
        Intent i = new Intent(context, Main.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        int dayNr = intent.getIntExtra("day", 1);
        String subjectString;
        List<String> subjectList = new ArrayList<>();
        String[] week = getMain().getWeek();
        String day = week[dayNr];
        StringBuilder stringBuilder = new StringBuilder();

        for(int h = 1; h <= 11; h++) {
            String subject = getMain().getSharedPreferences().getString(day + h + "s", "-");
            if(!subject.equals("-") && !subjectList.contains(subject)) {
                stringBuilder.append("\n- ");
                stringBuilder.append(subject);
                subjectList.add(subject);
            }
        }
        subjectString = stringBuilder.toString();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                // TODO: get Icon
                .setSmallIcon(R.drawable.ic_menu_manage)
                // Set the header
                .setContentTitle(context.getString(R.string.schedule))
                // Set the content text (text that will be displayed in the notification)
                .setContentText(context.getString(R.string.notification_desc))
                // Set time stamp to the current time
                .setWhen(System.currentTimeMillis())
                // Set big text (normal text is cut after a specific length)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_desc) + subjectString))
                // Set the intent opening after click
                .setContentIntent(pendingIntent)
                // Notification will auto-remove itself after a click
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Send notification
        notificationManager.notify(1, builder.build());
    }

    private static Main getMain() {
        return new Main();
    }
}
