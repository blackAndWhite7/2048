package com.game.wei.a2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends Activity implements View.OnClickListener {
    private int lineNumber;
    private int targetScore;
    private SharedPreferences sp;
    private Button bt_option_line;
    private Button bt_option_goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        //先去文件里查找，看有没有以前保存过的参数，没有的话，用默认的4,2048
        sp = getSharedPreferences("info", MODE_PRIVATE);
        lineNumber = sp.getInt("lineNumber", 4);
        targetScore = sp.getInt("targetScore", 2048);

        Button bt_option_back = (Button) findViewById(R.id.bt_option_back);
        Button bt_option_done = (Button) findViewById(R.id.bt_option_done);
        bt_option_line = (Button) findViewById(R.id.bt_option_line);
        bt_option_goal = (Button) findViewById(R.id.bt_option_goal);

        Button bt_contactme = (Button) findViewById(R.id.bt_contactme);

        bt_contactme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:17010288867"));
                startActivity(intent);
            }
        });

        //如果以前设置过参数，把以前保存的参数显示出来
        bt_option_line.setText(lineNumber + "");
        bt_option_goal.setText(targetScore + "");

        bt_option_back.setOnClickListener(this);
        bt_option_done.setOnClickListener(this);
        bt_option_line.setOnClickListener(this);
        bt_option_goal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_option_back:
                back();
                break;
            case R.id.bt_option_done:
                done();
                break;
            case R.id.bt_option_line:
                setLines();
                break;
            case R.id.bt_option_goal:
                setScore();
                break;
        }

    }

    private void back() {
        finish();
    }

    private void done() {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("lineNumber", lineNumber);
        edit.putInt("targetScore", targetScore);
        edit.commit();
        //把设置好的参数发送给主界面，让主界面处理
        Intent intent = new Intent();
        intent.putExtra("lineNumber", lineNumber);
        intent.putExtra("targetScore", targetScore);
        setResult(200, intent);
        finish();
        //Toast.makeText(this,lineNumber+"=="+targetScore, Toast.LENGTH_SHORT).show();
    }

    private int select = 0;

    private void setScore() {
        final String[] traget = {"1024", "2048", "4096", "8192"};
        new AlertDialog.Builder(this)
                .setTitle("设置目标分数")
                .setSingleChoiceItems(traget, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select = which;
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetScore = Integer.parseInt(traget[select]);
                        bt_option_goal.setText(traget[select]);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private int choice = 0;

    private void setLines() {
        final String[] choices = {"4", "5", "6", "8"};
        new AlertDialog.Builder(this)
                .setTitle("设置布局大小")
                .setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice = which;
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lineNumber = Integer.parseInt(choices[choice]);
                        bt_option_line.setText(choices[choice]);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
