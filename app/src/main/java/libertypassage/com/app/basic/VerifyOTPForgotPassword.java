package libertypassage.com.app.basic;


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


public class VerifyOTPForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private final String TAG = VerifyOTPForgotPassword.class.getSimpleName();
    private TextView tv_continue, mResendTV, tv_timer;
    private LinearLayout ll_resend;
    private PinEntryEditText otpView;
    private ImageView iv_back;
    private String isVerify, mobile, bearer_token, s_otp, from;
    int otp;

    private static final long START_TIME_IN_MILLIS = 60000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp_forgot_password);

        context = VerifyOTPForgotPassword.this;
        findID();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        otpView = findViewById(R.id.otpView);
        tv_continue = findViewById(R.id.tv_continue);
        tv_timer = findViewById(R.id.tv_timer);
        ll_resend = findViewById(R.id.ll_resend);
        mResendTV = findViewById(R.id.tv_resendCode);

        iv_back.setOnClickListener(this);
        tv_continue.setOnClickListener(this);
        mResendTV.setOnClickListener(this);

        from = getIntent().getStringExtra("from");
        isVerify = Utility.getSharedPreferences(context, "isVerify");
        bearer_token = Utility.getSharedPreferences(context, "t_bearer");
        mobile = Utility.getSharedPreferences(context, "t_mobile");
        otp = Integer.parseInt(Utility.getSharedPreferences(context, "otp"));

        otpView.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence s_otp) {
                dismissKeyboard(VerifyOTPForgotPassword.this);

                if (s_otp.toString().equals(String.valueOf(otp))) {
                    Utility.setSharedPreference(context, "t_mobile", "Na");
                    Utility.setSharedPreference(context, "otp", "Na");
                    if (isVerify.equals("forgot")) {
                        Utility.setSharedPreference(context, "isVerify", "forgotEmail");
                        Intent intent = new Intent(VerifyOTPForgotPassword.this, RequestOtpEmailForgot.class);
                        startActivity(intent);
                    } else if (isVerify.equals("login")) {
                        Toast.makeText(context, "You are login successfully", Toast.LENGTH_LONG).show();
                        Utility.setSharedPreference(context, "isVerify", "Na");
                        Utility.setSharedPreference(context, "t_bearer", "Na");
                        Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearer_token);
                        Intent intent = new Intent(context, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(Objects.requireNonNull(from).equals("forgot")){
            startTimer();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continue: {
                String s_otp = otpView.getText().toString().trim();
                if (!TextUtils.isEmpty(s_otp)) {
                    if (s_otp.equals(String.valueOf(otp))) {

                        Utility.setSharedPreference(context, "t_mobile", "Na");
                        Utility.setSharedPreference(context, "otp", "Na");

                        if (isVerify.equals("forgot")) {
                            Toast.makeText(context, "Mobile number verifiy successfully", Toast.LENGTH_LONG).show();
                            Utility.setSharedPreference(context, "isVerify", "forgotEmail");
                            Intent intent = new Intent(VerifyOTPForgotPassword.this, RequestOtpEmailForgot.class);
                            startActivity(intent);
                        } else if (isVerify.equals("login")) {
                            Toast.makeText(context, "You are login successfully", Toast.LENGTH_LONG).show();
                            Utility.setSharedPreference(context, "isVerify", "Na");
                            Utility.setSharedPreference(context, "t_bearer", "Na");
                            Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearer_token);
                            Intent intent = new Intent(context, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(context, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter the OTP", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.tv_resendCode: {
                if (!TextUtils.isEmpty(mobile)) {
                    if (Utility.isConnectingToInternet(context)) {
                        getOTPResponse(mobile);
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
                if (isVerify.equals("forgot")) {
                    Intent intent = new Intent(context, ForgotPassword.class);
                    startActivity(intent);
                    finish();
                } else if (isVerify.equals("login")) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            }
        }
    }

    private void getOTPResponse(String mobile) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelOTP> call = apiInterface.getOtpResponse(Constants.KEY_BOT, mobile);

        call.enqueue(new Callback<ModelOTP>() {
            @Override
            public void onResponse(Call<ModelOTP> call, Response<ModelOTP> response) {
                Utility.stopProgressDialog(context);
                ModelOTP model = response.body();
                Log.e("ResendOTP", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {
                    DetailsOTP detailsOTP = model.getDetails();
                    otp = detailsOTP.getOtp();

                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    Utility.setSharedPreference(context, "otp", String.valueOf(otp));
                    ll_resend.setVisibility(View.GONE);
                    tv_timer.setVisibility(View.VISIBLE);
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
                ll_resend.setVisibility(View.VISIBLE);
            }
        }.start();
        mTimerRunning = true;
        tv_timer.setVisibility(View.VISIBLE);
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
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
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
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
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
        if (isVerify.equals("forgot")) {
            Intent intent = new Intent(context, ForgotPassword.class);
            startActivity(intent);
            finish();
        } else if (isVerify.equals("login")) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
