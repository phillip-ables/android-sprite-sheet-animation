package com.example.phill.sprite_sheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
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
        boolean isOneShot = false;

        private int canvasWidth, canvasHeight, lastCanvasHeight;
        private int score, lifeCounterOfTurtle;
        private Paint scorePaint = new Paint();
        private Bitmap life[] = new Bitmap[2];

        Bitmap bitmap_turtle;
        private int turtle_x = 5;
        private int turtle_y;
        private int turtle_speed = 4;
        private int turtle_gravity = 2;
        private int turtle_jumpSpeed = 40;

        private int turtle_scaleFactor = 5;
        private int turtle_frameWidth = 300;
        private int turtle_frameHeight = 125;
        private int turtle_upFrameCount = 4;
        private int turtle_idleFrameCount = 2;
        private int turtle_frameCount;
        private int turtle_currentFrame = 0;
        //float turtle_speedPerSecond = 250;

        Bitmap bitmap_worm;
        private int worm_x = 200;
        private int worm_y = 300;
        private int worm_speed = 16;

        private int worm_scaleWidth = 8;
        private int worm_scaleHeight = 3 * worm_scaleWidth;
        private int worm_frameWidth = 200;
        private int worm_frameHeight = 93;
        private int worm_frameCount = 4;
        private int worm_currentFrame;

        //Bitmaps for hire

        //Bitmap bitmapWorm;
        //Bitmap bitmapTurtle;
        Bitmap bitmapBackground;
        Bitmap bitmapstraw;
        float backgroundXPos = 0;

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
                worm_frameWidth,
                worm_frameHeight
        );

        RectF worm_whereToDraw = new RectF(
                worm_x,
                worm_y,
                worm_x + worm_frameWidth,
                worm_y + worm_frameHeight
        );


        //call new special constructor method runs
        public GameView(Context context) {
            super(context);  // asks suface view to set up our object

            ourHolder = getHolder();
            paint = new Paint();

            bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_235);
            bitmap_turtle = Bitmap.createScaledBitmap(
                    bitmap_turtle,
                    turtle_frameWidth,
                    turtle_frameHeight,
                    false
            );

            //worm
            bitmap_worm = BitmapFactory.decodeResource(this.getResources(), R.drawable.worm_200_93);
            bitmap_worm = Bitmap.createScaledBitmap(
                    bitmap_worm,
                    worm_frameWidth,
                    worm_frameHeight,
                    false
            );


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lastCanvasHeight = displayMetrics.heightPixels;
            canvasWidth = displayMetrics.widthPixels;

            turtle_frameWidth = canvasWidth / turtle_scaleFactor;
            worm_frameWidth = canvasWidth / worm_scaleWidth;
            worm_frameHeight = lastCanvasHeight/ worm_scaleHeight;

            //DISPLAYS
            scorePaint.setColor(Color.WHITE);
            scorePaint.setTextSize(70);
            scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
            scorePaint.setAntiAlias(true);

            life[0] =

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

                //if(!isOneShot)
                //    isMoving = false;
            }
        }

        public void update() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            canvasHeight = displayMetrics.heightPixels;

            if(lastCanvasHeight != canvasHeight){
                Log.e("CHANGE", "reset frameWidth");
                canvasWidth = displayMetrics.widthPixels;
                canvasHeight = displayMetrics.heightPixels;

                turtle_frameWidth = canvasWidth / turtle_scaleFactor;
                turtle_frameHeight = canvasHeight / turtle_scaleFactor;

                worm_frameWidth = canvasWidth / worm_scaleWidth;
                worm_frameHeight = canvasHeight / worm_scaleHeight;

                lastCanvasHeight = canvasHeight;
            }

            int minTurtleY = bitmap_turtle.getHeight();
            int maxTurtleY = canvasHeight - (2 * bitmap_turtle.getHeight());
            turtle_y += turtle_speed;
            // i think below is a better substitute for this
            //turtle_y += (turtle_speedPerSecond / fps);
            turtle_speed += turtle_gravity;


            if (turtle_y < minTurtleY)
                turtle_y = minTurtleY;
            if (turtle_y > maxTurtleY)
                turtle_y = maxTurtleY;

            //junk code for which resource to use
            if(isMoving) {
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_up_350_235);
                turtle_frameCount = turtle_upFrameCount;
            }else{
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_235);
                turtle_frameCount = turtle_idleFrameCount;
            }

            //worm logic
            if(collisionChecker(worm_x, worm_y)){
                //score += 10;
                worm_x -= 300;
            }
            worm_x -= worm_speed;
            if(worm_x < 0){
                worm_x = canvasWidth + worm_frameWidth;
                worm_y = (int) Math.floor(Math.random() * (maxTurtleY - minTurtleY) + minTurtleY);
            }
        }

        public void draw() {
            //valid or we crash
            if(ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);

                //this will be placed in a final draw function
                bitmap_turtle = Bitmap.createScaledBitmap(
                        bitmap_turtle,
                        turtle_frameWidth * turtle_frameCount,
                        turtle_frameHeight,
                        false
                );
                turtle_whereToDraw.set(
                        turtle_x,
                        turtle_y,
                        turtle_x + turtle_frameWidth,
                        turtle_y + turtle_frameHeight
                );

                //WORM
                bitmap_worm = Bitmap.createScaledBitmap(
                  bitmap_worm,
                  worm_frameWidth * worm_frameCount,
                  worm_frameHeight,
                  false
                );
                worm_whereToDraw.set(
                        worm_x,
                        worm_y,
                        worm_x + worm_frameWidth,
                        worm_y + worm_frameHeight
                );


                //not sure why this is here
                getCurrentFrame();

                canvas.drawBitmap(
                        bitmap_turtle,
                        turtle_frameToDraw,
                        turtle_whereToDraw,
                        paint
                );

                canvas.drawBitmap(
                        bitmap_worm,
                        worm_frameToDraw,
                        worm_whereToDraw,
                        paint
                );




                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void getCurrentFrame() {
            long time = System.currentTimeMillis();
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {  // this logic orig came after is moving logic
                turtle_currentFrame++;
                worm_currentFrame++;
                if(isMoving) {  // animate for if is moving
                    lastFrameChangeTime = time;  // this maybe should be in the main function and not ever inner peice
                    if (turtle_currentFrame >= turtle_upFrameCount){
                        //isOneShot = false;
                        turtle_frameCount = 0;
                        isMoving = false;
                    }
                }
                else{
                    lastFrameChangeTime = time;
                    if(turtle_currentFrame >= turtle_idleFrameCount)
                        turtle_currentFrame = 0;
                }
                if(worm_currentFrame >= worm_frameCount)
                    worm_currentFrame = 0;
            }
            turtle_frameToDraw.left = turtle_currentFrame * turtle_frameWidth;
            turtle_frameToDraw.right = turtle_frameToDraw.left + turtle_frameWidth;

            worm_frameToDraw.left = worm_currentFrame * worm_frameWidth;
            worm_frameToDraw.right = worm_frameToDraw.left + worm_frameWidth;
        }

        public boolean collisionChecker(int x, int y){
            if(turtle_x < x && x < (turtle_x + bitmap_turtle.getWidth()) &&
                    turtle_y < y && y < (turtle_y + turtle_frameWidth))
                return true;
            return false;
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
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                //isOneShot = true;
                isMoving = true;
                turtle_speed -= turtle_jumpSpeed;
            }

            /*
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isOneShot = true;
                    isMoving = true;
                    turtle_speed -= turtle_jumpSpeed;
                    break;

                case MotionEvent.ACTION_UP:
                    if(!isOneShot) {
                        isMoving = false;
                    }
                    break;
            }
            */
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
