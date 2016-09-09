package com.game.wei.a2048;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
/*       全他妈是广告
        AdManager.getInstance(this).init("e01e7fda56b7b2c2","b88e30966cc8663b", true);

        SpotManager.getInstance(this).loadSpotAds();
        SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);

        SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
        //SpotManager.getInstance(this).showSpotAds(this);

        SpotManager.getInstance(this).showSplashSpotAds(this,MainActivity.class);*/

        new Thread(new Runnable() {
            @Override
            public void run() {
               /* try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                SystemClock.sleep(2000);
                //三秒钟之后，跳入到main页面
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }).start();
    }
}
