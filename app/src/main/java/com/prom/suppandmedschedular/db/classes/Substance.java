package com.prom.suppandmedschedular.db.classes;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.PublicData.TYPE;
import com.prom.suppandmedschedular.helper.PublicData.UNIT;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.prom.suppandmedschedular.R;
import com.prom.suppandmedschedular.db.helper.DBConstants;

public class Substance extends BasicDBEntry{

	private static final long serialVersionUID = 1L;

	private String name;
	private TYPE type;
	private int baseSteroidID;
	private int stepSize;
	private UNIT unit;
    
    public Substance()
    {
    	
    }
    
    public Substance(TYPE type, UNIT unit, String name, int baseSteroidID, int stepSize)
    {
    	this.type = type;
    	this.unit = unit;
    	this.name = name;
    	this.baseSteroidID = baseSteroidID;
    	this.stepSize = stepSize;
    }
    
    public int[] getAmounts()
    {
    	int[] amounts = new int[100];
    	for (int i = 0; i < 100; i++)
    	{
    		if (unit == UNIT.PIECES)
    			amounts[i] = i + 1;
    		else
    			amounts[i] = (i + 1) * stepSize;
    	}
    	return amounts;
    }
    
    public String[] getAmountsString(Context c)
    {
    	String[] amounts = new String[100];
    	
    	for (int i = 0; i < 100; i++)
    	{
    		if (unit == UNIT.PIECES)
    			amounts[i] = c.getResources().getQuantityString(R.plurals.amount_string_pieces, i + 1, i + 1);
    		else
    			amounts[i] = String.valueOf((i + 1) * stepSize) + c.getString(R.string.mg);
    	}
    	return amounts;
    }
    
    @Override
    public String toString()
    {
    	return name;
    }

	@Override
	protected String getDBName() {
		return DBConstants.TBL_SUBSTANCE;
	}

	@Override
	protected String[] getDBCols() {
		return DBConstants.ALL_COLS_SUBSTANCES;
	}

	@Override
	protected void setContentValues() {
		values = new ContentValues();
		values.put(DBConstants.COL_TYPE, type.ordinal());
		values.put(DBConstants.COL_UNIT, unit.ordinal());
		values.put(DBConstants.COL_NAME, name);
		values.put(DBConstants.COL_FK_BASE_STEROID, baseSteroidID);
		values.put(DBConstants.COL_STEP_SIZE, stepSize);
	}

	@Override
	public void fromCursor(Cursor cursor, boolean withSubData) {
		id = cursor.getLong(0);
		type = PublicData.TYPE.values()[cursor.getInt(1)];
		unit = PublicData.UNIT.values()[cursor.getInt(2)];
		name = cursor.getString(3);
		baseSteroidID = cursor.getInt(4);
		stepSize = cursor.getInt(5);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PublicData.TYPE getType() {
		return type;
	}

	public void setType(PublicData.TYPE type) {
		this.type = type;
	}
	
	public PublicData.UNIT getUnit() {
		return unit;
	}

	public void setUnit(PublicData.UNIT unit) {
		this.unit = unit;
	}

	public int getBaseSteroidID() {
		return baseSteroidID;
	}

	public void setBaseSteroidID(int baseSteroidID) {
		this.baseSteroidID = baseSteroidID;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}
}
