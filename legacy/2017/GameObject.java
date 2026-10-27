
package com.example.user.catchthemonster;

/**
 * Created by User on 08.06.2017.
 */

public abstract class GameObject {

    protected int x;
    protected int y;


    GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void SetX(int x) {
        this.x = x;
    }

    void SetY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void update(final long millisSinceLastUpdate);

}
