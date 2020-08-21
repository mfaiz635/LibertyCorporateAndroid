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

public class AddAddress extends AppCompatActivity implements View.OnClickListener {

    private String TAG = AddAddress.class.getSimpleName();
    private CenturyGothicTextview tv_next, tv_currentLocation;
    private CenturyGothicEditText et_address;
    private ImageView iv_back, iv_edit, iv_qrcode_scanerr;
    private String token, address = "", postalCode = "", city = "";
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitudes, longitudes, altitudes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);

        context = AddAddress.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findID();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        iv_qrcode_scanerr = findViewById(R.id.iv_qrcode_scanerr);
        tv_currentLocation = findViewById(R.id.tv_currentLocation);
        et_address = findViewById(R.id.et_address);
        iv_edit = findViewById(R.id.iv_edit);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        iv_qrcode_scanerr.setOnClickListener(this);
        tv_currentLocation.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_next.setOnClickListener(this);
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
                address = et_address.getText().toString().trim();
                if (address.equals("")) {
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
                    address = contents;
                    et_address.setText(address);
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
        dialog = new Dialog(AddAddress.this);
        dialog.setContentView(R.layout.dialog_get_location);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

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
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location != null) {
                                    latitudes=location.getLatitude();
                                    longitudes=location.getLongitude();
                                    altitudes=location.getAltitude();
                                    getAddress();
                                }
                            }
                        }
                );
           }

    private void getAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitudes, longitudes, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address11 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city11 = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();
//            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            address = address11;
            et_address.setText(address);
            et_address.setSelection(et_address.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAddressAndTemp() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addressAndTemp(Constants.KEY_HEADER + token, Constants.KEY_BOT, address,
                Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE), Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP));

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();

                if (model != null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    Utility.setSharedPreference(context, Constants.KEY_START, "5");
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, address);
                    Intent intent = new Intent(context, ConfirmDetails.class);
                    startActivity(intent);


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

//    private void addUserAddress() {
//        Utility.showProgressDialog(context);
//        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
//        Call<ModelResponse> call = apiInterface.addUserAddress(Constants.KEY_HEADER + token, Constants.KEY_BOT, address, postalCode, city);
//
//        call.enqueue(new Callback<ModelResponse>() {
//            @Override
//            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
//                Utility.stopProgressDialog(context);
//                ModelResponse model = response.body();
//                Log.e("AddUserAdd", new Gson().toJson(model));
//                if (model != null && model.getError().equals(false)) {
//                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
//
//                    Utility.setSharedPreference(context, Constants.KEY_START, "5");
//                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, address);
//                    Intent intent = new Intent(context, ConfirmDetails.class);
//                    startActivity(intent);
//
//                } else if (model != null && model.getError().equals(true)) {
//                    Utility.stopProgressDialog(context);
//                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ModelResponse> call, Throwable t) {
//                Utility.stopProgressDialog(context);
////                Log.e("model", "onFailure    " + t.getMessage());
////                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

}
