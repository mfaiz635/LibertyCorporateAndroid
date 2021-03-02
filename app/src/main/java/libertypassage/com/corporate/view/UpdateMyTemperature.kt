package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.update_my_temperture.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.utilities.CircleSeekBar
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility


class UpdateMyTemperature : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    private var seekBar: CircleSeekBar? = null
    private var tempType = "Celsius"
    private var currentTemp = "30.00"
    private var from = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_my_temperture)
        context = this@UpdateMyTemperature

        seekBar = findViewById(R.id.seekBar)
        iv_back.setOnClickListener(this)
        tv_celcius.setOnClickListener(this)
        tv_farenheit.setOnClickListener(this)
        tv_next.setOnClickListener(this)

        init()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() {
        tempType = Utility.getSharedPreferences(context, Constants.KEY_TEMP_TYPE)!!
        currentTemp = Utility.getSharedPreferences(context, Constants.KEY_MY_TEMP)!!
        from = intent.getStringExtra("from")!!
        tv_tempTpye.text = tempType
        tv_tempValue.text = currentTemp

        if (tempType == "Celsius") {
            val temps = currentTemp.toFloat()- 30.00
            seekBar!!.curProcess = temps
            tv_celcius.background = resources.getDrawable(R.drawable.rounded_gray_button)
            tv_farenheit.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
        } else if (tempType == "Fahrenheit") {
            val temps = currentTemp.toFloat() - 90.00
            seekBar!!.curProcess = temps
            tv_celcius.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
            tv_farenheit.background = resources.getDrawable(R.drawable.rounded_gray_button)
        }



        seekBar?.setOnSeekBarChangeListener(object : CircleSeekBar.OnSeekBarChangeListener {
            override fun onChanged(seekbar: CircleSeekBar?, curValue: Double) {
                if (tempType == "Celsius") {
                    tv_tempValue.text = (curValue + 30.00).toString()
                } else if (tempType == "Fahrenheit") {
                    tv_tempValue.text = (curValue + 90.00).toString()
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tv_celcius -> {
                tempType = "Celsius"
                tv_tempValue.text = 30.00.toString()
                seekBar!!.curProcess = 0.00
                tv_tempTpye.text = "Celsius"
                tv_celcius.background = resources.getDrawable(R.drawable.rounded_gray_button)
                tv_farenheit.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
            }

            R.id.tv_farenheit -> {
                tempType = "Fahrenheit"
                tv_tempValue.text = 90.00.toString()
                seekBar!!.curProcess = 0.00
                tv_tempTpye.text = "Fahrenheit"
                tv_celcius.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
                tv_farenheit.background = resources.getDrawable(R.drawable.rounded_gray_button)
            }

            R.id.tv_next -> {
                currentTemp = tv_tempValue.text.toString()
                val temp = currentTemp.toDouble()

                if (tempType == "Celsius" && temp < 35) {
                    Toast.makeText(
                        context,
                        "Please select your correct temperature",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (tempType == "Fahrenheit" && temp < 90) {
                    Toast.makeText(
                        context,
                        "Please select your correct temperature",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, currentTemp)
                    Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, tempType)

                    val intent = Intent(context, UpdateAddress::class.java)
                    intent.putExtra("from", from)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
