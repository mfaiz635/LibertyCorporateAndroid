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
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.*
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.*
import libertypassage.com.corporate.view.adapter.*
import libertypassage.com.corporate.viewmodel.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    private lateinit var industryListViewModel: IndustryListViewModel
    private lateinit var professionListViewModel: ProfessionListViewModel
    var dialogProgress: DialogProgress? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var fullName = ""
    var email = ""
    var countryCode = ""
    var countryId = ""
    var mobile = ""
    var gender = ""
    var ageGroup = ""
    var industryId = ""
    var professionId = ""
    var password = ""
    var firebaseToken = ""

    var industryAdapter: IndustryAdapter? = null
    var professionAdapter: ProfessionsAdapter? = null
    var genderList = ArrayList<String>()
    var ageGroupList = ArrayList<String>()
    var industryArrayList: ArrayList<DetailIndustry> = ArrayList<DetailIndustry>()
    var ipArrayList: ArrayList<DetailIndustryProf> = ArrayList<DetailIndustryProf>()

    //  country dialog
    var countryNamesArray = ArrayList<String>()
    var countries = ArrayList<CountryDto>()
    var filterNames = ArrayList<CountryDto>()
    var adapter: CountryCodeSpinnerAdapter? = null
    var dialog: Dialog? = null
    var countryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupParent(window.decorView.findViewById(android.R.id.content))

        context = this@SignUpActivity
        dialogProgress = DialogProgress(context)
        industryListViewModel = ViewModelProvider(this).get(IndustryListViewModel::class.java)
        professionListViewModel = ViewModelProvider(this).get(ProfessionListViewModel::class.java)

        iv_back.setOnClickListener(this)
        tv_countryCode.setOnClickListener(this)
        pass_view.setOnClickListener(this)
        pass_hide.setOnClickListener(this)
        tv_next.setOnClickListener(this)

        init()
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
                countryNamesArray.add(it)
            }
        }
        adapter = CountryCodeSpinnerAdapter(context, countryNamesArray)
        spnCountryCode.adapter = adapter
        spnCountryCode.onItemSelectedListener = object : OnItemSelectedListener {
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
                    countryId = countries[k].countryId.toString()
                    spnCountryCode.setSelection(k)
                    tv_countryCode.setText(code)
                } else if (flag == "x") {
                    val country = spnCountryCode.selectedItem.toString()
                    val positionCountry: Int = countryNamesArray.indexOf(country)
                    val code: Int = countries[positionCountry].countryCode!!
                    countryId = countries[positionCountry].countryId.toString()
                    spnCountryCode.setSelection(positionCountry)
                    tv_countryCode.setText(code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        industryArrayList.clear()
        industryArrayList.addAll(Utility.getIndustry(context))
        industryAdapter = IndustryAdapter(context, industryArrayList)
        spinnerIndustry.adapter = industryAdapter
        if (industryArrayList.size == 0) {
            if (Utility.isConnectingToInternet(context)) {
                callApiIndustryList()
            } else {
                Toast.makeText(context, "Please connect to internet and try again", Toast.LENGTH_LONG).show()
            }
        }

        et_fullName.setText(Utility.getSharedPreferences(context, Constants.KEY_FULLNAME))
        et_email.setText(Utility.getSharedPreferences(context, Constants.KEY_EMAIL))
        et_mobileNo.setText(Utility.getSharedPreferences(context, Constants.KEY_MOBILE))
      //  et_mobileNo.setText("+919658965832")

        genderList.add("Select Gender")
        genderList.add("Male")
        genderList.add("Female")

        ageGroupList.add("Select Age Group")
        ageGroupList.add("Under 18")
        ageGroupList.add("18-30")
        ageGroupList.add("31-45")
        ageGroupList.add("46-60")
        ageGroupList.add("61-80")
        ageGroupList.add("Above 80")

        val genderAdapter = GenderAdapter(context, genderList)
        spinnerGender.adapter = genderAdapter

        val ageGroupAdapter = AgeGroupAdapter(context, ageGroupList)
        spinnerAgeGroup.adapter = ageGroupAdapter

        ipArrayList.clear()
        ipArrayList.add(
            DetailIndustryProf(
                0,
                1,
                "Select Profession"
            )
        )
        professionAdapter = ProfessionsAdapter(context, ipArrayList)
        spinnerProfession.adapter = professionAdapter


        spinnerGender.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long) {
                if(spinnerGender.selectedItemPosition != 0) {
                    gender = genderList[position]
                }else{
                    gender=""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerAgeGroup.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                if (spinnerAgeGroup.selectedItemPosition != 0) {
                    ageGroup = ageGroupList[position]
                }else{
                    ageGroup=""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerIndustry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                if (spinnerIndustry.selectedItemPosition != 0) {
                    industryId = industryArrayList[position].industryId.toString()
                    callApiProfessionList()
                }else{
                    industryId=""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerProfession.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                if(spinnerProfession.selectedItemPosition != 0) {
                    professionId = ipArrayList[position].profId.toString()
                }else{
                    professionId=""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tv_countryCode -> {
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

            R.id.tv_next -> {
                fullName = et_fullName.text.toString().trim()
                email = et_email.text.toString().trim()
//                countryCode = tv_countryCode.text.toString().trim()
                mobile = et_mobileNo.text.toString().trim()
                password = et_password.text.toString().trim()

                if (!isValid) return
                callApiUserExistence()
            }
        }
    }

    private val isValid: Boolean
        get() {
            var isValid = true
            val messages = ArrayList<String>()
            when {
                TextUtils.isEmpty(fullName) -> {
                    messages.add(resources.getString(R.string.requiredFullName))
                    isValid = false
                }
//                TextUtils.isEmpty(email) -> {
//                    messages.add(resources.getString(R.string.requiredEmail))
//                    isValid = false
//                }
                !TextUtils.isEmpty(email) && !email.matches(emailPattern) -> {
                    messages.add(resources.getString(R.string.invalidEmail))
                    isValid = false
                }
                TextUtils.isEmpty(mobile) -> {
                    messages.add(resources.getString(R.string.requiredMobile))
                    isValid = false
                }
                mobile.length < 6 -> {
                    messages.add(resources.getString(R.string.invalidMobile))
                    isValid = false
                }
                TextUtils.isEmpty(gender) -> {
                    messages.add(resources.getString(R.string.requiredGender))
                    isValid = false
                }
                TextUtils.isEmpty(ageGroup) -> {
                    messages.add(resources.getString(R.string.requiredAgeGroup))
                    isValid = false
                }
                TextUtils.isEmpty(industryId) -> {
                    messages.add(resources.getString(R.string.requiredIndustry))
                    isValid = false
                }
                TextUtils.isEmpty(professionId) -> {
                    messages.add(resources.getString(R.string.requiredProfession))
                    isValid = false
                }
                TextUtils.isEmpty(password) -> {
                    messages.add(resources.getString(R.string.requiredPassword))
                    isValid = false
                }
                password.length < 5 -> {
                    messages.add("Password length must have 6 character")
                    isValid = false
                }
                !Pattern.compile("[A-Z ]").matcher(password).find() -> {
                    messages.add("Password must have one uppercase character")
                    isValid = false
                }
            }
            if (!isValid) {
                dialogValidation(
                        context,
                        resources.getString(R.string.validation_title),
                        resources.getString(R.string.validation_sub_title),
                        messages
                )
            }
            return isValid
        }

    var dialogValidation: Dialog? = null
    private fun dialogValidation(
            context: Context,
            title: String,
            subTitle: String,
            msgs: ArrayList<String>
    ) {
        if (dialogValidation != null && dialogValidation!!.isShowing)
            dialogValidation!!.dismiss()
        dialogValidation = DialogValidation(context, title, subTitle, msgs)
        dialogValidation!!.show()
    }

    private fun callApiIndustryList() {
        dialogProgress!!.show()
        industryListViewModel.getList()!!.observe(this,
            { response ->
                dialogProgress!!.dismiss()
                if (response != null && response.error?.equals(false)!!) {
                    val indList = response.details

                    industryArrayList.clear()
                    industryArrayList.add(
                        DetailIndustry(
                            0,
                            "Select Industry"
                        )
                    )
                    industryArrayList.addAll(indList!!)
                    industryAdapter = IndustryAdapter(context, industryArrayList)
                    spinnerIndustry.adapter = industryAdapter
                    Utility.saveIndustry(context, industryArrayList)
                } else {
                    if (response.error?.equals(true)!!) {
                        Utility.stopProgressDialog(context)
                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun callApiProfessionList() {
        dialogProgress!!.show()
        professionListViewModel.getList("1")!!.observe(this,
            { response ->
                dialogProgress!!.dismiss()
                if (response != null && response.error?.equals(false)!!) {
                    val ipList = response.details
                    ipArrayList.clear()
                    ipArrayList.add(
                        DetailIndustryProf(
                            0,
                            1,
                            "Select Profession"
                        )
                    )
                    ipArrayList.addAll(ipList!!)
                    professionAdapter = ProfessionsAdapter(context, ipArrayList)
                    spinnerProfession.adapter = professionAdapter
                    Utility.saveIndustryProfession(context, ipArrayList)
                } else {
                    if (response.error == true) {
                        Utility.stopProgressDialog(context)
                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun callApiUserExistence() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelExistence> = apiInterface.checkUserExistence(Constants.KEY_BOT, email, mobile)

        call.enqueue(object : Callback<ModelExistence?> {
            override fun onResponse(call: Call<ModelExistence?>, response: Response<ModelExistence?>) {
                val model: ModelExistence? = response.body()
                dialogProgress!!.dismiss()
                if (model != null && model.error?.equals(false)!!) {
                    callApiSendOtp()

                } else if (model != null && model.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, model.message, Toast.LENGTH_LONG).show()
                    Log.e("ErrorResponse", model.message!!)
                }
            }

            override fun onFailure(call: Call<ModelExistence?>, t: Throwable) {
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

                    val detailOtp = modelResponse.details
                    val otp = detailOtp?.otp

                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()

                    Utility.setSharedPreference(context, "isVerify", "signup")
                    Utility.setSharedPreference(context, Constants.KEY_FULLNAME, fullName)
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, email)
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, mobile)
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, gender)
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, ageGroup)
                    Utility.setSharedPreference(context, Constants.KEY_INDUSTRY, industryId)
                    Utility.setSharedPreference(context, Constants.KEY_PROF, professionId)
                    Utility.setSharedPreference(context, Constants.KEY_COUNTRY_ID, countryId)
                    Utility.setSharedPreference(context, "password", password)
                    Utility.setSharedPreference(context, "firebaseToken", firebaseToken)
                    Utility.setSharedPreference(context, "otp", otp.toString())

                    Log.e("otp", otp.toString())
                    val intent = Intent(context, VerifyOtpSignUp::class.java)
                    intent.putExtra("from", "signup")
                    startActivity(intent)
                    finish()

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    dialogUpdate(modelResponse.message)
                    Log.e("ErrorResponse", modelResponse.message!!)
                }
            }

            override fun onFailure(call: Call<ModelOTP?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }


    private fun dialogUpdate(message: String?) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_error_msg)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById(R.id.ivCancel) as ImageView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        tvTitle.text = message
        ivCancel.setOnClickListener { dialog.dismiss() }
        tvOk.setOnClickListener({ dialog.dismiss() })
        dialog.show()
    }

    private fun selectCountryDialog() {
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.country_popup)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                                countryId = country.countryId.toString()
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
