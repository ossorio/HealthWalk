package com.asimov.healthwalk;

import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Actividad para mostrar los centros de salud
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class CentroSaludActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d(Utils.ASIMOV, "CentroSaludActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_centro_salud);

		Bundle bundle = getIntent().getExtras();
		final CentroSalud centro_salud = (CentroSalud) bundle.getSerializable("centro_salud");
		
		TextView nombre = (TextView) findViewById(R.id.nombre);
		nombre.setText(centro_salud.getNombre());

		TextView direccion = (TextView) findViewById(R.id.direccion);
		direccion.setText(getString(R.string.direccion) + " " + centro_salud.getDireccion());

		TextView ciudad = (TextView) findViewById(R.id.ciudad);
		ciudad.setText(getString(R.string.ciudad) + " " + centro_salud.getCiudad());

		TextView telefono = (TextView) findViewById(R.id.telefono);
		telefono.setText(getString(R.string.telefono) + " " + centro_salud.getTelefono());
		
		// En el caso de que se pulse el boton Llamar de la actividad, se realiza
		// una llamada al centro de salud seleccionado
		Button llamar = (Button) findViewById(R.id.llamar);
		llamar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + centro_salud.getTelefono()));
				startActivity(intent);
			}
		});
		
		// Si se pulsa el boton Ruta de la actividad, se calcula la ruta en Google Maps.
		// Si Google Maps no esta instalado en el dispositivo, se abre su pagina de instalacion
		// Google Play
		Button ruta = (Button) findViewById(R.id.ruta);
		ruta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Location locCentroSalud = centro_salud.getLocalizacion();
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f&dirflg=%s", locCentroSalud.getLatitude(), locCentroSalud.getLongitude(), Utils.getModoDesplazamiento());
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				try
				{
					startActivity(intent);
				}
				catch(ActivityNotFoundException ex)
				{

					Intent intentInstalar = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
					startActivity(intentInstalar);

					finish();

				}       
			}
		});

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