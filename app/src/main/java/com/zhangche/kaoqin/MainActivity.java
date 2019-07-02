package com.zhangche.kaoqin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {

    int mYear = 2019;
    int mMonth = 6;
    int mDay = 20;
    int tempYear = mYear;
    int tempMonth = mMonth;
    TextView monthInfo;
    LinearLayout mainLayout;
    LinearLayout list_title;
    GetBusGpsData getBusGpsData;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.date_list_layout);
        list_title = findViewById(R.id.list_title);

        monthInfo = findViewById(R.id.date_info);
        monthInfo.setText(getMonth(null));
        monthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMonthChooser();
            }
        });

        reDrawViews();
        final TextView textView = findViewById(R.id.info_show);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int[] busData = (int[]) msg.obj;
                textView.setText("到站时间：" + busData[0] + "分钟\n");
                textView.append("当前方向：" + (getBusGpsData.isGoHome ? "回家" :"去公司") + "\n");
                textView.append("距离本站站点：" + busData[1] + "站\n");
                textView.append("距离本站距离：" + busData[2] + "米\n");
                textView.append("当前站点：" + getBusGpsData.getCurrentStation() + "\n");
            }
        };
        getBusGpsData = new GetBusGpsData(handler);
        GregorianCalendar ca = new GregorianCalendar();
        getBusGpsData.isGoHome = (ca.get(GregorianCalendar.AM_PM) == 1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBusGpsData.isGoHome = !getBusGpsData.isGoHome;
                textView.setText("当前方向：" + (getBusGpsData.isGoHome ? "回家" :"去公司") + "\n");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBusGpsData.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getBusGpsData.need_exit = true;
    }

    private void reDrawViews() {
        monthInfo.setText(mYear + "年" + mMonth + "月");
        list_title.removeAllViews();
        list_title.addView(new DayDataLinerLayout(MainActivity.this,"20190620",true));
        mainLayout.removeAllViews();
        for(int i = 0;i<getMonthDays(mYear,mMonth);i++) {
            mainLayout.addView(new DayDataLinerLayout(MainActivity.this,formatTime(mYear,mMonth,i + 1),false));
        }

    }
    private void createMonthChooser() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setIcon(null);
        alertDialog.setTitle("选择月份");
        alertDialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.month_chooser,(ViewGroup) findViewById(R.id.month_chooser));
        DatePicker dp = layout.findViewById(R.id.datePicker);
        dp.init(mYear, mMonth - 1, mDay, new dateChangeListener());
        ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        alertDialog.setView(layout);
        alertDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        mYear = tempYear;
                        mMonth = tempMonth;
                        reDrawViews();
                    }
                });
            alertDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                            tempYear = mYear;
                            tempMonth = mMonth;
                        }
                    });
        // 显示
        alertDialog.show();
    }

    class dateChangeListener implements DatePicker.OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            tempYear = year;
            tempMonth = month + 1;
        }
    }
    private String getMonth(String targetDate) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
        try {
            if (targetDate != null)
                calendar.setTime(sdf.parse(targetDate));
            sdf.format(calendar.getTime());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return sdf.format(calendar.getTime());

    }
    public static int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){
                    return 29;
                }else{
                    return 28;
                }
            default:
                return -1;
        }
    }
    private String formatTime(int year, int month, int day) {
        String result = "";
        if (month < 10)
            result = result + year + "0" + month;
        else
            result = result + year + month;

        if (day < 10)
            result = result + "0" + day;
        else
            result = result + day;

        return result;
    }
}
