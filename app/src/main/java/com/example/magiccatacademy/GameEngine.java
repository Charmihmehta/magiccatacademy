package com.example.magiccatacademy;


import android.content.Context;

import android.content.Intent;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    Paint paintbrush1;
    Paint paintbrush_transp;
    Bitmap bgImg;
    Bitmap livesImg;
    Bitmap heartImg;
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
    int speed = 0;

    //  gesture code
    String[] gesture_code = new String[]{"line", "up_arrow", "down_arrow"};
   // String[] gesture_img = new String[]{"line","up-arrow","down-arrow"};
    //List<String> gesture_code = new ArrayList<>()
    int code;
    int[] gesture;
    List<Enemy> enemy_same_code = new ArrayList<Enemy>();

    int[] copy_gesture;

    // sound variables

    MediaPlayer bg_sound;
    MediaPlayer life_lost_sound;
    MediaPlayer crocodial_kill_sound;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();
        this.paintbrush1 = new Paint();
        this.paintbrush_transp = new Paint();

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

        livesImg = BitmapFactory.decodeResource(context.getResources(),R.drawable.monkey_head);
        heartImg = BitmapFactory.decodeResource(context.getResources(),R.drawable.heart);

        bg_sound = MediaPlayer.create(this.getContext(), R.raw.background_sond);
        life_lost_sound = MediaPlayer.create(this.getContext(), R.raw.life_lost);
        crocodial_kill_sound = MediaPlayer.create(this.getContext(), R.raw.crocodial_kill);

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

    public static int[] random_gesture(String[] gesture) {
        Random r = new Random();

        int no = generate(1, 2);
        int[] new_no = new int[no];
        for (int i = 0; i < no; i++) {

            new_no[i] = r.nextInt(gesture.length);

        }


        return new_no;

    }

    private void spawnEnemy() {

        if(lives <= 3){
        no = generate(2, 4);
        }
        else
        {
            no = generate(1,2);
        }


        // Random r = new Random();

//        for (int i = 0; i < no; i++) {
//            gesture_code[i] = String.valueOf(r.nextInt(gesture_code.length));
//        }
//        System.out.println(Arrays.toString(gesture_code));

        // gesture = 1;

        for (int i = 0; i < no; i++) {

            if (x == 0) {
                x = this.screenWidth;
                image_flag = true;
                gesture = random_gesture(gesture_code);

            } else if (x == this.screenWidth) {
                x = 0;
                image_flag = false;
                gesture = random_gesture(gesture_code);
            }

            y = generate((int) (this.screenHeight * 0.65), this.screenHeight);

            //@TODO: Place the enemies in a random location

            enemy = new Enemy(this.getContext(), x, y, image_flag, gesture);
            enemy_list.add(enemy);

        }

//        enemy_list.add(new Enemy(this.getContext(),x,y,image_flag,new int[] {2,2}));
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
            bg_sound.start();
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
                    life_lost_sound.start();
                    // Log.e(TAG, "Enemy count: " + enemy_list.size());
                    player.setXPosition(this.player.xPosition);
                    player.setYPosition(this.player.yPosition);
                    player.getHitbox();


                }
              //  life_lost_sound.stop();

            }
//spawnEnemy();

        }

            else{
                spawnEnemy();
               //
               // finish();
            }
        if(lives == 0) {
            bg_sound.stop();
            Intent gameEngine = new Intent(this.getContext(), GameOverScreen.class);
            this.getContext().startActivity(gameEngine);

        }

    }
    public void heartGesture(String gesture){
        if(gesture.equals("Heart")){
            if(lives<5 && lives!=0) {
                lives++;
            }
        }
    }

    public void drawlives(int lives){
        int x=10;
        int y =10;
        for(int i =0 ; i< lives;i++){

            livesImg = Bitmap.createScaledBitmap(livesImg,200,200,false);
            canvas.drawBitmap(livesImg,x,y,paintbrush);

            x=x+250;



        }
        if(lives<5){
            heartImg = Bitmap.createScaledBitmap(heartImg,200,200,false);
            canvas.drawBitmap(heartImg,screenWidth/2 ,screenHeight/2,paintbrush);

        }

       //


    }


    public void tempKill(String gesture) {
        int[] buffer_Array;
        Bitmap [] buffer_image;
        if (enemy_list.size() != 0) {

            //@TODO: Getting the code
            for (int i = 0; i < gesture_code.length; i++) {
                if (gesture_code[i].equals(gesture)) {
                    code = i;
                    break;
                }
            }


            for (Enemy tempEnemy : enemy_list) {

                if (tempEnemy.enemy_gesture[tempEnemy.gesture_index] == code) {

                    if (tempEnemy.enemy_gesture.length > 1) {

                        buffer_Array = new int[tempEnemy.enemy_gesture.length - 1];
                        buffer_image = new Bitmap[tempEnemy.enemy_gesture.length - 1];

//                        for (int ii = 0, jj = 0; ii < tempEnemy.enemy_gesture.length; ii++) {
//
//                            if (tempEnemy.enemy_gesture[ii] != code) {
//                                jj++;
//                                break;
//                            }
//                            Log.e(TAG, "II-------------------------------------: " + ii);
//                            Log.e(TAG, "JJ-------------------------------------: " + jj);
//                            buffer_Array[jj] = tempEnemy.enemy_gesture[ii];
//
//                            Log.e(TAG, "tempKill-------------------------------------: " + Arrays.toString(tempEnemy.enemy_gesture));
//                            Log.e(TAG, "buffer-------------------------------: " + Arrays.toString(buffer_Array) );
//                        }
                        buffer_Array[0] = tempEnemy.enemy_gesture[1];
                        tempEnemy.enemy_gesture = buffer_Array;

                        buffer_image[0] = tempEnemy.gesture_image[1];
                        tempEnemy.gesture_image = buffer_image;

                        Log.e(TAG, "enemy gesture---------: " + Arrays.toString(tempEnemy.enemy_gesture) + "gesture image--------------" + (tempEnemy.gesture_image.length) );
//                            Log.e(TAG, "buffer-------------------------------: " + Arrays.toString(buffer_Array) );
                    } else {

                        tempEnemy.enemy_gesture = null;
                        tempEnemy.gesture_image = null;

                    }
                }
            }
            List<Enemy> bufferEnemy = new ArrayList<Enemy>();

            for (Enemy tempEnemy : enemy_list) {

                if(tempEnemy.enemy_gesture == null || tempEnemy.enemy_gesture.length == 0){

                    bufferEnemy.add(tempEnemy);

                }

            }
            for(Enemy removeEnemy : bufferEnemy){

                score++;

                enemy_list.remove(removeEnemy);
                crocodial_kill_sound.start();


            }
            if(score >= 10) {
                bg_sound.stop();
                Intent gameEngine = new Intent(this.getContext(), WinScreen.class);
                this.getContext().startActivity(gameEngine);
            }


        } else {
            spawnEnemy();
        }

    }

//    public void kill_enemy() {
//        Log.e(TAG, " enemy list " + enemy_list.size());
//        if (enemy_list.size() != 0) {
//            for (int i = 0; i < enemy_list.size(); i++) {
//
//                //  int single_enemy[] = enemy_list.get(i).enemy_gesture;
//                int single_enemy[] = enemy_list.get(i).enemy_gesture;
//                if (single_enemy.length != 0) {
//                    Log.e(TAG, "charmi " + Arrays.toString(single_enemy));
//
//                    for (int j = 0; j < single_enemy.length; j++) {
//
//                        if (single_enemy[j] == code) {
//                            Log.e(TAG, "charmi " + single_enemy[j]);
//
////                            // shifting elements
//                            for (int k = j; k < single_enemy.length - 1; k++) {
//                                single_enemy[k] = single_enemy[k + 1];
//                            }
//
//                        }
//                        else{
//
//                        }
//                    }
//                }
//
//                //int remove_gesture = enemy_list.get(i).enemy_gesture;
//
////                if (single_enemy[] == code) {
////                    enemy_same_code.add(enemy_list.get(i));
////                    score++;
////
////                    Log.e(TAG, "enemy same code " + enemy_same_code.size());
////
////                } else {
////                    Toast.makeText(this.getContext(), "not removed", Toast.LENGTH_LONG).show();
////                }
//
//
//            }
//
//            enemy_list.removeAll(enemy_same_code);
//            Log.e(TAG, "after enemy same code " + enemy_same_code.size());
//            Log.e(TAG, "after enemy list " + enemy_list.size());
//        } else {
//            spawnEnemy();
//        }
//    }

//    public void gesture_performed(String gesture) {
//
//        if (gesture.equals("Heart")) {
//            if (lives < 5 && lives != 0) {
//                //  code = 0;
//                lives++;
//            } else {
//                Toast.makeText(this.getContext(), "gesture not valid", Toast.LENGTH_SHORT).show();
//
//            }
//        } else if (gesture.equals("down_arrow")) {
//            code = 2;
//            // score++;
//            kill_enemy();
//        } else if (gesture.equals("up_arrow")) {
//            code = 1;
//            // score++;
//            //spawnEnemy();
//            kill_enemy();
//        } else if (gesture.equals("line")) {
//            code = 0;
//
//            // spawnEnemy();
//            kill_enemy();
//            //score++;
//
//        }
//
//    }


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
            paintbrush_transp.setColor(getResources().getColor(android.R.color.transparent));
            paintbrush_transp.setStyle(Paint.Style.STROKE);
            paintbrush_transp.setStrokeWidth(5);
            Rect playerHitbox = player.getHitbox();
            canvas.drawRect(playerHitbox, paintbrush_transp);

            //@TODO: Show the hitboxes on enemy and gestures
            paintbrush_transp.setColor(getResources().getColor(android.R.color.transparent));
            paintbrush_transp.setStyle(Paint.Style.STROKE);
            paintbrush_transp.setStrokeWidth(5);




            for (int i = 0; i < enemy_list.size(); i++) {
                Enemy hitBox = enemy_list.get(i);
                Rect enemyHitbox = hitBox.getHitbox();


                if(hitBox.gesture_image.length !=0) {
                    for (int j = 0; j < hitBox.enemy_gesture.length; j++) {
                        int x_position = enemyHitbox.left;
                        if (j != 0) {
                            x_position = x_position + 80;
                        }

                        canvas.drawBitmap(hitBox.gesture_image[j], x_position, enemyHitbox.top - 50, paintbrush);
                    }
                }
               // canvas.drawText(" " + Arrays.toString(hitBox.enemy_gesture), enemyHitbox.left, enemyHitbox.top , paintbrush);
                canvas.drawRect(enemyHitbox, paintbrush_transp);
            }
            drawlives(lives);


            //@TODO: Draw gesture

            //@TODO: Draw text on screen
            paintbrush.setTextSize(50);
          //  canvas.drawText("lives left:" + this.lives, 50, 600, paintbrush);


            paintbrush1.setColor(Color.WHITE);
            paintbrush1.setStyle(Paint.Style.STROKE);
            paintbrush1.setStrokeWidth(5);
            paintbrush1.setTextSize(100);
            canvas.drawText("" + this.score, this.screenWidth - 200, 100, paintbrush1);

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

            if(lives <= 3){
               speed = 20;
            }
            else{
                speed = 40;
            }
            gameThread.sleep(speed);
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
