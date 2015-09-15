package com.sos.my.mycalendar;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
    private String currentDate = "2015-7-30";//当前选中日期
    private String startDate = "";//可选日期的开始
    private String endDate = "2015-8-15";//可选日期的结束
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(getApplicationContext(),CalendarActivity.class));
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDayWeek(1);//设置日历第一列周一，0（周日），1（周一），2（周二），3（周三），4（周四），5（周五），6（周六），

        calendarView.setData(currentDate, startDate, endDate);
    }

}
