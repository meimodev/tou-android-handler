package com.meimodev.sitouhandler;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.textView_title)
    TextView tvTitle;

    public static final String KEY_INTENT_TITLE = "title";
    public static final String KEY_INTENT_DESTINATION_URL = "destination_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        String title = getIntent().getExtras() != null ?
                getIntent().getStringExtra("title") : "No Title";

        String destinationUrl = getIntent().getExtras() != null ?
                getIntent().getStringExtra("destination_url") : "google.com";

        tvTitle.setText(title);

        String urlPrefix = destinationUrl.contains("http") ? "": "https://";
        String url = urlPrefix + destinationUrl;
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(WebViewActivity.this, "Page Finish", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onPageFinished: ----------------------------");
            }
        });

    }
}
