package android.thaihn.webviewsample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
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

    private var mWebView: WebView? = null

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

    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }
        super.onRenderProcessGone(view, detail)

        detail?.let {
            if (!it.didCrash()) {
                // Renderer was killed because the system ran out of memory.
                // The app can recover gracefully by creating a new WebView instance
                // in the foreground.

                Log.d(TAG, "onRenderProcessGone(): System killed the WebView rendering process to reclaim. Recreating...")
                mWebView?.let { webView ->
                    val webViewContainer: ViewGroup = webView.parent as ViewGroup
                    webViewContainer.removeView(webView)
                    webView.destroy()
                    mWebView = null
                }

                // By this point, the instance variable "mWebView" is guaranteed
                // to be null, so it's safe to reinitialize it.

                return true
            }
        }

        // Renderer crashed because of an internal error, such as a memory access violation.
        Log.e("MY_APP_TAG", "The WebView rendering process crashed!")

        // In this example, the app itself crashes after detecting that the
        // renderer crashed. If you choose to handle the crash more gracefully
        // and allow your app to continue executing, you should 1) destroy the
        // current WebView instance, 2) specify logic for how the app can
        // continue executing, and 3) return "true" instead.

        return false
    }
}
