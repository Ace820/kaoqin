package com.zhangche.kaoqin;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayDataLinerLaylout extends LinearLayout {

    TextView date,workOn,workOff;
    public DayDataLinerLaylout(Context context, int time, Date workOnTime, Date workOffTime) {
        super(context);
//        date = new Button(context);
//        workOn = new Button(context);
//        workOff = new Button(context);
        date = new TextView(context);
        workOn = new TextView(context);
        workOff = new TextView(context);
        TextView textView = new TextView(context);

        setOrientation(HORIZONTAL);
        addView(date);
        addView(workOn);
        addView(textView);
        addView(workOff);

        LinearLayout.LayoutParams dateLayoutParams = (LayoutParams) date.getLayoutParams();
        dateLayoutParams.weight = 2;
        dateLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        date.setLayoutParams(dateLayoutParams);
        date.setText(String.valueOf(time));
        date.setGravity(Gravity.CENTER);
        date.setBackgroundColor(Color.WHITE);


        LinearLayout.LayoutParams workOnLayoutParams = (LayoutParams) workOn.getLayoutParams();
        workOnLayoutParams.weight = 2;
        workOnLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOn.setLayoutParams(workOnLayoutParams);
        workOn.setText(getFormatTime(workOnTime,true));
        workOn.setGravity(Gravity.CENTER);
        workOn.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams textLayoutParams = (LayoutParams) textView.getLayoutParams();
        textLayoutParams.weight = 1;
        textLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setLayoutParams(textLayoutParams);
        textView.setText("-");
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams workOffLayoutParams = (LayoutParams) workOff.getLayoutParams();
        workOffLayoutParams.weight = 2;
        workOffLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOff.setLayoutParams(workOffLayoutParams);
        workOff.setText(getFormatTime(workOffTime,false));
        workOff.setGravity(Gravity.CENTER);
        workOff.setBackgroundColor(Color.WHITE);

    }

    private String getFormatTime(Date time,boolean isMorning) {
        if (time == null)
            return isMorning ? "8:30" : "?";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.CHINA);
        sdf.format(calendar.getTime());
        return sdf.format(calendar.getTime());
    }
}
