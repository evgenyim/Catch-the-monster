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
    static Button btn9;
    static Button btn10;
    static Button btn11;
    static Button btn12;
    static Button btn13;
    static Button btn14;
    static Button btn15;
    static Button btn16;
    static Button btn17;
    static Button btn18;
    static Button btn19;
    static Button btn20;
    static Button btn21;
    static Button btn22;
    static Button btn23;
    static Button btn24;
    static Button btn25;
    static Button btn26;
    static Button btn27;
    static Button btn28;
    static Button btn29;
    static Button btn30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);


        int space_y = MainMenu.height1 - 6 * MainMenu.width1/5;
        final Button button1 = (Button) findViewById(R.id.button1);
        btn1 = button1;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button1.getLayoutParams();
        lp.height = MainMenu.width1/6;
        lp.width = MainMenu.width1/6;
        button1.setLayoutParams(lp);
        button1.setY(MainMenu.height1/8+MainMenu.height1/8);
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
        button2.setY(MainMenu.height1/8+MainMenu.height1/8);
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
        button3.setY(MainMenu.height1/8+MainMenu.height1/8);
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
        button4.setY(MainMenu.height1/8+MainMenu.height1/8);
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
        button5.setY(MainMenu.height1/8+MainMenu.height1/8);
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
        button6.setY(MainMenu.height1/8*2+MainMenu.height1/8 + MainMenu.height1/64);
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
        button7.setY(MainMenu.height1/8*2+MainMenu.height1/8 + MainMenu.height1/64);
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
        button8.setY(MainMenu.height1/8*2+MainMenu.height1/8 + MainMenu.height1/64);
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
        
        final Button button9 = (Button) findViewById(R.id.button9);
        btn9 = button9;
        button9.setLayoutParams(lp);
        button9.setY(MainMenu.height1/8*2+MainMenu.height1/8 + MainMenu.height1/64);
        button9.setX(MainMenu.width1/6 *3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_8")) {
            if (MainMenu.sPref.getBoolean("Level_8", true)){
                button9.setText("9");
                if (MainMenu.sPref.contains("Level_9")) {
                    if (MainMenu.sPref.getBoolean("Level_9", true)) {
                        button9.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button9.setBackgroundResource(R.drawable.start_button_background);
                } else button9.setBackgroundResource(R.drawable.start_button_background);
            } else button9.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button9.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button10 = (Button) findViewById(R.id.button10);
        btn10 = button10;
        button10.setLayoutParams(lp);
        button10.setY(MainMenu.height1/8*2+MainMenu.height1/8 + MainMenu.height1/64);
        button10.setX(MainMenu.width1/6 *4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_9")) {
            if (MainMenu.sPref.getBoolean("Level_9", true)){
                button10.setText("10");
                if (MainMenu.sPref.contains("Level_10")) {
                    if (MainMenu.sPref.getBoolean("Level_10", true)) {
                        button10.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button10.setBackgroundResource(R.drawable.start_button_background);
                } else button10.setBackgroundResource(R.drawable.start_button_background);
            } else button10.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button10.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button11 = (Button) findViewById(R.id.button11);
        btn11 = button11;
        button11.setLayoutParams(lp);
        button11.setY(MainMenu.height1/8*3+MainMenu.height1/8 + MainMenu.height1/64*2);
        button11.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        if (MainMenu.sPref.contains("Level_10")) {
            if (MainMenu.sPref.getBoolean("Level_10", true)){
                button11.setText("11");
                if (MainMenu.sPref.contains("Level_11")) {
                    if (MainMenu.sPref.getBoolean("Level_11", true)) {
                        button11.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button11.setBackgroundResource(R.drawable.start_button_background);
                } else button11.setBackgroundResource(R.drawable.start_button_background);
            } else button11.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button11.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button12 = (Button) findViewById(R.id.button12);
        btn12 = button12;
        button12.setLayoutParams(lp);
        button12.setY(MainMenu.height1/8*3+MainMenu.height1/8 + MainMenu.height1/64*2);
        button12.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_11")) {
            if (MainMenu.sPref.getBoolean("Level_11", true)){
                button12.setText("12");
                if (MainMenu.sPref.contains("Level_12")) {
                    if (MainMenu.sPref.getBoolean("Level_12", true)) {
                        button12.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button12.setBackgroundResource(R.drawable.start_button_background);
                } else button12.setBackgroundResource(R.drawable.start_button_background);
            } else button12.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button12.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button13 = (Button) findViewById(R.id.button13);
        btn13 = button13;
        button13.setLayoutParams(lp);
        button13.setY(MainMenu.height1/8*3+MainMenu.height1/8 + MainMenu.height1/64*2);
        button13.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_12")) {
            if (MainMenu.sPref.getBoolean("Level_12", true)){
                button13.setText("13");
                if (MainMenu.sPref.contains("Level_13")) {
                    if (MainMenu.sPref.getBoolean("Level_13", true)) {
                        button13.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button13.setBackgroundResource(R.drawable.start_button_background);
                } else button13.setBackgroundResource(R.drawable.start_button_background);
            } else button13.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button13.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button14 = (Button) findViewById(R.id.button14);
        btn14 = button14;
        button14.setLayoutParams(lp);
        button14.setY(MainMenu.height1/8*3+MainMenu.height1/8 + MainMenu.height1/64*2);
        button14.setX(MainMenu.width1/6 *3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_13")) {
            if (MainMenu.sPref.getBoolean("Level_13", true)){
                button14.setText("14");
                if (MainMenu.sPref.contains("Level_14")) {
                    if (MainMenu.sPref.getBoolean("Level_14", true)) {
                        button14.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button14.setBackgroundResource(R.drawable.start_button_background);
                } else button14.setBackgroundResource(R.drawable.start_button_background);
            } else button14.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button14.setBackgroundResource(R.drawable.start_button_background_lock);

        final Button button15 = (Button) findViewById(R.id.button15);
        btn15 = button15;
        button15.setLayoutParams(lp);
        button15.setY(MainMenu.height1/8*3+MainMenu.height1/8 + MainMenu.height1/64*2);
        button15.setX(MainMenu.width1/6 *4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_14")) {
            if (MainMenu.sPref.getBoolean("Level_14", true)){
                button15.setText("15");
                if (MainMenu.sPref.contains("Level_15")) {
                    if (MainMenu.sPref.getBoolean("Level_15", true)) {
                        button15.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button15.setBackgroundResource(R.drawable.start_button_background);
                } else button15.setBackgroundResource(R.drawable.start_button_background);
            } else button15.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button15.setBackgroundResource(R.drawable.start_button_background_lock);

        final Button button16 = (Button) findViewById(R.id.button16);
        btn16 = button16;
        button16.setLayoutParams(lp);
        button16.setY(MainMenu.height1/8*4+MainMenu.height1/8 + MainMenu.height1/64*3);
        button16.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        if (MainMenu.sPref.contains("Level_15")) {
            if (MainMenu.sPref.getBoolean("Level_15", true)){
                button16.setText("16");
                if (MainMenu.sPref.contains("Level_16")) {
                    if (MainMenu.sPref.getBoolean("Level_16", true)) {
                        button16.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button16.setBackgroundResource(R.drawable.start_button_background);
                } else button16.setBackgroundResource(R.drawable.start_button_background);
            } else button16.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button16.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button17 = (Button) findViewById(R.id.button17);
        btn17 = button17;
        button17.setLayoutParams(lp);
        button17.setY(MainMenu.height1/8*4+MainMenu.height1/8 + MainMenu.height1/64*3);
        button17.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_16")) {
            if (MainMenu.sPref.getBoolean("Level_16", true)){
                button17.setText("17");
                if (MainMenu.sPref.contains("Level_17")) {
                    if (MainMenu.sPref.getBoolean("Level_17", true)) {
                        button17.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button17.setBackgroundResource(R.drawable.start_button_background);
                } else button17.setBackgroundResource(R.drawable.start_button_background);
            } else button17.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button17.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button18 = (Button) findViewById(R.id.button18);
        btn18 = button18;
        button18.setLayoutParams(lp);
        button18.setY(MainMenu.height1/8*4+MainMenu.height1/8 + MainMenu.height1/64*3);
        button18.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_17")) {
            if (MainMenu.sPref.getBoolean("Level_17", true)){
                button18.setText("18");
                if (MainMenu.sPref.contains("Level_18")) {
                    if (MainMenu.sPref.getBoolean("Level_18", true)) {
                        button18.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button18.setBackgroundResource(R.drawable.start_button_background);
                } else button18.setBackgroundResource(R.drawable.start_button_background);
            } else button18.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button18.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button19 = (Button) findViewById(R.id.button19);
        btn19 = button19;
        button19.setLayoutParams(lp);
        button19.setY(MainMenu.height1/8*4+MainMenu.height1/8 + MainMenu.height1/64*3);
        button19.setX(MainMenu.width1/6 *3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_18")) {
            if (MainMenu.sPref.getBoolean("Level_18", true)){
                button19.setText("19");
                if (MainMenu.sPref.contains("Level_19")) {
                    if (MainMenu.sPref.getBoolean("Level_19", true)) {
                        button19.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button19.setBackgroundResource(R.drawable.start_button_background);
                } else button19.setBackgroundResource(R.drawable.start_button_background);
            } else button19.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button19.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button20 = (Button) findViewById(R.id.button20);
        btn20 = button20;
        button20.setLayoutParams(lp);
        button20.setY(MainMenu.height1/8*4+MainMenu.height1/8 + MainMenu.height1/64*3);
        button20.setX(MainMenu.width1/6 *4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_19")) {
            if (MainMenu.sPref.getBoolean("Level_19", true)){
                button20.setText("20");
                if (MainMenu.sPref.contains("Level_20")) {
                    if (MainMenu.sPref.getBoolean("Level_20", true)) {
                        button20.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button20.setBackgroundResource(R.drawable.start_button_background);
                } else button20.setBackgroundResource(R.drawable.start_button_background);
            } else button20.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button20.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button21 = (Button) findViewById(R.id.button21);
        btn21 = button21;
        button21.setLayoutParams(lp);
        button21.setY(MainMenu.height1/8*5+MainMenu.height1/8 + MainMenu.height1/64*4);
        button21.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        if (MainMenu.sPref.contains("Level_20")) {
            if (MainMenu.sPref.getBoolean("Level_20", true)){
                button21.setText("21");
                if (MainMenu.sPref.contains("Level_21")) {
                    if (MainMenu.sPref.getBoolean("Level_21", true)) {
                        button21.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button21.setBackgroundResource(R.drawable.start_button_background);
                } else button21.setBackgroundResource(R.drawable.start_button_background);
            } else button21.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button21.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button22 = (Button) findViewById(R.id.button22);
        btn22 = button22;
        button22.setLayoutParams(lp);
        button22.setY(MainMenu.height1/8*5+MainMenu.height1/8 + MainMenu.height1/64*4);
        button22.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_21")) {
            if (MainMenu.sPref.getBoolean("Level_21", true)){
                button22.setText("22");
                if (MainMenu.sPref.contains("Level_22")) {
                    if (MainMenu.sPref.getBoolean("Level_22", true)) {
                        button22.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button22.setBackgroundResource(R.drawable.start_button_background);
                } else button22.setBackgroundResource(R.drawable.start_button_background);
            } else button22.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button22.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button23 = (Button) findViewById(R.id.button23);
        btn23 = button23;
        button23.setLayoutParams(lp);
        button23.setY(MainMenu.height1/8*5+MainMenu.height1/8 + MainMenu.height1/64*4);
        button23.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_22")) {
            if (MainMenu.sPref.getBoolean("Level_22", true)){
                button23.setText("23");
                if (MainMenu.sPref.contains("Level_23")) {
                    if (MainMenu.sPref.getBoolean("Level_23", true)) {
                        button23.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button23.setBackgroundResource(R.drawable.start_button_background);
                } else button23.setBackgroundResource(R.drawable.start_button_background);
            } else button23.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button23.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button24 = (Button) findViewById(R.id.button24);
        btn24 = button24;
        button24.setLayoutParams(lp);
        button24.setY(MainMenu.height1/8*5+MainMenu.height1/8 + MainMenu.height1/64*4);
        button24.setX(MainMenu.width1/6 *3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_23")) {
            if (MainMenu.sPref.getBoolean("Level_23", true)){
                button24.setText("24");
                if (MainMenu.sPref.contains("Level_24")) {
                    if (MainMenu.sPref.getBoolean("Level_24", true)) {
                        button24.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button24.setBackgroundResource(R.drawable.start_button_background);
                } else button24.setBackgroundResource(R.drawable.start_button_background);
            } else button24.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button24.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button25 = (Button) findViewById(R.id.button25);
        btn25 = button25;
        button25.setLayoutParams(lp);
        button25.setY(MainMenu.height1/8*5+MainMenu.height1/8 + MainMenu.height1/64*4);
        button25.setX(MainMenu.width1/6 *4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_24")) {
            if (MainMenu.sPref.getBoolean("Level_24", true)){
                button25.setText("25");
                if (MainMenu.sPref.contains("Level_25")) {
                    if (MainMenu.sPref.getBoolean("Level_25", true)) {
                        button25.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button25.setBackgroundResource(R.drawable.start_button_background);
                } else button25.setBackgroundResource(R.drawable.start_button_background);
            } else button25.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button25.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button26 = (Button) findViewById(R.id.button26);
        btn26 = button26;
        button26.setLayoutParams(lp);
        button26.setY(MainMenu.height1/8*6+MainMenu.height1/8 + MainMenu.height1/64*5);
        button26.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        if (MainMenu.sPref.contains("Level_25")) {
            if (MainMenu.sPref.getBoolean("Level_25", true)){
                button26.setText("26");
                if (MainMenu.sPref.contains("Level_26")) {
                    if (MainMenu.sPref.getBoolean("Level_26", true)) {
                        button26.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button26.setBackgroundResource(R.drawable.start_button_background);
                } else button26.setBackgroundResource(R.drawable.start_button_background);
            } else button26.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button26.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button27 = (Button) findViewById(R.id.button27);
        btn27 = button27;
        button27.setLayoutParams(lp);
        button27.setY(MainMenu.height1/8*6+MainMenu.height1/8 + MainMenu.height1/64*5);
        button27.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        if (MainMenu.sPref.contains("Level_26")) {
            if (MainMenu.sPref.getBoolean("Level_26", true)){
                button27.setText("27");
                if (MainMenu.sPref.contains("Level_27")) {
                    if (MainMenu.sPref.getBoolean("Level_27", true)) {
                        button27.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button27.setBackgroundResource(R.drawable.start_button_background);
                } else button27.setBackgroundResource(R.drawable.start_button_background);
            } else button27.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button27.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button28 = (Button) findViewById(R.id.button28);
        btn28 = button28;
        button28.setLayoutParams(lp);
        button28.setY(MainMenu.height1/8*6+MainMenu.height1/8 + MainMenu.height1/64*5);
        button28.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        if (MainMenu.sPref.contains("Level_27")) {
            if (MainMenu.sPref.getBoolean("Level_27", true)){
                button28.setText("28");
                if (MainMenu.sPref.contains("Level_28")) {
                    if (MainMenu.sPref.getBoolean("Level_28", true)) {
                        button28.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button28.setBackgroundResource(R.drawable.start_button_background);
                } else button28.setBackgroundResource(R.drawable.start_button_background);
            } else button28.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button28.setBackgroundResource(R.drawable.start_button_background_lock);
        
        final Button button29 = (Button) findViewById(R.id.button29);
        btn29 = button29;
        button29.setLayoutParams(lp);
        button29.setY(MainMenu.height1/8*6+MainMenu.height1/8 + MainMenu.height1/64*5);
        button29.setX(MainMenu.width1/6 *3 + MainMenu.width1/6/6*4);
        if (MainMenu.sPref.contains("Level_28")) {
            if (MainMenu.sPref.getBoolean("Level_28", true)){
                button29.setText("29");
                if (MainMenu.sPref.contains("Level_29")) {
                    if (MainMenu.sPref.getBoolean("Level_29", true)) {
                        button29.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button29.setBackgroundResource(R.drawable.start_button_background);
                } else button29.setBackgroundResource(R.drawable.start_button_background);
            } else button29.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button29.setBackgroundResource(R.drawable.start_button_background_lock);
        
        
        final Button button30 = (Button) findViewById(R.id.button30);
        btn30 = button30;
        button30.setLayoutParams(lp);
        button30.setY(MainMenu.height1/8*6+MainMenu.height1/8 + MainMenu.height1/64*5);
        button30.setX(MainMenu.width1/6 *4 + MainMenu.width1/6/6*5);
        if (MainMenu.sPref.contains("Level_29")) {
            if (MainMenu.sPref.getBoolean("Level_29", true)){
                button30.setText("30");
                if (MainMenu.sPref.contains("Level_30")) {
                    if (MainMenu.sPref.getBoolean("Level_30", true)) {
                        button30.setBackgroundResource(R.drawable.start_button_background_star);
                    } else button30.setBackgroundResource(R.drawable.start_button_background);
                } else button30.setBackgroundResource(R.drawable.start_button_background);
            } else button30.setBackgroundResource(R.drawable.start_button_background_lock);
        } else button30.setBackgroundResource(R.drawable.start_button_background_lock);       
        
        
        

        final ImageView level = (ImageView) findViewById(R.id.imageView);
        btn1 = button1;
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.level);
        final Bitmap levels = Bitmap.createScaledBitmap(bitmap,MainMenu.width1, MainMenu.height1/8*2, false);
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
        
        final Button btn6 =(Button) findViewById(R.id.button6);
        final View.OnClickListener listener6 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_5", false)) {
                    Intent intent = new Intent(this_, Level_5.class);
                    startActivity(intent);
                }
            }
        };
        btn5.setOnClickListener(listener6);
        
        final Button btn7 =(Button) findViewById(R.id.button7);
        final View.OnClickListener listener7 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_6", false)) {
                    Intent intent = new Intent(this_, Level_6.class);
                    startActivity(intent);
                }
            }
        };
        btn5.setOnClickListener(listener7);
        
        final Button btn8 =(Button) findViewById(R.id.button8);
        final View.OnClickListener listener8 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_7", false)) {
                    Intent intent = new Intent(this_, Level_7.class);
                    startActivity(intent);
                }
            }
        };
        btn5.setOnClickListener(listener8);



    }

}
