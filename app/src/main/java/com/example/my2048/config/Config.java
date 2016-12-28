package com.example.my2048.config;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/12/27.
 */
public class Config extends Application {
    // 最终数字
    public static int gameGoal;
    // 当前得分
    public static int gameScore;
    // 最高分
    public static int gameRecord;
    // 行列数
    public static int gameLines;
    // 音效
    public static boolean soundAction;
    // 背景音乐
    public static boolean soundBackground;
    public static SharedPreferences sp;
    public static final String SP_NAME = "sp";
    public static final String KEY_GAME_GOAL = "goal";
    public static final String KEY_GAME_RECORD = "record";
    public static final String KEY_GAME_LINES = "lines";
    public static final String KEY_SOUND_ACTION = "sound_action";
    public static final String KEY_SOUND_BACKGROUND = "sound_background";

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        gameGoal = sp.getInt(KEY_GAME_GOAL, 2048);
        gameScore = 0;
        gameRecord = sp.getInt(KEY_GAME_RECORD, 0);
        gameLines = sp.getInt(KEY_GAME_LINES, 4);
        soundAction = sp.getBoolean(KEY_SOUND_ACTION, false);
        soundBackground = sp.getBoolean(KEY_SOUND_BACKGROUND, false);
    }
}