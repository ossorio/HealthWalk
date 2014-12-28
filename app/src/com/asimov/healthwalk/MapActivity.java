package com.asimov.healthwalk;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	 // Mapa que sirve de base para indicar localizaciones
	protected GoogleMap mMap;
	protected  TextView texto;

	// Bandera que indica si se estan solicitando actualizaciones de ubicacion
	// sin los servicios de Google activados (LocationManager)
	protected boolean solicitandoActualizaciones;
	
	private LocalizadorUsuario gps;

	// Marcador que indica la ultima ubicacion actualizada
	protected Marker marcadorUbicacionActual;
	
	// Almacena la ubicacion actual
	protected Location location;
	
	// Almacena los ajustes para crear un marcador
	protected MarkerOptions opcionesMarcador;

	// TODO: Rellenar con datos de la BD 
	protected Location [] centrosSalud;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Inicializacion de los parametros basicos para la IU
		location = new Location("");
		opcionesMarcador = new MarkerOptions();
		// TODO: Si el terminal no tiene google services, mMap es null
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		texto = (TextView) findViewById(R.id.textoMapa);
		texto.setGravity(Gravity.CENTER);
		
		centrosSalud = new Location [42];
		
		// Se crea un localizador de ubicaciones
		gps = new LocalizadorUsuario(this);
		
		// TODO: rellenar con datos de la BD
		centrosSalud[0] = new Location("");
		centrosSalud[0].setLatitude(41.6344462);
		centrosSalud[0].setLongitude(-4.7478554);
		centrosSalud[1] = new Location("");
		centrosSalud[1].setLatitude(41.644327);
		centrosSalud[1].setLongitude(-4.7311999);
		gps.agregaMarcador(centrosSalud[0], "Centro de Salud nº 3", Utils.COLOR_MARCADOR_CENTRO_SALUD );
		gps.agregaMarcador(centrosSalud[1], "Centro de Salud nº 166", Utils.COLOR_MARCADOR_CENTRO_SALUD);
	}
	
	/*
	 * Lo llama el LocalizadorUsuario cuando existe una nueva localizacion disponible,
	 * dibuja la nueva localizacion y decide si ampliar o reducir los centros mostrados
	 */
	public void cambioLocalizacion(Location nueva_localizacion){
		
	}
	
	/*
	 * Dibuja un marcador con las coordenadas de localizacion en el mapa
	 */
	public void dibujarLocalizacion(Location localizacion){
		
	}
	
	@Override
	protected void onPause() {
		Log.d(Utils.ASIMOV, "OnPause()");
		super.onPause();
		
		if(gps.serviciosActivados){
			gps.pararActualizaciones();
		}else{
			gps.pararActualizacionesSinServicios();
		}
	}
	
	@Override
	protected void onStop() {
		Log.d(Utils.ASIMOV, "OnStop()");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(Utils.ASIMOV, "OnDestroy()");
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		Log.d(Utils.ASIMOV, "OnResume()");
		super.onResume();
		
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
		
		if(marcadorUbicacionActual != null){
			gps.eliminaMarcador(marcadorUbicacionActual);
			marcadorUbicacionActual = null;
		}
		// Si la localizacion obtenida es nula (aun no se ha calculado la actual)
		if(location.getLatitude() == 0 && location.getLongitude() == 0){
			gps.zoom(gps.VALLADOLID);
			texto.setText(getResources().getString(R.string.distanciaACentroSalud) + " " + getResources().getString(R.string.ubicando));
		}else{
		// Se centra la posicion del mapa en la ubicacion actual
		gps.muestraUbicacion(location, getResources().getString(R.string.ubicacionActual) );
		gps.muestraDistanciaCentroSalud(location);
		}
	}
	
	@Override
	protected void onStart() {
		Log.d(Utils.ASIMOV, "OnStart()");
		super.onStart();
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
	}
}
	
