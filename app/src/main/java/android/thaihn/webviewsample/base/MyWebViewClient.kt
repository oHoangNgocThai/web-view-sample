package android.thaihn.webviewsample.base

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if(Uri.parse(url).host == "dantri.com.vn") {

        }
        return super.shouldOverrideUrlLoading(view, url)
    }
}
