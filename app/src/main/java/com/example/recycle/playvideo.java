package com.example.recycle;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class playvideo extends AppCompatActivity {

    private static final String VIDEO_URL = "https://www.youtube.com/embed/xrHWHCjYumQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);

        // WebView 설정
        WebView webView = findViewById(R.id.playvideo);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // 미디어 자동 재생 허용
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(VIDEO_URL);

        // 홈 버튼 클릭 이벤트
        ImageButton homeButton = findViewById(R.id.buttonhome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(playvideo.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
