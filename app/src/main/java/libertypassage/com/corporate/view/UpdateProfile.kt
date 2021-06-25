package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.update_profile.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.*
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.DialogValidation
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.adapter.AgeGroupAdapter
import libertypassage.com.corporate.view.adapter.GenderAdapter
import libertypassage.com.corporate.view.adapter.IndustryAdapter
import libertypassage.com.corporate.view.adapter.ProfessionsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*



class UpdateProfile : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var token = ""
    var fullName = ""
    var email = ""
    var emailNew = ""
    var emailVerify = ""
    var mobile = ""
    var gender = ""
    var ageGroup = ""
    var genderId = 0
    var ageGroupId = 0
    var industryId = ""
    var professionId = ""
    var industry = "Select Industry"
    var profession = "Select Profession"

    var industryAdapter: IndustryAdapter? = null
    var professionAdapter: ProfessionsAdapter? = null
    var genderList = ArrayList<String>()
    var ageGroupList = ArrayList<String>()
    var industryArrayList = ArrayList<DetailIndustry>()
    var ipArrayList = ArrayList<DetailIndustryProf>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_profile)
        setupParent(window.decorView.findViewById(android.R.id.content))

        context = this@UpdateProfile
        dialogProgress = DialogProgress(context)

        iv_back.setOnClickListener(this)
        ivNotVerify.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        init()
    }

    private fun init() {
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        et_fullName.setText(Utility.getSharedPreferences(context, Constants.KEY_FULLNAME))
        tvMobile.text = Utility.getSharedPreferences(context, Constants.KEY_MOBILE)
        emailVerify = Utility.getSharedPreferences(context, Constants.KEY_EMAIL_VERIFIED)!!
        email = Utility.getSharedPreferences(context, Constants.KEY_EMAIL)!!
        et_email.setText(email)
        ivNotVerify.visibility = View.GONE
        gender = Utility.getSharedPreferences(context, Constants.KEY_GENDER)!!
        ageGroup = Utility.getSharedPreferences(context, Constants.KEY_AGE_GROUP)!!
        industryId = Utility.getSharedPreferences(context, Constants.KEY_INDUSTRY)!!
        professionId = Utility.getSharedPreferences(context, Constants.KEY_PROF)!!


        industryArrayList.clear()
        industryArrayList.addAll(Utility.getIndustry(context))
        industryAdapter = IndustryAdapter(context, industryArrayList)
        spinnerIndustry.adapter = industryAdapter

        if(industryArrayList.size!=0 && industryId.isNotEmpty()) {
            val indexInd = getPositionIndustry()
            if(indexInd!=-1){
                spinnerIndustry!!.setSelection(indexInd)
            }
        }

        if (industryArrayList.size == 0) {
            if (Utility.isConnectingToInternet(context)) {
                callApiIndustryList()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please connect to internet and try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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
        spinnerGender.setSelection(genderList.indexOf(gender))

        val ageGroupAdapter = AgeGroupAdapter(context, ageGroupList)
        spinnerAgeGroup.adapter = ageGroupAdapter
        spinnerAgeGroup.setSelection(ageGroupList.indexOf(ageGroup))

        spinnerGender.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (spinnerGender.selectedItemPosition != 0) {
                    gender = genderList[position]
                    genderId = position
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
                    ageGroupId = position
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
                    callApiProfessionList(industryId)
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
                if (spinnerProfession.selectedItemPosition != 0) {
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
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in)
                finish()
            }

            R.id.ivNotVerify -> {
            }

            R.id.tv_next -> {
                fullName = et_fullName.text.toString().trim()
                emailNew = et_email.text.toString().trim()
                mobile = tvMobile.text.toString().trim()

                if (!isValid) return
                callApiUpdateProfile()
            }
        }
    }

    private val isValid: Boolean
        get() {
            var isValid = true
            val messages = ArrayList<String>()
            when {
//                TextUtils.isEmpty(emailNew) -> {
//                    messages.add(resources.getString(R.string.requiredEmail))
//                    isValid = false
//                }
                !TextUtils.isEmpty(emailNew) && !emailNew.matches(emailPattern)-> {
                    messages.add(resources.getString(R.string.invalidEmail))
                    isValid = false
                }
                emailNew != Utility.getSharedPreferences(context, Constants.KEY_EMAIL) -> {
                    callApiUserExistence(emailNew)
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
                TextUtils.isEmpty(professionId) -> {
                    messages.add(resources.getString(R.string.requiredProfession))
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
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<Industry> = apiInterface.getIndustries(Constants.KEY_BOT)

        call.enqueue(object : Callback<Industry?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<Industry?>,
                response: Response<Industry?>
            ) {
                dialogProgress!!.dismiss()
                val responses: Industry? = response.body()

                if (responses != null && responses.error?.equals(false)!!) {

                    val indList = responses.details

                    industryArrayList.clear()
                    industryArrayList.add(
                        DetailIndustry(
                            0,
                            "Select Industry"
                        )
                    )
                    industryArrayList.addAll(indList!!)
                    industryAdapter = IndustryAdapter(context, industryArrayList)
                    spinnerIndustry!!.adapter = industryAdapter

                    if(industryArrayList.size!=0 && industryId.isNotEmpty()) {
                        val indexInd = getPositionIndustry()
                        if(indexInd!=-1){
                            spinnerIndustry!!.setSelection(indexInd)
                        }
                    }
                    Utility.saveIndustry(context, industryArrayList)

                } else if (responses != null && responses.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Industry?>, t: Throwable) {
                dialogProgress!!.dismiss()
            }
        })
    }

    private fun callApiProfessionList(industryId: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<IndustryProfessions> =
            apiInterface.getIndustryProfessions(Constants.KEY_BOT, industryId)

        call.enqueue(object : Callback<IndustryProfessions?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<IndustryProfessions?>,
                response: Response<IndustryProfessions?>
            ) {
                dialogProgress!!.dismiss()
                val responses: IndustryProfessions? = response.body()
                if (responses != null && responses.error?.equals(false)!!) {

                    val ipList = responses.details

                    ipArrayList.clear()
                    ipArrayList.add(
                        DetailIndustryProf(
                            0,
                            industryId.toInt(),
                            "Select Profession"
                        )
                    )
                    ipArrayList.addAll(ipList!!)
                    professionAdapter = ProfessionsAdapter(context, ipArrayList)
                    spinnerProfession!!.adapter = professionAdapter

                    if(ipArrayList.size!=0 && professionId.isNotEmpty()) {
                        val indexProf = getPositionProfession()
                        if(indexProf!=-1){
                            spinnerProfession!!.setSelection(indexProf)
                        }
                    }

                } else if (responses != null && responses.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<IndustryProfessions?>, t: Throwable) {
                dialogProgress!!.dismiss()
            }
        })
    }

    private fun callApiUserExistence(emailNew: String) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelExistence> = apiInterface.checkUserExistence(Constants.KEY_BOT, emailNew, "")

        call.enqueue(object : Callback<ModelExistence?> {
            override fun onResponse(call: Call<ModelExistence?>, response: Response<ModelExistence?>) {
                val model: ModelExistence? = response.body()
                dialogProgress!!.dismiss()
                if (model != null && model.error?.equals(false)!!) {

                    Log.e("model", model.message!!)

                } else if (model != null && model.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Log.e("ErrorResponse", model.message!!)
                }
            }

            override fun onFailure(call: Call<ModelExistence?>, t: Throwable) {
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }


    private fun callApiUpdateProfile() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelSignUp> = apiInterface.updateProfile(Constants.KEY_HEADER+token,
            Constants.KEY_BOT, Constants.KEY_DEVICE_TYPE, emailNew, mobile, gender, ageGroup,
            industryId, professionId)

        call.enqueue(object : Callback<ModelSignUp?> {
            override fun onResponse(call: Call<ModelSignUp?>, response: Response<ModelSignUp?>) {
                val modelResponse: ModelSignUp? = response.body()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    Utility.setSharedPreference(context, Constants.KEY_FULLNAME, fullName)
                    Utility.setSharedPreference(context, Constants.KEY_EMAIL, emailNew)
                    Utility.setSharedPreference(context, Constants.KEY_MOBILE, mobile)
                    Utility.setSharedPreference(context, Constants.KEY_GENDER, gender)
                    Utility.setSharedPreference(context, Constants.KEY_AGE_GROUP, ageGroup)
                    Utility.setSharedPreference(context, Constants.KEY_INDUSTRY, industryId)
                    Utility.setSharedPreference(context, Constants.KEY_PROF, professionId)
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_LONG).show()
                    finish()

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


    @SuppressLint("ClickableViewAccessibility")
    private fun setupParent(view: View) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (currentFocus != null) {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    fun getPositionProfession(): Int = ipArrayList.indexOfFirst {
        it.profId == professionId.toInt()
    }

    fun getPositionIndustry(): Int = industryArrayList.indexOfFirst {
        it.industryId == industryId.toInt()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
