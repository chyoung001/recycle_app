package com.example.recycle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class settings extends AppCompatActivity {

    private static final int EDIT_ALARM_REQUEST = 1;
    private TextView alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 알람 텍스트뷰 초기화
        alarm = findViewById(R.id.alarm);

        // 뒤로 가기 버튼 클릭 이벤트
        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        // 편집 버튼 클릭 이벤트
        ImageButton editButton = findViewById(R.id.edit);
        editButton.setOnClickListener(v -> {
            // 알람 편집 화면으로 이동
            Intent intent = new Intent(settings.this, edit_alarm.class);
            startActivityForResult(intent, EDIT_ALARM_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ALARM_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // 반환된 데이터 가져오기
                String day = data.getStringExtra("day");
                String time = data.getStringExtra("time");

                if (day != null && time != null) {
                    // 알람 텍스트 업데이트
                    alarm.setText(day + " " + time);
                } else {
                    // null 값 처리
                    Log.e("SettingsActivity", "onActivityResult: 받은 데이터가 null입니다. day=" + day + ", time=" + time);
                }
            } else {
                Log.e("SettingsActivity", "onActivityResult 실패. resultCode=" + resultCode);
            }
        }
    }
}
