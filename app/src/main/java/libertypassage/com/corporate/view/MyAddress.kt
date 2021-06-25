package libertypassage.com.corporate.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.my_address.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.others.QRCodeScannerActivity
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MyAddress : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    private val urlRegex = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
    private var token = ""
    private var address = ""
    private var tempType = ""
    private var currentTemp = ""
    private var latitudes = 0.0
    private var longitudes = 0.0
    private var altitudes = 0.0
    private var venueId = ""
    private var nircId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_address)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@MyAddress

        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        tempType = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE)!!
        currentTemp = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP)!!
        nircId = Utility.getSharedPreferences(context, Constants.KEY_NRIC_ID)!!

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dialogProgress = DialogProgress(context)

        iv_back.setOnClickListener(this)
        iv_qrcode_scanerr.setOnClickListener(this)
        tv_currentLocation.setOnClickListener(this)
        iv_edit.setOnClickListener(this)
        tv_next.setOnClickListener(this)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.iv_edit -> {
                et_address.isEnabled = true
                et_address.setSelection(et_address.length())
            }

            R.id.iv_qrcode_scanerr -> {
                val permissions = arrayOf<String?>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 123)
                } else {
                    val intent = Intent(context, QRCodeScannerActivity::class.java)
                    startActivityForResult(intent, 170)
                }
            }

            R.id.tv_currentLocation -> {
                val permissions = arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 456)
                } else {
                    getLastLocation()
                }
            }


            R.id.tv_next -> {
                address = et_address.text.toString().trim()

                val pattern: Pattern = Pattern.compile(urlRegex)
                val matcher: Matcher = pattern.matcher(address)

                if (address == "") {
                    Toast.makeText(context, "Require address", Toast.LENGTH_LONG).show()
                } else if (Utility.isConnectingToInternet(context)) {
                    if (matcher.find() && address.length > 50) {
                        venueId = address.substring(address.lastIndexOf("/") + 1)
                        //  dialogNIRCCode()
                        val intent = Intent(context, ConfirmNricId::class.java)
                        intent.putExtra("from", "myAddress")
                        intent.putExtra("address", address)
                        intent.putExtra("venueId", venueId)
                        startActivity(intent)
                    } else {
                        addAddressAndTemp()
                    }
                } else {
                    Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun dialogUpdate() {
        val dialog = Dialog(context)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_get_location)

        val ivCancel = dialog.findViewById(R.id.iv_cancel) as ImageView
        val tvNotAllow = dialog.findViewById(R.id.tv_notAllow) as TextView
        val tvOk = dialog.findViewById(R.id.tv_ok) as TextView

        ivCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvNotAllow.setOnClickListener {
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            getLastLocation()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient?.lastLocation?.addOnCompleteListener(
            OnCompleteListener<Location?> { task ->
                val location = task.result
                if (location != null) {
                    latitudes = location.latitude
                    longitudes = location.longitude
                    altitudes = location.altitude
                    getAddress()
                }
            }
        )
    }

    private fun getAddress() {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                latitudes,
                longitudes,
                1
            )
            val address11 = addresses[0].getAddressLine(0)
            address = address11
            et_address.setText(address)
            et_address.setSelection(et_address.length())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addAddressAndTemp() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface =
            ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelResponse> = apiInterface.addAddressTemp(
            Constants.KEY_HEADER + token,
            Constants.KEY_BOT, address, tempType, currentTemp, venueId, nircId, "checkin"
        )

        call.enqueue(object : Callback<ModelResponse?> {
            override fun onResponse(
                call: Call<ModelResponse?>,
                response: Response<ModelResponse?>
            ) {
                dialogProgress!!.dismiss()
                val responses: ModelResponse? = response.body()

                if (responses != null && responses.error!!.equals(false)) {

//                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()

                    Utility.setSharedPreference(context, Constants.KEY_START, "5")
                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, address)
                    val intent = Intent(context, ConfirmDetails::class.java)
                    startActivity(intent)

                } else if (responses != null && responses.error!!.equals(true)) {
                    dialogProgress!!.dismiss()
                    Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                dialogProgress!!.dismiss()
                Log.e("model", "onFailure    " + t.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 170) {
            if (resultCode == 160) {
                val contents = data?.getStringExtra("ResultText")
                if (contents != null) {
                    address = contents
                    et_address.setText(address)
                    et_address.setSelection(et_address.length())
                    Log.e("contents", contents)
                }
            }

        } else if (requestCode == 101) {
            val intent = Intent(context, QRCodeScannerActivity::class.java)
            startActivityForResult(intent, 170)

        } else if (requestCode == 104) {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            123 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {

                    val intent = Intent(context, QRCodeScannerActivity::class.java)
                    startActivityForResult(intent, 170)
                    // permission was granted, yay! do the
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    dialogRequestPermissionCamera()
                }
                return
            }
            456 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                    // permission was granted, yay! do the
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    dialogRequestPermission()
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

        tvDescription.text = "Camera & Storage Permission access must be allowed to use the Liberty App. " +
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

    private fun dialogRequestPermission() {
        val dialog = Dialog(context)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_runtime_permission)

        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSetting = dialog.findViewById(R.id.tvSetting) as TextView

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
