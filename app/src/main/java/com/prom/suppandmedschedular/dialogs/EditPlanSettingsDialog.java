package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.Functions;
import com.prom.suppandmedschedular.helper.PublicData;

import java.util.Calendar;
import java.util.Date;

import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class EditPlanSettingsDialog extends Dialog implements OnClickListener, OnCheckedChangeListener {

	private MainActivity parent;
	private Date date;
	private boolean isActivated;
	
	public EditPlanSettingsDialog(MainActivity parent) {
		super(parent);
		this.parent = parent;
		setContentView(R.layout.edit_name_date);
		setTitle(R.string.plan_settings);
		
		((EditText)findViewById(R.id.etPlanname)).setText(PublicData.selectedPlan.getName());
		if (PublicData.selectedPlan.getDate() != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(PublicData.selectedPlan.getDate());
			((DatePicker)findViewById(R.id.dpStartDate)).init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
		}
		isActivated = PublicData.selectedPlan.getDate() != null;
		date = PublicData.selectedPlan.getDate();
		((CheckBox)findViewById(R.id.cbDeactivatePlan)).setChecked(!isActivated);
		if (PublicData.selectedPlan.getDate() == null)
			((DatePicker)findViewById(R.id.dpStartDate)).setVisibility(View.INVISIBLE);
		findViewById(R.id.btBack).setOnClickListener(this);
		findViewById(R.id.btSave).setOnClickListener(this);		
		((CheckBox)findViewById(R.id.cbDeactivatePlan)).setOnCheckedChangeListener(this);
	}
	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
		if (isChecked)
			((DatePicker)findViewById(R.id.dpStartDate)).setVisibility(View.INVISIBLE);
		else
			((DatePicker)findViewById(R.id.dpStartDate)).setVisibility(View.VISIBLE);
    }
	
	public void onClick(View view) 
	{
		if (view.getId() == R.id.btBack) {
			dismiss();			
		}
		else if (view.getId() == R.id.btSave)
		{
			int year = ((DatePicker)findViewById(R.id.dpStartDate)).getYear();
	    	int month = ((DatePicker)findViewById(R.id.dpStartDate)).getMonth();
	    	int day = ((DatePicker)findViewById(R.id.dpStartDate)).getDayOfMonth();
	    	if (((CheckBox)findViewById(R.id.cbDeactivatePlan)).isChecked())
	    		PublicData.selectedPlan.setDate(null);
	    	else
	    		PublicData.selectedPlan.setDate(Functions.getFirstWeekDayDate(new Date(year - 1900, month, day)));
	    	PublicData.selectedPlan.setName(((EditText)findViewById(R.id.etPlanname)).getText().toString());
	    	PublicData.selectedPlan.update();
	    	parent.update(PublicData.selectedPlan.getDate() != date, true, true, true, -1, isActivated != (PublicData.selectedPlan.getDate() != null), PublicData.selectedPlan.getID(), false);
			dismiss();
		}
	}
}
