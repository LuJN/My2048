package com.example.my2048.bean;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.my2048.config.Config;

/**
 * Created by Administrator on 2016/12/27.
 */
public class GameItem extends FrameLayout {
    private int showNum;
    private TextView tvNum;

    public GameItem(Context context, int showNum) {
        super(context);
        this.showNum = showNum;
        initGameItem();
    }

    private void initGameItem() {
        setBackgroundColor(Color.GRAY);
        tvNum = new TextView(getContext());
        setNum(showNum);
        int gameLines = Config.gameLines;
        if(gameLines == 4) {
            tvNum.setTextSize(35);
        } else if(gameLines == 5) {
            tvNum.setTextSize(25);
        } else {
            tvNum.setTextSize(20);
        }
        TextPaint tp = tvNum.getPaint();
        tp.setFakeBoldText(true);
        tvNum.setGravity(Gravity.CENTER);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);
        addView(tvNum, params);
    }

    public void setNum(int showNum) {
        this.showNum = showNum;
        if(showNum == 0) {
            tvNum.setText("");
        } else {
            tvNum.setText("" + showNum);
        }
        switch(showNum) {
            case 0:
                tvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                tvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                tvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                tvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                tvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                tvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                tvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                tvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                tvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                tvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                tvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                tvNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                tvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }

    public int getNum() {
        return showNum;
    }

    public View getItemView() {
        return tvNum;
    }
}
