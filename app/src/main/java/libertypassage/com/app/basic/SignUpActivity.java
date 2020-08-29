package libertypassage.com.app.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import libertypassage.com.app.R;
import libertypassage.com.app.adapter.AgeGroupAdapter;
import libertypassage.com.app.adapter.CountryCodeAdapter;
import libertypassage.com.app.adapter.CountryCodeSpinnerAdapter;
import libertypassage.com.app.adapter.GenderAdapter;
import libertypassage.com.app.adapter.ProfessionsAdapter;
import libertypassage.com.app.image_croping.CropImage;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.CountryDto;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.DetailsOTP;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelCountryList;
import libertypassage.com.app.models.ModelExistance;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.ImageCompression;
import libertypassage.com.app.utilis.LocationUpdateService;
import libertypassage.com.app.utilis.RecyclerItemClickListener;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private CenturyGothicEditText et_fullName, et_email, et_mobileNo, et_password;
    private CenturyGothicTextview tv_next, tv_countryCode;
    private ImageView iv_back, editImage, pass_view, pass_hide;
    private CircleImageView civProfile;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String error_msg = "Required";
    private String firebaseToken, fullName, email, mobile, mobileWithCountry,
            password, imageProfilePath, s_gender, s_ageGroup, s_profession;
    private int profId, countryId;
    private Context context;
    private String TAG = SignUpActivity.class.getSimpleName();
    Spinner spnCountryCode, spinnerGender, spinnerAgeGroup, spinnerProfession;
    String country_Name, flag = "x", country;
    ArrayList<String> Country_names_array = new ArrayList<String>();
    ArrayList<CountryDto> countries = new ArrayList<>();
    ArrayList<CountryDto> filterdNames = new ArrayList<>();
    CountryCodeSpinnerAdapter adapter;
    Dialog dialog;
    GenderAdapter genderAdapter;
    AgeGroupAdapter ageGroupAdapter;
    ProfessionsAdapter professionAdapter;
    ArrayList<String> genderList = new ArrayList<String>();
    ArrayList<String> ageGroupList = new ArrayList<String>();
    List<DetailIndustryProf> ipList = new ArrayList<DetailIndustryProf>();
    ArrayList<DetailIndustryProf> ipArrayList = new ArrayList<DetailIndustryProf>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupParent(getWindow().getDecorView().findViewById(android.R.id.content));

        context = SignUpActivity.this;
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        civProfile = findViewById(R.id.civProfile);
        editImage = findViewById(R.id.editImage);
        et_fullName = findViewById(R.id.et_fullName);
        et_email = findViewById(R.id.et_email);
        spnCountryCode = findViewById(R.id.spnCountryCode);
        tv_countryCode = findViewById(R.id.tv_countryCode);
        et_mobileNo = findViewById(R.id.et_mobileNo);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerProfession = findViewById(R.id.spinnerProfession);
        et_password = findViewById(R.id.et_password);
        pass_view = findViewById(R.id.pass_view);
        pass_hide = findViewById(R.id.pass_hide);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        editImage.setOnClickListener(this);
        tv_countryCode.setOnClickListener(this);
        pass_view.setOnClickListener(this);
        pass_hide.setOnClickListener(this);
        tv_next.setOnClickListener(this);
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
                        Log.e(TAG, "token = " + firebaseToken);
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
        spnCountryCode.setAdapter(adapter);
        spnCountryCode.setOnItemSelectedListener(SignUpActivity.this);
        setCountryCode();

        ipArrayList.clear();
        ipArrayList.addAll(Utility.getIndustryProfession(context));
        professionAdapter = new ProfessionsAdapter(context, ipArrayList);
        spinnerProfession.setAdapter(professionAdapter);
        if (ipArrayList.size() == 0) {
            if (Utility.isConnectingToInternet(context)) {
                getIndustryProfessions();
            } else {
                Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
            }
        }

        et_fullName.setText(Utility.getSharedPreferences(context, Constants.KEY_FULLNAME));
        et_email.setText(Utility.getSharedPreferences(context, Constants.KEY_EMAIL));
        Glide.with(context).load(Utility.getSharedPreferences(context, Constants.KEY_POFILE_PIC)).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(civProfile);

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


        genderAdapter = new GenderAdapter(context, R.layout.spinner_layout, genderList);
        spinnerGender.setAdapter(genderAdapter);

        ageGroupAdapter = new AgeGroupAdapter(context, R.layout.spinner_layout, ageGroupList);
        spinnerAgeGroup.setAdapter(ageGroupAdapter);


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

        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    profId = ipArrayList.get(position).getProfId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editImage: {
                FileTypeDialog();
                break;
            }

            case R.id.tv_next: {
                fullName = et_fullName.getText().toString();
                email = et_email.getText().toString();
                mobile = et_mobileNo.getText().toString();
                password = et_password.getText().toString();
                country = tv_countryCode.getText().toString();
                mobileWithCountry = country+mobile;

                if (!TextUtils.isEmpty(fullName)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (email.matches(emailPattern)) {
                            if (!TextUtils.isEmpty(mobile)) {
                                if (mobile.length() > 7) {
                                    if (!TextUtils.isEmpty(s_gender)) {
                                        if (!TextUtils.isEmpty(s_ageGroup)) {
                                            if (profId!=0) {
                                                if (!TextUtils.isEmpty(password)) {
                                                    if (password.length() > 5) {
                                                        if (Pattern.compile("[A-Z ]").matcher(password).find()) {     //Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);  Pattern digitCasePatten = Pattern.compile("[0-9 ]");

                                                            if (Utility.isConnectingToInternet(context)) {
                                                                Log.e("signUpParameter", Constants.KEY_BOT + fullName + email + mobileWithCountry + password + "Tokan : " + firebaseToken);
                                                                checkUserExistance();

                                                            } else {
                                                                Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            et_password.setError("Password must have one uppercase character");
                                                        }
                                                    } else {
                                                        et_password.setError("Password length must have 6 character");
                                                    }
                                                } else {
                                                    et_password.setError(error_msg);
                                                }
                                            } else {
                                                Toast.makeText(context, "Select Profession", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "Select Age Group", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Select Gender", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    et_mobileNo.setError("Required valid number");
                                }
                            } else {
                                et_mobileNo.setError(error_msg);
                            }
                        } else {
                            et_email.setError("Required valid email");
                        }
                    } else {
                        et_email.setError(error_msg);
                    }
                } else {
                    et_fullName.setError(error_msg);
                }
                break;
            }

            case R.id.tv_countryCode: {
                selectCountry();
                break;
            }
            case R.id.iv_back: {
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                finish();
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

        }
    }


    private void checkUserExistance() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelExistance> call = apiInterface.checkUserExistance(Constants.KEY_BOT, email, mobileWithCountry);

        call.enqueue(new Callback<ModelExistance>() {
            @Override
            public void onResponse(Call<ModelExistance> call, Response<ModelExistance> response) {
//                Utility.stopProgressDialog(context);
                ModelExistance model = response.body();
                Log.e("model", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    getOTPResponse(mobileWithCountry);

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

    private void getOTPResponse(String mobile) {
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelOTP> call = apiInterface.getOtpResponse(Constants.KEY_BOT, mobile);

        call.enqueue(new Callback<ModelOTP>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ModelOTP> call, Response<ModelOTP> response) {
                Utility.stopProgressDialog(context);
                ModelOTP model = response.body();

                Log.e("ResendOTP", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    DetailsOTP details = model.getDetails();
                    int otp = details.getOtp();
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    Log.e("signUpParameter", Constants.KEY_BOT + fullName + email + mobileWithCountry + password + "Tokan : " + firebaseToken);
                    Utility.setSharedPreference(context, "isVerify", "signup");
                    Utility.setSharedPreference(context, Constants.KEY_FULLNAME, fullName);
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, email);
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, mobileWithCountry);
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, s_gender);
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, s_ageGroup);
                    Utility.setSharedPreference(context, Constants.KEY_PROF, String.valueOf(profId));
                    Utility.setSharedPreference(context, Constants.KEY_COUNTRY_ID, String.valueOf(countryId));
                    Utility.setSharedPreference(context, "password", password);
                    Utility.setSharedPreference(context, "firebaseToken", firebaseToken);
                    Utility.setSharedPreference(context, "otp", String.valueOf(otp));
//                    if (imageProfilePath != null) {
//                        Utility.setSharedPreference(context, Constants.KEY_POFILE_PIC, imageProfilePath);
//                    }
                    Intent intent = new Intent(context, VerifyOTPSignUp.class);
                    intent.putExtra("from", "signup");
                    startActivity(intent);

//                    intent.putExtra("imageProfilePath", imageProfilePath);
//                    intent.putExtra("firstName", firstName);
//                    intent.putExtra("lastName", lastName);
//                    intent.putExtra("email", email);
//                    intent.putExtra("mobileWithCountry", mobileWithCountry);
//                    intent.putExtra("password", password);
//                    intent.putExtra("firebaseToken", firebaseToken);
//                    intent.putExtra("otp", otp);


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    dialogUpdate(model.getMessage());
//                    Toast.makeText(context, model.getMessage(), 10000).show();
                }
            }

            @Override
            public void onFailure(Call<ModelOTP> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (140): {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
                        CropImage.activity(tempUri).start(this);
                    }

                }
                break;
            }

            case (150): {
                if (resultCode == Activity.RESULT_OK) {
                    assert data != null;
                    Uri selectedMediaUri = data.getData();
                    assert selectedMediaUri != null;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedMediaUri);
                        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                        CropImage.activity(tempUri).start(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE): {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageProfilePath = resultUri + "";
                    Glide.with(context).load(resultUri).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(civProfile);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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


    private void getIndustryProfessions() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<IndustryProfessions> call = apiInterface.getIndustryProfessions(Constants.KEY_BOT, "1");

        call.enqueue(new Callback<IndustryProfessions>() {
            @Override
            public void onResponse(Call<IndustryProfessions> call, Response<IndustryProfessions> response) {
                Utility.stopProgressDialog(context);
                IndustryProfessions model = response.body();
                Log.e("modelChangeStaus", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    ipList.clear();
                    ipList = model.getDetails();

                    ipArrayList.clear();
                    ipArrayList.add(new DetailIndustryProf(0, 1, "Select Profession"));
                    ipArrayList.addAll(ipList);
                    professionAdapter = new ProfessionsAdapter(context, ipArrayList);
                    spinnerProfession.setAdapter(professionAdapter);
                    if (ipArrayList != null) {
                        Utility.saveIndustryProfession(context, ipArrayList);
                    }

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

    private void setCountryCode() {
        country_Name = Utility.getSharedPreferences(context, "country_name");
        if (country_Name.equals("")) {
//            Toast.makeText(this, "Unable to pick country.", Toast.LENGTH_LONG).show();
        } else {
            flag = "1";
            Log.e("country_Name", country_Name);
//            int spinnerPosition = adapter.getPosition(country_Name);
//            spnCountryCode.setSelection(spinnerPosition);
//            Log.e(country_Name, spinnerPosition+"");
//            int i = Country_names_array.indexOf(country_Name);
//            Log.e("name-Size", String.valueOf(Country_names_array.size()));
//            int code = countries.get(i).getCountryCode();
//            spnCountryCode.setSelection(i);
//            Log.e("CODE--", code + "");
//            Log.e("COUNTRY--", spnCountryCode.getSelectedItem().toString());
//            tv_countryCode.setText("+" + code);
//            Utility.setSharedPreference(context, "country_code", String.valueOf(code));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e("FLAG-", flag);
        if (flag.equals("1")) {
            Log.e("1 FLAG-", flag);
            int k = Country_names_array.indexOf(country_Name);
            int code = countries.get(k).getCountryCode();
            countryId = countries.get(k).getCountryId();
            spnCountryCode.setSelection(k);
            tv_countryCode.setText(code);

            flag = "x";
        } else if (flag.equals("x")) {
            Log.e("X FLAG-", flag);
            String country = spnCountryCode.getSelectedItem().toString();
            int position = Country_names_array.indexOf(country);
            int code = countries.get(position).getCountryCode();
            countryId = countries.get(position).getCountryId();
            spnCountryCode.setSelection(position);
            tv_countryCode.setText(code);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void selectCountry() {
        dialog = new Dialog(SignUpActivity.this);
        dialog.setContentView(R.layout.country_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        EditText edtCountry = dialog.findViewById(R.id.edtCountry);
        ImageView cancel = dialog.findViewById(R.id.cancel);
        RecyclerView rvCountry = dialog.findViewById(R.id.rvCountry);
        final CountryCodeAdapter countryAdapter;
        LinearLayoutManager linearLayoutManager;

        linearLayoutManager = new LinearLayoutManager(SignUpActivity.this);
        countryAdapter = new CountryCodeAdapter(SignUpActivity.this, countries);
        rvCountry.setLayoutManager(linearLayoutManager);
        rvCountry.setAdapter(countryAdapter);
        rvCountry.addOnItemTouchListener(new RecyclerItemClickListener(SignUpActivity.this, rvCountry, new RecyclerItemClickListener.OnItemClickListener() {
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
                countryId = country.getCountryId();
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
        //new array list that will hold the filtered data
        filterdNames = new ArrayList<>();

        //looping through existing elements

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

    private void FileTypeDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(SignUpActivity.this);
        View view = SignUpActivity.this.getLayoutInflater().inflate(R.layout.file_type_layout, null);
        dialog.setContentView(view);

        LinearLayout camera_RL = view.findViewById(R.id.file_camera);
        LinearLayout images_RL = view.findViewById(R.id.file_images);
        CenturyGothicTextview cancel_btn = view.findViewById(R.id.cancel_btn);

        camera_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, 140);
                    dialog.dismiss();
                }
            }
        });

        images_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 150);
            }
        });

//        cancel_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    private void dialogUpdate(String msg) {
        Dialog dialog;
        dialog = new Dialog(SignUpActivity.this);
        dialog.setContentView(R.layout.dialog_error_msg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView cancel = dialog.findViewById(R.id.cancel);
        CenturyGothicTextview title = dialog.findViewById(R.id.title);
        CenturyGothicTextview tv_ok = dialog.findViewById(R.id.tv_ok);

        title.setText(msg);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
