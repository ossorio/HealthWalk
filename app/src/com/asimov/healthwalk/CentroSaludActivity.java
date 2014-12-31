package com.asimov.healthwalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
	}
}