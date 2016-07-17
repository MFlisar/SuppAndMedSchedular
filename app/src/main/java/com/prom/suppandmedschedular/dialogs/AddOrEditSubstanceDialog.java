package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.Functions;
import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.ReminderTime;
import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.Substance;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;

public class AddOrEditSubstanceDialog extends Dialog implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener {

	private MainActivity parent;
	private SubstanceToTake substanceToTake;
	
	private int startWeek;
	private int startDay;
	private int endWeek;
	private int endDay;
	private Substance[] substancesItems;
	private Substance substance;
	private int[] amounts;
	
	private int init = 0;
	
	@SuppressWarnings("unchecked")
	public AddOrEditSubstanceDialog(MainActivity parent, SubstanceToTake substanceToTake) {
		super(parent);
		this.parent = parent;
		this.substanceToTake = substanceToTake;
		setContentView(R.layout.edit_or_add_substance);
		if (substanceToTake == null)
			setTitle(R.string.add_substance);
		else
			setTitle(R.string.edit_substance);

		// Startwoche
		int maxWeeks = Functions.getMaxWeeksFromPreferences(parent.getBaseContext());
		String[] items = new String[maxWeeks];
		for (int i = 1; i <= maxWeeks; i++)
		{
			items[i - 1] = parent.getString(R.string.week) + " " + i;
		}
		((Spinner)findViewById(R.id.spStartWeek)).setAdapter(
				new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, items));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spStartWeek)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Starttag
		items = parent.getResources().getStringArray(R.array.week_days_long);
		((Spinner)findViewById(R.id.spStartDay)).setAdapter(
				new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, items));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spStartDay)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Steroid
		substancesItems = StaticDatabase.getDataSource().getAllSubstances();
		((Spinner)findViewById(R.id.spSteroid)).setAdapter(
				new ArrayAdapter<Substance>(parent, android.R.layout.simple_spinner_item, substancesItems));
		((ArrayAdapter<Substance>)((Spinner)findViewById(R.id.spSteroid)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (substancesItems.length > 0)
		{
			startWeek = 0;
			startDay = 0;
			endWeek = maxWeeks - 1;
			endDay = 6;
			if (substanceToTake == null)
				substance = substancesItems[0];
			else
				substance = substanceToTake.getSubstance();
			
			// Menge
			amounts = substance.getAmounts();
			String[] amountsString = substancesItems[0].getAmountsString(parent);
			((Spinner)findViewById(R.id.spAmount)).setAdapter(
					new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, amountsString));
	//		((Spinner)findViewById(R.id.spAmount)).setSelection(4);
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Einnahmeschema
			items = PublicData.getIntakeSchemas();
			((Spinner)findViewById(R.id.spIntakeSchema)).setAdapter(
					new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, items));
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spIntakeSchema)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			((Spinner)findViewById(R.id.spIntakeSchema)).setSelection(6);
			
			// Erinnerungszeit Wochentag
			ReminderTime reminderTime = Functions.getStandardReminderDateFromPreferences(parent.getBaseContext());
			((TimePicker)findViewById(R.id.tpReminderTime)).setCurrentHour(reminderTime.getHour());
			((TimePicker)findViewById(R.id.tpReminderTime)).setCurrentMinute(reminderTime.getMinutes());
			
			// Frontload
			items = new String[]{parent.getResources().getString(R.string.yes), parent.getResources().getString(R.string.no)};
			((CheckBox)findViewById(R.id.cbFrontload)).setChecked(false);
			((CheckBox)findViewById(R.id.cbFrontload)).setOnCheckedChangeListener(this);
			
			// Frontload Tag
			items = new String[Functions.getMaxFrontloadDaysPreferences(parent)];
			for (int i = 0; i < items.length; i++)
				items[i] = parent.getResources().getString(R.string.day) + " " + (i + 1);
			((Spinner)findViewById(R.id.spFrontloadDay)).setAdapter(
					new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, items));
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spFrontloadDay)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			((Spinner)findViewById(R.id.spFrontloadDay)).setSelection(0);
			((Spinner)findViewById(R.id.spFrontloadDay)).setEnabled(false);
			
			// Frontloadmenge
			((Spinner)findViewById(R.id.spFrontloadAmount)).setAdapter(
					new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, amountsString));
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spFrontloadAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	//		((Spinner)findViewById(R.id.spFrontloadAmount)).setSelection(4);
			((Spinner)findViewById(R.id.spFrontloadAmount)).setEnabled(false);
			
			// Datetime Picker
			if (Functions.isGerman(getContext()))
				((TimePicker)findViewById(R.id.tpReminderTime)).setIs24HourView(true);
	
			findViewById(R.id.btBack).setOnClickListener(this);
			findViewById(R.id.btAdd).setOnClickListener(this);	
			
			if (substanceToTake != null)
			{
				startWeek = substanceToTake.getStartWeek();
				startDay = substanceToTake.getStartWeekDay();
				endWeek = substanceToTake.getEndWeek();
				endDay = substanceToTake.getEndWeekDay();
				((Spinner)findViewById(R.id.spStartWeek)).setSelection(substanceToTake.getStartWeek());
				((Spinner)findViewById(R.id.spStartDay)).setSelection(substanceToTake.getStartWeekDay());
				((Spinner)findViewById(R.id.spEndWeek)).setSelection(substanceToTake.getEndWeek());
				((Spinner)findViewById(R.id.spEndDay)).setSelection(substanceToTake.getEndWeekDay());
				amountsString = substanceToTake.getSubstance().getAmountsString(parent);
				amounts = substanceToTake.getSubstance().getAmounts();
				
				
				((Spinner)findViewById(R.id.spAmount)).setAdapter(
						new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, amountsString));
				((ArrayAdapter<String>)((Spinner)findViewById(R.id.spAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				((Spinner)findViewById(R.id.spFrontloadAmount)).setAdapter(
						new ArrayAdapter<String>(parent, android.R.layout.simple_spinner_item, amountsString));
				((ArrayAdapter<String>)((Spinner)findViewById(R.id.spFrontloadAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				for (int i = 0; i < substancesItems.length; i++)
				{
					if (substanceToTake.getSubstance().getID() == substancesItems[i].getID())
					{
						((Spinner)findViewById(R.id.spSteroid)).setSelection(i);
						break;
					}
				}
				for (int i = 0; i < amounts.length; i++)
				{
					if (substanceToTake.getAmount() == amounts[i])
					{
						((Spinner)findViewById(R.id.spAmount)).setSelection(i);
						break;
					}
				}
				((TimePicker)findViewById(R.id.tpReminderTime)).setCurrentHour(substanceToTake.getReminderTime().getHour());
				((TimePicker)findViewById(R.id.tpReminderTime)).setCurrentMinute(substanceToTake.getReminderTime().getMinutes());
				
				for (int i = 0; i < PublicData.intakeSchemas.length; i++)
				{
					if (substanceToTake.getRegularity() == PublicData.intakeSchemas[i])
					{
						((Spinner)findViewById(R.id.spIntakeSchema)).setSelection(i);
						break;
					}
				}
				
				((CheckBox)findViewById(R.id.cbFrontload)).setChecked(substanceToTake.getFrontloadAmount() != 0);
				((Spinner)findViewById(R.id.spFrontloadDay)).setSelection(substanceToTake.getFrontloadDay());
				for (int i = 0; i < amounts.length; i++)
				{
					if (substanceToTake.getFrontloadAmount() == amounts[i])
					{
						((Spinner)findViewById(R.id.spFrontloadAmount)).setSelection(i);
						break;
					}
				}
				((Button)findViewById(R.id.btAdd)).setText(R.string.save);
			}
			
			// Endwoche und Endtag
			updateSpinners(false);
			
			((Spinner)findViewById(R.id.spStartWeek)).setOnItemSelectedListener(this);
			((Spinner)findViewById(R.id.spStartDay)).setOnItemSelectedListener(this);
			((Spinner)findViewById(R.id.spSteroid)).setOnItemSelectedListener(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateSpinners(boolean updateAmounts)
	{
		// Listeners entfernen
		((Spinner)findViewById(R.id.spEndWeek)).setOnItemSelectedListener(null);
		((Spinner)findViewById(R.id.spEndDay)).setOnItemSelectedListener(null);

		// Endwoche
		int maxWeeks = Functions.getMaxWeeksFromPreferences(parent.getBaseContext());
		String[] items = new String[maxWeeks - startWeek];
		for (int i = startWeek; i < maxWeeks; i++)
		{
			items[i - startWeek] = getContext().getString(R.string.week) + " " + (i + 1);
		}

		((Spinner)findViewById(R.id.spEndWeek)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spEndWeek)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)findViewById(R.id.spEndWeek)).setSelection(endWeek - startWeek, false);
		
		// Endtag
		String[] buffer = getContext().getResources().getStringArray(R.array.week_days_long);
		items = buffer;
		if (startWeek == endWeek)
		{
			items = new String[7 - startDay];
			for (int i = startDay; i < 7; i++)
				items[i - startDay] = buffer[i];
		}
		((Spinner)findViewById(R.id.spEndDay)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spEndDay)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (startWeek == endWeek)
			((Spinner)findViewById(R.id.spEndDay)).setSelection(endDay - startDay, false);
		else
			((Spinner)findViewById(R.id.spEndDay)).setSelection(endDay, false);	
		
		// amounts
		if (updateAmounts)
		{
			amounts = substance.getAmounts();
			String[] amountsString = substance.getAmountsString(parent);
			((Spinner)findViewById(R.id.spAmount)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, amountsString));
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			((Spinner)findViewById(R.id.spFrontloadAmount)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, amountsString));
			((ArrayAdapter<String>)((Spinner)findViewById(R.id.spFrontloadAmount)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}
		
		// Listeners wieder hinzuf�gen
		((Spinner)findViewById(R.id.spEndWeek)).setOnItemSelectedListener(this);
		((Spinner)findViewById(R.id.spEndDay)).setOnItemSelectedListener(this);
	}

	public void onClick(View view) 
	{
		if (view.getId() == R.id.btBack) {
			dismiss();			
		}
		else if (view.getId() == R.id.btAdd)
		{
			int amount = amounts[((Spinner)findViewById(R.id.spAmount)).getSelectedItemPosition()];
			boolean frontload = ((CheckBox)findViewById(R.id.cbFrontload)).isChecked();
			float regularity = PublicData.intakeSchemas[((Spinner)findViewById(R.id.spIntakeSchema)).getSelectedItemPosition()];
			ReminderTime reminderTime = new ReminderTime();
			reminderTime.setHour(((TimePicker)findViewById(R.id.tpReminderTime)).getCurrentHour());
			reminderTime.setMinutes(((TimePicker)findViewById(R.id.tpReminderTime)).getCurrentMinute());
			
			int frontloadDay = 0;
			int frontloadAmount = 0;
			if (frontload)
			{
				frontloadDay = ((Spinner)findViewById(R.id.spFrontloadDay)).getSelectedItemPosition();
				frontloadAmount = amounts[((Spinner)findViewById(R.id.spFrontloadAmount)).getSelectedItemPosition()];
			}
			if (substanceToTake != null)
			{
				substanceToTake.setData(substance, reminderTime, startWeek, startDay, endWeek, endDay, amount, frontloadDay, frontloadAmount, regularity);
				substanceToTake.update();
			}
			else
			{
				substanceToTake = new SubstanceToTake(substance, reminderTime, startWeek, startDay, endWeek, endDay, amount, frontloadDay, frontloadAmount, regularity);
				substanceToTake.create();
				PublicData.selectedPlan.addSubstanceToTake(substanceToTake);
				PublicData.selectedPlan.update();
			}
			parent.update(true, false, false, false, -1, true, PublicData.selectedPlan.getID(), false);
			dismiss();
		}
	}

	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		
		if ( init != 3)
		{
			init++;
			return;
		}
		if (parentView.getId() == R.id.spSteroid)
		{
			if (position >= 0)
			{
				substance = substancesItems[position];
				updateSpinners(true);
			}
		}
		
		if (parentView.getId() == R.id.spStartWeek)
		{
			if (position >= 0)
			{
				startWeek = position;
				updateSpinners(false);
			}
		}
		else if (parentView.getId() == R.id.spStartDay)
		{
			if (position >= 0)
			{
				startDay = position;
				updateSpinners(false);
			}
		}
		else if (parentView.getId() == R.id.spEndWeek)
		{
			if (position >= 0)
				endWeek = startWeek + position;
		}
		else if (parentView.getId() == R.id.spEndDay)
		{
			if (position >= 0)
			{
				if (startWeek == endWeek)
					endDay = startDay + position;
				else
					endDay = position;
			}
		}
	}

	public void onNothingSelected(AdapterView<?> parentView) {		
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.cbFrontload)
		{
			((Spinner)findViewById(R.id.spFrontloadDay)).setEnabled(isChecked);
			((Spinner)findViewById(R.id.spFrontloadAmount)).setEnabled(isChecked);		
		}
	}
}
