package libertypassage.com.app.basic;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import libertypassage.com.app.R;
import libertypassage.com.app.models.ModelConforme;
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

public class ChangeStatus extends AppCompatActivity implements View.OnClickListener {

    private String TAG = ChangeStatus.class.getSimpleName();
    private Context context;
    private CenturyGothicTextview tv_positive, tv_negative, tv_lastStatusChange, tv_select_date;
    private CenturyGothicEditText et_hospitalName;
    private ImageView iv_back, iv_calender;
    private String check_unfit = "0", check_fit = "0", status="", hospital, selectDate, token,
            dateChangeStatus = "", dateChangeStatus1, oldHospitalAddress, confirmHospitalDate;
    private int statusId;
    private CustomCheckBox checkBox_unfit, checkBox_fit;
    private RelativeLayout rl_goBack;
    private LinearLayout ll_infected, ll_notInfected;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_status);

        context = ChangeStatus.this;
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);
        status = Utility.getSharedPreferences(context, Constants.KEY_STATUS);
        dateChangeStatus = Utility.getSharedPreferences(context, Constants.KEY_CHANGE_STATUS);
        oldHospitalAddress = Utility.getSharedPreferences(context, Constants.KEY_HOSPITAL_ADD);
        confirmHospitalDate = Utility.getSharedPreferences(context, Constants.KEY_HOSPITAL_DATE);
        statusId = getIntent().getIntExtra("statusId", 0);
//        Log.e("statusId", statusId+"");
//        Log.e("status", status);
//        Log.e("dateChangeStatus", dateChangeStatus);
//        Log.e("oldHospitalAddress", oldHospitalAddress);
//        Log.e("confirmHospitalDate", confirmHospitalDate);

        findID();
        init();
    }


    private void findID() {
        iv_back = findViewById(R.id.iv_back);
        tv_lastStatusChange = findViewById(R.id.tv_lastStatusChange);
        ll_infected = findViewById(R.id.ll_infected);
        ll_notInfected = findViewById(R.id.ll_notInfected);
        checkBox_unfit = findViewById(R.id.checkBox_unfit);
        checkBox_fit = findViewById(R.id.checkBox_fit);
        et_hospitalName = findViewById(R.id.et_hospitalName);
        tv_select_date = findViewById(R.id.tv_select_date);
        iv_calender = findViewById(R.id.iv_calender);
        tv_positive = findViewById(R.id.tv_positive);
        tv_negative = findViewById(R.id.tv_negative);
        rl_goBack = findViewById(R.id.rl_goBack);

        iv_back.setOnClickListener(this);
        tv_select_date.setOnClickListener(this);
        iv_calender.setOnClickListener(this);
        tv_positive.setOnClickListener(this);
        tv_negative.setOnClickListener(this);
        rl_goBack.setOnClickListener(this);

        if(status.equals("0")){
            checkBox_fit.setChecked(true);
            checkBox_unfit.setChecked(false);
        }else if(status.equals("1")){
            checkBox_fit.setChecked(false);
            checkBox_unfit.setChecked(true);
        }

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        try {
            date = inputFormat.parse(dateChangeStatus);
            dateChangeStatus1 = outputFormat.format(date);
//            holder.tv_time.setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (statusId == 3) {    // green status
            tv_positive.setVisibility(View.VISIBLE);
            tv_negative.setVisibility(View.GONE);

            if (!dateChangeStatus.isEmpty()) {
                tv_lastStatusChange.setVisibility(View.VISIBLE);
                tv_lastStatusChange.setText("Confirmed 'Covid-19 negative' on " + dateChangeStatus1);
                et_hospitalName.setText(oldHospitalAddress);
                tv_select_date.setText(confirmHospitalDate);
            } else {
                tv_lastStatusChange.setVisibility(View.GONE);
            }
        } else if (statusId == 1) {   // yellow status
            tv_positive.setVisibility(View.VISIBLE);
            tv_negative.setVisibility(View.GONE);

            if (!dateChangeStatus.isEmpty()) {
                tv_lastStatusChange.setVisibility(View.VISIBLE);
                tv_lastStatusChange.setText("Last Status Changed on " + dateChangeStatus1);
                et_hospitalName.setText(oldHospitalAddress);
                tv_select_date.setText(confirmHospitalDate);
            } else {
                tv_lastStatusChange.setVisibility(View.GONE);
            }

        } else if (statusId == 2) {   // red status
            tv_positive.setVisibility(View.GONE);
            tv_negative.setVisibility(View.VISIBLE);

            if (!dateChangeStatus.isEmpty()) {
                tv_lastStatusChange.setVisibility(View.VISIBLE);
                tv_lastStatusChange.setText("Confirmed 'Covid-19 positive' on " + dateChangeStatus1);
                et_hospitalName.setText(oldHospitalAddress);
                tv_select_date.setText(confirmHospitalDate);
            } else {
                tv_lastStatusChange.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        checkBox_unfit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status="1";
                    check_unfit = "1";
                    et_hospitalName.setEnabled(true);
                    et_hospitalName.setSelection(et_hospitalName.length());
                    checkBox_fit.setChecked(false);
                    tv_positive.setVisibility(View.VISIBLE);
                    tv_negative.setVisibility(View.GONE);
                }
            }
        });

        checkBox_fit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status="0";
                    check_fit = "1";
                    check_unfit = "0";
                    et_hospitalName.setEnabled(true);
                    et_hospitalName.setSelection(et_hospitalName.length());
                    checkBox_unfit.setChecked(false);
                    tv_positive.setVisibility(View.GONE);
                    tv_negative.setVisibility(View.VISIBLE);
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

            case R.id.tv_positive: {
                hospital = et_hospitalName.getText().toString().trim();
                selectDate = tv_select_date.getText().toString().trim();
                Log.e("paramer", hospital + "/" + selectDate);

                if (check_unfit.equals("0")) {
                    Toast.makeText(context, "Accept confirmation", Toast.LENGTH_LONG).show();
                } else if (hospital.length() == 0) {
                    Toast.makeText(context, "Please write hospital details", Toast.LENGTH_LONG).show();
                } else if (selectDate.equals("Select Date")) {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_LONG).show();
                } else if (Utility.isConnectingToInternet(context)) {
                    changeStatusApi("1");
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.tv_negative: {
                hospital = et_hospitalName.getText().toString().trim();
                selectDate = tv_select_date.getText().toString().trim();

                if (check_fit.equals("0")) {
                    Toast.makeText(context, "Accept confirmation", Toast.LENGTH_LONG).show();
                } else if (hospital.length() == 0) {
                    Toast.makeText(context, "Please write hospital details", Toast.LENGTH_LONG).show();
                } else if (selectDate.equals("Select Date")) {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_LONG).show();
                } else if (Utility.isConnectingToInternet(context)) {
                    changeStatusApi("0");
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.tv_select_date: {
                dialogDatePicker();
                break;
            }
            case R.id.iv_calender: {
//                ShowDatePicker();
                dialogDatePicker();
                break;
            }
        }
    }

    public void ShowDatePicker() {
//        privious date block calender
//        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                String myFormat = "dd/MM/yyyy"; //In which you need put here
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                tv_select_date.setText(sdf.format(myCalendar.getTime()));
//            }
//        }, years, months, days);
//        //disaple past date
//        dpd.getDatePicker().setMinDate(new Date().getTime());
//        dpd.show();

        // To show current date in the datepicker
        final Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker = new DatePickerDialog(
                ChangeStatus.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat((myFormat), Locale.US);
                tv_select_date.setText(sdf.format(mcurrentDate.getTime()));
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void dialogDatePicker() {
        Dialog dialog;
        dialog = new Dialog(ChangeStatus.this);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView cancel = dialog.findViewById(R.id.cancel);
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        CenturyGothicTextview tv_notAllow = dialog.findViewById(R.id.tv_notAllow);
        CenturyGothicTextview tv_ok = dialog.findViewById(R.id.tv_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_notAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_select_date.setText(datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changeStatusApi(String positive) {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelConforme> call = apiInterface.changeStatus(Constants.KEY_HEADER+token, Constants.KEY_BOT, positive, hospital, selectDate);

        call.enqueue(new Callback<ModelConforme>() {
            @Override
            public void onResponse(Call<ModelConforme> call, Response<ModelConforme> response) {
                Utility.stopProgressDialog(context);
                ModelConforme model = response.body();
                Log.e("modelChangeStaus", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {
                    
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = df.format(date);

                    Utility.setSharedPreference(context, Constants.KEY_STATUS, status);
                    Utility.setSharedPreference(context, Constants.KEY_CHANGE_STATUS, currentDate);
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_ADD, hospital);
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_DATE, selectDate);

                    Log.e(" 4545", currentDate);
                    Intent intent = new Intent(context, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

//                    checkBox_fit.setChecked(false);
//                    checkBox_unfit.setChecked(false);
//                    et_hospitalName.setText("");
//                    tv_select_date.setText("Select Date");

                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelConforme> call, Throwable t) {
                Utility.stopProgressDialog(context);
//                Log.e("model", "onFailure    " + t.getMessage());
//                Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
