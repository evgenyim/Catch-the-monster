package com.example.user.catchthemonster;

/**
 * Created by User on 09.06.2017.
 */

public class Pit {
    int square;
    int x =  Pit_x(square);
    int y = Pit_y(square);


    public int Pit_x(int square)
    {
        return (square % 5)*(MainMenu.width1/5);
    }
    public int Pit_y(int square)
    {
        return (square/5)*(MainMenu.width1/5);
    }
}
