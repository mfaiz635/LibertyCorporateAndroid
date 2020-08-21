package libertypassage.com.app.basic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationHistory extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private String TAG = LocationHistory.class.getSimpleName();
    private Context context;
    private ImageView iv_back;
    private String token, from, s_lats="", s_longs="", s_location_address="";
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
        from = getIntent().getStringExtra("from");

        Utility.getMyLocation(context);
        s_lats = Utility.getSharedPreferences(context, Constants.KEY_LAT);
        s_longs = Utility.getSharedPreferences(context, Constants.KEY_LONG);
        s_location_address = Utility.getSharedPreferences(context, Constants.KEY_MY_LOCATION);
        Log.e("s_lats", s_lats + s_longs);

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
            Toast.makeText(context, "Required enable your location", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

//   https://developers.google.com/maps/documentation/android-sdk/shapes#polygons
//    https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering