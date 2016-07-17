package com.prom.suppandmedschedular.list.row_classes;

import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.PublicData.TYPE;
import com.prom.suppandmedschedular.helper.PublicData.UNIT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import com.prom.suppandmedschedular.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.Substance;
import com.prom.suppandmedschedular.dialogs.EditSingleSubstanceDialog;

public class EditSubstanceAdapter extends BaseAdapter implements OnItemSelectedListener, OnClickListener
{
	private LayoutInflater mInflater;
	private Context context;
	private Dialog parentDialog;
	public List<Substance> list = new ArrayList<Substance>();
	public List<Substance> listToDelete = new ArrayList<Substance>();
	
	private EditSingleSubstanceDialog dialog;
	
	public EditSubstanceAdapter(Context parent, Dialog parentDialog, Substance[] list)
	{
		mInflater = LayoutInflater.from(parent);
		this.context = parent;
		this.parentDialog = parentDialog;
		this.list = new LinkedList<Substance>(Arrays.asList(list));
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
		list.set(pos, (Substance)item);
	}
	
	public long getItemId(int pos) 
	{
		return pos;
	}
	
	public View getView(final int pos, View convertView, ViewGroup parent) 
	{
		ViewHolderEditSubstance holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.row_substance, null);	
			holder = new ViewHolderEditSubstance();
			
			holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
			
			holder.btEdit = (Button)convertView.findViewById(R.id.btEdit);
			holder.btEdit.setId(R.id.key_edit_substance);
			holder.btEdit.setOnClickListener(this);
			
			holder.btDelete = (Button)convertView.findViewById(R.id.btDelete);
			holder.btDelete.setId(R.id.key_delete_substance);
			holder.btDelete.setOnClickListener(this);
			
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolderEditSubstance) convertView.getTag();
		}
		
		holder.index = pos;
		holder.btEdit.setTag(pos);
		holder.btDelete.setTag(pos);
		holder.tvName.setText(list.get(pos).getName());
		return convertView;
	}
	
	public void onClick(View v)
	{
		if (v.getId() == R.id.btAdd)
		{
			Substance newSubstance = new Substance(PublicData.TYPE.SUPP, PublicData.UNIT.MG, "", PublicData.NONE, 50);
			list.add(newSubstance);
			notifyDataSetChanged();
		}
		else if (v.getId() == R.id.key_edit_substance)
		{
			int pos = Integer.parseInt(v.getTag().toString());
			dialog = new EditSingleSubstanceDialog(context, this, pos);
			dialog.show();
		}
		else if (v.getId() == R.id.btSave)
		{
			for (int i = 0; i < getCount(); i++)
			{
				// 1) Kein Steroid => BasisSteroid ist NONE
				// 2) Steroid => Einheit in mg
				if (((Substance)getItem(i)).getType() != TYPE.STEROID)
					((Substance)getItem(i)).setBaseSteroidID(PublicData.NONE);
				else
					((Substance)getItem(i)).setUnit(UNIT.MG);
				
				// 2) Einheit St�ck ht immer Schrittweite 1
				if (((Substance)getItem(i)).getUnit() == UNIT.PIECES)
					((Substance)getItem(i)).setStepSize(1);
				
				((Substance)getItem(i)).update();
			}
			for (int i = 0; i < listToDelete.size(); i++)
				listToDelete.get(i).delete();
			parentDialog.dismiss();
		}
		else if (v.getId() == R.id.key_delete_substance)
		{
			int pos = Integer.parseInt(v.getTag().toString());
			if (!StaticDatabase.getDataSource().checkIfSubstanceIsUsed(list.get(pos).getID()))
			{
				Substance substanceToRemove = list.remove(pos);
				listToDelete.add(substanceToRemove);
			}
			else
				PublicData.showToast(context, R.string.cant_delete_used_substance);
			notifyDataSetChanged();
		}
	}

	public void onItemSelected(AdapterView<?> parent,
			View view, int pos, long id) {
		
		if (parent.getId() == R.id.spType)
		{
			PublicData.TYPE type = PublicData.getTypeFromString(((String)parent.getItemAtPosition(pos)));
			PublicData.UNIT unit = PublicData.getUnitFromString(((Spinner)dialog.findViewById(R.id.spUnit)).getItemAtPosition(pos).toString());
			if (type != TYPE.STEROID)
			{
				dialog.findViewById(R.id.spBaseSteroid).setVisibility(View.INVISIBLE);
				dialog.findViewById(R.id.spUnit).setVisibility(View.VISIBLE);
				if (unit == UNIT.MG)
					dialog.findViewById(R.id.etStepSize).setVisibility(View.VISIBLE);
				else
					dialog.findViewById(R.id.etStepSize).setVisibility(View.INVISIBLE);
			}
			else
			{
				dialog.findViewById(R.id.spBaseSteroid).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.spUnit).setVisibility(View.INVISIBLE);
				dialog.findViewById(R.id.etStepSize).setVisibility(View.VISIBLE);
			}
		}
		else if (parent.getId() == R.id.spUnit)
		{
			PublicData.UNIT unit = PublicData.getUnitFromString(((String)parent.getItemAtPosition(pos)));
			if (unit == UNIT.MG)
				dialog.findViewById(R.id.etStepSize).setVisibility(View.VISIBLE);
			else
				dialog.findViewById(R.id.etStepSize).setVisibility(View.INVISIBLE);
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
}