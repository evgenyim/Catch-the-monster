package com.example.user.catchthemonster;

/**
 * Created by User on 09.06.2017.
 */

public class Square {


    public static int Square_x(int square)
    {
        return (square % 5)*(MainMenu.width1/5);
    }
    public static int Square_y(int square)
    {
        return (square/5+1)*(MainMenu.width1/5);
    }
}