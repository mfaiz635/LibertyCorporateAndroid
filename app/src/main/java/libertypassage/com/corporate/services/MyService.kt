package libertypassage.com.corporate.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelTrackUserLocation
import libertypassage.com.corporate.model.TrackUserLocationDetails
import libertypassage.com.corporate.model.UserDetailsTrackUser
import libertypassage.com.corporate.model.UserStatus
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MyService : android.app.Service(), LocationListener {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var token = ""
    private var wifiMacAddress = ""
    private var wifiIpAddress = ""
    private var wifiMode = ""
    private var wifiMacAddressOld = ""
    var latitudesOld = 0.0
    var longitudesOld = 0.0
    var altitudesOld = 0.0
    var latitudes = 0.0
    var longitudes = 0.0
    var altitudes = 0.0
    var corporationId = ""
    var counter = 0

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false
    var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null


     fun MyService() {
    }

    override fun onCreate() {
        super.onCreate()
        mCurrentMyService = this
        token = Utility.getSharedPreferences(this, Constants.KEY_BEARER_TOKEN)!!
        corporationId = Utility.getSharedPreferences(this, Constants.KEY_CORPORATION_ID)!!
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground()
        }
        if (token.isNotEmpty()) {
            locationGPS
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.e(TAG, "restarting Service !!")
        counter = 0

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            val bck = ProcessMainClass()
            bck.launchService(this)
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (token.isNotEmpty())
                Log.e("ServiceToken", token)
            restartForeground()
        }
        startTimer()
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    private fun restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "restarting foreground")
            try {
                val notification = NotificationLocation()
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "App is running", "Tap for more details", R.drawable.notification_logo)
                )
//              startForeground(NOTIFICATION_ID, notification.setNotification(this, "Alert Notification", notiTitle, R.drawable.ic_notification));
                Log.e(TAG, "restarting foreground successful")
                startTimer()
            } catch (e: Exception) {
                Log.e(TAG, "Error in notification " + e.message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy called")
        // restart the never ending service
        val broadcastIntent = Intent(Globals.RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    //  this is called when the process is killed by Android
    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved called")
        // restart the never ending service
        val broadcastIntent = Intent(Globals.RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }

    private fun startTimer() {
        Log.e(TAG, "Starting timer")
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask()
        timer = Timer()
        //initialize the TimerTask's job
        initializeTimerTask()
        Log.e(TAG, "Scheduling...")
        //schedule the timer, to wake up every 5 minitus
//        timer.schedule(timerTask, 300000, 300000);
        //schedule the timer, to wake up every 10 second
        timer!!.schedule(timerTask, 60000, 60000)
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    private fun initializeTimerTask() {
        Log.e(TAG, "initialising TimerTask")
        timerTask = object : TimerTask() {
            override fun run() {
                Log.e("in timer", "in timer ++++  " + counter++)
                if (token.isNotEmpty()) {
                    val manager = getSystemService(LOCATION_SERVICE) as LocationManager
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationByFused
                    } else {
                        locationGPS
                        val latitude = locationGPS!!.latitude
                        val longitude = locationGPS!!.longitude
                        val altitude = locationGPS!!.altitude
                        Log.e("GPSLatLong", "$latitude----$longitude")
                        getWifiDetails(latitude, longitude, altitude)
                    }
                }
            }
        }
    }

    //  not needed
    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private val locationByFused: Unit
        get() {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.fastestInterval = 50000
            if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mFusedLocationClient!!.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            val altitude = location.altitude
                            getWifiDetails(latitude, longitude, altitude)
                        }
                    }
        }

    private fun getWifiDetails(latitude: Double, longitude: Double, altitude: Double) {
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val wifiManager =
                super.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        if(wifiManager.isWifiEnabled){
            if(Objects.requireNonNull<NetworkInfo>(mWifi).isConnected) {
                wifiIpAddress = wInfo.ssid
                wifiMacAddress = wInfo.bssid
                wifiMode = "Connected"
            }else{
                val wifiList = wifiManager.scanResults
                val ssidList = ArrayList<String>()
                val bssidList = ArrayList<String>()
                for (scanResult in wifiList) {
                    ssidList.add(scanResult.SSID)
                    bssidList.add(scanResult.BSSID)
                }
                if (ssidList.size != 0) {
                    wifiIpAddress = ssidList[0]
                    wifiMacAddress = bssidList[0]
                    wifiMode = "Searchable"
                }
            }
        }

        Log.e("wifiMacAddress ", wifiMacAddress)
        Log.e("wifiIpAddress ", wifiIpAddress)
        Log.e("latitude ", latitude.toString())
        Log.e("longitude ", longitude.toString())
        Log.e("altitudes ", altitude.toString())
        Log.e("latitudesOld ", latitudesOld.toString())
        Log.e("longitudesOld ", longitudesOld.toString())
        latitudes = latitude
        longitudes = longitude
        altitudes = altitude
        val locationA = Location("Point A")
        locationA.latitude = latitudesOld
        locationA.longitude = longitudesOld
        val locationB = Location("Point B")
        locationB.latitude = latitudes
        locationB.longitude = longitudes
        val results = FloatArray(1)
        Location.distanceBetween(latitudes, longitudes, latitudesOld, longitudesOld, results)
        val distance = results[0]
        Log.e("distance", distance.toString() + "")

//        if (distance < 10.0) {
//            Log.e("distance", distance + "  Location Same");
//        } else if (distance > 10.0 && latitudes != 0.0) {
//            updateLocationOnServer();
//        }

//        localNotification(latitude, longitude, distance);
        if (latitudes != 0.0 && latitudes != latitudesOld) {
            updateLocationOnServer()
        } else {
            Log.e("same", "coordinate")
        }
        latitudesOld = latitudes
        longitudesOld = longitudes
        altitudesOld = altitudes
        if (wifiMacAddressOld == "" && wifiMacAddress != "") {
            updateLocationOnServer()
        } else if (wifiMacAddress == "" && wifiMacAddressOld != "") {
            updateLocationOnServer()
        }
        wifiMacAddressOld = wifiMacAddress
    }

    private fun updateLocationOnServer() {
        Log.e("LocationUpdateServer", "Continue")
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelTrackUserLocation> = apiInterface.trackUserLocation(Constants.KEY_HEADER + token,
                Constants.KEY_BOT, latitudes, longitudes, altitudes, wifiMacAddress, "$wifiIpAddress-$wifiMode", corporationId)

        call.enqueue(object : Callback<ModelTrackUserLocation?> {
            override fun onResponse(call: Call<ModelTrackUserLocation?>, response: Response<ModelTrackUserLocation?>) {
                val model: ModelTrackUserLocation? = response.body()

                if (model != null && model.error?.equals(false)!!) {
                    Log.e("ServerUpdateDone", model.message!!)
                    val details: TrackUserLocationDetails = model.details!!
                    val userDetails: UserDetailsTrackUser = details.userDetails!!
                    val userStatus: UserStatus = userDetails.userStatus!!

                } else if (model != null && model.error?.equals(true)!!) {
                    Log.e("ErrorResponse", model.message!!)
                }
            }

            override fun onFailure(call: Call<ModelTrackUserLocation?>, t: Throwable) {
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    // getting GPS status
    // getting network status
    @get:SuppressLint("MissingPermission")
    val locationGPS: Location?
        get() {
            try {
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                // getting GPS status
                isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                // getting network status
                isNetworkEnabled =
                        locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    canGetLocation = true
                    if (isNetworkEnabled) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.e("Network", "Network")
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                                    this)
                            Log.e("GPS Enabled", "GPS Enabled")
                            if (locationManager != null) {
                                location =
                                        locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return location
        }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@MyService)
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
            //System.out.println("GET LATITUDE------"+latitude);
        }
        // return latitude
        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
            //System.out.println("GET LONGITUDE------"+longitude);
        }
        // return longitude
        return longitude
    }

    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
//            Log.e("onLocationChanged", latitude+"-"+longitude);
    }

    companion object {
        protected const val NOTIFICATION_ID = 1234
        private const val TAG = "Service"
        private var mCurrentMyService: MyService? = null

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 5 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 40 // 60 second
                .toLong()

        /**
         * static to avoid multiple timers to be created when the service is called several times
         */
        private var timer: Timer? = null
        private var timerTask: TimerTask? = null
        fun getmCurrentMyService(): MyService? {
            return mCurrentMyService
        }

        fun setmCurrentMyService(mCurrentMyService: MyService?) {
            Companion.mCurrentMyService = mCurrentMyService
        }
    }
}