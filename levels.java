package com.example.user.catchthemonster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class levels extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);



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
        Log.d("3", String.valueOf(MainMenu.sPref.getBoolean("Level_1",true)));

        final Button btn2 =(Button) findViewById(R.id.button2);
        final View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainMenu.sPref.getBoolean("Level_1", false)) {
                    Log.d("2", "proshel");
                    Intent intent = new Intent(this_, Level_2.class);
                    startActivity(intent);
                }

            }
        };
        btn2.setOnClickListener(listener2);



    }

}
