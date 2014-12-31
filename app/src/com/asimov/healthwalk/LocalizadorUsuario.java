package com.asimov.healthwalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocalizadorUsuario extends Service 
								implements com.google.android.gms.location.LocationListener, 
										   ConnectionCallbacks, 
										   OnConnectionFailedListener, 
										   com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks, LocationListener
{
    private final Context mContext;

    // Atributos para determinar el proveedor de localizacion
    private boolean loc_activada = false;
    private boolean gps_activado = false;
    private boolean red_activada = false;
    private MapActivity map;
    
	// Indica si los servicios de Google Play estan activados
    protected boolean serviciosActivados;
    
    // Ubicacion inicial por defecto (Valladolid capital).
    protected Location VALLADOLID;
    
    private String PROVIDER;
   
    // Gestor de ubicaciones
    protected LocationManager loc_manager;

    private LocationRequest loc_request;
    
    // Cliente de la API de Google
    protected GoogleApiClient google_API_client;

    public LocalizadorUsuario(Context context) {
    	map = (MapActivity) context;
    	serviciosActivados = servicesConnected();
    	mContext = context;
    	loc_manager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    	VALLADOLID = new Location("");
    	VALLADOLID.setLatitude(41.652251);
    	VALLADOLID.setLongitude(-4.7245321);

		google_API_client = new GoogleApiClient.Builder(mContext)
                                        .addApi(LocationServices.API)
                                        .addConnectionCallbacks(this)
                                        .addOnConnectionFailedListener(this)
                                        .build();
		
		// TODO: los argumentos deberian poder modificarse en preferences
		loc_request = LocationRequest.create();
		loc_request.setFastestInterval(400);
		loc_request.setInterval(5000).setPriority(Utils.getPrioridad());
		loc_request.setSmallestDisplacement(5);
    }

    
    /*
     * Funcion usada cuando el dispositivo cambia de ubicacion
     */
    @Override
    public void onLocationChanged(Location location) {
    	// TODO: en vez de llamar directamente a un solo observador hay que llamar a todos
    	// los observadores registrados y hay que comprobar que la nueva localizacion
    	// es "buena"
    	ObservadorLocalizaciones observador = (ObservadorLocalizaciones) mContext;
    	observador.cambioLocalizacion(location);
    }
    
    /**
     * Llamada por un observador para ser notificado cuando la localizacion cambia
     * @param observador Observador que se quiere registrar frente a los cambios de estado
     */
    public void registrarObservador(ObservadorLocalizaciones observador){
    	// TODO: rellenar
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
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
	    	ex.printStackTrace();
	    }

	    red_activada = false;
	    try {
	      red_activada = loc_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }

	    loc_activada = gps_activado || red_activada;

	    if (!loc_activada) {
	     Toast.makeText(mContext, "Debe activar los servicios de ubicacion para que la aplicacion funcione", Toast.LENGTH_LONG).show();
	    } else {
			LocationServices.FusedLocationApi.requestLocationUpdates(
	        google_API_client, loc_request, this);
	    }
	}
	
	protected void solicitarActualizaciones(){
		if(!google_API_client.isConnected())
			google_API_client.connect();
	}
	
	protected void pararActualizaciones(){
		if(google_API_client.isConnected()){
			LocationServices.FusedLocationApi.removeLocationUpdates(google_API_client, this);	
			google_API_client.disconnect();
		}
		
	}
	
	protected void pararActualizacionesSinServicios(){
		if(map.solicitandoActualizaciones){
			loc_manager.removeUpdates(this);
			map.solicitandoActualizaciones = false;
		}
	}
	
	protected void solicitarActualizacionesSinServicios(){
		if(loc_manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			PROVIDER = LocationManager.GPS_PROVIDER;
		if(loc_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			PROVIDER = LocationManager.NETWORK_PROVIDER;
		if(!map.solicitandoActualizaciones){
			loc_manager.requestLocationUpdates(PROVIDER, Utils.getTiempoMinimo(), Utils.getDistanciaMinima(), this);
			map.solicitandoActualizaciones = true;
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		PROVIDER = provider;
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean servicesConnected() {
		// Comprueba que los servicios de Google Play estén disponibles
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(map);
		// Si los servicios de Google Play estan disponibles
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
	
}
	
