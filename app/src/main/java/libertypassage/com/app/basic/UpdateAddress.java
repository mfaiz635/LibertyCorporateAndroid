package libertypassage.com.app.basic;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.other.QRCodeScanerActivity;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAddress extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = UpdateAddress.class.getSimpleName();
    private CenturyGothicTextview tv_next, tv_currentLocation;
    private CenturyGothicEditText et_address;
    private ImageView iv_back, iv_edit, iv_qrcode_scanerr;
    private String token, from, s_address = "", postalCode = "", city = "", tempType, currentTemp;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitudes, longitudes, altitudes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upadate_address);

        context = UpdateAddress.this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findID();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        iv_qrcode_scanerr = findViewById(R.id.iv_qrcode_scanerr);
        et_address = findViewById(R.id.et_address);
        tv_currentLocation = findViewById(R.id.tv_currentLocation);
        iv_edit = findViewById(R.id.iv_edit);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        iv_qrcode_scanerr.setOnClickListener(this);
        tv_currentLocation.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        tempType = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE);
        currentTemp = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP);


        from = getIntent().getStringExtra("from");
        et_address.setText(Utility.getSharedPreferences(context, Constants.KEY_CURRENT_LOC));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.iv_edit: {
                et_address.setEnabled(true);
                et_address.setSelection(et_address.length());
                break;
            }
            case R.id.tv_next: {
                s_address = et_address.getText().toString().trim();
                if (s_address.equals("")) {
                    Toast.makeText(context, "Please add address", Toast.LENGTH_LONG).show();
                } else if (Utility.isConnectingToInternet(context)) {

                    updateAddressAndTemp();

                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.tv_currentLocation: {
                dialogUpdate();
                break;
            }
            case R.id.iv_qrcode_scanerr: {
                Intent intent = new Intent(context, QRCodeScanerActivity.class);
                startActivityForResult(intent, 170);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 170) {
            if (resultCode == 160) {
                String contents = data.getStringExtra("ResultText");
                if (contents != null) {
                    et_address.setText(contents);
                    et_address.setSelection(et_address.length());
                    Log.e("contents", contents);
                }
            }

            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private void dialogUpdate() {
        Dialog dialog;
        dialog = new Dialog(UpdateAddress.this);
        dialog.setContentView(R.layout.dialog_get_location);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView cancel = dialog.findViewById(R.id.cancel);
        CenturyGothicTextview tv_notAllow = dialog.findViewById(R.id.tv_notAllow);
        CenturyGothicTextview tv_ok = dialog.findViewById(R.id.tv_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_notAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            latitudes = location.getLatitude();
                            longitudes = location.getLongitude();
                            altitudes = location.getAltitude();
                            Log.e("altitudes", altitudes+"");
                            getAddress();
                        }
                    }
                }
        );
    }

    private void getAddress() {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitudes, longitudes, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address11 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city11 = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();
//            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            et_address.setText(address11);
            et_address.setSelection(et_address.length());
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void addUserAddress() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addUserAddress(Constants.KEY_HEADER + token, Constants.KEY_BOT, s_address, postalCode, city);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("AddUserAdd", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, s_address);

                    Intent intent = getIntent();
                    intent.putExtra("address", s_address);
                    setResult(121, intent);
                    finish();


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAddressAndTemp() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addressAndTemp(Constants.KEY_HEADER + token,
                Constants.KEY_BOT, s_address, tempType, currentTemp);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();

                if (model != null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, s_address);


                    if (!from.equals("home")) {
                        Intent intent = getIntent();
                        intent.putExtra("currentTemp", currentTemp);
                        intent.putExtra("tempType", tempType);
                        setResult(111, intent);
                        finish();
                    } else {
                        Intent intent = new Intent(context, HomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
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
