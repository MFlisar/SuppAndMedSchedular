package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.classes.Link;

import java.util.ArrayList;
import java.util.List;

import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectSubstancesToShowInGraphDialog extends Dialog implements OnClickListener {

	private MainActivity parentActivity;
	private List<Link> linksParentsOnly;
	private int parent;
	private int child;
	
	public SelectSubstancesToShowInGraphDialog(MainActivity parentActivity) {
		super(parentActivity);
		this.parentActivity = parentActivity;
		setContentView(R.layout.select_substances_to_show_in_graph);
		setTitle(R.string.select_substances_to_show);
		
		linksParentsOnly = PublicData.selectedPlan.getLinksForGraph(true);
		
		//indizes = PublicData.selectedPlan.gets.getSubstancesToTakeIndizesThatAreForGraph();
		//List<SubstanceToTake> substances = PublicData.selectedPlan.getCopyOfSubstancesToTakeThatAreForGraph();
		List<String> items = new ArrayList<String>();
		for (int i = 0; i < linksParentsOnly.size(); i++)
		{
			parent = linksParentsOnly.get(i).getParent();
			items.add(PublicData.selectedPlan.getSubstancesToTake().get(parent).getSubstance().getName());
			if (linksParentsOnly.get(i).getCountChildren() > 0)
				items.set(i, items.get(i) + ", " + linksParentsOnly.get(i).getChildrenString(PublicData.selectedPlan.getSubstancesToTake()));
		}
		((ListView)findViewById(R.id.lvSubstances)).setAdapter(new ArrayAdapter<String>(parentActivity, android.R.layout.simple_list_item_multiple_choice, items));	
		
		// checken ob Parent visible
		for (int i = 0; i < linksParentsOnly.size(); i++)
		{
			boolean visible = PublicData.selectedPlan.getSubstancesToTake().get(linksParentsOnly.get(i).getParent()).getShowInGraph();
			((ListView)findViewById(R.id.lvSubstances)).setItemChecked(i, visible);
		}
		((ListView)findViewById(R.id.lvSubstances)).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		findViewById(R.id.btBack).setOnClickListener(this);
		findViewById(R.id.btSave).setOnClickListener(this);			
	}
	
	public void onClick(View view) 
	{
		if (view.getId() == R.id.btBack) {
			dismiss();			
		}
		else if (view.getId() == R.id.btSave)
		{
		
			SparseBooleanArray sbArray = ((ListView)findViewById(R.id.lvSubstances)).getCheckedItemPositions();
			for (int i = 0; i < linksParentsOnly.size(); i++)
			{
				parent = linksParentsOnly.get(i).getParent();
				PublicData.selectedPlan.getSubstancesToTake().get(parent).setShowInGraph(sbArray.get(i));
				PublicData.selectedPlan.getSubstancesToTake().get(parent).update();
				for (int j = 0; j < linksParentsOnly.get(i).getCountChildren(); j++)
				{
					child =linksParentsOnly.get(i).getChildAt(j);
					PublicData.selectedPlan.getSubstancesToTake().get(child).setShowInGraph(sbArray.get(i));
					PublicData.selectedPlan.getSubstancesToTake().get(child).update();
				}
			}

			PublicData.selectedPlan.update();
			parentActivity.update(false, false, false, false, -1, false, -1, true);
			dismiss();
		}
	}
}
