package com.prom.suppandmedschedular.db.classes;

import com.prom.suppandmedschedular.helper.ReminderTime;

import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import com.prom.suppandmedschedular.db.helper.DBConstants;

public class AlarmEntry extends BasicDBEntry{

	private static final long serialVersionUID = 1L;

	public int alarm_id;
    public long plan_id;
    
    // zus�tzliche Daten...
    public List<String> texts;
    public ReminderTime reminderTime;
    public Calendar calendar;
    
    public AlarmEntry()
    {
    	
    }
    
    public static void add(String substance, int amount)
    {
    	
    }
    
    public AlarmEntry(int alarm_id, long plan_id)
    {
    	this.alarm_id = alarm_id;
    	this.plan_id = plan_id;
    }
    
    @Override
    public void create()
    {
    	super.create();
    }
    @Override
    public String toString()
    {
    	return "Alarm-ID: " + alarm_id + ", plan-ID: " + plan_id;
    }

	@Override
	protected String getDBName() {
		return DBConstants.TBL_ALARMS;
	}

	@Override
	protected String[] getDBCols() {
		return DBConstants.ALL_COLS_ALARM;
	}

	@Override
	protected void setContentValues() {
		values = new ContentValues();
		values.put(DBConstants.COL_ALARM_ID, alarm_id);
		values.put(DBConstants.COL_FK_plan, plan_id);
	}

	@Override
	public void fromCursor(Cursor cursor, boolean withSubData) {
		id = cursor.getLong(0);
		alarm_id = cursor.getInt(1);
		plan_id = cursor.getLong(2);
	}
	
	public static String getSelectAllString()
	{
		String query = "SELECT * FROM " + DBConstants.TBL_ALARMS;
		return query;
	}
}
