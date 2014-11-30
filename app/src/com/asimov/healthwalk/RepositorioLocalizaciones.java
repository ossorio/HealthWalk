package com.asimov.healthwalk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class RepositorioLocalizaciones {
	private Context mContext;
	private final int TAM_BUFFER = 1024;
	private final String TABLE = "localizaciones";
	private final String TAG;
	private String nombre_bd = null;
	private SQLiteDatabase bd = null;

	RepositorioLocalizaciones(Context contexto, String base_datos, String log_tag){
		TAG = log_tag;
		mContext = contexto;
		setBaseDatos(base_datos);
		start();
	}

	/*
	 * Parar la base de datos, normalmente se hace en el metodo onPause de la 
	 * actividad que haya instaciado esta clase 
	 */
	public void stop(){
		if(bd != null){
			bd.close();
			bd = null;
			Log.d(TAG, "Base de datos cerrada");
		}
	}
	
	/*
	 * Arrancar la base de datos, normalmente se hace en el metodo onResume
	 * de la actividad que haya instanciado esta clase
	 */
	public void start(){
		if(nombre_bd != null && bd == null){
			String path = getPathBD();
			bd = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			Log.d(TAG, "Base de datos abierta desde " + path);
		}
	}
	
	public Cursor getCentrosSalud(Location desde, double radio){
		// TODO: Hay que testearlo con el mapa
		if(bd != null){
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
			String[] columnas_consulta = {"_id", "Nombre", "Telefono", "Latitud", "Longitud"};

			return bd.query(true, TABLE, columnas_consulta, 
							"_id in (" + placeholders(ids.size()) + ")", ids.toArray(new String[ids.size()]), 
							"", "", "", "", null);
		}
		
		return null;
	}
	
	private String placeholders(int numero){
		String ret = "";
		for(int i = 0; i < numero; i++){
			if(i == 0){
				ret += "?";
			}else{
				ret += ", ?";
			}
		}
		return ret;
	}
	
	public void setBaseDatos(String base_datos){
		String path_bd = getPathBD(base_datos);
		nombre_bd = base_datos;
		if(!existeBaseDatos(path_bd)){
			Log.d(TAG, "Creando base de datos");
			copiarBaseDatos();
		}else{
			Log.d(TAG, "La base de datos ya existe");
		}
	}
	
	public String getPathBD(String base_datos){
		File files_dir = mContext.getFilesDir();
		File data_path = files_dir.getParentFile();
		return data_path.getAbsolutePath() + "/databases/" + base_datos;
	}
	
	public String getPathBD(){
		return getPathBD(nombre_bd);
	}

	private void copiarBaseDatos(){
		InputStream input = null;
		try{
			input = mContext.getAssets().open(nombre_bd);
		} catch(IOException e){
			Log.e(TAG, "No se ha encontrado a " + nombre_bd + " en el directorio assets.");
			nombre_bd = null;
			Toast toast = Toast.makeText(mContext, R.string.databaseError, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		String path_bd = getPathBD();
		File databases = new File(path_bd).getParentFile();
		if(!databases.exists()){
			Log.d(TAG, "Creando directorio databases en " + databases.getAbsolutePath());
			databases.mkdir();
		}
		OutputStream output;
		try {
			output = new FileOutputStream(path_bd);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "No se ha podido abrir la base de datos " + nombre_bd + " en " + path_bd);
			nombre_bd = null;
			Toast toast = Toast.makeText(mContext, R.string.databaseError, Toast.LENGTH_LONG);
			toast.show();
			try {
				input.close();
			} catch (IOException e1) {
			}
			return;
		}
		
		byte[] buffer = new byte[TAM_BUFFER];
		int length;
		try{
			while((length = input.read(buffer)) > 0){
				output.write(buffer, 0, length);
			}
		} catch(IOException e){
		}
		Log.d(TAG, "Base de datos copiada");
		
		try {
			output.flush();
			output.close();
		} catch (IOException e) {
			Log.e(TAG, "Error al cerrar output al copiar la base de datos.");
		}

		try {
			input.close();
		} catch (IOException e) {
			Log.e(TAG, "Error al cerrar input al copiar la base de datos.");
		}
	}
	
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
