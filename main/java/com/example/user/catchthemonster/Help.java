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
    private GameScreen gameScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

    }
}