package libertypassage.com.corporate.view

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import libertypassage.com.corporate.R


class TestingActivity : AppCompatActivity() {
    private lateinit var context: Context

   // testing content


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testing_activity)
        context = this@TestingActivity


    }
}
