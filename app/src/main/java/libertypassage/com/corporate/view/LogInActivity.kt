package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.spnCountryCode
import kotlinx.android.synthetic.main.forgot_password.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.CountryDetail
import libertypassage.com.corporate.model.CountryDto
import libertypassage.com.corporate.model.login.ModelLogin
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.*
import libertypassage.com.corporate.view.adapter.CountryCodeAdapter
import libertypassage.com.corporate.view.adapter.CountryCodeSpinnerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*



class LogInActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var firebaseToken = ""
    var countryCode = ""
    var mobile = ""
    var password = ""
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
        setContentView(R.layout.activity_login)
        setupParent(window.decorView.findViewById(android.R.id.content))

        context = this@LogInActivity
        dialogProgress = DialogProgress(context)

        init()

        tvRegister.setOnClickListener(this)
        tvCountryCode.setOnClickListener(this)
        pass_view.setOnClickListener(this)
        pass_hide.setOnClickListener(this)
        tv_forgot_pass.setOnClickListener(this)
        login_btn.setOnClickListener(this)

        et_mobileNo.isFocusable = false
        et_password.isFocusable = false
        et_mobileNo.setOnTouchListener { v, event ->
            et_mobileNo.isFocusableInTouchMode = true
            false
        }
        et_password.setOnTouchListener { v, event ->
            et_password.isFocusableInTouchMode = true
            false
        }

        et_mobileNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (isNumeric(charSequence.toString())) {
                    iv_country.visibility = View.VISIBLE
                    iv_country_gray.visibility = View.GONE
                    view.setBackgroundColor(resources.getColor(R.color.white))
                    tvCountryCode.setTextColor(resources.getColor(R.color.white))
                    tvCountryCode.isEnabled = true
                } else {
                    iv_country.visibility = View.GONE
                    iv_country_gray.visibility = View.VISIBLE
                    view.setBackgroundColor(resources.getColor(R.color.gray_out))
                    tvCountryCode.setTextColor(resources.getColor(R.color.gray_out))
                    tvCountryCode.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    fun isNumeric(strNum: String?): Boolean {
        if (strNum == null) {
            return false
        }
        try {
            val d = strNum.toDouble()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }


    private fun init() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("getInstanceId failed", task.exception.toString())
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                firebaseToken = task.result.token
                Log.e("firebaseToken =", firebaseToken)
            })

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
            R.id.tvRegister -> {
                val intent = Intent(context, SignUpActivity::class.java)
                startActivity(intent)
            }

            R.id.tvCountryCode -> {
                selectCountryDialog()
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

            R.id.tv_forgot_pass -> {
                val intent = Intent(context, ForgotPassword::class.java)
                startActivity(intent)
            }

            R.id.login_btn -> {
                countryCode = tvCountryCode1.text.toString().trim()
                mobile = et_mobileNo.text.toString().trim()
                password = et_password.text.toString().trim()

                if (!TextUtils.isEmpty(mobile)) {
                    if (isNumeric(mobile)) {
                        if (!tvCountryCode.text.toString().trim().equals("Select Country")) {
                            if (mobile.length > 7) {
                                if (!TextUtils.isEmpty(password)) {
                                    if (Utility.isConnectingToInternet(context)) {
                                        callApiLogIn(countryCode+mobile, password, "number")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.connectInternet,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    et_password.error = "Required password"
                                }
                            } else {
                                et_mobileNo.error = "Invalid mobile number"
                            }
                        } else {
                            Toast.makeText(context, "Required country", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        if (mobile.matches(emailPattern)) {
                            if (!TextUtils.isEmpty(password)) {
                                if (Utility.isConnectingToInternet(context)) {
                                    callApiLogIn(mobile, password, "email")
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.connectInternet,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(context, "Required password", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, "Required valid email", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Required email/mobile number", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun callApiLogIn(mobileNo: String, password: String, type: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelLogin> = apiInterface.userLogIn(
            Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, firebaseToken, mobileNo, password
        )
        call.enqueue(object : Callback<ModelLogin?> {
            override fun onResponse(call: Call<ModelLogin?>, response: Response<ModelLogin?>) {
                dialogProgress!!.dismiss()
                val responses: ModelLogin? = response.body()

                if (responses != null && responses.error?.equals(false)!!) {

                    val logInDetails = responses.details
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_BEARER_TOKEN,
                        logInDetails?.bearerToken
                    )
                    val userDetails = logInDetails?.userDetails
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_MOBILE,
                        userDetails?.phoneNumber
                    )
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, userDetails?.emailId)
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, userDetails?.gender)
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_AGE_GROUP,
                        userDetails?.ageGroup
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_INDUSTRY,
                        userDetails?.industryId?.toString()
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_PROF,
                        userDetails?.profId.toString()
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_CORPORATION_ID,
                        userDetails?.corpId.toString()
                    )

                    val add = userDetails?.address
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, add?.address)

                    val confirmed = userDetails?.confirmed
                    Utility.setSharedPreference(context, Constants.KEY_VACCINE_ID, confirmed?.vaccineId.toString())
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_CONFIRMATION,
                        confirmed?.confirmation.toString()
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_CHANGE_STATUS,
                        confirmed?.updatedAt
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_HOSPITAL_ADD,
                        confirmed?.clinicAddress
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_HOSPITAL_DATE,
                        confirmed?.date
                    )
                    val vaccines = confirmed?.vaccineName
                    Utility.setSharedPreference(context, Constants.KEY_VACCINE_NAME, vaccines?.name)

                    val temperature = userDetails?.temprature
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_MY_TEMP,
                        temperature?.temprature
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_TEMP_TYPE,
                        temperature?.tempType
                    )

                    val lastTrack = userDetails?.lastTrack
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_LAT,
                        lastTrack?.latitude
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_LONG,
                        lastTrack?.longitude
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_ALT,
                        lastTrack?.heightAzimuth
                    )

                    val userStatus = userDetails?.userStatus
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_RISK_SCORE,
                        userDetails?.riskScore.toString()
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_CURRENT_STATUS,
                        java.lang.String.valueOf(userStatus?.id)
                    )
                    Utility.setSharedPreference(context, Constants.KEY_TITLE, userStatus?.title)
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_SUB_TITLE,
                        userStatus?.subTitle
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_DESCRIPTION,
                        userStatus?.description
                    )
                    Utility.setSharedPreference(
                        context,
                        Constants.KEY_ALERT_DESCRIPTION,
                        userStatus?.alertDescription
                    )


                    localNotification()

                } else if (responses != null && responses.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelLogin?>, t: Throwable) {
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
                        //hideSoftKeyboard();
                        val country: CountryDto = if (filterNames.size > 0) {
                            filterNames[position]
                        } else {
                            countries[position]
                        }
                        tvCountryCode1.text = "+" + country.countryCode
                        tvCountryCode.text = country.countryName
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

    private fun localNotification() {
        Log.e("localNotification", "running")
        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intentAlarm = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = 7
        calendar[Calendar.MINUTE] = 0
        manager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_DAY,
            pendingIntent
        )

        val intent = Intent(context, HomePage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        Toast.makeText(context, "You are login Successfully", Toast.LENGTH_LONG).show()
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


