package com.example.user.catchthemonster;

/**
 * Created by user on 6/9/17.
 */

public class Coords {

    public static int getSquare(int x,int y,int field_size) {
        int i = y / (MainMenu.width1/5);
        int j = x / (MainMenu.width1/5);
        return i*field_size+j;
    }

}
