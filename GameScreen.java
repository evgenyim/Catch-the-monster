package com.example.user.catchthemonster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.icu.util.TimeUnit;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import static android.R.attr.delay;
import static android.R.attr.y;

/**
 * Created by user on 6/7/17.
 */

public class GameScreen extends FrameLayout {

    int x = 200;

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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.table5);
        canvas.drawBitmap(bitmap,100,500,null);
        canvas.drawCircle(x,x,50,paint);
        while (x < 1000){
            x ++;
            invalidate();
        }

    }

}