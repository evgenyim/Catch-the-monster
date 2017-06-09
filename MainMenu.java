package com.example.user.catchthemonster;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    static int width1;
    static int height1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        final int height = size.y;
        width1 = width;
        height1 = height;

        final Button btn3 = (Button) findViewById(R.id.btn3);
        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        btn3.setOnClickListener(listener3);
        final Button btn1 =(Button) findViewById(R.id.btn1);
        final MainMenu this_ = this;
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, levels.class);
                startActivity(intent);
            }
        };
        btn1.setOnClickListener(listener1);

        final Button btn2 =(Button) findViewById(R.id.btn2);
        final View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, Help.class);
                startActivity(intent);
            }
        };
        btn2.setOnClickListener(listener2);
    }

}
