package com.example.my2048.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.my2048.R;
import com.example.my2048.config.Config;
import com.example.my2048.service.SoundService;
import com.example.my2048.view.GameView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 100;
    private TextView mTvGoal, mTvScore, mTvRecord;
    private Button mBtnRevert, mBtnRestart, mBtnOptions;
    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        mGameView = new GameView(this);
        RelativeLayout gamePanel = (RelativeLayout) findViewById(R.id.id_rl_game_panel);
        gamePanel.addView(mGameView);
    }

    private void initViews() {
        mTvGoal = (TextView) findViewById(R.id.id_tv_goal);
        mTvScore = (TextView) findViewById(R.id.id_tv_score);
        mTvRecord = (TextView) findViewById(R.id.id_tv_record);
        mBtnRevert = (Button) findViewById(R.id.id_btn_revert);
        mBtnRestart = (Button) findViewById(R.id.id_btn_restart);
        mBtnOptions = (Button) findViewById(R.id.id_btn_options);
        mTvGoal.setText("" + Config.gameGoal);
        mTvScore.setText("" + Config.gameScore);
        mTvRecord.setText("" + Config.gameRecord);
        mBtnRevert.setOnClickListener(this);
        mBtnRestart.setOnClickListener(this);
        mBtnOptions.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.id_btn_revert:
                mGameView.revert();
                break;
            case R.id.id_btn_restart:
                mGameView.startGame();
                break;
            case R.id.id_btn_options:
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case REQUEST_CODE:
                mGameView.startGame();
                break;
            default:
                break;
        }
    }

    public void setGoalText(int goal) {
        mTvGoal.setText("" + goal);
    }

    public void setScoreText(int score) {
        mTvScore.setText("" + score);
    }

    public void setRecordText(int record) {
        mTvRecord.setText("" + record);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, SoundService.class);
        stopService(intent);
        super.onDestroy();
    }
}
