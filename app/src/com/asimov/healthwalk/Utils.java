package com.asimov.healthwalk;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Parametros del sistema, los que no son constantes son cambiados 
 * por la actividad de preferencias cuando el usuario quiera
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class Utils {
	// Constantes, no se modifican en las preferencias
	protected final static String ASIMOV = "ASIMOV";
	protected final static float ZOOM_LEVEL = 15.0f;
    protected final static String BASE_DATOS = "localizaciones";

	protected static float COLOR_MARCADOR_UBICACION_ACTUAL = BitmapDescriptorFactory.HUE_GREEN;
	protected static float COLOR_MARCADOR_CENTRO_SALUD = BitmapDescriptorFactory.HUE_RED;
    protected static double RADIO_BUSQUEDA = 3000;

    // TODO: es necesario reiniciar para que se apliquen los cambios?
    protected static long DISTANCIA_MINIMA = 10; // 10 metros
    protected static long TIEMPO_MINIMO = 1000 * 1; // 1 segundos
}