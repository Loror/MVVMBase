package com.loror.mvvm.utls;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.loror.lororUtil.text.TextUtil;

import java.io.File;
import java.net.URISyntaxException;

public class WebViewUtil {

    public interface WebViewUtilClient {
        Object javascriptHandler();

        void chosePhoto();

        boolean shouldOverrideUrlLoadingByApp(final WebView view, String url);

        void onProgressChanged(WebView view, int newProgress);

        void onPageFinished(WebView view, String url);
    }

    private final Context context;
    private final WebView webView;
    private WebViewUtilClient webViewUtilClient;

    //5.0以下使用
    private ValueCallback<Uri> uploadMessage;
    // 5.0及以上使用
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private String currentUrl;

    public WebViewUtil(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    public void setWebViewUtilClient(WebViewUtilClient webViewUtilClient) {
        this.webViewUtilClient = webViewUtilClient;
    }

    public String getCurrentUrl() {
        return currentUrl == null ? "" : currentUrl;
    }

    /**
     * WebView设置
     */
    @SuppressLint("JavascriptInterface")
    public void webSettings() {
        WebSettings webSettings = webView.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON); //设置webview支持插件
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setBlockNetworkImage(false);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 添加js交互接口类，并起别名 androidInterface
        if (webViewUtilClient != null && webViewUtilClient.javascriptHandler() != null) {
            webView.addJavascriptInterface(webViewUtilClient.javascriptHandler(), "androidInterface");
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, final String url) {
                currentUrl = url;
                if (webViewUtilClient != null) {
                    webViewUtilClient.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlLoadingByApp(view, url);
            }

            /**
             * 根据url的scheme处理跳转第三方app的业务
             */
            private boolean shouldOverrideUrlLoadingByApp(final WebView view, String url) {
                if (webViewUtilClient != null) {
                    if (webViewUtilClient.shouldOverrideUrlLoadingByApp(view, url)) {
                        return true;
                    }
                }
                if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
                    // 不处理http, https, ftp的请求
                    view.loadUrl(url);
                    return false;
                }
                final Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    Log.e("webview", "URISyntaxException: " + e.getLocalizedMessage());
                    return false;
                }
                intent.setComponent(null);
                if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                    // 说明系统中存在这个activity
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "未找到要打开的软件", Toast.LENGTH_SHORT).show();
                        Log.e("webview", "ActivityNotFoundException: " + e.getLocalizedMessage());
                    }
                    return true;
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (webViewUtilClient != null) {
                    webViewUtilClient.onProgressChanged(view, newProgress);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                chosePhoto();
                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                uploadMessage = uploadMsg;
                chosePhoto();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                uploadMessage = uploadMsg;
                chosePhoto();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                uploadMessage = uploadMsg;
                chosePhoto();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                if (!TextUtil.isEmpty(message)) {
                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage(message)
                            .setPositiveButton("确定", (dialog, which) -> {
                                result.confirm();
                                dialog.cancel();
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                                result.cancel();
                                dialog.cancel();
                            })
                            .create()
                            .show();
                    return true;
                } else {
                    return super.onJsAlert(view, url, message, result);
                }
            }
        });
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            //使用自带浏览器下载
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        });
    }

    /**
     * 选择图片
     */
    private void chosePhoto() {
        if (webViewUtilClient != null) {
            webViewUtilClient.chosePhoto();
        }
    }

    /**
     * 选择图片返回
     */
    public void choseResult(String path) {
        photoResult(new File(path));
    }

    /**
     * 选择图片返回
     */
    private void photoResult(File file) {
        Uri result = file != null ? Uri.fromFile(file) : null;
        if (uploadMessageAboveL != null) {
            uploadMessageAboveL.onReceiveValue(result != null ? new Uri[]{result} : null);
            uploadMessageAboveL = null;
        } else if (uploadMessage != null) {
            uploadMessage.onReceiveValue(result);
            uploadMessage = null;
        }
    }

}
