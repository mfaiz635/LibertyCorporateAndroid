package libertypassage.com.app.basic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.models.user.ModelUser;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Acknowledgement extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private final String TAG = Acknowledgement.class.getSimpleName();
    private CenturyGothicTextview tv_next, tv_latLong;
    private ImageView iv_back;
    private String token, address, city, state, country, postalCode, knownName, location = "1",
            macAddress="", wifiIpAddress="";
    private SwitchCompat location_switch, blutooth_switch;
    //  get current location by fused location api
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    double latitudes, longitudes, altitudes;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acknowledgement);

        context = Acknowledgement.this;
        findID();
//        init();
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
    }


    @SuppressLint("MissingPermission")
    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        tv_next = findViewById(R.id.tv_next);
        location_switch = findViewById(R.id.location_switch);
        blutooth_switch = findViewById(R.id.blutooth_switch);
        tv_latLong = findViewById(R.id.tv_latLong);

        iv_back.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        tv_latLong.setVisibility(View.GONE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    location = "1";
                } else {
                    dialogUpdate();
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(intent);
//                    location_switch.setChecked(true);
                }
            }
        });
    }
    
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.tv_next: {
                if (location.equals("0")) {
                    dialogUpdate();
                } else {
                    getLastLocation();
                }
                break;
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    tv_latLong.setText("LAT : " + location.getLatitude() + ", LONG : " + location.getLongitude() + "");
                                    latitudes = location.getLatitude();
                                    longitudes = location.getLongitude();
                                    altitudes = location.getAltitude();
                                    getAddress();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            tv_latLong.setText("LAT : " + mLastLocation.getLatitude() + ", LONG : " + mLastLocation.getLongitude() + "");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    private void getAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitudes, longitudes, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            if (Utility.isConnectingToInternet(context)) {
                addTrackUserLocation();
            } else {
                Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
            }

            Log.e("address", address + ", " + city + "," + state + "," + country + postalCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void addTrackUserLocation() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelUser> call = apiInterface.addTrackUserLocation(Constants.KEY_HEADER + token,
                Constants.KEY_BOT, latitudes, longitudes, altitudes, macAddress, wifiIpAddress);

        call.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                Utility.stopProgressDialog(context);
                ModelUser model = response.body();

                if (model != null && model.getError().equals(false)) {

                    Utility.setSharedPreference(context, Constants.KEY_START, "3");
                    Intent intent = new Intent(context, MyTemperature.class);
                    startActivity(intent);

                    
                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dialogUpdate() {
        Dialog dialog;
        dialog = new Dialog(Acknowledgement.this);
        dialog.setContentView(R.layout.dialog_location_required);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView cancel = dialog.findViewById(R.id.cancel);
        CenturyGothicTextview tv_notAllow = dialog.findViewById(R.id.tv_notAllow);
        CenturyGothicTextview tv_ok = dialog.findViewById(R.id.tv_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = "0";
                dialog.dismiss();
            }
        });
        tv_notAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location_switch.setChecked(false);
                location = "0";
                dialog.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location_switch.setChecked(true);
                location = "1";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}




