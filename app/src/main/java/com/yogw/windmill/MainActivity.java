package com.yogw.windmill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Windmill windmill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        windmill = (Windmill) findViewById(R.id.windmill);
        windmill.setWindSpeed(6);


    }

    @Override
    protected void onResume() {
        super.onResume();
        windmill.startAnimation();

    }

    @Override
    protected void onPause() {
        super.onPause();
        windmill.clearAnimation();
    }
}
