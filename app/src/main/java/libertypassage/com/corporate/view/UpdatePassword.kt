package libertypassage.com.corporate.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.update_password.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class UpdatePassword : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_password)
        context = this@UpdatePassword

        token = Utility.getSharedPreferences(context, "t_bearer")!!
        dialogProgress = DialogProgress(context)

        iv_back.setOnClickListener(this)
        pass_view.setOnClickListener(this)
        pass_hide.setOnClickListener(this)
        submit_btn.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.submit_btn -> {
                val password = et_password.text.toString()
                if (!TextUtils.isEmpty(password)) {
                    if (password.length > 5) {
                        if (Pattern.compile("[A-Z ]").matcher(password).find()) {
                            if (Utility.isConnectingToInternet(context)) {
                                callApiUpdatePassword(password)
                            } else {
                                Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            et_password.error = "Password must have one uppercase character"
                        }
                    } else {
                        et_password.error = "Password length must have 6 character"
                    }
                } else {
                    et_password.error = "Required password"
                }
            }

            R.id.pass_view -> {
                pass_hide.visibility = View.VISIBLE
                pass_view.visibility = View.GONE
                et_password.transformationMethod = null
                et_password.setSelection(et_password.length())
            }

            R.id.pass_hide -> {
                pass_view.visibility = View.VISIBLE
                pass_hide.visibility = View.GONE
                et_password.transformationMethod = PasswordTransformationMethod()
                et_password.setSelection(et_password.length())
            }

            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun callApiUpdatePassword(password: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelResponse> = apiInterface.updatePassword(Constants.KEY_HEADER+token, Constants.KEY_BOT, password)
        call.enqueue(object : Callback<ModelResponse?> {
            override fun onResponse(call: Call<ModelResponse?>, response: Response<ModelResponse?>) {
                val modelResponse: ModelResponse? = response.body()
                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val intent = Intent(context, LogInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(context, "Your password updated successfully, please login", Toast.LENGTH_LONG).show()


                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    Log.e("ErrorResponse", modelResponse.message!!)
                }
            }

            override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
