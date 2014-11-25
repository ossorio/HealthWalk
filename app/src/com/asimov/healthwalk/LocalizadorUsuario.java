package com.asimov.test_healthwalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

// TODO: por qué es un servicio?
public class Localizador extends Service 
						 implements com.google.android.gms.location.LocationListener, 
						 			ConnectionCallbacks, 
						 			OnConnectionFailedListener
{
    private final Context mContext;

    // Atributos para determinar el proveedor de localizacion
    private boolean loc_activada = false;
    private boolean gps_activado = false;
    private boolean red_activada = false;

    // Atributos para almacenar la localización
    private Location location;
    private double latitud; 
    private double longitud;

    // Parametros para controlar la precision de la localizacion
    private static final long DISTANCIA_MINIMA = 15; // 10 metros
    private static final long TIEMPO_MINIMO = 1000 * 10 * 1; // 10 segundos

    // Gestor de ubicaciones
    protected LocationManager loc_manager;
    protected LocationClient loc_client;
    private LocationRequest loc_request;
    protected GoogleApiClient google_API_client;
    protected static Location VALLADOLID;

    public Localizador(Context context) {
        this.mContext = context;
    	loc_manager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    	VALLADOLID = new Location("");
    	VALLADOLID.setLatitude(41.652251);
    	VALLADOLID.setLongitude(-4.7245321);
    	location = new Location("");
//    	locationClient = new LocationClient(context, this, this);
		google_API_client = new GoogleApiClient.Builder(context)
                                        .addApi(LocationServices.API)
                                        .addConnectionCallbacks(this)
                                        .addOnConnectionFailedListener(this)
                                        .build();
		// TODO: los argumentos deberian poder modificarse en preferences
		loc_request = LocationRequest.create();
		loc_request.setFastestInterval(1000);
		loc_request.setInterval(5000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		loc_request.setNumUpdates(5);
		loc_request.setSmallestDisplacement(10);
		loc_request.setExpirationDuration(10000);
    }
    
    protected Location getLocation() {
    	if(google_API_client.isConnected()){
    		LocationServices.FusedLocationApi.requestLocationUpdates(
    				google_API_client, loc_request, this);
    		location = LocationServices.FusedLocationApi.getLastLocation(google_API_client);
    		latitud = location.getLatitude();
    		longitud = location.getLongitude();
    	}
    	
        return location;
    }
    
    /*
     * Funcion usada cuando el dispositivo cambia de ubicacion
     */
    @Override
    public void onLocationChanged(Location location) {
//    	locationClient.removeLocationUpdates(this);
    	this.location = location;
 		// TODO: PASAR ESTOS METODOS A "OBJETO"
    	HealthWalk.eliminaMarcador(HealthWalk.marcador);
    	HealthWalk.muestraUbicacion(this.location, "Aquí");
    	HealthWalk.muestraDistanciaCentroSalud(this.location);
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    /*
     * Funcion que devuelve la latitud de la ubicacion 
     */
    public double getLatitude(){
        if(location != null){
            latitud = location.getLatitude();
        }
        return latitud;
    }
     
    /*
     * Funcion que devuelve la longitud de la ubicacion 
     */
    public double getLongitude(){
        if(location != null){
            longitud = location.getLongitude();
        }
        return longitud;
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("onConnected()","onConnected()");
		
//		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

	    gps_activado = false;
	    try {
	      gps_activado = loc_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    } catch (Exception ex) {
	    }

	    red_activada = false;
	    try {
	      red_activada = loc_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    } catch (Exception ex) {
	    }

	    loc_activada = gps_activado || red_activada;

	    if (!loc_activada) {
	     Toast.makeText(mContext, "Debe activar los servicios de ubicacion para que la aplicacion funcione", Toast.LENGTH_LONG).show();
	    } else {
			LocationServices.FusedLocationApi.requestLocationUpdates(
	        google_API_client, loc_request, this);
	    }
	}

	protected void pararActualizaciones(){
		LocationServices.FusedLocationApi.removeLocationUpdates(google_API_client, this);	
	}
	
	@Override
	public void onConnectionSuspended(int arg0) {
		
	}

//	public void onDisconnected() {
//		locationClient.removeLocationUpdates(this);
//		
//	}
	
//	private void compruebaProveedorUbicacion(){
//		// Se obtiene el estado del GPS
//        isGPSEnabled = locationManager
//                .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        // Se obtiene el estado de la red
//        isNetworkEnabled = locationManager
//                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        if (!isGPSEnabled && !isNetworkEnabled) {
//            Toast.makeText(this, "Debe activar los servicios de ubicación", Toast.LENGTH_LONG).show();
//            System.exit(-1);
//        } else {
//        	if(isGPSEnabled)
//        		PROVIDER = LocationManager.GPS_PROVIDER;
//        	else if(isNetworkEnabled)
//        		PROVIDER = LocationManager.NETWORK_PROVIDER;
//        }
//	}

		
}
	
