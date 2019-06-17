# Web View Sample

## Overview

* Android cung cấp việc hiển thị nội dung của trang web theo 2 cách sau: 

    * Android Browser: Truy cập trang web bằng browser trên thiết bị để xem được nội dung.
    * Android app: Sử dụng ứng dụng android và hiển thị nội dung của web thông qua WebView.
    
* Tuy nhiên cũng không phải lúc nào cũng sử dụng WebView, còn có các sự lựa chọn khác để có thể phù hợp với từng yêu cầu cụ thể như: 

    * Nếu bạn muốn gửi người dùng đến một trang web di động, hãy xây dựng một ứng dụng [Progressive Web App](https://developers.google.com/web/progressive-web-apps/)
    * Nếu muốn hiển thị nội dung trang web của bên thứ 3, hãy sử dụng Intent để gửi nội dung hiển thị cho các trình duyệt.
    * Nếu muốn tránh để ứng dụng của mình mở trình duyệt hoặc nếu muốn tùy chỉnh giao diện người dùng, hãy sử dụng [Chrome Custom Tab](https://developer.chrome.com/multidevice/android/customtabs)

## Build web app in WebView

* Nếu bạn muốn sử dụng ứng dụng web hoặc là 1 trang web như là 1 phần của ứng dụng client, bạn có thể sử dụng WebView. WebView là class mở rộng từ class View của Android, cho phép bạn hiển thị nội dung trang web như là 1 phần của layout.

* Nó không bao gồm bất kì tính năng nào của trình duyệt web như điều khiển điều hướng hoặc thanh địa chỉ, theo mặc định nó chỉ hiển thị phần nội dung của trang web.

* WebView được sử dụng trong ứng dụng android để hiển thị trang web, thông thường sẽ là những nội dung ít tương tác và chỉ mang tính thông báo như giới thiệu hoặc quy định gì đó cần cập nhật.

### Adding a WebView to your app

* Để thêm WebView vào trong ứng dụng, ta thêm thẻ **WebView** vào trong giao diện xml muốn sử dụng.

```
<WebView
    android:id="@+id/webview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
/>
```

* Tiếp đó muốn hiển thị 1 trang web, hãy sử dụng phương thức **loadUrl()** với tham số là URL muốn hiển thị nội dung.

```
myWebView.loadUrl("http://www.example.com")
```

* Hoặc có thể sử dụng để hiển thị HTML string như sau bằng phương thức **loadData()**: 

```
val unencodedHtml =
        "&lt;html&gt;&lt;body&gt;'%23' is the percent code for ‘#‘ &lt;/body&gt;&lt;/html&gt;"
val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
myWebView.loadData(encodedHtml, "text/html", "base64")
```

* Trước khi run cũng đừng cấp cho ứng dụng quyền sử dụng Internet để tải được nội dung về.

```
<manifest ... >
    <uses-permission android:name="android.permission.INTERNET" />
    ...
</manifest>
```

* Đó là những gì bạn cần để hiển thị 1 trang web. Ngoài ra bạn có thể tùy chỉnh như sau: 

    * Kích hoạt hỗ trợ toàn màn hình với [WebChromeClient](https://developer.android.com/reference/android/webkit/WebChromeClient), lớp này cũng được gọi khi WebView cần quyền để thay đổi UI ứng dụng máy chủ, chẳng hạn như tạo hoặc đóng cửa sổ và gửi hộp thoại JavaScript cho người dùng.
    * Xử lý các sự kiện ảnh hưởng đến việc render ra nội dung, chẳng hạn lỗi như gửi biểu mẫu hoặc điều hướng với [WebViewClient]() 
    * Kích hoạt Javascript bằng cách sửa đổi [WebSetting](https://developer.android.com/reference/android/webkit/WebSettings)
    * Sử dụng Javascript để truy cập vào Android framework mà bạn đã đưa vào WebView.
    
### Using JavaScript in WebView

* Nếu trang web của bạn dự định hiển thị WebView sử dụng JavaScript, bạn phải bật JavaScript cho WebView của bạn. Một khi đã bật JavaScript, bạn có thể tạo interface giữa code trong app và JavaScript code.

* Để bật JavaScript, ta cần thay đổi thuộc tính **javaScriptEnable** có trong WebSetting.

```
myWebView.settings.javaScriptEnabled = true
```

* Để liên kết code giữa JavaScript và Client code, hãy sử dụng phương thức **addJavascriptInterface()** để chuyển qua class mà bạn định nghĩa để liên kết với JavaScript.

```
class WebAppInterface(private val mContext: Context) {

    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}
```
> Nếu bạn đặt **targetSdkVersion** là 17 hoặc cao hơn, bạn phải thêm annotation **@JavascriptInterface** vào bất kì phương thức nào bạn muốn JavaScript có thể gọi được. Nếu không được cung cấp annotation, web page của bạn không thể truy cập được các phương thức khi android từ 4.2 trở lên.

* Thêm interface vào WebView có thể đặt thêm định danh người dùng vào như **Android** hoặc **IOS**.

```
webView.addJavascriptInterface(WebAppInterface(this), "Android")
```

* Nếu là trang web của bạn, hãy thêm đoạt JavaScript này để chắc rằng hàm toast chúng ta mới tạo sẽ được gọi từ JavaScript.

```
<input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />

<script type="text/javascript">
    function showAndroidToast(toast) {
        Android.showToast(toast);
    }
</script>
```

### Handling page navigation

* Khi người dùng nhấn vào liên kết từ 1 trang web trong WebView của bạn, lúc này bạn sẽ phải handle việc back lại trang trước như thế nào. Thông thường trình duyệt web sẽ tải một trang web mới với url đã click.
> Vì lý do bảo mật, hệ thống của trình duyệt sẽ không chia sẻ dữ liệu ứng dụng của nó với ứng dụng android của bạn.

* Để mở 1 link khi click bởi người dùng, cung cấp một **WebViewClient** cho WebView của bạn bằng phương thức **setWebViewClient()**.

```
webView.webViewClient = WebViewClient()

class MyWebViewClient(private val mContext: Context) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
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
}
```
> Khi nhấp vào liên kết, hệ thống sẽ gọi hàm trên xem có nên override Url trước đó không, Nếu nó khớp với tên miền trước đó thì không ghi đè nên, còn nếu khác thì sẽ mở 1 intent đến activity xử lý link chứa nội dung cần xem.

* Nếu muốn khi nhấn back lại thì sẽ load trang web trước đó được load, chúng ta cần xử lý sự kiện click vào phím back bằng phương thức **onKeyDown()**. Khi WebView ghi đè tải URL, nó sẽ tự động tích lũy lịch sử của các trang web bạn đã truy cập. Bạn có thể điều hướng lùi và chuyển tiếp qua lịch sử với các phương thức **goBack()**, **goForward()**.

```
override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
        webview.goBack()
        return true
    }
    // If it wasn't the Back key or there's no web page history, bubble up to the default
    // system behavior (probably exit the activity)
    return super.onKeyDown(keyCode, event)
}
```
> Phương thức **canGoBack()** hoặc **canGoForward()** để xác định xem WebView có thể back hoặc forward hay không, nếu có sẽ trả về true.

* Trong khi chạy ứng dụng, sẽ có trường hợp thay đổi trạng thái hoạt động khi cấu hình thiết bị thay đổi, chẳng hạn như người dùng xoay màn hình hoặc loại bỏ phương thức nhập(IME). Những thay đổi này sẽ khiến hoạt động của đối tượng WebView bị hủy và 1 hoạt động khác được tạo, điều này sẽ tạo ra đối tượng mới tải lại url của đối tượng cũ. Để sửa đổi hành vi mặc định này, bạn có thể thay đổi trong tệp [configuration](https://developer.android.com/guide/topics/resources/runtime-changes)

### Managing windows 

Theo mặc định, các yêu cầu mở cửa sổ mới được bỏ qua. Điều này đúng cho dù chúng được mở bằng JavaScript hoặc mởi thuộc tính đích trong 1 liên kết. Bạn có thể tùy chỉnh [WebChromeClient](https://developer.android.com/reference/android/webkit/WebChromeClient) để cung cấp hành vi của bạn.

## Managing WebView

#### Version API

* Thêm thư viện [Webkit](https://developer.android.com/reference/androidx/webkit/package-summary) của AndroidX để sử dụng các phương thức control WebView. 

```
implementation 'androidx.webkit:webkit:1.0.0'
```

* Bắt đầu từ Android 7.0 (API level 24), người dùng có thể chọn số package khác nhau có thể hiển thị nội dung trên WebView. Thư viện AndroidX webkit bao gồm phương thức **getClientWebViewPackage()** để tìm nạp thông tin liên quan đến gói đang hiển thị nội dung web trong ứng dụng.

```
val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(appContext)
Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo.versionName}")
```

#### Google Safe Browsing Service

* Để cung cấp cho người dùng của bạn trải nghiệm an toàn hơn, các đối tượng WebView xác minh URL bằng Google Safe Browsing, cho phép hiển thị cảnh báo đối với người dùng khi họ cố mở 1 trang web có khả năng không an toàn.

* Mặc dù giá trị mặc định của **EnableSafeBrowsing** là true, đôi khi bạn chỉ muốn bật duyệt web an toàn theo điều kiện hoặc vô hiệu hóa nó. Android 8.0 (API 26) hỗ trợ cao hơn bằng cách sử dụng phương thức **setSafeBrowsingEnables()** để chuyển đổi duyệt web an toàn cho 1 đối tượng WebView riêng lẻ.

* Nếu bạn muốn tất cả các đối tượng WebView từ chối kiểm tra duyệt web an toàn, bạn có thể thêm phần tử **<meta-data>** sau vào AndroidManifest.xml:

```
<meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
                   android:value="false" />
```

#### Defining programmatic actions

* Khi một instance của WebView cố tải 1 trang web được Google phân loại là mối đe dọa, WebView sẽ theo mặc định hiển thị 1 quảng cáo xen kẽ cảnh báo người dùng về mối đe dọa đã biết. Màn hình này cung cấp cho người dùng tùy chọn tải URL bằng mọi cách hoặc quay lại 1 cách an toàn.

* Nếu bạn đặt target Android 8.1 (API 27) hoặc cao hơn, bạn có thể định nghĩa cách ứng dụng của bạn đối phó với mối đe dọa đã biết.

    * Bạn có thể kiểm soát xem ứng dụng của mình có báo cáo các mối đe dọa đã biết đối với duyệt web an toàn hay không.
    * Bạn có thể để ứng dụng của mình thực hiện 1 hành động cụ thể, chẳng hạn như quay trở lại an toàn mỗi khi gặp nguy hiểm.
    
```
private lateinit var superSafeWebView: WebView
private var safeBrowsingIsInitialized: Boolean = false

override fun onCreate(savedInstanceState: Bundle?) {

    superSafeWebView = WebView(this)
    superSafeWebView.webViewClient = MyWebViewClient()
    safeBrowsingIsInitialized = false

    if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
        WebViewCompat.startSafeBrowsing(this, ValueCallback<Boolean> { success ->
            safeBrowsingIsInitialized = true
            if (!success) {
                Log.e("MY_APP_TAG", "Unable to initialize Safe Browsing!")
            }
        })
    }
}
```

* Thực hiện hành động quay lại trang trước đó khi gặp phải trường hợp web không an toàn ở trong WebViewClient.

```
override fun onSafeBrowsingHit(view: WebView, request: WebResourceRequest, threatType: Int, callback: SafeBrowsingResponseCompat) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
            callback.backToSafety(true)
            Toast.makeText(mContext, "Unsafe web page blocked", Toast.LENGTH_SHORT).show()
        }
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }
```

#### HTML5 Geolocation API

* Đối với những app chạy từ Android 6.0 (API 23) trở lên, Geolocation API chỉ hỗ trợ trên giao thức an toàn chẳng hạn như HTTPS. Mọi request tới Geolocation API đối với giao thức không an toàn sẽ tự động bị từ chối mà không cần gọi phương thức **onGeolocationPermissionsShowPrompt()**.

#### Opting out of metrics collection

* WebView có khả năng tải dữ liệu chẩn đoán ẩn danh trên Google khi người dùng đã đồng ý. Dữ liệu thu thập trên cơ sở mỗi ứng dụng đã khởi tạo WebView. Bạn có thể hủy tính năng này bằng cách tạo thẻ trong <applicaton>. 

```
<meta-data android:name="android.webkit.WebView.MetricsOptOut"
               android:value="true" />
```

#### Termination Handling API

* API này xử lý các trường hợp quá trình render cho 1 đối tượng WebView biến mất, vì hệ thống đã hủy trình render để lấy lại bộ nhớ cần thiết hoặc do chính trình render bị lỗi. Bằng cách sử dụng API này cho phép ứng dụng tiếp tục thực thi mặc dù quá trình render đã biến mất.
> Nếu ứng dụng của bạn tiếp tục thực thi sau khi trình render biến mất, instance của WebView được liên kết không thể được sử dụng lại, bất kể quá trình render bị hủy hay bị crash. Phải xóa instance cũ đi và khởi tạo 1 instance mới của WebView.

* Lưu ý rằng nếu trình render crash khi đang tải trang web, việc cố gắng tải lại trng đó có thể khiến instance WebView mới thực hiện giống như crash của trình render.

```
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
```

#### Renderer importance API

* Giờ dây, WebView hoạt đột ở chế độ [multiprocess](https://developer.android.com/preview/behavior-changes-all#security-all) bạn có thể linh hoạt trong cách ứng dụng của bạn xử lý tình huống hết bộ nhớ.

* Bạn có thể sử dụng Renderer Importance API, được giới thiệu trong Android 8.0, để cài đặt chính sách ưu tiên cho trình render được gán cho 1 đối tượng WebView cụ thể. 

* Cụ thể, bạn có thể muốn phần chính của ứng dụng tiếp tục thực thi khi trình render bị kill.

```
myWebView.setRendererPriorityPolicy(RENDERER_PRIORITY_BOUND, true)
```
> Trong đoạn mã trên, mức độ ưu tiên của trình render giống như mức độ ưu tiên mặc định cho ứng dụng. Đối số **true** sẽ giảm mức ưu tiên của trình render thành **RENDERER_PRIORITY_WAIVED** khi đối tượng WebVew được liên kết không còn hiển thị nữa.

## Migrating to WebView in Android 4.4

* Android 4.4 (API 19) giới thiệu phiên bản WebView mới dựa trên **Chromium**. Thay đổi này nhằm nâng cấp hiệu suất và tiêu chuẩn WebView hỗ trợ HTML5, CSS3 và JavaScript.

* Để giúp bạn có thể sửa các lỗi gặp phải khi di chuyển ứng dụng của bạn sang WebView trong Android 4.4,bạn có thể bật debugging thông qua Chrome trên máy tính để bàn bằng cách sử dụng hàm **setWebContentsDebuggingEnabled()**. Tính năng này trong WebView cho phép bạn kiểm tra và phân tích nội dung web, scripts và hoạt động của mạng trong khi chạy WebView. Xem thêm tại [đây](https://developers.google.com/web/tools/chrome-devtools/remote-debugging/?utm_source=dcc&utm_medium=redirect&utm_campaign=2016q3)

#### User Agent Changes

Nếu bạn không có nhu cầu thay đổi tác nhân người dùng, hãy sử dụng phương thức **getDefaultUserAgent()** để chỉ định người dùng mặc định. Còn nếu muốn ghi đè chuỗi tác nhân người dùng trong WebView, bạn có thể muốn sử dụng **getUserAgentString()**.

#### Multi-threading and Thread Blocking

* Bạn có thể gọi các phương thức của WebView từ bất kì luồng nào ngoài luồng UI của ứng dụng, nó có thể gây ra kết quả không mong muốn. Nếu ứng dụng của bạn có nhiều luồng, hãy sử dụng như sau: 

```
runOnUiThread {
    // Code for WebView goes here
}
```

* CŨng nên chắc chắn rằng bạn không bao giờ chặn UI thread. 1 tình huống trong đó là 1 số ứng dụng chờ đợi 1 cuộc gọi JavaScript.

```
// This code is BAD and will block the UI thread
webView.loadUrl("javascript:fn()")
while (result == null) {
    Thread.sleep(100)
}
```

* Thay vào đó bạn nên sử dụng 1 phương thức mới như **evaluateJavascript()** để chạy JavaScript không đồng bộ.

#### Custom URL Handling

* WebView mới áp dụng các hạn chế bổ sung khi yêu cầu tài nguyên và giải quyết các liên kết sử dụng custom URL schema. Ví dụ nếu bạn triển khai các phương thức như **shouldOverrideUrlLoading()** hoặc **shouldInterceptRequest()** thì WebView chỉ gọi chúng cho các URL hợp lệ.

* Nếu bạn đang sử dụng custom URL hoặc base URL và nhận thấy rằng ứng dụng của bạn sẽ nhận được ít cuộc gọi đến, hãy đảm bảo rằng các yêu cầu chỉ định URL hợp lệ với [RFC 3986](http://tools.ietf.org/html/rfc3986#appendix-A)

* Ví dụ dưới đây có thể WebView của bạn sẽ không gọi đến phương thức **shouldOverrideUrlLoading()**.

```
<a href="showProfile">Show Profile</a>
```

* Kết quả của việc click trên có thể khác nhau tùy trường hợp: 

    * Nếu bạn đã tải trang bằng cách gọi **loadData()** hoặc **loadDataWithBaseUrl()** với URL cơ sở không hợp lệ hoặc null thì bạn sẽ không nhận được cuộc gọi lại **shouldOverrideUrlLoading()** cho loại liên kết như này.
    * Nếu bạn đã tải trang bằng cách gọi **loadUrl()** hoặc cung cấp URL cơ sở hợp lệ với **loadDataWithBaseURL()** nhưng URL bạn nhận được sẽ tuyệt đối, liên quan đến trang hiện tại. Ví dụ URL bạn nhận được sẽ là **http://www.example.com/showProfile**.
    
* Thay vì sử dụng mỗi chuỗi đơn giản, bạn có thể sử dụng custom schema như dưới đây: 

```
<a href="example-app:showProfile">Show Profile</a>
```

* Khi đó việc handle URL bên trong phương thức **shouldOverrideUrlLoading()** như sau: 

```
// The URL scheme should be non-hierarchical (no trailing slashes)
const val APP_SCHEME = "example-app:"

override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
    return if (url?.startsWith(APP_SCHEME) == true) {
        urlData = URLDecoder.decode(url.substring(APP_SCHEME.length), "UTF-8")
        respondToData(urlData)
        true
    } else {
        false
    }
}
```

* Nếu bạn không thể thay đổi HTML thì bạn có thể sử dụng **loadDataWithBaseURL()** và đặt base URL bao gồm custom schema và host phù hợp.

```
webView.loadDataWithBaseURL("example-app://example.co.uk/", HTML_DATA, null, "UTF-8", null)
```

#### Viewport Changes

* Trước đây, nếu bạn đặt chiều rộng khung nhìn của bạn thành 1 giá trị nhỏ hơn hoặc bằng "320", nó sẽ được đặt thành chiều rộng thiết bị. Và nếu bạn đặt chiều cao của khung nhìn thành 1 giá trị nhỏ hơn hoặc bằng chiều cao của WebView, nó sẽ được đặt thành **chiều cao thiết bị**. Nhưng trong WebView mới thì giá trị chiều rộng hoặc chiều cao được tuân thủ và WebView phóng to để lấp đầy chiều rộng màn hình.

* Trước đây nếu bao gồm nhiều thẻ trong 1 trang web, WebView sẽ hợp nhất các thuộc tính từ tất cả các thẻ. Trong WebView mới, chỉ có chế độ xem cuối cùng được sử dụng và tất cả các chế độ xem khác được bỏ qua.

* Phương thức **getDefaultZoom()** và **setDefaultZoom()** không còn được hỗ trợ nữa và thay vào đó bạn nên xác định [chế độ xem](https://developer.chrome.com/multidevice/webview/pixelperfect) phù hợp cho trang web. Nếu bạn không thể đặt độ rộng của chế độ xem trong HTML, bạn bên gọi **setUserWideViewPort()** để dảm bảo trang được cung cấp chế độ xem lớn hơn.

```
webView.settings.apply {
    useWideViewPort = true
    loadWithOverviewMode = true
}
```

## Debugging web apps

* Nếu bạn đang test ứng dụng web của mình với thiết bị Android 4.4 trở lên, bạn có thể gỡ lỗi từ xa các trang web bằng **Chrome Developer Tools**, trong khi tiếp tục hỗ trợ các phiên bản Android cũ hơn. Xem thêm tại [đây](https://developers.google.com/web/tools/chrome-devtools/remote-debugging/?utm_source=dcc&utm_medium=redirect&utm_campaign=2016q3)

* Nếu bạn không có thiết bị chạy Android 4.4 trở lên, bạn có thẻ gỡ lỗi JavaScript bằng API JavaScript của bảng điều khiển và xem cá thông báo đầu ra để đăng nhập. Framework Webkit của Android hỗ trợ gần hết các API để hỗ trợ debug ở trong trình duyệt Android hoặc là WebView của bạn.

* Tham khảo một số cách sau:

    * [Remote Debugging on Android](https://developers.google.com/web/tools/chrome-devtools/remote-debugging/?utm_source=dcc&utm_medium=redirect&utm_campaign=2016q3)
    * [Debug your app](https://developer.android.com/studio/debug/index.html)

#### Using Console APIs in the Android Browser

* Khi bạn gọi hàm **console** (nằm trong đối tượng window.console của DOM), đầu ra xuất hiện trong logcat. Cách gọi đơn giản như **console.log("Hello World")** sẽ hiển thị log ra thông tin cần thiết. Có thể có 1 vài dạng log khác như:

    * console.log(String)
    * console.info(String)
    * console.warn(String)
    * console.error(String)
    
#### Using Console APIs in WebView

* Tất cả các API được hiển thị ở trên cũng được hỗ trợ khi debugging trên WebView. Nếu bạn đang nhắm mục tiêu Android 2.1 (API 7), bạn phải cung cấp **WebChromeClient** để thực hiện phương thức **onConsoleMessage()** để thông báo xuất hiện trong logcat.

```
val myWebView: WebView = findViewById(R.id.webview)
myWebView.webChromeClient = object : WebChromeClient() {

    override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
        Log.d("MyApplication", "$message -- From line $lineNumber of $sourceID")
    }
}
```

* Tuy nhiên, nếu phiên bản từ API 8 trở lên, thay vào đó bạn nên triển khai như sau:

```
val myWebView: WebView = findViewById(R.id.webview)
myWebView.webChromeClient = object : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        consoleMessage?.apply {
            Log.d("MyApplication", "${message()} -- From line ${lineNumber()} of ${sourceId()}")
        }
        return true
    }
}
```

#### Cache in WebView

* Trong những trường hợp mất mạng có thể thấy là trang web của chúng ta sẽ không thể tải lại được ngay cả khi đã được tải trước đó rồi. Chính vì vậy chúng ta phải sử dụng đến Cache để nếu trong trường hợp mất mạng còn có thể hiển thị nội dung chứ không phải là thông báo lỗi.

* Đầu tiên chúng ta cần bật chế độ cache lên ở trong **WebView.Setting** bằng phương thức ** setAppCacheEnabled(true)**```

* Sau đó cần xác định thư mục để chứa cache của trang web, ở đây sử dụng thư mục **CacheDir**.

```
val appCachePath = applicationContext.cacheDir.absolutePath
Log.d(TAG, "Cache path: $appCachePath")
mWebView.settings.setAppCachePath(appCachePath)
mWebView.settings.allowFileAccess = true
```

* Sử dụng phương thức **setCacheMode()** để quy định phương thức load của WebView.

```
// Network available
mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT

// No Network
mWebView.settings.cacheMode = WebSettings.LOAD_CACHE_ONLY
```

* Sau đó là load các trang web thông qua phương thức **loadUrl()**.
