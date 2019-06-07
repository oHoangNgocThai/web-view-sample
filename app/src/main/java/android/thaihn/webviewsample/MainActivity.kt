package android.thaihn.webviewsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true


        webview.loadUrl("https://dantri.com.vn/")
    }
}
