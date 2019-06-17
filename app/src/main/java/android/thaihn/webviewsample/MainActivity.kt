package android.thaihn.webviewsample

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var safeBrowsingIsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        setupWebView()
    }

    private fun setupWebView() {
        mWebView.settings.apply {
            setSupportZoom(true)
            builtInZoomControls = true
            loadsImagesAutomatically = true
            domStorageEnabled = true
            setAppCacheEnabled(true)

            val appCachePath = applicationContext.cacheDir.absolutePath
            Log.d(TAG, "Cache path: $appCachePath")
            setAppCachePath(appCachePath)
            allowFileAccess = true

            javaScriptEnabled = true
        }

        mWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        mWebView.isScrollbarFadingEnabled = true

        mWebView.webViewClient = MyWebViewClient(applicationContext)
        mWebView.webChromeClient = MyWebChromeClient()

        if (isOnline()) {
            mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT

            mWebView.addJavascriptInterface(WebAppInterface(applicationContext), "Android")

            mWebView.loadUrl("https://m.dantri.com.vn/")
        } else {
            mWebView.settings.cacheMode = WebSettings.LOAD_CACHE_ONLY
            mWebView.loadUrl("https://m.dantri.com.vn/")
        }

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
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
