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

## Managing WebView

