package libertypassage.com.corporate.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.confirm_details.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.utilities.AlarmReceiver
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import java.util.*


class ConfirmDetails : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_details)
        context = this@ConfirmDetails

        iv_back.setOnClickListener(this)
        iv_temp_edit.setOnClickListener(this)
        iv_location_edit.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        rl_restart.setOnClickListener(this)

        tvTemperature.text = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP)
        tvTemperatureType.text = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE)
        tvAddress.text = Utility.getSharedPreferences(context, Constants.KEY_CURRENT_LOC)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.iv_temp_edit -> {
                val intent = Intent(context, UpdateMyTemperature::class.java)
                intent.putExtra("from", "confir_details")
                startActivityForResult(intent, 110)
            }

            R.id.iv_location_edit -> {
                val intent = Intent(context, UpdateAddress::class.java)
                intent.putExtra("from", "confir_details")
                startActivityForResult(intent, 120)
            }

            R.id.rl_restart -> {
                val intent = Intent(context, EnrolmentDeclaration::class.java)
                startActivity(intent)
                finish()
            }

            R.id.tv_next -> {
                localNotification()
            }

        }
    }


    override fun onResume() {
        super.onResume()
        tvTemperature.text = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP)
        tvTemperatureType.text = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE)
        tvAddress.text = Utility.getSharedPreferences(context, Constants.KEY_CURRENT_LOC)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 110) {
            if (resultCode == 111) {
                val currentTemp = data?.getStringExtra("currentTemp")
                val tempType = data?.getStringExtra("tempType")
                if (currentTemp != null) {
                    tvTemperature.text = currentTemp
                    tvTemperatureType.text = tempType
                }
            }
        } else if (requestCode == 120) {
            if (resultCode == 121) {
                val contents = data?.getStringExtra("address")
                if (contents != null) {
                    tvAddress.text = contents
                }
            }
        }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
