package com.example.user.catchthemonster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class levels extends AppCompatActivity {
    GridView gridView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        int space_y = MainMenu.height1 - 6 * MainMenu.width1/5;

        final Button button1 = (Button) findViewById(R.id.button1);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button1.getLayoutParams();
        lp.height = MainMenu.width1/6;
        lp.width = MainMenu.width1/6;
        button1.setLayoutParams(lp);
        button1.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button1.setX(MainMenu.width1/6/6);
        button1.setBackgroundResource(R.drawable.start_button_background);
        button1.setText("1");

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setLayoutParams(lp);
        button2.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button2.setX(MainMenu.width1/6 + MainMenu.width1/6/6*2);
        button2.setBackgroundResource(R.drawable.start_button_background);
        button2.setText("2");

        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setLayoutParams(lp);
        button3.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button3.setX(MainMenu.width1/6*2 + MainMenu.width1/6/6*3);
        button3.setBackgroundResource(R.drawable.start_button_background);
        button3.setText("3");

        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setLayoutParams(lp);
        button4.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button4.setX(MainMenu.width1/6*3 + MainMenu.width1/6/6*4);
        button4.setBackgroundResource(R.drawable.start_button_background);
        button4.setText("4");

        final Button button5 = (Button) findViewById(R.id.button5);

        button5.setLayoutParams(lp);
        button5.setY(MainMenu.height1/8+MainMenu.height1/8/8);
        button5.setX(MainMenu.width1/6*4 + MainMenu.width1/6/6*5);
        button5.setBackgroundResource(R.drawable.start_button_background);
        button5.setText("5");

        final Button button6 = (Button) findViewById(R.id.button6);

        button6.setLayoutParams(lp);
        button6.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button6.setX(MainMenu.width1/6 *0 + MainMenu.width1/6/6*1);
        button6.setBackgroundResource(R.drawable.start_button_background);
        button6.setText("6");

        final Button button7 = (Button) findViewById(R.id.button7);

        button7.setLayoutParams(lp);
        button7.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button7.setX(MainMenu.width1/6 *1 + MainMenu.width1/6/6*2);
        button7.setBackgroundResource(R.drawable.start_button_background);
        button7.setText("7");

        final Button button8 = (Button) findViewById(R.id.button8);

        button8.setLayoutParams(lp);
        button8.setY(MainMenu.height1/8*2+MainMenu.height1/8/8*2);
        button8.setX(MainMenu.width1/6 *2 + MainMenu.width1/6/6*3);
        button8.setBackgroundResource(R.drawable.start_button_background);
        button8.setText("8");

        final ImageView level = (ImageView) findViewById(R.id.imageView);

        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.level);
        final Bitmap levels = Bitmap.createScaledBitmap(bitmap,MainMenu.width1, MainMenu.height1/8, false);
        final Drawable levels_1 = new BitmapDrawable(getResources(), levels);
        level.setBackground(levels_1);


        final Button btn1 =(Button) findViewById(R.id.button1);
        final levels this_ = this;
        final View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this_, Level_1.class);
                startActivity(intent);
            }
        };
        btn1.setOnClickListener(listener1);

    }
    private void adjustGridView() {
        gridView.setNumColumns(5);
    }

}
