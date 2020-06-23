package com.tarlochan.myandroidgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    public TDView(Context context, int x, int y) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        player = new PlayerShip(context,x,y);
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
            // Draw the player
            canvas.drawBitmap(player.getBitmap(),player.getX(),player.getY(),paint);
            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void update() {
        player.update();
    }
}
