package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Utility
import kotlinx.android.synthetic.main.my_temperture.*
import libertypassage.com.corporate.utilities.CircleSeekBar


class MyTemperature : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    private var seekBar: CircleSeekBar? = null
    private var tempType = "Celsius"
    private var currentTemp = "30.00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_temperture)
        context = this@MyTemperature

        seekBar = findViewById(R.id.seekBar)
        iv_back.setOnClickListener(this)
        tvCelcius.setOnClickListener(this)
        tvFarenheit.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        if (tempType == "Celsius") {
            tv_tempTpye.text = "Celsius"
        } else if (tempType == "Fahrenheit") {
            tv_tempTpye.text = "Fahrenheit"
        }
        tv_tempValue.text = 30.00.toString()
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

            R.id.tvCelcius -> {
                tempType = "Celsius"
                tv_tempValue.text = 30.00.toString()
                seekBar!!.curProcess = 0.00
                tv_tempTpye.text = "Celsius"
                tvCelcius.background = resources.getDrawable(R.drawable.rounded_gray_button)
                tvFarenheit.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
            }

            R.id.tvFarenheit -> {
                tempType = "Fahrenheit"
                tv_tempValue.text = 90.00.toString()
                seekBar!!.curProcess = 0.00
                tv_tempTpye.text = "Fahrenheit"
                tvCelcius.background = resources.getDrawable(R.drawable.rounded_gray_trans_button)
                tvFarenheit.background = resources.getDrawable(R.drawable.rounded_gray_button)
            }


            R.id.tv_next -> {
                currentTemp = tv_tempValue.text.toString()
                val temp = currentTemp.toDouble()

                if (tempType == "Celsius" && temp < 35) {
                    Toast.makeText(context,"Please select your correct temperature", Toast.LENGTH_LONG).show()
                } else if (tempType == "Fahrenheit" && temp < 90) {
                    Toast.makeText(context,"Please select your correct temperature", Toast.LENGTH_LONG).show()
                } else {
                    Utility.setSharedPreference(context, Constants.KEY_START, "4")
                    Utility.setSharedPreference(context, Constants.KEY_MY_TEMP, currentTemp)
                    Utility.setSharedPreference(context, Constants.KEY_TEMP_TYPE, tempType)
                    val intent = Intent(context, MyAddress::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
