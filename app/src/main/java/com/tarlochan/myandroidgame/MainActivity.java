package com.tarlochan.myandroidgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Prepare to load fastest time
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        btnplay = (Button) findViewById(R.id.btnPlay);
        // Get a reference to the TextView in our layout
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);
        btnplay.setOnClickListener(this);
        // Load fastest time
        // if not available our high score = 1000000
        long fastestTime = prefs.getLong("fastestTime", 1000000);
        // Put the high score in our TextView
        textFastestTime.setText("Fastest Time:" + fastestTime);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
        finish();
    }
}