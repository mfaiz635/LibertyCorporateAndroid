package libertypassage.com.corporate.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.about_app.*
import kotlinx.android.synthetic.main.activity_header.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.services.MyService
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.CustomCheckBox
import libertypassage.com.corporate.utilities.Utility


class AboutAppActivity : AppCompatActivity(), View.OnClickListener{

    private var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_app)

        iv_back.setOnClickListener(this)
        tvConsentForm.setOnClickListener(this)
        tvPrivacyPolicy.setOnClickListener(this)

        location_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                location = "1"
            } else {
                stopService(Intent(this@AboutAppActivity, MyService::class.java))
                Toast.makeText(this, "Turn off background location successfully", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tvConsentForm -> {
                dialogTerms()
            }
            R.id.tvPrivacyPolicy -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://passageliberty.azurewebsites.net/privacypolicy")))
            }
        }
    }


    private fun dialogTerms() {
        val dialog = Dialog(this@AboutAppActivity)
        dialog.setContentView(R.layout.dialog_terms_about)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val tvSafeEntry = dialog.findViewById<TextView>(R.id.tvSafeEntry)
        val tvPrivacyPolicy = dialog.findViewById<TextView>(R.id.tvPrivacyPolicy)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)

        tvSafeEntry.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.safeentry.gov.sg/")))
        }

        tvPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://passageliberty.azurewebsites.net/privacypolicy")))
        }

        tvOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}

