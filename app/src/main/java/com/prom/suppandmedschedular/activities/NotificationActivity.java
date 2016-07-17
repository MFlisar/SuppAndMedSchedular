package com.prom.suppandmedschedular.activities;

import com.prom.suppandmedschedular.helper.PublicData;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import com.prom.suppandmedschedular.R;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.Plan;

public class NotificationActivity extends Activity {

	private int alarm_id = 0;
	
	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        setContentView(R.layout.reminder);
        
        PublicData.init(this);
        StaticDatabase.init(this);
        
        
        alarm_id = getIntent().getExtras().getInt(getString(R.string.I_KEY_ALARM_ID));
        
        System.out.println("Alarm id in onCreate NotificationDisplay: " + alarm_id);
        int count = getIntent().getExtras().getInt(getString(R.string.I_KEY_COUNT));
    	long plan_id = getIntent().getExtras().getLong(getString(R.string.I_KEY_PLAN_ID));
    	Plan plan = StaticDatabase.getDataSource().getPlan(plan_id);
    	
    	((TextView)findViewById(R.id.tvTitle)).setText(plan.getName());
    	for (int i = 0; i < count; i++)
    	{
    		TextView tv = new TextView(this);
    	    tv.setText(getIntent().getExtras().getString(getString(R.string.I_KEY_ENTRY_PREFIX) + i));
    	    ((LinearLayout)findViewById(R.id.llList)).addView(tv);
    	}
    }

    public void onClick(View v) 
    {
    	((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(alarm_id);
    	
    	// Alarm aus Datenbank l�schen
    	StaticDatabase.getDataSource().getAlarm(alarm_id).delete();
    	
    	if (v.getId() == R.id.btOpenplanplaner)
    	{
	        Intent intent = new Intent(this, MainActivity.class);
	        intent.setAction(Intent.ACTION_MAIN);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	    }
        finish();    
    }
}
