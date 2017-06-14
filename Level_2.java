package com.example.user.catchthemonster;
import android.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Level_2 extends AppCompatActivity {
    static int width1;
    static int pit_square1;
    static ArrayList<Integer> obstacle = new ArrayList<>();
    static int character_square1;
    static int[] forobst1 = {};
    static boolean clicked = false;
    static ArrayList<Integer> user_obstacle = new ArrayList<>();
    static int obstacle_amount1;
    static boolean started1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_2);
        final boolean started = false;
        final ArrayList<Integer> way = new ArrayList<>();
        final GameScreen_2 gameScreen2 = (GameScreen_2) findViewById(R.id.game_screen_2);
        gameScreen2.counter = 0;
        gameScreen2.loose = false;
        final int moster_square = 6;
        final int pit_square = 23;
        final int character_square = 19;
        character_square1 = character_square;
        pit_square1 = pit_square;
        obstacle = new ArrayList<>();
        user_obstacle = new ArrayList<>();
        started1 = started;

        gameScreen2.addcoord(new Pair1(Square.Square_x(moster_square), Square.Square_y(moster_square)));
        final Level_2 this_ = this;
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
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started1 = true;
                int field_side = 5;
                int[] forobst = forobst1;
                for (int i = 0; i < forobst.length; i++) {
                    obstacle.add(forobst[i]);
                }
                bfs.bfs(field_side, moster_square, character_square, way, pit_square, obstacle);
                Log.d("bfs", String.valueOf(way.size()));
                int p1 = way.get(way.size() - 1);
                for (int i = way.size() - 2; i >= 0; i--) {
                    Log.d("bfs", String.valueOf(way.get(i)));
                    int p2 = way.get(i);
                    int curent_x = gameScreen2.coord.get(gameScreen2.coord.size() - 1).a;
                    int curent_y = gameScreen2.coord.get(gameScreen2.coord.size() - 1).b;

                    int delta = MainMenu.width1 / 5;

                    if ((p1 / 5 == p2 / 5) && (p1 < p2)) {
                        gameScreen2.addcoord(new Pair1(curent_x + delta, curent_y));
                    }
                    if ((p1 / 5 == p2 / 5) && (p1 > p2)) {
                        gameScreen2.addcoord(new Pair1(curent_x - delta, curent_y));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 < p2)) {
                        gameScreen2.addcoord(new Pair1(curent_x, curent_y + delta));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 > p2)) {
                        gameScreen2.addcoord(new Pair1(curent_x, curent_y - delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen2.addcoord(new Pair1(curent_x + delta, curent_y + delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen2.addcoord(new Pair1(curent_x - delta, curent_y + delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen2.addcoord(new Pair1(curent_x - delta, curent_y - delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen2.addcoord(new Pair1(curent_x + delta, curent_y - delta));

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

        final int[] obstacles = {3};
        final String[] o = {String.valueOf(obstacles[0])};

        final Button btn1 = (Button) findViewById(R.id.btn);
        btn1.setBackground(obstacle_start_1);
        btn1.setTextColor(Color.WHITE);
        btn1.setTextSize(MainMenu.width1/40);
        btn1.setText(o[0]);



        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked){
                    btn1.setBackground(obstacle_1);
                    clicked = true;
                    obstacles[0]--;
                    o[0] = String.valueOf(obstacles[0]);
                    btn1.setText(o[0]);
                }
            }
        };
        btn1.setOnClickListener(listener3);

        final float[] x = new float[1];
        final float[] y = new float[1];
        final int[] obstacle_amount = {obstacles[0]};
        obstacle_amount1 = obstacle_amount[0];
        final boolean[] out_of_obst = {false};
        final View.OnTouchListener list = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (! started1) {
                    x[0] = event.getX();
                    y[0] = event.getY();
                    if (clicked & !out_of_obst[0]) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (NumberByCoord.NumberByCoord(x[0], y[0]) >= -1 && NumberByCoord.NumberByCoord(x[0], y[0]) < 25) {
                                    btn1.setBackground(obstacle_1);
                                    obstacle.add(NumberByCoord.NumberByCoord(x[0], y[0]));
                                    user_obstacle.add(NumberByCoord.NumberByCoord(x[0], y[0]));
                                    clicked = false;
                                    obstacle_amount[0]--;
                                    gameScreen2.draw2();
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
        gameScreen2.setOnTouchListener(list);
    }
}
