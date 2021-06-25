package libertypassage.com.corporate.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.verify_otp_signup.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelOTP
import libertypassage.com.corporate.model.ModelSignUp
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.PinEntryEditText
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*



class VerifyOtpSignUp : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var otp = ""
    private var from = ""

    private var email = ""
    private var mobile = ""
    private var gender = ""
    private var ageGroup = ""
    private var profession = ""
    private var countryId = ""
    private var industryId = ""
    private var password = ""
    private var firebaseToken = ""
    private var corporationId = ""

    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mEndTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp_signup)
        context = this@VerifyOtpSignUp

        dialogProgress = DialogProgress(context)
        from = intent.getStringExtra("from")!!
        otp = Utility.getSharedPreferences(context, "otp")!!
        email = Utility.getSharedPreferences(context, Constants.KEY_EMAIL)!!
        mobile = Utility.getSharedPreferences(context, Constants.KEY_MOBILE)!!
        gender = Utility.getSharedPreferences(context, Constants.KEY_GENDER)!!
        ageGroup = Utility.getSharedPreferences(context, Constants.KEY_AGE_GROUP)!!
        profession = Utility.getSharedPreferences(context, Constants.KEY_PROF)!!
        countryId = Utility.getSharedPreferences(context, Constants.KEY_COUNTRY_ID)!!
        industryId = Utility.getSharedPreferences(context, Constants.KEY_INDUSTRY)!!
        corporationId = Utility.getSharedPreferences(context, Constants.KEY_CORPORATION_ID)!!
        password = Utility.getSharedPreferences(context, "password")!!
        firebaseToken = Utility.getSharedPreferences(context, "firebaseToken")!!



        iv_back.setOnClickListener(this)
        tv_continue.setOnClickListener(this)
        tv_resendCode.setOnClickListener(this)

        otpView.setOnPinEnteredListener(object : PinEntryEditText.OnPinEnteredListener {
            override fun onPinEntered(str: CharSequence) {
                dismissKeyboard(this@VerifyOtpSignUp)
                if (str.toString() == otp) {
                    if (Utility.isConnectingToInternet(context)) {
                        callApiSignUp()
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show()
                }
            }
        })


        if (Objects.requireNonNull(from) == "signup") {
            startTimer()
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                Utility.setSharedPreference(context, "isVerify", "Na")
                val intent = Intent(context, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.tv_continue -> {
                val otps = otpView.text.toString().trim()

                if (!TextUtils.isEmpty(otps)) {
                    if (otps == otp) {
                        if (Utility.isConnectingToInternet(context)) {
                            callApiSignUp()
                        } else {
                            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter the otp", Toast.LENGTH_LONG).show()
                }

            }


            R.id.tv_resendCode -> {
                if (!TextUtils.isEmpty(mobile)) {
                    if (Utility.isConnectingToInternet(context)) {
                        callApiSendOtp()
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "Required mobile number", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun callApiSignUp() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelSignUp> = apiInterface.userSignUp(Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE,
            email, mobile, gender, ageGroup, industryId, profession, countryId,  password,
            firebaseToken, corporationId, "0")


        Log.e("corporationId", corporationId)




        call.enqueue(object : Callback<ModelSignUp?> {
            override fun onResponse(call: Call<ModelSignUp?>, response: Response<ModelSignUp?>) {
                val modelResponse: ModelSignUp? = response.body()
                dialogProgress!!.dismiss()
                Log.e("ModelSignUp", Gson().toJson(modelResponse))
                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val signUpDetails = modelResponse.details
                    val bearerToken = signUpDetails?.bearerToken

                    Utility.setSharedPreference(context, Constants.KEY_BEARER_TOKEN, bearerToken)
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL_VERIFIED, "false")
                    Utility.setSharedPreference(context, Constants.KEY_START, "1")
                    Utility.setSharedPreference(context, "isVerify", "Na")
                    Utility.setSharedPreference(context, "password", "Na")
                    Utility.setSharedPreference(context, "firebaseToken", "Na")
                    Utility.setSharedPreference(context, "otp", "Na")

                    val intent = Intent(context, EnrolmentDeclaration::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelSignUp?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun callApiSendOtp() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelOTP> = apiInterface.sendOtp(Constants.KEY_BOT, mobile)

        call.enqueue(object : Callback<ModelOTP?> {
            override fun onResponse(call: Call<ModelOTP?>, response: Response<ModelOTP?>) {
                val modelResponse: ModelOTP? = response.body()
                dialogProgress!!.dismiss()
                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val detailsOTP = modelResponse.details
                    otp = detailsOTP?.otp.toString()

                    Log.e("otp", otp)
                    Utility.setSharedPreference(context, "otp", otp)
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    ll_resend.visibility = View.GONE
                    tv_timer.visibility = View.VISIBLE
                    mTimeLeftInMillis = startTimeInMillis
                    updateCountDownText()
                    startTimer()

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelOTP?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                mCountDownTimer!!.cancel()
                tv_timer.visibility = View.GONE
                ll_resend.visibility = View.VISIBLE
            }
        }.start()
        mTimerRunning = true
        tv_timer.visibility = View.VISIBLE
        ll_resend.visibility = View.GONE
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tv_timer.text = timeLeftFormatted
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("prefs_signup1", MODE_PRIVATE)
        mTimeLeftInMillis = prefs.getLong("millisLeft_signup1", startTimeInMillis)
        mTimerRunning = prefs.getBoolean("timerRunning_signup1", false)
        updateCountDownText()
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime_signup1", 0)
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis()
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0
                mTimerRunning = false
                updateCountDownText()
            } else {
                startTimer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mTimerRunning = true
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("prefs_signup1", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("millisLeft_signup1", mTimeLeftInMillis)
        editor.putBoolean("timerRunning_signup1", mTimerRunning)
        editor.putLong("endTime_signup1", mEndTime)
        editor.apply()
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }


    fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != activity.currentFocus) imm.hideSoftInputFromWindow(
            activity.currentFocus!!
                .applicationWindowToken, 0
        )
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Utility.setSharedPreference(context, "isVerify", "Na")
        val intent = Intent(context, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}
