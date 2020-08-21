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
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.PinEntryEditText;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyOtpEmailForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private String TAG = VerifyOtpEmailForgotPassword.class.getSimpleName();
    private TextView tv_continue, mResendTV, tv_timer, tv_timerMsg;
    private ImageView iv_back;
    private PinEntryEditText otpView;
    private LinearLayout ll_resend;
    private String bearer_token, emailNew, otp, from, isVerify;

    private static final long START_TIME_IN_MILLIS = 120000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp_email);

        context = VerifyOtpEmailForgotPassword.this;
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
        isVerify = Utility.getSharedPreferences(context, "isVerify");
        bearer_token = Utility.getSharedPreferences(context, "t_bearer");
        emailNew = Utility.getSharedPreferences(context, "t_email");
        otp = Utility.getSharedPreferences(context, "otp");

        otpView.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence s_otp) {
                dismissKeyboard(VerifyOtpEmailForgotPassword.this);
                if (s_otp.toString().equals(Utility.getSharedPreferences(context, "otp"))) {
                    Utility.setSharedPreference(context, "t_email", "Na");
                    Utility.setSharedPreference(context, "otp", "Na");
                    if (isVerify.equals("forgotEmail")) {
                        Intent intent = new Intent(context, UpdatePassword.class);
                        startActivity(intent);
                        Toast.makeText(context, "Email Verification Successfully", Toast.LENGTH_LONG).show();
                    } else if (isVerify.equals("loginEmail")) {
                        Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearer_token);
                        Intent intent = new Intent(context, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(context, "You are login Successfully", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show();
                }
            }
        });


        if(Objects.requireNonNull(from).equals("forgot")){
            tv_timerMsg.setVisibility(View.VISIBLE);
            startTimer();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continue: {
                String s_otp = otpView.getText().toString().trim();

                if (!TextUtils.isEmpty(s_otp)) {
                    if (s_otp.equals(Utility.getSharedPreferences(context, "otp"))) {

                        Utility.setSharedPreference(context, "t_email", "Na");
                        Utility.setSharedPreference(context, "otp", "Na");
                        Utility.setSharedPreference(context, "isVerify", "Na");

                        if (isVerify.equals("forgotEmail")) {
                            Intent intent = new Intent(context, UpdatePassword.class);
                            startActivity(intent);
                            Toast.makeText(context, "Email Verification Successfully", Toast.LENGTH_LONG).show();

                        } else if (isVerify.equals("loginEmail")) {
                            Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearer_token);
                            Intent intent = new Intent(context, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(context, "You are login Successfully", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter the otp", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.tv_resendCode: {
                if (!TextUtils.isEmpty(emailNew)) {
                    if (Utility.isConnectingToInternet(context)) {
                        getOTPResponse(emailNew);
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Required email address", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.iv_back: {
                Utility.setSharedPreference(context, "isVerify", "Na");
                Intent intent = new Intent(context, ForgotPassword.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }


    private void getOTPResponse(String email) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelOTP> call = apiInterface.resendOtpEmail(Constants.KEY_BOT, email);

        call.enqueue(new Callback<ModelOTP>() {
            @Override
            public void onResponse(Call<ModelOTP> call, Response<ModelOTP> response) {
                Utility.stopProgressDialog(context);
                ModelOTP model = response.body();
                Log.e("ResendOTP", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {
                    DetailsOTP detailsOTP = model.getDetails();

                    int otp = detailsOTP.getOtp();

                    Utility.setSharedPreference(context, "otp", String.valueOf(otp));
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    ll_resend.setVisibility(View.GONE);
                    tv_timer.setVisibility(View.VISIBLE);
                    tv_timerMsg.setVisibility(View.VISIBLE);
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
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
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
        SharedPreferences prefs = getSharedPreferences("prefs_forgot", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft_forgot", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning_forgot", false);
        updateCountDownText();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime_forgot", 0);
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
        SharedPreferences prefs = getSharedPreferences("prefs_forgot", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft_forgot", mTimeLeftInMillis);
        editor.putBoolean("timerRunning_forgot", mTimerRunning);
        editor.putLong("endTime_forgot", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
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
        Intent intent = new Intent(context, ForgotPassword.class);
        startActivity(intent);
        finish();
    }
}
