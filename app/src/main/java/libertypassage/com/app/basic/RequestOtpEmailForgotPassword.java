package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import libertypassage.com.app.R;
import libertypassage.com.app.models.DetailsOTP;
import libertypassage.com.app.models.ModelOTP;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestOtpEmailForgotPassword extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private String TAG = RequestOtpEmailForgotPassword.class.getSimpleName();
    private TextView tv_continue;
    private ImageView iv_back;
    private String isVerify, email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_otp_email_forgot_password);

        context = RequestOtpEmailForgotPassword.this;
        findID();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        tv_continue = findViewById(R.id.tv_continue);

        iv_back.setOnClickListener(this);
        tv_continue.setOnClickListener(this);

        email = Utility.getSharedPreferences(context, "t_email");
        isVerify = Utility.getSharedPreferences(context, "isVerify");
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continue: {

                if (!TextUtils.isEmpty(email)) {
                        getOTPResponse(email);
                } else {
                    Toast.makeText(context, "Required Email", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.iv_back: {
                Utility.setSharedPreference(context, "isVerify", "Na");
                if (isVerify.equals("forgotEmail")) {
                    Intent intent = new Intent(context, ForgotPassword.class);
                    startActivity(intent);
                    finish();
                } else if (isVerify.equals("loginEmail")) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                    Intent intent = new Intent(context, VerifyOtpEmailForgotPassword.class);
                    intent.putExtra("from", "forgot");
                    startActivity(intent);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility.setSharedPreference(context, "isVerify", "Na");
        if (isVerify.equals("forgotEmail")) {
            Intent intent = new Intent(context, ForgotPassword.class);
            startActivity(intent);
            finish();
        } else if (isVerify.equals("loginEmail")) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
