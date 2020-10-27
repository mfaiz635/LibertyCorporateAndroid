package libertypassage.com.app.basic;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import libertypassage.com.app.R;
import libertypassage.com.app.adapter.CountryCodeAdapter;
import libertypassage.com.app.adapter.CountryCodeSpinnerAdapter;
import libertypassage.com.app.models.login.Address;
import libertypassage.com.app.models.login.Confirmed;
import libertypassage.com.app.models.login.LastTrack;
import libertypassage.com.app.models.login.ModelLogin;
import libertypassage.com.app.models.login.Temprature;
import libertypassage.com.app.models.login.UserDetails;
import libertypassage.com.app.models.login.UserStatus;
import libertypassage.com.app.services.AlarmReceiver;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.CountryDto;
import libertypassage.com.app.models.login.LogInDetails;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout rl_signup, rl_countryView;
    private CenturyGothicEditText et_mobileNo, et_password;
    private CenturyGothicTextview tv_countryCode, tv_countryCode1, login_btn, tv_forgot_pass;
    private ImageView pass_view, pass_hide, iv_country, iv_country_gray;
    private View view;
    private String firebaseToken;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Spinner spn_countryCode;
    String country_Name, flag = "x", country;
    ArrayList<String> Country_names_array = new ArrayList<String>();
    ArrayList<CountryDto> countries = new ArrayList<>();
    ArrayList<CountryDto> filterdNames = new ArrayList<>();
    CountryCodeSpinnerAdapter adapter;
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupParent(getWindow().getDecorView().findViewById(android.R.id.content));

        context = LoginActivity.this;
        findID();
        init();
    }

    private void findID() {
        iv_country = findViewById(R.id.iv_country);
        iv_country_gray = findViewById(R.id.iv_country_gray);
        view = findViewById(R.id.view);
        tv_countryCode1 = findViewById(R.id.tv_countryCode1);
        spn_countryCode = findViewById(R.id.spnCountryCode);
        tv_countryCode = findViewById(R.id.tv_countryCode);
        et_mobileNo = findViewById(R.id.et_mobileNo);
        et_password = findViewById(R.id.et_password_login);
        login_btn = findViewById(R.id.login_btn);
        pass_view = findViewById(R.id.pass_view);
        pass_hide = findViewById(R.id.pass_hide);
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        rl_signup = findViewById(R.id.rl_signup);
        rl_countryView = findViewById(R.id.rl_countryView);

        tv_countryCode.setOnClickListener(this);
        tv_countryCode1.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        pass_view.setOnClickListener(this);
        pass_hide.setOnClickListener(this);
        rl_signup.setOnClickListener(this);
        tv_forgot_pass.setOnClickListener(this);

        et_mobileNo.setFocusable(false);
        et_password.setFocusable(false);
        et_mobileNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_mobileNo.setFocusableInTouchMode(true);
                return false;
            }
        });
        et_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_password.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isNumeric(charSequence.toString())) {
                    iv_country.setVisibility(View.VISIBLE);
                    iv_country_gray.setVisibility(View.GONE);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_countryCode1.setTextColor(getResources().getColor(R.color.white));
                    tv_countryCode1.setEnabled(true);
                } else {
                    iv_country.setVisibility(View.GONE);
                    iv_country_gray.setVisibility(View.VISIBLE);
                    view.setBackgroundColor(getResources().getColor(R.color.gray_out));
                    tv_countryCode1.setTextColor(getResources().getColor(R.color.gray_out));
                    tv_countryCode1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    private void init() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        firebaseToken = task.getResult().getToken();
                        Log.e(TAG, "firebaseToken = " + firebaseToken);
                    }
                });

        String jsonFileString = Utility.getJsonFromAssets(getApplicationContext(), "countrylist.json");
        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<CountryDetail>>() { }.getType();

        List<CountryDetail> countryDetails = gson.fromJson(jsonFileString, listUserType);
        for (int i = 0; i < countryDetails.size(); i++) {
            countries.add(new CountryDto(countryDetails.get(i).getCountryName(), countryDetails.get(i).getCallingcodes(), countryDetails.get(i).getId()));
            Country_names_array.add(countryDetails.get(i).getCountryName().toUpperCase());
        }
        adapter = new CountryCodeSpinnerAdapter(context, R.layout.spinner_text, Country_names_array);
        spn_countryCode.setAdapter(adapter);
        spn_countryCode.setOnItemSelectedListener(LoginActivity.this);
        setCountryCode();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_countryCode1: {
                selectCountry();
                break;
            }

            case R.id.tv_countryCode: {
                selectCountry();
                break;
            }

            case R.id.login_btn: {
                String password = et_password.getText().toString();
                String mobile = et_mobileNo.getText().toString();
                country = tv_countryCode.getText().toString();

                if (!TextUtils.isEmpty(mobile)) {
                    if (isNumeric(mobile)) {
                        if (!tv_countryCode1.getText().toString().equals("Select Country")) {
                            if (mobile.length() > 7) {
                                if (!TextUtils.isEmpty(password)) {
                                    if (Utility.isConnectingToInternet(context)) {
                                        logInReponseApi(country+mobile, password, "number");
                                    } else {
                                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    et_password.setError("Required password");
                                }
                            } else {
                                et_mobileNo.setError("Invalid mobile number");
                            }
                        } else {
                            Toast.makeText(context, "Required country", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (mobile.matches(emailPattern)) {
                            if (!TextUtils.isEmpty(password)) {
                                if (Utility.isConnectingToInternet(context)) {
                                    logInReponseApi(mobile, password, "email");
                                } else {
                                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Required password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "Required valid email", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Required email/mobile number", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.rl_signup: {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            }

            case R.id.pass_view: {
                pass_hide.setVisibility(View.VISIBLE);
                pass_view.setVisibility(View.GONE);
                et_password.setTransformationMethod(null);
                et_password.setSelection(et_password.length());
                break;
            }

            case R.id.pass_hide: {
                pass_view.setVisibility(View.VISIBLE);
                pass_hide.setVisibility(View.GONE);
                et_password.setTransformationMethod(new PasswordTransformationMethod());
                et_password.setSelection(et_password.length());
                break;
            }

            case R.id.tv_forgot_pass: {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
                break;
            }
        }
    }

    private void logInReponseApi(String email, String password, String from) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelLogin> call = apiInterface.userLogIn(Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, firebaseToken, email, password);

        Log.e("Login_parameter", email + "/" + password + "/" + from);
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(Call<ModelLogin> call, Response<ModelLogin> response) {
                Utility.stopProgressDialog(context);
                ModelLogin model = response.body();
                Log.e("LoginResponse", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {
                    LogInDetails logInDetails = model.getDetails();
                    Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, logInDetails.getBearerToken());

                    UserDetails userDetails = logInDetails.getUserDetails();
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, userDetails.getPhoneNumber());
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, userDetails.getEmailId());
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, userDetails.getGender());
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, userDetails.getAgeGroup());
                    Utility.setSharedPreference(context, Constants.KEY_INDUSTRY, String.valueOf(userDetails.getIndustryId()));
                    Utility.setSharedPreference(context, Constants.KEY_PROF, String.valueOf(userDetails.getProfId()));
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL_VERIFIED, String.valueOf(userDetails.getEmailVerified()));

                    Address add = userDetails.getAddress();
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, add.getAddress());

                    Confirmed confirmed = userDetails.getConfirmed();
                    Utility.setSharedPreference(context, Constants.KEY_STATUS, String.valueOf(confirmed.getConfirmation()));
                    Utility.setSharedPreference(context, Constants.KEY_CHANGE_STATUS, confirmed.getUpdatedAt());
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_ADD, confirmed.getClinicAddress());
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_DATE, confirmed.getDate());

                    Temprature temprature = userDetails.getTemprature();
                    Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, temprature.getTemprature());
                    Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, temprature.getTempType());

                    UserStatus userStatus = userDetails.getUserStatus();
                    Utility.setSharedPreference(context, Constants.KEY_RISK_SCORE, String.valueOf(userDetails.getRiskScore()));
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_STATUS, String.valueOf(userStatus.getId()));
                    Utility.setSharedPreference(context, Constants.KEY_TITLE, userStatus.getTitle());
                    Utility.setSharedPreference(context, Constants.KEY_SUB_TITLE, userStatus.getSubTitle());
                    Utility.setSharedPreference(context, Constants.KEY_DESCRIPTION, userStatus.getDescription());
                    Utility.setSharedPreference(context, Constants.KEY_ALERT_DESCRIPTION, userStatus.getAlertDescription());

                    localNotification();

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelLogin> call, Throwable t) {
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
            spn_countryCode.setSelection(i);
            Log.e("CODE--", code + "");
            Log.e("COUNTRY--", spn_countryCode.getSelectedItem().toString());
            tv_countryCode.setText("+" + code);
            tv_countryCode1.setText(country_Name);
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
            spn_countryCode.setSelection(k);
            tv_countryCode1.setText(country_Name);
            tv_countryCode.setText(code);

            flag = "x";
        } else if (flag.equals("x")) {
            Log.e("X FLAG-", flag);
            String country = spn_countryCode.getSelectedItem().toString();
            int position = Country_names_array.indexOf(country);
            int code = countries.get(position).getCountryCode();
            spn_countryCode.setSelection(position);
            tv_countryCode1.setText(country);
            tv_countryCode.setText(code);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void selectCountry() {
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.country_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        EditText edtCountry = dialog.findViewById(R.id.edtCountry);
        ImageView cancel = dialog.findViewById(R.id.cancel);
        RecyclerView rvCountry = dialog.findViewById(R.id.rvCountry);
        final CountryCodeAdapter countryAdapter;
        LinearLayoutManager linearLayoutManager;

        linearLayoutManager = new LinearLayoutManager(LoginActivity.this);
        countryAdapter = new CountryCodeAdapter(LoginActivity.this, countries);
        rvCountry.setLayoutManager(linearLayoutManager);
        rvCountry.setAdapter(countryAdapter);
        rvCountry.addOnItemTouchListener(new RecyclerItemClickListener(LoginActivity.this, rvCountry, new RecyclerItemClickListener.OnItemClickListener() {
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
                tv_countryCode1.setText(country.getCountryName());
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

    protected void setupParent(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }


    public void localNotification() {
        Log.e("localNotification", "running");
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

        Intent intent = new Intent(context, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(context, "You are login Successfully", Toast.LENGTH_LONG).show();
    }
}
