package com.example.riyu.smashinggame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import android.os.Handler;

/**
 * Created by Riyu on 2/13/15.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder  holder;
    private GameLoopThread gameLoopThread;
    private int dishes = 100;
    private int score = 0;

    public GameView(Context context) {
        super(context);
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
        //canvas.drawColor(Color.WHITE); // Black background
        /*canAdd = false;
        for (RectFP r : circles) {
            canvas.drawOval(r, r.getPaint());
        }
        canAdd = true;*/
    }

}
