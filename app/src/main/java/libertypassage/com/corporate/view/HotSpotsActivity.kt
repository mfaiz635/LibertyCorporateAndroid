package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.hotspots.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.DetailHotSpots
import libertypassage.com.corporate.model.ModelHotSpots
import libertypassage.com.corporate.model.MyItem
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.services.GPSTrackerLocation
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class HotSpotsActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {
    private var TAG = HotSpotsActivity::class.java.simpleName
    private lateinit var context: Context
    private var dialogProgress: DialogProgress? = null
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var detailHotsSpots: MutableList<DetailHotSpots> = ArrayList()
    private var token = ""
    private var lats = ""
    private var longs = ""
    private var address = "Address"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hotspots)
        context = this@HotSpotsActivity

        dialogProgress = DialogProgress(context)
        mapFragment = supportFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        lats = Utility.getSharedPreferences(context, Constants.KEY_LAT)!!
        longs = Utility.getSharedPreferences(context, Constants.KEY_LONG)!!

        iv_back.setOnClickListener(this)
        gpsLocation()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun gpsLocation() {
        val gps = GPSTrackerLocation(context)
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPSSettingsRequest(this@HotSpotsActivity)
        } else {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()
            lats = latitude.toString()
            longs = longitude.toString()
        }

        if (Utility.isConnectingToInternet(context)) {
            hotsSpotsApiResponse()
        } else {
            Toast.makeText(
                context,
                "Please connect to internet and try again",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        if (lats != "" && lats.isNotEmpty()) {
            val myLatLong = LatLng(lats.toDouble(), longs.toDouble())
            mMap!!.addMarker(MarkerOptions().position(myLatLong).title(address))
            mMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lats.toDouble(),
                        longs.toDouble()
                    ), 15f
                )
            )
        }
    }

    private fun hotsSpotsApiResponse() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelHotSpots> = apiInterface.getLocationHistory(
            Constants.KEY_HEADER+token, Constants.KEY_BOT)
        call.enqueue(object : Callback<ModelHotSpots?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<ModelHotSpots?>,
                response: Response<ModelHotSpots?>
            ) {
                dialogProgress!!.dismiss()
                val responses: ModelHotSpots? = response.body()
                Log.e("HotSpots", Gson().toJson(responses))

                if (responses != null && responses.error?.equals(false)!!) {
                    detailHotsSpots.clear()
                    detailHotsSpots = responses.details as MutableList<DetailHotSpots>

                    for (i in detailHotsSpots.indices) {
                        detailHotsSpots[i].latitude?.toDouble()!!
                        detailHotsSpots[i].longitude?.toDouble()!!
                        detailHotsSpots[i].address
                        detailHotsSpots[i].infections.toString()

                        val circle: Circle? = mMap?.addCircle(
                            CircleOptions()
                                .center(LatLng(detailHotsSpots[i].latitude!!.toDouble(), detailHotsSpots[i].longitude!!.toDouble()))
                                .radius(400.0)
                                .strokeColor(Color.RED)
                                .fillColor(Color.argb(60, 60, 60, 50)))
                    }

                } else if (responses != null && responses.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelHotSpots?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }



    private fun enableGPSSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2.toLong()
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
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
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> //                        Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        status.startResolutionForResult(this@HotSpotsActivity, 100)
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
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        when (requestCode) {
            100 -> when (resultCode) {
                Activity.RESULT_OK -> gpsLocation()
                Activity.RESULT_CANCELED -> Log.e("Cancel", "Not enabled GPS")
                else -> {
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
