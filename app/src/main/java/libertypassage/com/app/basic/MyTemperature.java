package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
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
import libertypassage.com.app.widgets.CenturyGothicTextview;
import libertypassage.com.app.widgets.CircleSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyTemperature extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MyTemperature.class.getSimpleName();
    private CenturyGothicTextview tv_next, tv_tempValue, tv_tempTpye, tv_celcius, tv_farenheit;
    private ImageView iv_back;
    private CircleSeekBar mSeekbar;
    private String token, tempType = "Celsius", currentTemp="30.00";
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_temperture);

        context = MyTemperature.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        mSeekbar = findViewById(R.id.seekbar);
        tv_tempValue = findViewById(R.id.tv_tempValue);
        tv_tempTpye = findViewById(R.id.tv_tempTpye);
        tv_celcius = findViewById(R.id.tv_celcius);
        tv_farenheit = findViewById(R.id.tv_farenheit);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        tv_celcius.setOnClickListener(this);
        tv_farenheit.setOnClickListener(this);
        tv_next.setOnClickListener(this);
    }


    private void init() {
        if (tempType.equals("Celsius")) {
            tv_tempTpye.setText("Celsius");
        } else if (tempType.equals("Fahrenheit")) {
            tv_tempTpye.setText("Fahrenheit");
        }
        tv_tempValue.setText(String.valueOf(30.00));

        mSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, double curValue) {
                if (tempType.equals("Celsius")) {
                    tv_tempValue.setText(String.valueOf(curValue+30.00));
                } else if (tempType.equals("Fahrenheit")) {
                    tv_tempValue.setText(String.valueOf(curValue+90.00));
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
            case R.id.tv_next: {
                currentTemp = tv_tempValue.getText().toString();
                double f_temp  = Double.parseDouble(currentTemp);

                if(tempType.equals("Celsius") && f_temp < 35){
                   Toast.makeText(context, "Please select your correct temperature", Toast.LENGTH_LONG).show();
                }else if (tempType.equals("Fahrenheit") && f_temp < 90) {
                   Toast.makeText(context, "Please select your correct temperature", Toast.LENGTH_LONG).show();
                } else {
                    if (Utility.isConnectingToInternet(context)) {

                        Utility.setSharedPreference(context, Constants.KEY_START, "4");
                        Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, currentTemp);
                        Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, tempType);
                        Intent intent = new Intent(context, AddAddress.class);
                        startActivity(intent);
//                        myTemperatureApi();
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

            case R.id.tv_celcius: {
                tempType = "Celsius";
                tv_tempValue.setText(String.valueOf(30.00));
                mSeekbar.setCurProcess(0.00);
                tv_tempTpye.setText("Celsius");
                tv_celcius.setBackground(getResources().getDrawable(R.drawable.rounded_gray_button));
                tv_farenheit.setBackground(getResources().getDrawable(R.drawable.rounded_gray_trans_button));
                break;
            }

            case R.id.tv_farenheit: {
                tempType = "Fahrenheit";
                tv_tempValue.setText(String.valueOf(90.00));
                mSeekbar.setCurProcess(0.00);
                tv_tempTpye.setText("Fahrenheit");
                tv_celcius.setBackground(getResources().getDrawable(R.drawable.rounded_gray_trans_button));
                tv_farenheit.setBackground(getResources().getDrawable(R.drawable.rounded_gray_button));
                break;
            }
        }
    }

    private void myTemperatureApi() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addUserTemp(Constants.KEY_HEADER + token, Constants.KEY_BOT, tempType, currentTemp);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("myTemp", new Gson().toJson(model));

                if (model !=null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    Utility.setSharedPreference(context, Constants.KEY_START, "4");
                    Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, currentTemp);
                    Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, tempType);
                    Intent intent = new Intent(context, AddAddress.class);
                    startActivity(intent);

                } else if (model !=null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

}
