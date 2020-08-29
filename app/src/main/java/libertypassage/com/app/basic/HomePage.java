package libertypassage.com.app.basic;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import libertypassage.com.app.R;
import libertypassage.com.app.models.user.Address;
import libertypassage.com.app.models.user.Confirmed;
import libertypassage.com.app.models.user.Details;
import libertypassage.com.app.models.user.LastTrack;
import libertypassage.com.app.models.user.ModelUser;
import libertypassage.com.app.models.user.Temprature;
import libertypassage.com.app.models.user.UserDetails;
import libertypassage.com.app.models.user.UserStatus;
import libertypassage.com.app.services.ProcessMainClass;
import libertypassage.com.app.services.RestartServiceBroadcastReceiver;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private String TAG = HomePage.class.getSimpleName();
    private TextView tv_changeStatus, tv_status_title, tv_people, tv_status_msg, tv_DetailsMsg,
            tv_yellowMsg, tv_redMsg, tv_whenDidYellow, tv_whenDidRed;
    private RelativeLayout rl_ok, rl_goBack;
    private ImageView iv_temp, iv_qrScaner;
    private ImageSpeedometer imageSpeedometer;
    private String token, title, subTitle, descrption, alertDescrption;
    private int statusId, no_of_people, PERMISSION_ID = 44;
    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private LinearLayout layoutDetails, layoutProfile, layoutInfectedArea, layoutLogout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        context = HomePage.this;
        findID();
        init();
    }


    private void findID() {
        iv_temp = findViewById(R.id.iv_temp);
        iv_qrScaner = findViewById(R.id.iv_qrScaner);
        imageSpeedometer = findViewById(R.id.imageSpeedometer);
        tv_status_title = findViewById(R.id.tv_status_title);
        tv_people = findViewById(R.id.tv_people);
        tv_status_msg = findViewById(R.id.tv_status_msg);
        tv_DetailsMsg = findViewById(R.id.tv_DetailsMsg);
//      green details
        tv_changeStatus = findViewById(R.id.tv_changeStatus);
        rl_ok = findViewById(R.id.rl_ok);
//      yellow details
        tv_yellowMsg = findViewById(R.id.tv_yellowMsg);
        tv_whenDidYellow = findViewById(R.id.tv_whenDidYellow);
//      red details
        tv_redMsg = findViewById(R.id.tv_redMsg);
        tv_whenDidRed = findViewById(R.id.tv_whenDidRed);
        rl_goBack = findViewById(R.id.rl_goBack);

        fabSettings = findViewById(R.id.fabSetting);
        layoutDetails = findViewById(R.id.layoutDetails);
        layoutProfile = findViewById(R.id.layoutProfile);
        layoutInfectedArea = findViewById(R.id.layoutInfectedArea);
        layoutLogout = findViewById(R.id.layoutLogout);


        iv_temp.setOnClickListener(this);
        iv_qrScaner.setOnClickListener(this);
        tv_changeStatus.setOnClickListener(this);
        tv_whenDidYellow.setOnClickListener(this);
        tv_whenDidRed.setOnClickListener(this);
        rl_ok.setOnClickListener(this);
        rl_goBack.setOnClickListener(this);
        fabSettings.setOnClickListener(this);
        layoutDetails.setOnClickListener(this);
        layoutProfile.setOnClickListener(this);
        layoutInfectedArea.setOnClickListener(this);
        layoutLogout.setOnClickListener(this);
        Utility.setSharedPreference(context, Constants.KEY_START, "5");
        closeSubMenusFab();
    }

    private void init() {
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        if (Utility.isConnectingToInternet(context)) {
            getUserDetails();
        } else {
            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
        }
        Log.e("token", token);

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Utility.getMyLocation(context);
            } else {
                dialogAlwaysLocationOn();
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_temp: {
                break;
            }
            case R.id.iv_qrScaner: {
                break;
            }
            case R.id.tv_changeStatus: {
                Intent intent = new Intent(context, ChangeStatus.class);
                intent.putExtra("statusId", statusId);
                startActivity(intent);
                break;
            }
            case R.id.tv_whenDidYellow: {
                Intent intent = new Intent(context, ChangeStatus.class);
                intent.putExtra("statusId", statusId);
                startActivity(intent);
                break;
            }
            case R.id.tv_whenDidRed: {
                Intent intent = new Intent(context, ChangeStatus.class);
                intent.putExtra("statusId", statusId);
                startActivity(intent);
                break;
            }
            case R.id.rl_ok: {
                AppExit();
                break;
            }
            case R.id.rl_goBack: {
                AppExit();
                break;
            }

            case R.id.fabSetting: {
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
                break;
            }

            case R.id.layoutDetails: {
                Intent intent = new Intent(context, UpdateMyTemperature.class);
                intent.putExtra("from", "home");
                startActivity(intent);
                closeSubMenusFab();
                break;
            }

//            case R.id.layoutLocation: {
//                Intent intent = new Intent(context, UpdateAddress.class);
//                intent.putExtra("from", "home");
//                startActivity(intent);
//                closeSubMenusFab();
//                break;
//            }

            case R.id.layoutProfile: {
                Intent intent = new Intent(context, UpdateProfile.class);
                startActivity(intent);
                closeSubMenusFab();
                break;
            }

            case R.id.layoutInfectedArea: {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        Utility.getMyLocation(context);
                    } else {
                        dialogAlwaysLocationOn();
                    }
                } else {
                    requestPermissions();
                }
                Intent intent = new Intent(context, LocationHistory.class);
                intent.putExtra("from", "home");
                startActivity(intent);
                closeSubMenusFab();
                break;
            }

//            case R.id.layoutglobExplore: {
//                Intent intent = new Intent(context, GlobeExploreActivity.class);
//                intent.putExtra("from", "home");
//                startActivity(intent);
//                closeSubMenusFab();
//                break;
//            }

            case R.id.layoutLogout: {
                closeSubMenusFab();
                Utility.clearPreference(context);
                Utility.setSharedPreference(context, Constants.KEY_FOR_TITLE, "Required LogIn for update your status");
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            }
        }
    }


    private void getUserDetails() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelUser> call = apiInterface.getUser(Constants.KEY_HEADER + token, Constants.KEY_BOT);

        call.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                Utility.stopProgressDialog(context);
                ModelUser model = response.body();
                Log.e("getUserDetails", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    Details details = model.getDetails();
                    UserDetails userDetails = details.getUserDetails();
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, userDetails.getPhoneNumber());

                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, userDetails.getEmailId());
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, userDetails.getGender());
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, userDetails.getAgeGroup());
                    Utility.setSharedPreference(context, Constants.KEY_PROF, String.valueOf(userDetails.getProfessionId()));

//                    Utility.setSharedPreference(context, Constants.KEY_FIRSTNAME, userDetails.getFirstName());
//                    Utility.setSharedPreference(context, Constants.KEY_LASTNAME, userDetails.getLastName());
//                    if(userDetails.getProfilePicture()!=null) {
//                        Utility.setSharedPreference(context, Constants.KEY_POFILE_PIC, userDetails.getProfilePicture());
//                    }

                    imageSpeedometer.setVisibility(View.VISIBLE);
                    imageSpeedometer.speedTo(userDetails.getRiskScore());

                    UserStatus userStatus = userDetails.getUserStatus();
                    statusId = userStatus.getId();

                    no_of_people = userStatus.getNoOfPeople();
                    title = userStatus.getTitle();
                    subTitle = userStatus.getSubTitle();
                    descrption = userStatus.getDescription();
                    alertDescrption = userStatus.getAlertDescription();
                    Utility.setSharedPreference(context, Constants.KEY_FOR_TITLE, subTitle);

                    Address add = userDetails.getAddress();
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, add.getAddress());

                    Confirmed confirmed = userDetails.getConfirmed();
                    Utility.setSharedPreference(context, Constants.KEY_STATUS, String.valueOf(confirmed.getConfirmation()));
                    Utility.setSharedPreference(context, Constants.KEY_CHANGE_STATUS, confirmed.getUpdatedAt());
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_ADD, confirmed.getClinicAddress());
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_DATE, confirmed.getDate());


                    Temprature temprature = userDetails.getTemprature();
                    Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, temprature.getTemprature());
                    Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, temprature.getTempType());

                    LastTrack lastTrack = userDetails.getLastTrack();
                    String latitudes = lastTrack.getLatitude();
                    String longitudes = lastTrack.getLongitude();

                    selectStatus(statusId);


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    if (model.getMessage().equals("User details not found. Please register/login again.")) {
                        closeSubMenusFab();
                        Utility.clearPreference(context);
                        Utility.setSharedPreference(context, Constants.KEY_FOR_TITLE, "Required LogIn for update your status");
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectStatus(int statusId) {
        tv_status_title.setText(title);
        tv_status_msg.setText(subTitle);
        tv_DetailsMsg.setText(descrption);
        tv_DetailsMsg.setVisibility(View.VISIBLE);
        tv_yellowMsg.setText(alertDescrption);
        tv_redMsg.setText(alertDescrption);

        if (statusId == 3) {    // green status
            tv_status_title.setTextColor(getResources().getColor(R.color.green_app));
            tv_people.setVisibility(View.GONE);
            tv_changeStatus.setVisibility(View.VISIBLE);
            tv_whenDidYellow.setVisibility(View.GONE);
            tv_whenDidRed.setVisibility(View.GONE);
            tv_yellowMsg.setVisibility(View.GONE);
            tv_redMsg.setVisibility(View.GONE);
            rl_ok.setVisibility(View.VISIBLE);
            rl_goBack.setVisibility(View.GONE);

        } else if (statusId == 1) {   // yellow status
            tv_status_title.setTextColor(getResources().getColor(R.color.yellow_app));
            tv_people.setVisibility(View.GONE);
            tv_status_msg.setVisibility(View.GONE);
            tv_people.setText(no_of_people + " people ");
            tv_changeStatus.setVisibility(View.VISIBLE);
            tv_whenDidYellow.setVisibility(View.GONE);
            tv_whenDidRed.setVisibility(View.GONE);
            tv_yellowMsg.setVisibility(View.VISIBLE);
            tv_redMsg.setVisibility(View.GONE);
            rl_ok.setVisibility(View.GONE);
            rl_goBack.setVisibility(View.VISIBLE);

        } else if (statusId == 2) {     // red status
            tv_status_title.setTextColor(getResources().getColor(R.color.red_app));
            tv_people.setVisibility(View.GONE);
            tv_changeStatus.setVisibility(View.VISIBLE);
            tv_whenDidYellow.setVisibility(View.GONE);
            tv_whenDidRed.setVisibility(View.GONE);
            tv_yellowMsg.setVisibility(View.GONE);
            tv_redMsg.setVisibility(View.VISIBLE);
            rl_ok.setVisibility(View.GONE);
            rl_goBack.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }

//        startService(new Intent(HomePage.this, SendNotificationMorning.class));
    }


    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutDetails.setVisibility(View.INVISIBLE);
        layoutProfile.setVisibility(View.INVISIBLE);
        layoutInfectedArea.setVisibility(View.INVISIBLE);
        layoutLogout.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.slider);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutDetails.setVisibility(View.VISIBLE);
        layoutProfile.setVisibility(View.VISIBLE);
        layoutInfectedArea.setVisibility(View.VISIBLE);
//        layoutLogout.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    private void dialogAlwaysLocationOn() {
        Dialog dialog;
        dialog = new Dialog(HomePage.this);
        dialog.setContentView(R.layout.dialog_location_alwayson);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        CenturyGothicTextview tv_ok = dialog.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    static final int LOCATION_SETTINGS_REQUEST = 1;

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            // user is back from location settings - check if location services are now enabled
            if (isLocationEnabled()) {
                Utility.getMyLocation(context);
            } else {
                dialogAlwaysLocationOn();
            }
        }
    }

    //for close app
    public void AppExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

