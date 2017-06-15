package com.example.user.catchthemonster;

/**
 * Created by user on 6/7/17.
 */

public class monstrik extends GameObject {
    static int x;
    static int y;
    static int a;
    static int b;

    monstrik(int x, int y){
        super(x, y);
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(long millisSinceLastUpdate) {

    }

    static void direction() {
        if ((a / 5 == b / 5) && (a < b)) {
            right();
        }
        if ((a / 5 == b / 5) && (a > b)) {
            left();
        }
        if ((a % 5 == b % 5) && (a < b)) {
            down();
        }
        if ((a % 5 == b % 5) && (a > b)) {
            up();
        }
        if ((a / 5 < b / 5) && (a % 5 < b % 5)) {
            right_down();
        }
        if ((a / 5 < b / 5) && (a % 5 > b % 5)) {
           left_down();
        }
        if ((a / 5 > b / 5) && (a % 5 > b % 5)) {
            left_up();
        }
        if ((a / 5 > b / 5) && (a % 5 < b % 5)) {
            right_up();
        }
    }
    static void right(){
        x+=20;
    }
    static void left(){
        x-=20;
    }
    static void down(){
        y+=20;
    }
    static void up(){
        y-=20;
    }
    static void right_down(){
        x+=20;
        y+=20;
    }
    static void left_down() {
        x -= 20;
        y += 20;
    }
    static void right_up(){
        x+=20;
        y-=20;
    }
    static void left_up(){
        x-=20;
        y-=20;
    }


}
