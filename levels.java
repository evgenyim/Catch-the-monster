package com.example.user.catchthemonster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    }

}
