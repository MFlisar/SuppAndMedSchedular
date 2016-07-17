package com.prom.suppandmedschedular.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.preference.PreferenceManager;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;

public class Functions {
	
	public static boolean isGerman(Context c)
	{
		return c.getResources().getConfiguration().locale.getLanguage().equals(Locale.GERMAN.getLanguage());
	}
	
	public static Date getFirstWeekDayDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 0)
			dayOfWeek = 6;
		else
			dayOfWeek -= 1;
		cal.add(Calendar.DATE, -dayOfWeek);
		return cal.getTime();
	}
	
	public static int getDaysBetweenDates(Date d1, Date d2)
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND,0);
        cal2.setTime(d2);
		cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND,0);
		return (int)((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 60 * 60 * 24));
	}
	
	public static int getMaxWeeksFromPreferences(Context c)
	{
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(c).getString("prefMaxWeekCount", "50"));
	}
	
	public static ReminderTime getStandardReminderDateFromPreferences(Context c)
	{
		int hour = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(c).getString("prefHour", "8"));
		int minute = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(c).getString("prefMinute", "0"));
		return new ReminderTime(hour, minute);
	}
	
	public static int getMaxFrontloadDaysPreferences(Context c)
	{
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(c).getString("prefMaxFrontloadDay", "7"));
	}
	
	public static boolean getBoolVibrateFromSettings(Context c)
	{
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("prefRemindWithVibrate", true);
	}
	
	public static boolean getBoolSoundFromSettings(Context c)
	{
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("prefRemindWithSound", true);
	}
	
	public static Calendar getReminderDate(Context c, Date startDate, SubstanceToTake substanceToTake, int dayFromStartDate)
	{
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, dayFromStartDate);
        
        calendar.set(Calendar.HOUR_OF_DAY, substanceToTake.getReminderTime().getHour());
        calendar.set(Calendar.MINUTE, substanceToTake.getReminderTime().getMinutes());

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
	}
}
