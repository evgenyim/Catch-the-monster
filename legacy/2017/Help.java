
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

public class Help extends AppCompatActivity {
    static int width1;
    private GameScreen gameScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        final int height = size.y;
        width1 = width;
        Log.d("2", String.valueOf(height));
        Log.d("3", String.valueOf(width));


        final ArrayList<Integer> way = new ArrayList<>();
        final GameScreen gameScreen = (GameScreen) findViewById(R.id.game_screen);
        gameScreen.counter = 0;
        gameScreen.addcoord(new Pair1(0,245));
        final Button btn = (Button) findViewById(R.id.button);
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                bfs.bfs(5,0,19,way);
                int p1 = 0;
                for (int i = way.size() - 2; i >= 0; i --){
                    int p2 = way.get(i);
                    Log.d("bfs", String.valueOf(way.get(i)));
                    int curent_x = gameScreen.coord.get(gameScreen.coord.size()-1).a;
                    int curent_y = gameScreen.coord.get(gameScreen.coord.size()-1).b;

                    Log.d("1", String.valueOf(width));

                    int delta = width/5;



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
            }
        };
        btn.setOnClickListener(listener1);
    }

    float x;
    float y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("help", x+" "+y);
        }
        return super.onTouchEvent(event);
    }
}

