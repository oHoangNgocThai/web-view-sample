package android.thaihn.webviewsample

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient

class MyWebChromeClient : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        consoleMessage?.apply {
            Log.d("MyApplication", "${message()} -- From line ${lineNumber()} of ${sourceId()}")
        }
        return true
    }
}
