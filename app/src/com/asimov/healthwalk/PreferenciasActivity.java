package com.asimov.healthwalk;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

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
	}
}