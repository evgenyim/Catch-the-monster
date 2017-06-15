package com.example.user.catchthemonster;

/**
 * Created by Rik on 12.06.2017.
 */

public class NumderByCoord {


    static int NumderByCoord (float x, float y) {
        int x1 = (int) x / (MainMenu.width1/5);
        int y1 = (int) y / (MainMenu.width1/5) - 1;
        return y1*5+x1;
    }
}
