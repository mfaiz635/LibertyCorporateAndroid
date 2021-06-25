package libertypassage.com.corporate.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import libertypassage.com.corporate.R
import libertypassage.com.corporate.view.HotSpotsActivity
import java.util.*



class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName
    var user_id: String? = ""
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "new token = $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e(TAG, "onMessageReceived = $remoteMessage")
        try {
            val intent = Intent(this, HotSpotsActivity::class.java)
            intent.putExtra("user_id", user_id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            if (remoteMessage.data.isEmpty()) {
                showNotification(
                    remoteMessage.notification!!.title, remoteMessage.notification!!.body, intent
                )
            } else {
                showNotification(remoteMessage.data, intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "error = " + e.message)
        }
    }

    private fun showNotification(data: Map<String, String>, intent: Intent) {
        val title = data["title"]
        val body = data["body"]
        user_id = data["user_id"]
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent =
            PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "com.com.app.channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Liberty Channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setContentInfo("Info")
            .setContentIntent(pendingIntent)
            .setSound(defaultSoundUri)
        notificationManager.notify(Random().nextInt(), builder.build())
    }

    private fun showNotification(title: String?, body: String?, intent: Intent) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent =
            PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "com.com.app.channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Liberty Channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setContentInfo("Info")
            .setContentIntent(pendingIntent)
            .setSound(defaultSoundUri)
        notificationManager.notify(Random().nextInt(), builder.build())
    }
}