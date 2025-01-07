package com.example.recycle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    // 액티비티 실행 결과를 처리하기 위한 런처들
    private ActivityResultLauncher<Intent> cameraLauncher; // 카메라 실행 결과
    private ActivityResultLauncher<Intent> galleryLauncher; // 갤러리 실행 결과
    private ActivityResultLauncher<String> requestPermissionLauncher; // 권한 요청

    private ImageView recentImageView; // 최근 선택한 이미지 표시
    private TextView resultTextView; // 결과 텍스트 표시

    // SharedPreferences 관련 상수
    private static final String PREF_NAME = "RecycleAppPrefs"; // SharedPreferences 이름
    private static final String IMAGE_URI_KEY = "imageUri"; // 이미지 URI 저장 키
    private static final String RESULT_TEXT_KEY = "resultText"; // 결과 텍스트 저장 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 초기화
        recentImageView = findViewById(R.id.recentimage); // 이미지 뷰
        resultTextView = findViewById(R.id.result); // 결과 텍스트

        // 앱 재시작 시 저장된 데이터 로드
        loadSavedData();

        // 가이드 화면에서 전달받은 데이터 처리
        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            saveToSharedPreferences(IMAGE_URI_KEY, imageUriString); // 이미지 URI 저장
            loadImage(imageUriString); // 이미지 로드
        }

        if (getIntent().hasExtra("resultText")) {
            String receivedText = getIntent().getStringExtra("resultText");
            saveToSharedPreferences(RESULT_TEXT_KEY, receivedText); // 결과 텍스트 저장
            resultTextView.setText(receivedText);
        }

        // 카메라 실행 결과 처리
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 카메라 데이터 가져오기
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        if (photo != null) {
                            try {
                                // Bitmap을 파일로 저장
                                File photoFile = saveBitmapToFile(photo);
                                // AI 액티비티로 이동
                                Intent intent = new Intent(MainActivity.this, ai.class);
                                intent.putExtra("imageUri", Uri.fromFile(photoFile).toString());
                                startActivity(intent);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "이미지를 저장하지 못했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "카메라 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // 갤러리 실행 결과 처리
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // AI 화면으로 이동
                            Intent intent = new Intent(MainActivity.this, ai.class);
                            intent.putExtra("imageUri", selectedImageUri.toString()); // URI 전달
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "이미지를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        // 알림 권한 요청 처리
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("MainActivity", "알림 권한이 부여되었습니다.");
                    } else {
                        Log.d("MainActivity", "알림 권한이 거부되었습니다.");
                        Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 알림 권한 요청
        requestNotificationPermission();

        // 설정 버튼 클릭 이벤트
        findViewById(R.id.iconSettings).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, settings.class);
            startActivity(intent);
        });

        // 카메라 버튼 클릭 이벤트
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 갤러리 버튼 클릭 이벤트
        ImageButton galleryButton = findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });
    }

    // AI 액티비티로 이동
    private void navigateToAiScreen(Object image, String resultText) {
        Intent intent = new Intent(MainActivity.this, ai.class);
        if (image instanceof Bitmap) {
            // Bitmap 데이터를 Intent에 추가
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((Bitmap) image).compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("image", byteArray);
        } else if (image instanceof String) {
            // URI 데이터를 Intent에 추가
            intent.putExtra("imageUri", (String) image);
        }
        intent.putExtra("text", resultText);
        startActivity(intent);
    }

    // 알림 권한 요청
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // Bitmap 데이터를 파일로 저장
    private File saveBitmapToFile(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "camera_image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        }
        return file;
    }

    // SharedPreferences에 데이터 저장
    private void saveToSharedPreferences(String key, String value) {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }

    // SharedPreferences에서 데이터 로드
    private void loadSavedData() {
        String savedImageUri = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(IMAGE_URI_KEY, null);
        String savedResultText = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(RESULT_TEXT_KEY, "기본 텍스트");

        if (savedImageUri != null) {
            loadImage(savedImageUri);
        }
        resultTextView.setText(savedResultText);
    }

    // URI로부터 이미지 로드
    private void loadImage(String imageUriString) {
        try {
            Uri imageUri = Uri.parse(imageUriString);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            recentImageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
