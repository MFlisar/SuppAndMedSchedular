package com.prom.suppandmedschedular.helper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.prom.suppandmedschedular.activities.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.prom.suppandmedschedular.R;
import android.widget.Toast;
import com.prom.suppandmedschedular.db.classes.BaseSteroid;
import com.prom.suppandmedschedular.db.classes.Plan;

public class PublicData {

	public static enum TYPE {
		STEROID,
		SUPP
	};
	
	public static enum UNIT {
		MG,
		PIECES
	};
	
	public static String[] types;
	public static String[] units;
	
	public static TYPE getTypeFromString(String type)
	{
		for (int i = 0; i < types.length; i++)
			if (types[i].equals(type))
				return TYPE.values()[i];
		return TYPE.STEROID;
	}
	public static UNIT getUnitFromString(String unit)
	{
		for (int i = 0; i < units.length; i++)
			if (units[i].equals(unit))
				return UNIT.values()[i];
		return UNIT.MG;
	}
	public static String MY_PREFS = "myPrefs";
	
	
	public static final SimpleDateFormat dateDBFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateShortFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	
	public static final DecimalFormat decimalFormat2 = new DecimalFormat("#.##");
	public static final DecimalFormat decimalFormat0 = new DecimalFormat("#");
	
	public static Plan selectedPlan = null;
	public static int bufferWeeks = 4;
	
	private static boolean wasInitialised = false;
	public static float[] intakeSchemas;
	
	public static int TESTO_E = 0;
	public static int TESTO_P = 1;
	public static int DECA = 2;
	public static int NONE = 3;
	
	public static BaseSteroid[] baseSteroids = new BaseSteroid[4];
	
	private static Toast toast = null;
	
	public static void showToast(Context c, String text)
	{
		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(c, text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void showToast(Context c, int text)
	{
		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(c, text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static int[] colors = {
		Color.GREEN,
		Color.YELLOW,
		Color.MAGENTA,
		Color.CYAN,
		Color.BLUE
	};
	
	public static void init(Context c)
	{
		if (!wasInitialised)
		{			
			baseSteroids[PublicData.TESTO_E] = new BaseSteroid(PublicData.TESTO_E, "TestoE", 1.19327140223722f, 4.5f, 0.581f, 0.154f, 63.6090899f / 3.467f / 250);
			baseSteroids[PublicData.TESTO_P] = new BaseSteroid(PublicData.TESTO_P, "TestoP", 0.23066494f, 0.8f, 3.005f, 0.866f, 66.5349091f / 3.467f / 50);
			baseSteroids[PublicData.DECA] = new BaseSteroid(PublicData.DECA, "Deca", 1 / 24, 143 / 24, 16.636f, 0.116f, 1 / 16.6f * 0.64f);
			baseSteroids[PublicData.NONE] = new BaseSteroid(PublicData.NONE, "-");
			
			types = new String[2];
			types[0] = c.getString(R.string.steroid);
			types[1] = c.getString(R.string.supp);
			
			units = new String[2];
			units[0] = c.getString(R.string.mg);
			units[1] = c.getString(R.string.piece);
			
			intakeSchemas = new float[14];
			for (float i = 1; i <= intakeSchemas.length; i++)
				intakeSchemas[(int)(i - 1)] = i;
		}
		wasInitialised = true;
	}
	public static String[] getIntakeSchemas()
	{
		DecimalFormat f = new DecimalFormat("#0.#"); 
		String[] values = new String[intakeSchemas.length];
		for (int i = 0; i < values.length; i++)
		{
			if (intakeSchemas[i] == 1)
				values[i] = "ed";
			else if (intakeSchemas[i] == 2)
				values[i] = "eod";
			else
				values[i] = "e" + f.format(intakeSchemas[i]) + "d";
		}
		return values;
	}
	public static int getColor(int index)
	{
		index = index % colors.length;
		return colors[index];
	}
	
	public static IconContextMenu getIconContextMenuPlan(MainActivity parent)
	{
		IconContextMenu menu = new IconContextMenu(parent, null);
		
		menu.addItem(parent.getResources(), R.string.create_new_plan, R.drawable.ic_menu_add, R.id.create_plan);
		menu.addItem(parent.getResources(), R.string.edit_plan, R.drawable.ic_menu_edit, R.id.edit_plan);
		menu.addItem(parent.getResources(), R.string.delete_plan, R.drawable.ic_menu_delete, R.id.delete_plan);
	        
		menu.setOnClickListener(parent);
		return menu;
	}
	
	public static IconContextMenu getIconContextMenuSubstance(MainActivity parent)
	{
		IconContextMenu menu = new IconContextMenu(parent, null);
		
		menu.addItem(parent.getResources(), R.string.add_substance, R.drawable.ic_menu_add, R.id.add_substance);
		menu.addItem(parent.getResources(), R.string.delete_all_substances, R.drawable.ic_menu_delete, R.id.delete_all_substances);
	        
		menu.setOnClickListener(parent);
		return menu;
	}
	
	public static IconContextMenu getIconContextMenuSubstanceHeader(MainActivity parent, Intent intent)
	{
		IconContextMenu menu = new IconContextMenu(parent, intent);
		
		menu.addItem(parent.getResources(), R.string.delete_substance, R.drawable.ic_menu_delete, R.id.delete_substance);
		menu.addItem(parent.getResources(), R.string.edit_substance, R.drawable.ic_menu_edit, R.id.edit_substance);
	        
		menu.setOnClickListener(parent);
		return menu;
	}
}
