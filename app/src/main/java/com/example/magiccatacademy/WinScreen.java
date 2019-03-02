package com.example.magiccatacademy;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class WinScreen extends Activity {
    private static int SPLASH_TIME_OUT =4000;
    // sound variables

    MediaPlayer bg_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.win_screen);
        bg_sound = MediaPlayer.create(this, R.raw.splash_sound);
        bg_sound.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent gameEngine = new Intent(WinScreen.this, MainActivity.class);
                startActivity(gameEngine);
                bg_sound.stop();
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
