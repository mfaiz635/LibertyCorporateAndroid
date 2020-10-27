package libertypassage.com.app.basic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.util.Objects;
import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelTrackUserLocation;
import libertypassage.com.app.models.TrackUserLocationDetails;
import libertypassage.com.app.models.UserDetailsTrackUser;
import libertypassage.com.app.models.UserStatus;
import libertypassage.com.app.services.GPSTrackerLocation;
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
    private TextView tv_changeStatus, tv_status_title, tv_status_msg, tv_detailsMsg, tv_colorMsg;
    private RelativeLayout rl_ok;
    private ImageSpeedometer imageSpeedometer;
    private ProgressDialog progressDialog;
    private String token, title, subTitle, descrption, alertDescrption;
    private int statusId, PERMISSION_ID = 44;
    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private LinearLayout layoutDetails, layoutProfile, layoutInfectedArea, layoutLogout;
    private String wifiMacAddress, wifiIpAddress;
    private FusedLocationProviderClient mFusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        context = HomePage.this;
        findID();
        init();
    }


    private void findID() {
        imageSpeedometer = findViewById(R.id.imageSpeedometer);
        tv_status_title = findViewById(R.id.tv_status_title);
        tv_status_msg = findViewById(R.id.tv_status_msg);
        tv_detailsMsg = findViewById(R.id.tv_DetailsMsg);
        tv_colorMsg = findViewById(R.id.tv_colorMsg);
        tv_changeStatus = findViewById(R.id.tv_changeStatus);
        rl_ok = findViewById(R.id.rl_ok);

        fabSettings = findViewById(R.id.fabSetting);
        layoutDetails = findViewById(R.id.layoutDetails);
        layoutProfile = findViewById(R.id.layoutProfile);
        layoutInfectedArea = findViewById(R.id.layoutInfectedArea);
        layoutLogout = findViewById(R.id.layoutLogout);

        tv_changeStatus.setOnClickListener(this);
        rl_ok.setOnClickListener(this);
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String statusId = Utility.getSharedPreferences(context, Constants.KEY_CURRENT_STATUS);
        if (!statusId.isEmpty()) {
            selectStatus(Integer.parseInt(statusId));
        }
        String riskScore = Utility.getSharedPreferences(context, Constants.KEY_RISK_SCORE);
        if (!riskScore.isEmpty()) {
            imageSpeedometer.speedTo(Integer.parseInt(riskScore));
        }

        GPSLocation();

//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                Utility.getMyLocation(context);
//                getLastLocation();
//            } else {
//                dialogAlwaysLocationOn();
//            }
//        } else {
//            requestPermissions();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    public void GPSLocation() {
        GPSTrackerLocation gps = new GPSTrackerLocation(context);
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
            enableGPSSettingsRequest(HomePage.this);
        }else{
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            double altitudes = gps.getAltitude();
            Utility.setSharedPreference(context, Constants.KEY_LAT, String.valueOf(latitude));
            Utility.setSharedPreference(context, Constants.KEY_LONG, String.valueOf(longitude));
            getWifiDetails(latitude, longitude, altitudes);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_changeStatus: {
                Intent intent = new Intent(context, ChangeStatus.class);
                intent.putExtra("statusId", statusId);
                startActivity(intent);
                break;
            }

            case R.id.rl_ok: {
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
                startActivity(intent);
                closeSubMenusFab();
                break;
            }

            case R.id.layoutLogout: {
//                Utility.clearPreference(context);
//                Utility.setSharedPreference(context, Constants.KEY_FOR_TITLE, "Required LogIn for update your status");
//                Intent intent = new Intent(context, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
                break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitudes = location.getLatitude();
                            double longitudes = location.getLongitude();
                            double altitudes = location.getAltitude();
                            Utility.setSharedPreference(context, Constants.KEY_LAT, String.valueOf(latitudes));
                            Utility.setSharedPreference(context, Constants.KEY_LONG, String.valueOf(longitudes));
                            getWifiDetails(latitudes, longitudes, altitudes);
                        }
                    }
                });
    }

    @SuppressLint("HardwareIds")
    private void getWifiDetails(double latitudes, double longitudes, double altitudes) {
        //check network type
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        if (Objects.requireNonNull(mWifi).isConnected()) {
            wifiIpAddress = wInfo.getSSID();
            wifiMacAddress = wInfo.getBSSID();
        } else {
            Log.e("WifiNotAvailable", "Connect WiFi right now");
        }

        if (Utility.isConnectingToInternet(context)) {
            if (latitudes != 0.0) {
                updateLocationOnServer(latitudes, longitudes, altitudes);
            }
        } else {
            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
        }
    }

    private void updateLocationOnServer(double latitudes, double longitudes, double altitudes) {
        Log.e("HomeParameter", Constants.KEY_HEADER + token + " Lat : " + latitudes + " Long : " +
                longitudes + " Alt : " + altitudes + " Mac : " + wifiMacAddress + " Wifi : " + wifiIpAddress);
        progressDialog = new ProgressDialog(context, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelTrackUserLocation> call = apiInterface.addTrackUserLocation(Constants.KEY_HEADER + token,
                Constants.KEY_BOT, latitudes, longitudes, altitudes, wifiMacAddress, wifiIpAddress, "0");

        call.enqueue(new Callback<ModelTrackUserLocation>() {
            @Override
            public void onResponse(Call<ModelTrackUserLocation> call, Response<ModelTrackUserLocation> response) {
                progressDialog.dismiss();
                ModelTrackUserLocation model = response.body();
                Log.e("updateLocationHome", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    TrackUserLocationDetails details = model.getDetails();
                    UserDetailsTrackUser userDetails = details.getUserDetails();

                    imageSpeedometer.speedTo(userDetails.getRiskScore());

                    UserStatus userStatus = userDetails.getUserStatus();
                    statusId = userStatus.getId();
                    title = userStatus.getTitle();
                    subTitle = userStatus.getSubTitle();
                    descrption = userStatus.getDescription();
                    alertDescrption = userStatus.getAlertDescription();
                    Utility.setSharedPreference(context, Constants.KEY_RISK_SCORE, String.valueOf(userDetails.getRiskScore()));
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_STATUS, String.valueOf(statusId));
                    Utility.setSharedPreference(context, Constants.KEY_TITLE, title);
                    Utility.setSharedPreference(context, Constants.KEY_SUB_TITLE, subTitle);
                    Utility.setSharedPreference(context, Constants.KEY_DESCRIPTION, descrption);
                    Utility.setSharedPreference(context, Constants.KEY_ALERT_DESCRIPTION, alertDescrption);
                    selectStatus(statusId);


                } else if (model != null && model.getError().equals(true)) {
                    progressDialog.dismiss();
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    if (model.getMessage().equals("User details not found. Please register/login again.")) {
                        closeSubMenusFab();
                        Utility.clearPreference(context);
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelTrackUserLocation> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Home", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectStatus(int statusId) {
        tv_status_title.setText(Utility.getSharedPreferences(context, Constants.KEY_TITLE));
        tv_status_msg.setText(Utility.getSharedPreferences(context, Constants.KEY_SUB_TITLE));
        tv_detailsMsg.setText(Utility.getSharedPreferences(context, Constants.KEY_DESCRIPTION));
        tv_colorMsg.setText(Utility.getSharedPreferences(context, Constants.KEY_ALERT_DESCRIPTION));

        if (statusId == 3) {           // green status
            tv_status_title.setTextColor(getResources().getColor(R.color.green_app));
            tv_colorMsg.setVisibility(View.GONE);
        } else if (statusId == 2) {     // red status
            tv_status_title.setTextColor(getResources().getColor(R.color.red_app));
            tv_colorMsg.setTextColor(getResources().getColor(R.color.red_app));
            tv_colorMsg.setVisibility(View.VISIBLE);
        } else if (statusId == 1) {   // yellow status
            tv_status_title.setTextColor(getResources().getColor(R.color.yellow_app));
            tv_colorMsg.setTextColor(getResources().getColor(R.color.yellow_app));
            tv_colorMsg.setVisibility(View.VISIBLE);
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
//       startService(new Intent(HomePage.this, LocationServiceContinue.class));
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
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
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
    static final int ENABLED_GPS_REQUEST = 2;
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST:
                if (isLocationEnabled()) {
                    getLastLocation();
                    Utility.getMyLocation(context);
                } else {
                    dialogAlwaysLocationOn();
                }
                break;
            case ENABLED_GPS_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        GPSLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        if (checkPermissions()) {
                            if (isLocationEnabled()) {
                                getLastLocation();
                            } else {
                                dialogAlwaysLocationOn();
                            }
                        } else {
                            requestPermissions();
                        }
                        Log.e("Cancel", "Not enabled GPS");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void enableGPSSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(HomePage.this, ENABLED_GPS_REQUEST);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    //for close app
    public void AppExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        finish();
    }
}
