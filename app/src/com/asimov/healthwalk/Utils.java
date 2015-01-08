package com.asimov.healthwalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Parametros del sistema, se utilizan las preferencias para devolverlos
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class Utils {
    public static Context mContext;

	// Constantes, no se modifican en las preferencias
    public final static String ESTADO_GUARDADO_FRAGMENTO = "fragmento";
    public final static String ESTADO_GUARDADO_LOCALIZACION = "localizacion";
    public final static String ESTADO_GUARDADO_TEXTO = "texto";
	public final static String ASIMOV = "ASIMOV";
	public final static float ZOOM_LEVEL = 17.0f;
	public final static float ZOOM_CYL = 7.25f;
    public final static String BASE_DATOS = "localizaciones";
    public final static int CODIGO_ERROR_SIN_SERVICIOS_GOOGLE = 9000;
    public final static int MARCO_TIEMPO = 1000 * 60;
    public final static LatLng LOCALIZACION_CYL = new LatLng(41.666667, -4.66);
    
    // Para la seccion de Actualizaciones de preferencias
    private final static String DISTANCIA_MINIMA = "10"; // 10 metros
    private final static String TIEMPO_MINIMO = "1"; // 1 segundos
    private final static String PRIORIDAD = "PRIORITY_BALANCED_POWER_ACCURACY";
    
    // Valores por defecto para la seccion de Mapa de preferencias
	private final static String COLOR_MARCADOR_UBICACION_ACTUAL = "HUE_GREEN";
	private final static String COLOR_MARCADOR_CENTRO_MAS_CERCANO = "HUE_AZURE";
	private final static String COLOR_MARCADOR_CENTRO_SALUD = "HUE_RED";
    private final static String RADIO_BUSQUEDA = "3";
    private final static String TIPO_MAPA = "MAP_TYPE_NORMAL";
    private final static String MODO_DESPLAZAMIENTO = "ANDANDO";
    
    /**
     * Asigna un nuevo contexto al atributo de la clase
     * @param context Contexto 
     */
    public static void setContext(Context context){
    	mContext = context;
    }
    
    /**
     * @return El tiempo minimo entre actualizaciones establecido en las preferencias
     */
    public static long getTiempoMinimo(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Long.parseLong(sharedPref.getString("tiempo_minimo", TIEMPO_MINIMO)) * 1000;
    }

    /**
     * @return La distancia minima entre actualizaciones establecida en las preferencias
     */
    public static long getDistanciaMinima(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Long.parseLong(sharedPref.getString("distancia_minima", DISTANCIA_MINIMA));
    }
    
    /**
     * @return La prioridad de busqueda de localizaciones establecida en las preferencias
     */
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
    
    /**
     * @param color Color del marcador del centro de salud establecido en las preferencias
     * @return Color del marcador del centro de salud establecido en las preferencias
     */
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

    /**
     * @return El color del marcador de la localizacion del usuario establecido en las preferencias
     */
    public static float getColorUsuario(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_usuario", COLOR_MARCADOR_UBICACION_ACTUAL));
    }

    /**
     * @return El color de los marcadores de los centros de salud establecido en las preferencias
     */
    public static float getColorCentros(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_centros_salud", COLOR_MARCADOR_CENTRO_SALUD));
    }
    
    /**
     * @return El color del marcador del centro de salud mas cercano establecido en las preferencias
     */
    public static float getColorCentroMasCercano(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return getColor(sharedPref.getString("color_centro_mas_cercano", COLOR_MARCADOR_CENTRO_MAS_CERCANO));
    }
    
    /**
     * @return El radio de busqueda de centros de salud establecido en las preferencias
     */
    public static double getRadio(){
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return Double.parseDouble(sharedPref.getString("radio", RADIO_BUSQUEDA)) * 1000;
    }
    
    /**
     * @return El tipo de mapa establecido en las preferencias
     */
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
    
    /**
     * @return El modo de desplazamiento de la ruta al centro de salud establecido en las preferencias
     */
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