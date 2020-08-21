package libertypassage.com.app.utilis;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;

import libertypassage.com.app.basic.TestingActivity;
import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelStatusMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String orderId = "";


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e(TAG, "new token = " + token);
        updateFirebaseToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            Intent intent = new Intent(this, TestingActivity.class).putExtra("order_id", orderId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (remoteMessage.getData().isEmpty()) {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), intent);
            } else {
                showNotification(remoteMessage.getData(), intent);
            }

        } catch (Exception e) {
            Log.e(TAG, "error = " + e.getMessage());
        }
    }

    private void showNotification(Map<String, String> data, Intent intent) {
        String title = data.get("title");
        String body = data.get("body");
        orderId = data.get("orderId");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "com.oss.asfi";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("ASFI Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void showNotification(String title, String body, Intent intent) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "com.oss.asfi";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("ASFI Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void updateFirebaseToken(String token) {
        String userId = Utility.getSharedPreferences(this, Constants.KEY_USERID);
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", false, false);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelStatusMsg> call = apiInterface.updateFirebaseToken(Constants.KEY_BOT, userId, Constants.APIVALUE, token);

        call.enqueue(new Callback<ModelStatusMsg>() {
            @Override
            public void onResponse(Call<ModelStatusMsg> call, Response<ModelStatusMsg> response) {
                progressDialog.dismiss();
                ModelStatusMsg model = response.body();
                Log.e("model", new Gson().toJson(model));
                if (model.getStatus().equals("success")) {
                    String message = model.getMsg();

                } else if (model.getStatus().equals("error")) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ModelStatusMsg> call, Throwable t) {
                progressDialog.dismiss();
//                Log.e("model", "onFailure    " + t.getMessage());
            }
        });
    }
}
