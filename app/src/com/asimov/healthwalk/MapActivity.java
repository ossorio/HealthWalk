package com.asimov.healthwalk;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
public class MapActivity extends FragmentActivity implements ObservadorLocalizaciones, OnInfoWindowClickListener {
	
	// Bandera para comprobar si ha habido cambios en las preferencias
	private boolean cambiosEnPreferencias;
	
	//Bandera para comprobar si el estado de la aplicacion ha sido restaurado 
	private boolean appRestaurada;
	
	// Fragmento que se utiliza para cargar el mapa cuando se restaura el estado
	private Fragment fragmento;
	
	// Partes del layout que hay que actualizar con cada nueva localizacion
	protected GoogleMap mMap;
	protected TextView texto;
	protected ImageButton botonUbicacion;
	protected ImageButton botonRuta;
	
	// Centros de salud mostrados actualmente en el mapa
	protected ArrayList<CentroSalud> centrosSalud;
	
	// Almacena la ubicacion actual y su marcador
	protected Location localizacion_actual;
	protected Marker marcadorUbicacionActual;
	private ArrayList<Marker> marcadoresCentrosSalud;

	// Localizador que llama a esta clase cada vez que hay una nueva actualizacion
	// disponible
	private LocalizadorUsuario localizador;
	private RepositorioLocalizaciones repositorio;

	/**
	 * Inicializa las principales clases de la aplicacion
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(Utils.ASIMOV, "MapActivity onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);
		Utils.setContext(this);

		repositorio = new RepositorioLocalizaciones(this, Utils.BASE_DATOS);
		localizador = new LocalizadorUsuario(this);
		localizador.registrarObservador(this);

		centrosSalud = new ArrayList<CentroSalud>();
		marcadoresCentrosSalud = new ArrayList<Marker>();
	
		texto = (TextView) findViewById(R.id.textoMapa);

		botonUbicacion = (ImageButton)findViewById(R.id.botonUbicacion);
		botonUbicacion.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
					muestraLocalizacionEnMapa(localizacion_actual);
			}
		});

		botonRuta = (ImageButton)findViewById(R.id.botonRuta);
		botonRuta.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
					if(centrosSalud != null && centrosSalud.size() > 0)
						muestraRutaACentroSalud(centrosSalud.get(0));
			}
		});

		cambiosEnPreferencias = false;
		// El estado de la aplicacion ha sido restaurado
		if(savedInstanceState != null){
			repositorio.start();
			localizacion_actual = savedInstanceState.getParcelable(Utils.ESTADO_GUARDADO_LOCALIZACION);
			fragmento = getFragmentManager().getFragment(savedInstanceState, Utils.ESTADO_GUARDADO_FRAGMENTO);
			mMap = ((MapFragment) fragmento).getMap();
			texto.setText(savedInstanceState.getString(Utils.ESTADO_GUARDADO_TEXTO));
			actualizaCentrosSalud(localizacion_actual);
			appRestaurada = true;
		}else{
			// El estado de la aplicacion no ha sido restaurado
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap == null){
				return;
			}
		}	
		texto.setGravity(Gravity.CENTER);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.setOnInfoWindowClickListener(this);
	}
	
	/**
	 * Arranca los recursos compartidos como el repositorio y el localizador de usuario
	 */
	@Override
	protected void onResume() {
		Log.d(Utils.ASIMOV, "MapActivity onResume");
		super.onResume();
		repositorio.start();
		if (mMap == null) {
			return;
		}
			
		mMap.setMapType(Utils.getTipoMapa());
		if(cambiosEnPreferencias)
			cargaNuevasPreferencias();
		
		localizador.solicitarActualizaciones();
		
		actualizaMarcadorUsuario(localizacion_actual);
		muestraInfoCentroSalud(localizacion_actual);
		cambiosEnPreferencias = false;
	}
	
	/**
	 * Para los recursos compartidos como el repositorio o el localizador de usuario
	 */
	@Override
	protected void onPause() {
		Log.d(Utils.ASIMOV, "MapActivity onPause");
		super.onPause();
		repositorio.stop();

		localizador.pararActualizaciones();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		localizador.eliminarObservador(this);
	}

	/**
	 * Guarda el estado de la actividad
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(Utils.ESTADO_GUARDADO_LOCALIZACION, localizacion_actual);
		outState.putString(Utils.ESTADO_GUARDADO_TEXTO, (String) texto.getText());
		getFragmentManager().putFragment(outState, Utils.ESTADO_GUARDADO_FRAGMENTO, getFragmentManager().findFragmentById(R.id.map));

	}
	
	
	/**
	 * Llamado cuando se pincha en un marcador del mapa
	 * @param marcador Marcador sobre el que se ha pinchado
	 */
	@Override
	public void onInfoWindowClick(Marker marcador) {
		String titulo = marcador.getTitle();
		if(!titulo.equals(getString(R.string.ubicacionActual))){
			for(CentroSalud cs : centrosSalud){
				if(titulo.equals(cs.getNombre())){
					Intent intent = new Intent(this, CentroSaludActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("centro_salud", cs);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}
	}
	
	/**
	 * Crea el menu de la actividad
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return true;
	}
	
	/**
	 * Si se pulsa en el icono de preferencias, se cargan en una nueva actividad
	 * Si se pulsa en el icono de ayuda, se carga en una nueva actividad
	 * Si se pulsa en el icono de acerca de, se carga en una nueva actividad
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
            case R.id.preferencias_menu:
            	Intent intent = new Intent(this, PreferenciasActivity.class);
            	startActivity(intent);
            	cambiosEnPreferencias = true;
            	return true;
            case R.id.ayuda_menu:
            	Intent intent32 = new Intent(this, HelpActivity.class);
            	startActivity(intent32);
            	return true;
            case R.id.acerca_menu:
            	Intent intent33 = new Intent(this, AboutActivity.class);
            	startActivity(intent33);
            	return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	/**
	 * Lo llama el LocalizadorUsuario cuando existe una nueva localizacion disponible,
	 * dibuja la nueva localizacion y decide si ampliar o reducir los centros mostrados
	 * @param nueva_localizacion Localizacion actualizada del usuario
	 */
	@Override
	public void cambioLocalizacion(Location nueva_localizacion){
		Log.d(Utils.ASIMOV, "Actualizacion para localizacion recibida.");

    	// Solo actualizamos los centros de salud cuando el usuario se ha movido del mas cercano
    	// o no hay centros de salud guardados
    	if(centrosSalud.size() == 0 ||
 		   nueva_localizacion.distanceTo(centrosSalud.get(0).getLocalizacion()) > Utils.getRadio() / 3){
    		actualizaCentrosSalud(nueva_localizacion);
    	}

    	actualizaMarcadorUsuario(nueva_localizacion);
    	muestraInfoCentroSalud(nueva_localizacion);
		localizacion_actual = nueva_localizacion;
	}

	/**
	 * Primero crea un marcador sobre la nueva ubicacion y despues realiza un
	 * "zoom" sobre la posicion
	 * @param nueva_localizacion Nueva localizacion del marcador de usuario
	 */
	protected void actualizaMarcadorUsuario(Location nueva_localizacion){
		if(nueva_localizacion != null){
			String etiqueta = getString(R.string.ubicacionActual);
			
			if(marcadorUbicacionActual == null && !appRestaurada && !cambiosEnPreferencias)
				muestraLocalizacionEnMapa(nueva_localizacion);
			else
				eliminaMarcador(marcadorUbicacionActual);
			
			marcadorUbicacionActual = agregaMarcador(nueva_localizacion, etiqueta, Utils.getColorUsuario());

			Log.d(Utils.ASIMOV, "Actualizacion para localizacion dibujado.");
		}else{
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Utils.LOCALIZACION_CYL, Utils.ZOOM_CYL));
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
		if(!etiqueta.equals(getString(R.string.ubicacionActual))){
			opcionesMarcador.snippet(getString(R.string.clickMarcador));
		}
		
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
	 * Redibuja los centros de salud mas cercanos a la nueva_localizacion
	 * @param nueva_localizacion Localizacion desde la que hay que dibujar los centros de salud
	 */
	private void actualizaCentrosSalud(Location nueva_localizacion) {
		Log.d(Utils.ASIMOV, "Actualizando centros de salud");
		if(nueva_localizacion != null){
			ArrayList<CentroSalud> nuevosCentrosSalud = repositorio.getCentrosSalud(nueva_localizacion, Utils.getRadio());
			if(nuevosCentrosSalud != null && nuevosCentrosSalud.size() > 0){
				// Limpiar todos los marcadores del mapa
				mMap.clear();
				marcadorUbicacionActual = null;
				centrosSalud.clear();
				marcadoresCentrosSalud.clear();

				centrosSalud = nuevosCentrosSalud;
				for(CentroSalud cs : centrosSalud){
					marcadoresCentrosSalud.add(agregaMarcador(cs.getLocalizacion(), cs.getNombre(), Utils.getColorCentros()));
				}
				Log.d(Utils.ASIMOV, "Centros de salud actualizados");
			}else{
				Toast toast = Toast.makeText(this, getString(R.string.errorCentrosSalud), Toast.LENGTH_LONG);
				toast.show();
				Log.e(Utils.ASIMOV, "No se pudieron actualizar los centros de salud");
			}
		}
	}
	
	/**
	 * Muestra la distancia al centro de salud mas cercano
	 * @param nueva_localizacion Nueva localizacion del marcador de usuario
	 */
	protected void muestraInfoCentroSalud(Location nueva_localizacion){
		if(nueva_localizacion != null && centrosSalud.size() > 0){
			CentroSalud centroSalud = centrosSalud.get(0);
			eliminaMarcador(marcadoresCentrosSalud.get(0));
			marcadoresCentrosSalud.add(0, agregaMarcador(centroSalud.getLocalizacion(), centroSalud.getNombre(), Utils.getColorCentroMasCercano()));
			texto.setText(centroSalud.getNombre() + "\n" + centroSalud.getDireccion());
		}
	}
	
	/**
	 * Muestra la ruta en Google Maps desde la localizacion actual al centro de salud 
	 * @param centroSalud Centro de salud sobre el que se calcula la ruta
	 */
	protected void muestraRutaACentroSalud(CentroSalud centroSalud){
		Location locCentroSalud = centroSalud.getLocalizacion();
		String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f&dirflg=%c", locCentroSalud.getLatitude(), locCentroSalud.getLongitude(), Utils.getModoDesplazamiento());
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
	
	/**
	 * Enfoca la localizacion del argumento en el mapa
	 * @param localizacion Localizacion a enfocar en el mapa
	 */
	protected void muestraLocalizacionEnMapa(Location localizacion){
		if(localizacion != null){
			LatLng posicion = new LatLng(localizacion.getLatitude(), localizacion.getLongitude());
			// Situa el centro del mapa sobre la ubicacion actual
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, Utils.ZOOM_LEVEL));
		}
	}
	
	/**
	 * Modifica los atributos de la clase cuando se cambian las preferencias
	 */
	protected void cargaNuevasPreferencias(){
		
		localizador.loc_request.setFastestInterval(Utils.getTiempoMinimo());
		localizador.loc_request.setInterval(Utils.getTiempoMinimo() * 3).setPriority(Utils.getPrioridad());
		localizador.loc_request.setSmallestDisplacement(Utils.getDistanciaMinima());;
		actualizaCentrosSalud(localizacion_actual);
	}
}
	
