package com.asimov.healthwalk;

import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Actividad que muestra el mapa con los centros de salud mas cercanos, es la
 * responsable de dibujar la IU principal y de implementar su comportamiento
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class MapActivity extends Activity implements ObservadorLocalizaciones{
	 // Partes del layout que hay que actualizar con cada nueva localizacion
	protected GoogleMap mMap;
	protected  TextView texto;

	// Centros de salud mostrados actualmente en el mapa
	protected ArrayList<CentroSalud> centrosSalud;
	
	// Almacena la ubicacion actual y su marcador
	protected Location localizacion_actual;
	protected Marker marcadorUbicacionActual;

	// Localizador que llama a esta clase cada vez que hay una nueva actualizacion
	// disponible
	private LocalizadorUsuario gps;
	private RepositorioLocalizaciones repositorio;

	// Bandera que indica si se estan solicitando actualizaciones de ubicacion
	// sin los servicios de Google activados (LocationManager)
	// TODO: Sigue siendo valido?
	protected boolean solicitandoActualizaciones;
	
	/*
	 * Inicializa las principales clases de la aplicacion
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(Utils.ASIMOV, "MapActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		centrosSalud = new ArrayList<CentroSalud>();
		
		// TODO: si no tiene una version del services > 6171000 peta
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		texto = (TextView) findViewById(R.id.textoMapa);
		texto.setGravity(Gravity.CENTER);
		
		gps = new LocalizadorUsuario(this);
		repositorio = new RepositorioLocalizaciones(this, Utils.BASE_DATOS);
		
		// Testing
		Location loc = new Location("");
		loc.setLatitude(41.6344462);
		loc.setLongitude(-4.7478554);
		cambioLocalizacion(loc);
	}
	
	@Override
	protected void onStart() {
		Log.d(Utils.ASIMOV, "MapActivity onStart");
		super.onStart();

		// TODO: lo mismo que en onPause
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
	}
	
	@Override
	protected void onResume() {
		Log.d(Utils.ASIMOV, "MapActivity onResume");
		super.onResume();
		
		// TODO: lo mismo que en onPause
		if(gps.serviciosActivados){
			gps.solicitarActualizaciones();
		}else{
			gps.solicitarActualizacionesSinServicios();
		}
		
		actualizaMarcadorUsuario(localizacion_actual);
		muestraDistanciaCentroSalud(localizacion_actual);
	}
	
	@Override
	protected void onPause() {
		Log.d(Utils.ASIMOV, "MapActivity onPause");
		super.onPause();
		
		// TODO: gestion del repositorio y abstraerlo para varias actividades
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
		Log.d(Utils.ASIMOV, "MapActivity onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(Utils.ASIMOV, "MapActivity onDestroy");
		super.onDestroy();
	}
	
	/**
	 * Lo llama el LocalizadorUsuario cuando existe una nueva localizacion disponible,
	 * dibuja la nueva localizacion y decide si ampliar o reducir los centros mostrados
	 * @param nueva_localizacion Localizacion actualizada del usuario
	 */
	@Override
	public void cambioLocalizacion(Location nueva_localizacion){
		// TODO: falta exception handling
		Log.d(Utils.ASIMOV, "Actualizacion para localizacion recibida.");
    	actualizaMarcadorUsuario(nueva_localizacion);

    	// Solo actualizamos los centros de salud cuando el usuario se ha movido del mas cercano
    	// o no hay centros de salud guardados
    	if(centrosSalud.size() == 0 ||
 		   nueva_localizacion.distanceTo(centrosSalud.get(0).getLocalizacion()) > Utils.RADIO_BUSQUEDA / 3){
    		actualizaCentrosSalud(nueva_localizacion);
    	}

    	muestraDistanciaCentroSalud(nueva_localizacion);
		localizacion_actual = nueva_localizacion;
	}

	/**
	 * Redibuja los centros de salud mas cercanos a la nueva_localizacion
	 * @param nueva_localizacion Localizacion desde la que hay que dibujar los centros de salud
	 */
	private void actualizaCentrosSalud(Location nueva_localizacion) {
		Log.d(Utils.ASIMOV, "Actualizando centros de salud");
		ArrayList<CentroSalud> nuevosCentrosSalud = repositorio.getCentrosSalud(nueva_localizacion, Utils.RADIO_BUSQUEDA);
		if(nuevosCentrosSalud != null){
			for(CentroSalud cs : centrosSalud){
				eliminaMarcador(cs.getMarcador());
			}
			centrosSalud.clear();
			centrosSalud = nuevosCentrosSalud;
			for(CentroSalud cs : centrosSalud){
				Marker marcador = agregaMarcador(cs.getLocalizacion(), cs.getNombre(), Utils.COLOR_MARCADOR_CENTRO_SALUD);
				cs.setMarcador(marcador);
			}
			Log.d(Utils.ASIMOV, "Centros de salud actualizados");
		}else{
			// TODO: llevarlo a un recurso
			Toast toast = Toast.makeText(this, "No se pudieron actualizar los centros de salud", Toast.LENGTH_LONG);
			Log.e(Utils.ASIMOV, "No se pudieron actualizar los centros de salud");
		}
	}
	
	/**
	 * Primero crea un marcador sobre la nueva ubicacion y despues realiza un
	 * "zoom" sobre la posicion
	 * @param nueva_localizacion Nueva localizacion del marcador de usuario
	 */
	protected void actualizaMarcadorUsuario(Location nueva_localizacion){
		if(marcadorUbicacionActual != null){
			eliminaMarcador(marcadorUbicacionActual);
			marcadorUbicacionActual = null;
		}

		if(nueva_localizacion != null){
			// TODO: la etiqueta deberia ser el nombre del centro de salud
			String etiqueta = getString(R.string.ubicacionActual);
			LatLng posicion = new LatLng(nueva_localizacion.getLatitude(), nueva_localizacion.getLongitude());
			marcadorUbicacionActual = agregaMarcador(nueva_localizacion, etiqueta, Utils.COLOR_MARCADOR_UBICACION_ACTUAL);

			// Situa la panoramica del mapa sobre una ubicacion
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, Utils.ZOOM_LEVEL));
			Log.d(Utils.ASIMOV, "Actualizacion para localizacion dibujado.");
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
	 * @param nueva_localizacion Nueva localizacion del marcador de usuario
	 */
	protected void muestraDistanciaCentroSalud(Location nueva_localizacion){
		if(nueva_localizacion != null){
			String unidad_distancia;
			CentroSalud centroSalud = centrosSalud.get(0);
			double distancia = nueva_localizacion.distanceTo(centroSalud.getLocation());
			// TODO: Podemos traducirlo a otros sistemas numericos facilmente?
			if(distancia >= 1000){
				distancia -= distancia % 10;
				distancia /= 1000;
				unidad_distancia = "Km";
			}else{
				unidad_distancia = "m";
			}
			texto.setText(getString(R.string.distanciaACentroSalud) + " " + distancia + " " + unidad_distancia);
			Log.d(Utils.ASIMOV, "Distancia a centro de salud actualizada.");
		}else{
			texto.setText(getResources().getString(R.string.distanciaACentroSalud) + " " + getResources().getString(R.string.ubicando));
		}
	}
}
	
