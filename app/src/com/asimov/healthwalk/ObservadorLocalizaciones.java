package com.asimov.healthwalk;

import android.location.Location;

/**
 * Interfaz que todo observador debe implementar para que el objeto observado
 * le notifique de una nueva localizacion
 * @author Alejandro Lopez Espinosa
 */
public interface ObservadorLocalizaciones {
	/**
	 * Es llamada por el observado cuando hay una nueva actualizacion
	 * disponible.
	 * @param nueva_localizacion localizacion actualizada del objeto observado
	 */
	public void cambioLocalizacion(Location nueva_localizacion);
}
