package com.asimov.healthwalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.asimov.healthwalk.RepositorioLocalizaciones;


public class MainActivity extends Activity {
	private RepositorioLocalizaciones repositorio;
	private final String BASE_DATOS = "localizaciones";
	private final String TAG = "HealthWalk";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// TODO: gestionar ciclo de vida del repositorio
		repositorio = new RepositorioLocalizaciones(this, BASE_DATOS, TAG);

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
