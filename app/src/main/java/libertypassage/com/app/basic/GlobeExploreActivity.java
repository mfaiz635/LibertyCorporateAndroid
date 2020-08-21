package libertypassage.com.app.basic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GlobeExploreActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = GlobeExploreActivity.class.getSimpleName();
    private CenturyGothicEditText et_searchCountry;
    private ImageView iv_back, iv_globe;
    private String token, from;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glob_explore_activity);

        context = GlobeExploreActivity.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        et_searchCountry = findViewById(R.id.et_searchCountry);
        iv_globe = findViewById(R.id.iv_globe);

        iv_back.setOnClickListener(this);
        iv_globe.setOnClickListener(this);
    }

    private void init() {
        from = getIntent().getStringExtra("from");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
//                if (!from.equals("home")) {
//                    finish();
//                } else {
//                    Intent intent = new Intent(context, HomePage.class);
//                    startActivity(intent);
//                    finish();
//                }
                break;
            }
            case R.id.iv_globe: {
//                currentTemp = tv_tempValue.getText().toString();
//                float f_temp = Float.parseFloat(currentTemp);
//                Log.e("currentTemp", currentTemp);
//                if (f_temp == 0) {
//                    Toast.makeText(context, "Please select your temperature", Toast.LENGTH_LONG).show();
//                } else if (f_temp <= 30) {
//                    Toast.makeText(context, "Please select your correct temperature", Toast.LENGTH_LONG).show();
//                } else {
//                    if (Utility.isConnectingToInternet(context)) {
//                        myTemperatureApi();
//                    } else {
//                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
//                    }
//                }
                break;
            }
        }
    }

    private void myTemperatureApi() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addUserTemp(Constants.KEY_HEADER + token, Constants.KEY_BOT, "tempType", "currentTemp");

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("myTemp", new Gson().toJson(model));

                if (model != null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();



                } else if (model.getError().equals(true)) {
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
//        if (!from.equals("home")) {
//            finish();
//        } else {
//            Intent intent = new Intent(context, HomePage.class);
//            startActivity(intent);
//            finish();
//        }
        finish();
    }
}
