package com.example.phill.sprite_sheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //the project that will hold the view
    //and the sprite sheet animation
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);  // passed the R.layout.activity_main
    }

    private class GameView extends SurfaceView implements Runnable{
        Thread gameThread = null;  // what is a type Thread

        //We will need this when we use paint and canvas in thread
        SurfaceHolder ourHolder;

        volatile boolean playing;  // what is volatile
        Canvas canvas;
        Paint paint;
        long fps;  // game frame rate
        private long timeThisFrame;  // helps calculate the fps

        //Bitmaps
        Bitmap bitmapWorm;
        Bitmap bitmapTurtle;
        Bitmap bitmapBackground;
        Bitmap bitmapstraw;

        boolean isMoving = false;
        float walkSpeedPerSecond = 250;

        float wormXPosition = 10;
        float turtleXPosition;  // will be greater then canvas length
        float backgroundXPos = 0;

        //call new special constructor method runs
        public GameView(Context context) {
            super(context);  // asks suface view to set up our object
        }

        @Override
        public void run() {

        }
    }
}
