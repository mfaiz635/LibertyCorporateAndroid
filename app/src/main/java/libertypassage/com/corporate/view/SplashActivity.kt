package libertypassage.com.corporate.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.dialog_runtime_permission.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.corporate.EnterContactNumber
import libertypassage.com.corporate.corporate.EnterReferenceNumber
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Constants.Companion.PERMISSION_CODE
import libertypassage.com.corporate.utilities.CustomCheckBox
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.utilities.Utility.hasPermissionInManifest


class SplashActivity : Activity() {
    private lateinit var context: Context
    private val handler = Handler()
    private var token: String? = null
    private var termsAccept: String? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    var permission = arrayOf<String?>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    private var mAppUpdateManager: AppUpdateManager? = null
    private val appUpdateCode = 110



    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this@SplashActivity
        token = Utility.getSharedPreferences(this, Constants.KEY_BEARER_TOKEN)
        termsAccept = Utility.getSharedPreferences(this, Constants.KEY_TERMS_ACCEPT)

//        if(termsAccept.equals("1")){
//            if (hasPermissionInManifest(this@SplashActivity, PERMISSION_CODE, permission)) {
//                handler.postDelayed(runnable, 10)
//            }
//        }else{
//            dialogTerms()
//        }

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
            val start = Utility.getSharedPreferences(context, Constants.KEY_START )
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
            if (Utility.getSharedPreferences(context, "isVerify") == "signup") {
                val i = Intent(context, VerifyOtpSignUp::class.java)
                i.putExtra("from", "splash")
                startActivity(i)
                finish()
            } else if (Utility.getSharedPreferences(context, "isVerify") == "refNumber") {
                val i = Intent(context, EnterReferenceNumber::class.java)
                startActivity(i)
                finish()
            } else if (Utility.getSharedPreferences(context, "isVerify") == "forgot") {
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 110) {
            if (resultCode == RESULT_CANCELED) {
                checkUpdate()
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(context,"App have been update successfully....!", Toast.LENGTH_LONG).show()
            } else {
                checkUpdate()
            }
        }
    }

    private fun checkUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager!!.registerListener(installStateUpdatedListener)

        mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    mAppUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@SplashActivity,
                        appUpdateCode
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackBarForCompleteUpdate()
            } else {
                Log.e("checkForAppUpdate: ", "NewUpdateNotAvailable")
                if(termsAccept.equals("1")){
                    if (hasPermissionInManifest(this@SplashActivity, PERMISSION_CODE, permission)) {
                        handler.postDelayed(runnable, 10)
                    }
                }else{
                    dialogTerms()
                }
            }
        }
    }

    private var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackBarForCompleteUpdate()
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager!!.unregisterListener(this)
                    }
                } else {
                    Log.e("InstallUpdated: ", ""+state.installStatus())
                }
            }
        }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(findViewById(R.id.rlMain),
            "New app is ready!",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Install") { view: View? ->
            if (mAppUpdateManager != null) {
                mAppUpdateManager!!.completeUpdate()
            }
        }
        snackBar.setActionTextColor(resources.getColor(R.color.colorPrimaryDark))
        snackBar.show()
    }

    override fun onStart() {
        super.onStart()
        checkUpdate()
    }

    override fun onStop() {
        super.onStop()
        if (mAppUpdateManager != null) {
            mAppUpdateManager!!.unregisterListener(installStateUpdatedListener);
        }
    }

    private fun dialogTerms() {
        val dialog = Dialog(this@SplashActivity)
        dialog.setContentView(R.layout.dialog_terms_splash)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val checkBoxTerms1 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms1)
        val checkBoxTerms2 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms2)
        val checkBoxTerms3 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms3)
        val checkBoxTerms4 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms4)
        val checkBoxTerms5 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms5)
        val checkBoxTerms6 = dialog.findViewById<CustomCheckBox>(R.id.checkBoxTerms6)
        val tvSafeEntry = dialog.findViewById<TextView>(R.id.tvSafeEntry)
        val tvPrivacyPolicy = dialog.findViewById<TextView>(R.id.tvPrivacyPolicy)
        val tvNotAgree = dialog.findViewById<TextView>(R.id.tvNotAgree)
        val tvAgree = dialog.findViewById<TextView>(R.id.tvAgree)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        var terms1 = "1"
        var terms2 = "1"
        var terms3 = "1"
        var terms4 = "1"
        var terms5 = "1"
        var terms6 = "1"

        checkBoxTerms1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms1 = "1"
                if (terms2 == "1" && terms3 == "1" && terms4 == "1" && terms5 == "1" && terms6 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms1 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        checkBoxTerms2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms2 = "1"
                if (terms1 == "1" && terms3 == "1" && terms4 == "1" && terms5 == "1" && terms6 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms2 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        checkBoxTerms3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms3 = "1"
                if (terms2 == "1" && terms1 == "1" && terms4 == "1" && terms5 == "1" && terms6 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms3 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        checkBoxTerms4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms4 = "1"
                if (terms1 == "1" && terms2 == "1" && terms3 == "1" && terms5 == "1" && terms6 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms4 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        checkBoxTerms5.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms5 = "1"
                if (terms1 == "1" && terms2 == "1" && terms3 == "1" && terms4 == "1" && terms6 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms5 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        checkBoxTerms6.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms6 = "1"
                if (terms1 == "1" && terms2 == "1" && terms3 == "1" && terms4 == "1" && terms5 == "1") {
                    tvNotAgree.visibility = View.GONE
                    tvAgree.visibility = View.VISIBLE
                } else {
                    tvNotAgree.visibility = View.VISIBLE
                    tvAgree.visibility = View.GONE
                }
            } else {
                terms6 = "0"
                tvNotAgree.visibility = View.VISIBLE
                tvAgree.visibility = View.GONE
            }
        }

        tvSafeEntry.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "https://www.safeentry.gov.sg/")
            startActivity(intent)
        }

        tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "https://passageliberty.azurewebsites.net/privacypolicy")
            startActivity(intent)
        }

        tvAgree.setOnClickListener {
            if (terms1 != "1") {
                Toast.makeText(context, "Accept of First Privacy Policy", Toast.LENGTH_LONG).show()
            } else if (terms2 != "1") {
                Toast.makeText(context, "Accept of Second Privacy Policy", Toast.LENGTH_LONG).show()
            } else if (terms3 != "1") {
                Toast.makeText(context, "Accept of Third Privacy Policy", Toast.LENGTH_LONG).show()
            } else if (terms4 != "1") {
                Toast.makeText(context, "Accept of Forth Privacy Policy", Toast.LENGTH_LONG).show()
            } else if (terms5 != "1") {
                Toast.makeText(context, "Accept of Fifth Privacy Policy", Toast.LENGTH_LONG).show()
            } else if (terms6 != "1") {
                Toast.makeText(context, "Accept of Six Privacy Policy", Toast.LENGTH_LONG).show()
            } else {
                Utility.setSharedPreference(this@SplashActivity, Constants.KEY_TERMS_ACCEPT, "1")
                if (hasPermissionInManifest(this@SplashActivity, PERMISSION_CODE, permission)) {
                    handler.postDelayed(runnable, 10)
                }
            }
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }
}