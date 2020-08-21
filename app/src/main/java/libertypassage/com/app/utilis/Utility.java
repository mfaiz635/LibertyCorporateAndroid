package libertypassage.com.app.utilis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import libertypassage.com.app.R;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.DetailLocationHistory;
import libertypassage.com.app.models.ModelMyLocation;


public class Utility {
    private static String PREFERENCE = "LIBERTY";
    private static String KEY_COUNTRY_LIST = "country_list";
    private static String KEY_INDUSTRY_PROF = "industry_profession";
    private static String KEY_LOCATION_HISTORY = "location_history";
    private static ProgressDialog progressDialog;

    public static void setSharedPreference(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static String getSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(name, "");
    }

    public static void clearPreference(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public static void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
    }

    public static void stopProgressDialog(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String[] permissionName) {
        for (String s : permissionName) {
            if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(activity, permissionName, requestCode);
                return false;
            }
        }
        return true;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e("servideRuning", "true");
                return true;
            }
        }
        return false;
    }


    public static void saveCountryList(Context context, List<CountryDetail> list) {
        String str = new Gson().toJson(list);
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_COUNTRY_LIST, str);
        editor.apply();
    }

    public static ArrayList<CountryDetail> getCountryList(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String str = sp.getString(KEY_COUNTRY_LIST, null);
        try {
            CountryDetail[] arr = new Gson().fromJson(str, CountryDetail[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveIndustryProfession(Context context, List<DetailIndustryProf> list) {
        String str = new Gson().toJson(list);
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_INDUSTRY_PROF, str);
        editor.apply();
    }

    public static ArrayList<DetailIndustryProf> getIndustryProfession(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String str = sp.getString(KEY_INDUSTRY_PROF, null);
        try {
            DetailIndustryProf[] arr = new Gson().fromJson(str, DetailIndustryProf[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveLocationHistory(Context context, List<DetailLocationHistory> list) {
        String str = new Gson().toJson(list);
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_LOCATION_HISTORY, str);
        editor.apply();
    }

    public static ArrayList<DetailLocationHistory> getLocationHistory(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String str = sp.getString(KEY_LOCATION_HISTORY, null);
        try {
            DetailLocationHistory[] arr = new Gson().fromJson(str, DetailLocationHistory[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressLint("MissingPermission")
    public static ModelMyLocation getMyLocation(Context context) {
        ModelMyLocation modelMyLocation = new ModelMyLocation();
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        double latitudes, longitudes, altitudes;
                        if (location != null) {
                             latitudes = location.getLatitude();
                             longitudes = location.getLongitude();
                             altitudes = location.getAltitude();

                            try {
                                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitudes, longitudes, 1);

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                modelMyLocation.setLatitudes(location.getLatitude());
                                modelMyLocation.setLongitudes(location.getLongitude());
                                modelMyLocation.setAltitudes(location.getAltitude());
                                modelMyLocation.setAddress(address);
                                modelMyLocation.setCity(city);
                                modelMyLocation.setState(state);
                                modelMyLocation.setCountry(country);
                                modelMyLocation.setPostalCode(postalCode);
                                modelMyLocation.setKnownName(knownName);

                                Utility.setSharedPreference(context, Constants.KEY_LAT, String.valueOf(latitudes));
                                Utility.setSharedPreference(context, Constants.KEY_LONG, String.valueOf(longitudes));
                                Utility.setSharedPreference(context, Constants.KEY_MY_LOCATION, address);
//                                Log.e("modelMyLocationUTILITY", new Gson().toJson(modelMyLocation));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        return modelMyLocation;
    }

}
