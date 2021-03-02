package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.change_status.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.DetailVaccines
import libertypassage.com.corporate.model.ModelConforme
import libertypassage.com.corporate.model.Vaccines
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.adapter.VaccineAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*



class ChangeStatus : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var vaccineAdapter: VaccineAdapter? = null
    var vaccineArrayList: ArrayList<DetailVaccines> = ArrayList<DetailVaccines>()
    private var token = ""
    private var confirmation = ""
    private var dateChangeStatus = ""
    private var dateChangeStatus1 = ""
    private var oldHospitalAddress = ""
    private var confirmHospitalDate = ""
    private var check = "0"
    private var hospital = ""
    private var selectDate = ""
    private var statusId = 0
    private var vaccineId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_status)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@ChangeStatus

        dialogProgress = DialogProgress(context)
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        confirmation = Utility.getSharedPreferences(context, Constants.KEY_CONFIRMATION)!!
        dateChangeStatus = Utility.getSharedPreferences(context, Constants.KEY_CHANGE_STATUS)!!
        oldHospitalAddress = Utility.getSharedPreferences(context, Constants.KEY_HOSPITAL_ADD)!!
        confirmHospitalDate = Utility.getSharedPreferences(context, Constants.KEY_HOSPITAL_DATE)!!
        statusId = Utility.getSharedPreferences(context, Constants.KEY_CURRENT_STATUS)!!.toInt()
        vaccineId = Utility.getSharedPreferences(context, Constants.KEY_VACCINE_ID)!!

        Log.e("vaccineId", vaccineId)
        Log.e("confirmation", confirmation)

        iv_back.setOnClickListener(this)
        ivSpinnerDown.setOnClickListener(this)
        rlCalender.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
        rl_goBack.setOnClickListener(this)

        init()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "ClickableViewAccessibility")
    private fun init() {
        if ((confirmation == "0")) {
            checkBox_negative.isChecked = true
            checkBox_postive.isChecked = false
            check = "0"
        } else {
            if (confirmation.isNotEmpty()) {
                checkBox_negative.isChecked = false
                checkBox_postive.isChecked = true
                check = "1"
            } else {
                checkBox_negative.isChecked = false
                checkBox_postive.isChecked = false
            }
        }

        et_hospitalName.isFocusable = false
        et_hospitalName.setOnTouchListener { v, event ->
            et_hospitalName.isFocusableInTouchMode = true
            et_hospitalName.setSelection(et_hospitalName.length())
            false
        }

        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd-MM-yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        try {
            date = inputFormat.parse(dateChangeStatus)
            dateChangeStatus1 = outputFormat.format(date)
        } catch (e: java.text.ParseException) {
            e.printStackTrace()
        }

        if (confirmation == "0") {  // negative confirmation
            if (dateChangeStatus.isNotEmpty()) {
                tv_lastStatusChange.visibility = View.VISIBLE
                tv_lastStatusChange.text = "Confirmed diagnosed negative on $dateChangeStatus1"
                et_hospitalName.setText(oldHospitalAddress)
                tv_select_date.text = confirmHospitalDate
            } else {
                tv_lastStatusChange.visibility = View.GONE
            }
        } else {
            if (dateChangeStatus.isNotEmpty()) {
                tv_lastStatusChange.visibility = View.VISIBLE
                tv_lastStatusChange.text = "Confirmed diagnosed positive on $dateChangeStatus1"
                et_hospitalName.setText(oldHospitalAddress)
                tv_select_date.text = confirmHospitalDate
            } else {
                tv_lastStatusChange.visibility = View.GONE
            }
        }

        checkBox_postive.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                confirmation = "1"
                check = "1"
                et_hospitalName.setSelection(et_hospitalName.length())
                checkBox_negative.isChecked = false
            }
        }

        checkBox_negative.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                confirmation = "0"
                check = "0"
                et_hospitalName.setSelection(et_hospitalName.length())
                checkBox_postive.isChecked = false
            }
        }

        vaccineArrayList.clear()
        vaccineArrayList.addAll(Utility.getVaccine(context))
        vaccineAdapter = VaccineAdapter(context, vaccineArrayList)
        spinnerVaccine.adapter = vaccineAdapter
        if (vaccineId.isNotEmpty() && vaccineId == "") {
            spinnerVaccine.setSelection(vaccineId.toInt())
        }
        if (vaccineArrayList.size == 0) {
            if (Utility.isConnectingToInternet(context)) {
                callApiVaccineList()
            } else {
                Toast.makeText(context,"Please connect to internet and try again", Toast.LENGTH_LONG).show()
            }
        }


        spinnerVaccine.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                vaccineId = vaccineArrayList[position].id.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.ivSpinnerDown -> {
                spinnerVaccine.performClick()
            }

            R.id.rlCalender -> {
                dialogDatePicker()
            }

            R.id.tvConfirm -> {
                hospital = et_hospitalName.text.toString().trim()
                selectDate = tv_select_date.text.toString().trim()

                if (hospital.isEmpty()) {
                    Toast.makeText(context, "Please write hospital details", Toast.LENGTH_LONG).show()
                } else if (selectDate == "Select Date") {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_LONG).show()
                } else if (Utility.isConnectingToInternet(context)) {
                    changeStatusApi()
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                }
            }

            R.id.rl_goBack -> {
                finish()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun dialogDatePicker() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_date_picker)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val ivCancel = dialog.findViewById(R.id.ivCancel) as ImageView
        val datePicker = dialog.findViewById(R.id.datePicker) as DatePicker
        val tvDontAllow = dialog.findViewById(R.id.tvDontAllow) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        ivCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvDontAllow.setOnClickListener {
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            tv_select_date.text =
                datePicker.dayOfMonth.toString() + "-" + (datePicker.month + 1) + "-" + datePicker.year
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun changeStatusApi() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelConforme> = apiInterface.changeStatus(Constants.KEY_HEADER + token, Constants.KEY_BOT, check,
            vaccineId, hospital, selectDate )
        call.enqueue(object : Callback<ModelConforme?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<ModelConforme?>, response: Response<ModelConforme?>) {
                dialogProgress!!.dismiss()
                val responses: ModelConforme? = response.body()

                if (responses != null && responses.error?.equals(false)!!) {

                    Toast.makeText(context, "Status changed successfully", Toast.LENGTH_SHORT).show()
                    val date = Calendar.getInstance().time
                    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val currentDate = df.format(date)

                    Utility.setSharedPreference(context, Constants.KEY_CONFIRMATION, confirmation)
                    Utility.setSharedPreference(context, Constants.KEY_CHANGE_STATUS, currentDate)
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_ADD, hospital)
                    Utility.setSharedPreference(context, Constants.KEY_HOSPITAL_DATE, selectDate)
                    Utility.setSharedPreference(context, Constants.KEY_VACCINE_ID, vaccineId)

                    val intent = Intent(context, HomePage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                } else if (responses != null && responses.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelConforme?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun callApiVaccineList() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<Vaccines> = apiInterface.getVaccines(Constants.KEY_BOT)
        call.enqueue(object : Callback<Vaccines?> {
            override fun onResponse(call: Call<Vaccines?>, response: Response<Vaccines?>) {
                val modelResponse: Vaccines? = response.body()
                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val indList = modelResponse.details
                    vaccineArrayList.clear()
                    vaccineArrayList.add(DetailVaccines(0,"No Vaccination"))
                    vaccineArrayList.addAll(indList!!)
                    vaccineAdapter = VaccineAdapter(context, vaccineArrayList)
                    spinnerVaccine.adapter = vaccineAdapter
                    if (vaccineId.isNotEmpty() &&  vaccineId=="") {
                        spinnerVaccine.setSelection(vaccineId.toInt())
                    }
                    Utility.saveVaccine(context, vaccineArrayList)

                } else if (modelResponse != null && modelResponse.error?.equals(true)!!) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, modelResponse.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Vaccines?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
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

