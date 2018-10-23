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
        //boolean isOneShot = false;

        private int canvasWidth, canvasHeight = 1, lastCanvasHeight;
        private int score, lifeCounterOfTurtle;
        private Paint scorePaint = new Paint();
        private Bitmap life[] = new Bitmap[2];

        //Bitmap bitmap_background;
        //private int background_x, background_speed = 7;

        private Bitmap bitmap_sky;
        //this doesnt need to exist because its as tall as the min y
        //private int sky_scaleHeight = 10;
        private int sky_frameWidth = 188;
        private int sky_frameHeight = 90;
        private int sky_frameCount = 13;
        private int sky_currentFrame = 0;

        private Rect sky_frameToDraw = new Rect(
                0,
                0,
                sky_frameWidth,
                sky_frameHeight
        );

        RectF sky_whereToDraw = new RectF(
                0,
                0,
                sky_frameWidth,
                sky_frameHeight
        );

        private Bitmap bitmap_water;
        private int background_spawn;
        private int background_x;
        private int background_speed = 3;
        // might give the sand a faster speed to create a parallax effect
        private Bitmap bitmap_reef;
        private int reef_spawn;
        private int reef_x;
        private int reef_speed = 4;

        private Bitmap bitmap_turtle;
        private int turtle_x = 5;
        private int turtle_y;
        private int turtle_speed = 4;
        private int turtle_gravity = 2;
        private int turtle_jumpSpeed = 40;
        private int turtle_sink = 35;
        private int turtle_fly = -35;
        private int minTurtleY;
        private int maxTurtleY;

        private int turtle_scaleFactor = 5;
        private int turtle_frameWidth = 300;
        private int turtle_frameHeight = 125;
        private int turtle_upFrameCount = 4;
        private int turtle_idleFrameCount = 2;
        private int turtle_frameCount;
        private int turtle_currentFrame = 0;
        //float turtle_speedPerSecond = 250;

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

        private Bitmap bitmap_splashEffect;
        private boolean isSplash = false;
        private int splash_velocity = 1;
        private int splash_frameWidth = 160;
        private int splash_frameHeight = 90;
        private int splash_scale = 1;
        private int splash_frameCount = 9;
        private int splash_currentFrame = 0;
        private Rect splash_frameToDraw = new Rect(
                0,
                0,
                splash_frameWidth,
                splash_frameHeight
        );

        RectF splash_whereToDraw = new RectF(
                turtle_x,
                100,
                turtle_x + splash_frameWidth,
                100 + splash_frameHeight
        );

        private Bitmap bitmap_sparkEffect;
        private boolean isSpark = false;
        private int spark_velocity = 1;
        private int spark_frameWidth = 65;
        private int spark_frameHeight = 120;
        private int spark_scale = 13;
        private int spark_frameCount = 9;
        private int spark_currentFrame;
        private Rect spark_frameToDraw = new Rect(
                0,
                0,
                spark_frameWidth,
                spark_frameHeight
        );

        RectF spark_whereToDraw = new RectF(
                turtle_x,
                10,
                turtle_x + spark_frameWidth,
                10 + spark_frameHeight
        );

        Bitmap bitmap_dropEffect;
        private int drop_x;
        private int drop_velocity;
        private int drop_frameWidth;
        private int drop_frameHeight;
        private int drop_scaleWidth;
        private int drop_scaleHeight;
        private int drop_frameCount;
        private int drop_currentFrame;
        private Rect drop_frameToDraw = new Rect(
                0,
                0,
                drop_frameWidth,
                drop_frameHeight
        );

        RectF drop_whereToDraw = new RectF(
                drop_x,
                turtle_y,
                turtle_x + drop_frameWidth,
                turtle_x + drop_frameHeight
        );

        Bitmap bitmap_worm;
        private int worm_x = 200;
        private int worm_y = 300;
        private int worm_speed = 16;

        private int worm_scaleWidth = 8;
        private int worm_scaleHeight = 3 * worm_scaleWidth;
        private int worm_frameWidth = 200;
        private int worm_frameHeight = 93;
        private int worm_frameCount = 4;
        private int worm_currentFrame = 0;

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

        Bitmap bitmap_straw;
        private int straw_x = 200;
        private int straw_y = 300;
        private int straw_speed = 16;

        private int straw_scaleWidth = 8;
        private int straw_scaleHeight = 5 * worm_scaleWidth;
        private int straw_frameWidth = 220;
        private int straw_frameHeight = 40;
        private int straw_frameCount = 3;
        private int straw_currentFrame = 0;

        private Rect straw_frameToDraw = new Rect(
                0,
                0,
                straw_frameWidth,
                straw_frameHeight
        );

        RectF straw_whereToDraw = new RectF(
                straw_x,
                straw_y,
                straw_x + straw_frameWidth,
                straw_y + straw_frameHeight
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
            bitmap_splashEffect = BitmapFactory.decodeResource(this.getResources(), R.drawable.splash_160_90);
            bitmap_splashEffect = Bitmap.createScaledBitmap(
                    bitmap_splashEffect,
                    splash_frameWidth,
                    splash_frameHeight,
                    false
            );
            bitmap_sparkEffect = BitmapFactory.decodeResource(this.getResources(), R.drawable.sparks_65_120);

            //worm
            bitmap_worm = BitmapFactory.decodeResource(this.getResources(), R.drawable.worm_200_93);
            bitmap_worm = Bitmap.createScaledBitmap(
                    bitmap_worm,
                    worm_frameWidth,
                    worm_frameHeight,
                    false
            );

            //straw
            bitmap_straw = BitmapFactory.decodeResource(this.getResources(), R.drawable.straw_220_40);
            bitmap_straw = Bitmap.createScaledBitmap(
                    bitmap_straw,
                    straw_frameWidth,
                    straw_frameHeight,
                    false
            );


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lastCanvasHeight = displayMetrics.heightPixels;
            canvasWidth = displayMetrics.widthPixels;

           // /*
            //i want to put this as just these variables and not stored in sky_variables

            sky_frameWidth = canvasWidth;
            //when i set this to minturtley then it doesnt draw
            //sky_frameHeight = minTurtleY;
             //*/

            /*
            sky_frameHeight = canvasHeight / sky_scaleHeight;


            //these all need checks to make sure that it isnt the initial size and crash
            //turtle_frameWidth = canvasWidth / turtle_scaleFactor;
           // turtle_frameHeight = canvasHeight / turtle_scaleFactor;
            spark_frameHeight = spark_frameWidth = canvasHeight / spark_scale;
            splash_frameHeight = splash_frameWidth = canvasHeight / splash_scale;

            worm_frameWidth = canvasWidth / worm_scaleWidth;
            worm_frameHeight = canvasHeight / worm_scaleHeight;

            straw_frameWidth = canvasWidth / straw_scaleWidth;
            straw_frameHeight = canvasHeight / straw_scaleHeight;
            */

            //DISPLAYS
            bitmap_sky = BitmapFactory.decodeResource(getResources(), R.drawable.sky_188_90);

            /*
            //trading this for the sky water reef resources
            bitmap_background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            their will need to be an x speed for water and sand
            background_x = 0;
            */

            scorePaint.setColor(Color.WHITE);
            scorePaint.setTextSize(70);
            scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
            scorePaint.setAntiAlias(true);

            life[0] = BitmapFactory.decodeResource(getResources(), R.drawable.hearts);
            life[1] = BitmapFactory.decodeResource(getResources(), R.drawable.heart_grey);

            score = 0;
            lifeCounterOfTurtle = 3;

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
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            canvasHeight = displayMetrics.heightPixels;

            if(lastCanvasHeight != canvasHeight){
                Log.e("CHANGE", "reset frameWidth");
                canvasWidth = displayMetrics.widthPixels;
                canvasHeight = displayMetrics.heightPixels;

                sky_frameWidth = canvasWidth;
                sky_frameHeight = minTurtleY;


                //these all need checks to make sure that it isnt the initial size and crash
                turtle_frameWidth = canvasWidth / turtle_scaleFactor;
                turtle_frameHeight = canvasHeight / turtle_scaleFactor;
                spark_frameHeight = spark_frameWidth = canvasHeight / spark_scale;
                splash_frameHeight = splash_frameWidth = canvasHeight / splash_scale;

                worm_frameWidth = canvasWidth / worm_scaleWidth;
                worm_frameHeight = canvasHeight / worm_scaleHeight;

                straw_frameWidth = canvasWidth / straw_scaleWidth;
                straw_frameHeight = canvasHeight / straw_scaleHeight;

                lastCanvasHeight = canvasHeight;
            }


            //THESE NEED UPDATED BECASUE THEY LOOK AWFUL ON A SLIM PHONE
            //MAYBE CLAMP THEM TO SOME VALUE SO THAT THE SKY IS ALWAYS ATLEAST X RATIO OR AMOUNT OF PIXELS
            //i cant stand get height () functions!!!

            //minTurtleY = bitmap_turtle.getHeight(); // i think i want this to be half of what it is
            //lets try this instead
            minTurtleY = turtle_frameHeight;
            //maxTurtleY = canvasHeight - (2 * bitmap_turtle.getHeight());
            maxTurtleY = canvasHeight - (2 * turtle_frameHeight);
            turtle_y += turtle_speed;
            // i think below is a better substitute for this
            //turtle_y += (turtle_speedPerSecond / fps);
            turtle_speed += turtle_gravity;


            if (turtle_y < minTurtleY && turtle_speed < 0) {
                turtle_y = 0;
                turtle_speed = turtle_sink;
                isSplash = true;
            }
            if (turtle_y > maxTurtleY) {
                turtle_y = maxTurtleY;
                turtle_speed = turtle_fly;
                isSpark = true;
            }

            //junk code for which resource to use
            if(isMoving) {
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_up_350_235);
                turtle_frameCount = turtle_upFrameCount;
            }else{
                bitmap_turtle = BitmapFactory.decodeResource(this.getResources(), R.drawable.turtle_swim_350_235);
                turtle_frameCount = turtle_idleFrameCount;
            }

            //BACKGROUND
            //im really iffy of this
            background_x -= background_speed;
            reef_x -= reef_speed;
            if(Math.abs(background_x) >= (water_width - canvasWidth) )  // i want my background to be double layered as a png
                background_x = canvasWidth;
            if(Math.abs(reef_x) >= (reef_width - canvasWidth) )  // i want my background to be double layered as a png
                reef_spawn = canvasWidth;

            //worm logic
            if(collisionChecker(worm_x, worm_y)){
                score += 10;
                worm_x -= 300;
            }
            worm_x -= worm_speed;
            if(worm_x < 0){
                worm_x = canvasWidth + worm_frameWidth;
                worm_y = (int) Math.floor(Math.random() * (maxTurtleY - minTurtleY) + minTurtleY);
            }


            ///*
            // STRAW LOGIC
            if(collisionChecker(straw_x, straw_y)){
                straw_x -= 300;
                lifeCounterOfTurtle--;
                if(lifeCounterOfTurtle == 0){
                    Toast.makeText(MainActivity.this, "GameOver", Toast.LENGTH_SHORT).show();
                    Log.e("FIN","game over");
                }
            }
            //*/
           // /*

            straw_x -= straw_speed;
            if(straw_x < 0){
                straw_x = canvasWidth + straw_frameWidth;
                straw_y = (int) Math.floor(Math.random() * ( maxTurtleY - minTurtleY) + minTurtleY);
            }
            //*/

        }

        public void draw() {
            //valid or we crash
            if(ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);

                //canvas.drawBitmap(bitmap_background, background_x, 0, null);

                bitmap_sky = Bitmap.createScaledBitmap(
                        bitmap_sky,
                        sky_frameWidth * sky_frameCount,
                        minTurtleY,
                        false
                );
                sky_whereToDraw.set(turtle_x, 0, turtle_x + sky_frameWidth, sky_frameHeight);

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
                if(isSpark){
                    bitmap_sparkEffect = Bitmap.createScaledBitmap(
                            bitmap_sparkEffect,
                            spark_frameWidth * spark_frameCount,
                            spark_frameHeight,
                            false
                    );
                    spark_whereToDraw.set(
                            turtle_x,
                            maxTurtleY,
                            turtle_x + spark_frameWidth,
                            maxTurtleY + spark_frameHeight
                    );
                }
                if(isSplash){
                    bitmap_splashEffect = Bitmap.createScaledBitmap(
                            bitmap_splashEffect,
                            splash_frameWidth * splash_frameCount,
                            splash_frameHeight,
                            false
                    );

                    splash_whereToDraw.set(
                            turtle_x,
                            minTurtleY,
                            turtle_x + splash_frameWidth,
                            minTurtleY + splash_frameHeight
                    );
                }

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

                ///*
                //STRAW
                bitmap_straw = Bitmap.createScaledBitmap(
                        bitmap_straw,
                        straw_frameWidth * straw_frameCount,
                        straw_frameHeight,
                        false
                );
                straw_whereToDraw.set(
                        straw_x,
                        straw_y,
                        straw_x + straw_frameWidth,
                        straw_y + straw_frameHeight
                );


                //*/
                //not sure why this is here, in this exact spot
                getCurrentFrame();
                canvas.drawBitmap(
                        bitmap_sky,
                        sky_frameToDraw,
                        sky_whereToDraw,
                        paint
                );

                canvas.drawBitmap(bitmap_water, background_x, minTurtleY, null);
                canvas.drawBitmap(bitmap_reef, background_x, 0, null);
                //canvas.drawBitmap(bitmap_reef, reef_x, 0, null);

                canvas.drawBitmap(
                        bitmap_turtle,
                        turtle_frameToDraw,
                        turtle_whereToDraw,
                        paint
                );

                if(isSpark){
                    canvas.drawBitmap(
                            bitmap_sparkEffect,
                            spark_frameToDraw,
                            spark_whereToDraw,
                            paint
                    );
                }

                if(isSplash){
                    canvas.drawBitmap(
                            bitmap_splashEffect,
                            splash_frameToDraw,
                            splash_whereToDraw,
                            paint);
                }


                canvas.drawBitmap(
                        bitmap_worm,
                        worm_frameToDraw,
                        worm_whereToDraw,
                        paint
                );

                ///*
                canvas.drawBitmap(
                        bitmap_straw,
                        straw_frameToDraw,
                        straw_whereToDraw,
                        paint
                );
                //*/

                canvas.drawText("Score : "+ score, 20, 60, scorePaint);

                //HEARTS
                for(int i = 0; i < 3; i++){
                    int x = (canvasWidth - 100 - (100 * i));
                    int y = 10;

                    if( i < lifeCounterOfTurtle)
                        canvas.drawBitmap(life[0], x, y, null);
                    else
                        canvas.drawBitmap(life[1], x, y, null);
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void getCurrentFrame() {
            long time = System.currentTimeMillis();
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {  // this logic orig came after is moving logic
                turtle_currentFrame++;
                worm_currentFrame++;
                straw_currentFrame++;
                sky_currentFrame++;

                if(sky_currentFrame >= sky_frameCount){
                    sky_currentFrame = 0;
                }

                if(isMoving) {  // animate for if is moving
                    lastFrameChangeTime = time;  // this maybe should be in the main function and not ever inner peice
                    if (turtle_currentFrame >= turtle_upFrameCount){
                        turtle_currentFrame = 1;
                        isMoving = false;
                    }
                }
                else{
                    lastFrameChangeTime = time;
                    if(turtle_currentFrame >= turtle_idleFrameCount)
                        turtle_currentFrame = 0;
                }
                if(isSpark) {
                    spark_currentFrame++;
                    if(spark_currentFrame >= spark_frameCount){
                        isSpark = false;
                        spark_currentFrame = 0;
                    }
                }
                if(isSplash) {
                    splash_currentFrame++;
                    if(splash_currentFrame >= splash_frameCount){
                        isSplash = false;
                        splash_currentFrame = 0;  // omg a two day bug all becasue i typed frame count instead of current frame
                    }
                }

                if(worm_currentFrame >= worm_frameCount)
                    worm_currentFrame = 0;

                if(straw_currentFrame >= straw_frameCount)
                    straw_currentFrame = 0;

            }

            sky_frameToDraw.left = sky_currentFrame * sky_frameWidth;
            sky_frameToDraw.right = sky_frameToDraw.left + sky_frameWidth;

            turtle_frameToDraw.left = turtle_currentFrame * turtle_frameWidth;
            turtle_frameToDraw.right = turtle_frameToDraw.left + turtle_frameWidth;

            if(isSpark){
                spark_frameToDraw.left = spark_currentFrame * spark_frameWidth;
                spark_frameToDraw.right = spark_frameToDraw.left + spark_frameWidth;
            }
            if(isSplash){
                splash_frameToDraw.left = splash_currentFrame * splash_frameWidth;
                splash_frameToDraw.right = splash_frameToDraw.left + splash_frameWidth;
            }

            worm_frameToDraw.left = worm_currentFrame * worm_frameWidth;
            worm_frameToDraw.right = worm_frameToDraw.left + worm_frameWidth;


            straw_frameToDraw.left = straw_currentFrame * straw_frameWidth;
            straw_frameToDraw.right = straw_frameToDraw.left + straw_frameWidth;
        }

        public boolean collisionChecker(int x, int y){
            if(turtle_x < x && x < (turtle_x + turtle_frameWidth) &&
                    turtle_y < y && y < (turtle_y + turtle_frameWidth)) {
                Log.e("Hit", "ture");
                return true;
            }
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
