package com.asimov.test_healthwalk;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
	 
	public class HealthWalk extends Activity {
	     
	    private Location location; // Almacena la ubicacion actual
	    private Localizador gps; // Almacena un objeto de la clase Localizador
        private GoogleMap mMap; // Mapa que sirve de base para indicar localizaciones
        private Location location2; // Centro de salud nº 3 de prueba 
        private Location location3; // Centro de salud nº 166 de prueba
        
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.fragmento_mapa);
	        
	        // Se crea un localizador de ubicaciones
	        gps = new Localizador(this);
	        
	        // Se obtiene la ubicacion actual
	        location = gps.getLocation();
	        
	        // Creacion de dos ubicaciones de prueba de centros de salud
	        location2 = new Location("");
	        location2.setLatitude(41.6344462);
	        location2.setLongitude(-4.7478554);
	        
	        location3 = new Location("");
	        location3.setLatitude(41.644327);
	        location3.setLongitude(-4.7311999);
	        
	        /* Se inicializa el objeto GoogleMap a partir del elemento "map" 
	         * localizado en el fichero "fragmento_mapa.xml" 
	         */
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        
	        mMap.getUiSettings().setZoomControlsEnabled(true);
	        mMap.getUiSettings().setMyLocationButtonEnabled(true);

	        // Se centra la posicion del mapa en la ubicacion actual
	        muestraUbicacion(location);
	        
	        // Se crean dos marcadores de prueba en dos centros de salud
	        muestraMarcador(location2, "Centro de Salud nº 3" );
	        muestraMarcador(location3, "Centro de Salud nº 166");
	       
	}
	    
	    @Override
	    protected void onStop() {
	    	super.onStop();
	    }
	    
	    @Override
	    protected void onRestart() {
	    	super.onRestart();
	    	Log.d("OnRestart", "OnRestart()");
	    	gps.onLocationChanged(location);
	    	muestraUbicacion(location);
	    }
	    
	    /*
	     * Muestra un marcador sobre la ubicacion, con una etiqueta
	     */
	    private void muestraMarcador(Location location, String label){
	    	mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(location.getLatitude(), location.getLongitude() ))
	        .title(label));
	    }
	    
	    /*
	     * Primero muestra un marcador sobre la ubicacion y despues realiza un
	     * "zoom" sobre la posicion
	     */
	    private void muestraUbicacion(Location location){
	    	muestraMarcador(location, "Aquí");
	    	zoom(location);
	    }
	    
	    /*
	     * Situa la panoramica del mapa sobre una ubicacion
	     */
	    private void zoom(Location location){
	    	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.5f));
	    }
	    
}
