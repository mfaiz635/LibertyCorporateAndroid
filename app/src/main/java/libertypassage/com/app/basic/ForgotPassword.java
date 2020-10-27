package libertypassage.com.app.basic;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import libertypassage.com.app.R;
import libertypassage.com.app.adapter.CountryCodeAdapter;
import libertypassage.com.app.adapter.CountryCodeSpinnerAdapter;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.CountryDto;
import libertypassage.com.app.models.DetailsResetPass;
import libertypassage.com.app.models.ModelReset;
import libertypassage.com.app.models.login.UserDetails;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.RecyclerItemClickListener;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private CenturyGothicEditText et_mobileNo;
    private CenturyGothicTextview submit_btn, tv_login, tv_countryCode;
    private String mobile, error_msg = "Required mobile number";
    private Context context;
    private final String TAG = ForgotPassword.class.getSimpleName();
    Spinner spnCountryCode;
    String country_Name, flag = "x", country;
    ArrayList<String> Country_names_array = new ArrayList<String>();
    ArrayList<CountryDto> countries = new ArrayList<>();
    ArrayList<CountryDto> filterdNames = new ArrayList<>();
    CountryCodeSpinnerAdapter adapter;
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        context = ForgotPassword.this;
        findID();
    }

    private void findID() {
        spnCountryCode = findViewById(R.id.spnCountryCode);
        tv_countryCode = findViewById(R.id.tv_countryCode);
        et_mobileNo = findViewById(R.id.et_mobileNo);
        submit_btn = findViewById(R.id.submit_btn);
        tv_login = findViewById(R.id.tv_login);

        tv_countryCode.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        String jsonFileString = Utility.getJsonFromAssets(getApplicationContext(), "countrylist.json");
        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<CountryDetail>>() { }.getType();

        List<CountryDetail> countryDetails = gson.fromJson(jsonFileString, listUserType);
        for (int i = 0; i < countryDetails.size(); i++) {
            countries.add(new CountryDto(countryDetails.get(i).getCountryName(), countryDetails.get(i).getCallingcodes(), countryDetails.get(i).getId()));
            Country_names_array.add(countryDetails.get(i).getCountryName().toUpperCase());
        }
        adapter = new CountryCodeSpinnerAdapter(context, R.layout.spinner_text, Country_names_array);
        spnCountryCode.setAdapter(adapter);
        spnCountryCode.setOnItemSelectedListener(ForgotPassword.this);
        setCountryCode();

        et_mobileNo.setFocusable(false);
        et_mobileNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_mobileNo.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_countryCode: {
                selectCountry();
                break;
            }
            case R.id.submit_btn: {
                if (!TextUtils.isEmpty(et_mobileNo.getText().toString())) {
                    if (et_mobileNo.getText().toString().length() > 7) {
                        if (Utility.isConnectingToInternet(context)) {
                            country = tv_countryCode.getText().toString();
                            String mobile = country + et_mobileNo.getText().toString();
                            getForgotPassword(mobile);
                        } else {
                            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        et_mobileNo.setError("Invalid mobile number");
                    }
                } else {
                    et_mobileNo.setError(error_msg);
                }
                break;
            }
            case R.id.tv_login: {
                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
            break;


        }


    }

    private void getForgotPassword(final String mobile) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelReset> call = apiInterface.forgotPassword(Constants.KEY_BOT, mobile);

        call.enqueue(new Callback<ModelReset>() {
            @Override
            public void onResponse(Call<ModelReset> call, Response<ModelReset> response) {
                Utility.stopProgressDialog(context);
                ModelReset model = response.body();
                Log.e("modelForgot", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {

                    DetailsResetPass detailsResetPass = model.getDetails();
                    UserDetails userDetails = detailsResetPass.getDetails();

                    Utility.setSharedPreference(context, "t_bearer", model.getBearer_token());
                    Utility.setSharedPreference(context, "t_email", userDetails.getEmailId());
                    Utility.setSharedPreference(context, "t_mobile", mobile);
                    Utility.setSharedPreference(context, "otp", String.valueOf(model.getOtp()));
                    Utility.setSharedPreference(context, "isVerify", "forgot");

                    Intent intent = new Intent(context, VerifyOTPForgotPassword.class);
                    intent.putExtra("from", "forgot");
                    startActivity(intent);
                    finish();
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelReset> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCountryCode() {
        Log.e("called---", "yesss");
        country_Name = Utility.getSharedPreferences(context, "country_Name");
        if (country_Name.equals("")) {
//            Toast.makeText(this, "Unable to pick country.", Toast.LENGTH_LONG).show();
        } else {
            flag = "1";
            int i = Country_names_array.indexOf(country_Name);
            Log.e("name-Size", String.valueOf(Country_names_array.size()));
            int code = countries.get(i).getCountryCode();
            spnCountryCode.setSelection(i);
            Log.e("CODE--", code + "");
            Log.e("COUNTRY--", spnCountryCode.getSelectedItem().toString());
            tv_countryCode.setText("+" + code);
            Utility.setSharedPreference(context, "country_code", String.valueOf(code));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e("FLAG-", flag);
        if (flag.equals("1")) {
            Log.e("1 FLAG-", flag);
            int k = Country_names_array.indexOf(country_Name);
            int code = countries.get(k).getCountryCode();
            spnCountryCode.setSelection(k);
            tv_countryCode.setText(code);

            flag = "x";
        } else if (flag.equals("x")) {
            Log.e("X FLAG-", flag);
            String country = spnCountryCode.getSelectedItem().toString();
            int position = Country_names_array.indexOf(country);
            int code = countries.get(position).getCountryCode();
            spnCountryCode.setSelection(position);

            tv_countryCode.setText(code);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void selectCountry() {
        dialog = new Dialog(ForgotPassword.this);
        dialog.setContentView(R.layout.country_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        EditText edtCountry = dialog.findViewById(R.id.edtCountry);
        ImageView cancel = dialog.findViewById(R.id.cancel);
        RecyclerView rvCountry = dialog.findViewById(R.id.rvCountry);
        final CountryCodeAdapter countryAdapter;
        LinearLayoutManager linearLayoutManager;

        linearLayoutManager = new LinearLayoutManager(ForgotPassword.this);
        countryAdapter = new CountryCodeAdapter(ForgotPassword.this, countries);
        rvCountry.setLayoutManager(linearLayoutManager);
        rvCountry.setAdapter(countryAdapter);
        rvCountry.addOnItemTouchListener(new RecyclerItemClickListener(ForgotPassword.this, rvCountry, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //               hideSoftKeyboard();
                CountryDto country;
                if (filterdNames.size() > 0) {
                    country = filterdNames.get(position);
                } else {
                    country = countries.get(position);
                }
                Log.e("Country code--", country.getCountryCode() + "");
                Log.e("Country name--", country.getCountryName());
                Utility.getSharedPreferences(context, "country_code");
                tv_countryCode.setText("+" + country.getCountryCode());
                dialog.dismiss();
            }

            @Override
            public void onItemDoubleClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        edtCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), countryAdapter);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void filter(String text, CountryCodeAdapter codeAdapter) {
        filterdNames = new ArrayList<>();
        for (CountryDto s : countries) {
            //if the existing elements contains the search input
            if (s.getCountryName().toUpperCase().startsWith(text.toUpperCase())) {
                //adding the element to filtered list
                filterdNames.add(s);

                Log.e("country with code--", s.getCountryCode() + "");
                Log.e("country name --", s.getCountryName());
            }
        }

        //calling a method of the adapter class and passing the filtered list
        codeAdapter.filterList(filterdNames);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
