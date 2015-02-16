package com.example.riyu.smashinggame;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Riyu on 2/13/15.
 */
public class GameLoopThread extends Thread{
    private GameView view;
    private boolean running = false;
    private static final long FPS = 60;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        long tPS = 1000 / FPS; //ticksPerSecond is 1000ms (1sec) divided by the FramesPerSecond.
        long startTime;
        long sleepTime;
        long millis;
        long init = System.currentTimeMillis();

        while(running) {
            Canvas c = new Canvas();
            millis = System.currentTimeMillis() - init;
            int seconds = (int) (millis / 1000);
            startTime = System.currentTimeMillis();
            int minutes = seconds / 60;
            seconds = seconds % 60;

            String passed_time = String.format("%d:%02d", minutes, seconds);

            try {
                c = view.getHolder().lockCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                c.drawPaint(paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(20);
                c.drawText(passed_time, 10, 25, paint);
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                if(c != null){
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }

            sleepTime = tPS - (System.currentTimeMillis() - startTime);
            try {
                if(sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //End run.
    } //End method.

    public void setRunning(boolean b) {
        running = b;
    }

    public boolean getRunning() {
        return running;
    }

}

