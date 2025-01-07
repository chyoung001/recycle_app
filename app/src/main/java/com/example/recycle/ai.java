package com.example.recycle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ai extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView imageView;
    private TextView aiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        imageView = findViewById(R.id.imageView);
        aiText = findViewById(R.id.aitext);

        // 데이터 초기화
        bitmap = null;

        // Intent 데이터 처리
        if (getIntent().hasExtra("imageBitmap")) {
            bitmap = getIntent().getParcelableExtra("imageBitmap");
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);

            } else {
                aiText.setText("카메라 데이터를 로드할 수 없습니다.");
            }
        } else if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    aiText.setText("갤러리 데이터를 로드할 수 없습니다.");
                }
            } else {
                aiText.setText("유효한 이미지 URI가 없습니다.");
            }
        } else {
            aiText.setText("데이터가 없어 이동하지 못했습니다.");
        }

        // 10초 후 GuideActivity로 이동
        new Handler().postDelayed(() -> {
            if (bitmap != null) {
                try {
                    // Bitmap 데이터를 파일로 저장
                    File file = saveBitmapToFile(bitmap);

                    // 파일 URI와 텍스트 전달
                    Intent intent = new Intent(ai.this, guide.class);
                    intent.putExtra("imageUri", Uri.fromFile(file).toString());
                    intent.putExtra("resultText", aiText.getText().toString());
                    startActivity(intent);
                    finish(); // AI 액티비티 종료
                } catch (IOException e) {
                    e.printStackTrace();
                    aiText.setText("이미지를 저장하지 못했습니다.");
                }
            } else {
                aiText.setText("이미지 데이터가 없어 GuideActivity로 이동하지 못했습니다.");
            }
        }, 10000); // 10초 대기
    }

    // Bitmap을 파일로 저장하는 메서드
    private File saveBitmapToFile(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "shared_image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        }
        return file;
    }
}
