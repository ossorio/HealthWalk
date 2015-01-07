package com.asimov.healthwalk;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * Actividad para las preferencias del sistema
 * @author Alejandro Lopez Espinosa
 * @author Oscar Gonzalez Ossorio
 */
public class PreferenciasActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MiFragmento()).commit();
		
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
	
	/**
	 * Esta clase estatica se utiliza en la clase PreferenciasActivity para 
	 * cargar las preferencias en un nuevo fragmento
	 */
	public static class MiFragmento extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
	
}