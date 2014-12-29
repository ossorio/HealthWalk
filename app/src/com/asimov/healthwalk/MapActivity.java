package com.asimov.healthwalk;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Actividad que muestra el mapa con los centros de salud mas cercanos, es la
 * responsable de dibujar la IU y de implementar los controles de la misma
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class MapActivity extends Activity implements ObservadorLocalizaciones{
	 // Partes del layout que hay que actualizar con cada nueva localizacion
	protected GoogleMap mMap;
	protected  TextView texto;

	// TODO: Rellenar con datos de la BD, deberia ser un array de Markers
	protected Location [] centrosSalud;
	
	// Almacena la ubicacion actual y su marcador
	protected Location localizacion_actual;
	protected Marker marcadorUbicacionActual;

	// Localizador que llama a esta clase cada vez que hay una nueva actualizacion
	// disponible
	private LocalizadorUsuario gps;

	// Bandera que indica si se estan solicitando actualizaciones de ubicacion
	// sin los servicios de Google activados (LocationManager)
	protected boolean solicitandoActualizaciones;
	
    // Variables para calcular la distancia al centro de salud mas proximo
    private float minDistancia;
    private float distancia;
    // TODO: en Utils?
	private String UNIDAD_DISTANCIA;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		texto = (TextView) findViewById(R.id.textoMapa);
		texto.setGravity(Gravity.CENTER);
		
		centrosSalud = new Location [42];
		
		// Se crea un localizador de ubicaciones
		gps = new LocalizadorUsuario(this);
		
		// TODO: inicializar el repositorio
	}
	
	/**
	 * Lo llama el LocalizadorUsuario cuando existe una nueva localizacion disponible,
	 * dibuja la nueva localizacion y decide si ampliar o reducir los centros mostrados
	 * @param nueva_localizacion Localizacion actualizada del usuario
	 */
	@Override
	public void cambioLocalizacion(Location nueva_localizacion){
		// TODO: actualizar la localizacion de los centros de salud cuando salgamos del radio
		localizacion_actual = nueva_localizacion;
    	actualizaMarcadorUsuario();
    	muestraDistanciaCentroSalud();
	}
	
	/**
	 * Primero crea un marcador sobre la nueva ubicacion y despues realiza un
	 * "zoom" sobre la posicion
	 * @param nueva_localizacion Nueva localizacion del marcador de usuario
	 */
	protected void actualizaMarcadorUsuario(){
		if(marcadorUbicacionActual != null){
			eliminaMarcador(marcadorUbicacionActual);
			marcadorUbicacionActual = null;
		}

		if(localizacion_actual != null){
			// TODO: la etiqueta deberia ser el nombre del centro de salud
			String etiqueta = getString(R.string.ubicacionActual);
			LatLng posicion = new LatLng(localizacion_actual.getLatitude(), localizacion_actual.getLongitude());
			marcadorUbicacionActual = agregaMarcador(localizacion_actual, etiqueta, Utils.COLOR_MARCADOR_UBICACION_ACTUAL);

			// Situa la panoramica del mapa sobre una ubicacion
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, Utils.ZOOM_LEVEL));
		}else{
			// TODO: hacer un zoom sobre cyl?
		}
	}

	/**
	 * Muestra un marcador sobre la ubicacion con una etiqueta
	 * @param location localizacion del marcador
	 * @param etiqueta etiqueta asignada al marcador
	 * @param color color del marcador
	 * @return el nuevo marcador agregado al mapa
	 */
	protected Marker agregaMarcador(Location localizacion, String etiqueta, float color){
		LatLng posicion = new LatLng(localizacion.getLatitude(), localizacion.getLongitude());
		MarkerOptions opcionesMarcador = new MarkerOptions();
		opcionesMarcador.position(posicion);
		opcionesMarcador.title(etiqueta);
		opcionesMarcador.icon(BitmapDescriptorFactory.defaultMarker(color));
		
		return mMap.addMarker(opcionesMarcador);
	}
	
	/**
	 * Elimina marcador del mapa
	 * @param marker marcador a eliminar
	 */
	protected void eliminaMarcador(Marker marker){
    	if(marker != null){
    		marker.remove();
    	}
	}
	
	/**
	 * Muestra la distancia al centro de salud más cercano
	 */
	protected void muestraDistanciaCentroSalud(){
		if(localizacion_actual != null){
			// TODO: calculaDistanciaCentroSalud no debería recibir ningun argumento
			float distancia = calculaDistanciaCentroSalud(localizacion_actual);
			texto.setText(getString(R.string.distanciaACentroSalud) + " " + distancia + " " + UNIDAD_DISTANCIA);
		}else{
			texto.setText(getResources().getString(R.string.distanciaACentroSalud) + " " + getResources().getString(R.string.ubicando));
		}
	}
	
	/**
	 * Calcula la distancia al centro de salud mas cercano
	 */
	private float calculaDistanciaCentroSalud(Location locActual){
		// TODO: esta centrosSalud ordenado?
		minDistancia = Math.round(locActual.distanceTo(centrosSalud[0]));

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
	
	@Override
	protected void onPause() {
		Log.d(Utils.ASIMOV, "OnPause()");
		super.onPause();
		
		// TODO: No habria que diferenciar entre si el dispositivo tiene los serviciosActivados
		// 		 en MapActivity, lo tiene que hacer LocalizadorUsuario
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
		
		// TODO: lo mismo que en onPause
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
		
		actualizaMarcadorUsuario();
		muestraDistanciaCentroSalud();
	}
	
	@Override
	protected void onStart() {
		Log.d(Utils.ASIMOV, "OnStart()");
		super.onStart();

		// TODO: lo mismo que en onPause
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
	}
}
	
