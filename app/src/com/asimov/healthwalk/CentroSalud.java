package com.asimov.healthwalk;

import java.io.Serializable;

import android.location.Location;

/**
 * Creada por el Repositorio para guardar la informacion de un centro de salud
 * Implementa la interfaz Serializable ya que es pasada en Intents
 * @author Alejandro Lopez Espinosa
 */
public class CentroSalud implements Serializable{

	private static final long serialVersionUID = 69L;
	private String nombre;
	private String direccion;
	private String ciudad;
	private String provincia;
	private String telefono;
	private double latitud;
	private double longitud;

	/**
	 * Constructor que inicializa los atributos de la clase con los argumentos proporcionados.
	 * @param nombre Nombre del centro de salud
	 * @param direccion Direccion del centro de salud
	 * @param ciudad Ciudad del centro de salud
	 * @param provincia Provincia del centro de salud
	 * @param telefono Telefono del centro de salud
	 * @param lat Latitud en la que se encuentra el centro de salud
	 * @param lon Longitud en la que se encuentra el centro de salud
	 */
	public CentroSalud(String nombre, String direccion, String ciudad,
			String provincia, String telefono, double lat, double lon) {
		super();
		this.nombre = nombre;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.provincia = provincia;
		this.telefono = telefono;
		this.latitud = lat;
		this.longitud = lon;
	}

	/**
	 * @return La localizacion del centro de salud
	 */
	public Location getLocalizacion() {
		Location loc = new Location("");
		loc.setLatitude(latitud);
		loc.setLongitude(longitud);
		return loc;
	}

	/**
	 * @return El nombre del centro de salud
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return La direccion del centro de salud
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * @return La ciudad del centro de salud
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * @return La provincia del centro de salud
	 */
	public String getProvincia() {
		return provincia;
	}

	/**
	 * @return El telefono del centro de salud
	 */
	public String getTelefono() {
		return telefono;
	}
}
