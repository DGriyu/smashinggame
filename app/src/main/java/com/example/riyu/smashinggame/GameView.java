package com.example.riyu.smashinggame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Riyu on 2/13/15.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder  holder;
    private GameLoopThread gameLoopThread;
    private int dishes = 100;
    private int score;
    private plates plate;
    long init = System.currentTimeMillis();

    public GameView(Context context) {
        super(context);
        plate = new plates();
        score = 0;
        gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("WrongCall")
            @Override
            public void surfaceCreated (SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
            }
        });

        //makePaints(); // Adds our paints to the arraylist.
    }

    @Override
    public void onDraw(Canvas canvas) {
        String scoring = String.format("Score: %d", score);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(scoring, 600, 25, paint);
        if (plate.getStart_plate() == true){
            paint.setColor(Color.BLACK);
            if (plate.shatter(canvas.getHeight())){
                plate.setPlate_state(false);
            }
            Bitmap image;
            if (plate.getPlate_state()){
                image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.dish);
                //canvas.drawBitmap(image, plate.getX_position(), plate.getY_position(), paint);
                plate.drop_plate(10);
            }
            else{
                image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.brokendish);
                if (!(plate.get_shatter_count() < 100)){
                    paint.setColor(Color.TRANSPARENT);
                }
                plate.setShatter_count(1);
            }
            RectF rectF = new RectF(plate.getX_position(), plate.getY_position(), plate.getX_position() + 200, plate.getY_position() + 100);
            canvas.drawBitmap(image, null, rectF, paint);
        }
        //canvas.drawColor(Color.WHITE); // Black background
        /*canAdd = false;
        for (RectFP r : circles) {
            canvas.drawOval(r, r.getPaint());
        }
        canAdd = true;*/
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (((x >= plate.getX_position()) && (x <= plate.getX_position() + 200)) && ((y >= plate.getY_position()) && y <= plate.getY_position() + 100)  && plate.getPlate_state()) {

            Random random = new Random();
            plate.setPlate_state(false);
            score++;
            //RectFP rectFP = new RectFP(x - 20, y - 20, x + 20, y + 20);
            //rectFP.setPaint(paints.get(random.nextInt(4)));
            //circles.add(rectFP);
        }

        return super.onTouchEvent(event);
    }

    public class plates {
        private boolean plate_state;
        private boolean start_plate;
        private float x_position;
        private float y_position;
        private int break_count = 0;
        public plates(){
            x_position = 50;
            y_position = -200;
            plate_state = true;
            start_plate = true;

        }
        public plates(int initial_x){
            x_position = initial_x;
            y_position = -200;
            plate_state = true;
            start_plate = true;
        }

        public void changed_plate(){
            plate_state = false;
        }

        public void start_plate(){
            start_plate = true;
        }

        public void drop_plate(float drop){
            y_position += drop;
        }
        public float getX_position(){
            return x_position;
        }
        public float getY_position(){
            return y_position;
        }
        public boolean getPlate_state(){
            return plate_state;
        }
        public boolean getStart_plate(){
            return start_plate;
        }
        public void setPlate_state(boolean set){
            plate_state = set;
        }
        public void setShatter_count(int x){
            break_count += x;
        }

        public int get_shatter_count(){
            return break_count;
        }

        public boolean shatter(float max){
            return ((max - (y_position + 100)) <= 10);
        }

    }

}
