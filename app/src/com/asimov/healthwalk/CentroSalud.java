package com.asimov.healthwalk;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

/**
 * Creada por el Repositorio para guardar la informacion de un centro de salud
 * @author Alejandro Lopez Espinosa
 */
public class CentroSalud {

	private Location localizacion;
	private String nombre;
	private String direccion;
	private String ciudad;
	private String provincia;
	private String telefono;
	private Marker marcador;

	public CentroSalud(String nombre, String direccion, String ciudad,
			String provincia, String telefono, double lat, double lon) {
		super();
		this.nombre = nombre;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.provincia = provincia;
		this.telefono = telefono;
		this.localizacion = new Location("");
		this.localizacion.setLatitude(lat);
		this.localizacion.setLongitude(lon);
	}


	public Location getLocalizacion() {
		return localizacion;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public Marker getMarcador() {
		return marcador;
	}

	public void setMarcador(Marker nuevoMarcador) {
		marcador = nuevoMarcador;
	}

	public Location getLocation() {
		return localizacion;
	}
	
}
