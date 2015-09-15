package com.sos.my.mycalendar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日历显示activity
 * 
 * @author jack_peng
 * 
 */
public class CalendarActivity extends Activity {

	private ViewFlipper flipper = null;
	private CalendarAdapter calV = null;
	private GridView gridView = null;
	private Drawable draw = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private TextView topText = null;
	private Button leftButton;
	private Button rightButton;

	private String currentDate = "2015-7-30";//当前选中日期
	private static final String startDate = "2015-7-26";//可选日期的开始
	private static final String endDate= "2015-8-15";//可选日期的结束

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initData();

		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);

		addGridView();
		gridView.setAdapter(calV);
		// flipper.addView(gridView);
		flipper.addView(gridView, 0);

		topText = (TextView) findViewById(R.id.toptext);
		addTextToTopTextView(topText);

		leftButton = (Button) findViewById(R.id.leftButton);
		rightButton = (Button) findViewById(R.id.rightButton);
		//初始化左右button的显示
//		isLeftMove();
//		isRightMove();

		setListener();
	}

	//初始化当前日期
	private void initData() {
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

	private void moveLeft() {
		int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
		// 像左滑动
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月
		//设置上次选中的日期
		calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);

		gridView.setAdapter(calV);
		// flipper.addView(gridView);
		addTextToTopTextView(topText);
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		this.flipper.showNext();
		flipper.removeViewAt(0);
		//左右button的显示
//		isLeftMove();
//		isRightMove();
	}

	private void moveRight() {
		int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
		// 向右滑动
		addGridView(); // 添加一个gridView
		jumpMonth--; // 上一个月
		//设置上次选中的日期
		calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);

		gridView.setAdapter(calV);
		gvFlag++;
		addTextToTopTextView(topText);
		// flipper.addView(gridView);
		flipper.addView(gridView, gvFlag);

		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		this.flipper.showPrevious();
		flipper.removeViewAt(0);
		//左右button的显示
//		isLeftMove();
//		isRightMove();
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

	// 添加gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);

		if (Width == 480 && Height == 800) {
			gridView.setColumnWidth(69);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundResource(R.mipmap.gridview_bk);


		gridView.setOnItemClickListener(new OnItemClickListener() {
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
						currentDate = scheduleYear+"-"+scheduleMonth+"-"+scheduleDay;
						calV.setSelectDay(position);
						Toast.makeText(getApplicationContext(), "年：" + scheduleYear + "-月:" + scheduleMonth + "-日:" + scheduleDay + "-" + week, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		gridView.setLayoutParams(params);

	}

}