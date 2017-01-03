package com.example.my2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.example.my2048.R;
import com.example.my2048.activity.MainActivity;
import com.example.my2048.bean.GameItem;
import com.example.my2048.config.Config;
import com.example.my2048.util.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class GameView extends GridLayout implements View.OnTouchListener {
    // 游戏行列数
    private int mGameLines;
    // 方块大小
    private int mItemSize;
    // 方块矩阵
    private GameItem[][] mGameMatrix;
    // 空白方块List
    private List<Point> mBlankList;
    // 记录坐标
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    // 滑动事件相关变量
    private int mCurrentNum;// 当前数字
    private int mKeyNum = -1;// 比较基准数字
    private List<Integer> mCalList;// 一行（列）的非0数字List
    // 游戏当前状态
    private static final int GAME_OVER = 0;
    private static final int GAME_NORMAL = 1;
    private static final int GAME_SUCCESS = 2;
    // 历史数字矩阵
    private int[][] mGameMatrixHistory;
    // 历史当前得分
    private int mScoreHistory;
    // 音效
    private SoundPool mSoundPool;
    private Map<String, Integer> mSoundMap;
    private static final String SOUND_ACTION = "soundAction";

    public GameView(Context context) {
        super(context);
        initSoundAction(SOUND_ACTION, R.raw.sound_action);
        initGameView();
    }

    /**
     * 初始化GameView
     */
    public void initGameView() {
        removeAllViews();
        mGameLines = Config.gameLines;
        DisplayMetrics outMetrics =  DisplayUtil.getScreenSize(getContext());
        mItemSize = outMetrics.widthPixels /  mGameLines;
        setColumnCount( mGameLines);
        setRowCount( mGameLines);
        initGameMatrix();
        setOnTouchListener(this);
    }

    /**
     * 初始化音效
     */
    private void initSoundAction(String soundName, int resId) {
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        int soundId = mSoundPool.load(getContext(), resId, 1);
        mSoundMap = new HashMap<>();
        mSoundMap.put(soundName, soundId);
    }

    /**
     * 播放音效
     */
    private void playSoundAction(String soundName) {
        AudioManager manager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int soundId = mSoundMap.get(soundName);
        float currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = currentVolume / maxVolume;
        float rightVolume = leftVolume;
        mSoundPool.play(soundId, leftVolume, rightVolume, 1, 0, 1);
    }

    /**
     * 初始化矩阵
     */
    private void initGameMatrix() {
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        GameItem item = null;
        for (int i = 0; i <  mGameLines; i++) {
            for (int j = 0; j <  mGameLines; j++) {
                item = new GameItem(getContext(), 0);
                mGameMatrix[i][j] = item;
                addView(item, mItemSize, mItemSize);
            }
        }
        mBlankList = new ArrayList<>();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mBlankList.add(new Point(i, j));
            }
        }
        addRandomNum();
        addRandomNum();
        mCalList = new ArrayList<>();
        mGameMatrixHistory = new int[mGameLines][mGameLines];
    }

    /**
     * 添加随机数字（2或4）
     */
    private void addRandomNum() {
        getBlankList();
        if(mBlankList.size() > 0 ) {
            int indexRandom = (int) (Math.random() * mBlankList.size());
            Point pointRandom = mBlankList.get(indexRandom);
            int numRandom = Math.random() > 0.2 ? 2 : 4;
            mGameMatrix[pointRandom.x][pointRandom.y].setNum(numRandom);
            // 动画
            animStart(mGameMatrix[pointRandom.x][pointRandom.y]);
            // 音效
            if(Config.soundAction) {
                playSoundAction(SOUND_ACTION);
            }
        }
    }

    /**
     * 播放动画
     */
    private void animStart(GameItem gameItem) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        gameItem.getItemView().startAnimation(sa);
    }

    /**
     * 得到空白方块List
     */
    private void getBlankList() {
        mBlankList.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if(mGameMatrix[i][j].getNum() == 0) {
                    mBlankList.add(new Point(i, j));
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                saveGameMatrixHistory();
                mStartX = x;
                mStartY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = x;
                mEndY = y;
                judgeDirectionAndMove(mEndX - mStartX, mEndY - mStartY);
                if(isMove()) {
                    addRandomNum();
                    ((MainActivity)getContext()).setScoreText(Config.gameScore);
                    if(Config.gameScore > Config.gameRecord) {
                        SharedPreferences.Editor editor = Config.sp.edit();
                        editor.putInt(Config.KEY_GAME_RECORD, Config.gameScore);
                        editor.commit();
                        Config.gameRecord = Config.gameScore;
                        ((MainActivity)getContext()).setRecordText(Config.gameRecord);
                    }
                    showGameResult();
                }
                break;
        }
        return true;
    }

    /**
     * 展示游戏结果
     */
    private void showGameResult() {
        int gameState = checkGameState();
        if(gameState == GAME_OVER) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("游戏失败")
                    .setPositiveButton("再来一次", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGame();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if(gameState == GAME_SUCCESS) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("游戏成功")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = Config.sp.edit();
                            if(Config.gameGoal == 1024) {
                                editor.putInt(Config.KEY_GAME_GOAL, 2048);
                            } else if(Config.gameGoal == 2048) {
                                editor.putInt(Config.KEY_GAME_GOAL, 4096);
                            } else {
                                editor.putInt(Config.KEY_GAME_GOAL, 4096);
                            }
                            editor.commit();
                            startGame();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    /**
     * 检查游戏当前状态
     */
    private int checkGameState() {
        // 检查是否有最终数字
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if(mGameMatrix[i][j].getNum() == Config.gameGoal) {
                    return GAME_SUCCESS;
                }
            }
        }
        // 没有最终数字
        getBlankList();
        if(mBlankList.size() != 0) {
            return GAME_NORMAL;
        }
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if(i < mGameLines - 1) {
                    if(mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                        return GAME_NORMAL;
                    }
                }
                if(j < mGameLines - 1) {
                    if(mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                        return GAME_NORMAL;
                    }
                }
            }
        }
        return GAME_OVER;
    }

    /**
     * 保存历史游戏矩阵
     */
    private void saveGameMatrixHistory() {
        mScoreHistory = Config.gameScore;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 判断是否移动了
     */
    private boolean isMove() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if(mGameMatrix[i][j].getNum() != mGameMatrixHistory[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断滑动方向并且移动
     */
    private void judgeDirectionAndMove(int offsetX, int offsetY) {
        int slideOffset = DisplayUtil.dp2px(getContext(), 5);
        int maxOffset = DisplayUtil.dp2px(getContext(), 200);
        boolean flagNormal = (Math.abs(offsetX) >= slideOffset && Math.abs(offsetX) < maxOffset) ||
                (Math.abs(offsetY) >= slideOffset && Math.abs(offsetY) < maxOffset);
        boolean flagSuper = (Math.abs(offsetX) >= maxOffset) || (Math.abs(offsetY) >= maxOffset);
        if(flagNormal && !flagSuper) {
            if(Math.abs(offsetX) >= Math.abs(offsetY)) {
                if(offsetX >= slideOffset) {
                    swipeRight();
                } else {
                    swipeLeft();
                }
            } else {
                if(offsetY >= slideOffset) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if(flagSuper) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText et = new EditText(getContext());
            builder.setTitle("后门").setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!TextUtils.isEmpty(et.getText().toString())) {
                                int num = Integer.parseInt(et.getText().toString());
                                if(checkSuperNum(num)) {
                                    addSuperNum(num);
                                }
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    /**
     * 滑动事件：上
     */
    private void swipeUp() {
        for (int column = 0; column < mGameLines; column++) {
            for (int row = 0; row < mGameLines; row++) {
                mCurrentNum = mGameMatrix[row][column].getNum();
                if(mCurrentNum != 0) {
                    if(mKeyNum == -1) {
                        mKeyNum = mCurrentNum;
                    } else {
                        if(mCurrentNum == mKeyNum) {
                            mCalList.add(mKeyNum * 2);
                            Config.gameScore += mKeyNum * 2;
                            mKeyNum = -1;
                        } else {
                            mCalList.add(mKeyNum);
                            mKeyNum = mCurrentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if(mKeyNum != -1) {
                mCalList.add(mKeyNum);
            }
            for (int row = 0; row < mCalList.size(); row++) {
                mGameMatrix[row][column].setNum(mCalList.get(row));
            }
            for (int row = mCalList.size(); row < mGameLines; row++) {
                mGameMatrix[row][column].setNum(0);
            }
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：下
     */
    private void swipeDown() {
        for (int column = 0; column < mGameLines; column++) {
            for (int row = mGameLines - 1; row >= 0; row--) {
                mCurrentNum = mGameMatrix[row][column].getNum();
                if(mCurrentNum != 0) {
                    if(mKeyNum == -1) {
                        mKeyNum = mCurrentNum;
                    } else {
                        if(mCurrentNum == mKeyNum) {
                            mCalList.add(mKeyNum * 2);
                            Config.gameScore += mKeyNum * 2;
                            mKeyNum = -1;
                        } else {
                            mCalList.add(mKeyNum);
                            mKeyNum = mCurrentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if(mKeyNum != -1) {
                mCalList.add(mKeyNum);
            }
            for (int row = 0; row < mGameLines - mCalList.size(); row++) {
                mGameMatrix[row][column].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int row = mGameLines - mCalList.size(); row < mGameLines; row++) {
                mGameMatrix[row][column].setNum(mCalList.get(index));
                index--;
            }
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：左
     */
    private void swipeLeft() {
        for (int row = 0; row < mGameLines; row++) {
            for (int column = 0; column < mGameLines; column++) {
                mCurrentNum = mGameMatrix[row][column].getNum();
                if(mCurrentNum != 0) {
                    if(mKeyNum == -1) {
                        mKeyNum = mCurrentNum;
                    } else {
                        if(mCurrentNum == mKeyNum) {
                            mCalList.add(mKeyNum * 2);
                            Config.gameScore += mKeyNum * 2;
                            mKeyNum = -1;
                        } else {
                            mCalList.add(mKeyNum);
                            mKeyNum = mCurrentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if(mKeyNum != -1) {
                mCalList.add(mKeyNum);
            }
            for (int column = 0; column < mCalList.size(); column++) {
                mGameMatrix[row][column].setNum(mCalList.get(column));
            }
            for (int column = mCalList.size(); column < mGameLines; column++) {
                mGameMatrix[row][column].setNum(0);
            }
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：右
     */
    private void swipeRight() {
        for (int row = 0; row < mGameLines; row++) {
            for (int column = mGameLines - 1; column >= 0; column--) {
                mCurrentNum = mGameMatrix[row][column].getNum();
                if(mCurrentNum != 0) {
                    if(mKeyNum == -1) {
                        mKeyNum = mCurrentNum;
                    } else {
                        if(mCurrentNum == mKeyNum) {
                            mCalList.add(mKeyNum * 2);
                            Config.gameScore += mKeyNum * 2;
                            mKeyNum = -1;
                        } else {
                            mCalList.add(mKeyNum);
                            mKeyNum = mCurrentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if(mKeyNum != -1) {
                mCalList.add(mKeyNum);
            }
            for (int column = 0; column < mGameLines - mCalList.size(); column++) {
                mGameMatrix[row][column].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int column = mGameLines - mCalList.size(); column < mGameLines; column++) {
                mGameMatrix[row][column].setNum(mCalList.get(index));
                index--;
            }
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 添加后门数字
     */
    private void addSuperNum(int num) {
        getBlankList();
        if(mBlankList.size() > 0) {
            int indexRandom = (int) (Math.random() * mBlankList.size());
            Point pointRandom = mBlankList.get(indexRandom);
            mGameMatrix[pointRandom.x][pointRandom.y].setNum(num);
        }
    }

    /**
     * 检查后门输入的数字是否合理
     */
    private boolean  checkSuperNum(int num) {
        boolean flag = (num == 2 || num == 4 || num == 8 || num == 16 || num == 32 || num == 64 ||
                num == 128 || num == 256 || num == 512);
        return flag;
    }

    /**
     * 撤销
     */
    public void revert() {
        // 未保存过历史数字矩阵，不能撤销
        int sum = 0;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                sum += mGameMatrixHistory[i][j];
            }
        }
        if(sum != 0) {
            Config.gameScore = mScoreHistory;
            ((MainActivity)getContext()).setScoreText(Config.gameScore);
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 重新开始游戏
     */
    public void startGame() {
        Config.gameGoal = Config.sp.getInt(Config.KEY_GAME_GOAL, 2048);
        Config.gameScore = Config.sp.getInt(Config.KEY_GAME_SCORE, 0);
        Config.gameRecord = Config.sp.getInt(Config.KEY_GAME_RECORD, 0);
        ((MainActivity)getContext()).setGoalText(Config.gameGoal);
        ((MainActivity)getContext()).setScoreText(Config.gameScore);
        ((MainActivity)getContext()).setRecordText(Config.gameRecord);
        initGameView();
    }
}
