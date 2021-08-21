package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.privacy_policy.*
import libertypassage.com.corporate.R


class PrivacyPolicyActivity : AppCompatActivity() {

    private var urlStr = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy)

        urlStr = intent.getStringExtra("url")!!
        this.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(urlStr)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

