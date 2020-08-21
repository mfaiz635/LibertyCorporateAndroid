package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.regex.Pattern;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdatePassword extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private CenturyGothicEditText et_password;
    private CenturyGothicTextview submit_btn;
    private ImageView pass_view, pass_hide;
    private String bearer_token, error_msg = "Required password";
    private Context context;
    private final String TAG = UpdatePassword.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password);

        context = UpdatePassword.this;
        findID();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        et_password = findViewById(R.id.et_password);
        pass_view = findViewById(R.id.pass_view);
        pass_hide = findViewById(R.id.pass_hide);
        submit_btn = findViewById(R.id.submit_btn);

        iv_back.setOnClickListener(this);
        pass_view.setOnClickListener(this);
        pass_hide.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        bearer_token = Utility.getSharedPreferences(context, "t_bearer");
        Log.e("bearer_token", bearer_token);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_btn: {
                String password = et_password.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    if (password.length() > 5) {
                        if (Pattern.compile("[A-Z ]").matcher(password).find()) {

                            if (Utility.isConnectingToInternet(context)) {
                                updatePassword(password);
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
            }
            break;

            case R.id.iv_back: {
                finish();
            }
            break;

            case R.id.pass_view: {
                pass_hide.setVisibility(View.VISIBLE);
                pass_view.setVisibility(View.GONE);
                et_password.setTransformationMethod(null);
                et_password.setSelection(et_password.length());
            }
            break;

            case R.id.pass_hide: {
                pass_view.setVisibility(View.VISIBLE);
                pass_hide.setVisibility(View.GONE);
                et_password.setTransformationMethod(new PasswordTransformationMethod());
                et_password.setSelection(et_password.length());
            }
            break;

        }
    }

    private void updatePassword(final String password) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.updateUserPassword(Constants.KEY_HEADER + bearer_token, Constants.KEY_BOT, password);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("model", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

//                    Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearer_token);
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(context, "Your password update successfully please login", Toast.LENGTH_LONG).show();

                    
                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
