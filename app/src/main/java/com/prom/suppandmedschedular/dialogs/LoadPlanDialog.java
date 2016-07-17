package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.Plan;

public class LoadPlanDialog extends Dialog implements OnClickListener, OnItemClickListener {

	private MainActivity parent;
	private Plan selectedPlan = null;
	
	public LoadPlanDialog(MainActivity parent) {
		super(parent);
		this.parent = parent;
		setContentView(R.layout.load_plan);
		setTitle(R.string.load_plan);
		
		((ListView)findViewById(R.id.lvPlans)).setAdapter(new ArrayAdapter<Plan>(parent, android.R.layout.simple_list_item_single_choice, StaticDatabase.getDataSource().getAllPlans()));	
		((ListView)findViewById(R.id.lvPlans)).setOnItemClickListener(this);
		findViewById(R.id.btBack).setOnClickListener(this);
		findViewById(R.id.btLoad).setOnClickListener(this);			
	}
	
	public void onClick(View view) 
	{
		if (view.getId() == R.id.btBack) {
			dismiss();			
		}
		else if (view.getId() == R.id.btLoad)
		{
			PublicData.selectedPlan = selectedPlan;
			parent.update(true, true, false, false, -1, false, -1, false);
			dismiss();
		}
	}

	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		selectedPlan = (Plan)adapter.getItemAtPosition(position);
	}
}
