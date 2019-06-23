package com.zhangche.kaoqin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mainLayout = findViewById(R.id.main_layout);
        for(int i = 1;i<31;i++) {
            mainLayout.addView(new DayDataLinerLaylout(MainActivity.this,i,null,null));
        }
    }
}
