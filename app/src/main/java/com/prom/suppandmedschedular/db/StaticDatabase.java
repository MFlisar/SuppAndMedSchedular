package com.prom.suppandmedschedular.db;

import android.content.Context;
import com.prom.suppandmedschedular.db.helper.MyDataSource;

public final class StaticDatabase {
	
	private static MyDataSource dataSource = null;
	private static Context mainContext = null;
	
	public static MyDataSource getDataSource()
	{
		if (dataSource == null)
		{
			dataSource = new MyDataSource(mainContext);
			dataSource.open();
		}
		return dataSource;		
	}
	
	public static void init(Context context)
	{
		mainContext = context;
	}

}
