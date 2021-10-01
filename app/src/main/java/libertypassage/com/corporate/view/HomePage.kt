package libertypassage.com.corporate.view
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
import com.github.anastr.speedviewlib.ImageSpeedometer
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.android.synthetic.main.inside_fab_layout.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.*
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.services.GPSTrackerLocation
import libertypassage.com.corporate.services.ProcessMainClass
import libertypassage.com.corporate.services.RestartServiceBroadcastReceiver
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class HomePage : AppCompatActivity(), View.OnClickListener {
    private var TAG = HomePage::class.java.simpleName
    lateinit var context: Context
    private var dialogProgress: DialogProgress? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var fabSetting: FloatingActionButton? = null
    private var imageSpeedometer: ImageSpeedometer? = null
    private var trCornerProgressBar: TextRoundCornerProgressBar? = null
    private var PERMISSION_ID = 44
    private var token = ""
    private var wifiMacAddress = ""
    private var wifiIpAddress = ""
    private var wifiMode = ""
    private var corporationId = ""
    private var title = ""
    private var subTitle = ""
    private var description = ""
    private var alertDescription = ""
    private var statusId = ""
    private var riskScore = ""
    private var countryName = ""
    private var fabExpanded = false


    @RequiresApi(Build.VERSION_CODES.Q)
    val permissions = arrayOf<String?>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        context = this@HomePage

        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        corporationId = Utility.getSharedPreferences(context, Constants.KEY_CORPORATION_ID)!!
        dialogProgress = DialogProgress(context)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        imageSpeedometer = findViewById(R.id.imageSpeedometer)
        fabSetting = findViewById(R.id.fabSetting)
        trCornerProgressBar = findViewById(R.id.trCornerProgressBar)

        statusId = Utility.getSharedPreferences(context, Constants.KEY_CURRENT_STATUS)!!
        if (statusId.isNotEmpty()) {
            selectStatus(statusId.toInt())
        }
        riskScore = Utility.getSharedPreferences(context, Constants.KEY_RISK_SCORE)!!
        if (riskScore.isNotEmpty()) {
            imageSpeedometer!!.speedTo(riskScore.toFloat())
        }

        trCornerProgressBar!!.progress = 90.toFloat()
        trCornerProgressBar!!.progressText = 90.toString()

        fabSetting?.setOnClickListener(this)
        layoutRecordTemp.setOnClickListener(this)
        layoutProfile.setOnClickListener(this)
        layoutHotspots.setOnClickListener(this)
        layoutSafeEntry.setOnClickListener(this)
        layoutAbout.setOnClickListener(this)
        layoutShareApp.setOnClickListener(this)
        layoutLogout.setOnClickListener(this)
        tvChangeStatus.setOnClickListener(this)
        rlOk.setOnClickListener(this)
        Utility.setSharedPreference(context, Constants.KEY_START, "5")
        closeSubMenusFab()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            RestartServiceBroadcastReceiver.scheduleJob(applicationContext)
//        } else {
//            val bck = ProcessMainClass()
//            bck.launchService(applicationContext)
//        }
//        getGPSLocation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 789)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.fabSetting -> {
                if (fabExpanded) {
                    closeSubMenusFab()
                } else {
                    openSubMenusFab()
                }
            }

            R.id.layoutRecordTemp -> {
                val intent = Intent(context, UpdateMyTemperature::class.java)
                intent.putExtra("from", "home")
                startActivity(intent)
                closeSubMenusFab()
            }

            R.id.layoutProfile -> {
                val intent = Intent(context, UpdateProfile::class.java)
                startActivity(intent)
                closeSubMenusFab()
            }

            R.id.layoutHotspots -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 456)
                } else {
                    if (isLocationEnabled()) {
                        Utility.getMyLocation(context)
                    } else {
                        dialogAlwaysLocationOn()
                    }
                    val intent = Intent(context, HotSpotsActivity::class.java)
                    startActivity(intent)
                    closeSubMenusFab()
                }
            }

            R.id.layoutSafeEntry -> {
                val intent = Intent(context, SafeEntryActivity::class.java)
                startActivity(intent)
                closeSubMenusFab()
            }

            R.id.layoutAbout -> {
                val intent = Intent(context, AboutAppActivity::class.java)
                startActivity(intent)
                closeSubMenusFab()
            }

            R.id.layoutShareApp -> {
                dialogAppShare()
            }

            R.id.layoutLogout -> {
//                Utility.clearPreference(context)
//                val intent = Intent(context, LogInActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
            }

            R.id.tvChangeStatus -> {
                val intent = Intent(context, ChangeStatus::class.java)
                startActivity(intent)
            }

            R.id.rlOk -> {
                appExit()
            }
        }
    }

    private fun getGPSLocation() {
        val gps = GPSTrackerLocation(context)
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPSSettingsRequest(this@HomePage)
        } else {
            try {
                val latitude: Double = gps.getLatitude()
                val longitude: Double = gps.getLongitude()
                val altitudes: Double = gps.getAltitude()
                Utility.setSharedPreference(context, Constants.KEY_LAT, latitude.toString())
                Utility.setSharedPreference(context, Constants.KEY_LONG, longitude.toString())
                getWifiDetails(latitude, longitude, altitudes)
            } catch (e: Exception) {
                Log.e(TAG, "Error " + e.message)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient!!.lastLocation
            .addOnSuccessListener(
                this
            ) { location ->
                if (location != null) {
                    val latitudes = location.latitude
                    val longitudes = location.longitude
                    val altitudes = location.altitude
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_LAT,
                        latitudes.toString()
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_LONG,
                        longitudes.toString()
                    )
                    getWifiDetails(latitudes, longitudes, altitudes)
                }
            }
    }

    private fun getWifiDetails(latitudes: Double, longitudes: Double, altitudes: Double) {
        //check network type
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val wifiManager = super.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        if (wifiManager.isWifiEnabled) {
            if (Objects.requireNonNull<NetworkInfo>(mWifi).isConnected) {
                wifiIpAddress = wInfo.ssid
                wifiMacAddress = wInfo.bssid
                wifiMode = "Connected"
            } else {
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

        try {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())
            addresses = geoCoder.getFromLocation(latitudes, longitudes, 1)
            countryName = addresses[0].countryName
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (Utility.isConnectingToInternet(context)) {
            if (latitudes != 0.0) {
                trackUserLocation(latitudes, longitudes, altitudes)
            }
        } else {
            Toast.makeText(context, R.string.connectInternet, Toast.LENGTH_LONG).show()
        }
    }

    private fun trackUserLocation(latitudes: Double, longitudes: Double, altitudes: Double) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelTrackUserLocation> = apiInterface.trackUserLocation(
            Constants.KEY_HEADER + token,
            Constants.KEY_BOT,
            latitudes,
            longitudes,
            altitudes,
            wifiMacAddress,
            "$wifiIpAddress-$wifiMode",
            corporationId
        )
        call.enqueue(object : Callback<ModelTrackUserLocation?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ModelTrackUserLocation?>,
                response: Response<ModelTrackUserLocation?>
            ) {
                val modelResponse: ModelTrackUserLocation? = response.body()
                dialogProgress!!.dismiss()
                Log.e("TrackUserLocationHome", Gson().toJson(modelResponse))

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val details: TrackUserLocationDetails = modelResponse.details!!

                    val times = details.timerCountdown!!
                    val min: Long = times.substring(0, 2).toInt().toLong()
                    val sec: Long = times.substring(3).toInt().toLong()
                    val t = min * 60L + sec
                    val result = TimeUnit.SECONDS.toMillis(t)
                    startTimeCounter(result)

                    val userDetails: UserDetailsTrackUser = details.userDetails!!
                    imageSpeedometer!!.speedTo(userDetails.riskScore!!.toFloat())

                    val userStatus: UserStatus = userDetails.userStatus!!
                    statusId = userStatus.id?.toString()!!
                    title = userStatus.title!!
                    subTitle = userStatus.subTitle.toString()
                    description = userStatus.description!!
                    alertDescription = userStatus.alertDescription!!
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_RISK_SCORE,
                        userDetails.riskScore.toString()
                    )
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_STATUS, statusId)
                    Utility.setSharedPreference(context, Constants.KEY_TITLE, title)
                    Utility.setSharedPreference(context, Constants.KEY_SUB_TITLE, subTitle)
                    Utility.setSharedPreference(context, Constants.KEY_DESCRIPTION, description)
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_ALERT_DESCRIPTION,
                        alertDescription
                    )
                    selectStatus(statusId.toInt())

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    if (modelResponse.message.equals("User details not found. Please register/login again.")) {
                        closeSubMenusFab()
                        Utility.clearPreference(context)
                        val intent = Intent(context, LogInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ModelTrackUserLocation?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun getCountdownDetails() {
//        dialogProgress!!.show()
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelTrackUserLocation> = apiInterface.getCountdownDetails(
            Constants.KEY_HEADER+token,
            Constants.KEY_BOT,
        )
        call.enqueue(object : Callback<ModelTrackUserLocation?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ModelTrackUserLocation?>,
                response: Response<ModelTrackUserLocation?>
            ) {
                val modelResponse: ModelTrackUserLocation? = response.body()
//                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {
                    val details: TrackUserLocationDetails = modelResponse.details!!

                    val times = details.timerCountdown!!
                    val min: Long = times.substring(0, 2).toInt().toLong()
                    val sec: Long = times.substring(3).toInt().toLong()
                    val t = min * 60L + sec
                    val result = TimeUnit.SECONDS.toMillis(t)
                    startTimeCounter(result)

                    val userDetails: UserDetailsTrackUser = details.userDetails!!
                    imageSpeedometer!!.speedTo(userDetails.riskScore!!.toFloat())

                    val userStatus: UserStatus = userDetails.userStatus!!
                    statusId = userStatus.id?.toString()!!
                    title = userStatus.title!!
                    subTitle = userStatus.subTitle.toString()
                    description = userStatus.description!!
                    alertDescription = userStatus.alertDescription!!
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_RISK_SCORE,
                        userDetails.riskScore.toString()
                    )
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_STATUS, statusId)
                    Utility.setSharedPreference(context, Constants.KEY_TITLE, title)
                    Utility.setSharedPreference(context, Constants.KEY_SUB_TITLE, subTitle)
                    Utility.setSharedPreference(context, Constants.KEY_DESCRIPTION, description)
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_ALERT_DESCRIPTION,
                        alertDescription
                    )
                    selectStatus(statusId.toInt())

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
//                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    if (modelResponse.message.equals("User details not found. Please register/login again.")) {
                        closeSubMenusFab()
                        Utility.clearPreference(context)
                        val intent = Intent(context, LogInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ModelTrackUserLocation?>, t: Throwable) {
//                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun startTimeCounter(timerCountdown: Long) {
        object : CountDownTimer(timerCountdown, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvTagTimer.text ="Next status update will be in : ";
                tvTimer.text = ""+String.format("0%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                tvTimer.text = "05:00"
                getCountdownDetails()
            }
        }.start()
    }

    private fun selectStatus(statusId: Int) {
        tv_status_title.text = Utility.getSharedPreferences(context, Constants.KEY_TITLE)
        tvStatusMsg.text = Utility.getSharedPreferences(context, Constants.KEY_SUB_TITLE)
        tvDetailsMsg.text = Utility.getSharedPreferences(context, Constants.KEY_DESCRIPTION)
        tv_colorMsg.text = Utility.getSharedPreferences(context, Constants.KEY_ALERT_DESCRIPTION)

        when (statusId) {
            3 -> {           // green status
                tv_status_title.setTextColor(resources.getColor(R.color.green_app))
                tv_colorMsg.visibility = View.GONE
                tvStatusMsg.visibility = View.VISIBLE
            }
            2 -> {     // red status
                tv_status_title.setTextColor(resources.getColor(R.color.red_app))
                tv_colorMsg.setTextColor(resources.getColor(R.color.red_app))
                tv_colorMsg.visibility = View.VISIBLE
                tvStatusMsg.visibility = View.VISIBLE
            }
            1 -> {   // yellow status
                tv_status_title.setTextColor(resources.getColor(R.color.yellow_app))
                tv_colorMsg.setTextColor(resources.getColor(R.color.yellow_app))
                tv_colorMsg.visibility = View.VISIBLE
                tvStatusMsg.visibility = View.GONE
            }
        }
    }

    //closes FAB submenus
    private fun closeSubMenusFab() {
        layoutRecordTemp.visibility = View.GONE
        layoutProfile.visibility = View.GONE
        layoutHotspots.visibility = View.GONE
        layoutSafeEntry.visibility = View.GONE
        layoutAbout.visibility = View.GONE
        layoutShareApp.visibility = View.GONE
        layoutLogout.visibility = View.GONE
        fabSetting!!.setImageResource(R.drawable.slider)
        fabExpanded = false
    }

    //Opens FAB submenus
    private fun openSubMenusFab() {
        layoutRecordTemp.visibility = View.VISIBLE
        layoutProfile.visibility = View.VISIBLE
        layoutHotspots.visibility = View.VISIBLE
        layoutAbout.visibility = View.VISIBLE
        layoutShareApp.visibility = View.VISIBLE

//        layoutLogout.visibility = View.VISIBLE
//        Singapore, UK, India
//        if(countryName == "Singapore" || countryName == "United Kingdom" || countryName == "UK" || countryName == "India"){
//            layoutSafeEntry.visibility = View.VISIBLE
//        }else{
//            layoutSafeEntry.visibility = View.GONE
//        }

        if (countryName == "Singapore") {
            layoutSafeEntry.visibility = View.VISIBLE
        } else {
            layoutSafeEntry.visibility = View.GONE
        }
        fabSetting!!.setImageResource(R.drawable.ic_close_white)
        fabExpanded = true
    }

    private fun dialogAlwaysLocationOn() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_location_alwayson)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        tvOk.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1)
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val LOCATION_SETTINGS_REQUEST = 1
    private val ENABLED_GPS_REQUEST = 2
    private val APP_UPDATE_REQUEST = 3

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_SETTINGS_REQUEST -> if (isLocationEnabled()) {
                getLastLocation()
                Utility.getMyLocation(context)
            } else {
                dialogAlwaysLocationOn()
            }
            ENABLED_GPS_REQUEST -> when (resultCode) {
                RESULT_OK -> getGPSLocation()
                RESULT_CANCELED -> {
                    if (checkPermissions()) {
                        if (isLocationEnabled()) {
                            getLastLocation()
                        } else {
                            dialogAlwaysLocationOn()
                        }
                    } else {
                        requestPermissions()
                    }
                    Log.e("Cancel", "Not enabled GPS")
                }
                else -> {
                }
            }
            APP_UPDATE_REQUEST ->
                if (resultCode != Activity.RESULT_OK) {
                    Log.e(TAG, "onActivityResult: app download failed")
                }
            104 ->
                if (resultCode != Activity.RESULT_OK) {
                    if (isLocationEnabled()) {
                        Utility.getMyLocation(context)
                    } else {
                        dialogAlwaysLocationOn()
                    }
                    val intent = Intent(context, HotSpotsActivity::class.java)
                    startActivity(intent)
                    closeSubMenusFab()
                }
        }
    }

    private fun enableGPSSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.e(
                    TAG,
                    "All location settings are satisfied."
                )
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(this@HomePage, ENABLED_GPS_REQUEST)
                } catch (e: SendIntentException) {
                    Log.e(TAG, "PendingIntent unable to execute request.")
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                    TAG,
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            456 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationEnabled()) {
                        Utility.getMyLocation(context)
                    } else {
                        dialogAlwaysLocationOn()
                    }
                    val intent = Intent(context, HotSpotsActivity::class.java)
                    startActivity(intent)
                    closeSubMenusFab()
                    // permission was granted, yay! do the
                } else {
                    // permission denied, boo! Disable the
                    dialogRequestPermission()
                }
                return
            }
            789 -> {
                try {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            RestartServiceBroadcastReceiver.scheduleJob(applicationContext)
                        } else {
                            val bck = ProcessMainClass()
                            bck.launchService(applicationContext)
                        }
                        getGPSLocation()
                        // permission was granted, yay! do the
                    } else {
                        Log.e("Permission", "Deny")
                        val latitude = Utility.getSharedPreferences(context, Constants.KEY_LAT)!!
                        val longitude = Utility.getSharedPreferences(context, Constants.KEY_LONG)!!
                        val altitudes = Utility.getSharedPreferences(context, Constants.KEY_ALT)!!
                        if (latitude.isNotEmpty() && latitude != "") {
                            getWifiDetails(
                                latitude.toDouble(),
                                longitude.toDouble(),
                                altitudes.toDouble()
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("error", e.message.toString())
                }
                return
            }
        }
    }

    private fun dialogRequestPermission() {
        val dialog = Dialog(context)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_runtime_permission)

        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSetting = dialog.findViewById(R.id.tvSetting) as TextView

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvSetting.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 456)
        }
        dialog.show()
    }

    private fun dialogAppShare() {
        val dialog = Dialog(context)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_app_share)

        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_SUBJECT, "Liberty & Passage")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Congratulations, you have been invited to join Liberty & Passage Universe, the Global Outbreak Management Solution. Please install the app from any of the following stores : "+
                        "\nAndroid :"+
                        "\nhttps://play.google.com/store/apps/details?id=libertypassage.com.corporate"+
                        "\niOS :"+
                        "\nhttps://apps.apple.com/in/app/liberty-passage-corporate/id1546042654"
            )
            intent.type = "text/plain"
            startActivity(intent)
        }
        dialog.show()
    }

    private fun appExit() {
        finish()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (dialogProgress != null && dialogProgress!!.isShowing) dialogProgress!!.dismiss()
        finish()
    }
}
