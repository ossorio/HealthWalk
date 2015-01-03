package com.asimov.healthwalk;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * Actividad para las preferencias del sistema
 * @author Alejandro Lopez Espinosa
 */
public class PreferenciasActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PreferenceFragment(){
					@Override
					public void onCreate(Bundle savedInstanceState){
						super.onCreate(savedInstanceState);
						addPreferencesFromResource(R.xml.preferences);
					}
				})
				.commit();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		NavUtils.navigateUpFromSameTask(this);
	    		return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}