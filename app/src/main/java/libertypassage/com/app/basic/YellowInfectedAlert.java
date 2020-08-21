package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CenturyGothicEditText;
import libertypassage.com.app.widgets.CenturyGothicTextview;
import libertypassage.com.app.widgets.CustomCheckBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YellowInfectedAlert extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = YellowInfectedAlert.class.getSimpleName();
    private CenturyGothicTextview tv_submit;
    private RelativeLayout rl_goBack;
    private CustomCheckBox checkBox_infected;
    private CenturyGothicEditText et_address;
    private ImageView iv_back;
    private String token, confirm="0", address;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yellow_infected_alert);

        context = YellowInfectedAlert.this;
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        tv_submit = findViewById(R.id.tv_submit);
        checkBox_infected = findViewById(R.id.checkBox_infected);
        et_address = findViewById(R.id.et_address);
        tv_submit = findViewById(R.id.tv_submit);
        rl_goBack = findViewById(R.id.rl_goBack);

        iv_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        rl_goBack.setOnClickListener(this);
    }

    private void init() {
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);

        checkBox_infected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    confirm = "1";
                } else {
                    confirm = "0";
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }

            case R.id.rl_goBack: {
                finish();
                break;
            }

            case R.id.tv_submit: {
                address = et_address.getText().toString().trim();
                if (confirm.equals("0")) {
                    Toast.makeText(context, "Accept confirmation", Toast.LENGTH_LONG).show();
                } else if (address.length() == 0) {
                    Toast.makeText(context, "Please write address", Toast.LENGTH_LONG).show();
                } else if (Utility.isConnectingToInternet(context)) {
                    falseInfectedAlert();
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void falseInfectedAlert() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.falseInfectedAlert(Constants.KEY_HEADER+token, Constants.KEY_BOT, confirm, address);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("InfectedStatus", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {

                    checkBox_infected.setChecked(false);
                    et_address.setText("");
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else if (model != null &&  model.getError().equals(true)) {
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
