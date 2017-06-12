package com.example.user.catchthemonster;
import android.*;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Level_1 extends AppCompatActivity {
    static int width1;
    private GameScreen gameScreen;
    static int pit_square1;
    static ArrayList <Integer> obstacle = new ArrayList<>();
    static int character_square1;
    static int [] forobst1 ={6,11,16,22,18,13,8,22,23};
    static boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_1);

        final ArrayList<Integer> way = new ArrayList<>();
        final GameScreen gameScreen = (GameScreen) findViewById(R.id.game_screen);
        gameScreen.counter = 0;
        final int moster_square = 0;
        final int pit_square = 12;
        final int character_square = 24;
        character_square1 = character_square;
        pit_square1 = pit_square;

        gameScreen.addcoord(new Pair1(Square.Square_x(moster_square),Square.Square_y(moster_square)));
        final Button btn = (Button) findViewById(R.id.button);
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int field_side = 5;
                int[] forobst = forobst1;
                for (int i = 0; i < forobst.length; i ++){
                    obstacle.add(forobst[i]);
                }
                bfs.bfs(field_side,moster_square,character_square,way,pit_square, obstacle);
                Log.d("bfs", String.valueOf(way.size()));
                int p1 = way.get(way.size()-1);
                for (int i = way.size() - 2; i >= 0; i --){
                    Log.d("bfs", String.valueOf(way.get(i)));
                    int p2 = way.get(i);
                    int curent_x = gameScreen.coord.get(gameScreen.coord.size()-1).a;
                    int curent_y = gameScreen.coord.get(gameScreen.coord.size()-1).b;

                    int delta = MainMenu.width1/5;

                    if ((p1 / 5 == p2 / 5) && (p1 < p2)) {
                        gameScreen.addcoord( new Pair1(curent_x+delta,curent_y));
                    }
                    if ((p1 / 5 == p2 / 5) && (p1 > p2)) {
                        gameScreen.addcoord( new Pair1(curent_x-delta,curent_y));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 < p2)) {
                        gameScreen.addcoord( new Pair1(curent_x,curent_y+delta));

                    }
                    if ((p1 % 5 == p2 % 5) && (p1 > p2)) {
                        gameScreen.addcoord( new Pair1(curent_x,curent_y-delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen.addcoord( new Pair1(curent_x+delta,curent_y+delta));

                    }
                    if ((p1 / 5 < p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen.addcoord( new Pair1(curent_x-delta,curent_y+delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 > p2 % 5)) {
                        gameScreen.addcoord( new Pair1(curent_x-delta,curent_y-delta));

                    }
                    if ((p1 / 5 > p2 / 5) && (p1 % 5 < p2 % 5)) {
                        gameScreen.addcoord( new Pair1(curent_x+delta,curent_y-delta));

                    }
                    p1 = p2;
                }
                btn.setOnClickListener(null);
                btn.setBackgroundColor(0);
                btn.setText("");
            }

        };
        btn.setOnClickListener(listener1);

        final Button btn1 =  (Button) findViewById(R.id.btn);
        btn1.setBackgroundResource(R.drawable.start_button_background);
        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackgroundResource(R.drawable.cage);
                flag = true;

            }
        };
        btn1.setOnClickListener(listener3);
    }




    float x;
    float y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        if (flag) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (NumderByCoord.NumderByCoord(x,y)>=-1 &&NumderByCoord.NumderByCoord(x,y)<25) {
                        obstacle.add(NumderByCoord.NumderByCoord(x, y));
                        flag = false;
                    }
            }
        }

        return super.onTouchEvent(event);
    }
}
