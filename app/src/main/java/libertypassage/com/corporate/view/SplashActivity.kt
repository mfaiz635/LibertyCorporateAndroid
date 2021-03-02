package libertypassage.com.corporate.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_splash.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.corporate.EnterContactNumber
import libertypassage.com.corporate.corporate.EnterReferenceNumber
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.utilities.Utility.hasPermissionInManifest
import libertypassage.com.corporate.utilities.Utility.setSharedPreference


class SplashActivity : Activity() {
    private var context: Context? = null
    private val handler = Handler()
    private var token: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    var permission = arrayOf<String?>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION)


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this@SplashActivity
        // get Device Id
        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        setSharedPreference(this, Constants.KEY_DEVICEID, deviceId)
        token = Utility.getSharedPreferences(this, Constants.KEY_BEARER_TOKEN)
        if (hasPermissionInManifest(this@SplashActivity, PERMISSION_CODE, permission)) {
            handler.postDelayed(runnable, 10)
        }

        rl_started.setOnClickListener {
            if (hasPermissionInManifest(
                    this@SplashActivity,
                    PERMISSION_CODE,
                    permission
                )
            ) {
                handler.postDelayed(runnable, TIME_OUT.toLong())
            }
        }
    }

    private var runnable = Runnable {
        Log.e("Splash", "token : $token")
        if (token != "") {
            val start = Utility.getSharedPreferences(
                context!!, Constants.KEY_START
            )
            if (start == "1") {
                startActivity(Intent(context, EnrolmentDeclaration::class.java))
                finish()
            } else if (start == "2") {
                startActivity(Intent(context, Acknowledgement::class.java))
                finish()
            } else if (start == "3") {
                startActivity(Intent(context, MyTemperature::class.java))
                finish()
            } else if (start == "4") {
                startActivity(Intent(context, MyAddress::class.java))
                finish()
            } else if (start == "5") {
                startActivity(Intent(context, HomePage::class.java))
                finish()
            } else {
                startActivity(Intent(context, HomePage::class.java))
                finish()
            }
        } else {
            if (Utility.getSharedPreferences(context!!, "isVerify") == "signup") {
                val i = Intent(context, VerifyOtpSignUp::class.java)
                i.putExtra("from", "splash")
                startActivity(i)
                finish()
            } else if (Utility.getSharedPreferences(context!!, "isVerify") == "refNumber") {
                val i = Intent(context, EnterReferenceNumber::class.java)
                startActivity(i)
                finish()
            } else if (Utility.getSharedPreferences(context!!, "isVerify") == "forgot") {
                val i = Intent(context, VerifyOtpForgot::class.java)
                i.putExtra("from", "splash")
                startActivity(i)
                finish()
            } else {
                val i = Intent(context, EnterContactNumber::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasAllPermissions = false
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    hasAllPermissions = true
                } else {
                    hasAllPermissions = false
                    handler.postDelayed(runnable, 2000)
                }
                if (hasAllPermissions) {
                    handler.postDelayed(runnable, 2000)
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_CODE = 100
    }
}