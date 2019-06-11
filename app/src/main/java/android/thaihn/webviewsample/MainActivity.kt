package android.thaihn.webviewsample

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var superSafeWebView: WebView
    private var safeBrowsingIsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        superSafeWebView = WebView(this)
        setContentView(superSafeWebView)

        superSafeWebView.webViewClient = MyWebViewClient(applicationContext)
        superSafeWebView.webChromeClient = MyWebChromeClient()

        superSafeWebView.settings.javaScriptEnabled = true
        superSafeWebView.addJavascriptInterface(WebAppInterface(applicationContext), "Android")
        safeBrowsingIsInitialized = false
        superSafeWebView.loadUrl("https://dantri.com.vn/")

        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this) { success ->
                safeBrowsingIsInitialized = true
                if (!success) {
                    Log.d(TAG, "Unable to initialize Safe Browsing")
                }
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && superSafeWebView.canGoBack()) {
            superSafeWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }
}
