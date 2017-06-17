package com.example.asus.ui_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.asus.ui_project.R;

import java.util.Timer;
import java.util.TimerTask;


public class GreetingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greeting);
        final Intent intent = new Intent();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                intent.setClass(GreetingActivity.this, LoginActivity.class);
                GreetingActivity.this.startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 1000);
    }
}
