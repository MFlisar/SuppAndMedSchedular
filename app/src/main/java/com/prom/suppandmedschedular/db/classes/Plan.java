package com.prom.suppandmedschedular.db.classes;

import com.prom.suppandmedschedular.helper.Functions;
import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.classes.Link;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.prom.suppandmedschedular.alarm.Alarm;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.prom.suppandmedschedular.R;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.helper.DBConstants;

public class Plan extends BasicDBEntry{

	public enum INDEX_TYPE
	{
		GRAPH_RELEVANT,
		VISIBLE,
		GRAPH_RELEVANT_PARENTS_ONLY
	}
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Date date;
	private List<SubstanceToTake> substancesToTake;
	private List<Link> linkedTo;
	
	public Plan()
	{
	}
	
	public Plan(String name)
	{
		this.name = name;
		this.date = null;
		substancesToTake = new ArrayList<SubstanceToTake>();
		linkedTo = new ArrayList<Link>();
	}
	
	public String getStatus(Context c)
	{
		if (date == null)
			return c.getString(R.string.status_deactivated);
		
		int daysToStart = Functions.getDaysBetweenDates(new Date(), date);
		int daysToEnd = daysToStart + getMaxWeekNumbers() * 7;
		if (daysToEnd < 0)
			return c.getString(R.string.status_finished);
		else if (daysToStart < getMaxWeekNumbers() * 7)
			return c.getString(R.string.status_running);
		else
			return c.getString(R.string.status_future);
	}
	
	public void addSubstanceToTake(SubstanceToTake substanceToTake)
	{
		substancesToTake.add(substanceToTake);
	}
	
	public SubstanceToTake[] removeSubstanceToTake(List<Integer> indices)
	{
		Collections.sort(indices);
		SubstanceToTake[] stt = new SubstanceToTake[indices.size()];
		for (int i = indices.size() - 1; i >= 0 ; i--)
			stt[i] = removeSubstanceToTake(indices.get(i));
		return stt;
	}
	
	public void addLink(Integer parent, Integer child)
	{
		for (int i = 0; i < linkedTo.size(); i++)
			if (linkedTo.get(i).getParent() == parent)
			{
				linkedTo.get(i).addChildIfNotExists(child);
				return;
			}
		Link link = new Link(parent);
		link.addChildIfNotExists(child);
		linkedTo.add(link);
	}
	
	public void removeLink(Integer parent, Integer child)
	{
		for (int i = 0; i < linkedTo.size(); i++)
			if (linkedTo.get(i).getParent() == parent)
			{
				linkedTo.get(i).removeChild(child);
				if (linkedTo.get(i).getCountChildren() == 0)
					linkedTo.remove(i);
				return;
			}
	}
	
	public void clearLinkedTo()
	{
		linkedTo.clear();
	}
	
	public SubstanceToTake removeSubstanceToTake(int index)
	{
		SubstanceToTake stt = null;
		if (index >= 0 && index < substancesToTake.size())
		{
			stt = substancesToTake.remove(index);
		}
		return stt;
	}
	
	public int getMaxWeekNumbers()
	{
		int max = 0;
		for (int i = 0; i < substancesToTake.size(); i++)
			max = Math.max(max, substancesToTake.get(i).getEndWeek() + 1);
		return max;
	}
	
	public void clearSteroids()
	{
		for (int i = 0; i < substancesToTake.size(); i++)
			substancesToTake.get(i).delete();
		substancesToTake.clear();
	}
	
	private List<List<Float>> getAlarmDataWholeWeeks()
	{
		List<List<Float>> intakeData = new ArrayList<List<Float>>();
		for (int i = 0; i < substancesToTake.size(); i++)
			intakeData.add(new ArrayList<Float>());
		
		int maxDays = getMaxWeekNumbers() * 7;
		float[] data;
		for (int i = 0; i < substancesToTake.size(); i++)
		{
			for (int j = 0; j < maxDays; j++)
			{
				int k = 0;
				while (k < substancesToTake.get(i).getStartWeek() * 7 + substancesToTake.get(i).getStartWeekDay())
				{
					intakeData.get(i).add(0f);
					k++;
				}
				data = substancesToTake.get(i).getIntakeData();
				k = 0;
				while (k < data.length)
				{
					intakeData.get(i).add(data[k]);
					k++;
				}
				k = substancesToTake.get(i).getStartWeek() * 7 + data.length;
			
				while (k < maxDays)
				{
					intakeData.get(i).add(0f);
					k++;
				}
			}			
		}
		
		return intakeData;
	}
	
	private void doCreateAlarms(Context c)
	{
		// checken ob schon Tage der plan vorbei sind...
		int daysToWeekStart = Functions.getDaysBetweenDates(new Date(), date);
		int daysToIgnore = daysToWeekStart < 0 ? daysToWeekStart * -1 : 0;
		
		if (daysToIgnore >= getMaxWeekNumbers() * 7)
			return;
		
		List<List<Float>> alarmData = getAlarmDataWholeWeeks();
		List<Float> singleAlarmData = new ArrayList<Float>();
		List<SubstanceToTake> singleAlarmSteroids = new ArrayList<SubstanceToTake>();
		for (int i = daysToIgnore; i < getMaxWeekNumbers() * 7; i++)
		{
	    	// Checken ob leerer Alarm
	    	boolean foundNotZero = false;
	    	for (int j = 0; j < alarmData.size(); j++)
	    	{
	    		if (alarmData.get(j).get(i) != 0)
	    		{
	    			foundNotZero = true;
	    			break;
	    		}
	    	}
	    	
	    	if (foundNotZero)
	    	{
	    		singleAlarmData.clear();
	    		singleAlarmSteroids.clear();
	    		for (int j = 0; j < alarmData.size(); j++)
		    		if (alarmData.get(j).get(i) != 0)
		    		{
		    			singleAlarmData.add(alarmData.get(j).get(i));
		    			singleAlarmSteroids.add(substancesToTake.get(j));
		    		}
	    		Alarm.createAlarm(c, id, singleAlarmSteroids, singleAlarmData, date,  i);
	    	}
	    	
		}
	}
	public void createAlarms(final Context c)
	{
		if (id != -1 && !getStatus(c).equals(c.getString(R.string.status_deactivated)))
		{
			Thread t = new Thread() {
                public void run() {
                	doCreateAlarms(c);
                }
            };
            t.start();
		}
	}
	
	@Override
	public void delete()
	{
		for (int i = 0; i < substancesToTake.size(); i++)
			substancesToTake.get(i).delete();
		super.delete();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	protected String getDBName() {
		return DBConstants.TBL_PLANS;
	}

	@Override
	protected String[] getDBCols() {
		return DBConstants.ALL_COLS_PLANS;
	}

	@Override
	protected void setContentValues() {
		values = new ContentValues();
		values.put(DBConstants.COL_NAME, name);
		if (date != null)
			values.put(DBConstants.COL_DATE, PublicData.dateDBFormat.format(date));
		else
			values.put(DBConstants.COL_DATE, "");
		
		String substancesToTakeAsString = "";
		for (int i = 0; i < substancesToTake.size(); i++)
		{
			if (i == 0)
				substancesToTakeAsString = String.valueOf(substancesToTake.get(i).getID());
			else
				substancesToTakeAsString += ";" + substancesToTake.get(i).getID();
		}
		values.put(DBConstants.COL_SUBSTANCES_TO_TAKE_IDS, substancesToTakeAsString);
		
		String linkedToString = "";
		for (int i = 0; i < linkedTo.size(); i++)
		{
			if (i > 0)
				linkedToString += ";";
			
			linkedToString += String.valueOf(linkedTo.get(i).getParent() + ":");
			for (int j = 0; j < linkedTo.get(i).getCountChildren(); j++)
			{
				if (j > 0)
					linkedToString += ",";
				linkedToString += linkedTo.get(i).getChildAt(j);
			}
		}
		values.put(DBConstants.COL_LINKED_TO, linkedToString);
	}

	@Override
	public void fromCursor(Cursor cursor, boolean withSubData) {
		id = cursor.getLong(0);
		name = cursor.getString(1);
		try 
		{
			if (cursor.getString(2).equals(""))
				date = null;
			else
				date = PublicData.dateDBFormat.parse(cursor.getString(2));
		} 
		catch (ParseException e) {
			date = new Date();
		}
		String substancesToTakeAsString = cursor.getString(3);
		String linkedToString = cursor.getString(4);
		
		substancesToTake = new ArrayList<SubstanceToTake>();
		linkedTo = new ArrayList<Link>();
		
		if (substancesToTakeAsString.length() > 0)
		{
			String[] splitted = substancesToTakeAsString.split(";");
			for (int i = 0; i < splitted.length; i++)
			{
				int id = Integer.parseInt(splitted[i]);
				SubstanceToTake substance = new SubstanceToTake();
				substance.setID(id);
				substance.reload(true);
				substancesToTake.add(substance);
			}
		}

		if (linkedToString.length() > 0)
		{
			String[] splitted = linkedToString.split(";");
			for (int i = 0; i < splitted.length; i++)
			{
				String[] splittedSub = splitted[i].split(":");
				int parent = Integer.parseInt(splittedSub[0]);
				String[] splittedSubSub = splittedSub[1].split(",");
				
				Link link = new Link(parent);
				for (int j = 0; j < splittedSubSub.length; j++)
					link.addChildIfNotExists(Integer.parseInt(splittedSubSub[j]));
				linkedTo.add(link);
			}
		}
	}
	
	@Override
	protected Cursor getSelectSingleCursor(long id)
	{
		String query = "SELECT * FROM " + DBConstants.TBL_PLANS + 
			" WHERE " + DBConstants.TBL_PLANS + "." + DBConstants.COL_ID + "=?";
		return StaticDatabase.getDataSource().getDatabase().rawQuery(query, new String[]{String.valueOf(id)});
	}
	
	public static String getSelectAllString()
	{
		String query = "SELECT * FROM " + DBConstants.TBL_PLANS;
		return query;
	}
	
	public String[] getAllSubstancesNames()
	{
		String[] substances = new String[substancesToTake.size()];
		for (int i = 0; i < substancesToTake.size(); i++)
			substances[i] = substancesToTake.get(i).toString();
		return substances;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<SubstanceToTake> getSubstancesToTake() {
		return substancesToTake;
	}
	
	public List<Link> getLinksForGraph(boolean realParentsOnly)
	{
		List<Link> list = new ArrayList<Link>();
		
		// alle Indexe hinzuf�gen
		for (int i = 0; i < substancesToTake.size(); i++)
			if (substancesToTake.get(i).isSubstanceForGraph())
				list.add(new Link(i));

		// alle Child Indexe zu ihren Parents hinzuf�gen, die ein Child sind
		int index = 0;
		for (int i = 0; i < substancesToTake.size(); i++)
			if (substancesToTake.get(i).isSubstanceForGraph())
			{
				for (int j = 0; j < linkedTo.size(); j++)
				{
					if (linkedTo.get(j).getParent() == i)
						list.get(index).addChildrenIfNotExists(linkedTo.get(j).getChildrenIndizes());
				}
				index++;
			}
		
		// eventuell Parents rausl�schen, die children sind
		if (realParentsOnly)
		{
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				int parent = list.get(i).getParent();
				
				for (int j = 0; j < size; j++)
					if (list.get(j).containsChild(parent))
					{
						list.remove(i);
						size--;
						i--;
						break;
					}
			}
		}

		return list;
	}
	
	public List<Integer> getIndizes(INDEX_TYPE type)
	{
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < substancesToTake.size(); i++)
		{
			if (type == INDEX_TYPE.GRAPH_RELEVANT)
			{
				if (substancesToTake.get(i).isSubstanceForGraph())
					list.add(i);
			}
			else if (type == INDEX_TYPE.VISIBLE)
			{
				if (substancesToTake.get(i).getShowInGraph())
					list.add(i);
			}
			else if (type == INDEX_TYPE.GRAPH_RELEVANT_PARENTS_ONLY)
			{
				if (substancesToTake.get(i).isSubstanceForGraph())
				{
					boolean isChild = false;
					for (int j = 0; j < linkedTo.size(); j++)
						if (linkedTo.get(j).containsChild(i))
						{
							isChild = true;
							break;
						}
					if (!isChild)
						list.add(i);
				}
			}
		}
		return list;
	}
}
