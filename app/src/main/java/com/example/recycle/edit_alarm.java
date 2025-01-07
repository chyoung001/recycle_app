package com.example.recycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class edit_alarm extends AppCompatActivity {

    private Spinner daySpinner;
    private TimePicker timePicker;
    private Button saveAlarmButton;

    private String selectedDay = "일요일"; // 기본값
    private int selectedHour = 6, selectedMinute = 0; // 기본값

    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String KEY_DAY = "selectedDay";
    private static final String KEY_HOUR = "selectedHour";
    private static final String KEY_MINUTE = "selectedMinute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        // UI 초기화
        daySpinner = findViewById(R.id.daySpinner);
        timePicker = findViewById(R.id.timePicker);
        saveAlarmButton = findViewById(R.id.saveAlarmButton);

        // SharedPreferences에서 저장된 설정 불러오기
        loadSavedSettings();

        // 요일 Spinner 설정
        setupDaySpinner();

        // 저장 버튼 클릭 이벤트
        saveAlarmButton.setOnClickListener(v -> {
            // TimePicker에서 시간 가져오기
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();

            // 설정 저장
            saveSettings();

            // 설정된 값 반환
            Intent resultIntent = new Intent();
            resultIntent.putExtra("day", selectedDay);
            resultIntent.putExtra("time", String.format("%02d:%02d", selectedHour, selectedMinute));
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(edit_alarm.this, "알람이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        });
    }

    // 저장된 설정 불러오기
    private void loadSavedSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        selectedDay = prefs.getString(KEY_DAY, "일요일"); // 기본값: 일요일
        selectedHour = prefs.getInt(KEY_HOUR, 6);       // 기본값: 6시
        selectedMinute = prefs.getInt(KEY_MINUTE, 0);   // 기본값: 0분

        // TimePicker 초기화
        timePicker.setHour(selectedHour);
        timePicker.setMinute(selectedMinute);
    }

    // 설정 저장하기
    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_DAY, selectedDay);
        editor.putInt(KEY_HOUR, selectedHour);
        editor.putInt(KEY_MINUTE, selectedMinute);

        boolean success = editor.commit(); // 동기 저장 (성공 여부 반환)
        if (!success) {
            Toast.makeText(this, "설정 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 요일 Spinner 설정
    private void setupDaySpinner() {
        final String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        // 저장된 요일을 Spinner에 설정
        int savedDayPosition = java.util.Arrays.asList(days).indexOf(selectedDay);
        if (savedDayPosition >= 0) {
            daySpinner.setSelection(savedDayPosition);
        }

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedDay = days[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 기본값 유지
            }
        });
    }
}
