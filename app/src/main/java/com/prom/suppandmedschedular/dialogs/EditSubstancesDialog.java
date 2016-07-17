package com.prom.suppandmedschedular.dialogs;

import com.prom.suppandmedschedular.list.row_classes.EditSubstanceAdapter;
import com.prom.suppandmedschedular.activities.MainActivity;
import android.app.Dialog;
import com.prom.suppandmedschedular.R;
import android.widget.ListView;
import com.prom.suppandmedschedular.db.StaticDatabase;

public class EditSubstancesDialog extends Dialog {

	//private planplanerActivity parent;
	
	public EditSubstancesDialog(MainActivity parent) {
		super(parent);
		//this.parent = parent;
		setContentView(R.layout.edit_substances);
		setTitle(R.string.edit_substances);

		// Steroid
		((ListView)findViewById(R.id.lvSteroids)).setAdapter(new EditSubstanceAdapter(parent, this, StaticDatabase.getDataSource().getAllSubstances()));
		
		findViewById(R.id.btAdd).setOnClickListener((EditSubstanceAdapter)((ListView)findViewById(R.id.lvSteroids)).getAdapter());
		findViewById(R.id.btSave).setOnClickListener((EditSubstanceAdapter)((ListView)findViewById(R.id.lvSteroids)).getAdapter());
	}
}
