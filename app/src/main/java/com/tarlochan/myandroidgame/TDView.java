package com.tarlochan.myandroidgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new  ArrayList<SpaceDust>();

    public TDView(Context context, int x, int y) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        player = new PlayerShip(context,x,y);
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);
        int numSpecs = 40;
        for(int i = 0; i < numSpecs; i++)
        {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
    }


    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        // There are many different events in MotionEvent
        // We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            // Has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
                // Do something here
                // ok
                player.stopBoosting();
                break;
                // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                // Do something here
                player.setBoosting();
                break;
        }
    return true;
    }

    @Override
    public void run()
    {
        while (playing)
        {
            update();
            draw();
            control();
        }
    }
    public void pause() {
        playing = false;
        try
        {
            gameThread.join();
        } catch (InterruptedException e)
        {

        }
    }

    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void control() {
        try
        {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {    }
    }

    private void draw() {

        if (ourHolder.getSurface().isValid())
        {  //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();
            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            //debugging rect around every sprite
            rectDebug();

            // Draw the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
            // White specs of dust
            paint.setColor(Color.argb(255, 255, 255, 255));
            for (SpaceDust sd : dustList)
            {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void rectDebug()
    {   // For debugging
        // Switch to white pixels
        paint.setColor(Color.argb(255, 255, 255, 255));
        // Draw Hit boxes
        canvas.drawRect(player.getHitbox().left, player.getHitbox().top, player.getHitbox().right, player.getHitbox().bottom, paint);
        canvas.drawRect(enemy1.getHitbox().left, enemy1.getHitbox().top, enemy1.getHitbox().right, enemy1.getHitbox().bottom, paint);
        canvas.drawRect(enemy2.getHitbox().left, enemy2.getHitbox().top, enemy2.getHitbox().right, enemy2.getHitbox().bottom, paint);
        canvas.drawRect(enemy3.getHitbox().left, enemy3.getHitbox().top, enemy3.getHitbox().right, enemy3.getHitbox().bottom, paint);
    }

    private void update() {
        // Collision detection on new positions
        // Before move because we are testing last frames
        // position which has just been drawn
        // If you are using images in excess of 100 pixels
        // wide then increase the -100 value accordingly
        if(Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
            enemy1.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())){
            enemy2.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy3.getHitbox())){
            enemy3.setX(-100);
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        for (SpaceDust sd : dustList)
        {
            sd.update(player.getSpeed());
        }
    }
}
