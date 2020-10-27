package libertypassage.com.app.basic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import libertypassage.com.app.R;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelCountryList;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.Q)
public class SplashActivity extends Activity {

    private Context context;
    private Handler handler = new Handler();
    private String TAG = SplashActivity.class.getSimpleName();
    private List<CountryDetail> countryDetails = new ArrayList<CountryDetail>();
    private ArrayList<DetailIndustryProf> ipArrayList = new ArrayList<DetailIndustryProf>();
    private String token;
    private static final int PERMISSION_CODE = 100;
    String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;
        int TIME_OUT = 10;

        // get Device Id
        String DeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Utility.setSharedPreference(context, Constants.KEY_DEVICEID, DeviceId);
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);

        if (Utility.hasPermissionInManifest(SplashActivity.this, PERMISSION_CODE, permission)) {
            handler.postDelayed(runnable, TIME_OUT);
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("Splash", "token : " + token);
            if (!token.equals("")) {
                String start = Utility.getSharedPreferences(context, Constants.KEY_START);
                if (start.equals("1")) {
                    startActivity(new Intent(context, EnrolmentDeclaration.class));
                    finish();
                } else if (start.equals("2")) {
                    startActivity(new Intent(context, Acknowledgement.class));
                    finish();
                } else if (start.equals("3")) {
                    startActivity(new Intent(context, MyTemperature.class));
                    finish();
                } else if (start.equals("4")) {
                    startActivity(new Intent(context, AddAddress.class));
                    finish();
                } else if (start.equals("6")) {
                    startActivity(new Intent(context, VerifyOtpEmailUpadteProfile.class).putExtra("from", "splash"));
                    finish();
                } else if (start.equals("5")) {
                    startActivity(new Intent(context, HomePage.class));
                    finish();
                } else {
                    startActivity(new Intent(context, HomePage.class));
                    finish();
                }
            } else {
                if (Utility.getSharedPreferences(context, "isVerify").equals("signup")) {
                    Intent i = new Intent(context, VerifyOTPSignUp.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("otpEmail")) {
                    Intent i = new Intent(context, VerifyOtpEmailSignUp.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("forgot")) {
                    Intent i = new Intent(context, VerifyOTPForgotPassword.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("forgotEmail")) {
                    Intent i = new Intent(context, VerifyOtpEmailForgotPassword.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasAllPermissions = false;
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    hasAllPermissions = true;
                } else {
                    hasAllPermissions = false;
                    handler.postDelayed(runnable, 2000);
                }

                if (hasAllPermissions) {
                    handler.postDelayed(runnable, 2000);
                }
                break;
        }
    }
}
