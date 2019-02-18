package com.example.magiccatacademy;


import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG = "MAGIC-CAT-ACADEMY";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;
    private static final long VELOCITY_THRESHOLD = 3000;

    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;
    Bitmap bgImg;
//    Path mPath;

//    Paint mPaint;

    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    Player player;
    Enemy enemy;
    Boolean image_flag;
    List<Enemy> enemy_list = new ArrayList<Enemy>();
    // TextView mTextView;

    // ----------------------------
    // ## GAME STATS
    // ----------------------------
    int score = 0;
    int lives = 5;
    //no of enemy
    int no;


    float monkeyX;
    float monkeyY;
    int x = this.screenWidth;
    int y;

    //  gesture code
    String[] gesture_code = new String[]{"line", "up_arrow", "down_arrow"};
    int code;
    int gesture;
    List<Enemy> enemy_same_code = new ArrayList<Enemy>();

    // sound variables

    MediaPlayer bg_sound;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();


        // @TODO: Add your sprites
        this.spawnPlayer();
        this.spawnEnemy();
        // @TODO: Any other game setup

        // load the background
        //load the image
        bgImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.jungle);
        //result the image on the phone

        bgImg = Bitmap.createScaledBitmap(bgImg, this.screenWidth, this.screenHeight, false);

        bg_sound = MediaPlayer.create(this.getContext(), R.raw.background_sond);
        bg_sound.start();
    }


    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {


        // Log.d(TAG, "spawn player (w, h) = " + ((this.screenWidth/2 ) )+ "," + ((this.screenHeight/2) ));
        //@TODO: Place the player in a location
        player = new Player(this.getContext(), this.screenWidth / 2, (int) ((int) this.screenHeight * 0.65));


    }

    public static int generate(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private void spawnEnemy() {

        no = generate(1, 6);
        Random r = new Random();
        gesture = r.nextInt(gesture_code.length);

        // gesture = 1;

        for (int i = 0; i < no; i++) {

            if (x == 0) {
                x = this.screenWidth;
                image_flag = true;
                gesture = r.nextInt(gesture_code.length);

            } else if (x == this.screenWidth) {
                x = 0;
                image_flag = false;
                gesture = r.nextInt(gesture_code.length);
            }

            y = generate((int) (this.screenHeight * 0.65), this.screenHeight);

            //@TODO: Place the enemies in a random location

            enemy = new Enemy(this.getContext(), x, y, image_flag, gesture);
            enemy_list.add(enemy);

        }
    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();

            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    public void updatePositions() {
        //  Log.e(TAG, "Enemy count: " + enemy_list.size());

        if (enemy_list.size() != 0) {

            for (int i = 0; i < enemy_list.size(); i++) {

                //get enemy out of the array
                Enemy single_enemy = enemy_list.get(i);
                //@TODO : update enemy
                double a = (player.xPosition - single_enemy.xPosition);
                double b = (player.yPosition - single_enemy.yPosition);
                double distance = Math.sqrt((a * a) + (b * b));

                // 2. calculate the "rate" to move
                double xn = (a / distance);
                double yn = (b / distance);

                // 3. move the enemy
                single_enemy.xPosition = single_enemy.xPosition + (int) (xn * 5);
                single_enemy.yPosition = single_enemy.yPosition + (int) (yn * 5);

                single_enemy.updateHitbox();

                // Log.d(TAG, "Position of bullet " + i + ": (" + bull.x + "," + bull.y + ")");

                // @TODO: Collision detection between player and enemy
            }


            for (int i = 0; i < enemy_list.size(); i++) {

//            for (Enemy temp_Enemy : enemy_list)

                if (player.getHitbox().intersect(enemy_list.get(i).getHitbox())) {
                    lives--;
//                enemy_list.clear();

                    enemy_list.remove(i);
                    // Log.e(TAG, "Enemy count: " + enemy_list.size());
                    player.setXPosition(this.player.xPosition);
                    player.setYPosition(this.player.yPosition);
                    player.getHitbox();


                }


            }
//spawnEnemy();

        } else {
            spawnEnemy();
        }

    }

    public void kill_enemy() {
        Log.e(TAG, " enemy list " + enemy_list.size());
        if (enemy_list.size() != 0) {
            for (int i = 0; i < enemy_list.size(); i++) {
                int single_enemy = enemy_list.get(i).enemy_gesture;


                if (single_enemy == code) {
                    enemy_same_code.add(enemy_list.get(i));
                    score++;

                    Log.e(TAG, "enemy same code " + enemy_same_code.size());

                } else {
                    Toast.makeText(this.getContext(), "not removed", Toast.LENGTH_LONG).show();
                }


            }

            enemy_list.removeAll(enemy_same_code);
            Log.e(TAG, "after enemy same code " + enemy_same_code.size());
            Log.e(TAG, "after enemy list " + enemy_list.size());
        } else {
            spawnEnemy();
        }
    }

    public void gesture_performed(String gesture) {

        if (gesture.equals("Heart")) {
            if (lives < 5 && lives != 0) {
                //  code = 0;
                lives++;
            } else {
                Toast.makeText(this.getContext(), "gesture not valid", Toast.LENGTH_SHORT).show();

            }
        } else if (gesture.equals("down_arrow")) {
            code = 2;
            // score++;
            kill_enemy();
        } else if (gesture.equals("up_arrow")) {
            code = 1;
            // score++;
            //spawnEnemy();
            kill_enemy();
        } else if (gesture.equals("line")) {
            code = 0;

            // spawnEnemy();
            kill_enemy();
            //score++;

        }

    }


    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();
            monkeyX = (float) ((this.screenWidth / 2) - (this.player.resized_player.getWidth() / 2));
            monkeyY = (float) ((this.screenHeight * 0.65));


            this.canvas.drawColor(Color.argb(255, 255, 255, 255));
            paintbrush.setColor(Color.WHITE);

            //@TODO: Draw the background
            canvas.drawBitmap(bgImg, 0, 0, paintbrush);

            //@TODO: Draw the gesture
//            this.canvas.drawPath( mPath,  mPaint);


            //@TODO: Draw the player
            // Log.d(TAG, "player position (left, top) = " + (float) ((this.screenWidth/2)- (this.player.resized_player.getWidth() / 2)) + "," +  (float) ((this.screenHeight/2)+ (this.player.resized_player.getHeight() / 2)));
            canvas.drawBitmap(this.player.getBitmap(), this.player.xPosition, this.player.yPosition, paintbrush);

            //@TODO: Draw the enemy

            //  canvas.drawBitmap(this.enemy.getBitmap(),this.enemy.xPosition , this.enemy.yPosition, paintbrush);

            for (int i = 0; i < enemy_list.size(); i++) {
                Enemy single_enemy = enemy_list.get(i);
                canvas.drawBitmap(single_enemy.getBitmap(), single_enemy.xPosition, single_enemy.yPosition, paintbrush);
            }

            //@TODO: Show the hitboxes on player
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);
            Rect playerHitbox = player.getHitbox();
            canvas.drawRect(playerHitbox, paintbrush);

            //@TODO: Show the hitboxes on enemy and gestures
            paintbrush.setColor(Color.RED);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);


            for (int i = 0; i < enemy_list.size(); i++) {
                Enemy hitBox = enemy_list.get(i);
                Rect enemyHitbox = hitBox.getHitbox();
                canvas.drawText(" " + hitBox.enemy_gesture, enemyHitbox.left, enemyHitbox.top, paintbrush);
                canvas.drawRect(enemyHitbox, paintbrush);
            }

            //   Rect enemyHitbox = enemy_list.getHitbox();
//            canvas.drawRect(enemyHitbox.left, enemyHitbox.top, enemyHitbox.right, enemyHitbox.bottom, paintbrush);
            //   canvas.drawRect(enemyHitbox, paintbrush);

            //@TODO: Draw gesture

            //@TODO: Draw text on screen
            paintbrush.setTextSize(50);
            canvas.drawText("lives left:" + this.lives, 50, 600, paintbrush);
            canvas.drawText("Score:" + this.score, 50, 500, paintbrush);

            //  this.holder.unlockCanvasAndPost(canvas);

//            paintbrush.setTextSize(60);
//            paintbrush.setColor(Color.BLACK);
            // canvas.drawText("Lives remaining: " + lives, 100, 800, paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }


    public void setFPS() {
        try {
            gameThread.sleep(50);
        } catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//      //  int userAction = event.getActionMasked();
//        //@TODO: What should happen when person touches the screen?
//
//        switch (event.getAction()){
//
//            case MotionEvent.ACTION_DOWN:
//                mPath.moveTo(event.getX(), event.getY());
//              //  mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                mPath.reset();
//                mPath.moveTo(event.getX(), event.getY());
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                mPath.lineTo(event.getX(), event.getY());
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return mDetector.onTouchEvent(event);
//    }




/*

    @Override
    public boolean onDown(MotionEvent e) {
//        spawnEnemy();
//        score ++;
//        Toast.makeText(getContext(),"tap 2 detected" ,Toast.LENGTH_SHORT).show();
        return true;

    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(Math.abs(velocityX) < VELOCITY_THRESHOLD
                && Math.abs(velocityY) < VELOCITY_THRESHOLD){
            return false;//if the fling is not fast enough then it's just like drag
        }

        //if velocity in X direction is higher than velocity in Y direction,
        //then the fling is horizontal, else->vertical
        if(Math.abs(velocityX) > Math.abs(velocityY)){
            if(velocityX >= 0){

                spawnEnemy();
                score++;
                Toast.makeText(getContext(),"swipe right" ,Toast.LENGTH_SHORT).show();
            }else{//if velocityX is negative, then it's towards left
                spawnEnemy();
                score++;
                Toast.makeText(getContext(),"swipe left" ,Toast.LENGTH_SHORT).show();
            }
        }else{
            if(velocityY >= 0){
                spawnEnemy();
                score++;
                Toast.makeText(getContext(),"swipe down" ,Toast.LENGTH_SHORT).show();
            }else{
                spawnEnemy();
                score++;
                Toast.makeText(getContext(),"swipe top" ,Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }
*/

}
