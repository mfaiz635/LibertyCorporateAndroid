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
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.verify_otp_forgot.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.DetailsOTP
import libertypassage.com.corporate.model.ModelOTP
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


class VerifyOtpForgot : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var mobile = ""
    private var from = ""
    private var isVerify = ""
    private var bearerToken = ""
    var otp = 0

    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mEndTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp_forgot)

        context = this@VerifyOtpForgot
        dialogProgress = DialogProgress(context)

        iv_back.setOnClickListener(this)
        tv_continue.setOnClickListener(this)
        tv_resendCode.setOnClickListener(this)
        init()
    }

    private fun init() {
        from = intent.getStringExtra("from")!!
        isVerify = Utility.getSharedPreferences(context, "isVerify")!!
        bearerToken = Utility.getSharedPreferences(context, "t_bearer")!!
        mobile = Utility.getSharedPreferences(context, "t_mobile")!!
        otp = Utility.getSharedPreferences(context, "otp")!!.toInt()

        otpView.setOnPinEnteredListener(object : PinEntryEditText.OnPinEnteredListener {
            override fun onPinEntered(str: CharSequence) {
                dismissKeyboard(this@VerifyOtpForgot)
                if (str.toString() == otp.toString()) {
                    Utility.setSharedPreference(context, "t_mobile", "Na")
                    Utility.setSharedPreference(context, "otp", "Na")
                    Utility.setSharedPreference(context, "isVerify", "Na")

                    Toast.makeText(context, "You mobile number verified successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, UpdatePassword::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(context, "Please enter valid otp", Toast.LENGTH_LONG).show()
                }
            }
        })
        if (Objects.requireNonNull(from) == "forgot") {
            startTimer()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                Utility.setSharedPreference(context, "isVerify", "Na")
                val intent = Intent(context, ForgotPassword::class.java)
                startActivity(intent)
                finish()
            }

            R.id.tv_continue -> {
                val sotp = otpView.text.toString().trim()
                if (!TextUtils.isEmpty(sotp)) {
                    if (sotp == otp.toString()) {
                        Utility.setSharedPreference(context, "t_mobile", "Na")
                        Utility.setSharedPreference(context, "otp", "Na")
                        Utility.setSharedPreference(context, "isVerify", "Na")

                        Toast.makeText(context, "You mobile number verified successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, UpdatePassword::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(context, "Please enter correct OTP", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter the OTP", Toast.LENGTH_LONG).show()
                }
            }

            R.id.tv_resendCode -> {
                if (!TextUtils.isEmpty(mobile)) {
                    if (Utility.isConnectingToInternet(context)) {
                        callApiSendOtp(mobile)
                    } else {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Required mobile number", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun callApiSendOtp(mobile: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelOTP> = apiInterface.sendOtp(Constants.KEY_BOT, mobile)

        call.enqueue(object : Callback<ModelOTP?> {
            override fun onResponse(call: Call<ModelOTP?>, response: Response<ModelOTP?>) {
                val modelResponse: ModelOTP? = response.body()
                dialogProgress!!.dismiss()
                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val detailsOTP: DetailsOTP = modelResponse.details!!
                    otp = detailsOTP.otp!!

                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    Utility.setSharedPreference(context, "otp", otp.toString())
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
        val prefs = getSharedPreferences("prefs1", Context.MODE_PRIVATE)
        mTimeLeftInMillis = prefs.getLong("millisLeft1", startTimeInMillis)
        mTimerRunning = prefs.getBoolean("timerRunning1", false)
        updateCountDownText()
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime1", 0)
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
        val prefs = getSharedPreferences("prefs1", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("millisLeft1", mTimeLeftInMillis)
        editor.putBoolean("timerRunning1", mTimerRunning)
        editor.putLong("endTime1", mEndTime)
        editor.apply()
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }


    fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != activity.currentFocus) imm.hideSoftInputFromWindow(activity.currentFocus!!.applicationWindowToken, 0)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Utility.setSharedPreference(context, "isVerify", "Na")
        val intent = Intent(context, ForgotPassword::class.java)
        startActivity(intent)
        finish()
    }
}
