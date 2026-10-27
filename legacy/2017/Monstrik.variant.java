package com.example.user.catchthemonster;

/**
 * Created by User on 08.06.2017.
 */

public class Monstrik extends GameObject {

    Direction movementDirection = Direction.NONE;

    static final int MOVE_SPEED = 50;

    Monstrik(int x, int y){
        super(x, y);
    }



    @Override
    public void update(long millisSinceLastUpdate) {
        float seconds = (float) millisSinceLastUpdate / 1000;
        switch (movementDirection) {
            case LEFT:
                break;
            case TOP:
                break;
            case DOWN:
                break;
            case RIGHT:
                this.x += seconds * MOVE_SPEED;
                break;
            case NONE:
                break;
        }
    }

    void goRight(){
        movementDirection = Direction.RIGHT;
    }


}
