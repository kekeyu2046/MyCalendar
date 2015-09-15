package com.sos.my.mycalendar;


import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 日历gridview中的每一个item显示的textview
 * @author jack_peng
 *
 */
public class CalendarAdapter2 extends BaseAdapter {

	private boolean isLeapyear = false;  //是否为闰年
	private int daysOfMonth = 0;      //某月的天数
	private int dayOfWeek = 0;        //具体某一天是星期几
	private int lastDaysOfMonth = 0;  //上一个月的总天数
	private Context context;
	private static final int MAX_DAY_COUNT = 42;//最大格子数量
	private String[] dayNumber = new String[MAX_DAY_COUNT];  //一个gridview中的日期存入此数组中
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;

	//当前年月日
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;     //用于标记当天

	private String showYear = "";   //用于在头部显示的年份
	private String showMonth = "";  //用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = "";   //闰哪一个月
	private String cyclical = "";   //天干地支
	//系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";

	//点击受限范围
	private long startDate;
	private long endDate;
	private ArrayList<Integer> rangeDate = new ArrayList<>();//受限日期范围角标

	//选中哪一天
	private String selectDay = "";
	private int selectDayFlag = -1;

	private int weekDay = 1;

	public CalendarAdapter2(){
		Date date = new Date();
		sysDate = sdf.format(date);  //当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	public CalendarAdapter2(Context context, int weekDay){
		this();
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.weekDay = weekDay;
	}

	public void setData(int jumpMonth, int jumpYear, int year_c, int month_c, int day_c, String startDate, String endDate, String selectDay) {
		this.selectDay = selectDay;
		try {
			if(TextUtils.isEmpty(startDate)) {
				this.startDate = 0;
			} else {
				Date start = sdf.parse(startDate);
				this.startDate = start.getTime();
			}
			if(TextUtils.isEmpty(endDate)) {
				this.endDate = 0;
			} else {
				Date end = sdf.parse(endDate);
				this.endDate = end.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}


		int stepYear = year_c+jumpYear;
		int stepMonth = month_c+jumpMonth ;
		if(stepMonth > 0){
			//往下一个月滑动
			if(stepMonth%12 == 0){
				stepYear = year_c + stepMonth/12 -1;
				stepMonth = 12;
			}else{
				stepYear = year_c + stepMonth/12;
				stepMonth = stepMonth%12;
			}
		}else{
			//往上一个月滑动
			stepYear = year_c - 1 + stepMonth/12;
			stepMonth = stepMonth%12 + 12;
			if(stepMonth%12 == 0){

			}

		}

		currentYear = String.valueOf(stepYear);;  //得到当前的年份
		currentMonth = String.valueOf(stepMonth);  //得到本月 （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = String.valueOf(day_c);  //得到当前日期是哪天

		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));


	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar, null);
		 }
		convertView.setBackgroundResource(R.mipmap.gridview_bk);
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		textView.setBackgroundResource(R.mipmap.gridview_bk);
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];
		//Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
		//textView.setTypeface(typeface);
		SpannableString sp = new SpannableString(d+"\n"+dv);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(dv != null || dv != ""){
            sp.setSpan(new RelativeSizeSpan(0.75f), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		//sp.setSpan(new ForegroundColorSpan(Color.MAGENTA), 14, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);


		int firstDayIndex = dayOfWeek >= weekDay ? (dayOfWeek-weekDay): (7-weekDay+dayOfWeek);
		if (position >= firstDayIndex && position < firstDayIndex+daysOfMonth) {
			// 当前月信息显示
			textView.setTextColor(Color.GRAY);// 当月字体设黑
			//设置可选范围的背景
			if(null != rangeDate && rangeDate.size() > 0 && rangeDate.contains(position)) {
				textView.setBackgroundColor(Color.WHITE);
			}

		} else {
			//非当前月信息不显示
			textView.setText("");
		}

		if(currentFlag == position){
			//设置当天的背景
			textView.setTextColor(Color.WHITE);
			textView.setBackgroundResource(R.mipmap.current_day_bgc);
		}

		//设置选中日期的背景
		if(selectDayFlag == position) {
			textView.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}
		return convertView;
	}
	
	//得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month){
		isLeapyear = sc.isLeapYear(year);              //是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);  //某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month);      //某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数
//		Log.d("DAY", isLeapyear + " ======  " + daysOfMonth + "  ============  " + dayOfWeek + "  =========   " + lastDaysOfMonth);
		getweek(year,month);
	}
	
	//将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		String lunarDay = "";

		//计算第一天所在的索引值
		int firstDayIndex = dayOfWeek >= weekDay ? (dayOfWeek-weekDay): (7-weekDay+dayOfWeek);
		//处理本月的数据
		for(int i = firstDayIndex;i< firstDayIndex + daysOfMonth;i++){
			String day = String.valueOf(i - firstDayIndex + 1);   //得到的日期
			lunarDay = lc.getLunarDate(year, month, i - firstDayIndex + 1,false);
			dayNumber[i] = (i - firstDayIndex + 1)+"."+lunarDay;
			//记录系统今天的日期
			if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){

				currentFlag = i;
			}

			long matchDate = 0 ;
			try {
				Date parse = sdf.parse(year + "-" + month + "-" + day);
				matchDate = parse.getTime();
				if(matchDate > 0) {
					if(startDate <= 0 && endDate > 0) {
						if(matchDate <= endDate) {
							//没有起始限制，记录选择日期范围角标
							rangeDate.add(i);
						}
					}
					if(endDate <= 0 && startDate > 0) {
						if(matchDate >= startDate) {
							//没有结束限制，记录选择日期范围角标
							rangeDate.add(i);
						}
					}
					if(startDate > 0 && endDate > 0) {
						if(matchDate > 0 && ( matchDate >= startDate && matchDate <= endDate)) {
							//有起始和结束限制，记录选择日期范围角标
							rangeDate.add(i);
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if(!TextUtils.isEmpty(selectDay)) {
				String[] split = selectDay.split("-");
				if(split[0].equals(String.valueOf(year)) && split[1].equals(String.valueOf(month)) && split[2].equals(day)){
					//记录选中日期角标
					selectDayFlag = i;
				}
			}

			setShowYear(String.valueOf(year));
			setShowMonth(String.valueOf(month));
			setAnimalsYear(lc.animalsYear(year));
			setLeapMonth(lc.leapMonth == 0?"": String.valueOf(lc.leapMonth));
			setCyclical(lc.cyclical(year));


		}
		//处理前一个月的数据

		for(int i = 0;i<firstDayIndex;i++){
			lunarDay = lc.getLunarDate(year, month-1, lastDaysOfMonth - firstDayIndex + i  + 1,false);
			dayNumber[i] = (lastDaysOfMonth - firstDayIndex + i  + 1)+"."+lunarDay;
		}


		//处理下一个月的数据
		for(int i = 0;i<MAX_DAY_COUNT - daysOfMonth - firstDayIndex;i++){
			lunarDay = lc.getLunarDate(year, month+1, i+1,false);
			dayNumber[firstDayIndex + daysOfMonth+i] = (i+1)+"."+lunarDay;

		}




		
//		for (int i = 0; i < dayNumber.length; i++) {
//			// 周一
////			if(i<7){
////				dayNumber[i]=week[i]+"."+" ";
////			} else
//			if(i < dayOfWeek){  //前一个月
//				int temp = lastDaysOfMonth - dayOfWeek+1;
//				lunarDay = lc.getLunarDate(year, month-1, temp+i,false);
//				dayNumber[i] = (temp + i)+"."+lunarDay;
//			}else if(i < daysOfMonth + dayOfWeek){   //本月
//				String day = String.valueOf(i - dayOfWeek + 1 );   //得到的日期
//				lunarDay = lc.getLunarDate(year, month, i-dayOfWeek+1,false);
//				dayNumber[i] = i-dayOfWeek+1+"."+lunarDay;
//				//对于当前月才去标记当前日期
//				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
//					//笔记当前日期
//					currentFlag = i;
//				}
//
//				long matchDate = 0 ;
//				try {
//					Date parse = sdf.parse(year + "-" + month + "-" + day);
//					matchDate = parse.getTime();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				if(startDate > 0 && endDate > 0) {
//					if(matchDate > 0 && ( matchDate >= startDate && matchDate <= endDate)) {
//						//记录选择日期范围角标
//						rangeDate.add(i);
//					}
//
//				}
//
//				if(!TextUtils.isEmpty(selectDay)) {
//					String[] split = selectDay.split("-");
//					if(split[0].equals(String.valueOf(year)) && split[1].equals(String.valueOf(month)) && split[2].equals(day)){
//						//记录选中日期角标
//						selectDayFlag = i;
//					}
//				}
//
//
//
//				setShowYear(String.valueOf(year));
//				setShowMonth(String.valueOf(month));
//				setAnimalsYear(lc.animalsYear(year));
//				setLeapMonth(lc.leapMonth == 0?"": String.valueOf(lc.leapMonth));
//				setCyclical(lc.cyclical(year));
//			}else{   //下一个月
//				lunarDay = lc.getLunarDate(year, month+1, j,false);
//				dayNumber[i] = j+"."+lunarDay;
//				j++;
//			}
//		}
//
//        String abc = "";
//        for(int i = 0; i < dayNumber.length; i++){
//        	 abc = abc+dayNumber[i]+":";
//        }
//		Log.d("DAYNUMBER", abc);


	}
	
	/**
	 * 点击每一个item时返回item中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return dayOfWeek;
	}
	
	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek+daysOfMonth)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}

	/**
	 * 获取可点击日期的postion
	 * @return
	 */
	public ArrayList<Integer> getRangeDate() {
		return rangeDate;
	}

	public void setSelectDay(int day) {
		selectDayFlag = day;
		notifyDataSetChanged();
	}

	//刷新日期数据
	public void refreshData(int jumpMonth, int jumpYear, int year_c, int month_c, int day_c, String startDate, String endDate, String currentDate) {
		rangeDate = new ArrayList<>();
		currentFlag = -1;
		selectDayFlag = -1;
		setData(jumpMonth, jumpYear, year_c, month_c, day_c, startDate, endDate, currentDate);
		notifyDataSetChanged();
	}
}
