package com.simple.rat;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FIREBASE_SERVICE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received: " + remoteMessage.getData());
        // بیدار کردن سرویس
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "New token: " + token);
        // ذخیره توکن برای ارسال FCM
    }
}
