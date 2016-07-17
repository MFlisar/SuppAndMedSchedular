package com.prom.suppandmedschedular.db.classes;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.helper.DBConstants;

public abstract class BasicDBEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected long id = -1;	
	protected transient ContentValues values = null;
	
	public long getID() { return id; }
	public void setID(long id) { this.id = id; }
	
	//---------------
	// Vorimplementierte Funktionen
	//---------------
	
	public void create()
	{
		setContentValues();
		id = StaticDatabase.getDataSource().getDatabase().insert(getDBName(), null, values);
		System.out.println("Created id = " + id + "; Object: " + this);
	}
	
	public void update()
	{
		if (id == -1)
			create();
		else
		{
			setContentValues();
			System.out.println("Updated id = " + id + "; Object: " + this);
			StaticDatabase.getDataSource().getDatabase().update(getDBName(), values, DBConstants.COL_ID + " = " + id, null);
		}
	}
	
	public void fromCursor(Cursor cursor)
	{
		fromCursor(cursor, true);
	}
	
	public void delete()
	{
		System.out.println("Deleted id = " + id + "; Object: " + this);
		StaticDatabase.getDataSource().getDatabase().delete(getDBName(), DBConstants.COL_ID + " = " + id, null);
	}
	
	public void load(long id)
	{
		load(id, true);
	}
	
	public void load(long id, boolean withSubData)
	{
		Cursor cursor = getSelectSingleCursor(id);
		cursor.moveToFirst();
		fromCursor(cursor, withSubData);
		cursor.close();
	}
	
	public void reload(boolean withSubData)
	{
		load(id, withSubData);
	}

	// Eventuell �berschreiben, falls ein Join mit einer anderen Tabelle notwendig ist
	protected Cursor getSelectSingleCursor(long id)
	{
		return StaticDatabase.getDataSource().getDatabase().query(getDBName(), getDBCols(), DBConstants.COL_ID + "=" + id, null, null, null, null);
	}
	
	//---------------
	// Abstrakte Funktionen
	//---------------
	
	public abstract String toString();
	
	protected abstract String getDBName();
	protected abstract String[] getDBCols();
	
	protected abstract void setContentValues();
	
	public abstract void fromCursor(Cursor cursor, boolean withSubData);
}
