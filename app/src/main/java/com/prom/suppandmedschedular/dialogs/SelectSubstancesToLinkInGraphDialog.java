package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.classes.Link;

import java.util.ArrayList;
import java.util.List;

import com.prom.suppandmedschedular.list.row_classes.LinkSubstanceAdapter;
import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;

public class SelectSubstancesToLinkInGraphDialog extends Dialog implements OnClickListener {

	private MainActivity parentActivity;
	//private List<Integer> indizesForGraph;
	private LinkSubstanceAdapter adapter;
	private int parent;
	private int child;
	
	public SelectSubstancesToLinkInGraphDialog(MainActivity parentActivity) {
		super(parentActivity);
		this.parentActivity = parentActivity;
		setContentView(R.layout.select_substances_to_show_in_graph);
		setTitle(R.string.link_data_in_graph);
		
		//indizesForGraph = PublicData.selectedPlan.getIndizes(INDEX_TYPE.GRAPH_RELEVANT);
		
		List<SubstanceToTake> substances = new ArrayList<SubstanceToTake>(PublicData.selectedPlan.getSubstancesToTake());
		for (int i = substances.size() - 1; i >= 0; i--)
			if (!substances.get(i).isSubstanceForGraph())
				substances.remove(i);
		List<Link> links = PublicData.selectedPlan.getLinksForGraph(false);
		
		adapter = new LinkSubstanceAdapter(parentActivity, this, substances, links);
		((ListView)findViewById(R.id.lvSubstances)).setAdapter(adapter);

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
			PublicData.selectedPlan.clearLinkedTo();
			for (int i = 0; i < adapter.indizesAndChildIndizes.size(); i++)
			{
				parent = adapter.indizesAndChildIndizes.get(i).getParent();
				boolean visible = PublicData.selectedPlan.getSubstancesToTake().get(parent).isSubstanceForGraph();
				for (int j = 0; j < adapter.indizesAndChildIndizes.get(i).getCountChildren(); j++)
				{
					child = adapter.indizesAndChildIndizes.get(i).getChildAt(j);
					// index anpassen
					System.out.println("parent: " + parent + ", child: " +  child);
					PublicData.selectedPlan.addLink(parent, child);
					PublicData.selectedPlan.getSubstancesToTake().get(child).setShowInGraph(visible);
				}
			}

			PublicData.selectedPlan.update();
			parentActivity.update(true, false, false, false, -1, false, -1, true);
			dismiss();
		}
	}
}
