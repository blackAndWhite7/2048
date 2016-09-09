package com.game.wei.a2048.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class NumberItem extends FrameLayout {

    private TextView tv_numberItem;
    private int number;
    public NumberItem(Context context) {
        super(context);
        init(context);
    }
    public NumberItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private void init(Context context) {
        tv_numberItem = new TextView(context);
        tv_numberItem.setText("");
        tv_numberItem.setGravity(Gravity.CENTER);
        tv_numberItem.setTextSize(15);
        setNumber(0);
        tv_numberItem.setBackgroundColor(0xFFCAE6CA);

        //给每个TextView设置一个margin
        //要把TextView增加到FrameLayout，所以必须给一个FrameLayout的LayoutParameter,即这个LayoutParams是 android.widget.FrameLayout包下面的
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(5,5,5,5);

        addView(tv_numberItem,params);
    }

    public void setNumber(int number){
        this.number = number;
        if(number==0){
            tv_numberItem.setText("");
        }else{
            tv_numberItem.setText(number+"");
        }
        switch (number){

            case 0:
                tv_numberItem.setBackgroundColor(0x00000000); //ARGB
                break;
            case 2:
                tv_numberItem.setBackgroundColor(0xFFFFF5EE);
                break;
            case 4:
                tv_numberItem.setBackgroundColor(0xFFFFEC8B);
                break;
            case 8:
                tv_numberItem.setBackgroundColor(0xFFFFE4C4);
                break;
            case 16:
                tv_numberItem.setBackgroundColor(0xFFFFDAB9);
                break;
            case 32:
                tv_numberItem.setBackgroundColor(0xFFFFC125);
                break;
            case 64:
                tv_numberItem.setBackgroundColor(0xFFFFB6C1);
                break;
            case 128:
                tv_numberItem.setBackgroundColor(0xFFFFA500);
                break;
            case 256:
                tv_numberItem.setBackgroundColor(0xFFFF83FA);
                break;
            case 512:
                tv_numberItem.setBackgroundColor(0xFFFF7F24);
                break;
            case 1024:
                tv_numberItem.setBackgroundColor(0xFFFF6A6A);
                break;
            case 2048:
                tv_numberItem.setBackgroundColor(0xFFFF1493);
                break;
            case 4096:
                tv_numberItem.setBackgroundColor(0xFFFF3030);
                break;
            case 8192:
                tv_numberItem.setBackgroundColor(0xFF008B45);
                break;
            case 16384:
                tv_numberItem.setBackgroundColor(0xFF0A0A0A);
                break;
        }
    }
    public void setText(String numberString){
        tv_numberItem.setText(numberString);
    }
    public int getNumber(){
        return number;
    }

}
