package com.prom.suppandmedschedular.activities;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.InputType;

import com.prom.suppandmedschedular.R;

public class PreferencesActivity extends PreferenceActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        EditTextPreference pref = (EditTextPreference)findPreference(getString(R.string.PREF_MAX_WEEK_COUNT));
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        
        pref = (EditTextPreference)findPreference(getString(R.string.PREF_MAX_FRONTLOAD_DAY));
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
