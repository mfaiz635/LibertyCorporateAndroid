package libertypassage.com.app.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import libertypassage.com.app.R;
import libertypassage.com.app.models.DetailLocationHistory;
import libertypassage.com.app.models.ModelLocationHistory;
import libertypassage.com.app.models.MyItem;
import libertypassage.com.app.other.MarkerClusterRenderer;
import libertypassage.com.app.services.GPSTrackerLocation;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationHistory extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private String TAG = LocationHistory.class.getSimpleName();
    private Context context;
    private ImageView iv_back;
    private String token, s_lats="", s_longs="", s_location_address="";
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private List<DetailLocationHistory> detailLocationHistories = new ArrayList<DetailLocationHistory>();
    private ClusterManager<MyItem> mClusterManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_history);

        context = LocationHistory.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        findID();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        iv_back.setOnClickListener(this);

        s_lats = Utility.getSharedPreferences(context, Constants.KEY_LAT);
        s_longs = Utility.getSharedPreferences(context, Constants.KEY_LONG);
        GPSLocation();
    }

    public void GPSLocation() {
        GPSTrackerLocation gps = new GPSTrackerLocation(context);
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
            enableGPSSettingsRequest(LocationHistory.this);
        }else{
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            s_lats = String.valueOf(latitude);
            s_longs = String.valueOf(longitude);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng indore = new LatLng(22.7192635, 75.8982338);
//        LatLng singapore = new LatLng(1.304833, 103.831833);
//        mMap.addMarker(new MarkerOptions().position(indore).title("Address"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.7192635, 75.8982338), 15));

        if(!s_lats.equals("") && !s_lats.isEmpty()) {
            LatLng myLatLong = new LatLng(Double.parseDouble(s_lats), Double.parseDouble(s_longs));
            mMap.addMarker(new MarkerOptions().position(myLatLong).title(s_location_address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(s_lats), Double.parseDouble(s_longs)), 15));
        }else{
//            Toast.makeText(context, "Required enable your location", Toast.LENGTH_LONG).show();
        }

         setUpClusterer();
    }


    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
//        float zoom = mMap.getCameraPosition().zoom;
//        Toast.makeText(context, mMap.getCameraPosition().zoom+"", Toast.LENGTH_LONG).show();

        mClusterManager.setAnimation(true);
        mClusterManager.setRenderer(new MarkerClusterRenderer(LocationHistory.this, mMap, mClusterManager));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            private float currentZoom = -1;
            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom != currentZoom){
                    currentZoom = pos.zoom;
                    // do you action here
                    mClusterManager.setRenderer(new MarkerClusterRenderer(LocationHistory.this, mMap, mClusterManager));

                }
            }
        });
    }

    private void addItems() {
//        mClusterManager.addItem(new MyItem(1.463780, 103.801811, "Senoko Road, Singapore", ""));
//        mClusterManager.addItem(new MyItem(1.307222, 103.819725, "Gleneagles Medical Center, Singapore", ""));
//        mClusterManager.addItem(new MyItem(1.294444, 103.846947, "Fort Canning Hill, Singapore", ""));
//        mClusterManager.addItem(new MyItem(1.446392, 103.780655, "", ""));
//        mClusterManager.addItem(new MyItem(1.3061, 103.8286, "", ""));
//        mClusterManager.addItem(new MyItem(1.273806, 103.817497, "", ""));
//        mClusterManager.addItem(new MyItem(1.282375, 103.864273, "", ""));
//        mClusterManager.addItem(new MyItem(1.369115, 103.845436, "", ""));
//        mClusterManager.addItem(new MyItem(1.371778, 103.893059, "", ""));

        detailLocationHistories.clear();
        detailLocationHistories.addAll(Utility.getLocationHistory(context));
        if(detailLocationHistories != null && detailLocationHistories.size()!=0){
            for (int i = 0; i < detailLocationHistories.size(); i++) {
                mClusterManager.addItem(new MyItem(Double.parseDouble(detailLocationHistories.get(i).getLatitude()),
                        Double.parseDouble(detailLocationHistories.get(i).getLongitude()),
                        detailLocationHistories.get(i).getAddress(), String.valueOf(detailLocationHistories.get(i).getInfections())));
            }
        }else {
            if (Utility.isConnectingToInternet(context)) {
                getLocationHistory();
            } else {
                Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void getLocationHistory() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelLocationHistory> call = apiInterface.getLocationHistory(Constants.KEY_HEADER+token, Constants.KEY_BOT);

        call.enqueue(new Callback<ModelLocationHistory>() {
            @Override
            public void onResponse(Call<ModelLocationHistory> call, Response<ModelLocationHistory> response) {
                Utility.stopProgressDialog(context);
                ModelLocationHistory model = response.body();
                Log.e("LocationHistory", new Gson().toJson(model));

                if(model != null && model.getError().equals(false)) {

                    detailLocationHistories = model.getDetails();
                    Log.e("LocationHistorySize", detailLocationHistories.size()+"");

                    for (int i = 0; i < detailLocationHistories.size(); i++) {
                        mClusterManager.addItem(new MyItem(Double.parseDouble(detailLocationHistories.get(i).getLatitude()),
                                Double.parseDouble(detailLocationHistories.get(i).getLongitude()),
                                detailLocationHistories.get(i).getAddress(), String.valueOf(detailLocationHistories.get(i).getInfections())));
                    }

                    if (detailLocationHistories != null) {
                        Utility.saveLocationHistory(context, detailLocationHistories);
                    }

                }else if(model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelLocationHistory> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableGPSSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

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
//                        Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            status.startResolutionForResult(LocationHistory.this, 100);
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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case 100:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        GPSLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("Cancel", "Not enabled GPS");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

