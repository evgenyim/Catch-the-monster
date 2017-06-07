package com.example.user.catchthemonster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class Level_1 extends AppCompatActivity {
    final  int right = 1;
    final int left = 2;
    final int down = 3;
    final int up = 4;
    final int right_down = 5;
    final int left_down = 6;
    final int right_up = 7;
    final int left_up = 8;
    public int direction(int a, int b){
        if ((a/5 == b/5) && (a < b))
        {
            return right;
        }
        if ((a/5 == b/5) && (a > b))
        {
            return left;
        }
        if ((a%5 == b%5) && (a < b))
        {
            return down;
        }
        if ((a%5 == b%5) && (a > b))
        {
            return up;
        }
        if ((a/5 < b/5) && (a%5 < b % 5))
        {
            return right_down;
        }
        if ((a/5 < b/5) && (a%5 > b % 5))
        {
            return left_down;
        }
        if ((a/5 > b/5) && (a%5 > b % 5))
        {
            return left_up;
        }
        if ((a/5 > b/5) && (a%5 < b % 5))
        {
            return right_up;
        }
        return 0;
    }
    public void move(int[] b,ImageView monster,int k) {
        int k1 = k;
        switch (b[k]){
            case 1:
                monster.animate().xBy(155);
                break;
            case 2:
                monster.animate().xBy(-155);
                break;
            case 3:
                monster.animate().yBy(155);
                break;
            case 4:
                monster.animate().yBy(-155);
                break;
            case 5:
                monster.animate().xBy(155).yBy(155);
                break;
            case 6:
                monster.animate().xBy(-155).yBy(155);
                break;
            case 7:
                monster.animate().xBy(-155).yBy(-155);
                break;
            case 8:
                monster.animate().xBy(155).yBy(-155);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_1);
        final ArrayList<Integer> way = new ArrayList<>();
        final ImageView monster =(ImageView) findViewById(R.id.imageView);
        final Button btn = (Button) findViewById(R.id.button);
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                bfs.bfs(5,0,5,way);
                int x = 0;
                int b [] = new int[way.size()+1];
                int k = 0;
                for (int i = way.size() - 2; i >= 0; i --){
                    int a = direction(x,way.get(i));
                    switch (a){
                        case 1:
                            monster.animate().xBy(155);
                            break;
                        case 2:
                            monster.animate().xBy(-155);
                            break;
                        case 3:
                            monster.animate().yBy(155);
                            break;
                        case 4:
                            monster.animate().yBy(-155);
                            break;
                        case 5:
                            monster.animate().xBy(155).yBy(155);
                            break;
                        case 6:
                            monster.animate().xBy(-155).yBy(155);
                            break;
                        case 7:
                            monster.animate().xBy(-155).yBy(-155);
                            break;
                        case 8:
                            monster.animate().xBy(155).yBy(-155);
                            break;
                    }
                    x = way.get(i);
                    Log.d("lvl1", String.valueOf(way.get(i)));
                }


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
