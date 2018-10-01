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

        //Bitmaps for hire
        Bitmap bitmapWorm;
        Bitmap bitmapTurtle;
        Bitmap bitmapBackground;
        Bitmap bitmapstraw;

        boolean isMoving = false;
        //you need to find where these go
        float turtle_speedPerSecond = 250;
        float worm_speedPerSecond = 250;


        //this needs fixed or handled, i hate generic values
        float wormXPosition = 300, wormYPosition=300;
        float turtleXPosition = 5, turtleYPostition=5;
        float backgroundXPos = 0;

        //when these increase the drawn picture is bigger
        private int turtle_frameWidth = 350;
        private int turtle_frameHeight = 150;
        private int turtle_upFrameCount = 4;
        private int turtle_idleFrameCount = 2;
        private int turtle_frameCount;

        private int worm_frameWidth = 150;
        private int worm_frameHeight = 50;
        private int worm_frameCount;

        private int turtle_currentFrame = 0;
        // dont think we need a frame count for everything that has only one animation
        private int worm_currentFrame = 0;  // we will need current frame for each so we can reset it to zero
        private long lastFrameChangeTime = 0;

        private int frameLengthInMilliseconds = 100;


        //draw, this will be a function after tonight

        //Turtle draw
        private Rect turtle_frameToDraw = new Rect(
                0,
                0,
                turtle_frameWidth,
                turtle_frameHeight
        );

        RectF turtle_whereToDraw = new RectF(
                turtleXPosition,
                turtleYPostition,
                turtleXPosition + turtle_frameWidth,
                turtle_frameHeight
        );

        //i wounder if i loose the worm will it work
        /*
        //worm draw
        private Rect worm_frameToDraw = new Rect(
                0,
                0,
                turtle_frameWidth,
                turtle_frameHeight
        );

        RectF worm_whereToDraw = new RectF(
                wormXPosition,
                wormYPosition,
                wormXPosition + worm_frameWidth,
                worm_frameHeight
        );
        */

        //call new special constructor method runs
        public GameView(Context context) {
            super(context);  // asks suface view to set up our object

            ourHolder = getHolder();
            paint = new Paint();

            //this is where what is in update was before i moved it

            //this was missing from round one
            bitmapTurtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_245);
            //i honestly think that this might be where the error is coming from
            bitmapTurtle = Bitmap.createScaledBitmap(
                    bitmapTurtle,
                    //turtle_frameWidth * turtle_frameCount,
                    50,
                    turtle_frameHeight,
                    false
            );

            //worm
            /*
            bitmapWorm = BitmapFactory.decodeResource(this.getResources(), R.drawable.worm_566_259);

            bitmapWorm = Bitmap.createScaledBitmap(
                    bitmapWorm,
                    worm_frameWidth * worm_frameCount,
                    worm_frameHeight,
                    false
            );
            */        }

        @Override
        public void run() {
            while(playing){
                long startFrameTime = System.currentTimeMillis();

                update();
                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            //junk code for which resource to use
            if(isMoving) {
                bitmapTurtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_up_350_235);  // these really should be the same height for contentuities sake
                turtle_frameCount = turtle_upFrameCount;

                // THESE DID NOT WORK
                //added this to see if create scaled bitmap is why i get a null render crash
                bitmapTurtle = Bitmap.createScaledBitmap(
                        bitmapTurtle,
                        turtle_frameWidth * turtle_frameCount,
                        turtle_frameHeight,
                        false
                );
            }else{
                bitmapTurtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_245);
                turtle_frameCount = turtle_idleFrameCount;

                //else added same
                bitmapTurtle = Bitmap.createScaledBitmap(
                        bitmapTurtle,
                        turtle_frameWidth * turtle_frameCount,
                        turtle_frameHeight,
                        false
                );
            }

            //i dont think this needs to be called anymore
            /*
            if(isMoving){
                turtleXPosition = turtleXPosition + (turtle_speedPerSecond / fps);
            }
            */
        }

        public void draw() {
            //valid or we crash
            if(ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);


                turtle_whereToDraw.set(
                        (int)turtleXPosition,
                        0,
                        (int)turtleXPosition + turtle_frameWidth,
                        turtle_frameHeight
                );

                getCurrentFrame();

                canvas.drawBitmap(
                        bitmapTurtle,
                        turtle_frameToDraw,
                        turtle_whereToDraw,
                        paint
                );

                /*
                worm_whereToDraw.set(
                        (int)wormXPosition,
                        0,
                        (int)wormXPosition + worm_frameWidth,
                        worm_frameHeight
                );

                getCurrentFrame();

                canvas.drawBitmap(
                        bitmapWorm,
                        worm_frameToDraw,
                        worm_whereToDraw,
                        paint
                );
                */

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void getCurrentFrame() {
            long time = System.currentTimeMillis();
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {  // this logic orig came after is moving logic
                turtle_currentFrame++;

                //worm_currentFrame++;
                if(isMoving) {  // animate for if is moving
                    lastFrameChangeTime = time;  // this maybe should be in the main function and not ever inner peice
                    //want this to be update frame function
                    //update frame count
                    //check current frame
                    if (turtle_currentFrame >= turtle_upFrameCount) {
                        turtle_currentFrame = 0;
                    }
                }
                //this would be the not moving section
                //i think instead of re using that code we could just use the base
                //pass in which animation based of is moving logic
                //but for now
                else{
                    lastFrameChangeTime = time;
                    turtle_currentFrame++;

                    if(turtle_currentFrame >= turtle_idleFrameCount)
                        turtle_currentFrame = 0;
                }
                /*
                if (worm_currentFrame >= worm_frameCount)
                    worm_currentFrame = 0;
                    */
            }



            //only the turtle can move moving

            turtle_frameToDraw.left = turtle_currentFrame * turtle_frameWidth;
            turtle_frameToDraw.right = turtle_frameToDraw.left + turtle_frameWidth;

            /*
            worm_frameToDraw.left = worm_currentFrame * worm_frameWidth;
            worm_frameToDraw.right = worm_frameToDraw.left + worm_frameWidth;
            */
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
