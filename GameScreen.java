package com.example.user.catchthemonster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by user on 6/7/17.
 */

public class GameScreen extends FrameLayout {
    static int counter;

    ArrayList <Pair1> coord = new ArrayList<>();

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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.monster);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.table5);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.pit);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),R.drawable.youwin);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(),R.drawable.character);
        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(),R.drawable.stone);
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(),R.drawable.cage);

        Bitmap monster = Bitmap.createScaledBitmap(bitmap,MainMenu.width1/5, MainMenu.width1/5, false);
        Bitmap field = Bitmap.createScaledBitmap(bitmap1,MainMenu.width1, MainMenu.width1, false);
        Bitmap pit = Bitmap.createScaledBitmap(bitmap2,MainMenu.width1/5, MainMenu.width1/5, false);
        Bitmap character = Bitmap.createScaledBitmap(bitmap4,MainMenu.width1/5, MainMenu.width1/5, false);
        Bitmap stone = Bitmap.createScaledBitmap(bitmap5, MainMenu.width1/5, MainMenu.width1/5, false);
        Bitmap cage = Bitmap.createScaledBitmap(bitmap6,MainMenu.width1/5, MainMenu.width1/5, false);
        Bitmap youwin = Bitmap.createScaledBitmap(bitmap3,MainMenu.width1/5*4, MainMenu.width1/5*3, false);

        canvas.drawBitmap(field,0, MainMenu.width1/5,null);
        canvas.drawBitmap(pit,Square.Square_x(Level_1.pit_square1),Square.Square_y(Level_1.pit_square1),null);
        canvas.drawBitmap(character,Square.Square_x(Level_1.character_square1),Square.Square_y(Level_1.character_square1),null);
        int index = Math.min(counter, coord.size() - 1);
        canvas.drawBitmap(monster,coord.get(index).a, coord.get(index).b,null);
        for(int i = 0;i<Level_1.forobst1.length;i++) {
            canvas.drawBitmap(stone,Square.Square_x(Level_1.forobst1[i]),Square.Square_y(Level_1.forobst1[i]),null);
        }

        if (coord.get(index).a == Square.Square_x(Level_1.pit_square1) && coord.get(index).b==Square.Square_y(Level_1.pit_square1)) {
            counter = coord.size();
            canvas.drawBitmap(cage,Square.Square_x(Level_1.pit_square1),Square.Square_y(Level_1.pit_square1),null);
            canvas.drawBitmap(youwin,MainMenu.width1/7,MainMenu.height1/4,null);
        }



        counter ++;
        if (counter < coord.size()){
            postInvalidateDelayed(600);
        }
    }


    public void addcoord(final Pair1 p) {
        coord.add(p);
        postInvalidateDelayed(50);
    }
}