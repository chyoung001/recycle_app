package com.example.recycle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class logo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // 3초 후 MainActivity로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(logo.this, MainActivity.class);
                startActivity(intent);
                finish(); // 스플래시 화면을 종료하여 뒤로가기 방지
            }
        }, 3000); // 3000ms = 3초
    }
}