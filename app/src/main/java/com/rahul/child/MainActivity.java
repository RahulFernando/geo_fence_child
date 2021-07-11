package com.rahul.child;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Animation splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hooks
        splash = AnimationUtils.loadAnimation(this, R.anim.splash);
        img = findViewById(R.id.img_splash);

        img.setAnimation(splash);

        // start login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        },4000);
    }
}