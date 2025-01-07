package com.example.recycle;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive: 알람 수신");

        // 알림 채널 생성
        createNotificationChannel(context);

        // 알림 빌더 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm) // 알림 아이콘 (res/drawable/ic_alarm 추가 필요)
                .setContentTitle("알람 알림")
                .setContentText("설정된 알람 시간이 되었습니다!")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위 높음
                .setAutoCancel(true); // 클릭 시 알림 자동 삭제

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 권한 확인 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e("AlarmReceiver", "알림 권한이 없습니다.");
                return;
            }
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d("AlarmReceiver", "알림이 표시되었습니다.");
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "알람 채널";
            String description = "알람 알림을 위한 채널";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("AlarmReceiver", "알림 채널이 생성되었습니다.");
            }
        }
    }
}
