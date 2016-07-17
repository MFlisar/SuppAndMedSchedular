package com.prom.suppandmedschedular.db.helper;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.PublicData.TYPE;
import com.prom.suppandmedschedular.helper.PublicData.UNIT;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.prom.suppandmedschedular.db.classes.AlarmEntry;
import com.prom.suppandmedschedular.db.classes.Plan;
import com.prom.suppandmedschedular.db.classes.Substance;

public class MyDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLHelper dbHelper;

	public MyDataSource(Context context) 
	{
		dbHelper = new MySQLHelper(context);
	}

	public void open() throws SQLiteException 
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close() 
	{
		dbHelper.close();
	}
	
	public SQLiteDatabase getDatabase()
	{
		return database;
	}
	
	public void createInitData()
	{
		if (getAllSubstances().length != 0)
			return;
		
		// Standarddaten erstellen
		Substance[] substances = new Substance[5];
		substances[0] = new Substance(TYPE.STEROID, UNIT.MG, "Testo E", PublicData.TESTO_E, 50);
		substances[1] = new Substance(TYPE.STEROID, UNIT.MG, "Testo P",  PublicData.TESTO_P, 50);
		substances[2] = new Substance(TYPE.STEROID, UNIT.MG, "Deca", PublicData.DECA, 50);
		substances[3] = new Substance(TYPE.STEROID, UNIT.MG, "Clomid", PublicData.NONE, 50);
		substances[4] = new Substance(TYPE.SUPP, UNIT.PIECES, "Zink 50er", PublicData.NONE, 0);
		
		substances[0].create();
		substances[1].create();
		substances[2].create();
		substances[3].create();
		substances[4].create();
	}
	
	public Substance[] getAllSubstances()
	{
		List<Substance> substances = new ArrayList<Substance>();
		Cursor cursor = database.query(DBConstants.TBL_SUBSTANCE, DBConstants.ALL_COLS_SUBSTANCES, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			Substance substance = new Substance();
			substance.fromCursor(cursor);
			substances.add(substance);
			cursor.moveToNext();
		}
		cursor.close();
		return (Substance[])substances.toArray(new Substance[substances.size()]);
	}
	
	public boolean checkIfSubstanceIsUsed(long substance_id)
	{
		Cursor cursor = database.query(DBConstants.TBL_SUBSTANCE_TO_TAKE, new String[]{DBConstants.COL_FK_SUBSTANCE}, DBConstants.COL_FK_SUBSTANCE + "=" + substance_id, null, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast())
			return true;
		cursor.close();
		return false;
	}
	
	public Plan getPlan(long id)
	{
		Cursor cursor = database.query(DBConstants.TBL_PLANS, DBConstants.ALL_COLS_PLANS, DBConstants.COL_ID + "=" + id, null, null, null, null);
		cursor.moveToFirst();
		Plan plan = new Plan();
		if (!cursor.isAfterLast())
			plan.fromCursor(cursor);
		cursor.close();
		return plan;
	}
	
	public Plan[] getAllPlans()
	{
		List<Plan> plans = new ArrayList<Plan>();
		Cursor cursor = database.rawQuery(Plan.getSelectAllString(), new String[]{});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			Plan plan = new Plan();
			plan.fromCursor(cursor);
			plans.add(plan);
			cursor.moveToNext();
		}
		cursor.close();
		return (Plan[])plans.toArray(new Plan[plans.size()]);
	}
	
	public int getMaxAlarmID()
	{
		String query = AlarmEntry.getSelectAllString();
		query = query.replace("*", "max(" + DBConstants.COL_ALARM_ID + ")");
		Cursor cursor = database.rawQuery(query, new String[]{});
		cursor.moveToFirst();
		int max = 0;
		if (!cursor.isAfterLast())
			max = cursor.getInt(0);
		cursor.close();
		return max;
	}
	
	public AlarmEntry[] getAllAlarmEntries(long plan_id)
	{
		List<AlarmEntry> alarms = new ArrayList<AlarmEntry>();
		Cursor cursor = database.query(DBConstants.TBL_ALARMS, DBConstants.ALL_COLS_ALARM, DBConstants.COL_FK_plan + "=" + plan_id, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			AlarmEntry alarm = new AlarmEntry();
			alarm.fromCursor(cursor);
			alarms.add(alarm);
			cursor.moveToNext();
		}
		return (AlarmEntry[])alarms.toArray(new AlarmEntry[alarms.size()]);
	}
	
	public AlarmEntry getAlarm(int alarm_id)
	{
		Cursor cursor = database.query(DBConstants.TBL_ALARMS, DBConstants.ALL_COLS_ALARM, DBConstants.COL_ALARM_ID + "=" + alarm_id, null, null, null, null);
		cursor.moveToFirst();
		AlarmEntry alarm = new AlarmEntry();
		if (!cursor.isAfterLast()) 
			alarm.fromCursor(cursor);
		cursor.close();
		return alarm;
	}
}
