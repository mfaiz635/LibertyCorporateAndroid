package libertypassage.com.corporate.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.change_status.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.*
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.adapter.VaccineAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*


class ChangeStatus : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    private var dialogProgress: DialogProgress? = null
    private var vaccineAdapter: VaccineAdapter? = null
    private var vaccineArrayList: ArrayList<DetailVaccines> = ArrayList<DetailVaccines>()
    private var returnImagesList: MutableList<ClinicQrImage> = mutableListOf()
    private val imagesArrayList = ArrayList<Uri>()
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
        llAddImages.setOnClickListener(this)
        llViewImages.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
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
                tv_lastStatusChange.text = "Confirmed I am fit for work on $dateChangeStatus1"
                et_hospitalName.setText(oldHospitalAddress)
                tv_select_date.text = confirmHospitalDate
            } else {
                tv_lastStatusChange.visibility = View.GONE
            }
        } else {
            if (dateChangeStatus.isNotEmpty()) {
                tv_lastStatusChange.visibility = View.VISIBLE
                tv_lastStatusChange.text = "Confirmed I am unfit for work on $dateChangeStatus1"
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

        try {
            if (vaccineId.isNotEmpty() && vaccineId != "null" && vaccineId != "") {
                spinnerVaccine.setSelection(vaccineId.toInt())
            }
        } catch (e: NumberFormatException) {
            Log.e("Exception", e.message + "")
        }

        if (vaccineArrayList.size == 0) {
            if (Utility.isConnectingToInternet(context)) {
                callApiVaccineList()
            } else {
                Toast.makeText(
                    context,
                    "Please connect to internet and try again",
                    Toast.LENGTH_LONG
                ).show()
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

            R.id.llAddImages -> {
                val permissions = arrayOf<String?>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 123)
                } else {
                    selectMultipleImages()
                }
            }

            R.id.llViewImages -> {
                val intent = Intent(context, ChangeStatusImagesList::class.java)
                startActivity(intent)
            }

            R.id.tvConfirm -> {
                hospital = et_hospitalName.text.toString().trim()
                selectDate = tv_select_date.text.toString().trim()

                if (!checkBox_postive.isChecked && !checkBox_negative.isChecked) {
                    Toast.makeText(
                        context,
                        "Please confirmed your fit/unfit status",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (hospital.isEmpty()) {
                    Toast.makeText(context, "Please write remark details", Toast.LENGTH_LONG).show()
                } else if (selectDate == "Select Date") {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_LONG).show()
                } else if (Utility.isConnectingToInternet(context)) {
                    if (imagesArrayList.size > 0) {
                        changeStatusWithImages()
                    } else {
                        changeStatusApi()
                    }
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                }
            }

            R.id.rl_goBack -> {
                finish()
            }

            R.id.ivCancel -> {
                imagesArrayList.clear()
                rlImagesSelected.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun selectMultipleImages() {
        UwMediaPicker
            .with(this)                        // Activity or Fragment
            .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
            .setGridColumnCount(2)                                  // Grid column count, default is 3
            .setMaxSelectableMediaCount(3)                         // Maximum selectable media count, default is null which means infinite
            .setLightStatusBar(true)                                // Is llight status bar enable, default is true
            .enableImageCompression(true)                // Is image compression enable, default is false
            .setCompressionMaxWidth(1280F)                // Compressed image's max width px, default is 1280
            .setCompressionMaxHeight(720F)                // Compressed image's max height px, default is 720
            .setCompressFormat(Bitmap.CompressFormat.JPEG)        // Compressed image's format, default is JPEG
            .setCompressionQuality(85)                // Image compression quality, default is 85
//          .setCompressedFileDestinationPath(destinationPath)	// Compressed image file's destination path, default is "${application.getExternalFilesDir(null).path}/Pictures"
            .launch { selectedMediaList ->
                for (index in selectedMediaList!!.indices) {
                    imagesArrayList.add(Uri.parse(selectedMediaList[index].mediaPath))
                }
                if (imagesArrayList.size > 0) {
                    if (imagesArrayList.size == 1) {
                        tvSelectedImage.text =
                            "You have selected " + imagesArrayList.size + " Image"
                    } else {
                        tvSelectedImage.text =
                            "You have selected " + imagesArrayList.size + " Images"
                    }
                    rlImagesSelected.visibility = View.VISIBLE
                } else {
                    rlImagesSelected.visibility = View.GONE
                }
            } // (::onMediaSelected)	// Will be called when media is selected
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
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelConfirm> = apiInterface.changeStatus(
            Constants.KEY_HEADER + token, Constants.KEY_BOT, check,
            vaccineId, hospital, selectDate
        )
        call.enqueue(object : Callback<ModelConfirm?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<ModelConfirm?>,
                response: Response<ModelConfirm?>
            ) {
                dialogProgress!!.dismiss()
                val responses: ModelConfirm? = response.body()
                Log.e("changeStatus", Gson().toJson(responses))
                if (responses != null && responses.error?.equals(false)!!) {

                    val details = responses.details
                    val clinicQrImageList = details!!.clinicQrImage
                    returnImagesList.clear()
                    returnImagesList.addAll(clinicQrImageList!!)
                    Utility.saveStatusImages(context, returnImagesList)

                    Toast.makeText(context, "Status changed successfully", Toast.LENGTH_SHORT)
                        .show()
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
                    if (responses.message.equals("User details not found. Please register/login again.")) {
                        Utility.clearPreference(context)
                        val intent = Intent(context, LogInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ModelConfirm?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    private fun changeStatusWithImages() {
        dialogProgress!!.show()
        Log.e("imagesArrayList",  Gson().toJson(imagesArrayList))
        val imagesParts = arrayOfNulls<MultipartBody.Part>(imagesArrayList.size)
        for (index in 0 until imagesArrayList.size) {
            val file = File(imagesArrayList[index].path)
            val surveyBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            imagesParts[index] =
                MultipartBody.Part.createFormData("if_clinic_qr_image[]", file.path, surveyBody)
        }
        val ifBot = Constants.KEY_BOT.toRequestBody("text/plain".toMediaTypeOrNull())
        val confirmed = check.toRequestBody("text/plain".toMediaTypeOrNull())
        val idVaccine = vaccineId.toRequestBody("text/plain".toMediaTypeOrNull())
        val clinicAddress = hospital.toRequestBody("text/plain".toMediaTypeOrNull())
        val dateSelected = selectDate.toRequestBody("text/plain".toMediaTypeOrNull())

        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call = apiInterface.changeStatusWithImages(
            Constants.KEY_HEADER + token, ifBot,
            confirmed, idVaccine, clinicAddress, dateSelected, imagesParts
        )
        call.enqueue(object : Callback<ModelConfirm?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<ModelConfirm?>,
                response: Response<ModelConfirm?>
            ) {
                dialogProgress!!.dismiss()
                val model: ModelConfirm? = response.body()
                Log.e("changeStatusMultipart", Gson().toJson(model))
                if (model != null && model.error!!.equals(false)) {

                    val details = model.details
                    val clinicQrImageList = details!!.clinicQrImage
                    returnImagesList.clear()
                    returnImagesList.addAll(clinicQrImageList!!)
                    Utility.saveStatusImages(context, returnImagesList)

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

                } else if (model != null && model.error!!.equals(true)) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show()
                    if (model.message.equals("User details not found. Please register/login again.")) {
                        Utility.clearPreference(context)
                        val intent = Intent(context, LogInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ModelConfirm?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("onFailureImg", t.message.toString())
            }
        })
    }

    private fun callApiVaccineList() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<Vaccines> = apiInterface.getVaccines(Constants.KEY_BOT)
        call.enqueue(object : Callback<Vaccines?> {
            override fun onResponse(call: Call<Vaccines?>, response: Response<Vaccines?>) {
                val modelResponse: Vaccines? = response.body()
                dialogProgress!!.dismiss()

                if (modelResponse != null && modelResponse.error?.equals(false)!!) {

                    val indList = modelResponse.details
                    vaccineArrayList.clear()
                    vaccineArrayList.add(DetailVaccines(0, "No Vaccination"))
                    vaccineArrayList.addAll(indList!!)
                    vaccineAdapter = VaccineAdapter(context, vaccineArrayList)
                    spinnerVaccine.adapter = vaccineAdapter

                    if (vaccineId.isNotEmpty() && vaccineId != "null" && vaccineId != "") {
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

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == 101) {
            selectMultipleImages()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    selectMultipleImages()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    dialogRequestPermissionCamera()
                }
                return
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogRequestPermissionCamera() {
        val dialog = Dialog(context)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_runtime_permission)

        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSetting = dialog.findViewById(R.id.tvSetting) as TextView

        tvDescription.text =
            "Camera & Storage Permission access must be allowed to use the Liberty App. " +
                    "You can allow the access in Setting > Permission."

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvSetting.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 456)
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

