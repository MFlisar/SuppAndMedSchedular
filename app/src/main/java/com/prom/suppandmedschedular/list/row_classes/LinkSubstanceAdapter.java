package com.prom.suppandmedschedular.list.row_classes;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.classes.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import com.prom.suppandmedschedular.R;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.prom.suppandmedschedular.db.classes.Plan.INDEX_TYPE;
import com.prom.suppandmedschedular.db.classes.Substance;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;
import com.prom.suppandmedschedular.dialogs.EditSingleSubstanceDialog;

public class LinkSubstanceAdapter extends BaseAdapter implements OnItemSelectedListener
{	
	private LayoutInflater mInflater;
	private Context context;
	private Dialog parentDialog;
	public List<SubstanceToTake> list = new ArrayList<SubstanceToTake>();
	public List<Link> indizesAndChildIndizes;
	private List<View> views = null;
	private int init = 0;
	
	private EditSingleSubstanceDialog dialog;
	
	public LinkSubstanceAdapter(Context parent, Dialog parentDialog, List<SubstanceToTake> list, List<Link> indizesAndChildIndizes)
	{
		mInflater = LayoutInflater.from(parent);
		this.context = parent;
		this.parentDialog = parentDialog;
		this.list = list;
		views = new ArrayList<View>();
		for (int i = 0; i < list.size(); i++)
			views.add(null);
		this.indizesAndChildIndizes = indizesAndChildIndizes;
	}
	
	public int getCount()
	{
		return list.size();
	}
	
	public Object getItem(int pos) 
	{
		return list.get(pos);
	}
	
	public void setItem(int pos, Object item) 
	{
		list.set(pos, (SubstanceToTake)item);
	}
	
	public long getItemId(int pos) 
	{
		return pos;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public View getView(final int pos, View convertView, ViewGroup parent) 
	{
		int posAdjusted = views.size() - 1 - pos;
		if (views.get(posAdjusted) == null)
		{
			ViewHolderLinkSubstance holder = new ViewHolderLinkSubstance();
			convertView = mInflater.inflate(R.layout.row_link_data, null);	
			views.set(posAdjusted, convertView);
			
			holder.tvSubstance = (TextView)convertView.findViewById(R.id.tvSubstance);
			holder.tvSubstance.setText(list.get(posAdjusted).getSubstance().getName());
			
			holder.spSubstances = (Spinner)convertView.findViewById(R.id.spSubstances);
			holder.spSubstances.setTag(posAdjusted);
			Vector<SubstanceToTake> currentList = new Vector<SubstanceToTake>();
			currentList.addAll(list);
			currentList.remove(posAdjusted);
			SubstanceToTake substancetoTakeZero = new SubstanceToTake();
			Substance substance = new Substance();
			substance.setName("-");
			substancetoTakeZero.setSubstance(substance);
			currentList.insertElementAt(substancetoTakeZero, 0);
			holder.spSubstances.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, currentList));
			((ArrayAdapter<SubstanceToTake>)holder.spSubstances.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			
			
			// Link in Spinner, falls Item linked ist
			// im Spinner ist Parent
			// posAdjusted ist das Child
			
			List<Integer> l = PublicData.selectedPlan.getIndizes(INDEX_TYPE.GRAPH_RELEVANT);
			
			for (int k = 0; k < l.size(); k++)
			{
				if (l.get(k) == posAdjusted)
					posAdjusted = k;
			}	
			
			for (int i = 0; i < indizesAndChildIndizes.size(); i++)		
				for (int j = 0; j < indizesAndChildIndizes.get(i).getCountChildren(); j++)
					if (indizesAndChildIndizes.get(i).getChildAt(j) == indizesAndChildIndizes.get(posAdjusted).getParent())
					{	
						int parentIndex = indizesAndChildIndizes.get(i).getParent();

						for (int k = 0; k < l.size(); k++)
						{
							if (l.get(k) == parentIndex)
								parentIndex = k;
						}
						
						int index = parentIndex + 1;
						if (parentIndex >= posAdjusted)
							index--;
						holder.spSubstances.setSelection(index);
					}
			holder.spSubstances.setOnItemSelectedListener(this);

			convertView.setTag(holder);
		} 
		else if (convertView == null && views.get(posAdjusted) != null)
		{
			convertView = views.get(posAdjusted);
			//views.set(posAdjusted, (ViewHolderLinkSubstance)convertView.getTag());
		}
		
		
		updateVisibility();
		
		// Todo update lists!!!

		return convertView;
	}

	private void updateVisibility()
	{
		// alle Views checken
		for (int i = 0; i < views.size(); i++)
		{
			if (views.get(i) != null)
			{
				boolean isParent = false;
				isParent = indizesAndChildIndizes.get(i).getCountChildren() != 0;
//				for (int j = 0; j < indizesAndChildIndizes.size(); j++)
//				{
//					if (indizesAndChildIndizes.get(j).getParent() == i && 
//						indizesAndChildIndizes.get(j).getCountChildren() > 0)
//					{
//						isParent = true;
//						break;
//					}
//				}
				
				if (isParent)
					System.out.println(i + " is parent: " + ((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.getAdapter().getItem(((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.getSelectedItemPosition()).toString());
				else
					System.out.println(i + " is NOT parent: " + ((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.getAdapter().getItem(((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.getSelectedItemPosition()).toString());
				
				if (isParent)
					((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.setVisibility(View.INVISIBLE);
				else
					((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.setVisibility(View.VISIBLE);
				((ViewHolderLinkSubstance)views.get(i).getTag()).spSubstances.invalidate();
			}
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		
		if (init != list.size())
		{
			init++;
			return;
		}
		
		// pos = Index in Spinner => Parent
		// tag = Index des Spinners => Child	
		int spinnerIndex = Integer.parseInt(parent.getTag().toString());
		int childIndex = indizesAndChildIndizes.get(spinnerIndex).getParent();
		
		int parentIndex = pos - 1; // "-" wird abgez�hlt
		if (pos != 0)
		{
			if (parentIndex >= spinnerIndex) // Indexerh�hung f�r das eine entfernte Element
				parentIndex++;
			parentIndex = indizesAndChildIndizes.get(parentIndex).getParent();
		}
		
		// 1) checken, ob Child schon einen Parent hat und dieser ein anderer als der neue ist
		// => falls ja, l�schen
		boolean pairExists = false;
		int existingParent = -1;
		Pair<Integer, Integer> toDelete = new Pair<Integer, Integer>(-1, -1);
		for (int i = 0; i < indizesAndChildIndizes.size(); i++)
		{
			if (indizesAndChildIndizes.get(i).getParent() == parentIndex)
				existingParent = i;
			for (int j = 0; j < indizesAndChildIndizes.get(i).getCountChildren(); j++)
			{
				if (indizesAndChildIndizes.get(i).getParent() == parentIndex && 
					indizesAndChildIndizes.get(i).getChildAt(j) == childIndex)
						pairExists = true;
				else if (indizesAndChildIndizes.get(i).getParent() != parentIndex && 
					indizesAndChildIndizes.get(i).getChildAt(j) == childIndex)
					toDelete = new Pair<Integer, Integer>(i, j);
			}
		}
		
		if (toDelete.first != -1)
			indizesAndChildIndizes.get(toDelete.first).removeChildAt(toDelete.second);
		
		// 2) wenn child mit Parent noch nicht existiert hat, Child mit Parent hinzuf�gen
		if (!pairExists && existingParent != -1)
			indizesAndChildIndizes.get(existingParent).addChildIfNotExists(childIndex);

		updateVisibility();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}