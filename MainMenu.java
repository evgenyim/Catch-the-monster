package com.example.user.catchthemonster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainMenu extends AppCompatActivity {

    static int width1;
    static int height1;

    static SharedPreferences sPref;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sPref = getPreferences(MODE_PRIVATE);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        final int height = size.y;
        width1 = width;
        height1 = height;

        final Button btn3 = (Button) findViewById(R.id.btn3);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn3.getLayoutParams();
        lp.height = height1/10;
        lp.width = width1/4;
        btn3.setX(width1/2 - width1/8);
        btn3.setY(height1/2+2*height1/10+2*height1/20);
        final View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        };
        btn3.setOnClickListener(listener3);

        final Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setLayoutParams(lp);
        btn1.setX(width1/2 - width1/8);
        btn1.setY(height1/2);
        final MainMenu this_ = this;
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, levels.class);
                startActivity(intent);
            }
        };
        btn1.setOnClickListener(listener1);

        final Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setLayoutParams(lp);
        btn2.setX(width1/2 - width1/8);
        btn2.setY(height1/2+height1/10+height1/20);
        final View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, Help.class);
                startActivity(intent);
            }
        };
        btn2.setOnClickListener(listener2);
        final ImageView title = (ImageView) findViewById(R.id.imageView);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ctm_title);
        final Bitmap titles = Bitmap.createScaledBitmap(bitmap,MainMenu.width1/1, MainMenu.height1/2-height1/10, false);
        final Drawable title_1 = new BitmapDrawable(getResources(), titles);
        title.setBackground(title_1);
        title.setX(0);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
