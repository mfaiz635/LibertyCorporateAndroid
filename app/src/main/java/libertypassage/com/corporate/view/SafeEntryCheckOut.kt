package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.safe_entry_checkin.*
import kotlinx.android.synthetic.main.safe_entry_checkout.*
import kotlinx.android.synthetic.main.safe_entry_checkout.ivFav
import kotlinx.android.synthetic.main.safe_entry_checkout.ivNotFav
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelSafeEntry
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import java.text.SimpleDateFormat
import java.util.*


class SafeEntryCheckOut : AppCompatActivity(), View.OnClickListener {

    private lateinit var context: Context
    private var address = ""
    private var tempType = ""
    private var currentTemp = ""
    private var venueId = ""
    private var nricId = ""
    private var currentDate = ""
    private var checkout = 0
    private var favorite = 0
    private var position = 0



    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.safe_entry_checkout)
        context = this@SafeEntryCheckOut


        iv_back.setOnClickListener(this)
        tvBackToHome.setOnClickListener(this)
        ivNotFav.setOnClickListener(this)
        ivFav.setOnClickListener(this)

        address = intent.getStringExtra("address")!!
        tempType = intent.getStringExtra("tempType")!!
        currentTemp = intent.getStringExtra("currentTemp")!!
        venueId = intent.getStringExtra("venueId")!!
        nricId = intent.getStringExtra("nricId")!!
        currentDate = intent.getStringExtra("currentDate")!!
        checkout = intent.getIntExtra("checkout", 0)
        favorite = intent.getIntExtra("favorite", 0)
        position = intent.getIntExtra("position", 0)

        if(favorite==1){
            ivNotFav.visibility = View.GONE
            ivFav.visibility = View.VISIBLE
        }else{
            ivNotFav.visibility = View.VISIBLE
            ivFav.visibility = View.GONE
        }
        createEntry(favorite)

        val date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy")
        val tf = SimpleDateFormat("HH:mm a")
        val currentDate = df.format(date)
        val currentTime = tf.format(date)
        tvDate.text = currentDate
        tvTime.text = currentTime
        tvCompassOne.text = venueId
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
                createEntry(1)
            }

            R.id.ivFav -> {
                ivFav.visibility = View.GONE
                ivNotFav.visibility = View.VISIBLE
                createEntry(0)
            }

            R.id.tvBackToHome -> {
                finish()
            }
        }

    }

    private fun createEntry(favorite: Int) {
//        val listSize = Constants.safeEntryArrayList.size
        Constants.safeEntryArrayList.set(position, ModelSafeEntry(
            address,
            tempType,
            currentTemp,
            venueId,
            nricId,
            currentDate,
            checkout,
            favorite)
        )
        Utility.saveSafeEntry(context, Constants.safeEntryArrayList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

