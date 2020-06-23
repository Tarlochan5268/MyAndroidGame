package com.tarlochan.myandroidgame;

import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class GameActivity extends AppCompatActivity {
    private TDView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        // Create an instance of our Tappy Defender View
        // Also passing in this.
        // Also passing in the screen resolution to the constructor
        gameView = new TDView(this, size.x, size.y);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}