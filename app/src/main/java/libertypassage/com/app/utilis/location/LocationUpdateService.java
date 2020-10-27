package libertypassage.com.app.utilis.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

import libertypassage.com.app.utilis.Utility;


public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context mContext;
    Location mLocation;
    GoogleApiClient mGoogleApiClient;

    public double lattitude = 0.0, longitude = 0.0;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60 * 1000;
    private long FASTEST_INTERVAL = 30 * 1000;

    public static String LOCATION_UPDATE_BROADCAST = "libertypassage.com.app";

    private static final String TAG = LocationUpdateService.class.getSimpleName();

    @Override
    public void onCreate() {
        showLog("onCreate");

        mContext = LocationUpdateService.this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        showLog("onStart");

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        showLog("onConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            try {
                lattitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
            } catch (Exception ex) {
                showLog("Exception = " + ex.getMessage());
            }

            if (lattitude != 0.0 && longitude != 0.0) {
                //Here set latitude longitude in prefreance when get new lattitude longitude
                Utility.setSharedPreference(mContext, "longitude", String.valueOf(longitude));
                Utility.setSharedPreference(mContext, "lattitude", String.valueOf(lattitude));

                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String country_name = addresses.get(0).getCountryName();

                        Utility.setSharedPreference(mContext, "country_name", country_name);
                        // Session.setCountry_name(mContext, country_name);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LocationUpdateService.LOCATION_UPDATE_BROADCAST);
                intent.setPackage(mContext.getPackageName());
                mContext.sendBroadcast(intent);
            }
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        showLog("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showLog("onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
//        showLog("onLocationChanged");

        if (location != null) {
            try {
                lattitude = location.getLatitude();
                longitude = location.getLongitude();
            } catch (Exception ex) {
                showLog("Exception = " + ex.getMessage());
            }

            if (lattitude != 0.0 && longitude != 0.0) {
                //Here again set location in prefreance if latitude longitude changes
                Utility.setSharedPreference(mContext, "longitude", String.valueOf(longitude));
                Utility.setSharedPreference(mContext, "lattitude", String.valueOf(lattitude));

                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String country_name = addresses.get(0).getCountryName();

                        Utility.setSharedPreference(mContext, "country_name", country_name);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LocationUpdateService.LOCATION_UPDATE_BROADCAST);
                intent.setPackage(mContext.getPackageName());
                mContext.sendBroadcast(intent);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        showLog("Service Stop");
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void showLog(String text) {
        Log.e(TAG, text);
    }
}
