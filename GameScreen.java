package com.example.user.catchthemonster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by user on 6/7/17.
 */

public class GameScreen extends FrameLayout {
    static int counter;

    ArrayList <Pair> coord = new ArrayList<>();

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
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shrek);
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        canvas.drawBitmap(bm,coord.get(counter).a, coord.get(counter).b,null);
        counter ++;
        if (counter < coord.size()){
            postInvalidateDelayed(1000);
        }
    }
    public void addcoord(final Pair p) {
        coord.add(p);
        postInvalidateDelayed(50);
    }

}