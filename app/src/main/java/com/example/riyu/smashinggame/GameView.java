package com.example.riyu.smashinggame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Riyu on 2/13/15.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder  holder;
    private GameLoopThread gameLoopThread;
    private int dishes = 20;
    private int score;
    private int timeout = 0;
    private ArrayList<plates> plate_stack = new ArrayList<plates>();
    private ArrayList<plates> plates_out = new ArrayList<plates>();
    long init = System.currentTimeMillis();

    public GameView(Context context) {
        super(context);
        for (int i = 0; i < dishes; i++){
            Random rand = new Random();
            int x = rand.nextInt((600 - 0) + 1) + 0;
            int velocity = rand.nextInt((80 - 30) + 1) + 0;
            plates plate = new plates(x, velocity);
            plate_stack.add(plate);
        }
        //plate = new plates();
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
        dishes = plate_stack.size() + plates_out.size();
        if (timeout > 0){
            timeout--;
        }
        if (dishes <= 0){
            gameLoopThread.setRunning(false);
            build_dialog();
        }
        if (plates_out.size() < 5  && (timeout == 0 ) &&(plate_stack.size() > 0)){
            plates_out.add(plate_stack.get(0));
            plate_stack.remove(0);
            timeout = 10;
        }
        String scoring = String.format("Score: %d", score);
        String plates_left = String.format("Plates Left: %d", dishes);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(scoring, 600, 25, paint);
        canvas.drawText(plates_left, 280, 25, paint);
        for(int i = 0; i < plates_out.size(); i++){
            plates plate = plates_out.get(i);
            if (plate.getStart_plate() == true) {
                paint.setColor(Color.BLACK);
                if (plate.shatter(canvas.getHeight())) {
                    plate.setPlate_state(false);
                }
                Bitmap image;
                if (plate.getPlate_state()) {
                    image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dish);
                    //canvas.drawBitmap(image, plate.getX_position(), plate.getY_position(), paint);
                    plate.drop_plate(plate.getVelocity());
                } else {
                    image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.brokendish);
                    if ((plate.get_shatter_count() > 5)) {
                        paint.setColor(Color.TRANSPARENT);
                        plates_out.remove(i);
                        timeout = 5;
                    }
                    plate.setShatter_count(1);
                }
                RectF rectF = new RectF(plate.getX_position(), plate.getY_position(), plate.getX_position() + 200, plate.getY_position() + 100);
                canvas.drawBitmap(image, null, rectF, paint);
            }
        }
        //canvas.drawColor(Color.WHITE); // Black background
        /*canAdd = false;
        for (RectFP r : circles) {
            canvas.drawOval(r, r.getPaint());
        }
        canAdd = true;*/
    }

    public void build_dialog(){

        DialogFragment dialog = new YesNoDialog();
        Bundle args = new Bundle();
        long millis = System.currentTimeMillis() - init;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String passed_time = String.format("%d:%02d", minutes, seconds);
        args.putString("title", "Game Over");
        String scoring = String.format("with a score of %d", score);
        String message = ("You finished " + scoring + " and a time of " + passed_time + ".");
        args.putString("message", message);
        dialog.setArguments(args);
        Fragment f = new Fragment();
        dialog.setTargetFragment(f, 0);
        dialog.show(f.getFragmentManager(), "tag");
        gameLoopThread.setRunning(false);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for(int i = 0; i < plates_out.size(); i++) {
            plates plate = plates_out.get(i);
            if (((x >= plate.getX_position()) && (x <= plate.getX_position() + 200)) && ((y >= plate.getY_position()) && y <= plate.getY_position() + 100) && plate.getPlate_state()) {

                Random random = new Random();
                plate.setPlate_state(false);
                score++;
                //RectFP rectFP = new RectFP(x - 20, y - 20, x + 20, y + 20);
                //rectFP.setPaint(paints.get(random.nextInt(4)));
                //circles.add(rectFP);
            }
        }

        return super.onTouchEvent(event);
    }

    public class plates {
        private boolean plate_state;
        private boolean start_plate;
        private float x_position;
        private float y_position;
        private float velocity;
        private int break_count = 0;
        public plates(){
            x_position = 50;
            y_position = -200;
            velocity = 10;
            plate_state = true;
            start_plate = true;

        }
        public plates(float initial_x, float init_velocity){
            x_position = initial_x;
            y_position = -200;
            velocity = init_velocity;
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

        public float getVelocity(){
            return velocity;
        }

        public boolean shatter(float max){
            return ((max - (y_position + 100)) <= 10);
        }

    }

    public class YesNoDialog extends DialogFragment
    {
        public YesNoDialog()
        {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            Bundle args = getArguments();
            String title = args.getString("title", "");
            String message = args.getString("message", "");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, null);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), getActivity().RESULT_CANCELED, null);
                        }
                    })
                    .create();
        }
    }

}
