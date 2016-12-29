package com.example.my2048.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.my2048.R;
import com.example.my2048.config.Config;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnLines, mBtnGoal, mBtnBack, mBtnDone;
    private AlertDialog.Builder mBuilder;
    private String[] mLinesArray, mGoalArray;
    private Switch mSoundAction, mSoundBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initViews();
        mBtnLines.setOnClickListener(this);
        mBtnGoal.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
        Config.gameLines = Config.sp.getInt(Config.KEY_GAME_LINES, 4);
        Config.gameGoal = Config.sp.getInt(Config.KEY_GAME_GOAL, 2048);
        Config.soundAction = Config.sp.getBoolean(Config.KEY_SOUND_ACTION, false);
        Config.soundBackground = Config.sp.getBoolean(Config.KEY_SOUND_BACKGROUND, false);
        mBtnLines.setText("" + Config.gameLines);
        mBtnGoal.setText("" + Config.gameGoal);
        mSoundAction.setChecked(Config.soundAction);
        mSoundBackground.setChecked(Config.soundBackground);
    }

    private void initViews() {
        mBtnLines = (Button) findViewById(R.id.id_btn_game_lines);
        mBtnGoal = (Button) findViewById(R.id.id_btn_game_goal);
        mBtnBack = (Button) findViewById(R.id.id_btn_back);
        mBtnDone = (Button) findViewById(R.id.id_btn_done);
        mBuilder = new AlertDialog.Builder(this);
        mLinesArray = new String[] {"4", "5", "6"};
        mGoalArray = new String[] {"1024", "2048", "4096"};
        mSoundAction = (Switch) findViewById(R.id.id_switch_sound_action);
        mSoundBackground = (Switch) findViewById(R.id.id_switch_sound_background);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.id_btn_game_lines:
                mBuilder.setTitle("选择游戏行列数")
                        .setItems(mLinesArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnLines.setText(mLinesArray[which]);
                            }
                        }).create().show();
                break;
            case R.id.id_btn_game_goal:
                mBuilder.setTitle("选择游戏最终数字")
                        .setItems(mGoalArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnGoal.setText(mGoalArray[which]);
                            }
                        }).create().show();
                break;
            case R.id.id_btn_back:
                finish();
                break;
            case R.id.id_btn_done:
                saveConfig();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    /**
     * 保存设置
     */
    private void saveConfig() {
        SharedPreferences.Editor editor = Config.sp.edit();
        editor.putInt(Config.KEY_GAME_LINES, Integer.parseInt(mBtnLines.getText().toString()));
        editor.putInt(Config.KEY_GAME_GOAL, Integer.parseInt(mBtnGoal.getText().toString()));
        editor.putBoolean(Config.KEY_SOUND_ACTION, mSoundAction.isChecked());
        editor.putBoolean(Config.KEY_SOUND_BACKGROUND, mSoundBackground.isChecked());
        editor.commit();
    }
}
