package libertypassage.com.app.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelResponse;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import libertypassage.com.app.widgets.CustomCheckBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrolmentDeclaration extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = EnrolmentDeclaration.class.getSimpleName();
    private Context context;
    private TextView tv_next;
    private ImageView iv_back;
    private CustomCheckBox checkBox_terms1, checkBox_terms2, checkBox_terms3, checkBox_terms4;
    private String token, terms1 = "1", terms2 = "1", terms3 = "1", terms4 = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enrolment_declaration);

        context = EnrolmentDeclaration.this;
        findID();
        init();
    }

    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        checkBox_terms1 = findViewById(R.id.checkBox_terms1);
        checkBox_terms2 = findViewById(R.id.checkBox_terms2);
        checkBox_terms3 = findViewById(R.id.checkBox_terms3);
        checkBox_terms4 = findViewById(R.id.checkBox_terms4);
        tv_next = findViewById(R.id.tv_next);

        iv_back.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
    }

    private void init() {
        checkBox_terms1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    terms1 = "1";
                } else {
                    terms1 = "0";
                }
            }
        });

        checkBox_terms2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    terms2 = "1";
                } else {
                    terms2 = "0";
                }
            }
        });

        checkBox_terms3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    terms3 = "1";
                } else {
                    terms3 = "0";
                }
            }
        });

        checkBox_terms4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    terms4 = "1";
                } else {
                    terms4 = "0";
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
                Log.e("terms", terms1 + terms2 + terms3 + terms4);
                if (!terms1.equals("1")) {
                    Toast.makeText(context, "Accept of First Declaration", Toast.LENGTH_LONG).show();
                } else if (!terms2.equals("1")) {
                    Toast.makeText(context, "Accept of Second Declaration", Toast.LENGTH_LONG).show();
                } else if (!terms3.equals("1")) {
                    Toast.makeText(context, "Accept of Third Declaration", Toast.LENGTH_LONG).show();
                } else if (!terms4.equals("1")) {
                    Toast.makeText(context, "Accept of Forth Declaration", Toast.LENGTH_LONG).show();
                } else {
                    if (Utility.isConnectingToInternet(context)) {
                        enrollApiResponse();
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


    private void enrollApiResponse() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelResponse> call = apiInterface.addUserEnrollDeclare(Constants.KEY_HEADER + token, Constants.KEY_BOT, terms1, terms2, terms3, terms4);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                Utility.stopProgressDialog(context);
                ModelResponse model = response.body();
                Log.e("modelEnrollment", new Gson().toJson(model));

                if (model !=null && model.getError().equals(false)) {
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    Utility.setSharedPreference(context, Constants.KEY_START, "2");

                    Intent intent = new Intent(context, Acknowledgement.class);
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
