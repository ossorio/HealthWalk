package com.asimov.healthwalk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

/**
 * Se encarga de proporcionar los datos necesarios para mostrar en el mapa, si la base
 * de datos no existe la copia desde los assets en el directorio
 * /data/data/con.asimov.healthwalk/databases
 * @author Alejandro Lopez Espinosa
 */
public class RepositorioLocalizaciones {
	
	private Context mContext;
	private final int TAM_BUFFER = 1024;
	private final String TABLE = "localizaciones";
	private String nombre_bd = null;
	private SQLiteDatabase bd = null;

	/**
	 * Unico constructor de la clase
	 * @param contexto actividad que lo ha llamado
	 * @param base_datos nombre de la base de datos a crear
	 */
	RepositorioLocalizaciones(Context contexto, String base_datos){
		mContext = contexto;
		setBaseDatos(base_datos);
	}

	/**
	 * Parar la base de datos, normalmente se hace en el metodo onPause de la 
	 * actividad que haya instaciado esta clase 
	 */
	public void stop(){
		if(bd != null){
			bd.close();
			bd = null;
			Log.d(Utils.ASIMOV, "Base de datos cerrada");
		}
	}
	
	/**
	 * Arrancar la base de datos, normalmente se hace en el metodo onResume
	 * de la actividad que haya instanciado esta clase
	 */
	public void start(){
		if(nombre_bd != null && bd == null){
			String path = getPathBD();
			bd = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			Log.d(Utils.ASIMOV, "Base de datos abierta desde " + path);
		}
	}
	
	/**
	 * Devuelve los centros de salud separados de desde una distancia menor a radio.
	 * Esta metodo respeta la interfaz de cursores de las consultas sqlite.
	 * @param desde Centro de la circunferencia de centros de salud
	 * @param radio Radio de la circunferencia
	 * @return cursor con los centros de salud pedidos
	 */
	public ArrayList<CentroSalud> getCentrosSalud(final Location desde, double radio){
		if(bd != null){
			// Averiguamos los ids de los centros de salud que están cerca de desde
			String[] columnas = {"_id", "Latitud", "Longitud"};
			Cursor cursor = bd.query(true, TABLE, columnas, "", null, "", "", "", "", null);
			cursor.moveToFirst();
			List<String> ids = new ArrayList<String>();
			while(cursor.isAfterLast() == false){
				double lat = cursor.getDouble(1);
				double lon = cursor.getDouble(2);
				Location punto = new Location("");
				punto.setLatitude(lat);
				punto.setLongitude(lon);
				double distancia = punto.distanceTo(desde);
				if(distancia < radio){
					ids.add(Integer.toString(cursor.getInt(0)));
				}
				cursor.moveToNext();
			}

			// Devolvemos los resultados de la consulta
			String[] columnas_consulta = {"_id", "Nombre", "Direccion", "Ciudad", "Provincia", "Telefono", "Latitud", "Longitud"};
			ArrayList<CentroSalud> centrosSalud = new ArrayList<CentroSalud>(ids.size());
			Cursor cursor_cs = bd.query(true, TABLE, columnas_consulta, 
										"_id in (" + placeholders(ids.size()) + ")", ids.toArray(new String[ids.size()]), 
										"", "", "", "", null);
			cursor_cs.moveToFirst();
			while(cursor_cs.isAfterLast() == false){
				CentroSalud cs = new CentroSalud(cursor_cs.getString(1), cursor_cs.getString(2), cursor_cs.getString(3), 
											  	 cursor_cs.getString(4), cursor_cs.getString(5),
											  	 cursor_cs.getDouble(6), cursor_cs.getDouble(7));
				centrosSalud.add(cs);
				cursor_cs.moveToNext();
			}
			
			Collections.sort(centrosSalud, new Comparator<CentroSalud>(){
				@Override
				public int compare(CentroSalud cs1, CentroSalud cs2){
					double dis1 = desde.distanceTo(cs1.getLocalizacion());
					double dis2 = desde.distanceTo(cs2.getLocalizacion());
					return (int) Math.round(dis1 - dis2);
				}
			});
			
			return centrosSalud;
		}
		
		return null;
	}
	
	/**
	 * Usado en getCentrosSalud para construir un string con tantos placeholders como se indique
	 */
	private String placeholders(int n){
		String ret = "";
		for(int i = 0; i < n; i++){
			if(i == 0){
				ret += "?";
			}else{
				ret += ", ?";
			}
		}
		return ret;
	}
	
	/**
	 * Setter de la base de datos, provoca la copia de la base de datos si no existe.
	 * @param base_datos Nombre de la base de datos
	 */
	public void setBaseDatos(String base_datos){
		String path_bd = getPathBD(base_datos);
		nombre_bd = base_datos;
		if(!existeBaseDatos(path_bd)){
			Log.d(Utils.ASIMOV, "Creando base de datos");
			copiarBaseDatos();
		}else{
			Log.d(Utils.ASIMOV, "La base de datos ya existe");
		}
	}
	
	/**
	 * Devuelve la ruta en el sistema de ficheros de una base de datos pasada como argumento
	 * @param base_datos Nombre de la base de datos
	 * @return Path a la base de datos pasada como argumento
	 */
	public String getPathBD(String base_datos){
		File files_dir = mContext.getFilesDir();
		File data_path = files_dir.getParentFile();
		return data_path.getAbsolutePath() + "/databases/" + base_datos;
	}
	
	/**
	 * Metodo sobrecargado para ofrecer un comportamiento por defecto cuando no se pasa ningun
	 * argumento a getPathBD(String)
	 * @return Path a la base de datos interna a la clase
	 */
	public String getPathBD(){
		return getPathBD(nombre_bd);
	}

	/**
	 * Copia la base de datos del directorio assets al directorio databases de la aplicacion
	 */
	private void copiarBaseDatos(){
		// Abrimos input en el directorio assets
		InputStream input = null;
		try{
			input = mContext.getAssets().open(nombre_bd);
		} catch(IOException e){
			Log.e(Utils.ASIMOV, "No se ha encontrado a " + nombre_bd + " en el directorio assets.");
			nombre_bd = null;
			Toast toast = Toast.makeText(mContext, R.string.databaseError, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		// Creamos el directorio databases si este no existe en el directorio data
		String path_bd = getPathBD();
		File databases = new File(path_bd).getParentFile();
		if(!databases.exists()){
			Log.d(Utils.ASIMOV, "Creando directorio databases en " + databases.getAbsolutePath());
			databases.mkdir();
		}
		
		// Abrimos output en el directorio de destino
		OutputStream output;
		try {
			output = new FileOutputStream(path_bd);
		} catch (FileNotFoundException e) {
			Log.e(Utils.ASIMOV, "No se ha podido abrir la base de datos " + nombre_bd + " en " + path_bd);
			nombre_bd = null;
			Toast toast = Toast.makeText(mContext, R.string.databaseError, Toast.LENGTH_LONG);
			toast.show();
			try {
				input.close();
			} catch (IOException e1) {
			}
			return;
		}
		
		// Realizamos la copia
		byte[] buffer = new byte[TAM_BUFFER];
		int length;
		try{
			while((length = input.read(buffer)) > 0){
				output.write(buffer, 0, length);
			}
		} catch(IOException e){
		}
		Log.d(Utils.ASIMOV, "Base de datos copiada");
		
		// Cerramos output e input
		try {
			output.flush();
			output.close();
		} catch (IOException e) {
			Log.e(Utils.ASIMOV, "Error al cerrar output al copiar la base de datos.");
		}
		try {
			input.close();
		} catch (IOException e) {
			Log.e(Utils.ASIMOV, "Error al cerrar input al copiar la base de datos.");
		}
	}
	
	/**
	 * Comprueba si existe la base de datos pasada como argumento
	 */
	private boolean existeBaseDatos(String path_bd){
		SQLiteDatabase checkDB = null;
		try{
			checkDB = SQLiteDatabase.openDatabase(path_bd, null, SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e){
			return false;
		}
		
		return checkDB != null ? true : false;
	}
}
