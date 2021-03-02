package libertypassage.com.corporate.corporate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.c_confirm_employer.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.LogInActivity
import libertypassage.com.corporate.view.SignUpActivity


class ConfirmEmployer : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var userExist = ""
    var companyName = ""
    var companyAddress = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_confirm_employer)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@ConfirmEmployer

        userExist = Utility.getSharedPreferences(context, "userExist")!!
        companyName = Utility.getSharedPreferences(context, "companyName")!!
        companyAddress = Utility.getSharedPreferences(context, "companyAddress")!!
        tv_companyName.text = companyName
        tv_address.text = companyAddress

        if (userExist == "true") {
            tv_continue.text = "CONTINUE"
        } else if (userExist == "false") {
            tv_continue.text = "START YOUR REGISTRATION"
        }

        iv_back.setOnClickListener(this)
        tv_continue.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_continue -> {
                if (userExist == "true") {
                    val intent = Intent(context, LogInActivity::class.java)
                    startActivity(intent)
                } else if (userExist == "false") {
                    val intent = Intent(context, SignUpActivity::class.java)
                    startActivity(intent)
                }
            }
        }
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
