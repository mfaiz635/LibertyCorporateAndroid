package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class UpdateMyTemperature extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = UpdateMyTemperature.class.getSimpleName();
    private LinearLayout rootLayout;
    private CenturyGothicTextview tv_tempValue, tv_tempTpye, tv_next, tv_celcius, tv_farenheit;
    private ImageView iv_back;
    private CircleSeekBar mSeekbar;
    private String token, from, tempType = "Celsius", currentTemp = "30.00";
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_my_temperture);

        context = UpdateMyTemperature.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        tempType = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE);
        currentTemp = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP);
        from = getIntent().getStringExtra("from");
        findID();
        init();
    }


    private void findID() {
        rootLayout = findViewById(R.id.rootLayout);
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
        tv_tempTpye.setText(tempType);
        tv_tempValue.setText(currentTemp);

        if (tempType.equals("Celsius")) {
            double temps = Double.parseDouble(currentTemp) - 30.00;
            mSeekbar.setCurProcess(temps);
            tv_celcius.setBackground(getResources().getDrawable(R.drawable.rounded_gray_button));
            tv_farenheit.setBackground(getResources().getDrawable(R.drawable.rounded_gray_trans_button));
        } else if (tempType.equals("Fahrenheit")) {
            double temps = Double.parseDouble(currentTemp) - 90.00;
            mSeekbar.setCurProcess(temps);
            tv_celcius.setBackground(getResources().getDrawable(R.drawable.rounded_gray_trans_button));
            tv_farenheit.setBackground(getResources().getDrawable(R.drawable.rounded_gray_button));
        }


        mSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, double curValue) {
                if (tempType.equals("Celsius")) {
                    tv_tempValue.setText(String.valueOf(curValue + 30.00));
                } else if (tempType.equals("Fahrenheit")) {
                    tv_tempValue.setText(String.valueOf(curValue + 90.00));
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
                float f_temp = Float.parseFloat(currentTemp);
                Log.e("currentTemp", currentTemp);

                if (tempType.equals("Celsius") && f_temp < 35) {
                    Toast.makeText(context, "Please select your correct temperature", Toast.LENGTH_LONG).show();
                } else if (tempType.equals("Fahrenheit") && f_temp < 90) {
                    Toast.makeText(context, "Please select your correct temperature", Toast.LENGTH_LONG).show();
                } else {
                        Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, currentTemp);
                        Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, tempType);

                        Intent intent = new Intent(context, UpdateAddress.class);
                        intent.putExtra("from", from);
                        startActivity(intent);
                        finish();
//                        if (!from.equals("home")) {
//                            myTemperatureApi();
//                        } else {
//                            Intent intent = new Intent(context, UpdateAddress.class);
//                            intent.putExtra("from", "home");
//                            startActivity(intent);
//                        }
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

                if (model != null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    intent.putExtra("currentTemp", currentTemp);
                    intent.putExtra("tempType", tempType);
                    setResult(111, intent);
                    finish();

                } else if (model != null && model.getError().equals(true)) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
