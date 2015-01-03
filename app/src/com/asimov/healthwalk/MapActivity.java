package com.asimov.healthwalk;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
	
	private boolean appRestaurada;
	private Fragment fragmento;
	
	// Partes del layout que hay que actualizar con cada nueva localizacion
	protected GoogleMap mMap;
	protected TextView texto;
	protected ImageButton botonUbicacion;
	protected ImageButton botonRuta;
	protected Context mContext;
	
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

		if(savedInstanceState != null){
			localizacion_actual = savedInstanceState.getParcelable("localizacion");
			fragmento = getFragmentManager().getFragment(savedInstanceState, "fragmento");
			mMap = ((MapFragment) fragmento).getMap();
			texto.setText(savedInstanceState.getString("texto"));
			actualizaCentrosSalud(localizacion_actual);
			appRestaurada = true;
		}else{
			// TODO: si no tiene una version del services > 6171000 peta
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			texto.setGravity(Gravity.CENTER);
		}
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
		mMap.setMapType(Utils.getTipoMapa());
		repositorio.start();
		
		localizador.solicitarActualizaciones();
		
		actualizaMarcadorUsuario(localizacion_actual);
		muestraInfoCentroSalud(localizacion_actual);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable("localizacion", localizacion_actual);
		outState.putString("texto", (String) texto.getText());
		getFragmentManager().putFragment(outState, "fragmento", getFragmentManager().findFragmentById(R.id.map));

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
            case R.id.preferencias_menu:
            	Intent intent = new Intent(this, PreferenciasActivity.class);
            	startActivity(intent);
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
			
			if(marcadorUbicacionActual == null && !appRestaurada)
				muestraLocalizacionEnMapa(nueva_localizacion);
			else
				eliminaMarcador(marcadorUbicacionActual);
			
			marcadorUbicacionActual = agregaMarcador(nueva_localizacion, etiqueta, Utils.getColorUsuario());

			Log.d(Utils.ASIMOV, "Actualizacion para localizacion dibujado.");
		}else{
			LatLng cyl = new LatLng(40.346544, -3.563344);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cyl, Utils.ZOOM_ES));
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
	 * Muestra la distancia al centro de salud más cercano
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
	
	protected void muestraLocalizacionEnMapa(Location localizacion){
		if(localizacion != null){
			LatLng posicion = new LatLng(localizacion.getLatitude(), localizacion.getLongitude());
			// Situa el centro del mapa sobre la ubicacion actual
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, Utils.ZOOM_LEVEL));
		}
	}
}
	
