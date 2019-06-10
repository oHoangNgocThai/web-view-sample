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

### Version API

* Thêm thư viện [Webkit](https://developer.android.com/reference/androidx/webkit/package-summary) của AndroidX để sử dụng các phương thức control WebView. 

```
implementation 'androidx.webkit:webkit:1.0.0'
```

* Bắt đầu từ Android 7.0 (API level 24), người dùng có thể chọn số package khác nhau có thể hiển thị nội dung trên WebView. Thư viện AndroidX webkit bao gồm phương thức **getClientWebViewPackage()** để tìm nạp thông tin liên quan đến gói đang hiển thị nội dung web trong ứng dụng.

```
val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(appContext)
Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo.versionName}")
```

### Google Safe Browsing Service

* Để cung cấp cho người dùng của bạn trải nghiệm an toàn hơn, các đối tượng WebView xác minh URL bằng Google Safe Browsing, cho phép hiển thị cảnh báo đối với người dùng khi họ cố mở 1 trang web có khả năng không an toàn.

* Mặc dù giá trị mặc định của **EnableSafeBrowsing** là true, đôi khi bạn chỉ muốn bật duyệt web an toàn theo điều kiện hoặc vô hiệu hóa nó. Android 8.0 (API 26) hỗ trợ cao hơn bằng cách sử dụng phương thức **setSafeBrowsingEnables()** để chuyển đổi duyệt web an toàn cho 1 đối tượng WebView riêng lẻ.

* Nếu bạn muốn tất cả các đối tượng WebView từ chối kiểm tra duyệt web an toàn, bạn có thể thêm phần tử **<meta-data>** sau vào AndroidManifest.xml:

```
<meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
                   android:value="false" />
```

### Defining programmatic actions

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

### HTML5 Geolocation API

