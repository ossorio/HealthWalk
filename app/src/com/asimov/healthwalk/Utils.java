package com.asimov.healthwalk;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Constantes del sistema
 * @author Oscar Gonzalez Ossorio
 * @author Alejandro Lopez Espinosa
 */
public class Utils {
	protected final static float ZOOM_LEVEL = 15.0f;
	protected final static float COLOR_MARCADOR_UBICACION_ACTUAL = BitmapDescriptorFactory.HUE_GREEN;
	protected final static float COLOR_MARCADOR_CENTRO_SALUD = BitmapDescriptorFactory.HUE_RED;
	protected final static String ASIMOV = "ASIMOV";
    protected final static long DISTANCIA_MINIMA = 10; // 10 metros
    protected final static long TIEMPO_MINIMO = 1000 * 1; // 1 segundos
    protected final static String BASE_DATOS = "localizaciones";
    protected final static double RADIO_BUSQUEDA = 3000;
}