package libertypassage.com.corporate.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.*
import java.io.IOException
import java.util.*
import java.util.regex.Pattern


object Utility {
    private const val PREFERENCE = "LIBERTY"
    private const val KEY_COUNTRY_LIST = "country_list"
    private const val KEY_INDUSTRY_LIST = "industry_list"
    private const val KEY_INDUSTRY_PROF = "industry_profession"
    private const val KEY_HOTSPOTS_LIST = "hotspots_list"
    private const val KEY_SAFE_ENTRY = "safe_entry"
    private const val KEY_VACCINE_LIST = "vaccine_list"
    private var progressDialog: ProgressDialog? = null


    fun setSharedPreference(context: Context, name: String?, value: String?) {
        val settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun getSharedPreferences(context: Context, name: String?): String? {
        val settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        return settings.getString(name, "")
    }

    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun clearPreference(context: Context) {
        val settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
    }

    fun showProgressDialog(context: Context?) {
        progressDialog = ProgressDialog(context, R.style.MyTheme)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar_Small)
        progressDialog!!.show()
    }

    fun stopProgressDialog(context: Context?) {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    fun hasPermissionInManifest(
            activity: Activity?,
            requestCode: Int,
            permissionName: Array<String?>
    ): Boolean {
        for (s in permissionName) {
            if (ContextCompat.checkSelfPermission(
                            activity!!,
                            s!!
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(activity, permissionName, requestCode)
                return false
            }
        }
        return true
    }

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
            return true
        }
        return false
    }

    fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.e("servideRuning", "true")
                return true
            }
        }
        return false
    }

    fun saveIndustry(context: Context, list: List<DetailIndustry?>?) {
        val str = Gson().toJson(list)
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_INDUSTRY_LIST, str)
        editor.apply()
    }

    fun getIndustry(context: Context): ArrayList<DetailIndustry> {
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val str = sp.getString(KEY_INDUSTRY_LIST, null)
        return try {
            val arr = Gson().fromJson(str, Array<DetailIndustry>::class.java)
            return ArrayList<DetailIndustry>(Arrays.asList<DetailIndustry>(*arr))
        } catch (e: java.lang.Exception) {
            ArrayList()
        }
    }

    fun saveVaccine(context: Context, list: List<DetailVaccines?>?) {
        val str = Gson().toJson(list)
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_VACCINE_LIST, str)
        editor.apply()
    }

    fun getVaccine(context: Context): ArrayList<DetailVaccines> {
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val str = sp.getString(KEY_VACCINE_LIST, null)
        return try {
            val arr = Gson().fromJson(str, Array<DetailVaccines>::class.java)
            return ArrayList<DetailVaccines>(Arrays.asList<DetailVaccines>(*arr))
        } catch (e: java.lang.Exception) {
            ArrayList()
        }
    }


    fun saveIndustryProfession(context: Context, list: List<DetailIndustryProf?>?) {
        val str = Gson().toJson(list)
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_INDUSTRY_PROF, str)
        editor.apply()
    }

    fun getIndustryProfession(context: Context): ArrayList<DetailIndustryProf> {
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val str = sp.getString(KEY_INDUSTRY_PROF, null)
        return try {
            val arr = Gson().fromJson(str, Array<DetailIndustryProf>::class.java)
            return ArrayList<DetailIndustryProf>(Arrays.asList<DetailIndustryProf>(*arr))
        } catch (e: java.lang.Exception) {
            ArrayList()
        }
    }


    fun saveHotspots(context: Context, list: List<DetailHotSpots?>?) {
        val str = Gson().toJson(list)
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_HOTSPOTS_LIST, str)
        editor.apply()
    }

    fun getHotspots(context: Context): ArrayList<DetailHotSpots> {
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val str = sp.getString(KEY_HOTSPOTS_LIST, null)
        return try {
            val arr = Gson().fromJson(str, Array<DetailHotSpots>::class.java)
            return ArrayList<DetailHotSpots>(Arrays.asList<DetailHotSpots>(*arr))
        } catch (e: java.lang.Exception) {
            ArrayList()
        }
    }

    fun saveSafeEntry(context: Context, list: List<ModelSafeEntry?>?) {
        val str = Gson().toJson(list)
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_SAFE_ENTRY, str)
        editor.apply()
    }

    fun getSafeEntry(context: Context): ArrayList<ModelSafeEntry> {
        val sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val str = sp.getString(KEY_SAFE_ENTRY, null)
        return try {
            val arr = Gson().fromJson(str, Array<ModelSafeEntry>::class.java)
            return ArrayList<ModelSafeEntry>(Arrays.asList<ModelSafeEntry>(*arr))
        } catch (e: java.lang.Exception) {
            ArrayList()
        }
    }

    fun getJsonFromAssets(context: Context, fileName: String?): String? {
        val jsonString: String
        jsonString = try {
            val `is` = context.assets.open(fileName!!)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(context: Context?): ModelMyLocation? {
        val modelMyLocation =
            ModelMyLocation()
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                context!!
        )
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            val latitudes: Double
            val longitudes: Double
            val altitudes: Double
            if (location != null) {
                latitudes = location.latitude
                longitudes = location.longitude
                altitudes = location.altitude
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitudes, longitudes, 1)
                    val address =
                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val city = addresses[0].locality
                    val state = addresses[0].adminArea
                    val country = addresses[0].countryName
                    val postalCode = addresses[0].postalCode
                    val knownName = addresses[0].featureName // Only if available else return NULL
                    modelMyLocation.latitudes = latitudes
                    modelMyLocation.longitudes = longitudes
                    modelMyLocation.altitudes = altitudes
                    modelMyLocation.address = address
                    modelMyLocation.city = city
                    modelMyLocation.state = state
                    modelMyLocation.country = country
                    modelMyLocation.postalCode = postalCode
                    modelMyLocation.knownName = knownName
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
                    setSharedPreference(
                            context,
                            Constants.KEY_MY_LOCATION,
                            address
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return modelMyLocation
    }


}