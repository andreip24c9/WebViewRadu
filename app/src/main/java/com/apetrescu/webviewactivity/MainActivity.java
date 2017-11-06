package com.apetrescu.webviewactivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoadingView.OnRetryClickListener, View.OnClickListener {

    private WebView mWebView;
    private LoadingView mLoadingView;

    private View mWebContainer;
    private String mUrlToLoad = "https://www.emag.ro/";

    // setup a timeout
    private Runnable mTimeoutRunnable = new Runnable() {
        public void run() {
            // do what you want
            showError("Timeout Error");
        }
    };
    private Runnable mLoadSuccessRunnable = new Runnable() {
        public void run() {
            mWebContainer.setVisibility(View.VISIBLE);
            Log.d("Ninja", "cookie: " + getCookie(mUrlToLoad, "site_ver"));
        }
    };
    private Handler mHandler;
    private Button mNextPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mWebView = (WebView) findViewById(R.id.web_view);
        mLoadingView = (LoadingView) findViewById(R.id.web_loading_layout);
        mWebContainer = findViewById(R.id.web_container);
        mNextPageBtn = (Button) findViewById(R.id.next_page_btn);
        mNextPageBtn.setOnClickListener(this);


        mLoadingView.setOnRetryClickListener(this);

        setupWebView();
        loadUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mHandler = new Handler(Looper.getMainLooper());

        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mHandler.postDelayed(mTimeoutRunnable, 5000); //  timeout after 5s
                mLoadingView.isLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // page load finished either
                mHandler.removeCallbacks(mTimeoutRunnable); // remove timeout dispatch
                mHandler.postDelayed(mLoadSuccessRunnable, 200);
                mLoadingView.isLoading(false);
            }


//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                Log.d("Ninja", "onReceivedError: ");
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.getSettings();
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }

    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(CookieName)){
                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    private void showError(String error) {
        mWebView.stopLoading();
        mLoadingView.isLoading(false);
        mLoadingView.showError(error);
    }

    private void loadUrl() {
        mHandler.removeCallbacks(mTimeoutRunnable);
        mHandler.removeCallbacks(mLoadSuccessRunnable);
        mWebContainer.setVisibility(View.GONE);

        String lang = Locale.getDefault().getLanguage();
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept-Language", lang);
        headerMap.put("Authorization", "Ana are mere mari");

        mWebView.loadUrl(mUrlToLoad, headerMap);
    }

    @Override
    public void onRetry() {
        mLoadingView.hideError();
        loadUrl();
    }

    @Override
    public void onClick(View view) {
        mUrlToLoad = "https://facebook.com";
        loadUrl();
    }
}
