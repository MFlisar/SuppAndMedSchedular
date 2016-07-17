package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.list.row_classes.EditSubstanceAdapter;
import android.app.Dialog;
import android.content.Context;
import com.prom.suppandmedschedular.R;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.prom.suppandmedschedular.db.classes.BaseSteroid;

public class EditSingleSubstanceDialog extends Dialog {

	private EditSubstanceAdapter adapter;
	private int pos = -1;
	
	
	@SuppressWarnings("unchecked")
	public EditSingleSubstanceDialog(Context context, EditSubstanceAdapter adapter, int pos) {
		super(context);
		this.adapter = adapter;
		this.pos = pos;
		setContentView(R.layout.edit_single_substances);
		setTitle(R.string.edit_substance);
		
		((EditText)findViewById(R.id.etName)).setText(adapter.list.get(pos).getName());

		((Spinner)findViewById(R.id.spType)).setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, PublicData.types));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spType)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		((Spinner)findViewById(R.id.spBaseSteroid)).setAdapter(new ArrayAdapter<BaseSteroid>(context, android.R.layout.simple_spinner_item, PublicData.baseSteroids));
		((ArrayAdapter<BaseSteroid>)((Spinner)findViewById(R.id.spBaseSteroid)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		((Spinner)findViewById(R.id.spUnit)).setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, PublicData.units));
		((ArrayAdapter<String>)((Spinner)findViewById(R.id.spUnit)).getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		((EditText)findViewById(R.id.etStepSize)).setText(String.valueOf(adapter.list.get(pos).getStepSize()));
		
		((Spinner)findViewById(R.id.spType)).setSelection(adapter.list.get(pos).getType().ordinal());
		((Spinner)findViewById(R.id.spUnit)).setSelection(adapter.list.get(pos).getUnit().ordinal());
		((Spinner)findViewById(R.id.spBaseSteroid)).setSelection(adapter.list.get(pos).getBaseSteroidID());
		
		((Spinner)findViewById(R.id.spType)).setOnItemSelectedListener(adapter);
		((Spinner)findViewById(R.id.spUnit)).setOnItemSelectedListener(adapter);
	}
	
	public void onBackPressed()
	{
		adapter.list.get(pos).setName(((EditText)findViewById(R.id.etName)).getText().toString());
		adapter.list.get(pos).setBaseSteroidID(((BaseSteroid)((Spinner)findViewById(R.id.spBaseSteroid)).getSelectedItem()).getId());
		PublicData.TYPE type = PublicData.getTypeFromString(((String)((Spinner)findViewById(R.id.spType)).getSelectedItem().toString()));
		adapter.list.get(pos).setType(type);
		PublicData.UNIT unit = PublicData.getUnitFromString(((String)((Spinner)findViewById(R.id.spUnit)).getSelectedItem().toString()));
		adapter.list.get(pos).setUnit(unit);
		adapter.list.get(pos).setStepSize(Integer.parseInt(((EditText)findViewById(R.id.etStepSize)).getText().toString()));
		
		adapter.notifyDataSetChanged();
		super.onBackPressed();
	}
}
