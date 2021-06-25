package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.forgot_password.*
import kotlinx.android.synthetic.main.forgot_password.spnCountryCode
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.CountryDetail
import libertypassage.com.corporate.model.CountryDto
import libertypassage.com.corporate.model.DetailsResetPass
import libertypassage.com.corporate.model.ModelReset
import libertypassage.com.corporate.model.login.UserDetails
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.RecyclerItemClickListener
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.adapter.CountryCodeAdapter
import libertypassage.com.corporate.view.adapter.CountryCodeSpinnerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ForgotPassword : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var countryCode = ""
    var mobile = ""
    //  country dialog
    var countryNamesArray = ArrayList<String>()
    var countries = ArrayList<CountryDto>()
    var filterNames = ArrayList<CountryDto>()
    var adapter: CountryCodeSpinnerAdapter? = null
    var dialog: Dialog? = null
    var countryName = ""


    

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@ForgotPassword

        dialogProgress = DialogProgress(context)

        init()

        submit_btn.setOnClickListener(this)
        tv_countryCode.setOnClickListener(this)
        tvGoBack.setOnClickListener(this)

        etMobileNo.isFocusable = false
        etMobileNo.setOnTouchListener { v, event ->
            etMobileNo.isFocusableInTouchMode = true
            false
        }
    }

    private fun init() {
        val jsonFileString = Utility.getJsonFromAssets(applicationContext, "countrylist.json")
        val gson = Gson()
        val listUserType = object : TypeToken<List<CountryDetail?>?>() {}.type
        val countryDetails: List<CountryDetail> = gson.fromJson<List<CountryDetail>>(
            jsonFileString,
            listUserType
        )

        for (i in countryDetails.indices) {
            countries.add(
                CountryDto(
                    countryDetails[i].countryName,
                    countryDetails[i].callingcodes,
                    countryDetails[i].id
                )
            )
            countryDetails[i].countryName?.toUpperCase(Locale.ROOT)?.let {
                countryNamesArray.add(
                    it
                )
            }
        }
        adapter = CountryCodeSpinnerAdapter(context, countryNamesArray)
        spnCountryCode.adapter = adapter
        spnCountryCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val flag = ""
                if (flag == "1") {
                    val k: Int = countryNamesArray.indexOf(countryName)
                    val code: Int = countries[k].countryCode!!
                    spnCountryCode.setSelection(k)
                    tv_countryCode.setText(code)
                } else if (flag == "x") {
                    val country = spnCountryCode.selectedItem.toString()
                    val positionCountry: Int = countryNamesArray.indexOf(country)
                    val code: Int = countries[positionCountry].countryCode!!
                    spnCountryCode.setSelection(positionCountry)
                    tv_countryCode.setText(code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.submit_btn -> {
                countryCode = tv_countryCode.text.toString().trim()
                mobile = etMobileNo.text.toString().trim()

                if (!TextUtils.isEmpty(etMobileNo.text.toString().trim())) {
                    if (etMobileNo.text.toString().length > 7) {
                        if (Utility.isConnectingToInternet(context)) {
                            callApiForgot(countryCode+mobile)
                        } else {
                            Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        etMobileNo.error = "Invalid mobile number"
                    }
                } else {
                    etMobileNo.error = "Required mobile number"
                }
            }

            R.id.tv_countryCode -> {
                selectCountryDialog()
            }

            R.id.tvGoBack -> {
                finish()
            }
        }
    }

    private fun callApiForgot(mobileNo: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelReset> = apiInterface.forgotPassword(Constants.KEY_BOT, mobileNo)
        call.enqueue(object : Callback<ModelReset?> {
            override fun onResponse(call: Call<ModelReset?>, response: Response<ModelReset?>) {
                val modelResponse: ModelReset? = response.body()
                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {
                    val detailsResetPass: DetailsResetPass = modelResponse.details!!
                    val userDetails: UserDetails = detailsResetPass.details!!

                    Utility.setSharedPreference(context, "t_bearer", modelResponse.bearer_token)
                    Utility.setSharedPreference(context, "t_email", userDetails.emailId)
                    Utility.setSharedPreference(context, "t_mobile", mobile)
                    Utility.setSharedPreference(context, "otp", modelResponse.otp.toString())
                    Utility.setSharedPreference(context, "isVerify", "forgot")

                    val intent = Intent(context, VerifyOtpForgot::class.java)
                    intent.putExtra("from", "forgot")
                    startActivity(intent)
                    finish()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()


                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                    Log.e("ErrorResponse", modelResponse.message!!)
                }
            }

            override fun onFailure(call: Call<ModelReset?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }



    private fun selectCountryDialog() {
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.country_popup)
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        val edtCountry = dialog!!.findViewById<EditText>(R.id.edtCountry)
        val cancel = dialog!!.findViewById<ImageView>(R.id.cancel)
        val rvCountry: RecyclerView = dialog!!.findViewById(R.id.rvCountry)
        val linearLayoutManager = LinearLayoutManager(context)
        val countryAdapter = CountryCodeAdapter(context, countries)
        rvCountry.layoutManager = linearLayoutManager
        rvCountry.adapter = countryAdapter
        rvCountry.addOnItemTouchListener(
            RecyclerItemClickListener(context, rvCountry,
                object : RecyclerItemClickListener.OnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onItemClick(view: View?, position: Int) {
                        //               hideSoftKeyboard();
                        val country: CountryDto = if (filterNames.size > 0) {
                            filterNames[position]
                        } else {
                            countries[position]
                        }
                        tv_countryCode.text = "+" + country.countryCode
                        dialog!!.dismiss()
                    }

                    override fun onItemDoubleClick(view: View?, position: Int) {}
                    override fun onItemLongClick(view: View?, position: Int) {}
                })
        )

        edtCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString(), countryAdapter)
            }
        })
        cancel.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String, codeAdapter: CountryCodeAdapter) {
        //new array list that will hold the filtered data
        filterNames = ArrayList()
        //looping through existing elements
        for (s in countries) {
            //if the existing elements contains the search input
            if (s.countryName?.toUpperCase()?.startsWith(text.toUpperCase())!!) {
                //adding the element to filtered list
                filterNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        codeAdapter.filterList(filterNames)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
