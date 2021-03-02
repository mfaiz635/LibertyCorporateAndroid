package libertypassage.com.corporate.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.about_app.*
import kotlinx.android.synthetic.main.activity_header.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.services.MyService


class AboutAppActivity : AppCompatActivity(), View.OnClickListener{

    private var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_app)

        iv_back.setOnClickListener(this)

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
        }
    }

}

