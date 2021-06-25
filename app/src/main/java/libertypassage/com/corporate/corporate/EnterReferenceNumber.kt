package libertypassage.com.corporate.corporate

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.c_enter_reference_number.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.CorpDetails
import libertypassage.com.corporate.model.CorporateEmployee
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.PinEntryEditText
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnterReferenceNumber : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var mobileNo = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_enter_reference_number)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@EnterReferenceNumber

        dialogProgress = DialogProgress(context)
        mobileNo = Utility.getSharedPreferences(context, Constants.KEY_MOBILE)!!

        iv_back.setOnClickListener(this)
        tv_continue.setOnClickListener(this)
        tv_contactUs.setOnClickListener(this)

        otpView.setOnPinEnteredListener(object : PinEntryEditText.OnPinEnteredListener {
            override fun onPinEntered(str: CharSequence) {
                dismissKeyboard(this@EnterReferenceNumber)
                val otpss = str.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(otpss)) {
                    verifyEmployee(otpss)
                } else {
                    Toast.makeText(context, "Please enter reference number", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                Utility.setSharedPreference(context, "isVerify", "Na")
                val intent = Intent(context, EnterContactNumber::class.java)
                startActivity(intent)
                finish()
            }

            R.id.tv_continue -> {
                val otp = otpView.text.toString().trim()

                if (!TextUtils.isEmpty(otp)) {
                    verifyEmployee(otp)
                } else {
                    Toast.makeText(context, "Please enter reference number", Toast.LENGTH_LONG).show()
                }
            }

            R.id.tv_contactUs -> {
                val intent = Intent(context, ContactUs::class.java)
                startActivity(intent)
            }
        }
    }

    private fun verifyEmployee(otp: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<CorporateEmployee> = apiInterface.verifyEmployee(Constants.KEY_BOT, mobileNo, otp)

        call.enqueue(object : Callback<CorporateEmployee?> {
            override fun onResponse(call: Call<CorporateEmployee?>, response: Response<CorporateEmployee?>) {
                dialogProgress!!.dismiss()
                val responses: CorporateEmployee? = response.body()

                if (responses != null && responses.error.equals(false)) {
                val details = responses.details
                val userExist: Boolean = details!!.userExists
                val corpDetails: CorpDetails = details.corpDetails!!
                Utility.setSharedPreference(context, "isVerify", "Na")
                Utility.setSharedPreference(context,"companyName", corpDetails.companyName)
                Utility.setSharedPreference(context,"companyAddress", corpDetails.companyAddress)
                Utility.setSharedPreference(context, "userExist", userExist.toString())
                Utility.setSharedPreference(context, Constants.KEY_CORPORATION_ID, corpDetails.corpId.toString())

                Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                val intent = Intent(context, ConfirmEmployer::class.java)
                startActivity(intent)

                } else if (responses != null && responses.error.equals(true)) {
                    dialogProgress!!.dismiss()
                    dialogUpdate(responses.message!!)
                }
            }

            override fun onFailure(call: Call<CorporateEmployee?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }


    private fun dialogUpdate(msg: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_error_msg)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val ivCancel = dialog.findViewById(R.id.ivCancel) as ImageView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        tvTitle.text = msg
        ivCancel.setOnClickListener { dialog.dismiss() }
        tvOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupParent(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (currentFocus != null) {
                    val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
                false
            }
        }
        //If a layout container, iterate over children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupParent(innerView)
            }
        }
    }

    fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != activity.currentFocus) imm.hideSoftInputFromWindow(activity.currentFocus!!
                .applicationWindowToken, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Utility.setSharedPreference(context, "isVerify", "Na")
        val intent = Intent(context, EnterContactNumber::class.java)
        startActivity(intent)
        finish()
    }
}
