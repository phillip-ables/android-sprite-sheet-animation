package com.example.phill.sprite_sheets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //the project that will hold the view
    //and the sprite sheet animation
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView();  // passed the this context argument
        setContentView(R.layout.activity_main);  // passed the gameView 
    }

    private class GameView {
    }
}