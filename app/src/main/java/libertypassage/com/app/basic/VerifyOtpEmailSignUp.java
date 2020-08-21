package libertypassage.com.app.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import libertypassage.com.app.R;
import libertypassage.com.app.models.DetailsOTP;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.models.ModelSignUp;
import libertypassage.com.app.models.SignUpDetails;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.PinEntryEditText;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyOtpEmailSignUp extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private String TAG = VerifyOtpEmailSignUp.class.getSimpleName();
    private TextView tv_continue, mResendTV, tv_timer, tv_timerMsg;
    private ImageView iv_back;
    private PinEntryEditText otpView;
    private LinearLayout ll_resend;
    private String firebaseToken, firstName, email, mobileWithCountry,  s_gender, s_ageGroup,
            s_profession, password, imageProfilePath, countryId, from;
    private int otp;

    private static final long START_TIME_IN_MILLIS = 120000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp_email);

        context = VerifyOtpEmailSignUp.this;
        findID();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        otpView = findViewById(R.id.otpView);
        tv_continue = findViewById(R.id.tv_continue);
        tv_timer = findViewById(R.id.tv_timer);
        tv_timerMsg = findViewById(R.id.tv_timerMsg);
        ll_resend = findViewById(R.id.ll_resend);
        mResendTV = findViewById(R.id.tv_resendCode);

        iv_back.setOnClickListener(this);
        tv_continue.setOnClickListener(this);
        mResendTV.setOnClickListener(this);


        from = getIntent().getStringExtra("from");
//        imageProfilePath = Utility.getSharedPreferences(context, Constants.KEY_POFILE_PIC);
//        firstName = Utility.getSharedPreferences(context, Constants.KEY_FIRSTNAME);
        email = Utility.getSharedPreferences(context, Constants.KEY_EMAIL);
        mobileWithCountry = Utility.getSharedPreferences(context, Constants.KEY_MOBILE);
        s_gender = Utility.getSharedPreferences(context, Constants.KEY_GENDER);
        s_ageGroup = Utility.getSharedPreferences(context, Constants.KEY_AGE_GROUP);
        s_profession = Utility.getSharedPreferences(context, Constants.KEY_PROF);
        countryId = Utility.getSharedPreferences(context, Constants.KEY_COUNTRY_ID);
        password = Utility.getSharedPreferences(context, "password");
        firebaseToken = Utility.getSharedPreferences(context, "firebaseToken");
        otp = Integer.parseInt(Utility.getSharedPreferences(context, "otp"));

        otpView.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence s_otp) {
                dismissKeyboard(VerifyOtpEmailSignUp.this);
                if (s_otp.toString().equals(String.valueOf(otp))) {
                    signUpApiResponse();
                } else {
                    Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(Objects.requireNonNull(from).equals("signup")){
            tv_timerMsg.setVisibility(View.VISIBLE);
            startTimer();
        }

    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mCountDownTimer.cancel();
                tv_timer.setVisibility(View.GONE);
                tv_timerMsg.setVisibility(View.GONE);
                ll_resend.setVisibility(View.VISIBLE);
            }
        }.start();
        mTimerRunning = true;
        tv_timer.setVisibility(View.VISIBLE);
        tv_timerMsg.setVisibility(View.VISIBLE);
        ll_resend.setVisibility(View.GONE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv_timer.setText(timeLeftFormatted);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs_es", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft_es", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning_es", false);
        updateCountDownText();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime_es", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
            } else {
                startTimer();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimerRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs_es", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft_es", mTimeLeftInMillis);
        editor.putBoolean("timerRunning_es", mTimerRunning);
        editor.putLong("endTime_es", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continue: {
                String s_otp = otpView.getText().toString().trim();

                if (!TextUtils.isEmpty(s_otp)) {
                    if (s_otp.equals(String.valueOf(otp))) {

                        signUpApiResponse();

                    } else {
                        Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter the otp", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.tv_resendCode: {
                if (!TextUtils.isEmpty(email)) {
                    if (Utility.isConnectingToInternet(context)) {
                        getOTPResponse(email);
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Required mobile number", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.iv_back: {
                Utility.setSharedPreference(context, "isVerify", "Na");
                Intent intent = new Intent(context, RequestOtpEmail.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    private void getOTPResponse(String email) {
        Utility.showProgressDialog(context);
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
                    otp = detailsOTP.getOtp();

                    Utility.setSharedPreference(context, "otp", String.valueOf(otp));
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    tv_timerMsg.setVisibility(View.VISIBLE);
                    tv_timer.setVisibility(View.VISIBLE);
                    ll_resend.setVisibility(View.GONE);
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    updateCountDownText();
                    startTimer();

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelOTP> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signUpApiResponse() {
        Utility.showProgressDialog(context);
        Log.e("VerifyOTP", "userOtp" +otp+Constants.KEY_BOT +otp + mobileWithCountry  + countryId + password + "Tokan : " + firebaseToken);
//        if (imageProfilePath != null) {
//            Log.e("imageProfilePath", imageProfilePath);
//            File imageProfile = new File(imageProfilePath);
//            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), imageProfile);
//            MultipartBody.Part image1 = MultipartBody.Part.createFormData("if_profilePic", imageProfile.getName(), requestBodyImage);
//            RequestBody bot1 = RequestBody.create(MediaType.parse("text/plain"), Constants.KEY_BOT);
//            RequestBody firstName1 = RequestBody.create(MediaType.parse("text/plain"), firstName);
//            RequestBody lastName1 = RequestBody.create(MediaType.parse("text/plain"), lastName);
//            RequestBody email1 = RequestBody.create(MediaType.parse("text/plain"), email);
//            RequestBody mobile1 = RequestBody.create(MediaType.parse("text/plain"), mobileWithCountry);
//            RequestBody password1 = RequestBody.create(MediaType.parse("text/plain"), password);
//            RequestBody firebaseToken1 = RequestBody.create(MediaType.parse("text/plain"), firebaseToken);
//
//            ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
//            call = apiInterface.userSignUpImage(bot1, firstName1, lastName1, email1, mobile1, password1, firebaseToken1, image1);
//            Log.e("signUp", "withImage");
//        } else {

        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelSignUp> call = apiInterface.userSignUp(Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, email, mobileWithCountry, s_gender, s_ageGroup, "1", s_profession, countryId, password, firebaseToken);

        call.enqueue(new Callback<ModelSignUp>() {
            @Override
            public void onResponse(Call<ModelSignUp> call, Response<ModelSignUp> response) {
                Utility.stopProgressDialog(context);
                ModelSignUp model = response.body();
                Log.e("SignUpModel", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {
                    SignUpDetails signUpDetails = model.getDetails();
                    String bearerToken = signUpDetails.getBearerToken();

                    Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearerToken);
                    Utility.setSharedPreference(context, Constants.KEY_START, "1");
                    Utility.setSharedPreference(context, "isVerify", "Na");
                    Utility.setSharedPreference(context, "password", "Na");
                    Utility.setSharedPreference(context, "firebaseToken", "Na");
                    Utility.setSharedPreference(context, "otp", "Na");

                    Intent intent = new Intent(context, EnrolmentDeclaration.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();



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


    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility.setSharedPreference(context, "isVerify", "Na");
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
