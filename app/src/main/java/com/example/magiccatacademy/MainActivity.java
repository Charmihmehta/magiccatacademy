package com.example.magiccatacademy;


import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    GameEngine magicCat;

    // Gesture objects
    GestureLibrary lib;
    GestureOverlayView gestureOverlayView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gesture object iniialization

        gestureOverlayView = new GestureOverlayView(this);
        frameLayout = new FrameLayout(this);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        // Orientation for gesture

        gestureOverlayView.setOrientation(gestureOverlayView.ORIENTATION_VERTICAL);
        gestureOverlayView.setEventsInterceptionEnabled(true);
        gestureOverlayView.setGestureStrokeType(gestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);


        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        magicCat = new GameEngine(this, size.x, size.y);
        lib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

                ArrayList<Prediction> gesture_array = lib.recognize(gesture);


//                for (Prediction temp : gesture_array) {

                if (gesture_array.size() > 0) {
                    Prediction pred = gesture_array.get(0);

                    if (pred.score > 1.0) {
                        Toast.makeText(MainActivity.this, pred.name, Toast.LENGTH_LONG).show();
                        Log.e("Gesture", "Gesture detected:" + pred.name);
                        magicCat.tempKill(pred.name);

                    }
//            }
                }
            }
        });

        frameLayout.addView(magicCat, 0);
        frameLayout.addView(gestureOverlayView, 1);

        // Make GameEngine the view of the Activity
        setContentView(frameLayout);


        if (!lib.load()) {
            finish();

        }


    }

    // Android Lifecycle function
    @Override
    protected void onResume() {
        super.onResume();
        magicCat.startGame();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        magicCat.pauseGame();
    }

    // @Override

}