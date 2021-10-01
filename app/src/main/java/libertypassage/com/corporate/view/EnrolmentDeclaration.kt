package libertypassage.com.corporate.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import libertypassage.com.corporate.R
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import kotlinx.android.synthetic.main.enrolment_declaration.*
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.DialogProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnrolmentDeclaration : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var terms1 = "1"
    private var terms2 = "1"
    private var terms3 = "1"
    private var terms4 = "1"
    private var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enrolment_declaration)
        context = this@EnrolmentDeclaration

        dialogProgress = DialogProgress(context)
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!

        tvTerms1.setOnClickListener(this)
        tvTerms2.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        init()
    }

    private fun init() {
        checkBox_terms1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms1 = "1"
            } else {
                terms1 = "0"
            }
        }
        checkBox_terms2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms2 = "1"
            } else {
                terms2 = "0"
            }
        }
        checkBox_terms3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms3 = "1"
            } else {
                terms3 = "0"
            }
        }
        checkBox_terms4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                terms4 = "1"
            } else {
                terms4 = "0"
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvTerms1 -> {
                dialogTerms()
            }
            R.id.tvTerms2 -> {
                val intent = Intent(this, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://passageliberty.azurewebsites.net/privacypolicy")
                startActivity(intent)
            }
            R.id.tv_next -> {
                Log.e("terms", terms1 + terms2 + terms3 + terms4)
                if (terms1 != "1") {
                    Toast.makeText(context, "Accept of Personal Data Declaration", Toast.LENGTH_LONG).show()
                } else if (terms2 != "1") {
                    Toast.makeText(context, "Accept of Privacy Policy Declaration", Toast.LENGTH_LONG).show()
//                } else if (terms3 != "1") {
//                    Toast.makeText(context, "Accept of Third Declaration", Toast.LENGTH_LONG).show()
//                } else if (terms4 != "1") {
//                    Toast.makeText(context, "Accept of Fourth Declaration", Toast.LENGTH_LONG).show()
                } else {
                    if (Utility.isConnectingToInternet(context)) {
                        enrollApiResponse()
                    } else {
                        Toast.makeText(context, R.string.connectInternet, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun enrollApiResponse() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelResponse> = apiInterface.enrollDeclare(Constants.KEY_HEADER+token,
            Constants.KEY_BOT, terms1, terms2, terms3, terms4)
        call.enqueue(object : Callback<ModelResponse?> {
            override fun onResponse(call: Call<ModelResponse?>, response: Response<ModelResponse?>) {
                val modelResponse: ModelResponse? = response.body()
                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    Utility.setSharedPreference(context, Constants.KEY_START, "2")

                    val intent = Intent(context, Acknowledgement::class.java)
                    startActivity(intent)

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun dialogTerms() {
        val dialog = Dialog(this@EnrolmentDeclaration)
        dialog.setContentView(R.layout.dialog_terms_about)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val tvSafeEntry = dialog.findViewById<TextView>(R.id.tvSafeEntry)
        val tvPrivacyPolicy = dialog.findViewById<TextView>(R.id.tvPrivacyPolicy)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)

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

        tvOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}



