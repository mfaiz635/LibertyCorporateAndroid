package libertypassage.com.app.services;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import libertypassage.com.app.R;
import libertypassage.com.app.models.user.Details;
import libertypassage.com.app.models.user.ModelUser;
import libertypassage.com.app.models.user.UserDetails;
import libertypassage.com.app.models.user.UserStatus;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyService extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1234;
    private static String TAG = "Service";
    private static MyService mCurrentMyService;
    private int counter = 0;
    private String token, notiTitle, wifiMacAddress="", wifiIpAddress="", wifiMacAddressOld="";
    Double latitudesOld=22.1234, longitudesOld=75.1234,
            latitudes=0.0, longitudes=0.0, altitudes=0.0;
    private FusedLocationProviderClient mFusedLocationClient;


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
        getLastLocation();
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
        //schedule the timer, to wake up every 5 second
        timer.schedule(timerTask, 300000, 300000);
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        Log.e(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.e("in timer", "in timer ++++  " + (counter++));

                getWifiDetails();
                getLastLocation();

                Log.e("wifiMacAddress ", wifiMacAddress);
                Log.e("wifiIpAddress ", wifiIpAddress);
                Log.e("altitudes ", String.valueOf(altitudes));
                Log.e("latitude ", String.valueOf(latitudes));
                Log.e("longitude ", String.valueOf(longitudes));
                Log.e("latitudesOld ", String.valueOf(latitudesOld));
                Log.e("longitudesOld ", String.valueOf(longitudesOld));

                Location locationA = new Location("Point A");
                locationA.setLatitude(latitudesOld);
                locationA.setLongitude(longitudesOld);
                Location locationB = new Location("Point B");
                locationB.setLatitude(latitudes);
                locationB.setLongitude(longitudes);
                double distance = locationA.distanceTo(locationB) ;

//                if(distance<5.0){
//                    Log.e("distance", distance+"  Location Same");
//                }else if(distance>5.0 && latitudes!=0.0){
//                    updateLocationOnServer(distance);
//                }

                if(latitudesOld!=null && latitudesOld.equals(latitudes)){
                    Log.e("same","value");
                    Log.e("distance", distance+"  Location Same");
                }else{
                    updateLocationOnServer(distance);
                    Log.e("different","value");
                }
                latitudesOld = latitudes;
                longitudesOld = longitudes;


                if(wifiMacAddressOld.equals("") && !wifiMacAddress.equals("")){
                    updateLocationOnServer(distance);
                }else if(wifiMacAddress.equals("") && !wifiMacAddressOld.equals("")) {
                    updateLocationOnServer(distance);
                }
                wifiMacAddressOld = wifiMacAddress;
            }
        };
    }


    private void updateLocationOnServer(double distance) {
        Log.e("distance", distance+"");
        Log.e("LocationUpdateServer", "Continue");
//        Log.e("Parameter", Constants.KEY_HEADER+token+" Lat : "+latitudes+" Long : "+
//        longitudes+" Alt : "+altitudes+" Mac : "+wifiMacAddress+" Wifi : "+wifiIpAddress);
        if (token != null) {
            ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<ModelUser> call = apiInterface.addTrackUserLocation(Constants.KEY_HEADER+token,
                    Constants.KEY_BOT, latitudes, longitudes, altitudes, wifiMacAddress, wifiIpAddress);

            call.enqueue(new Callback<ModelUser>() {
                @Override
                public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                    ModelUser model = response.body();
                    if (model != null && model.getError().equals(false)) {
                        Log.e("SuccessResponse", model.getMessage());
                        Details details = model.getDetails();
                        UserDetails userDetails = details.getUserDetails();
                        UserStatus userStatus = userDetails.getUserStatus();
                        notiTitle = userStatus.getSubTitle();


                        Log.e("notiTitle",notiTitle+"token ; "+token);
                    } else if (model != null && model.getError().equals(true)) {
                        Log.e("ErroResponse", model.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ModelUser> call, Throwable t) {
                    Log.e("model", "onFailure    " + t.getMessage());
                }
            });
        } else {
//            Log.e("same", " LatOld : " + latitudesOld + " Lat : " + latitudes);
        }
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            latitudes = location.getLatitude();
                            longitudes = location.getLongitude();
                            altitudes = location.getAltitude();
                            Log.e("newLatitudes", latitudes+"");
                            Log.e("newLongitudes", longitudes+"");
                            Log.e("LocationAccuracy", location.getAccuracy()+"");
                        }
                    }
                }
        );
    }

    @SuppressLint("HardwareIds")
    private void getWifiDetails() {
        //check network type
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        //check WifiInfo
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        if (Objects.requireNonNull(mWifi).isConnected()) {
//            wifiIpAddress = Formatter.formatIpAddress(dhcp.gateway);
            wifiIpAddress =  wInfo.getSSID();
            wifiMacAddress = wInfo.getBSSID();
//            Log.e("wifiMacAddress", wifiMacAddress);
//            Log.e("WifiIPAddress", wifiIpAddress);
//            Log.e("ssidName", wInfo.getSSID());
//            Log.e("deviceMacAddress", wInfo.getMacAddress());
        }else{
            Log.e("WifiNotAvailable", "Connect WiFi right now");
        }
    }

//    http://android-devhelp.blogspot.com/2015/12/making-service-run-in-foreground.html
//    https://stackoverflow.com/questions/35578586/background-process-timer-on-android
}
