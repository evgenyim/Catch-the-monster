package com.example.user.catchthemonster;

import android.util.Log;

/**
 * Created by User on 13.06.2017.
 */

public class NumberByCoord {
    int x;
    int y;
    public static int NumberByCoord(float x, float y){
        int x1 = (int) x / (MainMenu.width1/5);
        int y1 = (int) y / (MainMenu.width1/5);
        return x1+y1*5-5;
    }
}
