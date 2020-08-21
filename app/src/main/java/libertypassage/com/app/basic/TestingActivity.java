package libertypassage.com.app.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import libertypassage.com.app.R;
import libertypassage.com.app.services.AlarmReceiver;
import libertypassage.com.app.widgets.CenturyGothicTextview;


public class TestingActivity extends AppCompatActivity {
    private Context context;
    CenturyGothicTextview tv_timer;
    private TextView mButtonStartPause;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        context = TestingActivity.this;
        tv_timer = findViewById(R.id.tv_timer);
        mButtonStartPause = findViewById(R.id.button_start_pause);


        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReceiver.class).putExtra("type", "morning");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, pendingIntent);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 17);
                calendar.set(Calendar.MINUTE, 40);
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            }
        });


    }

}