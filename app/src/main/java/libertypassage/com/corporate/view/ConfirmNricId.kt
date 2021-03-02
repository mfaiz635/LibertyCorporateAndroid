package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.confirm_nric_id.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.model.ModelSafeEntry
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Constants.Companion.safeEntryArrayList
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ConfirmNricId : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var token = ""
    private var address = ""
    private var tempType = ""
    private var currentTemp = ""
    private var from = ""
    private var venueId = ""
    private var nricId = ""



    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_nric_id)
        context = this@ConfirmNricId

        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        tempType = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE)!!
        currentTemp = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP)!!
        nricId = Utility.getSharedPreferences(context, Constants.KEY_NRIC_ID)!!
        dialogProgress = DialogProgress(context)

        address = intent.getStringExtra("address")!!
        venueId = intent.getStringExtra("venueId")!!
        from = intent.getStringExtra("from")!!


        if(nricId.isEmpty()){
            tvNricId.text = "Provide your NRIC Id"
        }else{
            tvNricId.text = "Confirm your NRIC Id"
            etNricId.setText(nricId)
        }
        etNricId.isFocusable = false
        etNricId.setOnTouchListener { v, event ->
            etNricId.isFocusableInTouchMode = true
            false
        }

        iv_back.setOnClickListener(this)
        tvNext.setOnClickListener(this)
        tvTerms31.setOnClickListener(this)
        tvTerms32.setOnClickListener(this)
        tvTerms33.setOnClickListener(this)
    }


    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tvNext -> {
                nricId = etNricId.text.toString().trim()
                if (nricId == "") {
                    Toast.makeText(context, "Require NRIC Id", Toast.LENGTH_LONG).show()
                } else {
                    addAddressAndTemp()
                }
            }

            R.id.tvTerms31 -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.safeentry-qr.gov.sg/termsofuse")))
            }
            R.id.tvTerms32 -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.safeentry-qr.gov.sg/termsofuse")))
            }
            R.id.tvTerms33 -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.safeentry-qr.gov.sg/termsofuse")))
            }
        }
    }

    private fun addAddressAndTemp() {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelResponse> = apiInterface.addAddressTemp(
            Constants.KEY_HEADER + token,
            Constants.KEY_BOT,
            address,
            tempType,
            currentTemp,
            venueId,
//            "STG-180000001W-83338-SEQRSELFTESTSINGLE-SE",
            nricId,
            "checkin"
        )

        call.enqueue(object : Callback<ModelResponse?> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<ModelResponse?>,
                response: Response<ModelResponse?>
            ) {
                dialogProgress!!.dismiss()
                val responses: ModelResponse? = response.body()

                if (responses != null && responses.error!!.equals(false)) {

                    Utility.setSharedPreference(context, Constants.KEY_CURRENT_LOC, address)
                    Utility.setSharedPreference(context, Constants.KEY_NRIC_ID, nricId)

                    val date = Calendar.getInstance().time
                    val df = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    val currentDate = df.format(date)

                    safeEntryArrayList.add(
                        ModelSafeEntry(
                            address,
                            tempType,
                            currentTemp,
                            venueId,
                            nricId,
                            currentDate,
                            0,
                            0
                        )
                    )
                    Utility.saveSafeEntry(context, safeEntryArrayList)

                    val dfSend = SimpleDateFormat("dd-MMM-yyyy")
                    val tfSend = SimpleDateFormat("HH:mm a")
                    val currentDateSend = dfSend.format(date)
                    val currentTimeSend = tfSend.format(date)

                    val intent = Intent(context, SafeEntryCheckIn::class.java)
                    intent.putExtra("address", address)
                    intent.putExtra("tempType", tempType)
                    intent.putExtra("currentTemp", currentTemp)
                    intent.putExtra("venueId", venueId)
                    intent.putExtra("nricId", nricId)
                    intent.putExtra("currentDate", currentDate)
                    intent.putExtra("checkout", 0)
                    intent.putExtra("favorite", 0)
                    intent.putExtra("date", currentDateSend)
                    intent.putExtra("time", currentTimeSend)
                    startActivity(intent)
                    finish()

                    if (from == "myAddress") {
                        Utility.setSharedPreference(context, Constants.KEY_START, "5")
                        val intent = Intent(context, ConfirmDetails::class.java)
                        startActivity(intent)
                        finish()
                    } else if (from != "home") {
                        val intent = intent
                        intent.putExtra("currentTemp", currentTemp)
                        intent.putExtra("tempType", tempType)
                        setResult(111, intent)
                        finish()
                    } else {
                        finish()
                    }

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
