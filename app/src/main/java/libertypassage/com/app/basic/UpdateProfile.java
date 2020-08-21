package libertypassage.com.app.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import libertypassage.com.app.R;
import libertypassage.com.app.adapter.AgeGroupAdapter;
import libertypassage.com.app.adapter.GenderAdapter;
import libertypassage.com.app.adapter.ProfessionsAdapter;
import libertypassage.com.app.image_croping.CropImage;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.DetailsOTP;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelExistance;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.models.ModelSignUp;
import libertypassage.com.app.models.user.Details;
import libertypassage.com.app.models.user.ModelUser;
import libertypassage.com.app.models.user.UserDetails;
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
    private ImageView iv_back, editImage;
    private CircleImageView civProfile;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String error_msg = "Required";
    private String token, fullName, email, emailNew, mobile, imageProfilePath,
            s_gender, s_ageGroup, s_profession = "Select Profession";
    private int profId;
    private Context context;
    Spinner spinnerGender, spinnerAgeGroup, spinnerProfession;
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
        setContentView(R.layout.update_profile);
        setupParent(getWindow().getDecorView().findViewById(android.R.id.content));

        context = UpdateProfile.this;
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        civProfile = findViewById(R.id.civProfile);
        editImage = findViewById(R.id.editImage);
        et_fullName = findViewById(R.id.et_fullName);
        et_email = findViewById(R.id.et_email);
        tv_mobile = findViewById(R.id.tv_mobile);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerProfession = findViewById(R.id.spinnerProfession);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        editImage.setOnClickListener(this);
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
        profId = Integer.parseInt(Utility.getSharedPreferences(context, Constants.KEY_PROF));
//        Glide.with(context).load(Utility.getSharedPreferences(context, Constants.KEY_POFILE_PIC)).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(civProfile);

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

        ipArrayList.clear();
        ipArrayList.addAll(Utility.getIndustryProfession(context));
        professionAdapter = new ProfessionsAdapter(context, ipArrayList);
        spinnerProfession.setAdapter(professionAdapter);
        spinnerProfession.setSelection(profId);
        if (ipArrayList.size() == 0) {
            if (Utility.isConnectingToInternet(context)) {
                getIndustryProfessions();
            } else {
                Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
            }
        }


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
            case R.id.editImage: {
                FileTypeDialog();
                break;
            }

            case R.id.tv_next: {
                fullName = et_fullName.getText().toString();
                emailNew = et_email.getText().toString();
                mobile = tv_mobile.getText().toString();
                Log.e("pdate_profId", s_profession + "//" + profId + "");
//                if (imageProfilePath != null) {
//                    Utility.setSharedPreference(context, Constants.KEY_POFILE_PIC, imageProfilePath);
//                }

                if (!TextUtils.isEmpty(emailNew)) {
                    if (emailNew.matches(emailPattern)) {
                        if (emailNew.equals(Utility.getSharedPreferences(context, Constants.KEY_EMAIL))) {
                            if (!TextUtils.isEmpty(mobile)) {
                                if (mobile.length() > 9) {
                                    if (!TextUtils.isEmpty(s_gender)) {
                                        if (!TextUtils.isEmpty(s_ageGroup)) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (140): {   // for camera
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        assert imageBitmap != null;
                        Uri tempUri = getImageUri(context, imageBitmap);
                        CropImage.activity(tempUri).start(UpdateProfile.this);
                    }
                }
                break;
            }

            case (150): {   // for gallery
                if (resultCode == Activity.RESULT_OK) {
                    assert data != null;
                    Uri selectedMediaUri = data.getData();
                    assert selectedMediaUri != null;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedMediaUri);
                        Uri tempUri = getImageUri(context, bitmap);
                        CropImage.activity(tempUri).start(UpdateProfile.this);
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


    private void FileTypeDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(UpdateProfile.this);
        View view = UpdateProfile.this.getLayoutInflater().inflate(R.layout.file_type_layout, null);
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


    private void updateProfileApiResponse() {
        Log.e("VerifyOTP", "userOtp" + Constants.KEY_BOT + imageProfilePath + fullName + email + mobile);
//        if (imageProfilePath != null) {
//            Log.e("imageProfilePath", imageProfilePath);
//            File imageProfile = new File(imageProfilePath);
//            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), imageProfile);
//            MultipartBody.Part image1 = MultipartBody.Part.createFormData("if_profilePic", imageProfile.getName(), requestBodyImage);
//            RequestBody bot1 = RequestBody.create(MediaType.parse("text/plain"), Constants.KEY_BOT);
//            RequestBody firstName1 = RequestBody.create(MediaType.parse("text/plain"), fullName);
//            RequestBody email1 = RequestBody.create(MediaType.parse("text/plain"), email);
//            RequestBody mobile1 = RequestBody.create(MediaType.parse("text/plain"), mobile);
//
//            ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
//            call = apiInterface.upadteProfileImage(Constants.KEY_HEADER + token, bot1, firstName1, email1, mobile1, image1);
//            Log.e("signUp", "withImage");
//        } else {
//            {

        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelSignUp> call = apiInterface.updateProfile(Constants.KEY_HEADER + token, Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, emailNew, mobile, s_gender, s_ageGroup, 1, profId);

        call.enqueue(new Callback<ModelSignUp>() {
            @Override
            public void onResponse(Call<ModelSignUp> call, Response<ModelSignUp> response) {
//                Utility.stopProgressDialog(context);
                ModelSignUp model = response.body();
                Log.e("SignUpModel", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {

                    Utility.setSharedPreference(context, Constants.KEY_FULLNAME, fullName);
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, emailNew);
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, mobile);
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, s_gender);
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, s_ageGroup);
                    Utility.setSharedPreference(context, Constants.KEY_PROF, String.valueOf(profId));

                    Intent intent = new Intent(context, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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


    private void getIndustryProfessions() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<IndustryProfessions> call = apiInterface.getIndustryProfessions(Constants.KEY_BOT, "1");

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

                    // spinnerProfession.setSelection(profId);
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
