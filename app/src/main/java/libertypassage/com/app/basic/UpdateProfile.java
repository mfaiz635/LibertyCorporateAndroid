package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import libertypassage.com.app.R;
import libertypassage.com.app.adapter.AgeGroupAdapter;
import libertypassage.com.app.adapter.GenderAdapter;
import libertypassage.com.app.adapter.IndustryAdapter;
import libertypassage.com.app.adapter.ProfessionsAdapter;
import libertypassage.com.app.models.DetailIndustry;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.DetailsOTP;
import libertypassage.com.app.models.Industry;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelExistance;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.models.ModelSignUp;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
    private String TAG = UpdateProfile.class.getSimpleName();
    private CenturyGothicEditText et_fullName, et_email;
    private CenturyGothicTextview tv_mobile, tv_next;
    private ImageView iv_back;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String error_msg = "Required";
    private String token, fullName, email, emailNew, mobile,
            s_gender, s_ageGroup, emailVerify, s_industry = "Select Industry",
            s_profession = "Select Profession";
    private int industryId, profId;
    private Context context;
    Spinner spinnerGender, spinnerAgeGroup, spinnerIndustry, spinnerProfession;
    GenderAdapter genderAdapter;
    AgeGroupAdapter ageGroupAdapter;
    IndustryAdapter industryAdapter;
    ProfessionsAdapter professionAdapter;
    ArrayList<String> genderList = new ArrayList<String>();
    ArrayList<String> ageGroupList = new ArrayList<String>();
    List<DetailIndustry> industryList = new ArrayList<DetailIndustry>();
    ArrayList<DetailIndustry> industryArrayList = new ArrayList<DetailIndustry>();
    List<DetailIndustryProf> ipList = new ArrayList<DetailIndustryProf>();
    ArrayList<DetailIndustryProf> ipArrayList = new ArrayList<DetailIndustryProf>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        setupParent(getWindow().getDecorView().findViewById(android.R.id.content));

        context = UpdateProfile.this;
        findID();
        init();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        et_fullName = findViewById(R.id.et_fullName);
        et_email = findViewById(R.id.et_email);
        tv_mobile = findViewById(R.id.tv_mobile);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerIndustry = findViewById(R.id.spinnerIndustry);
        spinnerProfession = findViewById(R.id.spinnerProfession);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        tv_next.setOnClickListener(this);
    }

    private void init() {
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);

        et_fullName.setText(Utility.getSharedPreferences(context, Constants.KEY_FULLNAME));
        tv_mobile.setText(Utility.getSharedPreferences(context, Constants.KEY_MOBILE));
        email = Utility.getSharedPreferences(context, Constants.KEY_EMAIL);
        et_email.setText(email);
        s_gender = Utility.getSharedPreferences(context, Constants.KEY_GENDER);
        s_ageGroup = Utility.getSharedPreferences(context, Constants.KEY_AGE_GROUP);
        emailVerify = Utility.getSharedPreferences(context, Constants.KEY_EMAIL_VERIFIED);
        profId = Integer.parseInt(Utility.getSharedPreferences(context, Constants.KEY_PROF));
        industryId = Integer.parseInt(Utility.getSharedPreferences(context, Constants.KEY_INDUSTRY));


//        if(emailVerify.equals("true")){
//
//        }else if(emailVerify.equals("false")){
//
//        }


        genderList.add("Select Gender");
        genderList.add("Male");
        genderList.add("Female");

        ageGroupList.add("Select Age Group");
        ageGroupList.add("Under 18");
        ageGroupList.add("18-30");
        ageGroupList.add("31-45");
        ageGroupList.add("46-60");
        ageGroupList.add("61-80");
        ageGroupList.add("Above 80");

        industryArrayList.clear();
        industryArrayList.addAll(Utility.getIndustry(context));
        industryAdapter = new IndustryAdapter(context, industryArrayList);
        spinnerIndustry.setAdapter(industryAdapter);
        spinnerIndustry.setSelection(industryId);
        if (industryArrayList.size() == 0) {
            if (Utility.isConnectingToInternet(context)) {
                getIndustry();
            } else {
                Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
            }
        }

        ipArrayList.clear();
        ipArrayList.addAll(Utility.getIndustryProfession(context));
        professionAdapter = new ProfessionsAdapter(context, ipArrayList);
        spinnerProfession.setAdapter(professionAdapter);
        spinnerProfession.setSelection(profId);


        genderAdapter = new GenderAdapter(context, R.layout.spinner_layout, genderList);
        spinnerGender.setAdapter(genderAdapter);

        ageGroupAdapter = new AgeGroupAdapter(context, R.layout.spinner_layout, ageGroupList);
        spinnerAgeGroup.setAdapter(ageGroupAdapter);

        spinnerGender.setSelection(genderAdapter.getPosition(s_gender));
        spinnerAgeGroup.setSelection(ageGroupAdapter.getPosition(s_ageGroup));

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerGender.getSelectedItemPosition() != 0) {
                    s_gender = genderList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerAgeGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerAgeGroup.getSelectedItemPosition() != 0) {
                    s_ageGroup = ageGroupList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerIndustry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerIndustry.getSelectedItemPosition() != 0) {
                    industryId = industryArrayList.get(position).getIndustryId();
                    s_industry = industryArrayList.get(position).getTitle();
                }
                if (ipArrayList.size() == 0) {
                    if (Utility.isConnectingToInternet(context)) {
                        getIndustryProfessions();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profId = ipArrayList.get(position).getProfId();
                s_profession = ipArrayList.get(position).getTitle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next: {
                fullName = et_fullName.getText().toString();
                emailNew = et_email.getText().toString();
                mobile = tv_mobile.getText().toString();
                Log.e("pdate_profId", s_profession + "//" + profId + "");

                if (!TextUtils.isEmpty(emailNew)) {
                    if (emailNew.matches(emailPattern)) {
                        if (emailNew.equals(Utility.getSharedPreferences(context, Constants.KEY_EMAIL))) {
                            if (!TextUtils.isEmpty(mobile)) {
                                if (mobile.length() > 9) {
                                    if (!TextUtils.isEmpty(s_gender)) {
                                        if (!TextUtils.isEmpty(s_ageGroup)) {
                                            if (!s_industry.equals("Select Industry")) {
                                                if (!s_profession.equals("Select Profession")) {
                                                    if (Utility.isConnectingToInternet(context)) {
                                                        updateProfileApiResponse();
                                                    } else {
                                                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Select Profession", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Select Industry", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "Select Age Group", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Select Gender", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    tv_mobile.setError("Required valid number");
                                }
                            } else {
                                tv_mobile.setError(error_msg);
                            }
                        } else {
                            // for verify email
                            checkUserExistance(emailNew);
                            // getOtpEmail(emailNew);
                        }
                    } else {
                        et_email.setError("Required valid email");
                    }
                } else {
                    et_email.setError(error_msg);
                }
                break;
            }

            case R.id.iv_back: {
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                finish();
                break;
            }
        }
    }


    private void updateProfileApiResponse() {
        Log.e("VerifyOTP", "userOtp" + Constants.KEY_BOT + fullName + email + mobile);
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelSignUp> call = apiInterface.updateProfile(Constants.KEY_HEADER + token, Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, emailNew, mobile, s_gender, s_ageGroup, 1, profId);

        call.enqueue(new Callback<ModelSignUp>() {
            @Override
            public void onResponse(Call<ModelSignUp> call, Response<ModelSignUp> response) {
                Utility.stopProgressDialog(context);
                ModelSignUp model = response.body();
                Log.e("SignUpModel", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {
                    Utility.setSharedPreference(context, Constants.KEY_FULLNAME, fullName);
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, emailNew);
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, mobile);
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, s_gender);
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, s_ageGroup);
                    Utility.setSharedPreference(context, Constants.KEY_PROF, String.valueOf(profId));
                    finish();

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelSignUp> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Log.e("onFailure", t.toString());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getIndustry() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Industry> call = apiInterface.getIndustries(Constants.KEY_BOT);

        call.enqueue(new Callback<Industry>() {
            @Override
            public void onResponse(Call<Industry> call, Response<Industry> response) {
                Utility.stopProgressDialog(context);
                Industry model = response.body();
                Log.e("modelChangeStaus", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    industryList.clear();
                    industryList = model.getDetails();

                    industryArrayList.clear();
                    industryArrayList.add(new DetailIndustry(0, "Select Industry"));
                    industryArrayList.addAll(industryList);
                    industryAdapter = new IndustryAdapter(context, industryArrayList);
                    spinnerIndustry.setAdapter(industryAdapter);
                    if (industryArrayList != null) {
                        Utility.saveIndustry(context, industryArrayList);
                    }
                    int selecteIndustry = industryId;
                    spinnerIndustry.setSelection(selecteIndustry);

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Industry> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getIndustryProfessions() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<IndustryProfessions> call = apiInterface.getIndustryProfessions(Constants.KEY_BOT, industryId);

        call.enqueue(new Callback<IndustryProfessions>() {
            @Override
            public void onResponse(Call<IndustryProfessions> call, Response<IndustryProfessions> response) {
                Utility.stopProgressDialog(context);
                IndustryProfessions model = response.body();
                Log.e("getIndustryProfessions", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    ipList.clear();
                    ipList = model.getDetails();

                    ipArrayList.clear();
                    ipArrayList.add(new DetailIndustryProf(0, 0, "Select Profession"));
                    ipArrayList.addAll(ipList);
                    professionAdapter = new ProfessionsAdapter(context, ipArrayList);
                    spinnerProfession.setAdapter(professionAdapter);
                    if (ipArrayList != null) {
                        Utility.saveIndustryProfession(context, ipArrayList);
                    }

                    int selecteProf = profId;
                    spinnerProfession.setSelection(selecteProf);


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IndustryProfessions> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserExistance(String email) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelExistance> call = apiInterface.checkUserExistance(Constants.KEY_BOT, email, "");

        call.enqueue(new Callback<ModelExistance>() {
            @Override
            public void onResponse(Call<ModelExistance> call, Response<ModelExistance> response) {
//                Utility.stopProgressDialog(context);
                ModelExistance model = response.body();
                Log.e("model", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    getOtpEmail(email);

                } else {
                    if (model != null && model.getError().equals(true)) {
                        Utility.stopProgressDialog(context);
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelExistance> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOtpEmail(String email) {
//        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelOTP> call = apiInterface.getOtpEmail(Constants.KEY_BOT, email);

        call.enqueue(new Callback<ModelOTP>() {
            @Override
            public void onResponse(Call<ModelOTP> call, Response<ModelOTP> response) {
                Utility.stopProgressDialog(context);
                ModelOTP model = response.body();
                Log.e("ResendOTP", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {
                    DetailsOTP detailsOTP = model.getDetails();
                    int otp = detailsOTP.getOtp();

                    Utility.setSharedPreference(context, Constants.KEY_START, "6");
                    Utility.setSharedPreference(context, "otp", String.valueOf(otp));
                    Utility.setSharedPreference(context, "emailNew", email);
                    Intent intent = new Intent(context, VerifyOtpEmailUpadteProfile.class);
                    intent.putExtra("from", "profile");
                    startActivity(intent);
                    finish();
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelOTP> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
