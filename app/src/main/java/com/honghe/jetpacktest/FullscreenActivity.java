package com.honghe.jetpacktest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FullscreenActivity extends AppCompatActivity {
    EditText et_url;
    WebView webView;
    TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        et_url = findViewById(R.id.et_url);
        webView = findViewById(R.id.webView);
        tv_content = findViewById(R.id.tv_content);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "yunfan");
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                pauseSpeaking(getApplicationContext());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 获取页面内容
                view.loadUrl("javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);");
                // 获取解析<meta name="share-description" content="获取到的值">
                view.loadUrl("javascript:window.java_obj.showDescription("
                        + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
                        + ");");
                super.onPageFinished(view, url);
            }
        });
//        webView.loadUrl(et_url.getText().toString());
//        webView.loadUrl("file:///android_asset/test-1/test.html");
        webView.loadUrl("http://question.yunjiai.cn/question/result?id=5cd0ee1645ce525d5bdfa75d");
//        webView.loadUrl("http://yunfan.yunjikj.cn");
        et_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId ==
                        EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() ==
                        KeyEvent.KEYCODE_ENTER)) {
                    webView.loadUrl(et_url.getText().toString());
                }
                return true;
            }
        });
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
//            Log.e("wanghh", "====>html=" + html);
            Document doc = Jsoup.parse(html);
//            Element element = doc.getElementById("divTitle1");
//            tv_content.setText(doc.body().text());
//            String content = element.text();
            tv_content.setText(doc.wholeText());
//            AmapTTSController.getInstance(getApplicationContext()).playText(content);
        }

        @JavascriptInterface
        public void showDescription(String str) {
//            Log.e("wanghh", "====>html=" + str);
        }

        @JavascriptInterface
        public void JsToJavaInterface(final String param) {
            Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show();
        }
        @JavascriptInterface
        public void TextCall(final String param){
            Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show();
        }

    }

}
