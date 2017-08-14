package com.yogw.windmill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Windmill windmill_big;
    Windmill windmill_small;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        windmill_big = (Windmill) findViewById(R.id.windmill_big);
        windmill_big.setWindSpeed(6);
        windmill_small = (Windmill) findViewById(R.id.windmill_small);
        windmill_small.setWindSpeed(6);


    }

    @Override
    protected void onResume() {
        super.onResume();
        windmill_big.startAnimation();
        windmill_small.startAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        windmill_big.clearAnimation();
        windmill_small.clearAnimation();
    }
}
