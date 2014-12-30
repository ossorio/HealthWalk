package com.asimov.healthwalk;

import java.io.Serializable;

import android.location.Location;

import com.google.android.gms.maps.model.Marker;

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

	public Location getLocalizacion() {
		Location loc = new Location("");
		loc.setLatitude(latitud);
		loc.setLongitude(longitud);
		return loc;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public String getProvincia() {
		return provincia;
	}

	public String getTelefono() {
		return telefono;
	}
}
