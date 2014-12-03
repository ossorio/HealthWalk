package com.asimov.healthwalk;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	// TODO: Moverlo a un recurso
	
	
	 // Mapa que sirve de base para indicar localizaciones
	protected GoogleMap mMap;
	protected  TextView texto;

	// Bandera que indica si se estan solicitando actualizaciones de ubicacion
	// sin los servicios de Google activados (LocationManager)
	private boolean solicitandoActualizaciones;
	
	private LocalizadorUsuario gps;
	// TODO: Cual es la diferencia entre marcador y location?
	// Marcador que indica la ultima ubicacion actualizada
	// TODO: Por que es estatico?
	protected Marker marcadorUbicacionActual;
	// Almacena la ubicacion actual
	private Location location;
	
	// Indica si los servicios de Google Play están activados
	protected boolean serviciosActivados;
	
	protected MarkerOptions opcionesMarcador;

	// TODO: Rellenar con datos de la BD 
	protected Location [] centrosSalud;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		serviciosActivados = servicesConnected();
		
		opcionesMarcador = new MarkerOptions();
		// Inicializacion de los parametros basicos para la IU
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
	
	@Override
	protected void onPause() {
		Log.d(Utils.ASIMOV, "OnPause()");
		super.onPause();
		if(serviciosActivados){
			if(gps.google_API_client.isConnected())
				gps.pararActualizaciones();
			gps.google_API_client.disconnect();
		}else{
			if(solicitandoActualizaciones){
				gps.pararActualizacionesSinServicios();
				solicitandoActualizaciones = false;
			}
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
		if(serviciosActivados){
			if(!gps.google_API_client.isConnected())
				gps.google_API_client.connect();
		}else{
			if(!solicitandoActualizaciones)
				gps.solicitarActualizacionesSinServicios();
				solicitandoActualizaciones = true;
		}
		location = gps.getLocation();
		if(marcadorUbicacionActual != null){
			gps.eliminaMarcador(marcadorUbicacionActual);
			marcadorUbicacionActual = null;
		}
		// Si la localizacion obtenida es nula, porque aun no se ha calculado la actual.
		if(location.getLatitude() == 0 && location.getLongitude() == 0){
			gps.zoom(gps.VALLADOLID);
			texto.setText(getResources().getString(R.string.distanciaACentroSalud) + " " + getResources().getString(R.string.ubicando));
		}else{
		// TODO: Se deberian eliminar los marcadores de los centros sociales?
		// Se centra la posicion del mapa en la ubicacion actual
		gps.muestraUbicacion(location, getResources().getString(R.string.ubicacionActual) );
		gps.muestraDistanciaCentroSalud(location);
		}
	}
	@Override
	protected void onStart() {
		Log.d(Utils.ASIMOV, "OnStart()");
		super.onStart();
		if(serviciosActivados){
			gps.google_API_client.connect();
		}else{
			gps.solicitarActualizacionesSinServicios();
			solicitandoActualizaciones = true;
		}
	}
	
	private boolean servicesConnected() {
		// Comprueba que los servicios de Google Play estén disponibles
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// Si los servicios de Google Play están disponibles
		if (ConnectionResult.SUCCESS == resultCode) {
			// Escritura en el log para depuracion
			Log.d(Utils.ASIMOV,
					"Los servicios de Google Play están disponibles.");
			// Confirmacion de que los servicios estan disponibles
			return true;
			// Los servicios de Google Play no estan disponibles por alguna razon.
		} else {
			Log.d(Utils.ASIMOV,"Los servicios de Google Play NO están disponibles.");
			return false;
		}
	}
	
//	/*
//	 * Muestra un marcador sobre la ubicacion con una etiqueta
//	 */
//	private Marker agregaMarcador(Location location, String etiqueta, float color){
//		LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
//		opcionesMarcador.position(posicion);
//		opcionesMarcador.title(etiqueta);
//		opcionesMarcador.icon(BitmapDescriptorFactory
//		        .defaultMarker(color));
//		
//		return mMap.addMarker(opcionesMarcador);
//	}
//	
//	protected void eliminaMarcador(Marker marker){
//		marker.remove();
//	}
//	
//	/*
//	 * Primero muestra un marcador sobre la ubicacion y despues realiza un
//	 * "zoom" sobre la posicion
//	 */
//	protected void muestraUbicacion(Location location, String etiqueta){
//		marcadorUbicacionActual = agregaMarcador(location, etiqueta, COLOR_MARCADOR_UBICACION_ACTUAL);
//		zoom(location);
//	}
//	
//	/*
//	 * Situa la panoramica del mapa sobre una ubicacion
//	 */
//	private void zoom(Location location){
//		LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
//		// TODO: sustituir el 13.5 por una constante
//		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, ZOOM_LEVEL));
//	}
//	
//	private float calculaDistanciaCentroSalud(Location locActual){
//		float minDistancia = Math.round(locActual.distanceTo(centrosSalud[0]));
//		float distancia;
//		int i = 1;
//
//		while(centrosSalud[i] != null && i < centrosSalud.length){
//			distancia = Math.round(locActual.distanceTo(centrosSalud[i]));
//			if(distancia < minDistancia)
//				minDistancia =	distancia;
//				i++;
//		}
//
//		if(minDistancia >= 1000){
//			minDistancia -= minDistancia % 10;
//			minDistancia /= 1000;
//			UNIDAD_DISTANCIA = "Km";
//		}else{
//			UNIDAD_DISTANCIA = "metros";
//		}
//
//		return minDistancia;
//	}
//	
//	protected void muestraDistanciaCentroSalud(Location locActual){
//		float distancia = calculaDistanciaCentroSalud(locActual);
//		texto.setText(R.string.distanciaACentroSalud + distancia + " " + UNIDAD_DISTANCIA);
//	}
//	
//	private boolean servicesConnected() {
//		// Comprueba que los servicios de Google Play estén disponibles
//		int resultCode =
//				GooglePlayServicesUtil.
//				isGooglePlayServicesAvailable(this);
//		// Si los servicios de Google Play están disponibles
//		if (ConnectionResult.SUCCESS == resultCode) {
//			// Escritura en el log para depuracion
//			Log.d(ASIMOV,
//					"Los servicios de Google Play están disponibles.");
//			// Confirmacion de que los servicios estan disponibles
//			return true;
//			// Los servicios de Google Play no estan disponibles por alguna razon.
//		} else {
//			Log.d(ASIMOV,"Los servicios de Google Play NO están disponibles.");
//			return false;
//		}
//	}

	
}
