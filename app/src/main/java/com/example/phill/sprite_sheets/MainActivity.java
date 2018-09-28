package com.example.phill.sprite_sheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

        private int frameWideth = 100;
        private int frameHeight = 50;
        private int upFrameCount = 4;
        private int idleFrameCount = 2;

        private int currentFrame = 0;

        private long lastFrameChangeTime = 0;

        private int frameLengthInMilliseconds = 100;

        private Rect frameToDraw = new Rect(
                0,
                0,
                frameWideth,
                frameHeight
        );

        RectF whereToDraw = new RectF(
                turtleXPosition,
                0,
                turtleXPosition + frameWideth,
                frameHeight
        );

        //call new special constructor method runs
        public GameView(Context context) {
            super(context);  // asks suface view to set up our object

            ourHolder = getHolder();
            paint = new Paint();

            bitmapTurtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle);
        }

        @Override
        public void run() {
            while(playing){
                long startFrameTime = System.currentTimeMillis();

                //update
                //draw

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            if(isMoving){
                turtleXPosition = turtleXPosition + (walkSpeedPerSecond / fps);
            }
        }

        public void draw() {
            //valid or we crash
            if(ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);
                canvas.drawText("FPS:"+fps, 20,40, paint);


                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error: ", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = true;
                    break;

                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
            }
            return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}
