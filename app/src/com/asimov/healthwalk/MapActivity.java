package com.asimov.test_healthwalk;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HealthWalk extends Activity {
	// TODO: Moverlo a un recurso
	private final String STRING_AQUI = "Aquí";
	private static String UNIDAD_DISTANCIA;

	 // Mapa que sirve de base para indicar localizaciones
	private static GoogleMap mMap;
	private static TextView texto;

	private Localizador gps;
	// TODO: Cual es la diferencia entre marcador y location?
	// Marcador que indica la ultima ubicacion actualizada
	// TODO: Por que es estatico?
	protected static Marker marcador;
	// Almacena la ubicacion actual
	private Location location;

	// TODO: Rellenar con datos de la BD 
	private static Location [] centrosSalud = new Location [42];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmento_mapa);
		
		// Inicializacion de los parametros basicos para la IU
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		texto = (TextView) findViewById(R.id.textoMapa);
		texto.setGravity(Gravity.CENTER);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		// Se crea un localizador de ubicaciones
		gps = new Localizador(this);
		
		// TODO: rellenar con datos de la BD
		centrosSalud[0] = new Location("");
		centrosSalud[0].setLatitude(41.6344462);
		centrosSalud[0].setLongitude(-4.7478554);
		centrosSalud[1] = new Location("");
		centrosSalud[1].setLatitude(41.644327);
		centrosSalud[1].setLongitude(-4.7311999);
		agregaMarcador(centrosSalud[0], "Centro de Salud nº 3" );
		agregaMarcador(centrosSalud[1], "Centro de Salud nº 166");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gps.google_API_client.disconnect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gps.google_API_client.disconnect();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!gps.google_API_client.isConnecting())
			gps.google_API_client.reconnect();
		location = gps.getLocation();
		if(marcador != null){
			eliminaMarcador(marcador);
			marcador = null;
		}
		// TODO: Se deberian eliminar los marcadores de los centros sociales?
		// Se centra la posicion del mapa en la ubicacion actual
		muestraUbicacion(location, STRING_AQUI);
		muestraDistanciaCentroSalud(location);
	}
	@Override
	protected void onStart() {
		super.onStart();
		gps.google_API_client.connect();
	}
	
	/*
	 * Muestra un marcador sobre la ubicacion con una etiqueta
	 */
	private static Marker agregaMarcador(Location location, String label){
		LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
		MarkerOptions marcador = new MarkerOptions();
		marcador.position(posicion);
		marcador.title(label);

		return mMap.addMarker(marcador);
	}
	
	protected static void eliminaMarcador(Marker marker){
		marker.remove();
	}
	
	/*
	 * Primero muestra un marcador sobre la ubicacion y despues realiza un
	 * "zoom" sobre la posicion
	 */
	protected static void muestraUbicacion(Location location, String label){
		marcador = agregaMarcador(location, label);
		zoom(location);
	}
	
	/*
	 * Situa la panoramica del mapa sobre una ubicacion
	 */
	private static void zoom(Location location){
		LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
		// TODO: sustituir el 13.5 por una constante
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.5f));
	}
	
	private static float calculaDistanciaCentroSalud(Location locActual){
		float minDistancia = Math.round(locActual.distanceTo(centrosSalud[0]));
		float distancia;
		int i = 1;

		while(centrosSalud[i] != null && i < centrosSalud.length){
			distancia = Math.round(locActual.distanceTo(centrosSalud[i]));
			if(distancia < minDistancia)
				minDistancia =	distancia;
				i++;
		}

		if(minDistancia >= 1000){
			minDistancia -= minDistancia % 10;
			minDistancia /= 1000;
			UNIDAD_DISTANCIA = "Km";
		}else{
			UNIDAD_DISTANCIA = "metros";
		}

		return minDistancia;
	}
	
	protected static void muestraDistanciaCentroSalud(Location locActual){
		float distancia = calculaDistanciaCentroSalud(locActual);
		texto.setText("Distancia: " + distancia + " " + UNIDAD_DISTANCIA);
	}
}
