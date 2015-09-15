package com.sos.my.mycalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/7/29.
 */
public class CalendarView extends LinearLayout{

    private Context mContext;
    //外部传入的数据
    private String currentDate = "";//当前选中日期
    private String startDate = "";//可选日期的开始
    private String endDate = "";//可选日期的结束
    //需要显示的view的引用
    private Button  leftButton = null;
    private Button  rightButton = null;
    private TextView toptext = null;
    private GridView gridView = null;
    private LinearLayout weekLinearLayout = null;

    private CalendarAdapter2 calV = null;
    private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;

    //日历第一列星期几
    private int dayWeek = 1;
    private static String week[] = {"日","一","二","三","四","五","六"};

    public CalendarView(Context context) {
        super(context);
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }
    //设置日历第一列是周几
    public void setDayWeek(int dayWeek) {
        this.dayWeek = dayWeek;
    }

    public void setData(String currentDate, String startDate, String endDate) {
        this.currentDate = currentDate;
        this.startDate = startDate;
        this.endDate = endDate;
        initView();

    }
    //加载view
    public void initView() {
        removeAllViews();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.calendar_view, null);
        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton);
        toptext = (TextView) rootView.findViewById(R.id.toptext);
        weekLinearLayout = (LinearLayout) rootView.findViewById(R.id.week);
        creatWeekView();
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        addView(rootView);
        initData();
    }

    //创建星期显示
    private void creatWeekView() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);

        for(int i = dayWeek; i < week.length; i++) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(params);
            textView.setTextColor(Color.BLACK);
            textView.setText(week[i]);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            weekLinearLayout.addView(textView);
        }
        for(int i = 0; i < dayWeek; i++) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(params);
            textView.setTextColor(Color.BLACK);
            textView.setText(week[i]);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            weekLinearLayout.addView(textView);
        }

    }

    //初始化时间
    private void initData() {

        initDate();
        //创建Adapter
        calV = new CalendarAdapter2(mContext, dayWeek);
        calV.setData(jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);
        gridView.setAdapter(calV);
        //设置日历头部
        addTextToTopTextView(toptext);
        //设置事件监听
        setListener();

    }

    private void setListener() {
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				if(!isLeftMove()) {
//					return;
//				}
                moveRight();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				if(!isRightMove()) {
//					return;
//				}
                moveLeft();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // gridView中的每一个item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                if (startPosition <= position && position <= endPosition) {
                    String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
                    // String scheduleLunarDay =
                    // calV.getDateByClickItem(position).split("\\.")[1];
                    // //这一天的阴历
                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
                    String week = "";

                    // 直接跳转到需要添加日程的界面

                    // 得到这一天是星期几
                    switch (position % 7) {
                        case 0:
                            week = "星期日";
                            break;
                        case 1:
                            week = "星期一";
                            break;
                        case 2:
                            week = "星期二";
                            break;
                        case 3:
                            week = "星期三";
                            break;
                        case 4:
                            week = "星期四";
                            break;
                        case 5:
                            week = "星期五";
                            break;
                        case 6:
                            week = "星期六";
                            break;
                    }
                    if (null != calV.getRangeDate() && calV.getRangeDate().contains(position)) {
                        currentDate = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
                        calV.setSelectDay(position);
//                        Toast.makeText(mContext, "年：" + scheduleYear + "-月:" + scheduleMonth + "-日:" + scheduleDay + "-" + week, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void moveLeft() {
        // 向左滑动
        jumpMonth++; // 下一个月
        //设置上次选中的日期
        calV.refreshData(jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);

        addTextToTopTextView(toptext);

        //左右button的显示
//		isLeftMove();
//		isRightMove();
    }

    private void moveRight() {
        // 向右滑动
        jumpMonth--; // 上一个月
        //设置上次选中的日期
        calV.refreshData(jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);

        addTextToTopTextView(toptext);

        //左右button的显示
//		isLeftMove();
//		isRightMove();
    }

    //是否可以移动
    private boolean isLeftMove() {
        if(TextUtils.isEmpty(startDate)) {
            return true;
        } else {
            String[] split = startDate.split("-");
            int startYear = Integer.parseInt(split[0]);
            int startMonth = Integer.parseInt(split[1]);
            if(year_c >= startYear && (month_c + jumpMonth) > startMonth) {
                leftButton.setVisibility(View.VISIBLE);
                return true;
            } else {
                leftButton.setVisibility(View.INVISIBLE);
                return false;
            }
        }

    }

    //是否可以移动
    private boolean isRightMove() {
        if(TextUtils.isEmpty(endDate)) {
            return true;
        } else {
            String[] split = endDate.split("-");
            int endYear = Integer.parseInt(split[0]);
            int endMonth = Integer.parseInt(split[1]);
            if(year_c <= endYear && (month_c + jumpMonth) < endMonth) {
                rightButton.setVisibility(View.VISIBLE);
                return true;
            } else {
                rightButton.setVisibility(View.INVISIBLE);
                return false;
            }
        }

    }

    // 添加头部的年份 闰哪月等信息
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("年").append(calV.getShowMonth()).append("月").append("\t");
        if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
            textDate.append("闰").append(calV.getLeapMonth()).append("月").append("\t");
        }
        textDate.append(calV.getAnimalsYear()).append("年").append("(").append(calV.getCyclical()).append("年)");
        view.setText(textDate);
        view.setTextColor(Color.BLACK);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    //初始化时间
    private void initDate() {
        if(TextUtils.isEmpty(currentDate)) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            currentDate = sdf.format(date); // 当期日期
        }
        splitDate(currentDate);
    }

    private void splitDate(String currentDate) {
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    static class DensityUtil {

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         */
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }

}
