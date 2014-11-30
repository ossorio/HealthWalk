package com.asimov.healthwalk;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	private RepositorioLocalizaciones repositorio;
	private final String BASE_DATOS = "localizaciones";
	private final String TAG = "HealthWalk";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "Lanzando MainActivity");
		// TODO: gestionar ciclo de vida del repositorio
		repositorio = new RepositorioLocalizaciones(this, BASE_DATOS, TAG);
		
		// Testear el repositorio
		Location loc = new Location("");
		loc.setLatitude(41.6344462);
		loc.setLongitude(-4.7478554);
		Cursor centros_salud = repositorio.getCentrosSalud(loc, 1000);
		if(centros_salud != null){
			centros_salud.moveToFirst();
			Log.d(TAG, Integer.toString(centros_salud.getCount()));
		}

		SetupButtonHelp();
		SetupButtonMuyGrave();
		SetupButtonGrave();
		SetupButtonNormal();
		SetupButtonLeve();
	}
	
	private void SetupButtonHelp() {
		Button buttonHelp = (Button) findViewById(R.id.buttonHelp);
		buttonHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, HelpActivity.class));
			}
		});
	}
	
	private void SetupButtonMuyGrave() {
		Button buttonHelp = (Button) findViewById(R.id.buttonMuyGrave);
		buttonHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	private void SetupButtonGrave() {
		Button buttonHelp = (Button) findViewById(R.id.buttonGrave);
		buttonHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	private void SetupButtonNormal() {
		Button buttonHelp = (Button) findViewById(R.id.buttonNormal);
		buttonHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	private void SetupButtonLeve() {
		Button buttonHelp = (Button) findViewById(R.id.buttonLeve);
		buttonHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
}
