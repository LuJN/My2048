package com.example.my2048.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.my2048.R;

public class SoundService extends Service {
    private MediaPlayer mPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MediaPlayer.create(this, R.raw.sound_background);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPlayer.setLooping(true);
        mPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mPlayer.release();
        super.onDestroy();
    }
}
