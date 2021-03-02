package libertypassage.com.corporate.utilities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import libertypassage.com.corporate.R
import libertypassage.com.corporate.view.HomePage
import java.text.SimpleDateFormat
import java.util.*



class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("AlarmReceiver", "running")
        val date = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("HH")
        val currentHours = df.format(date).toInt()
        if (currentHours < 12) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context.applicationContext, "morning")
            mBuilder.setContentTitle("Help us help you")
            mBuilder.setContentText("Kindly update your temperature and location")
            mBuilder.setSmallIcon(R.drawable.notification_logo)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mBuilder.setSound(alarmSound)
            mBuilder.setAutoCancel(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel("12345", "MORNING", importance)
                mBuilder.setChannelId("12345")
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
            val notifyIntent = Intent(context, HomePage::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                10,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(pendingIntent)
            assert(mNotificationManager != null)
            mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        } else {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context.applicationContext, "evening")
            mBuilder.setContentText("Do you know the total cases in Singapore today?")
            mBuilder.setSmallIcon(R.drawable.notification_logo)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mBuilder.setSound(alarmSound)
            mBuilder.setAutoCancel(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel("54321", "EVENING", importance)
                mBuilder.setChannelId("54321")
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
            val notifyIntent = Intent(context, HomePage::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                15,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(pendingIntent)
            assert(mNotificationManager != null)
            mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        }
    }
}