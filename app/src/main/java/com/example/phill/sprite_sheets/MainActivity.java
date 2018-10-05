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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //the project that will hold the vie
    //and the sprite sheet animation
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);  // passed the R.layout.activity_main
    }

    private class GameView extends SurfaceView implements Runnable{
        Thread gameThread = null;  // what is type Thread

        //We will need this when we use paint and canvas in thread
        SurfaceHolder ourHolder;

        volatile boolean playing;  // what is volatile
        Canvas canvas;
        Paint paint;

        long fps;  // game frame rate
        private long timeThisFrame;  // helps calculate the fps
        private long lastFrameChangeTime;

        private int frameLengthInMilliseconds = 150;

        boolean isMoving = false;
        boolean touch = false;

        private int canvasWidth, canvasHeight, lastCanvasHeight;

        Bitmap bitmap_turtle;
        private int turtle_x = 5;
        private int turtle_y;
        private int turtle_speed = 5;
        private int turtle_gravity = 3;
        private int turtle_jumpSpeed = 50;

        private int turtle_scaleFactor = 5;
        private int turtle_frameWidth = 300;
        private int turtle_frameHeight = 125;
        private int turtle_upFrameCount = 4;
        private int turtle_idleFrameCount = 2;
        private int turtle_frameCount;
        private int turtle_currentFrame = 0;

        float turtle_speedPerSecond = 250;

        //Bitmaps for hire
        Bitmap bitmapWorm;
        //Bitmap bitmapTurtle;
        Bitmap bitmapBackground;
        Bitmap bitmapstraw;

        //boolean isMoving = false;
        //you need to find where these go
        float worm_speedPerSecond = 250;


        //this needs fixed or handled, i hate generic values
        float wormXPosition = 300, wormYPosition=300;
        //float turtleXPosition = 5, turtleYPostition=5;
        float backgroundXPos = 0;

        private int worm_frameWidth = 150;
        private int worm_frameHeight = 50;
        private int worm_frameCount;

        // dont think we need a frame count for everything that has only one animation
        private int worm_currentFrame = 0;  // we will need current frame for each so we can reset it to zero

        //draw, this will be a function after tonight

        //Turtle draw
        private Rect turtle_frameToDraw = new Rect(
                0,
                0,
                turtle_frameWidth,
                turtle_frameHeight
        );

        RectF turtle_whereToDraw = new RectF(
                turtle_x,
                turtle_y,
                //turtleXPosition + turtle_frameWidth,
                turtle_x + turtle_frameWidth,
                turtle_y + turtle_frameHeight
        );

        //worm draw (we're good)
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


        //call new special constructor method runs
        public GameView(Context context) {
            super(context);  // asks suface view to set up our object

            ourHolder = getHolder();
            paint = new Paint();

            //this is where what is in update was before i moved it

            //this was missing from round one
            bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_235);
            //i honestly think that this might be where the error is coming from





            //i dont think that hard coded value matters, other then an IllegalArgumentException and to give it a value greater then 0
            bitmap_turtle = Bitmap.createScaledBitmap(
                    bitmap_turtle,
                    turtle_frameWidth,
                    turtle_frameHeight,
                    false
            );

            //worm


            // HERE LIES THE PROBLEM
            /*
            bitmapWorm = BitmapFactory.decodeResource(this.getResources(), R.drawable.worm_566_259);

            bitmapWorm = Bitmap.createScaledBitmap(
                    bitmapWorm,
                    worm_frameWidth * worm_frameCount,
                    worm_frameHeight,
                    false
            );
            */

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lastCanvasHeight = displayMetrics.heightPixels;
            canvasWidth = displayMetrics.widthPixels;

            //ITS GONNA BE A SQUARE FOR NOW
            // here the scale is what it should be
            turtle_frameWidth = canvasWidth / turtle_scaleFactor;

            //Log.e("FrameH", ""+turtle_frameHeight);  // value it initailized with
            //Log.e("FrameW",""+turtle_frameWidth);  // scaled factor
            //Log.e("canvas", ""+canvasWidth);  // every value that i log afterward
        }

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
            //Log.e("FrameH","1Update "+turtle_frameHeight);  // these are still good
            //Log.e("FrameW", "1Update "+turtle_frameWidth);

            //wtf was this????
            //turtle_frameWidth = canvasWidth;

            //canvasWidth = canvas.getWidth();
            //canvasHeight = canvas.getHeight();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            canvasHeight = displayMetrics.heightPixels;

            if(lastCanvasHeight != displayMetrics.heightPixels){
                Log.e("CHANGE", "reset frameWidth");
                canvasWidth = displayMetrics.widthPixels;
                canvasHeight = displayMetrics.heightPixels;
                Toast.makeText(MainActivity.this, "change height to " + canvasHeight, Toast.LENGTH_SHORT).show();


                //ITS GONNA BE A SQUARE FOR NOW
                turtle_frameWidth = canvasWidth / turtle_scaleFactor;
                turtle_frameHeight = canvasHeight / turtle_scaleFactor;
                lastCanvasHeight = canvasHeight;
            }

            int minTurtleY = bitmap_turtle.getHeight();
            int maxTurtleY = canvasHeight - (bitmap_turtle.getHeight());
            //int maxTurtleY = 80;
            turtle_y += turtle_speed;

            if (turtle_y < minTurtleY)
                turtle_y = minTurtleY;
            if (turtle_y > maxTurtleY)
                turtle_y = maxTurtleY;

            turtle_speed += turtle_gravity;


            //junk code for which resource to use
            if(isMoving) {
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_up_350_235);  // these really should be the same height for contentuities sake
                turtle_frameCount = turtle_upFrameCount;

                bitmap_turtle = Bitmap.createScaledBitmap(
                        bitmap_turtle,
                        turtle_frameWidth * turtle_frameCount,
                        turtle_frameHeight,
                        false
                );
            }else{
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_235);
                turtle_frameCount = turtle_idleFrameCount;

                //else added same
                bitmap_turtle = Bitmap.createScaledBitmap(
                        bitmap_turtle,
                        turtle_frameWidth * turtle_frameCount,
                        turtle_frameHeight,
                        false
                );
            }

            Log.e("FrameH", "Update"+turtle_frameHeight);
            Log.e("FrameW","Update "+turtle_frameWidth);


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
                        turtle_x,
                        turtle_y,
                        //(int)turtleXPosition + turtle_frameWidth,
                        turtle_x + turtle_frameWidth,
                        turtle_y + turtle_frameHeight
                );

                getCurrentFrame();

                canvas.drawBitmap(
                        bitmap_turtle,
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


                Log.e("FrameW","Draw "+turtle_frameWidth);
                Log.e("FrameH","Draw"+turtle_frameHeight);
                Log.e("canvas", ""+canvasWidth);

                ourHolder.unlockCanvasAndPost(canvas);
                //Log.e("x", ""+turtle_x);
                //Log.e("y", ""+turtle_y);
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
                    turtle_speed -= turtle_jumpSpeed;
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
