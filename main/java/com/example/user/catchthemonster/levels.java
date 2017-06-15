package com.example.user.catchthemonster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class levels extends AppCompatActivity {


    static Button btn1;
    static Button btn2;
    static Button btn3;
    static Button btn4;
    static Button btn5;
    static Button btn6;
    static Button btn7;
    static Button btn8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);


        int space_y = MainMenu.height1 - 6 * MainMenu.width1/5;
        Log.d("huy", "jopa");
        final Button button1 = (Button) findViewById(R.id.button1);
        btn1 = button1;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button1.getLayoutParams();
        lp.height = MainMenu.width1/6;
        lp.width = MainMenu.width1/6;
        button1.setLayoutParams(lp);
        button1.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button1.setX(MainMenu.width1/6/6);
        if (MainMenu.sPref.contains("Level_1")) {
            if (MainMenu.sPref.getBoolean("Level_1", true)) {
                button1.setBackgroundResource(R.drawable.start_button_background_star);
            } else button1.setBackgroundResource(R.drawable.start_button_background);
        } else button1.setBackgroundResource(R.drawable.start_button_background);
        button1.setText("1");

        final Button button2 = (Button) findViewById(R.id.button2);
        btn2 = button2;
        button2.setLayoutParams(lp);
        button2.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button2.setX(MainMenu.width1/6 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_1")) {
            if (MainMenu.sPref.getBoolean("Level_1", true)){
                button2.setText("2");
                if (MainMenu.sPref.contains("Level_2")) {
                    button2.setText("2");
                    if (MainMenu.sPref.getBoolean("Level_2", true)) {
                        button2.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button2.setBackgroundResource(R.drawable.start_button_background);
                } else button2.setBackgroundResource(R.drawable.start_button_background);
            } else button2.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button2.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button3 = (Button) findViewById(R.id.button3);
        btn3 = button3;
        button3.setLayoutParams(lp);
        button3.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button3.setX(MainMenu.width1/6*2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_2")) {
            if (MainMenu.sPref.getBoolean("Level_2", true)){
                button3.setText("3");
                if (MainMenu.sPref.contains("Level_3")) {
                    if (MainMenu.sPref.getBoolean("Level_3", true)) {
                        button3.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button3.setBackgroundResource(R.drawable.start_button_background);
                } else button3.setBackgroundResource(R.drawable.start_button_background);
            } else button3.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button3.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button4 = (Button) findViewById(R.id.button4);
        btn4 = button4;
        button4.setLayoutParams(lp);
        button4.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button4.setX(MainMenu.width1/6*3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_3")) {
            if (MainMenu.sPref.getBoolean("Level_3", true)){
                button4.setText("4");
                if (MainMenu.sPref.contains("Level_4")) {
                    if (MainMenu.sPref.getBoolean("Level_4", true)) {
                        button4.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button4.setBackgroundResource(R.drawable.start_button_background);
                } else button4.setBackgroundResource(R.drawable.start_button_background);
            } else button4.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button4.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button5 = (Button) findViewById(R.id.button5);
        btn5 = button5;
        button5.setLayoutParams(lp);
        button5.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button5.setX(MainMenu.width1/6*4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_4")) {
            if (MainMenu.sPref.getBoolean("Level_4", true)){
                button5.setText("5");
                if (MainMenu.sPref.contains("Level_5")) {
                    if (MainMenu.sPref.getBoolean("Level_5", true)) {
                        button5.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button5.setBackgroundResource(R.drawable.start_button_background);
                } else button5.setBackgroundResource(R.drawable.start_button_background);
            } else button5.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button5.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button6 = (Button) findViewById(R.id.button6);
        btn6 = button6;
        button6.setLayoutParams(lp);
        button6.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button6.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        if (MainMenu.sPref.contains("Level_5")) {
            if (MainMenu.sPref.getBoolean("Level_5", true)){
                button6.setText("6");
                if (MainMenu.sPref.contains("Level_6")) {
                    if (MainMenu.sPref.getBoolean("Level_6", true)) {
                        button6.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button6.setBackgroundResource(R.drawable.start_button_background);
                } else button6.setBackgroundResource(R.drawable.start_button_background);
            } else button6.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button6.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button7 = (Button) findViewById(R.id.button7);
        btn7 = button7;
        button7.setLayoutParams(lp);
        button7.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button7.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_6")) {
            if (MainMenu.sPref.getBoolean("Level_6", true)){
                button7.setText("7");
                if (MainMenu.sPref.contains("Level_7")) {
                    if (MainMenu.sPref.getBoolean("Level_7", true)) {
                        button7.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button7.setBackgroundResource(R.drawable.start_button_background);
                } else button7.setBackgroundResource(R.drawable.start_button_background);
            } else button7.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button7.setBackgroundResource(R.drawable.start_button_background_lock);


        final Button button8 = (Button) findViewById(R.id.button8);
        btn8 = button8;
        button8.setLayoutParams(lp);
        button8.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button8.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_7")) {
            if (MainMenu.sPref.getBoolean("Level_7", true)){
                button8.setText("8");
                if (MainMenu.sPref.contains("Level_8")) {
                    if (MainMenu.sPref.getBoolean("Level_8", true)) {
                        button8.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button8.setBackgroundResource(R.drawable.start_button_background);
                } else button8.setBackgroundResource(R.drawable.start_button_background);
            } else button8.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button8.setBackgroundResource(R.drawable.start_button_background_lock);


        final ImageView level = (ImageView) findViewById(R.id.imageView);
        btn1 = button1;
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.level);
        final Bitmap levels = Bitmap.createScaledBitmap(bitmap,MainMenu.width1, MainMenu.height1/8, false);
        final Drawable levels_1 = new BitmapDrawable(getResources(), levels);
        level.setBackground(levels_1);


        final Button btn1 =(Button) findViewById(R.id.button1);
        final levels this_ = this;
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, Level_1.class);
                startActivity(intent);
            }
        };
        btn1.setOnClickListener(listener1);


        final Button btn2 =(Button) findViewById(R.id.button2);
        final View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_1", false)) {
                    Log.d("2", "proshel");
                    Intent intent = new Intent(this_, Level_2.class);
                    startActivity(intent);
                }

            }
        };
        btn2.setOnClickListener(listener2);

        final Button btn3 =(Button) findViewById(R.id.button3);
        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_2", false)) {
                    Intent intent = new Intent(this_, Level_3.class);
                    startActivity(intent);
                }
            }
        };
        btn3.setOnClickListener(listener3);

        final Button btn4 =(Button) findViewById(R.id.button4);
        final View.OnClickListener listener4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_3", false)) {
                    Intent intent = new Intent(this_, Level_4.class);
                    startActivity(intent);
                }
            }
        };
        btn4.setOnClickListener(listener4);

        final Button btn5 =(Button) findViewById(R.id.button5);
        final View.OnClickListener listener5 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_4", false)) {
                    Intent intent = new Intent(this_, Level_4.class);
                    startActivity(intent);
                }
            }
        };
        btn5.setOnClickListener(listener5);


    }

}