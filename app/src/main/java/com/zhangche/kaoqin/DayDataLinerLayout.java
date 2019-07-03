package com.zhangche.kaoqin;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

import static android.content.ContentValues.TAG;

public class DayDataLinerLayout extends LinearLayout {

    Context mContext;
    TextView date, workOn, workOff, workTime;
    DataBaseFunc dataBaseFunc;
    String thisDay;
    String mWorkOnTime = "08:30";
    String mWorkOffTime = "??:??";
    String mOverWorkTime = "??:??";
    int dayOfWeek = 0;
    int dayOfMonth = 0;
    int[] styleColor = {
            0xFFFF7F00, //orange
            0xFFFFFF00, //yellow
            0xFF00FF00, //green
            0xFF00FFFF, //cyan
            0xFF6495ED, //blue
            0xFF8B00FF, //purple
            0xFFFF0000, //red
    };
    String[] weekDayName = {"日", "一", "二", "三", "四", "五", "六"};

    public DayDataLinerLayout(Context context, String time, boolean isTitle) {
        super(context);
        mContext = context;
        dataBaseFunc = new DataBaseFunc(mContext);
        date = new TextView(context);
        workOn = new TextView(context);
        workOff = new TextView(context);
        TextView textView = new TextView(context);
        workTime = new TextView(context);

        thisDay = time;
        initWeekDays(thisDay);
        if (dataBaseFunc.isDateSeted(thisDay)) {
            String[] result = dataBaseFunc.getData(thisDay);
            mWorkOnTime = result[0];
            mWorkOffTime = result[1];
        }
        calcOverWorkTime();

        setOrientation(HORIZONTAL);
        setBackgroundColor(styleColor[dayOfWeek]);

//        setShowDividers(SHOW_DIVIDER_MIDDLE);
//        setDividerDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.black_line,null));
        addView(date);
        addView(workOn);
        addView(textView);
        addView(workOff);
        addView(workTime);

        LinearLayout.LayoutParams dateLayoutParams = (LayoutParams) date.getLayoutParams();
        dateLayoutParams.weight = 2;
        dateLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        date.setLayoutParams(dateLayoutParams);
        date.setText(formatInt(dayOfMonth) + "(" + weekDayName[dayOfWeek] + ")");
        date.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
//        date.setBackgroundColor(styleColor[dayOfWeek]);


        LinearLayout.LayoutParams workOnLayoutParams = (LayoutParams) workOn.getLayoutParams();
        workOnLayoutParams.weight = 2;
        workOnLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOn.setLayoutParams(workOnLayoutParams);
        workOn.setText(mWorkOnTime);
        workOn.setOnClickListener(new dateClickListener(false));
        workOn.setGravity(Gravity.CENTER);
//        workOn.setBackgroundColor(styleColor[dayOfWeek]);

        LinearLayout.LayoutParams textLayoutParams = (LayoutParams) textView.getLayoutParams();
        textLayoutParams.weight = 1;
        textLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setLayoutParams(textLayoutParams);
        textView.setText("-");
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//        textView.setBackgroundColor(styleColor[dayOfWeek]);

        LinearLayout.LayoutParams workOffLayoutParams = (LayoutParams) workOff.getLayoutParams();
        workOffLayoutParams.weight = 2;
        workOffLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workOff.setLayoutParams(workOffLayoutParams);
        workOff.setText(mWorkOffTime);
        workOff.setOnClickListener(new dateClickListener(true));
        workOff.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        LinearLayout.LayoutParams workTimeLayoutParams = (LayoutParams) workTime.getLayoutParams();
        workTimeLayoutParams.weight = 2;
        workTimeLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        workTime.setLayoutParams(workOffLayoutParams);
        workTime.setText(mOverWorkTime);
        workTime.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        if (isTitle) {
            setBackgroundColor(Color.BLACK);
            date.setText(" 日期  ");
            date.setTextColor(Color.WHITE);
            workOn.setText(" 上班 ");
            workOn.setTextColor(Color.WHITE);
            workOn.setOnClickListener(null);
            workOff.setText("下班");
            workOff.setTextColor(Color.WHITE);
            workOff.setOnClickListener(null);
            workTime.setText("加班");
            workTime.setTextColor(Color.WHITE);
            textView.setTextColor(Color.WHITE);
        }
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
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            minuteOfHour = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    if (!isWorkOff) {
                        mWorkOnTime = formatInt(hour) + ":" + formatInt(minute);
                        workOn.setText(mWorkOnTime);
                    } else {
                        mWorkOffTime = formatInt(hour) + ":" + formatInt(minute);
                        Log.d(TAG, mWorkOffTime);
                        workOff.setText(mWorkOffTime);
                    }
                    Log.d(TAG, mWorkOffTime);
                    calcOverWorkTime();
                    workTime.setText(mOverWorkTime);
                    dataBaseFunc.insert(thisDay, mWorkOnTime, mWorkOffTime);
                }
            }, hourOfDay, minuteOfHour, true).show();

        }
    }

    private void initWeekDays(String date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void calcOverWorkTime() {
        if (mWorkOffTime.equals("??:??")) {
            return;
        }
        String[] temp = mWorkOnTime.split(":");
        int workOnTime = Integer.parseInt(temp[0]) * 60 + Integer.parseInt(temp[1]);
        temp = mWorkOffTime.split(":");
        int workOffTime = Integer.parseInt(temp[0]) * 60 + Integer.parseInt(temp[1]);
        int workTime = workOffTime - workOnTime;
        if (dayOfWeek == 0 || dayOfWeek == 6) {
            workTime += 9 * 60;
        }
        mOverWorkTime = formatInt(workTime / 60 - 9) + ":" + formatInt(workTime % 60);
    }

    private String formatInt(int number) {
        if (number < 10) {
            if (number < 0)
                return String.valueOf(number);
            return "0" + number;
        }
        return String.valueOf(number);
    }
}
