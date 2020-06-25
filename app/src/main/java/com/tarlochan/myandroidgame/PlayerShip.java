package com.tarlochan.myandroidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class PlayerShip {
    private Bitmap bitmap;
    private int shieldStrength;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    // A hit box for collision detection
    private Rect hitBox;

    private final int GRAVITY = -12;
    // Stop ship leaving the screenprivate
    int maxY;
    private int minY;
    //Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    public PlayerShip(Context context, int screenX, int screenY)
    {
        boosting = false;
        this.x = 50;
        this.y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        shieldStrength = 2;
        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }
    public void update()
    {
        // Are we boosting?
        if (boosting)
        {    // Speed up
            speed += 2;
        }
        else {
            // Slow down
            // down
            speed -= 5;
        }  // Constrain top speed
        if (speed > MAX_SPEED) {    speed = MAX_SPEED;}
        // Never stop completely
        if (speed < MIN_SPEED) {    speed = MIN_SPEED;}
        // move the ship up or down
        y -= speed + GRAVITY;
        // But don't let ship stray off screen
        if (y < minY) {    y = minY;  }
        if (y > maxY) {    y = maxY;  }
        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public void setBoosting() {  boosting = true;}
    public void stopBoosting() {  boosting = false;}

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }
    public Rect getHitbox(){ return hitBox; }
    public int getShieldStrength() {
        return shieldStrength;
    }
    public void reduceShieldStrength(){
        shieldStrength --;
    }
}
