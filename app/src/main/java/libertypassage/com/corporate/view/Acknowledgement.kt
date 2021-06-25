package libertypassage.com.corporate.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.acknowledgement.*
import kotlinx.android.synthetic.main.activity_header.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelTrackUserLocation
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.services.GPSTrackerLocation
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Acknowledgement : AppCompatActivity(), View.OnClickListener {
    private var TAG = Acknowledgement::class.java.simpleName
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    private var PERMISSION_ID = 44
    private var latitudes = 0.0
    private var longitudes = 0.0
    private var altitudes = 0.0
    private var token = ""
    private var wifiMacAddress = ""
    private var wifiIpAddress = ""
    private var wifiMode = ""
    private var corporationId = ""
    private var location = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    val permissions = arrayOf<String?>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acknowledgement)
        context = this@Acknowledgement

        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        corporationId = Utility.getSharedPreferences(context, Constants.KEY_CORPORATION_ID)!!
        dialogProgress = DialogProgress(context)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        iv_back.setOnClickListener(this)
        tv_next.setOnClickListener(this)

        location_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                location = "1"
            } else {
                dialogUpdate()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tv_next -> {
                if (location == "0") {
                    dialogUpdate()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissions, 456)
                    } else {
                        if (isLocationEnabled()) {
                            Utility.getMyLocation(context)
                        } else {
                            dialogAlwaysLocationOn()
                        }
                        getGPSLocation()
                    }
                }
            }
        }
    }

    private fun getGPSLocation() {
        val gps = GPSTrackerLocation(context)
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPSSettingsRequest(this@Acknowledgement)
        } else {
            latitudes = gps.getLatitude()
            longitudes = gps.getLongitude()
            altitudes = gps.getAltitude()

            Utility.setSharedPreference(context, Constants.KEY_LAT, latitudes.toString())
            Utility.setSharedPreference(context, Constants.KEY_LONG, longitudes.toString())
            Utility.setSharedPreference(context, Constants.KEY_ALT, altitudes.toString())
            getWifiDetails()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitudes = location.latitude
                        longitudes = location.longitude
                        altitudes = location.altitude
                        getWifiDetails()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
//            val mLastLocation = locationResult.lastLocation
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation()
//            }
//        }
        when (requestCode) {
        456 -> {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    Utility.getMyLocation(context)
                } else {
                    dialogAlwaysLocationOn()
                }
                getGPSLocation()
                // permission was granted, yay! do the
            } else {
                // permission denied, boo! Disable the
                dialogRequestPermission()
            }
            return
        }
        }
    }

    private fun dialogUpdate() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_location_required)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val ivCancel = dialog.findViewById(R.id.ivCancel) as ImageView
        val tvDontAllow = dialog.findViewById(R.id.tvDontAllow) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        ivCancel.setOnClickListener {
            location = "0"
            dialog.dismiss()
        }

        tvDontAllow.setOnClickListener {
            location_switch.isChecked = false
            location = "0"
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            location_switch.isChecked = true
            location = "1"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getWifiDetails() {
        //check network type
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

        if (Utility.isConnectingToInternet(context)) {
            if (latitudes != 0.0) {
                trackUserLocation()
            }
        } else {
            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun trackUserLocation() {
        Log.e("LocationUpdateServer", "Continue")
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelTrackUserLocation> = apiInterface.trackUserLocation(Constants.KEY_HEADER + token,
            Constants.KEY_BOT, latitudes, longitudes, altitudes, wifiMacAddress, "$wifiIpAddress-$wifiMode", "0")

        call.enqueue(object : Callback<ModelTrackUserLocation?> {
            override fun onResponse(call: Call<ModelTrackUserLocation?>, response: Response<ModelTrackUserLocation?>) {
                val model: ModelTrackUserLocation? = response.body()

                if (model != null && model.error?.equals(false)!!) {
                    Utility.setSharedPreference(context, Constants.KEY_START, "3")
                    val intent = Intent(context, MyTemperature::class.java)
                    startActivity(intent)

                } else if (model != null && model.error?.equals(true)!!) {
                    Log.e("ErrorResponse", model.message!!)
                }
            }

            override fun onFailure(call: Call<ModelTrackUserLocation?>, t: Throwable) {
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun enableGPSSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
            googleApiClient,
            builder.build()
        )
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.e(
                    TAG,
                    "All location settings are satisfied."
                )
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(this@Acknowledgement, ENABLED_GPS_REQUEST)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "PendingIntent unable to execute request.")
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                    TAG,
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    private val LOCATION_SETTINGS_REQUEST = 10
    private val ENABLED_GPS_REQUEST = 20
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_SETTINGS_REQUEST -> if (isLocationEnabled()) {
                getLastLocation()
            } else {
                getGPSLocation()
            }
            ENABLED_GPS_REQUEST -> when (resultCode) {
                RESULT_OK -> getGPSLocation()
                RESULT_CANCELED -> {
                    if (checkPermissions()) {
                        if (isLocationEnabled()) {
                            getLastLocation()
                        } else {
                            getGPSLocation()
                        }
                    } else {
                        requestPermissions()
                    }
                    Log.e("Cancel", "Not enabled GPS")
                }
                else -> {
                }
            }
            104 ->
                if (resultCode != Activity.RESULT_OK) {
                    if (isLocationEnabled()) {
                        Utility.getMyLocation(context)
                    } else {
                        dialogAlwaysLocationOn()
                    }
                    getGPSLocation()
                }
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
