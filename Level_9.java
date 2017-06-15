package com.example.user.catchthemonster;
import android.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Level_9 extends AppCompatActivity { //
    static int width1;
    static int pit_square1;
    static ArrayList<Integer> obstacle = new ArrayList<>();
    static int character_square1;
    static int[] forobst1 = {7,16};
    static boolean clicked = false;
    static ArrayList<Integer> user_obstacle = new ArrayList<>();
    static int obstacle_amount1;
    static boolean started1;
    static Button next;
    static RelativeLayout.LayoutParams lp_next;
    static int space;
    static View.OnClickListener listen;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_9); //
        final Editor ed = MainMenu.sPref.edit();
        if (! MainMenu.sPref.contains("Level_9")) {//
            ed.putBoolean("Level_9", false);//
            ed.commit();
        }



        final boolean started = false;
        final ArrayList<Integer> way = new ArrayList<>();
        final GameScreen_9 gameScreen = (GameScreen_9) findViewById(R.id.game_screen_9); //////
        gameScreen.counter = 0;
        gameScreen.loose = false;
        final int moster_square = 5;
        final int pit_square = 13;
        final int character_square = 24;

        character_square1 = character_square;
        pit_square1 = pit_square;
        obstacle = new ArrayList<>();
        user_obstacle = new ArrayList<>();
        started1 = started;

        gameScreen.addcoord(new Pair1(Square.Square_x(moster_square), Square.Square_y(moster_square)));

        final Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setBackgroundResource(R.drawable.start_button_background);
        final View.OnClickListener listener5 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        };
        btn2.setOnClickListener(listener5);

        final Button btn = (Button) findViewById(R.id.button);
        int space_y = MainMenu.height1 - 6 * MainMenu.width1/5;
        space = space_y;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        lp.height = MainMenu.width1/5;
        lp.width = MainMenu.width1/5;
        btn.setLayoutParams(lp);
        lp_next = lp;
        btn.setY(MainMenu.width1*6/5 + space_y/4);
        btn.setX((MainMenu.width1/5)*3 + MainMenu.width1/10);
        btn.setText("START");
        btn.setBackgroundResource(R.drawable.start_button_background);

        final Button next_lvl = (Button) findViewById(R.id.btn_next);
        next_lvl.setBackgroundColor(0);
        next = next_lvl;


        btn2.setLayoutParams(lp);
        btn2.setY(MainMenu.width1*6/5 + space_y/4);
        btn2.setX((MainMenu.width1/5)*0 + MainMenu.width1/10);
        int[] forobst = forobst1;
        for (int i = 0; i < forobst.length; i++) {
            obstacle.add(forobst[i]);
        }



        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started1 = true;
                int field_side = 5;


                bfs.bfs(field_side, moster_square, character_square, way, pit_square, obstacle);
                Log.d("bfs", String.valueOf(way.size()));
                int p1 = way.get(way.size() - 1);
                for (int i = way.size() - 2; i >= 0; i--) {
                    Log.d("bfs", String.valueOf(way.get(i)));
                    int p2 = way.get(i);
                    int curent_x = gameScreen.coord.get(gameScreen.coord.size() - 1).a;
                    int curent_y = gameScreen.coord.get(gameScreen.coord.size() - 1).b;

                    int delta = MainMenu.width1 / 5;

                    if ((p1 / 5 == p2 / 5) && (p1 < p2)) {
                        gameScreen.addcoord(new Pair1(curent_x + delta, curent_y));
                    }
                    if ((p1 / 5 == p2 / 5) && (p1 > p2)) {
                        gameScreen.addcoord(new Pair1(curent_x - delta, curent_y));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 < p2)) {
                        gameScreen.addcoord(new Pair1(curent_x, curent_y + delta));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 > p2)) {
                        gameScreen.addcoord(new Pair1(curent_x, curent_y - delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen.addcoord(new Pair1(curent_x + delta, curent_y + delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen.addcoord(new Pair1(curent_x - delta, curent_y + delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen.addcoord(new Pair1(curent_x - delta, curent_y - delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen.addcoord(new Pair1(curent_x + delta, curent_y - delta));

                    }
                    p1 = p2;
                }
                btn.setOnClickListener(null);
                btn.setBackgroundColor(0);
                btn.setText("");
            }

        };
        btn.setOnClickListener(listener1);

        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.obstacle_start);

        final Bitmap obstacle_start = Bitmap.createScaledBitmap(bitmap,MainMenu.width1/5, MainMenu.width1/5, false);
        final Drawable obstacle_start_1 = new BitmapDrawable(getResources(), obstacle_start);

        final Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.obstacle);

        final Bitmap obstacle1 = Bitmap.createScaledBitmap(bitmap2,MainMenu.width1/5, MainMenu.width1/5, false);
        final Drawable obstacle_1 = new BitmapDrawable(getResources(), obstacle1);


        final Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),R.drawable.no_obstacle);
        final Bitmap no_obstacle = Bitmap.createScaledBitmap(bitmap3,MainMenu.width1/5, MainMenu.width1/5, false);
        final Drawable no_obstacle_1 = new BitmapDrawable(getResources(), no_obstacle);

        final int[] obstacles = {2};
        final String[] o = {String.valueOf(obstacles[0])};

        final Button btn1 = (Button) findViewById(R.id.btn);

        btn1.setLayoutParams(lp);
        btn1.setY(MainMenu.width1*6/5 + space_y/4);
        btn1.setX((MainMenu.width1/5)*2);
        btn1.setTextColor(Color.WHITE);
        btn1.setTextSize(MainMenu.width1/40);
        btn1.setText(o[0]);
        btn1.setBackground(obstacle_start_1);
        btn1.setTextColor(Color.WHITE);
        btn1.setTextSize(MainMenu.width1/40);
        btn1.setText(o[0]);



        final ImageView level = (ImageView) findViewById(R.id.imageView);

        final Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(),R.drawable.level8); //
        final Bitmap level_1 = Bitmap.createScaledBitmap(bitmap4,MainMenu.width1, MainMenu.width1/5, false);
        final Drawable level1 = new BitmapDrawable(getResources(), level_1);
        level.setBackground(level1);



        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn1.setBackground(obstacle_1);
                if(!clicked) {
                    obstacles[0]--;
                    o[0] = String.valueOf(obstacles[0]);
                    btn1.setText(o[0]);
                }
                clicked = true;


            }
        };
        btn1.setOnClickListener(listener3);


        Log.d("hui", String.valueOf(started1));
        final float[] x = new float[1];
        final float[] y = new float[1];
        final int[] obstacle_amount = {obstacles[0]};
        obstacle_amount1 = obstacle_amount[0];
        final boolean[] out_of_obst = {false};
        final View.OnTouchListener list = new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (! started1) {
                    x[0] = event.getX();
                    y[0] = event.getY();
                    if (clicked & !out_of_obst[0]) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (NumberByCoord.NumberByCoord(x[0], y[0]) >= -1 && NumberByCoord.NumberByCoord(x[0], y[0]) < 25  && NumberByCoord.NumberByCoord(x[0], y[0])!= pit_square && NumberByCoord.NumberByCoord(x[0], y[0])!= character_square && NumberByCoord.NumberByCoord(x[0], y[0])!=moster_square && !obstacle.contains(NumberByCoord.NumberByCoord(x[0], y[0]))) {
                                    btn1.setBackground(obstacle_1);
                                    obstacle.add(NumberByCoord.NumberByCoord(x[0], y[0]));
                                    user_obstacle.add(NumberByCoord.NumberByCoord(x[0], y[0]));
                                    clicked = false;
                                    obstacle_amount[0]--;
                                    gameScreen.draw();
                                    btn1.setBackground(obstacle_start_1);
                                    if (obstacle_amount[0] <= 0) {
                                        btn1.setBackground(no_obstacle_1);
                                        btn1.setOnClickListener(null);
                                        out_of_obst[0] = true;
                                    }

                                }
                        }
                    }
                }
                return false;
            }
        };
        gameScreen.setOnTouchListener(list);
        Level_9 this_ = this; //
        this_ = this;
        final Level_9 finalThis_ = this_; //
        final View.OnClickListener list1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(finalThis_,levels.class);
                startActivity(intent1);
                Intent intent = new Intent(finalThis_,Level_10.class); //
                startActivity(intent);
            }
        };
        listen = list1;
    }
    @Override
    public void onBackPressed() {
        Level_9 this_ = this;
        Intent intent = new Intent(this_,levels.class);
        startActivity(intent);
    }
}
