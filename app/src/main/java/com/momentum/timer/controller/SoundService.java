package com.momentum.timer.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.momentum.timer.R;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;


public class SoundService extends Service {
//    private final IBinder binder = new LocalBinder();
    MediaPlayer player;
//    public class LocalBinder extends Binder {
//        public SoundService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return SoundService.this;
//        }
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.momentum.timer";
        String channelName = "Background Warning Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_wall_clock)
                .setContentTitle("Momentum Timer is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

    }


//    public void playAudio(String uriS) {
//        if (uriS==null) {
//            player = MediaPlayer.create(this, R.raw.audio);
//        }
//        else {
//            Uri uri = Uri.parse(uriS);
//            player = MediaPlayer.create(this, uri);
//        }
//        player.start();
//    }
//    public void stopAudio() {
//        if (player!=null)
//            player.stop();
//    }
//    public Boolean isPlaying() {
//        if (player!=null) {
//            return player.isPlaying();
//        }
//        return false;
//    }
    private boolean audio=false;
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tone = intent.getExtras().getString("uri");
        long timer = intent.getExtras().getLong("timer");
        if (tone.trim().isEmpty()) {
            player = MediaPlayer.create(this, R.raw.audio); //select music file
        } else {
            Uri uri = Uri.parse(tone);
            player = MediaPlayer.create(this, uri);
        }
        new CountDownTimer(timer*1000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long sec=millisUntilFinished/1000;
                Log.d("startService","time " +sec);
                if (sec<=3 && !audio) {
                    try {
                        player.start();
                        audio = true;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        stopSelf();
                        cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                Log.d("startService","finish");

                    try {
                        if (player.isPlaying()) {
                            player.stop();

                            audio = false;
                        }
                    }catch (IllegalStateException e) {
                        e.printStackTrace();
                        stopSelf();
                        cancel();
                    }
            }
        }.start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        stopSelf();
        super.onDestroy();
    }
}

