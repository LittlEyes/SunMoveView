package com.litteyes.sunmoveview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.litteyes.sunmoveview.view.SunProgressBar;

public class MainActivity extends AppCompatActivity {

    private SunProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (SunProgressBar) findViewById(R.id.sun_bar);
        bar.post(new Runnable() {
            @Override
            public void run() {
                bar.startAnimator(0, 1, 1500);
            }
        });
    }
}
