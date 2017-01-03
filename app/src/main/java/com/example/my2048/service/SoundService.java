package com.example.my2048.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.my2048.R;

public class SoundService extends Service {
    private MediaPlayer mPlayer;
    public static final String EXTRA_OPERATE = "operate";
    public static final int PLAY = 0;
    public static final int PAUSE = 1;

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
        int operate = intent.getIntExtra(EXTRA_OPERATE, PLAY);
        switch (operate) {
            case PLAY:
                if (!mPlayer.isPlaying()) {
                    mPlayer.setLooping(true);
                    mPlayer.start();
                }
                break;
            case PAUSE:
                if(mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        super.onDestroy();
    }
}
