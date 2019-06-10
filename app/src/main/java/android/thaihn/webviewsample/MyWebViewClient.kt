package android.thaihn.webviewsample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import androidx.webkit.SafeBrowsingResponseCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature

class MyWebViewClient(private val mContext: Context) : WebViewClientCompat() {

    companion object {
        private val TAG = MyWebViewClient::class.java.simpleName
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Log.d(TAG, "shouldOverrideUrlLoading(): url:$url --- host:${Uri.parse(url).host}")
        if (Uri.parse(url).host == "m.dantri.com.vn") {
            // This is my web site, so do not override; let my WebView load the page
            return false
        }

        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(this)
        }
        return true
    }

    override fun onSafeBrowsingHit(view: WebView, request: WebResourceRequest, threatType: Int, callback: SafeBrowsingResponseCompat) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
            callback.backToSafety(true)
            Toast.makeText(mContext, "Unsafe web page blocked", Toast.LENGTH_SHORT).show()
        }
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }
}
