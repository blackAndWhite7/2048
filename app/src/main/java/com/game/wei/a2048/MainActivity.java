package com.game.wei.a2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.wei.a2048.view.GameView;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView tv_main_score;
    private SharedPreferences sp;
    private TextView tv_main_record;
    private Button bt_main_revert;
    private Button bt_main_restart;
    private Button bt_main_option;
    private GameView gameView;
    private TextView tv_main_targetscore;
    private int targetScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        // 实例化广告条
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);

// 获取要嵌入广告条的布局
        LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

// 将广告条加入到布局中
        adLayout.addView(adView);*/

        RelativeLayout rl_main_center = (RelativeLayout) findViewById(R.id.rl_main_center);
        //      因为GameView是new出来的，所以会调用GameView的  public GameView(Context context)这个构造函数
        //      如果是把GameView写在布局文件中，则会调用 public GameView(Context context, AttributeSet attrs)，
        //      因为要解析属性列表，所以要把属性列表传进来
        gameView = new GameView(this);
        rl_main_center.addView(gameView);

        tv_main_score = (TextView) findViewById(R.id.tv_main_score);
        tv_main_record = (TextView) findViewById(R.id.tv_main_record);
        tv_main_targetscore = (TextView) findViewById(R.id.tv_main_targetscore);

        //先去文件里查找，看有没有保存过的参数，没有的话用默认给的defaultValue
        sp = getSharedPreferences("info", MODE_PRIVATE);
        int record_saved = sp.getInt("record", 0);
        targetScore = sp.getInt("targetScore", 2048);

        tv_main_record.setText(record_saved + "");//一开始初始化的时候，把record显示出来
        tv_main_targetscore.setText(targetScore + "");

        bt_main_revert = (Button) findViewById(R.id.bt_main_revert);
        bt_main_restart = (Button) findViewById(R.id.bt_main_restart);
        bt_main_option = (Button) findViewById(R.id.bt_main_option);

        bt_main_revert.setOnClickListener(this);
        bt_main_restart.setOnClickListener(this);
        bt_main_option.setOnClickListener(this);

    }

    public int getTargetScore() {
        return this.targetScore;
    }

    //给score设置显示的值
    public void setScore(int score) {
        tv_main_score.setText(score + "");
    }

    public void setTargetScore(int targetScore) {
        tv_main_targetscore.setText(targetScore + "");
    }

    //给record设置显示的值
    public void updateRecordScore(int record) {
        int record_saved = sp.getInt("record", 0);
        if (record > record_saved) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("record", record);
            edit.commit();
            tv_main_record.setText(record + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_revert:
                revert();
                break;
            case R.id.bt_main_restart:
                restart();
                break;
            case R.id.bt_main_option:
                option();
                break;
        }
    }

    private void restart() {
        new AlertDialog.Builder(this)
                .setTitle("restart")
                .setMessage("重新开始新一局吗？")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.restart();
                    }
                })
                .setNegativeButton("no", null)
                .show();
    }

    private void revert() {
        new AlertDialog.Builder(this)
                .setTitle("重新开始")
                .setMessage("确定要撤销上一步吗？")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.revert();
                    }
                })
                .setNegativeButton("no", null)
                .show();

    }

    private void option() {
        startActivityForResult(new Intent(this, OptionActivity.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 200) {
            int lineNumber = data.getIntExtra("lineNumber", 4);
            int targetScore = data.getIntExtra("targetScore", 2048);
            gameView.setTarget_score(targetScore);
            //更新设置之后的目标分数
            setTargetScore(targetScore);
            //更新棋盘
            gameView.setRowNumber(lineNumber);
            gameView.setColumnNumber(lineNumber);
            gameView.restart();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
