package com.tarlochan.myandroidgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    private float distanceRemaining;
    private long timeTaken;
    private boolean gameEnded;
    private Context context;
    private long timeStarted;
    private long fastestTime;
    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    private int screenX;
    private int screenY;
    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new  ArrayList<SpaceDust>();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;
        screenX = x;
        screenY = y;
        ourHolder = getHolder();
        paint = new Paint();

        startGame();
        // Get a reference to a file called HiScores.
        // If id doesn't exist one is created
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
        // Initialize the editor ready
        editor = prefs.edit();
        // Load fastest time from a entry in the file
        // labeled "fastestTime"
        // if not available highscore = 1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);
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
                // ok
                player.stopBoosting();
                break;
                // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                // If we are currently on the pause screen, start a new game
                if(gameEnded){
                    startGame();
                }
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
            //rectDebug();

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

            if(!gameEnded)
            {
                //draw the text and other info on screen
                drawHUD();
            }
            else
            {
                gameOverHUD();
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }
    private void gameOverHUD()
    {
        // Show pause screen
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", screenX/2, 100, paint);
        paint.setTextSize(25);
        canvas.drawText("Fastest:"+ fastestTime + "s", screenX/2, 160, paint);
        canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 200, paint);
        canvas.drawText("Distance remaining:" + distanceRemaining/1000 + " KM",screenX/2, 240, paint);
        paint.setTextSize(80);
        canvas.drawText("Tap to replay!", screenX/2, 350, paint);
    }
    private void drawHUD()
    {
        // Draw the hud
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(25);
        canvas.drawText("Fastest:"+ fastestTime + "s", 10, 20, paint);
        canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20, paint);
        canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);
        canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
        canvas.drawText("Speed:" +player.getSpeed() * 60 + " MPS", (screenX /3 ) * 2, screenY - 20, paint);
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
        boolean hitDetected = false;
        // If you are using images in excess of 100 pixels
        // wide then increase the -100 value accordingly
        if(Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
            hitDetected = true;
            enemy1.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())){
            hitDetected = true;
            enemy2.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy3.getHitbox())){
            hitDetected = true;
            enemy3.setX(-100);
        }

        if(hitDetected) {
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                //game over
                gameEnded = true;
            }
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        for (SpaceDust sd : dustList)
        {
            sd.update(player.getSpeed());
        }

        if(!gameEnded) {
            //subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();
            //How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }
        //Completed the game!
        if(distanceRemaining < 0){
            //check for new fastest time
            if(timeTaken < fastestTime) {
                // Save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }

            // avoid ugly negative numbers
            // in the HUD
            distanceRemaining = 0;
            // Now end the game
            gameEnded = true;
        }
    }

    private void startGame(){
        //Initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);
        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }
        // Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
        // Get start time
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }
}
