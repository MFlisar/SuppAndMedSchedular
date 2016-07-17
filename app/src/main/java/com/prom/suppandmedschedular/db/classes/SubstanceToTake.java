package com.prom.suppandmedschedular.db.classes;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.ReminderTime;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;

import com.jjoe64.graphview.GraphView.GraphViewData;

import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.helper.DBConstants;


public class SubstanceToTake extends BasicDBEntry{

	private static final long serialVersionUID = 1L;
	
	private Substance substance;
	private ReminderTime reminderTime;
	private int startWeek;
	private int startWeekDay;
	private int endWeek;
	private int endWeekDay;
	private int amount;
	private int frontloadDay;
	private int frontloadAmount;
	private float regularity;
	
	// extra Daten
	private boolean showInGraph = true;

	public SubstanceToTake()
	{
		
	}
	public SubstanceToTake(Substance substance, ReminderTime reminderTime, int startWeek, int startWeekDay, int endWeek, int endWeekDay, int amount, int frontloadDay, int frontloadAmount, float regularity)
	{
		setData(substance, reminderTime, startWeek, startWeekDay, endWeek, endWeekDay, amount, frontloadDay, frontloadAmount, regularity);
	}
	
	public void setData(Substance substance, ReminderTime reminderTime, int startWeek, int startWeekDay, int endWeek, int endWeekDay, int amount, int frontloadDay, int frontloadAmount, float regularity)
	{
		this.substance = substance;
		this.reminderTime = reminderTime;
		this.startWeek = startWeek;
		this.startWeekDay = startWeekDay;
		this.endWeek = endWeek;
		this.endWeekDay = endWeekDay;
		this.amount = amount;
		this.frontloadDay = frontloadDay;
		this.frontloadAmount = frontloadAmount;
		this.regularity = regularity;
	}
	
	public int getAmountOfDay(int day)
	{
		if (day == frontloadDay)
			return amount + frontloadAmount;
		else
			return amount;
	}
	
	public boolean isSubstanceForGraph()
	{
		return substance.getBaseSteroidID() != PublicData.NONE;
	}
	
	public Vector<GraphViewData> calcDataFullWeeks(boolean addDataForEmptyStart)
	{
		Vector<GraphViewData> data = new Vector<GraphViewData>();
		
		if (substance.getBaseSteroidID() == PublicData.NONE)
			return data;
		
		if (addDataForEmptyStart)
		{
			int emptyDaysInFront = 7 * startWeek + startWeekDay;
			for (int i = 0; i < emptyDaysInFront; i++)
				data.add(new GraphViewData(i, 0));
		}
		
		int days = (endWeek - startWeek) * 7 + (endWeekDay - startWeekDay) + 1;
		int daysToEndOfLastWeek = endWeekDay - startWeekDay;
		if (addDataForEmptyStart)
			daysToEndOfLastWeek = endWeekDay;
		int startDay = startWeek * 7 + startWeek;
		int totalEntries = days + (PublicData.bufferWeeks - 1) * 7 + daysToEndOfLastWeek + 1;
		float amountArray[][] = new float[totalEntries][totalEntries];
		float amountOfDay = 0;
		BaseSteroid base = PublicData.baseSteroids[substance.getBaseSteroidID()];
		for (int r = 0; r < totalEntries; r++)
        {
            for (int c = r; c < totalEntries; c++)	
            {
            	amountOfDay = 0;
            	if (r % regularity == 0 && r < days)
            		amountOfDay = amount;
            	if (r == frontloadDay)
            		amountOfDay += frontloadAmount;
            	float value = (float)(amountOfDay * base.getC0() * base.getkInf() / (base.getkEv() - base.getkInf()) * (Math.exp(-base.getkInf() * ((c - 0 - r + 0))) - Math.exp(-base.getkEv() * (c - 0 - r + 0))));
            	amountArray[r][c] = value;
            }
        }
		if (!addDataForEmptyStart)
			for (int r = 0; r < startWeekDay; r++)
				data.add(new GraphViewData(startDay + r, 0));
		for (int r = 0; r < totalEntries; r++)
        {
            float sum = 0;
            for (int row = 0; row <= r; row++)
            {
                sum += amountArray[row][r];
            }
            data.add(new GraphViewData(startDay + startWeekDay + r, sum));
        }
		return data;
	}
	
	public float[] getIntakeData()
	{
		int days = (endWeek - startWeek) * 7 + (endWeekDay - startWeekDay) + 1;
		float[] data = new float[days];
		for (int i = 0; i < days; i++)
		{
			data[i] = 0;
			if (i % regularity == 0)
        		data[i] = amount;
        	if (i == frontloadDay)
        		data[i] += frontloadAmount;
		}
		return data;
	}
	
	 @Override
	 public void delete()
	 {
		 
	 }

	@Override
	public String toString() {
		return substance.getName();
	}

	@Override
	protected String getDBName() {
		return DBConstants.TBL_SUBSTANCE_TO_TAKE;
	}

	@Override
	protected String[] getDBCols() {
		return DBConstants.ALL_COLS_SUBSTANCES_TO_TAKE;
	}

	@Override
	protected void setContentValues() {
		values = new ContentValues();
		values.put(DBConstants.COL_FK_SUBSTANCE, substance.getID());
		values.put(DBConstants.COL_REMINDER_TIME, reminderTime.toString());
		values.put(DBConstants.COL_START_WEEK, startWeek);
		values.put(DBConstants.COL_START_DAY, startWeekDay);
		values.put(DBConstants.COL_END_WEEK, endWeek);
		values.put(DBConstants.COL_END_DAY, endWeekDay);
		values.put(DBConstants.COL_AMOUNT, amount);
		values.put(DBConstants.COL_AMOUNT_FRONTLOAD, frontloadAmount);
		values.put(DBConstants.COL_REGULARITY, regularity);
	}

	@Override
	public void fromCursor(Cursor cursor, boolean withSubData) {
		id = cursor.getLong(0);
		reminderTime = new ReminderTime();
		reminderTime.fromString(cursor.getString(2));
		startWeek = cursor.getInt(3);
		startWeekDay = cursor.getInt(4);
		endWeek = cursor.getInt(5);
		endWeekDay = cursor.getInt(6);
		amount = cursor.getInt(7);
		frontloadDay = cursor.getInt(8);
		frontloadAmount = cursor.getInt(9);
		regularity = cursor.getInt(10);
		
		if (withSubData)
		{
			substance = new Substance();
			substance.setID(cursor.getLong(11));
			substance.setType(PublicData.TYPE.values()[cursor.getInt(12)]);
			substance.setUnit(PublicData.UNIT.values()[cursor.getInt(13)]);
			substance.setName(cursor.getString(14));
			substance.setBaseSteroidID(cursor.getInt(15));
			substance.setStepSize(cursor.getInt(16));
		}
	}
	
	@Override
	protected Cursor getSelectSingleCursor(long id)
	{
		String query = "SELECT * FROM " + DBConstants.TBL_SUBSTANCE_TO_TAKE + 
			" LEFT JOIN " + DBConstants.TBL_SUBSTANCE + 
				" ON " + DBConstants.TBL_SUBSTANCE_TO_TAKE + "." + DBConstants.COL_FK_SUBSTANCE + "=" + DBConstants.TBL_SUBSTANCE + "." + DBConstants.COL_ID +
			" WHERE " + DBConstants.TBL_SUBSTANCE_TO_TAKE + "." + DBConstants.COL_ID + "=?";
		return StaticDatabase.getDataSource().getDatabase().rawQuery(query, new String[]{String.valueOf(id)});
	}
	public Substance getSubstance() {
		return substance;
	}
	public void setSubstance(Substance substance) {
		this.substance = substance;
	}
	public ReminderTime getReminderTime() {
		return reminderTime;
	}
	public void setReminderTime(ReminderTime reminderTime) {
		this.reminderTime = reminderTime;
	}
	public int getStartWeek() {
		return startWeek;
	}
	public void setStartWeek(int startWeek) {
		this.startWeek = startWeek;
	}
	public int getStartWeekDay() {
		return startWeekDay;
	}
	public void setStartWeekDay(int startWeekDay) {
		this.startWeekDay = startWeekDay;
	}
	public int getEndWeek() {
		return endWeek;
	}
	public void setEndWeek(int endWeek) {
		this.endWeek = endWeek;
	}
	public int getEndWeekDay() {
		return endWeekDay;
	}
	public void setEndWeekDay(int endWeekDay) {
		this.endWeekDay = endWeekDay;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getFrontloadDay() {
		return frontloadDay;
	}
	public void setFrontloadDay(int frontloadDay) {
		this.frontloadDay = frontloadDay;
	}
	public int getFrontloadAmount() {
		return frontloadAmount;
	}
	public void setFrontloadAmount(int frontloadAmount) {
		this.frontloadAmount = frontloadAmount;
	}
	public float getRegularity() {
		return regularity;
	}
	public void setRegularity(float regularity) {
		this.regularity = regularity;
	}
	
	public void setShowInGraph(boolean showInGraph) {
		this.showInGraph = showInGraph;
	}
	public boolean getShowInGraph() {
		return showInGraph;
	}
}
