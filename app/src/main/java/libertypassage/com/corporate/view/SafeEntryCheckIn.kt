package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.safe_entry_checkin.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.model.ModelSafeEntry
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SafeEntryCheckIn : AppCompatActivity(), View.OnClickListener {

    private lateinit var context: Context
    var dialogProgress: DialogProgress? = null
    private var token = ""
    private var date = ""
    private var time = ""

    private var address = ""
    private var tempType = ""
    private var currentTemp = ""
    private var venueId = ""
    private var nricId = ""
    private var currentDate = ""
    private var checkout = 0
    private var favorite = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.safe_entry_checkin)
        context = this@SafeEntryCheckIn
        dialogProgress = DialogProgress(context)

        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
        address = intent.getStringExtra("address")!!
        tempType = intent.getStringExtra("tempType")!!
        currentTemp = intent.getStringExtra("currentTemp")!!
        venueId = intent.getStringExtra("venueId")!!
        nricId = intent.getStringExtra("nricId")!!
        currentDate = intent.getStringExtra("currentDate")!!
        checkout = intent.getIntExtra("checkout", 0)
        favorite = intent.getIntExtra("favorite", 0)

        date = intent.getStringExtra("date")!!
        time = intent.getStringExtra("time")!!

        tvDateIn.text = date
        tvTimeIn.text = time
        tvTagIn.text = venueId

        iv_back.setOnClickListener(this)
        rlBtnCheckout.setOnClickListener(this)
        ivNotFav.setOnClickListener(this)
        ivFav.setOnClickListener(this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.ivNotFav -> {
                ivNotFav.visibility = View.GONE
                ivFav.visibility = View.VISIBLE
                favorite=1
                createEntry(1)
            }

            R.id.ivFav -> {
                ivFav.visibility = View.GONE
                ivNotFav.visibility = View.VISIBLE
                favorite=0
                createEntry(0)
            }

            R.id.rlBtnCheckout -> {
                val listSize = Constants.safeEntryArrayList.size
                addAddressAndTemp(
                    address,
                    tempType,
                    currentTemp,
                    venueId,
                    nricId,
                    currentDate,
                    1,
                    favorite,
                    listSize-1)
            }
        }
    }

    private fun createEntry(favorite: Int) {
        val listSize = Constants.safeEntryArrayList.size
        Constants.safeEntryArrayList.set(listSize-1, ModelSafeEntry(
            address,
            tempType,
            currentTemp,
            venueId,
            nricId,
            currentDate,
            0,
            favorite)
        )
        Utility.saveSafeEntry(context, Constants.safeEntryArrayList)
    }

    private fun addAddressAndTemp(
        address: String,
        tempType: String,
        currentTemp: String,
        venueId: String,
        nircId: String,
        currentDate: String,
        checkout: Int,
        favorite: Int,
        position: Int
    ) {
        dialogProgress!!.show()
        val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
        val call: Call<ModelResponse> = apiInterface.addAddressTemp(Constants.KEY_HEADER+token,
            Constants.KEY_BOT, address, tempType, currentTemp, venueId, nircId, "checkout")

        call.enqueue(object : Callback<ModelResponse?> {
            override fun onResponse(call: Call<ModelResponse?>, response: Response<ModelResponse?>) {
                dialogProgress!!.dismiss()
                val responses: ModelResponse? = response.body()

                if (responses != null && responses.error!!.equals(false)) {

                    Constants.safeEntryArrayList.set(position, ModelSafeEntry(
                        address,
                        tempType,
                        currentTemp,
                        venueId,
                        nircId,
                        currentDate,
                        1,
                        favorite))
                    Utility.saveSafeEntry(context, Constants.safeEntryArrayList)
                    val intent = Intent(context, SafeEntryCheckOut::class.java)
                    intent.putExtra("address", address)
                    intent.putExtra("tempType", tempType)
                    intent.putExtra("currentTemp", currentTemp)
                    intent.putExtra("venueId", venueId)
                    intent.putExtra("nricId", nircId)
                    intent.putExtra("currentDate", currentDate)
                    intent.putExtra("checkout", checkout)
                    intent.putExtra("favorite", favorite)
                    intent.putExtra("position", position)
                    startActivity(intent)
                    finish()

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

