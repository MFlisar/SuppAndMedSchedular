package com.prom.suppandmedschedular.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLHelper extends SQLiteOpenHelper 
{
	protected MySQLHelper(Context context) 
	{
		super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		String[] createStrings = DBConstants.getAllTableCreateStrings();
		for (int i = 0; i < createStrings.length; i++)
			database.execSQL(createStrings[i]);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		String[] tableNames = DBConstants.getAllTableNames();
		for (int i = 0; i < tableNames.length; i++)
			db.execSQL("DROP TABLE IF EXISTS " + tableNames[i]);
		onCreate(db);
	}

}