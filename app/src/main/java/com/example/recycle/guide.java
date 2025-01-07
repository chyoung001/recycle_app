package com.example.recycle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class guide extends AppCompatActivity {

    private ImageView scanImage;
    private TextView recycleGuide;

    private Uri savedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        scanImage = findViewById(R.id.scanimage);
        recycleGuide = findViewById(R.id.recycleguide);

        // 인텐트 데이터 처리
        if (getIntent().hasExtra("imageBitmap")) {
            Bitmap bitmap = getIntent().getParcelableExtra("imageBitmap");
            if (bitmap != null) {
                scanImage.setImageBitmap(bitmap);
                savedImageUri = saveBitmapToFile(bitmap); // Bitmap을 파일로 저장
            }
        } else if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            savedImageUri = Uri.parse(imageUriString);
            scanImage.setImageURI(savedImageUri);
        }



        // 홈 버튼 클릭 이벤트
        ImageButton homeButton = findViewById(R.id.homebutton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(guide.this, MainActivity.class);

            // 파일 URI와 텍스트를 전달
            if (savedImageUri != null) {
                intent.putExtra("imageUri", savedImageUri.toString());
            }
            intent.putExtra("resultText", recycleGuide.getText().toString());

            startActivity(intent);
            finish();
        });

        // 동영상 보기 버튼 클릭 이벤트
        Button videoButton = findViewById(R.id.video);
        videoButton.setOnClickListener(v -> {
            Intent intent = new Intent(guide.this, playvideo.class);
            startActivity(intent);
        });
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        File cacheDir = getCacheDir(); // 앱의 캐시 디렉토리 사용
        File file = new File(cacheDir, "shared_image.png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
