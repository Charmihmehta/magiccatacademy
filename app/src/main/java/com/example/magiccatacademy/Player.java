package com.example.magiccatacademy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import static com.example.magiccatacademy.GameEngine.TAG;

public class Player {

    int xPosition;
    int yPosition;
    int monkeyWidth;
    int monkeyHeight;
    int direction = -1;              // -1 = not moving, 0 = down, 1 = up
    Bitmap playerImage;
    Bitmap resized_player;
    private Rect hitbox;
    public Player(Context context, int x, int y) {

        this.playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.monkey);
        resized_player = Bitmap.createScaledBitmap(this.playerImage,(int)(this.playerImage.getWidth()*0.30), (int)(this.playerImage.getHeight()*0.30), true);
        this.xPosition = x - (resized_player.getWidth() / 2);
        this.yPosition = y ;

        monkeyWidth = this.resized_player.getWidth();
        monkeyHeight = this.resized_player.getHeight();

//       // Log.d(TAG, "Playerx: " + x + "plaY = " + y);
//
     //  Log.d(TAG, "positionffff (left, top) = " + this.xPosition  + "," + this.yPosition);
      // Log.d(TAG, "monkey (right, bottom) = " + (this.monkeyWidth )+ "," + (this.monkeyHeight));

        // @TODO: Resizing the hit box
       this.hitbox = new Rect((int) (this.xPosition * 1.05 ), (int) (this.yPosition * 1.02), (int)((this.xPosition + monkeyWidth) * 0.97), (int)((this.yPosition + monkeyHeight) * 0.99 ));

    }
    public void updateHitbox() {
        // update the position of the hitbox
       //  this.xPosition = (int) (this.xPosition * 1.05 );

        this.hitbox = new Rect((int) (this.xPosition * 1.05 ), (int) (this.yPosition * 1.02), (int)((this.xPosition + monkeyWidth) * 0.97), (int)((this.yPosition + monkeyHeight) * 0.99 ));


        //move the hitbox
//        this.hitbox.left = this.xPosition ;   //x1
//        this.hitbox.top = this.yPosition;     //y1
//        this.hitbox.right = (this.xPosition + this.resized_player.getWidth());   //x2
//        this.hitbox.bottom = (this.yPosition + this.resized_player.getHeight());  //y2

    }

    public Rect getHitbox() {
        return this.hitbox;
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

//    /**
//     * Sets the direction of the player
//     * @param i     0 = down, 1 = up
//     */
//    public void setDirection(int i) {
//        this.direction = i;
//    }
    public Bitmap getBitmap() {
        return resized_player;
    }
}
