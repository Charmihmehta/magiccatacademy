package com.example.magiccatacademy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public class Enemy {
    int xPosition;
    int yPosition;
    int direction;
    int image_enemy;
    Bitmap image;
    Bitmap resized_enemy;
    int[]  enemy_gesture;
    int gesture_index ;
    Bitmap [] gesture_image;


    private Rect hitBox;

    public Enemy(Context context, int x, int y, boolean image_flag,  int[] gesture) {

        if (image_flag == true) {

            image_enemy = R.drawable.croc_walk01;
        } else {
            image_enemy = R.drawable.croc_walk_left_01;
        }
        this.image = BitmapFactory.decodeResource(context.getResources(), image_enemy);
        resized_enemy = Bitmap.createScaledBitmap(this.image, (int) (this.image.getWidth() * 0.50), (int) (this.image.getHeight() * 0.50), true);
        this.xPosition = x;
        this.yPosition = y;
        this.enemy_gesture = gesture;
        gesture_index = 0;

        //@TODO: Draw the gesture
        this.gesture_image = new Bitmap[this.enemy_gesture.length];
        for(int i =0 ; i< this.enemy_gesture.length ; i++){

            if(this.enemy_gesture[i] == 0) {
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.line);
            }
            else if(this.enemy_gesture[i] == 1) {
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.up_arrow);
            }
            else if(this.enemy_gesture[i] == 2) {
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.down_arrow);
            }
            this.gesture_image[i] = this.image;

            Log.e("enemy gesture of i","" + this.enemy_gesture[i]);






        }
        Log.e("enemy gesture of i","------------------------------" );

        // @TODO: Resizing the hit box
        this.hitBox = new Rect((int) (this.xPosition * 1.01), (int) (this.yPosition * 1.3), (int) ((this.xPosition + this.resized_enemy.getWidth()) * 0.99), (int) ((this.yPosition + this.resized_enemy.getHeight()) * 0.96));
    }

    public Bitmap[] drawGestureEnemy() {

            return this.gesture_image;

        // update the position of the hitbox


    }

    public void deleteEnemy() {

    }

    public void updateHitbox() {
        // update the position of the hitbox
        // this.xPosition = this.xPosition - 15;


        //move the hitbox
        this.hitBox.left = this.xPosition;   //x1
        this.hitBox.top = this.yPosition;     //y1
        this.hitBox.right = (this.xPosition + this.resized_enemy.getWidth());   //x2
        this.hitBox.bottom = (this.yPosition + this.resized_enemy.getHeight());  //y2

        this.getHitbox();
    }

    public Rect getHitbox() {
        return this.hitBox;
    }


    public void setXPosition(int x) {
        this.xPosition = x;
        this.updateHitbox();
    }

    public void setYPosition(int y) {
        this.yPosition = y;
        this.updateHitbox();
    }

    public int getXPosition() {
        return this.xPosition;
    }

    public int getYPosition() {
        return this.yPosition;
    }

    public Bitmap getBitmap() {
        return resized_enemy;
    }

}