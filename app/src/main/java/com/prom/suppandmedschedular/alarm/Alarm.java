package com.prom.suppandmedschedular.alarm;

import com.prom.suppandmedschedular.helper.Functions;
import com.prom.suppandmedschedular.helper.PublicData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.prom.suppandmedschedular.activities.NotificationActivity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.prom.suppandmedschedular.R;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.AlarmEntry;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;

public class Alarm extends BroadcastReceiver 
{    
    @Override
    public void onReceive(Context context, Intent intent) 
    {   
    	int count = intent.getExtras().getInt(context.getString(R.string.I_KEY_COUNT));
    	int alarm_id = intent.getExtras().getInt(context.getString(R.string.I_KEY_ALARM_ID));
    	long plan_id = intent.getExtras().getLong(context.getString(R.string.I_KEY_PLAN_ID));
    	String[] texts = new String[count];
    	for (int i = 0; i < count; i++)
    		texts[i] = intent.getExtras().getString(context.getString(R.string.I_KEY_ENTRY_PREFIX) + i);
    	System.out.println("Receive Alarm_id: " + alarm_id + ", count: " + count);
    	createStatusNotification(context, texts, plan_id, alarm_id);
    }
    
    public static void createAlarm(Context c, long plan_id, List<SubstanceToTake> substances, List<Float> amounts, Date startDate, int day)
    {  			
    	Calendar calToday = Calendar.getInstance();
        calToday.setTime(new Date());
        
        boolean[] timeIsInFuture = new boolean[substances.size()];
        Calendar[] calendars = new Calendar[substances.size()];
    	for (int i = 0; i < substances.size(); i++)
    	{
    		Calendar calendar = Functions.getReminderDate(c, startDate, substances.get(i), day);
    		calendars[i] = calendar;
    		// checken ob Alarm in der Zukunft
            if (calendar.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
        		calendar.get(Calendar.MONTH) == calToday.get(Calendar.MONTH) &&
    			calendar.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR) &&
            	calendar.getTimeInMillis() < calToday.getTimeInMillis())
            	timeIsInFuture[i] = true;
            else
            	timeIsInFuture[i] = false;
    	}
        
        // Erinnerungen zur gleichen Zeit zusammenfassen
    	List<AlarmEntry> alarmEntries = new ArrayList<AlarmEntry>();
    	AlarmEntry entry;
    	for (int i = 0; i < substances.size(); i++)
    	{
    		if (!timeIsInFuture[i])
    		{
	    		int timeFoundAtIndex = -1;
	    		for (int j = 0; j < alarmEntries.size(); j++)
	    			if (alarmEntries.get(j).reminderTime == substances.get(i).getReminderTime())
	    			{
	    				timeFoundAtIndex = j;
	    				break;
	    			}
	    		
	    		if (timeFoundAtIndex == -1)
	    		{
	    			int id = StaticDatabase.getDataSource().getMaxAlarmID() + 1;
	    			entry = new AlarmEntry(id, plan_id);
	    			entry.reminderTime = substances.get(i).getReminderTime();
	    			entry.calendar = calendars[i];
	    			entry.texts = new ArrayList<String>();
	    			entry.texts.add(substances.get(i).getSubstance().getName() + ": " + substances.get(i).getAmountOfDay(day) + "mg");
	    			entry.create(); // Daten f�r Datenbank sind schon vollkommen vorhanden!
	    			alarmEntries.add(entry);
	    		}
	    		else
	    		{
	    			alarmEntries.get(timeFoundAtIndex).texts.add(substances.get(i).getSubstance().getName() + ": " + substances.get(i).getAmountOfDay(day) + "mg");
	    		}
    		}
    	}
    	
    	// Alarme erstellen
    	Intent intent;
    	for (int i = 0; i < alarmEntries.size(); i++)
    	{
	    	intent = new Intent(c, Alarm.class);
	    	intent.putExtra(c.getString(R.string.I_KEY_ALARM_ID), alarmEntries.get(i).alarm_id);
	    	intent.putExtra(c.getString(R.string.I_KEY_PLAN_ID), alarmEntries.get(i).plan_id);
	    	intent.putExtra(c.getString(R.string.I_KEY_COUNT), alarmEntries.get(i).texts.size());
	    	for (int j = 0; j < alarmEntries.get(i).texts.size(); j++)
	    		intent.putExtra(c.getString(R.string.I_KEY_ENTRY_PREFIX) + j, alarmEntries.get(i).texts.get(j));
	        PendingIntent sender = PendingIntent.getBroadcast(c, alarmEntries.get(i).alarm_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	
	        System.out.println("Creater Alarm_id: " + alarmEntries.get(i).alarm_id + ", day: " + day + ", date: " + PublicData.dateDBFormat.format(alarmEntries.get(i).calendar.getTime()));
	
	        // Schedule the alarm!
	        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
	        am.set(AlarmManager.RTC_WAKEUP, alarmEntries.get(i).calendar.getTimeInMillis(), sender);
    	}
    }
    
    public static void removeAlarm(Context c, long plan_id)
    {
    	AlarmEntry[] alarmEntries = StaticDatabase.getDataSource().getAllAlarmEntries(plan_id);
    	for (int i = 0; i < alarmEntries.length; i++)
    	{
    		((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(alarmEntries[i].alarm_id);
    		// Alarm aus Datenbank entfernen
    		alarmEntries[i].delete();
    	
	    	Intent intent = new Intent(c, Alarm.class);
	    	PendingIntent sender = PendingIntent.getBroadcast(c, alarmEntries[i].alarm_id, intent, 0);
	    	AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
	        am.cancel(sender);
    	}
    }
    
    public static void createStatusNotification(Context c, String[] texts, long plan_id, int alarm_id)
    {
		Intent intent = new Intent(c, NotificationActivity.class);
		intent.putExtra(c.getString(R.string.I_KEY_ALARM_ID), alarm_id);
		intent.putExtra(c.getString(R.string.I_KEY_PLAN_ID), plan_id);
		intent.putExtra(c.getString(R.string.I_KEY_COUNT), texts.length);
		for (int i = 0; i < texts.length; i++)
			intent.putExtra(c.getString(R.string.I_KEY_ENTRY_PREFIX) + i, texts[i]);
		PendingIntent contentIntent = PendingIntent.getActivity(c, alarm_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    	String title = c.getString(R.string.app_name) + ": " + c.getString(R.string.intake_reminder);
    	String message =  texts.length + " " + c.getResources().getQuantityString(R.plurals.substance, texts.length) + " " + c.getResources().getQuantityString(R.plurals.have_to_be_taken_today, texts.length);
    	for (int i = 0; i < texts.length; i++)
    	{
    		if (i > 0)
    			message += "\n";
    		message += texts[i];
    	}
    	NotificationManager manager = (NotificationManager)c. getSystemService(Context.NOTIFICATION_SERVICE);
//    	Notification notification = new Notification(R.drawable.testo, title, System.currentTimeMillis());

		NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
				.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.testo)
				.setContentTitle(title)
				.setContentText(message);
		Notification notification = builder.build();

    	if (Functions.getBoolSoundFromSettings(c))
    		notification.defaults |= Notification.DEFAULT_SOUND;
    	if (Functions.getBoolVibrateFromSettings(c))
    		notification.defaults |= Notification.DEFAULT_VIBRATE;
    	notification.defaults |= Notification.DEFAULT_LIGHTS;
    	
    	System.out.println("Alarm id in createStatusNotification: " + alarm_id + ", message: " + message);
    	





//    	notification.setLatestEventInfo(c, title, message, contentIntent);
    	manager.notify(alarm_id, notification);
    }
}
