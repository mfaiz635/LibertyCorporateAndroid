package libertypassage.com.app.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import libertypassage.com.app.R;
import libertypassage.com.app.basic.HomePage;
import libertypassage.com.app.basic.LocationHistory;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;

import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "running");

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("HH");
        int currentHours = Integer.parseInt(df.format(date));

        if (currentHours<12){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "morning");
            mBuilder.setContentTitle("Help us help you");
            mBuilder.setContentText("Kindly update your temperature and location");
            mBuilder.setSmallIcon(R.drawable.notification_logo);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("12345", "MORNING", importance);
                mBuilder.setChannelId("12345");
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            Intent notifyIntent = new Intent(context, HomePage.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            assert mNotificationManager != null;
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        }else{
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "evening");
            mBuilder.setContentText("Do you know the total cases in Singapore today?");
            mBuilder.setSmallIcon(R.drawable.notification_logo);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("54321", "EVENING", importance);
                mBuilder.setChannelId("54321");
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            Intent notifyIntent = new Intent(context, LocationHistory.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 15, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            assert mNotificationManager != null;
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        }
    }
}
