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

public class Help extends AppCompatActivity {

    private GameScreen gameScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_1);
        final ArrayList<Integer> way = new ArrayList<>();
        final Button btn = (Button) findViewById(R.id.button);
        GameScreen gameScreen = (GameScreen) findViewById(R.id.game_screen);
        gameScreen.invalidate();

        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                bfs.bfs(5,0,7,way);

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
