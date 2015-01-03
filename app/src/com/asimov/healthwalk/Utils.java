package com.asimov.healthwalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Parametros del sistema, se utilizan las preferencias para devolverlos
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class Utils {
    public static Context mContext;

	// Constantes, no se modifican en las preferencias
	public final static String ASIMOV = "ASIMOV";
	public final static float ZOOM_LEVEL = 17.0f;
	public final static float ZOOM_ES = 5.5f;
    public final static String BASE_DATOS = "localizaciones";
    public final static int CODIGO_ERROR_SIN_SERVICIOS_GOOGLE = 9000;
    public static final int MARCO_TIEMPO = 1000 * 60;
    
    // Para la seccion de Actualizaciones de preferencias
    // TODO: es necesario reiniciar para que se apliquen los cambios?
    private final static String DISTANCIA_MINIMA = "10"; // 10 metros
    private final static String TIEMPO_MINIMO = "1"; // 1 segundos
    private final static String PRIORIDAD = "PRIORITY_HIGH_ACCURACY";
    
    // Valores por defecto para la seccion de Mapa de preferencias
	private final static String COLOR_MARCADOR_UBICACION_ACTUAL = "HUE_GREEN";
	private final static String COLOR_MARCADOR_CENTRO_MAS_CERCANO = "HUE_AZURE";
	private final static String COLOR_MARCADOR_CENTRO_SALUD = "HUE_RED";
    private final static String RADIO_BUSQUEDA = "3000";
    private final static String TIPO_MAPA = "MAP_TYPE_NORMAL";
    private final static String MODO_DESPLAZAMIENTO = "ANDANDO";
    
    public static void setContext(Context context){
    	mContext = context;
    }
    
    public static long getTiempoMinimo(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Long.parseLong(sharedPref.getString("tiempo_minimo", TIEMPO_MINIMO)) * 1000;
    }

    public static long getDistanciaMinima(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Long.parseLong(sharedPref.getString("distancia_minima", DISTANCIA_MINIMA));
    }
    
    public static int getPrioridad(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	switch(sharedPref.getString("prioridad", PRIORIDAD)) {
    		case "PRIORITY_BALANCED_POWER_ACCURACY":
    			return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    		case "PRIORITY_HIGH_ACCURACY":
    			return LocationRequest.PRIORITY_HIGH_ACCURACY;
    		case "PRIORITY_LOW_POWER":
    			return LocationRequest.PRIORITY_LOW_POWER;
    		case "PRIORITY_NO_POWER":
    			return LocationRequest.PRIORITY_NO_POWER;
    	}
    	return LocationRequest.PRIORITY_HIGH_ACCURACY;
    }
    
    private static float getColor(String color){
    	switch(color){
    	 	case "HUE_AZURE":
    	 		return BitmapDescriptorFactory.HUE_AZURE;
    	 	case "HUE_GREEN":
    	 		return BitmapDescriptorFactory.HUE_GREEN;
    	 	case "HUE_MAGENTA":
    	 		return BitmapDescriptorFactory.HUE_MAGENTA;
    	 	case "HUE_ORANGE":
    	 		return BitmapDescriptorFactory.HUE_ORANGE;
    	 	case "HUE_RED":
    	 		return BitmapDescriptorFactory.HUE_RED;
    	 	case "HUE_VIOLET":
    	 		return BitmapDescriptorFactory.HUE_VIOLET;
    	 	case "HUE_YELLOW":
    	 		return BitmapDescriptorFactory.HUE_YELLOW;
    	 		
    	}
    	return BitmapDescriptorFactory.HUE_ROSE;
    }

    public static float getColorUsuario(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_usuario", COLOR_MARCADOR_UBICACION_ACTUAL));
    }

    public static float getColorCentros(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_centros_salud", COLOR_MARCADOR_CENTRO_SALUD));
    }
    
    public static float getColorCentroMasCercano(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_centro_mas_cercano", COLOR_MARCADOR_CENTRO_MAS_CERCANO));
    }
    
    public static double getRadio(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Double.parseDouble(sharedPref.getString("radio", RADIO_BUSQUEDA));
    }
    
    public static int getTipoMapa(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	String tipo = sharedPref.getString("tipo_mapa", TIPO_MAPA);
    	switch(tipo){
    		case "MAP_TYPE_HYBRID":
    			return GoogleMap.MAP_TYPE_HYBRID;
    		case "MAP_TYPE_NONE":
    			return GoogleMap.MAP_TYPE_NONE;
    		case "MAP_TYPE_NORMAL":
    			return GoogleMap.MAP_TYPE_NORMAL;
    		case "MAP_TYPE_SATELLITE":
    			return GoogleMap.MAP_TYPE_SATELLITE;
    		case "MAP_TYPE_TERRAIN":
    			return GoogleMap.MAP_TYPE_TERRAIN;
    	}
    	return GoogleMap.MAP_TYPE_NORMAL;
    }
    
    public static char getModoDesplazamiento(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	String modo = sharedPref.getString("modo_desplazamiento", MODO_DESPLAZAMIENTO);
    	switch(modo) {
			case "ANDANDO":
				return 'w';
			case "COCHE":
				return 'd';
			case "TRANSPORTE_PUBLICO":
				return 'r';
		}
		return 'w';
    }
}