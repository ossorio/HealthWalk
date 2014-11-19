package com.asimov.test_healthwalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class Localizador extends Service implements LocationListener {
	
    private final Context mContext;
 
    // Bandera para indicar si el GPS esta activo
    boolean isGPSEnabled = false;
 
    // Bandera para indicar si la ubicacion a traves de red esta activa
    boolean isNetworkEnabled = false;
 
    // Bandera para indicar si es posible obtener la ubicacion actual
    boolean canGetLocation = false;
 
    Location location; 
    double latitud; 
    double longitud; 
 
    // Constante que indica la distancia mínima para actualizar la ubicacion
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 metros
 
    // Constante que indica el tiempo mínimo entre actualizaciones
    private static final long MIN_TIME_BW_UPDATES = 1000 * 45 * 1; // 45 segundos
 
    // Gestor de ubicaciones
    protected LocationManager locationManager;
 
    /*
     * Constructor
     */
    public Localizador(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
        	// Inicializacion del gestor de localizacion
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // Se obtiene el estado del GPS
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // Se obtiene el estado de la red
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // No esta activado ningun servicio de ubicacion - No se hace nada
            } else {
                this.canGetLocation = true;
                // La primera opcion es obtener la ubicacion mediante el GPS
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Activado", "Usando GPS para obtener ubicacion");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();
                            }
                        }
                    }
                 // Si el GPS no esta activado, la segunda opcion es obtener la ubicacion
                 // usando la red
                }else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Red activada", "Usando red para obtener ubicacion");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    
    /*
     * Funcion usada cuando el dispositivo cambia de ubicacion
     */
    @Override
    public void onLocationChanged(Location location) {
    	location = this.getLocation();
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
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
         
        // return latitude
        return latitud;
    }
     
    /*
     * Funcion que devuelve la longitud de la ubicacion 
     */
    public double getLongitude(){
        if(location != null){
            longitud = location.getLongitude();
        }
         
        // return longitude
        return longitud;
    }
    
    /*
     * Funcion que verifica si es posible o no calcular la ubicacion actual
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /*
     * Funcion para desactivar el GPS
     */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(Localizador.this);
        }      
    }
		
}
	
