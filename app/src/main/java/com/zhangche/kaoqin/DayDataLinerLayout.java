package com.zhangche.kaoqin;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class DayDataLinerLayout extends LinearLayout {

    Context mContext;
    TextView date,workOn,workOff;
    public DayDataLinerLayout(Context context, String time, Date workOnTime, Date workOffTime) {
        super(context);
        mContext = context;
        date = new TextView(context);
        workOn = new TextView(context);
        workOff = new TextView(context);
        TextView textView = new TextView(context);

        setOrientation(HORIZONTAL);

//        setShowDividers(SHOW_DIVIDER_MIDDLE);
//        setDividerDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.black_line,null));
        addView(date);
        addView(workOn);
        addView(textView);
        addView(workOff);

        LinearLayout.LayoutParams dateLayoutParams = (LayoutParams) date.getLayoutParams();
        dateLayoutParams.weight = 2;
        dateLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        date.setLayoutParams(dateLayoutParams);
        date.setText(getWeekDays(time));
        date.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
//        date.setBackgroundColor(Color.WHITE);


        LinearLayout.LayoutParams workOnLayoutParams = (LayoutParams) workOn.getLayoutParams();
        workOnLayoutParams.weight = 2;
        workOnLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOn.setLayoutParams(workOnLayoutParams);
        workOn.setText(getFormatTime(workOnTime,true));
        workOn.setOnClickListener(new dateClickListener(false));
        workOn.setGravity(Gravity.CENTER);
//        workOn.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams textLayoutParams = (LayoutParams) textView.getLayoutParams();
        textLayoutParams.weight = 1;
        textLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setLayoutParams(textLayoutParams);
        textView.setText("-");
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);

        LinearLayout.LayoutParams workOffLayoutParams = (LayoutParams) workOff.getLayoutParams();
        workOffLayoutParams.weight = 2;
        workOffLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOff.setLayoutParams(workOffLayoutParams);
        workOff.setText(getFormatTime(workOffTime,false));
        workOff.setOnClickListener(new dateClickListener(true));
        workOff.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
//        workOff.setBackgroundColor(Color.WHITE);

    }

    class dateClickListener implements View.OnClickListener {
        private int hourOfDay = 8;
        private int minuteOfHour = 30;
        boolean isWorkOff = false;
        public dateClickListener(boolean workOff) {
            if (workOff) {
                hourOfDay = 17;
                isWorkOff = true;
            }
        }
        @Override
        public void onClick(View v) {
            new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    if (isWorkOff)
                        workOff.setText(hour + ":" + minute);
                    else
                        workOn.setText(hour + ":" + minute);
                }
            },hourOfDay,minuteOfHour,true).show();

        }
    }
    private String getFormatTime(Date time,boolean isMorning) {
        if (time == null)
            return isMorning ? "8:30" : "??:??";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.CHINA);
        sdf.format(calendar.getTime());
        return sdf.format(calendar.getTime());
    }
    private String getWeekDays(String date) {
        String[] weekDayName = {"日","一","二","三","四","五","六"};
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) -1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth + "  (" + weekDayName[dayOfWeek] + ")";

    }
}
