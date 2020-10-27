package libertypassage.com.app.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import libertypassage.com.app.R;
import libertypassage.com.app.basic.LocationHistory;
import libertypassage.com.app.models.ModelTrackUserLocation;
import libertypassage.com.app.models.TrackUserLocationDetails;
import libertypassage.com.app.models.UserDetailsTrackUser;
import libertypassage.com.app.models.UserStatus;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyService extends android.app.Service implements LocationListener {
    protected static final int NOTIFICATION_ID = 1234;
    private static String TAG = "Service";
    private static MyService mCurrentMyService;
    private int counter = 0;
    private String token, wifiMacAddress = "", wifiIpAddress = "", wifiMacAddressOld = "";
    double latitudesOld = 0, longitudesOld = 0, altitudesOld = 0,
            latitudes = 0, longitudes = 0, altitudes = 0;
    private FusedLocationProviderClient mFusedLocationClient;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 40; // 60 second
    // Declaring a Location Manager
    protected LocationManager locationManager;


    public MyService() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentMyService = this;
        token = Utility.getSharedPreferences(this, Constants.KEY_BEARER_TOKEN);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        if(token!=null){
            getLocationGPS();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "restarting Service !!");
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (!token.isEmpty())
                Log.e("ServiceToken", token);
            restartForeground();
        }
        startTimer();
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "restarting foreground");
            try {
                NotificationLocation notification = new NotificationLocation();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "", "", R.drawable.notification_logo));
//                    startForeground(NOTIFICATION_ID, notification.setNotification(this, "Alert Notification", notiTitle, R.drawable.ic_notification));
                Log.e(TAG, "restarting foreground successful");
                startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    //  this is called when the process is killed by Android
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private static Timer timer;
    private static TimerTask timerTask;


    public void startTimer() {
        Log.e(TAG, "Starting timer");
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        Log.e(TAG, "Scheduling...");
        //schedule the timer, to wake up every 5 minitus
//        timer.schedule(timerTask, 300000, 300000);
        //schedule the timer, to wake up every 10 second
        timer.schedule(timerTask, 60000, 60000);
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        Log.e(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.e("in timer", "in timer ++++  " + (counter++));
                if(token!=null) {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        getLocationByFused();
                    } else {
                        getLocationGPS();
                        double latitude = getLocationGPS().getLatitude();
                        double longitude = getLocationGPS().getLongitude();
                        double altitude = getLocationGPS().getAltitude();
                        Log.e("GPSLatLong", latitude + "----" + longitude);
                        getWifiDetails(latitude, longitude, altitude);
                    }
                }
            }
        };
    }


    //  not needed
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static MyService getmCurrentMyService() {
        return mCurrentMyService;
    }

    public static void setmCurrentMyService(MyService mCurrentMyService) {
        MyService.mCurrentMyService = mCurrentMyService;
    }

    private void getLocationByFused() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(50000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            double altitude = location.getAltitude();
                            getWifiDetails(latitude, longitude, altitude);
                        }
                    }
                });
    }


    private void getWifiDetails(double latitude, double longitude, double altitude) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        if (Objects.requireNonNull(mWifi).isConnected()) {
            wifiIpAddress = wInfo.getSSID();
            wifiMacAddress = wInfo.getBSSID();
        } else {
            wifiIpAddress = "";
            wifiMacAddress = "";
            Log.e("WifiNotAvailable", "Connect WiFi right now");
        }

        Log.e("wifiMacAddress ", wifiMacAddress);
        Log.e("wifiIpAddress ", wifiIpAddress);
        Log.e("latitude ", String.valueOf(latitude));
        Log.e("longitude ", String.valueOf(longitude));
        Log.e("altitudes ", String.valueOf(altitude));
        Log.e("latitudesOld ", String.valueOf(latitudesOld));
        Log.e("longitudesOld ", String.valueOf(longitudesOld));
        latitudes = latitude;
        longitudes = longitude;
        altitudes = altitude;

        Location locationA = new Location("Point A");
        locationA.setLatitude(latitudesOld);
        locationA.setLongitude(longitudesOld);
        Location locationB = new Location("Point B");
        locationB.setLatitude(latitudes);
        locationB.setLongitude(longitudes);
        float[] results = new float[1];
        Location.distanceBetween(latitudes, longitudes, latitudesOld, longitudesOld, results);
        float distance = results[0];
        Log.e("distance", distance+"");

//        if (distance < 10.0) {
//            Log.e("distance", distance + "  Location Same");
//        } else if (distance > 10.0 && latitudes != 0.0) {
//            updateLocationOnServer();
//        }

//        localNotification(latitude, longitude, distance);

        if(latitudes != 0.0 && latitudes != latitudesOld) {
            updateLocationOnServer();
        }else{
            Log.e("same", "coordinate");
        }
        latitudesOld = latitudes;
        longitudesOld = longitudes;
        altitudesOld = altitudes;

        if (wifiMacAddressOld.equals("") && !wifiMacAddress.equals("")) {
            updateLocationOnServer();
        } else if (wifiMacAddress.equals("") && !wifiMacAddressOld.equals("")) {
            updateLocationOnServer();
        }
        wifiMacAddressOld = wifiMacAddress;
    }

    private void updateLocationOnServer() {
        Log.e("LocationUpdateServer", "Continue");
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelTrackUserLocation> call = apiInterface.addTrackUserLocation(Constants.KEY_HEADER + token,
                Constants.KEY_BOT, latitudes, longitudes, altitudes, wifiMacAddress, wifiIpAddress, "0");

        call.enqueue(new Callback<ModelTrackUserLocation>() {
            @Override
            public void onResponse(Call<ModelTrackUserLocation> call, Response<ModelTrackUserLocation> response) {
                ModelTrackUserLocation model = response.body();
                if (model != null && model.getError().equals(false)) {
                    Log.e("TrackUserService", model.getMessage());
                    TrackUserLocationDetails details = model.getDetails();
                    UserDetailsTrackUser userDetails = details.getUserDetails();
                    UserStatus userStatus = userDetails.getUserStatus();

                } else if (model != null && model.getError().equals(true)) {
                    Log.e("ErroResponse", model.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ModelTrackUserLocation> call, Throwable t) {
                Log.e("model", "onFailure    " + t.getMessage());
            }
        });
    }

    void localNotification(double latitudes11, double longitudes11, double distance) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "evening111");
        mBuilder.setContentText(latitudes11 + "-----" + longitudes11+"----"+distance);
        mBuilder.setSmallIcon(R.drawable.notification_logo);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("5432771", "EVENING111", importance);
            mBuilder.setChannelId("5432771");
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        Intent notifyIntent = new Intent(this, LocationHistory.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 15, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }


    @SuppressLint("MissingPermission")
    public Location getLocationGPS() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.e("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.e("LAST KNOW LOCATION", "location");
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                System.out.println("LAST KNOW LOCATION------"+latitude+"-"+longitude);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(MyService.this);
        }
    }


    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
            //System.out.println("GET LATITUDE------"+latitude);
        }
        // return latitude
        return latitude;
    }


    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
            //System.out.println("GET LONGITUDE------"+longitude);
        }
        // return longitude
        return longitude;
    }


    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
//            Log.e("onLocationChanged", latitude+"-"+longitude);
        }
    }


//    http://android-devhelp.blogspot.com/2015/12/making-service-run-in-foreground.html
//    https://stackoverflow.com/questions/35578586/background-process-timer-on-android
}
