package libertypassage.com.app.basic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import libertypassage.com.app.R;
import libertypassage.com.app.services.AlarmReceiver;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicTextview;

public class ConfirmDetails extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = ConfirmDetails.class.getSimpleName();
    private CenturyGothicTextview tv_next, tv_address, tv_temprature, tv_tempratureType;
    private ImageView iv_back, iv_temp_edit, iv_location_edit;
    private RelativeLayout rl_restart;
    private String token;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_details);

        context = ConfirmDetails.this;
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        tv_temprature = findViewById(R.id.temprature);
        tv_tempratureType = findViewById(R.id.tempratureType);
        tv_address = findViewById(R.id.address1);
        iv_temp_edit = findViewById(R.id.iv_temp_edit);
        iv_location_edit = findViewById(R.id.iv_location_edit);
        tv_next = findViewById(R.id.tv_next);
        rl_restart = findViewById(R.id.rl_restart);

        iv_back.setOnClickListener(this);
        iv_temp_edit.setOnClickListener(this);
        iv_location_edit.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        rl_restart.setOnClickListener(this);
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
    }

    private void init() {
        tv_temprature.setText(Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP));
        tv_tempratureType.setText(Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE));
        tv_address.setText(Utility.getSharedPreferences(context, Constants.KEY_CURRENT_LOC));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }

            case R.id.tv_next: {
                localNotification();

                break;
            }

            case R.id.iv_temp_edit: {
                Intent intent = new Intent(context, UpdateMyTemperature.class);
                intent.putExtra("from", "confir_details");
                startActivityForResult(intent, 110);
                break;
            }

            case R.id.iv_location_edit: {
                Intent intent = new Intent(context, UpdateAddress.class);
                intent.putExtra("from", "confir_details");
                startActivityForResult(intent, 120);
                break;
            }

            case R.id.rl_restart: {
                Intent intent = new Intent(context, EnrolmentDeclaration.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        tv_temprature.setText(Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP));
        tv_tempratureType.setText(Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE));
        tv_address.setText(Utility.getSharedPreferences(context, Constants.KEY_CURRENT_LOC));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110) {
            if (resultCode == 111) {
                String currentTemp = data.getStringExtra("currentTemp");
                String tempType = data.getStringExtra("tempType");
                if (currentTemp != null) {
                    tv_temprature.setText(currentTemp);
                    tv_tempratureType.setText(tempType);
                }
            }

        } else if (requestCode == 120) {
            if (resultCode == 121) {
                String contents = data.getStringExtra("address");
                if (contents != null) {
                    tv_address.setText(contents);
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            //  handle cancel
        }

    }

    public void localNotification() {
        Log.e("localNotification", "running");
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);


        Intent intent = new Intent(context, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

