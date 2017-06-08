package com.example.user.catchthemonster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * Created by user on 6/7/17.
 */

public class GameScreen extends FrameLayout {

    private long lastUpdateTime = System.currentTimeMillis();

    LinkedList<GameObject> gameObjects = new LinkedList<>();
    public GameScreen(@NonNull Context context) {
        super(context);
    }

    public GameScreen(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameScreen(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("onDraw", "mi lubim java");
        long now = System.currentTimeMillis();
        long passedSinceLastUpdate = now - lastUpdateTime;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        for (GameObject gameObject : gameObjects) {
            gameObject.update(passedSinceLastUpdate);
            canvas.drawCircle(gameObject.getX(), gameObject.getY(), 50, paint);
        }
        postInvalidateDelayed(50);
        lastUpdateTime = System.currentTimeMillis();
    }

    public void addGameObject(final GameObject gameObject) {
        gameObjects.add(gameObject);
        postInvalidateDelayed(50);
    }
}