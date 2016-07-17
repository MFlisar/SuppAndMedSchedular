package com.prom.suppandmedschedular.db.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DBConstants {
	
	// DB Feldtypen
	private static final String ID = "integer primary key autoincrement";
	private static final String DATE = "date";
	private static final String INT = "integer";
	private static final String REAL = "real";
	private static final String LONG = "bigint";
	private static final String TEXT = "text COLLATE NOCASE NOT NULL DEFAULT ''"; // collate, damit Vergleich/Sortierung case INsensitive ist
	private static final String VARCHAR_100 = "varchar(100) COLLATE NOCASE NOT NULL DEFAULT ''"; // collate, damit Vergleich/Sortierung case INsensitive ist
	private static final String VARCHAR_5 = "varchar(5) COLLATE NOCASE NOT NULL DEFAULT ''"; // collate, damit Vergleich/Sortierung case INsensitive ist
	
	// allgemeine Parameter
	public static final String DATABASE_NAME = "planplaner.db";
	public static final int DATABASE_VERSION = 1;
	
	// Tabellennamen
	public static final String TBL_SUBSTANCE = "tbl_substance";
	public static final String TBL_SUBSTANCE_TO_TAKE = "tbl_subs_to_take";
	public static final String TBL_PLANS = "tbl_plans";
	public static final String TBL_ALARMS = "tbl_alarms";

	// Spaltennamen
	public static final String COL_ID = "id";
	public static final String COL_NAME = "name";
	public static final String COL_TYPE = "type";
	public static final String COL_UNIT = "unit";
	public static final String COL_STEP_SIZE = "step_size";
	public static final String COL_FK_BASE_STEROID = "fk_base_steroid";
	public static final String COL_REMINDER_TIME = "reminder_time";
	public static final String COL_DATE = "date";
	public static final String COL_FK_SUBSTANCE = "fk_substance_id";
	public static final String COL_START_WEEK = "start_week";
	public static final String COL_START_DAY = "start_day";
	public static final String COL_END_WEEK = "end_week";
	public static final String COL_END_DAY = "end_day";
	public static final String COL_AMOUNT = "amount";
	public static final String COL_FRONTLOAD_DAY = "frontload_day";
	public static final String COL_AMOUNT_FRONTLOAD = "amount_frontload";
	public static final String COL_REGULARITY = "regularity";
//	public static final String COL_HWZ_INVASION = "hwzInvasion";
//	public static final String COL_HWZ_EVASION = "hwzEvasion";
//	public static final String COL_K_INF = "kInf";
//	public static final String COL_K_EV = "kEv";
//	public static final String COL_C0 = "c0";	
	public static final String COL_SUBSTANCES_TO_TAKE_IDS = "substances_to_take";
	public static final String COL_LINKED_TO = "linked_to";
	public static final String COL_ALARM_ID = "alarm_id";
	public static final String COL_FK_plan = "plan_id";
	
	// Spalten zu den Tabellen
	public static final String[] ALL_COLS_SUBSTANCES = { COL_ID, COL_TYPE, COL_UNIT, COL_NAME, COL_FK_BASE_STEROID, COL_STEP_SIZE };
	private static final String[] COLS_SUBSTANCES_TYPES = { ID, INT, INT, VARCHAR_100, INT, INT };

	public static final String[] ALL_COLS_SUBSTANCES_TO_TAKE = { COL_ID, COL_FK_SUBSTANCE, COL_REMINDER_TIME, COL_START_WEEK, COL_START_DAY, COL_END_WEEK, COL_END_DAY, COL_AMOUNT, COL_FRONTLOAD_DAY, COL_AMOUNT_FRONTLOAD, COL_REGULARITY };
	private static final String[] COLS_SUBSTANCES_TO_TAKE_TYPES = { ID, LONG, VARCHAR_5, INT, INT, INT, INT, INT, REAL, INT, INT };
	
	public static final String[] ALL_COLS_PLANS = {COL_ID, COL_NAME, COL_DATE, COL_SUBSTANCES_TO_TAKE_IDS, COL_LINKED_TO };
	private static final String[] COLS_PLANS_TYPES = { ID, VARCHAR_100, DATE, TEXT, TEXT };
	
	public static final String[] ALL_COLS_ALARM = {COL_ID, COL_ALARM_ID, COL_FK_plan };
	private static final String[] COLS_ALARM_TYPES = { ID, INT, LONG };
	
	// create table Strings + all tables + Hilfs-Maps
	@SuppressWarnings("serial")
	public static final Map<String, String[]> MAP_ALL_COLS = new HashMap<String, String[]>(){{
		put(TBL_SUBSTANCE_TO_TAKE, ALL_COLS_SUBSTANCES_TO_TAKE);
		put(TBL_SUBSTANCE, ALL_COLS_SUBSTANCES);
		put(TBL_PLANS, ALL_COLS_PLANS);
		put(TBL_ALARMS, ALL_COLS_ALARM);
	}};
	@SuppressWarnings("serial")
	public static final Map<String, String[]> MAP_COLS_TYPES = new HashMap<String, String[]>(){{
		put(TBL_SUBSTANCE_TO_TAKE, COLS_SUBSTANCES_TO_TAKE_TYPES);
		put(TBL_SUBSTANCE, COLS_SUBSTANCES_TYPES);
		put(TBL_PLANS, COLS_PLANS_TYPES);
		put(TBL_ALARMS, COLS_ALARM_TYPES);
	}};
	public static final String[] getAllTableNames()
	{
		String[] all_table_names = new String[MAP_ALL_COLS.size()];
		Iterator<Entry<String, String[]>> it_cols = MAP_ALL_COLS.entrySet().iterator();
		
		int counter = 0;
		while (it_cols.hasNext())
		{
			all_table_names[counter] = ((Entry<String, String[]>)it_cols.next()).getKey();
			counter++;
		}
		return all_table_names;
	}
	
	public static final String[] getAllTableCreateStrings()
	{
		String[] create_strings = new String[MAP_ALL_COLS.size()];
		
		Iterator<Entry<String, String[]>> it_cols = MAP_ALL_COLS.entrySet().iterator();
		Iterator<Entry<String, String[]>> it_col_types = MAP_COLS_TYPES.entrySet().iterator();

		String create_string;
		int counter = 0;
		while (it_cols.hasNext() && it_col_types.hasNext())
		{
			Entry<String, String[]> pair_cols = (Entry<String, String[]>)it_cols.next();
			Entry<String, String[]> pair_col_types = (Entry<String, String[]>)it_col_types.next();
			
			create_string = "create table " + pair_cols.getKey() + " (";
			String[] cols = pair_cols.getValue();
			String[] colTypes = pair_col_types.getValue();
			int count_cols = cols.length;
			for (int i = 0; i < count_cols; i++)
			{
				create_string += cols[i] + " " + colTypes[i];
				if (i < count_cols - 1)
					create_string += ", ";
			}
			create_string += ");";
			create_strings[counter] = create_string;
			counter++;
		}
		return create_strings;
	}
}
